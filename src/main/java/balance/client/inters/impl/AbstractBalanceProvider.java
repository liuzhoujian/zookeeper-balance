package balance.client.inters.impl;

import balance.client.inters.BalanceProvider;

import java.util.List;

public abstract class AbstractBalanceProvider<T> implements BalanceProvider<T> {

    //客户端获取所有的服务器列表
    protected abstract List<T> getBalanceItems();

    //按照某种算法获取一个服务器
    protected abstract T balanceAlgorithm(List<T> items);

    public T getBalanceItem() {
        return balanceAlgorithm(getBalanceItems());
    }
}
