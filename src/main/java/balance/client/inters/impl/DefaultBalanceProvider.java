package balance.client.inters.impl;

import balance.server.model.ServerData;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultBalanceProvider extends AbstractBalanceProvider<ServerData> {

    private String zkAddress;
    private String serversPath;
    private ZkClient zc;

    private static final Integer SESSION_TIME_OUT = 10000;
    private static final Integer CONNECT_TIME_OUT = 10000;

    public DefaultBalanceProvider(String zkAddress, String serversPath) {
        this.zkAddress = zkAddress;
        this.serversPath = serversPath;
        this.zc = new ZkClient(this.zkAddress, SESSION_TIME_OUT, CONNECT_TIME_OUT, new SerializableSerializer());
    }

    protected List<ServerData> getBalanceItems() {
        List<ServerData> serverDataList = new ArrayList<ServerData>();
        //获取zk servers节点的所有子节点
        List<String> children = zc.getChildren(this.serversPath);
        for (int i = 0; i < children.size(); i++) {
            ServerData data = zc.readData(this.serversPath.concat("/").concat(children.get(i).toString()));
            serverDataList.add(data);
        }

        return serverDataList;
    }

    protected ServerData balanceAlgorithm(List<ServerData> items) {
        if(items == null || items.size() < 0) {
            return null;
        }

        Collections.sort(items); //将服务器的负载从小到大排序，选取负载最小的那个
        return items.get(0);
    }

    public String getZkAddress() {
        return zkAddress;
    }

    public String getServersPath() {
        return serversPath;
    }

    public ZkClient getZc() {
        return zc;
    }
}
