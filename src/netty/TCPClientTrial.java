package netty;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by Administrator on 2019/10/24 10:47.
 */
public class TCPClientTrial {

    String ip = "192.168.0.126";
    int port = 5454;
    Connection connection = null;

    public static void main(String[] args) {

        TCPClientTrial tcpClient = new TCPClientTrial();

        tcpClient.conn();

    }

    private void conn() {

        //方式一
        connection = TcpClient.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) -> {

                    outbound.sendString( //写操作
                            Mono.just("ttt")
                                    .doOnNext(byteBuf -> {
                                        System.out.println("~~doOnNext~~W");
                                        System.out.println(byteBuf);
                                        try {
                                            Thread.sleep(1000L);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }))
                            .then()
                            .subscribe();


                    return inbound.receive()////读操作
                            .doOnNext(byteBuf -> {
                                System.out.println("~~doOnNext~~R");
                                String r = new String(UTF_8.decode(byteBuf.nioBuffer()).array());
                                System.out.println(r);

                                if(r.equals("22")) connection.dispose();//关闭连接

                            }).then();
                })
                .connectNow();
        connection.onDispose()
                .block();


        //方式二：链式调用
//        TcpClient.create()
//                .host(ip)
//                .port(port)
//                .handle((inbound, outbound) ->
//                        outbound.sendString(Mono.just("vvv"))
//                                .then(inbound.receive()
//                                        .asString()
//                                        .doOnNext(System.out::println)
//                                        .then())
//                )
//                .connectNow()
//                .onDispose()
//                .block();


        //方式三：关闭连接
//        connection = TcpClient.create()
//                .host(ip)
//                .port(port)
//                .handle((inbound, outbound) -> {
//                            outbound.sendString(Mono.just("vvv"))
//                                    .then().subscribe();
//
//                            inbound.receive()
//                                    .asString()
//                                    .doOnNext(s -> {
//                                        System.out.println(s);
//                                        if (s.equals("22")) connection.dispose();
//                                    })
//                                    .subscribe();
//
//                            return Mono.never();
//                        }
//                )
//                .connectNow();
//        connection.onDispose().block();
    }


}

