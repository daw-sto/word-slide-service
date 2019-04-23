package pl.dast.store;

import org.springframework.stereotype.Component;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.ThreadLocalRandom;

@ThreadSafe
@Component
public class RandomStoreImpl implements IStore {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;

    @Override
    public boolean hasValue(String key) {
        return ThreadLocalRandom.current().nextBoolean();
    }

    @Override
    public int getValue(String key) {
        return ThreadLocalRandom.current().nextInt(MIN_VALUE, MAX_VALUE + 1);
    }
}
