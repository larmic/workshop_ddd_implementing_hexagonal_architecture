package de.larmic.ddd.infrastructure.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(HelloWorldRestController::class)
internal class HelloWorldRestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    internal fun `say hello`() {
        val content = this.mockMvc.perform(get("/api/hello").contentType(MediaType.TEXT_PLAIN))
            .andDo(print())
            .andExpect(status().is2xxSuccessful)
            .andReturn().response.contentAsString

        assertThat(content).isEqualTo("Hello World!")
    }
}