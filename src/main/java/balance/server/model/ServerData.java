package balance.server.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 用以描述服务器的数据
 */
@Data
public class ServerData implements Serializable, Comparable<ServerData> {
    //用以描述当前服务器负载大小
    private Integer balance;
    //服务器地址
    private String host;
    //服务器端口
    private String port;

    public int compareTo(ServerData another) {
        return this.getBalance().compareTo(another.balance);
    }
}
