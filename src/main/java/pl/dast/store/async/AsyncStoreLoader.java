package pl.dast.store.async;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.dast.store.IStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AsyncStoreLoader {

    private final IStore store;
    private final int threadPoolSize;
    private final int responseTimeOutInMls;

    public AsyncStoreLoader(
            @Autowired IStore store,
            @Value("${async.store.loader.thread.pool.size:10}") int threadPoolSize,
            @Value("${async.store.loader.response.timeout.in.mls:100}") int responseTimeOutInMls
    ) {
        this.store = store;
        this.threadPoolSize = threadPoolSize;
        this.responseTimeOutInMls = responseTimeOutInMls;
    }

    public Map<String, Integer> getValues(Set<String> keySet) throws AsyncStoreLoaderException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        List<Callable<Pair<String, Integer>>> collect = keySet
                .stream()
                .map(key -> new StoreLoadCallable(key))
                .collect(Collectors.toList());

        List<Future<Pair<String, Integer>>> futuresList;
        try {
            futuresList = executorService.invokeAll(collect, responseTimeOutInMls, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Error while loading values.", e);
            throw new AsyncStoreLoaderException("Exception during value retrieval.");
        }

        List<Pair<String, Integer>> keyValuePairList = new ArrayList<>();
        for (Future<Pair<String, Integer>> future : futuresList) {
            try {
                keyValuePairList.add(future.get());
            } catch (CancellationException | InterruptedException | ExecutionException e) {
                log.error("Error while loading values.", e);
                throw new AsyncStoreLoaderException("Incomplete result.");
            }
        }

        return keyValuePairList
                .stream()
                .collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue()));
    }

    @AllArgsConstructor
    class StoreLoadCallable implements Callable<Pair<String, Integer>> {
        private final String key;

        @Override
        public Pair<String, Integer> call() {
            try {
                return Pair.of(key, store.getValue(key));
            } catch (Exception e) {
                log.error("Error while loading value from store for key '{}'.", key);
                throw e;
            }
        }
    }


}
