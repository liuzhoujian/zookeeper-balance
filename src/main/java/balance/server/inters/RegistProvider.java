package balance.server.inters;

/**
 * 服务注册工具
 */
public interface RegistProvider {
    /**
     * 注册
     * @param context 上下文
     * @throws Exception
     */
    void regist(Object context) throws Exception;

    /**
     * 注销
     * @param context 上下文
     * @throws Exception
     */
    void unRegist(Object context) throws Exception;

}
