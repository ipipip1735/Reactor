package netty;

import io.netty.buffer.Unpooled;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by Administrator on 2019/10/24 8:14.
 */
public class TCPServerTrial {


    String ip = "192.168.0.127";
    int port = 5454;

    public static void main(String[] args) {
        TCPServerTrial serverTial = new TCPServerTrial();

        serverTial.server();
//        serverTial.request();
//        serverTial.response();

//        serverTial.hook();
    }

    private void hook() {

        DisposableServer server = TcpServer.create()
                .doOnConnection(conn -> {
                    System.out.println("~~conn~~");
                    conn.addHandler(new ReadTimeoutHandler(10, TimeUnit.SECONDS));
                }).bindNow();

        server.onDispose()
                .block();
    }

    private void response() {

        DisposableServer server = TcpServer.create()
                .host("localhost")
                .port(8080)
                .handle((inbound, outbound) -> {
                    System.out.println("ok");
                    return outbound.sendString(Mono.just("hello"));
                })
                .bindNow();

        server.onDispose()
                .block();

    }

    private void request() {

        DisposableServer server = TcpServer.create()
                .host("localhost")
                .port(8080)
                .handle((inbound, outbound) -> {
                    System.out.println("~~handle~~");
                    inbound.receive().count().subscribe(System.out::println);
                    return outbound.sendString(Mono.just("hello"));
                })
                .bindNow();

        server.onDispose()
                .block();

    }

    private void server() {

//        read();
//        write();
        rw();

    }

    private void write() {

        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        outbound.sendObject(Unpooled.wrappedBuffer(UTF_8.encode("ok")))
                .neverComplete())
                .wiretap(true)
                .bindNow();

        server.onDispose()
                .block();
    }

    private void read() {

        //方式一
        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) -> {
                    return inbound.receive().doOnNext(byteBuf -> {
                        System.out.println(byteBuf);
                        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());
                        System.out.println(UTF_8.decode(byteBuf.readerIndex(0).nioBuffer()));
                    })
                            .then();

                })
                .bindNow();
        server.onDispose()
                .block();


        //方式二
//        DisposableServer server = TcpServer.create()
//                .host(ip)
//                .port(port)
//                .handle((inbound, outbound) -> {
//                    ByteBufFlux byteBufFlux = inbound.receive();
//
//                    byteBufFlux.doOnNext(byteBuf -> {
//                        System.out.println(byteBuf);
//                        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());
//                        System.out.println(UTF_8.decode(byteBuf.readerIndex(0).nioBuffer()));
//                    });
//
//                    return byteBufFlux.then();
////                    return Mono.<Void>never();
//                })
//                .bindNow();
//
//        server.onDispose()
//                .block();
    }

    private void rw() {

        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) -> {
                    System.out.println("~~handle~~");
                    return inbound.receive()
                            .doOnNext(byteBuf -> {
                                System.out.println(byteBuf);
                                outbound.sendObject(Unpooled.wrappedBuffer("ok".getBytes()))
                                        .then();

                            })
                            .then();

                })
//                .handle((inbound, outbound) -> {
//                            System.out.println("~~handleW~~");
//                            return outbound.sendString(Mono.just("ok"))
//                                    .neverComplete().then();
//                        }
//                )
                .wiretap(true)
                .bindNow();

        server.onDispose()
                .block();
    }
}
