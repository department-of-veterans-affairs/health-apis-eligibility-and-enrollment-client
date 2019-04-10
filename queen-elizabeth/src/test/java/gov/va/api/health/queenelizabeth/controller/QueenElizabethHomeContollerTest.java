package gov.va.api.health.queenelizabeth.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {QueenElizabethHomeController.class})
public class QueenElizabethHomeContollerTest {
  @MockBean Eligibilities eligibilities;

  @Autowired private MockMvc mockMvc;

  @Test
  @SneakyThrows
  public void openApiJson() {
    mockMvc
        .perform(get("/openapi.json"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi", equalTo("3.0.1")));
  }

  @Test
  @SneakyThrows
  public void openapiYaml() {
    String expected =
        StreamUtils.copyToString(
            getClass().getResourceAsStream("/ee-api-v1.yaml"), StandardCharsets.UTF_8);
    mockMvc
        .perform(get("/openapi.yaml"))
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(expected)));
  }

  @Test
  @SneakyThrows
  public void openapiYamlFromIndex() {
    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi", equalTo("3.0.1")));
  }
}
