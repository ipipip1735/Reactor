import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2019/10/24 13:42.
 */
public class NettyTrial {
    public static void main(String[] args) {

        NettyTrial nettyTrial = new NettyTrial();

        nettyTrial.tcp();
    }

    private void tcp() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress("localhost", 8080));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);


                    ChannelHandler handler = new ChannelHandler() {
                        @Override
                        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("~~" + getClass().getSimpleName() + ".handlerAdded~~");
                            System.out.println("ctx is " + ctx);

                        }

                        @Override
                        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("~~" + getClass().getSimpleName() + ".handlerRemoved~~");
                            System.out.println("ctx is " + ctx);

                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            System.out.println("~~" + getClass().getSimpleName() + ".exceptionCaught~~");
                            System.out.println("ctx is " + ctx);
                            System.out.println("cause is " + cause);

                        }
                    };


                    ch.pipeline().addLast(handler);

                }
            };

            serverBootstrap.childHandler(init);
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
