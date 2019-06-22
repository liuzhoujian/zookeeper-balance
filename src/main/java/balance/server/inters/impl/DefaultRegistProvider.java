package balance.server.inters.impl;

import balance.server.inters.RegistProvider;
import balance.server.model.ZookeeperRegistContext;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

public class DefaultRegistProvider implements RegistProvider {

    public void regist(Object context) throws Exception {
        ZookeeperRegistContext zkContext = (ZookeeperRegistContext)context;

        String path = zkContext.getPath();
        ZkClient zkClient = zkContext.getZkClient();

        try{
            zkClient.createEphemeral(path, zkContext.getData());
        } catch (ZkNoNodeException e) {
            //说明parentNode没有被创建
            String parentDir = path.substring(0, path.lastIndexOf("/"));
            zkClient.createPersistent(parentDir, true);

            //再执行注册
            regist(context);
        }
    }

    public void unRegist(Object context) throws Exception {
        //ignore;
    }
}
