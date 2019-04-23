package pl.dast.store.async

import com.google.common.util.concurrent.Uninterruptibles
import pl.dast.store.IStore
import spock.lang.Specification

import java.util.concurrent.TimeUnit


class AsyncStoreLoaderSpec extends Specification {

    def "should throw exception if value not in store"() {
        given:
        def asyncStoreLoader = createLoaderBackedWithMap([] as Map)

        when:
        asyncStoreLoader.getValues(["A"] as Set)

        then:
        AsyncStoreLoaderException ex = thrown()
        ex.message == "Incomplete result."
    }

    def "should load single value from store"() {
        given:
        def asyncStoreLoader = createLoaderBackedWithMap(["A": 1])

        when:
        def result = asyncStoreLoader.getValues(["A"] as Set)

        then:
        result == ["A": 1]
    }

    def "should load many values from store"() {
        given:
        def asyncStoreLoader = createLoaderBackedWithMap(["A": 1, "B": 2, "C": 3])

        when:
        def result = asyncStoreLoader.getValues(["A", "C"] as Set)

        then:
        result == ["A": 1, "C": 3]
    }

    def "should throw incomplete result if store throws exception"() {
        given:
        def asyncStoreLoader = createLoader(new MapBackedStore() {
            @Override
            int getValue(String key) {
                throw RuntimeException();
            }
        });

        when:
        asyncStoreLoader.getValues(["A"] as Set)

        then:
        AsyncStoreLoaderException ex = thrown()
        ex.message == "Incomplete result."
    }

    def "should load value if response is on time"() {
        given:
        def timeOutInMls = 50;
        def asyncStoreLoader = createLoader(new MapBackedStore() {
            @Override
            int getValue(String key) {
                return 1
            }
        }, 2, timeOutInMls);

        when:
        def result = asyncStoreLoader.getValues(["A"] as Set)

        then:
        result == ["A": 1]
    }

    def "should throw incomplete result if response is too late"() {
        given:
        def timeOutInMls = 50;
        def asyncStoreLoader = createLoader(new MapBackedStore() {
            @Override
            int getValue(String key) {
                Uninterruptibles.sleepUninterruptibly(timeOutInMls * 2, TimeUnit.MILLISECONDS)
                return 1
            }
        }, 2, timeOutInMls);

        when:
        asyncStoreLoader.getValues(["A"] as Set)

        then:
        AsyncStoreLoaderException ex = thrown()
        ex.message == "Incomplete result."
    }


    AsyncStoreLoader createLoaderBackedWithMap(def dataInStore) {
        createLoader(new MapBackedStore(dataInStore: dataInStore))
    }

    AsyncStoreLoader createLoader(MapBackedStore customStore, threadPoolSize = 2, timeOutInMls = 50) {
        new AsyncStoreLoader(customStore, threadPoolSize, timeOutInMls);
    }

    class MapBackedStore implements IStore {

        Map<String, Integer> dataInStore;

        @Override
        boolean hasValue(String key) {
            return dataInStore.keySet().contains(key)
        }

        @Override
        int getValue(String key) {
            return dataInStore.get(key)
        }
    }

}