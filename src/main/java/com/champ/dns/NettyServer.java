package com.champ.dns;

import java.util.logging.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DatagramDnsQueryDecoder;
import io.netty.handler.codec.dns.DatagramDnsResponseEncoder;

public class NettyServer {
    private static final Logger logger = Logger.getLogger(NettyServer.class.getName());

    public void run(int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel channel) {
                            ChannelPipeline pipeline = channel.pipeline();

                            pipeline.addLast(new DatagramDnsQueryDecoder())
                                    .addLast(new DatagramDnsResponseEncoder())
                                    .addLast(new DNSHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            logger.info(String.format("Starting DNS Server on port: %d", port));
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyServer dns = new NettyServer();
        dns.run(8080);
    }
}