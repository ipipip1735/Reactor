package netty;

import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by Administrator on 2019/10/24 10:47.
 */
public class TCPClientTrial {

    String ip = "192.168.0.126";
    int port = 5454;

    public static void main(String[] args) {

        TCPClientTrial tcpClient = new TCPClientTrial();

//        tcpClient.urlConn();
        tcpClient.conn();
//        tcpClient.receive();
    }

    private void receive() {

        Connection connection =
                TcpClient.create()
                        .host("example.com")
                        .port(80)
                        .doOnConnect(conn -> {
                            System.out.println("~~conn~~");
//                            conn.
                        })
                        .handle((inbound, outbound) -> inbound.receive().then())
                        .connectNow();

        connection.onDispose()
                .block();
    }

    private void conn() {

        Connection connection = TcpClient.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) -> {
                    outbound.sendString(Mono.just("ttt"))
                            .then()
                            .doOnNext(byteBuf -> {
                                System.out.println("~~doOnNext~~W");
                                System.out.println(byteBuf);
                            })
                            .subscribe();
                    return inbound.receive().doOnNext(byteBuf -> {
                        System.out.println("~~doOnNext~~R");
                        System.out.println(UTF_8.decode(byteBuf.nioBuffer()));
                    }).then();
                })
                .connectNow();
        connection.onDispose()
                .block();


    }

    private void urlConn() {
        try {
            URLConnection urlConnection = new URL("http://localhost:8080").openConnection();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            System.out.println(inputStream.available());

//            byte[] bytes = new byte[inputStream.available()];
//            while (inputStream.read() != -1) {
//                System.out.println(new String(bytes));
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

