package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import com.google.common.base.Utf8;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import static util.IndexToken.requestSplit;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

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
            System.out.println("line="+ line);
            String url = requestSplit(line);
            System.out.println("url =" + url);


                byte[] body = "Hello World".getBytes();
         if(url.equals("/index.html")) {
             body = Files.readAllBytes(new File("./webapp" + url).toPath());
         }
         if(url.equals("/user/form.html")){
             body = Files.readAllBytes(new File("./webapp" + url).toPath());
         }

            if (url.contains("?")) {
                String subUrl = url.substring(13);
                System.out.println("subUrl =" + subUrl);
                Map<String, String> parsedUrl = HttpRequestUtils.parseQueryString(subUrl);
                User user = new User(parsedUrl.get("userId"),parsedUrl.get("password"),parsedUrl.get("name")
                        ,parsedUrl.get("email"));

                System.out.println("userId=" +user.getUserId());
                System.out.println("userName= " +user.getName());
                System.out.println("userEmail=" + user.getEmail());
                System.out.println("userPassword=" + user.getPassword());
            }


            while (!"".equals(line)) {
                if (line == null) {
                    return;
                }
                System.out.println("line=" + line);
                line = br.readLine();
            }

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
