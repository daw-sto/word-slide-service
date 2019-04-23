package pl.dast.store;

public interface IStore {

    boolean hasValue(String key);

    int getValue(String key);
}
