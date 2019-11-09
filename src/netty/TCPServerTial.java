package netty;

import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.TimeUnit;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by Administrator on 2019/10/24 8:14.
 */
public class TCPServerTial {


    String ip = "192.168.0.127";
    int port = 5454;

    public static void main(String[] args) {
        TCPServerTial serverTial = new TCPServerTial();

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

        read();
//        write();


    }

    private void write() {

        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .map(byteBuf -> {
                                    System.out.println(byteBuf);
                                    return outbound.sendString(Mono.just("ok"));
                                })
                                .then()
                )
//                .handle((inbound, outbound) ->
//                        outbound.sendObject(Unpooled.wrappedBuffer(UTF_8.encode("ok"))))
                .bindNow();

        server.onDispose()
                .block();
    }

    private void read() {

        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .doOnNext(byteBuf -> {
                                    System.out.println(UTF_8.decode(byteBuf.nioBuffer()));
                                })
                        .then())
                .bindNow();

        server.onDispose()
                .block();
    }

    private void rw() {

        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .map(byteBuf -> {
                                    System.out.println(byteBuf);
                                    return outbound.sendString(Mono.just("ok"));
                                })
                                .then()
                )
                .bindNow();

        server.onDispose()
                .block();
    }
}
