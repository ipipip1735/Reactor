import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
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

        nettyTrial.server();
    }

    private void server() {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress("192.168.0.126", 5454));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);


                    ChannelInboundHandlerAdapter channelInboundHandlerAdapter = new ChannelInboundHandlerAdapter() {
                        @Override
                        public void handlerAdded(ChannelHandlerContext ctx) {
                            System.out.println("~~" + getClass().getSimpleName() + ".handlerAdded~~");
                            System.out.println("ctx is " + ctx);
                        }

                        @Override
                        public void handlerRemoved(ChannelHandlerContext ctx) {
                            System.out.println("~~" + getClass().getSimpleName() + ".handlerRemoved~~");
                            System.out.println("ctx is " + ctx);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) {
                            System.out.println("~~" + getClass().getSimpleName() + ".channelRead~~");
                            System.out.println("ctx is " + ctx);
                            System.out.println("msg is " + msg);
                            ByteBuf byteBuf = (ByteBuf) msg;

                            byte[] bytes;

                            System.out.println(byteBuf.readableBytes());
                            System.out.println("msg is " + msg);

                            System.out.println(byteBuf.readByte());

                            System.out.println(byteBuf.readableBytes());
                            System.out.println(byteBuf.readByte());
                            System.out.println(byteBuf.readableBytes());


//                            for (int i = 0; i < bytes.length; i++) {
//                                System.out.println(bytes[i]);
//
//                            }

//                            System.out.println(bytes.length);




                        }
                    };


                    ch.pipeline().addLast(channelInboundHandlerAdapter);

                }
            };

            serverBootstrap.childHandler(init)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
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
