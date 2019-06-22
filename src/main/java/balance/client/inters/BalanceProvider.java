package balance.client.inters;

public interface BalanceProvider<T> {
    T getBalanceItem();
}
