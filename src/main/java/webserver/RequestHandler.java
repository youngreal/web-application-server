package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import static util.IndexToken.requestSplit;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private static User user;


    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        /**
         * InputStream : 1byte씩 읽히기때문에, 숫자를입력해도 아스키코드값으로 출력됨
         * InputStreamReader : 문자로읽을수있음, InputStream객체를 입력으로 가지고있어야함
         * BufferedReader : 통째로읽을수있음, InputStream을사용할때 배열의 크기를 일일히 정해줘야한다는 장점을 상쇄시킬수있음
         */

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = br.readLine();
            String url = requestSplit(line);

            byte[] body = "Hello World".getBytes();

            if (url.equals("/")) {
                if (printAllLine(line, br)) return;
                ResponseBodyMethod(out, body);
            }

         if(url.equals("/index.html")) {
             body = Files.readAllBytes(new File("./webapp" + url).toPath());
             if (printAllLine(line, br)) return;
             ResponseBodyMethod(out,body);
         }

         if(url.equals("/user/form.html")){
             body = Files.readAllBytes(new File("./webapp" + url).toPath());
             if (printAllLine(line, br)) return;
             ResponseBodyMethod(out,body);
         }

         if(url.equals("/user/login_failed.html")){
             body = Files.readAllBytes(new File("./webapp" + url).toPath());
             if (printAllLine(line, br)) return;
             ResponseBodyMethod(out, body);
         }

            /**
             * post 방식 회원가입할때
             */
            if(url.equals("/user/create")){
                printAllLine(line, br);
                String brBody = IOUtils.readData(br, 89); // 요청메시지의 body를 읽는다.
                System.out.println("brBody= " + brBody);
                // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
                Map<String, String> parsedUrl = HttpRequestUtils.parseQueryString(brBody); //요청메시지의 body를 &를 기준으로 파싱한다
                user = new User(parsedUrl.get("userId"),parsedUrl.get("password"),parsedUrl.get("name"),parsedUrl.get("email")); //user 생성자 생성
                DataBase.addUser(user); // DB에 저장?

                DataOutputStream dos = new DataOutputStream(out); // 서버-> 클라이언트
                response302HeaderBody(dos, body.length, brBody); // 302 redirect + index로 돌림 + 유저정보 body에 실음
            }
            /**
             * 로그인 페이지 들어갔을때
             */
            if (url.equals("/user/login.html")) {
                body = Files.readAllBytes(new File("./webapp" + url).toPath());
                printAllLine(line, br);
                ResponseBodyMethod(out, body);
            }

            /**
             * 로그인 페이지에서 post방식으로 로그인시도
             */
            if (url.equals("/user/login")) {
                /**
                 * http 바디 읽기
                 */
                printAllLine(line, br);
                String brBody = IOUtils.readData(br, 27);
                System.out.println("brBody= " + brBody);
                Map<String, String> parsedUrl = HttpRequestUtils.parseQueryString(brBody);

                /**
                 * 로그인 성공시
                 */
                DataOutputStream dos = new DataOutputStream(out);
                if(DataBase.findUserById(user.getUserId()).getUserId().equals(parsedUrl.get("userId"))&&
                        DataBase.findUserById(user.getUserId()).getPassword().equals(parsedUrl.get("password"))){
                    System.out.println("성공시 url= " + url);
                    System.out.println("로그인성공");
                    responseSetCookie(dos,body.length, brBody);
                }
                /**
                 * 로그인 실패시
                 */
                else {
                    System.out.println("로그인 실패");
                    responseSetCookieFalse(dos, body.length, brBody);
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseSetCookieFalse(DataOutputStream dos, int length, String body) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: http://localhost:8080/user/login_failed.html\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=false\r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    private void responseSetCookie(DataOutputStream dos, int lengthOfBodyContent, String body) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: http://localhost:8080/index.html\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(body);

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private boolean printAllLine(String line, BufferedReader br) throws IOException {
        while (!"".equals(line)) {
            if (line == null) {
                return true;
            }
            System.out.println("line=" + line);
            line = br.readLine();
        }
        return false;
    }

    private void ResponseBodyMethod(OutputStream out, byte[] body) throws IOException {
        // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        DataOutputStream dos = new DataOutputStream(out);
        response200Header(dos, body.length);
        responseBody(dos, body);
    }



    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderBody(DataOutputStream dos, int length, String body) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: http://localhost:8080/index.html\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
