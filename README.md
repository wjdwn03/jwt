### JWT 발급, 재발급 코드 정리 중입니다.

## 📔프로젝트 구조
##### ※ 발급, 재발급에 반드시 필요한 파일만 적었습니다. 
##### ※ exception, request, response와 관련된 것들은 구조만 적었습니다.    

        
### mysql-version

```
jwt
│  JwtApplication.java
│
├─api
│  ├─controller
│  │      UserController.java
│  ├─data
│  ├─request
│  ├─response
│  └─service
│          UserService.java
│
├─config
│      CorsConfig.java
│      JwtConfig.java
│      SecurityConfig.java
│
├─db
│  ├─domain
│  │      User.java
│  │
│  └─repository
│          UserRepository.java
│
├─exception
├─oauth
│      AppProperties.java
│      PrincipalDetails.java
│      RoleType.java
│
├─token
│      AuthToken.java
│      AuthTokenProvider.java
│      TokenAccessDeniedHandler.java
│      TokenAuthenticationFilter.java
│
└─util
        CookieUtil.java
        HeaderUtil.java
```
