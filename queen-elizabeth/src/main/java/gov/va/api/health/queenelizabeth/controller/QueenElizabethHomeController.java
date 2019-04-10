package gov.va.api.health.queenelizabeth.controller;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QueenElizabethHomeController {
  private static final YAMLMapper YAML_MAPPER = new YAMLMapper();

  private final Resource openapi;

  @Autowired
  public QueenElizabethHomeController(@Value("classpath:/ee-api-v1.yaml") Resource openapi) {
    this.openapi = openapi;
  }

  /** The OpenAPI specific content in yaml form. */
  @Bean
  public String openapiContent() throws IOException {
    try (InputStream inputStream = openapi.getInputStream()) {
      return StreamUtils.copyToString(inputStream, Charset.defaultCharset());
    }
  }

  /* RESTful interfaces - Json and YAML. */

  /**
   * Provide access to the OpenAPI as JSON via RESTful interface. This is also used as the /
   * redirect.
   */
  @GetMapping(
    value = {"/", "/openapi.json"},
    produces = "application/json"
  )
  @ResponseBody
  public Object openapiJson() throws IOException {
    return QueenElizabethHomeController.YAML_MAPPER.readValue(openapiContent(), Object.class);
  }

  /** Provide access to the OpenAPI yaml via RESTful interface. */
  @GetMapping(value = "/openapi.yaml", produces = "application/vnd.oai.openapi")
  @ResponseBody
  public String openapiYaml() throws IOException {
    return openapiContent();
  }
}
