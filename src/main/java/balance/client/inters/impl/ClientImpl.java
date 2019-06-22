package balance.client.inters.impl;

import balance.client.inters.BalanceProvider;
import balance.client.inters.Client;
import balance.server.model.ServerData;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端：根据负载均衡策略选取一个服务器进行连接
 */
public class ClientImpl implements Client {

    //netty相关
    private EventLoopGroup group = null;
    private Bootstrap bootstrap = null;
    private Channel channel = null;

    private BalanceProvider<ServerData> balanceProvider;

    public ClientImpl(BalanceProvider<ServerData> balanceProvider) {
        this.balanceProvider = balanceProvider;
    }

    public BalanceProvider<ServerData> getBalanceProvider() {
        return balanceProvider;
    }

    public void connect() throws Exception {
        //客户端根据负载均衡策略选取一个服务器
        ServerData sd = balanceProvider.getBalanceItem();
        System.out.println("connecting to "+sd.getHost()+":"+sd.getPort()+", it's balance:"+sd.getBalance());

        //根据选取的服务器，建立netty连接
        try {
            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(null);
                        }
                    });

            ChannelFuture f = bootstrap.bind(sd.getHost(), Integer.valueOf(sd.getPort())).syncUninterruptibly();
            System.out.println("started success!");
            channel = f.channel();
        } catch (Exception e) {
            System.out.println("连接异常:"+e.getMessage());
        }
    }

    public void disconnect() throws Exception {
        try{
            if(channel != null) {
                channel.close().syncUninterruptibly();
            }

            group.shutdownGracefully();
            group = null;
            System.out.println("disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
