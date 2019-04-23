package pl.dast.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dast.service.WordSlidesService;
import pl.dast.store.async.AsyncStoreLoaderException;

import java.util.Map;

@RestController
public class WordSlidesController {

    private WordSlidesService wordSlidesService;

    public WordSlidesController(@Autowired WordSlidesService wordSlidesService) {
        this.wordSlidesService = wordSlidesService;
    }

    @RequestMapping("/getSlides")
    public Map<String, Integer> getSlides(@RequestParam(value = "sentence") String sentence) throws AsyncStoreLoaderException {
        return wordSlidesService.findSlides(sentence);
    }
}
