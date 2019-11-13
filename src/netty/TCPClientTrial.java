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

        tcpClient.conn();


    }

    private void conn() {

        //方式一
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




        //方式二
//        Connection connection =
//                TcpClient.create()
//                        .host(ip)
//                        .port(port)
//                        .handle((inbound, outbound) ->
//                                outbound.sendString(Mono.just("OK"))
//                                        .then(inbound.receive()
//                                                .asString()
//                                                .doOnNext(System.out::println)
//                                                .then())
//                        )
//                        .connectNow();
//        connection.onDispose()
//                .block();

    }
}

