import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/10/24 13:42.
 */
public class NettyTrial {

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

            while (byteBuf.isReadable()) System.out.println(byteBuf.readByte());
            byteBuf.release();


            String s = "ok";
            byteBuf = Unpooled.buffer(s.length());
            for (char c : s.toCharArray()) byteBuf.writeChar(c);

            ChannelFuture channelFuture = ctx.writeAndFlush(byteBuf);

            System.out.println(channelFuture);
            System.out.println(channelFuture.channel());
//            channelFuture.addListener(ChannelFutureListener.CLOSE);
            channelFuture.addListener(future -> {
                ChannelFuture cf = (ChannelFuture) future;
                System.out.println(cf .channel());
                cf.channel().close();
                ctx.close();
                System.out.println("closing");
            });


//            ctx.flush();
//            ctx.close().addListener(future -> {
//                System.out.println("closed");
//            });


        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelActive~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelInactive~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".exceptionCaught~~");
            ctx.close();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~" + getClass().getSimpleName() + ".channelReadComplete~~");
            System.out.println("ctx is " + ctx);
        }
    };


    ChannelInboundHandlerAdapter empty = new ChannelInboundHandlerAdapter() {
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".handlerAdded~~");
            System.out.println("ctx is " + ctx);

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".handlerRemoved~~");
            System.out.println("ctx is " + ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelRead~~");
            System.out.println("ctx is " + ctx);
            System.out.println("msg is " + msg);


            ctx.fireChannelRead(msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelActive~~");
            System.out.println("ctx is " + ctx);

            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelInactive~~");
            System.out.println("ctx is " + ctx);

            ctx.fireChannelInactive();
        }



        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".exceptionCaught~~");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            System.out.println("~~empty|" + getClass().getSimpleName() + ".channelReadComplete~~");
            System.out.println("ctx is " + ctx);

            ctx.fireChannelReadComplete();
        }
    };


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
            serverBootstrap.localAddress(new InetSocketAddress("192.168.0.127", 5454));

            ChannelInitializer<SocketChannel> init = new ChannelInitializer<>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    System.out.println("~~" + getClass().getSimpleName() + ".initChannel~~");
                    System.out.println("ch is " + ch);



//                    ch.pipeline().addLast(empty);
//                    ch.pipeline().addLast(channelInboundHandlerAdapter);
//                    ch.pipeline().addLast(empty, channelInboundHandlerAdapter);
                    ch.pipeline().addLast(new MessageToMessageDecoder<Integer>() {
                        @Override
                        protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {

                        }
                    });

                }
            };

            serverBootstrap.childHandler(init)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);



            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println("----------" + channelFuture.channel());

            channelFuture.channel()
                    .closeFuture()
                    .addListener(future -> { System.out.println("closed!!"); })
                    .sync();




        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
                System.out.println("shutdown Gracefully!");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
