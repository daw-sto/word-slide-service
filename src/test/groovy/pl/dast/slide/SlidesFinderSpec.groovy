package pl.dast.slide


import org.apache.commons.lang3.StringUtils
import pl.dast.store.IStore
import spock.lang.Specification

import java.util.concurrent.ThreadLocalRandom


class SlidesFinderSpec extends Specification {

    def "should find slides for simple sentence"(String inputSentence, Set<String> slidesInStore, Set<String> expectedSlides) {
        given:
        def slidesFinder = new SlidesFinder(new SetBackedStore(dataInStore: slidesInStore))

        expect:
        slidesFinder.searchForSlides(inputSentence) == expectedSlides

        where:
        inputSentence | slidesInStore         | expectedSlides
        ""            | []                    | []
        "A"           | []                    | []
        ""            | ["A"]                 | []
        "A"           | ["A"]                 | ["A"]
        "A"           | ["B"]                 | []
        "A B"         | ["A"]                 | ["A"]
        "A B"         | ["B"]                 | ["B"]
        "A B"         | ["A", "B"]            | ["A", "B"]
        "A B"         | ["A B"]               | ["A B"]
        "A B"         | ["A B", "A"]          | ["A B"]
        "A B A"       | ["A B", "B A"]        | ["A B"]
        "A B A"       | ["A", "B A"]          | ["A", "B A"]
        "A A A"       | ["A A A", "A A", "A"] | ["A A A"]
        "A A A"       | ["A A", "A"]          | ["A", "A A"]
        "A A A A"     | ["A", "A A"]          | ["A A"]
        "A A A A A"   | ["A", "A A"]          | ["A", "A A"]
        "A A A A A"   | ["A A", "A A A"]      | ["A A", "A A A"]
        "A A A A A A" | ["A", "A A", "A A A"] | ["A A A"]
        "A B C D E"   | ["A", "A B", "A B C"] | ["A B C"]
    }

    def "should find slides for complex sentence"(String inputSentence, Set<String> slidesInStore, Set<String> expectedSlides) {
        given:
        def slidesFinder = new SlidesFinder(new SetBackedStore(dataInStore: slidesInStore))

        expect:
        slidesFinder.searchForSlides(inputSentence) == expectedSlides

        where:
        inputSentence                             | slidesInStore                              | expectedSlides
        "A B B B A A A B B B A A"                 | ["B B B", "A", "A A", "A A A"]             | ["B B B", "A", "A A", "A A A"]
        "A B B B A A A B B B A A"                 | ["B B B", "A", "A A"]                      | ["B B B", "A", "A A"]
        "A B B B A A A B B B A A"                 | ["B B B", "A A A"]                         | ["B B B", "A A A"]
        "A B C D E"                               | ["A B", "B C", "C D", "D E"]               | ["A B", "C D"]
        "A B C D E"                               | ["A B", "B C", "C D", "D E", "B C D"]      | ["B C D"]
        "A B A C C C B A B"                       | ["C C C", "A B", "B A"]                    | ["C C C", "A B", "B A"]
        "A B A C C C B A B A"                     | ["C C C", "A B", "B A"]                    | ["C C C", "A B", "B A"]
        "X A A A D D D D B B D D D D C D D D D Y" | ["D D D D", "A A A", "B B", "X", "Y"]      | ["D D D D", "A A A", "B B", "X", "Y"]
        "A B B B A A B B A A A B"                 | ["A", "A A", "A A A", "B", "B B", "B B B"] | ["A", "A A", "A A A", "B", "B B", "B B B"]
    }

    def "should find slides for 'Mary went Mary's gone'"(String inputSentence, Set<String> slidesInStore, Set<String> expectedSlides) {
        given:
        def slidesFinder = new SlidesFinder(new SetBackedStore(dataInStore: slidesInStore))
        expect:
        slidesFinder.searchForSlides(inputSentence) == expectedSlides
        where:
        inputSentence           | slidesInStore                                                       | expectedSlides
        "Mary went Mary's gone" | ["Mary", "Mary gone", "Mary", "Mary's gone", "went Mary's", "went"] | ["went Mary's", "Mary"]
    }


    def "should find slides for random sentence"() {
        given:
        def randomSlideList = []
        ('A'..'E').each {
            def letter = "${it} "
            10.times {
                def randomStr = generateSlide(letter)
                randomSlideList.add(randomStr)
            }
        }

        Collections.shuffle(randomSlideList)

        String randomSentence = "";
        randomSlideList.each {
            randomSentence += it + " " + generateSlide("X ") + " "
        }

        def randomSlideSet = randomSlideList.toSet()
        def slidesFinder = new SlidesFinder(new SetBackedStore(dataInStore: randomSlideSet))

        when:
        def actualSlides = slidesFinder.searchForSlides(randomSentence)

        then:
        actualSlides == randomSlideSet
    }

    String generateSlide(def letter) {
        def strLength = ThreadLocalRandom.current().nextInt(1, 10)
        StringUtils.repeat(letter, strLength).trim()
    }

    class SetBackedStore implements IStore {

        Set<String> dataInStore;

        @Override
        boolean hasValue(String key) {
            return dataInStore.contains(key)
        }

        @Override
        int getValue(String key) {
            return 1
        }
    }
}