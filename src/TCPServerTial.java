import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/10/24 8:14.
 */
public class TCPServerTial {

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

        DisposableServer server = TcpServer.create()
                .host("localhost")
                .port(8080)
                .handle((inbound, outbound) -> outbound.sendString(Mono.just("ok")))
                .bindNow();

        server.onDispose()
                .block();


    }
}
