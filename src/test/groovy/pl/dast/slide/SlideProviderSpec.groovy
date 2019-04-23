package pl.dast.slide

import spock.lang.Specification

class SlideProviderSpec extends Specification {

    def "should not get any slides for null string"() {
        when:
        def slideProvider = new SlideProvider(null)

        then:
        slideProvider.maxSlideSize == 0
    }

    def "should not get any slides for empty string"() {
        when:
        def slideProvider = new SlideProvider("  ")

        then:
        slideProvider.maxSlideSize == 0
    }

    def "should get slide for single word"() {
        given:
        def slideProvider = new SlideProvider(" word  ")

        expect:
        slideProvider.maxSlideSize == 1
        slideProvider.getSlideBetweenWords(0, 1) == "word"
    }

    def "should get slides form multi word sentence"() {
        given:
        def sentence = "A B C D E"
        def slideProvider = new SlideProvider(sentence)

        expect:
        slideProvider.maxSlideSize == 5
        slideProvider.getSlideBetweenWords(0, 1) == "A"
        slideProvider.getSlideBetweenWords(2, 3) == "C"
        slideProvider.getSlideBetweenWords(4, 5) == "E"
        slideProvider.getSlideBetweenWords(1, 3) == "B C"
        slideProvider.getSlideBetweenWords(0, 4) == "A B C D"
        slideProvider.getSlideBetweenWords(1, 5) == "B C D E"
        slideProvider.getSlideBetweenWords(0, 5) == "A B C D E"
    }
}
