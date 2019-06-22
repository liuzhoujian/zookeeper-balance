package balance.server.inters;

/**
 * 服务器增加负载和减少负载工具
 */
public interface BalanceUpdateProvider {

    boolean addBalance(Integer step);

    boolean reduceBalance(Integer step);
}
