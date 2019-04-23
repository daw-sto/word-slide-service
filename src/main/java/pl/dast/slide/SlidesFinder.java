package pl.dast.slide;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.dast.store.IStore;

import java.util.Set;

@Component
public class SlidesFinder {

    private final IStore store;

    public SlidesFinder(@Autowired IStore store) {
        this.store = store;
    }

    public Set<String> searchForSlides(String sentence) {
        SlideProvider slideProvider = new SlideProvider(sentence);
        Set<String> foundSlides = Sets.newHashSet();
        searchForSlides(foundSlides, slideProvider, 0, slideProvider.getMaxSlideSize(), slideProvider.getMaxSlideSize());
        return foundSlides;
    }

    private void searchForSlides(Set<String> foundSlides,
                                 SlideProvider slideProvider,
                                 int startIdx,
                                 int endIdx,
                                 int slideLength) {
        if (startIdx >= endIdx || slideLength <= 0) {
            return;
        }

        int currentSlideStartIdx = startIdx;
        int lastConsumedSlideEndIdx = currentSlideStartIdx;
        while (currentSlideStartIdx + slideLength <= endIdx) {
            String currentSlide = slideProvider.getSlideBetweenWords(currentSlideStartIdx, currentSlideStartIdx + slideLength);
            if (store.hasValue(currentSlide)) {
                foundSlides.add(currentSlide);
                if (lastConsumedSlideEndIdx < currentSlideStartIdx) {
                    searchForSlides(foundSlides, slideProvider, lastConsumedSlideEndIdx, currentSlideStartIdx, currentSlideStartIdx - lastConsumedSlideEndIdx);
                }
                currentSlideStartIdx += slideLength;
                lastConsumedSlideEndIdx = currentSlideStartIdx;
            } else {
                currentSlideStartIdx++;
            }
        }

        if (lastConsumedSlideEndIdx == startIdx) {
            searchForSlides(foundSlides, slideProvider, startIdx, endIdx, slideLength - 1);
        } else if (lastConsumedSlideEndIdx < endIdx) {
            searchForSlides(foundSlides, slideProvider, lastConsumedSlideEndIdx, endIdx, endIdx - lastConsumedSlideEndIdx);
        }
    }

}
