package balance.server.inters.impl;

import balance.server.inters.BalanceUpdateProvider;
import balance.server.model.ServerData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkBadVersionException;
import org.apache.zookeeper.data.Stat;

public class DefaultBalanceUpdateProvider implements BalanceUpdateProvider {

    private String serverPath;
    private ZkClient zkClient;

    public DefaultBalanceUpdateProvider(String serverPath, ZkClient zkClient) {
        this.serverPath = serverPath;
        this.zkClient = zkClient;
    }

    public boolean addBalance(Integer step) {
        Stat stat = new Stat();//记录版本信息，在写入的时候查看版本信息和之前读取的是否一致，不一致再次重读，一致再写入（乐观锁）。
        ServerData data = null;
        while(true) {
            try {
                data = zkClient.readData(this.serverPath, stat);
                data.setBalance(data.getBalance() + step);
                zkClient.writeData(this.serverPath, data, stat.getVersion());

                return true;
            } catch (ZkBadVersionException e) {
                //ignore; 发生这种版本不匹配的异常，再次尝试
            } catch (Exception e) {
                return false;
            }
        }
    }

    public boolean reduceBalance(Integer step) {
        Stat stat = new Stat();
        ServerData data = null;

        while (true) {
            try{
                data = zkClient.readData(this.serverPath, stat);
                data.setBalance(data.getBalance() > step ? data.getBalance() - step : 0);
                zkClient.writeData(this.serverPath, data, stat.getVersion());

                return true;
            } catch (ZkBadVersionException e) {
                //ignore; 再次重试
            } catch (Exception e) {
                return false;
            }
        }
    }
}
