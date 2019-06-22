package balance.server.model;

import lombok.Data;
import org.I0Itec.zkclient.ZkClient;

import java.io.Serializable;

/**
 * 注册上下文
 */
@Data
public class ZookeeperRegistContext implements Serializable {
    private String path;
    private ZkClient zkClient;
    private Object data;

    public ZookeeperRegistContext(String path, Object data, ZkClient zkClient) {
        this.path = path;
        this.zkClient = zkClient;
        this.data = data;
    }
}
