package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.sctp.SctpOutboundByteStreamHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.w3c.dom.ls.LSOutput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.channel.BootstrapHandlers;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * Created by Administrator on 2019/10/24 8:14.
 */
public class TCPServerTrial {


    String ip = "192.168.0.126";
    int port = 5454;

    public static void main(String[] args) {
        TCPServerTrial serverTial = new TCPServerTrial();

        serverTial.server();//读/写数据
//        serverTial.hook();//周期函数
//        serverTial.config();//配置
    }

    private void config() {

        TcpServer.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)//配置客户端通道选项
                .runOn(LoopResources.create("event-loop", 1, 4, true)) //配置线程
                .bootstrap(serverBootstrap -> { //配置serverBootstrap
                    System.out.println("~~bootstrap~~");
                    System.out.println(serverBootstrap);

                    serverBootstrap.option(ChannelOption.AUTO_READ, false);
                    BootstrapHandlers.updateConfiguration(serverBootstrap, "oo",
                            (connectionObserver, channel) -> {
                                System.out.println("~~update~~");
                                System.out.println(connectionObserver);

                                ByteBuf delimiter = Unpooled.wrappedBuffer("o".getBytes());
                                channel.pipeline()
                                        .addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            });

                    return serverBootstrap;

                })
                .doOnBind(serverBootstrap -> {
                    System.out.println("~~doOnBind~~");
                    System.out.println(serverBootstrap);

                    //这里的配置和上面bootstrap()方法中的都是等价的
//                    serverBootstrap.option(ChannelOption.AUTO_READ, false);
//                    BootstrapHandlers.updateConfiguration(serverBootstrap, "oo",
//                            (connectionObserver, channel) -> {
//                                System.out.println("~~update~~");
//                                System.out.println(connectionObserver);
//                                ByteBuf delimiter = Unpooled.wrappedBuffer("o".getBytes());
//                                channel.pipeline()
//                                        .addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
//                            });


                })
                .doOnBound(disposableServer -> {//一般UDP协议才会用到这个周期函数
                    System.out.println("~~doOnBound~~");

                    System.out.println(disposableServer);
                    System.out.println(disposableServer.channel().pipeline());//获取管线

                })
                .doOnConnection(connection -> {
                    System.out.println("~~doOnConnection~~");
                    System.out.println(connection);
                    //增加处理器到管线
                    ByteBuf delimiter = Unpooled.wrappedBuffer("o".getBytes());
                    connection.addHandlerLast(new DelimiterBasedFrameDecoder(1024, delimiter));

                })
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .doOnNext(byteBuf -> {
                                    System.out.println("~~doOnNext~~");
                                    System.out.println(byteBuf);
                                })
                                .then())
                .bindNow()
                .onDispose()
                .block();

    }

    private void hook() {

        DisposableServer server = TcpServer.create()
                .doOnBind(derverBootstrap -> { //绑定Socket前触发
                    System.out.println("~~doOnBind~~");
                    System.out.println(derverBootstrap);
                })
                .doOnBound(disposableServer -> { //绑定Socket后触发
                    System.out.println("~~doOnBound~~");
                    System.out.println(disposableServer);
                })
                .doOnConnection(connection -> { //客户端通道激活时触发
                    System.out.println("~~doOnConnection~~");
                    System.out.println(connection);
                }).bindNow();

        server.onDispose()
                .block();
    }

    private void server() {

//        read();
        write();

    }

    private void write() {

        //方式一
//        DisposableServer server = TcpServer.create()
//                .host(ip)
//                .port(port)
//                .handle((inbound, outbound) ->
//                        inbound.receive()
//                                .doOnNext(byteBuf -> {
//                                    System.out.println(byteBuf);
//                                    System.out.println(UTF_8.decode(byteBuf.nioBuffer()));
//                                    outbound.send(Mono.just(Unpooled.wrappedBuffer("OK".getBytes())))
//                                            .then()
//                                            .subscribe();
//                                })
//                                .then())
//
//                .bindNow();
//
//        server.onDispose()
//                .block();

        //方式二
        TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .asString()
                                .flatMap(s -> {
                                    System.out.println(s);
                                    return outbound.sendString(Flux.just("11", "22")
                                            .delayElements(Duration.ofMillis(500)));
                                }).then())
                .bindNow()
                .onDispose()
                .block();
    }

    private void read() {

        //方式一：读取数据
//        DisposableServer server = TcpServer.create()
//                .host(ip)
//                .port(port)
//                .handle((inbound, outbound) -> {
//                    return inbound.receive().doOnNext(byteBuf -> {
//                        System.out.println(byteBuf);
//                        while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());
//                        System.out.println(UTF_8.decode(byteBuf.readerIndex(0).nioBuffer()));
//                    })
//                            .then();
//                })
//                .bindNow();
//        server.onDispose()
//                .block();

        //方法二：直接读取字符串
        DisposableServer server = TcpServer.create()
                .host(ip)
                .port(port)
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .asString()//转换为字符串
                                .doOnNext(System.out::println)
                                .then())
                .bindNow();
        server.onDispose()
                .block();


        //方式三：设置引用
//        DisposableServer server = TcpServer.create()
//                .host(ip)
//                .port(port)
//                .handle((inbound, outbound) ->
//                        inbound.receive()
//                                .retain()//让每个Bytebuf引用+1
//                                .doOnNext(byteBuf -> {
//                                    System.out.println(byteBuf.refCnt());
//                                })
//                                .then())
//                .bindNow();
//
//        server.onDispose()
//                .block();

    }
}
