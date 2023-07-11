package com.mysql.jwt.api.service;

import com.mysql.jwt.api.data.LoginUserData;
import com.mysql.jwt.api.request.LoginUserReq;
import com.mysql.jwt.db.domain.User;
import com.mysql.jwt.db.repository.UserRepository;
import com.mysql.jwt.exception.NotFoundException;
import com.mysql.jwt.oauth.AppProperties;
import com.mysql.jwt.token.AuthToken;
import com.mysql.jwt.token.AuthTokenProvider;
import com.mysql.jwt.util.CookieUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final UserRepository userRepository;
    // private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화용
    private final AppProperties appProperties;
    private final AuthTokenProvider authTokenProvider;
    @Value("${app.auth.refresh-token-expiry}")
    private long refreshTokenExpiry;

    @Transactional
    public LoginUserData loginUser(LoginUserReq loginUserReq, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        // 사용자가 입력한 이메일과 비밀번호가 맞는지 확인한다.
        User user = userRepository.findByEmailAndPassword(loginUserReq.getEmail(), loginUserReq.getPassword()).orElseThrow(() -> new NotFoundException("이메일과 비밀번호를 다시 확인해주세요."));

        // access token 발급
        AuthToken accessToken = makeAccessToken(user.getId());

        // refresh token 발급
        AuthToken refreshToken = makeRefreshToken();

        // 발급한 refresh token을 DB에 저장
        saveRefreshToken(user, refreshToken.getToken());

        int cookieMaxAge = (int) refreshTokenExpiry / 60;

        // refresh toekn 쿠키에 담기
        CookieUtil.deleteCookie(httpServletRequest, httpServletResponse, REFRESH_TOKEN);
        CookieUtil.addCookie(httpServletResponse, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return LoginUserData.builder().accessToken(accessToken.getToken()).build();

        /**
        // 암호화해서 비밀번호를 저장하는게 좋지만 지금 중요한 부분이 아니므로 주석 처리 했다.
        if(bCryptPasswordEncoder.matches(loginUserReq.getPassword(), user.getPassword())){
            String accessToken = issueJwt(user.getId());
            return LoginUserData.builder().accessToken(accessToken).build();
        } else {
            throw new NotFoundException("비밀번호가 일치하지 않습니다.");
        }
         **/
    }

    /**
     * access token을 발급합니다.
     *
     * @param userId access token을 생성할 user id
     * @return 성공 시 발급된 access token을 AuthToken 타입의 객체로 반환합니다.
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
     * @return 성공 시 생성한 refresh token을 AuthToken 타입의 객체로 반환합니다.
     */
    public AuthToken makeRefreshToken() {

        Date now = new Date();

        return authTokenProvider.createAuthToken(new Date(now.getTime() + refreshTokenExpiry));
    }

    /**
     * 발급된 refresh token을 DB에 저장합니다
     * @param user refresh token을 저장할 사용자
     * @param refreshToken DB에 저장할 refresh token
     */
    @Transactional
    public void saveRefreshToken(User user, String refreshToken) {
        user.saveRefreshToken(refreshToken);
    }


}
