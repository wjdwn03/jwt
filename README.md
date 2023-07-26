### JWT 발급, 재발급 코드 정리 중입니다.

### MySQL, Redis 두 가지 버전으로 코드를 정리할 예정입니다.

## 업로드 예상 일정

&nbsp;✅ mysql-version 코드 업로드 (~2023.07.12)

&nbsp;⬜ mysql-version 코드 설명 README, [JWT 정리한 기술 블로그](https://jjhwang.tistory.com/category/%EB%B3%B4%EC%95%88%2C%20%EB%A1%9C%EA%B7%B8%EC%9D%B8/JWT)에 업로드 예정 (~2023.07.18 - 개인적인 사정으로 일정 변경하겠습니다.) 

&nbsp;⬜ redis-version 코드 업로드 예정 (~2023.07.19)

&nbsp;⬜ redis-version 코드 설명 README, [JWT 정리한 기술 블로그](https://jjhwang.tistory.com/category/%EB%B3%B4%EC%95%88%2C%20%EB%A1%9C%EA%B7%B8%EC%9D%B8/JWT)에 업로드 예정 (~2023.07.20)

## 📢 프로젝트 실행 전 준비 사항

- `mysql-version > src > main > resources > data.sql` 파일을 MySQL Workbench에서 실행.

## ✔️ 실행 방법

**1. JWT 발급 api 테스트(로그인 api)**

- 로컬에서 프로젝트 실행 후 postman 같은 플랫폼에서 다음 내용을 바디에 넣어 로그인 api를 호출하면 JWT가 발급된다.
  ```
  email : qwer@qwer.com,
  password : qwer
  ```

**2. JWT 재발급 api 테스트**

- 발급된 access token을 헤더에 담아서 테스트해 볼 수 있다.
  - 쿠키에 refresh token이 담겨 있는지 확인해야 한다.
  - 쿠키에 refresh token이 담겨 있지 않으면 <U>"DB에 저장된 refresh token과 다르다"</U>라는 에러 문구가 return 될 것이다.

## 📔프로젝트 구조

##### ※ 발급, 재발급에 반드시 필요한 파일만 적었습니다.

##### ※ exception, request, response와 관련된 것들은 구조만 적었습니다.

### mysql-version
<img width="550" alt="image" src="https://github.com/wjdwn03/jwt/assets/109848753/c7aa1118-44c7-48c3-92cd-b963604fae45">

## 🔍 코드 설명
#### | config 패키지
- [CorsConfig 설명](https://jjhwang.tistory.com/35)