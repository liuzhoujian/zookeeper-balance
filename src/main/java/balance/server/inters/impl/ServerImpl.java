package balance.server.inters.impl;

import balance.server.inters.RegistProvider;
import balance.server.inters.Server;
import balance.server.model.ServerData;
import balance.server.model.ZookeeperRegistContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * 服务器：
 * 1、注册到zk节点，并开启socket监听
 * 2、连接一个client，将服务器的负载加一，端开则减一
 */
public class ServerImpl implements Server {
    //netty相关
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private ChannelFuture cf = null;

    //zookeeper服务器连接地址
    private String zkAddress;
    //服务器列表节点路径
    private String serversPath;
    private ServerData serverData;
    //zk客户端
    private ZkClient zkClient;
    //注册工具类
    private RegistProvider registProvider;

    private static final int SESSION_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;

    private String currentServerPath = null;
    private volatile static boolean binded = false;

    public ServerImpl(String zkAddress, String serversPath, ServerData serverData) {
        this.zkAddress = zkAddress;
        this.serversPath = serversPath;
        this.serverData = serverData;
        this.zkClient = new ZkClient(zkAddress, SESSION_TIMEOUT, CONNECTION_TIMEOUT, new SerializableSerializer());
        this.registProvider = new DefaultRegistProvider();
    }

    //初始化服务端
    public void initRunning() throws Exception {
        String mePath = this.serversPath.concat("/").concat(serverData.getPort().toString());
        registProvider.regist(new ZookeeperRegistContext(mePath, serverData, zkClient));
        currentServerPath = mePath;
    }

    public void bind() {
        if(binded) {
            return;
        }

        System.out.println(serverData.getPort()+":binding...");

        try {
            initRunning();
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ServerHandler(new DefaultBalanceUpdateProvider(currentServerPath, zkClient)));
                    }
                });

        try {
            cf = serverBootstrap.bind(Integer.valueOf(serverData.getPort())).sync();
            binded = true;
            System.out.println(serverData.getPort()+":binded...");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public String getServersPath() {
        return serversPath;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public String getCurrentServerPath() {
        return currentServerPath;
    }
}
