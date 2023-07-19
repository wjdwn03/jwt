package com.mysql.jwt.api.service;

import com.mysql.jwt.api.data.LoginUserData;
import com.mysql.jwt.api.data.ReissuedAccessTokenData;
import com.mysql.jwt.api.request.LoginUserReq;
import com.mysql.jwt.db.domain.User;
import com.mysql.jwt.db.repository.UserRepository;
import com.mysql.jwt.exception.NotFoundException;
import com.mysql.jwt.exception.UnAuthorizedException;
import com.mysql.jwt.oauth.AppProperties;
import com.mysql.jwt.oauth.PrincipalDetails;
import com.mysql.jwt.token.AuthToken;
import com.mysql.jwt.token.AuthTokenProvider;
import com.mysql.jwt.util.CookieUtil;
import com.mysql.jwt.util.HeaderUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ROLE = "ROLE_USER";


    // private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화용
    private final UserRepository userRepository;
    private final AppProperties appProperties;
    private final AuthTokenProvider authTokenProvider;


    /**
     * 일반 회원이 입력한 이메일과 패스워드를 확인하고 로그인 처리해줍니다.
     *
     * @param loginUserReq        사용자가 입력한 email과 password를 담은 객체
     * @param httpServletRequest
     * @param httpServletResponse
     * @return 성공 시 발급된 access token을  담은 LoginUserData 타입의 객체를 반환합니다
     */
    @Transactional
    public LoginUserData loginUser(LoginUserReq loginUserReq, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        // 사용자가 입력한 이메일과 비밀번호가 맞는지 확인한다.
        User user = userRepository.findByEmailAndPassword(loginUserReq.getEmail(), loginUserReq.getPassword()).orElseThrow(() -> new NotFoundException("이메일과 비밀번호를 다시 확인해주세요."));

        // access token 발급
        AuthToken accessToken = makeAccessToken(user.getId());

        // refresh token 발급
        AuthToken refreshToken = makeRefreshToken();

        // 발급한 refresh token을 DB에 저장
        saveRefreshToken(user, refreshToken.getToken());

        int cookieMaxAge = (int) appProperties.getAuth().getRefreshTokenExpiry() / 60;

        // refresh toekn 쿠키에 담기
        CookieUtil.deleteCookie(httpServletRequest, httpServletResponse, REFRESH_TOKEN);
        CookieUtil.addCookie(httpServletResponse, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return LoginUserData.builder().accessToken(accessToken.getToken()).build();


        // 암호화해서 비밀번호를 저장하는게 좋지만 지금 중요한 부분이 아니므로 주석 처리 했다.
//         if(bCryptPasswordEncoder.matches(loginUserReq.getPassword(), user.getPassword())){
//         String accessToken = issueJwt(user.getId());
//         return LoginUserData.builder().accessToken(accessToken).build();
//         } else {
//         throw new NotFoundException("비밀번호가 일치하지 않습니다.");
//         }

    }

    /**
     * access token을 발급합니다.
     *
     * @param userId access token을 생성할 user id
     * @return 성공 시 발급된 access token을 AuthToken 타입의 객체로 반환합니다
     */
    public AuthToken makeAccessToken(Long userId) {

        Date now = new Date();

        return authTokenProvider.createAuthToken(
                userId,
                ROLE,
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );
    }

    /**
     * refresh token을 발급합니다.
     *
     * @return 성공 시 생성한 refresh token을 AuthToken 타입의 객체로 반환합니다
     */
    public AuthToken makeRefreshToken() {

        Date now = new Date();

        return authTokenProvider.createAuthToken(new Date(now.getTime() + appProperties.getAuth().getRefreshTokenExpiry()));
    }

    /**
     * 발급된 refresh token을 DB에 저장합니다
     *
     * @param user         refresh token을 저장할 사용자
     * @param refreshToken DB에 저장할 refresh token
     */
    @Transactional
    public void saveRefreshToken(User user, String refreshToken) {
        user.saveRefreshToken(refreshToken);
    }


    /**
     * access token을 재발급합니다.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return 성공 시 재발급한 access token을 담은 ReissuedAccessTokenData 타입의 객체를 반환합니다
     */
    @Transactional
    public ReissuedAccessTokenData reissueAccessToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        // 헤더에 담긴 access token
        String headerAccessToken = HeaderUtil.getAccessToken(httpServletRequest);


        AuthToken authHeaderAccessToken = authTokenProvider.convertAuthToken(headerAccessToken);
        Authentication authentication = authTokenProvider.getExpiredUser(authHeaderAccessToken);

        // 재발급은 Security 필터에서 User 정보를 못 담아오기 때문에 access token에서 뽑아낸다.
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        // 쿠키에 담긴 refresh token
        String cookieRefreshToken = CookieUtil.getCookie(httpServletRequest, REFRESH_TOKEN).map(Cookie::getValue)
                .orElse(null);

        log.debug("cookieRefreshToken : {}", cookieRefreshToken);


        try {
            // 쿠키에 담긴 refresh 토큰이 null 이거나 DB에 저장된 refresh 토큰과 다른 경우 다시 로그인 해야 함.
            if (cookieRefreshToken == null || !cookieRefreshToken.equals(user.getRefreshToken())) {
                deleteCookieRefreshToken(httpServletRequest, httpServletResponse);
                throw new UnAuthorizedException("DB에 저장되어 있는 refreshToken과 다릅니다. 다시 로그인 해주세요.");
            }


            AuthToken authCookieRefreshToken = authTokenProvider.convertAuthToken(cookieRefreshToken);
            Claims refreshClaims = authCookieRefreshToken.getExpiredTokenClaims();

            // 쿠키에 담긴 refresh token 유효기간 확인
            long expirationTime = refreshClaims.get("exp", Long.class); //"exp" 필드 값을 가져옵니다.
            long currentTime = System.currentTimeMillis() / 1000; // 현재 시간을 초 단위로 가져옵니다.

            // 유효기간이 이미 지났거나 DB에 refresh token이 저장되어 있지 않으면 유효하지 않은 refresh token으로 판단
            if (expirationTime < currentTime || user.getRefreshToken() == null) {

                log.error("유효하지 않은 refresh token 입니다.");
                deleteCookieRefreshToken(httpServletRequest, httpServletResponse);

                throw new UnAuthorizedException("유효하지 않은 refresh token 입니다.");
            }

        } catch (NullPointerException e) {
            deleteCookieRefreshToken(httpServletRequest, httpServletResponse);
            throw new UnAuthorizedException("refresh token이 만료되었습니다. 다시 로그인 해주세요.");
        }

        log.debug("쿠키에 담긴 refreshToken : {}", cookieRefreshToken);

        // access token 재발급
        AuthToken accessToken = makeAccessToken(user.getId());

        log.info("정상적으로 액세스토큰 재발급!!!");

        return ReissuedAccessTokenData.builder().accessToken(accessToken.getToken()).build();
    }

    /**
     * 쿠키에 담긴 refresh token을 삭제합니다.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     */
    public void deleteCookieRefreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        CookieUtil.deleteCookie(httpServletRequest, httpServletResponse, REFRESH_TOKEN);
    }
}
