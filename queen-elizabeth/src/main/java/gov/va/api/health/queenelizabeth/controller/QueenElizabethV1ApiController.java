package gov.va.api.health.queenelizabeth.controller;

import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class QueenElizabethV1ApiController {

  private final Eligibilities eligibilities;

  /** Implementation of /v1/eligibilityandenrollment -- ee-api-v1.yaml */
  @RequestMapping(
    value = "/v1/eligibilityandenrollment",
    produces = {"application/xml", "application/json"},
    method = RequestMethod.GET
  )
  @SneakyThrows
  public ResponseEntity<String> requestEligibility(
      @Valid @RequestParam("id") String id,
      @Value("${ee.header.username}") String eeUsername,
      @Value("${ee.header.password}") String eePassword,
      @Value("${ee.request.name}") String eeRequestName) {
    String xml =
        eligibilities.request(
            SoapMessageGenerator.builder()
                .id(id)
                .eeUsername(eeUsername)
                .eePassword(eePassword)
                .eeRequestName(eeRequestName)
                .build());
    return ResponseEntity.ok().body(xml);
  }
}
