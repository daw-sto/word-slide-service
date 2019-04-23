package pl.dast.service

import pl.dast.slide.SlidesFinder
import pl.dast.store.async.AsyncStoreLoader
import spock.lang.Specification


class WordSlidesServiceSpec extends Specification {

    private AsyncStoreLoader asyncStoreLoader
    private SlidesFinder slidesFinder

    void setup() {
        asyncStoreLoader = Mock(AsyncStoreLoader.class)
        slidesFinder = Mock(SlidesFinder.class)
    }

    def "should find slides"() {
        given:
        def sentence = "A B C"
        def slideSet = ["B C"] as Set

        slidesFinder.searchForSlides(sentence) >> slideSet
        asyncStoreLoader.getValues(slideSet) >> ["B C":1]
        WordSlidesService wordSlidesService = new WordSlidesService(asyncStoreLoader, slidesFinder)

        when:
        def slides = wordSlidesService.findSlides(sentence)

        then:
        1 * slidesFinder.searchForSlides(sentence) >> slideSet

        then:
        1 * asyncStoreLoader.getValues(slideSet) >> ["B C":1]

        expect:
        slides == ["B C":1]

    }
}