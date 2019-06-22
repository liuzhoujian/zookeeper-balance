package balance.server.inters.impl;

import balance.server.inters.BalanceUpdateProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * netty：消息处理类
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private BalanceUpdateProvider balanceUpdateProvider;
    private static final Integer BALANCE_STEP = 1;

    public ServerHandler(BalanceUpdateProvider balanceUpdateProvider) {
        this.balanceUpdateProvider = balanceUpdateProvider;
    }

    public BalanceUpdateProvider getBalanceUpdateProvider() {
        return balanceUpdateProvider;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("one client connect...");
        balanceUpdateProvider.addBalance(BALANCE_STEP);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        balanceUpdateProvider.reduceBalance(BALANCE_STEP);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
