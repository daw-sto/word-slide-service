package pl.dast.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.dast.slide.SlidesFinder;
import pl.dast.store.async.AsyncStoreLoader;
import pl.dast.store.async.AsyncStoreLoaderException;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

@Slf4j
@Component
@ThreadSafe
public class WordSlidesService {

    private final AsyncStoreLoader asyncStoreLoader;
    private final SlidesFinder slidesFinder;

    public WordSlidesService(@Autowired AsyncStoreLoader asyncStoreLoader,
                             @Autowired SlidesFinder slidesFinder) {
        this.asyncStoreLoader = asyncStoreLoader;
        this.slidesFinder = slidesFinder;
    }

    public Map<String, Integer> findSlides(@NonNull String sentence) throws AsyncStoreLoaderException {
        log.info("Finding slides for sentence '{}'", sentence);
        Set<String> foundSlides = slidesFinder.searchForSlides(sentence);
        Map<String, Integer> asyncStoreLoaderValues = asyncStoreLoader.getValues(foundSlides);
        log.info("Found '{}' slides for sentence '{}'", asyncStoreLoaderValues.size(), sentence);
        return asyncStoreLoaderValues;
    }
}
