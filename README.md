# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
-InputStream : 1byte씩 읽히기때문에, 숫자를입력해도 아스키코드값으로 출력됨
-InputStreamReader : 문자로읽을수있음, InputStream객체를 입력으로 가지고있어야함
-BufferedReader : 통째로읽을수있음, InputStream을사용할때 배열의 크기를 일일히 정해줘야한다는 장점을 상쇄시킬수있음

### 요구사항 2 - get 방식으로 회원가입
* HTTP요청 메시지를 일일히 파싱해서 값을 전달하는 과정이 생각보다 너무 까다로웠다
* 부끄럽지만 HttpRequestUtils.parseQueryString()메서드를 처음알았다.
 ![image](https://user-images.githubusercontent.com/59333182/166653234-9f512a75-3ee4-48a8-8517-479bda8efba9.png)
 
안을 들어가보면 구분자 &를 기준으로 요청메시지를 파싱해주기때문에, get방식으로 쿼리스트링이 달려있는 요청메시지를 이름=값으로 구분할수있었다.

### 요구사항 3 - post 방식으로 회원가입
* ```java
String brBody = IOUtils.readData(br, 87);
``` 
bufferedReader에서 본문데이터를 읽는 메소드를 알게되었다.
*  서블릿이 빨리 나타났으면..

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
