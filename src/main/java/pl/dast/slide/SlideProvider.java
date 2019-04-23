package pl.dast.slide;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.Objects;

public class SlideProvider {

    static final char WORD_SEPARATOR = ' ';

    private final List<String> splitted;

    public SlideProvider(String sentence) {
        splitted = Splitter
                .on(WORD_SEPARATOR)
                .trimResults()
                .omitEmptyStrings()
                .splitToList(Objects.toString(sentence, ""));
    }

    public int getMaxSlideSize() {
        return splitted.size();
    }

    public String getSlideBetweenWords(int startIdxInclusively, int endIdxExclusively) {
        return Joiner
                .on(WORD_SEPARATOR)
                .join(splitted.subList(startIdxInclusively, endIdxExclusively));
    }

}
