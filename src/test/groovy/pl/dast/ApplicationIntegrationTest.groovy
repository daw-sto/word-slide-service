package pl.dast

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.servlet.MockMvc
import pl.dast.store.IStore
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(classes = [Application, MockConfig])
@AutoConfigureMockMvc
class ApplicationIntegrationTest extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    IStore store

    def "should initialize context and call service"() {
        given:
        def sentence = "A B C"
        store.hasValue(sentence) >> true
        store.getValue(sentence) >> 4

        when:
        def mvcResult = mvc
                .perform(get("/getSlides")
                .param("sentence", sentence))
                .andExpect(status().is2xxSuccessful())
                .andReturn()

        then:
        mvc != null
        mvcResult.getResponse().getContentAsString() == "{\"${sentence}\":4}"
    }

    @TestConfiguration
    static class MockConfig {
        private DetachedMockFactory factory = new DetachedMockFactory()

        @Bean
        @Primary
        IStore iStore() {
            return factory.Stub(IStore)
        }
    }
}