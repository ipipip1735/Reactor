package netty;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/11/13 23:00.
 */
public class HttpClientTrial {

    String host = "localhost";
    String ip = "192.168.0.126";
    int port = 8080;
    String uri = "http://" + ip + "/" + port;

    public static void main(String[] args) {

        HttpClientTrial httpClientTrial = new HttpClientTrial();

        httpClientTrial.conn();


    }

    private void conn() {

        HttpClientResponse response =
                HttpClient.create()
                        .get()
                        .uri(uri)
                        .response()
                        .block();





    }
}
