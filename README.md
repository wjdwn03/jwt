### JWT 발급, 재발급 코드 정리 중입니다.

## 📢 프로젝트 실행 전 준비 사항
- `mysql-version > src > main > resources > data.sql` 파일을 MySQL Workbench에서 실행.

## ✔️ 실행 방법

- **JWT 발급 api 테스트(로그인 api)** : 로컬에서 프로젝트 실행 후 postman 같은 플랫폼에서 다음 내용을 바디에 넣어 로그인 api를 호출하면 JWT가 발급된다.
  ```
  email : qwer@qwer.com,
  password : qwer
  ```
- **JWT 재발급 api 테스트** : 발급된 access token을 헤더에 담아서 테스트해 볼 수 있다.
    - 쿠키에 refresh token이 담겨 있는지 확인해야 한다.
    - 쿠키에 refresh token이 담겨 있지 않으면 <U>"DB에 저장된 refresh token과 다르다"</U>라는 에러 문구가 return 될 것이다.

## 📔프로젝트 구조
##### ※ 발급, 재발급에 반드시 필요한 파일만 적었습니다. 
##### ※ exception, request, response와 관련된 것들은 구조만 적었습니다.    

        
### mysql-version

```
jwt
│  build.gradle
└─src
    ├─main
      ├─java
      │  └─com
      │      └─mysql
      │          └─jwt
      │              │  JwtApplication.java
      │              │
      │              ├─api
      │              │  ├─controller
      │              │  │      UserController.java
      │              │  │
      │              │  ├─data
      │              │  ├─request
      │              │  ├─response
      │              │  └─service
      │              │          UserService.java
      │              │
      │              ├─config
      │              │      CorsConfig.java
      │              │      JwtConfig.java
      │              │      SecurityConfig.java
      │              │
      │              ├─db
      │              │  ├─domain
      │              │  │      User.java
      │              │  │
      │              │  └─repository
      │              │          UserRepository.java
      │              │
      │              ├─exception
      │              ├─oauth
      │              │      AppProperties.java
      │              │      PrincipalDetails.java
      │              │      RoleType.java
      │              │
      │              ├─token
      │              │      AuthToken.java
      │              │      AuthTokenProvider.java
      │              │      TokenAccessDeniedHandler.java
      │              │      TokenAuthenticationFilter.java
      │              │
      │              └─util
      │                      CookieUtil.java
      │                      HeaderUtil.java
      │
      └─resources
              application-secret.yml
              application.yml
              data.sql

```
