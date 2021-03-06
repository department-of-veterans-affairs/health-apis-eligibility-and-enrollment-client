package gov.va.api.health.queenelizabeth.ee.mock.endpoints;

import gov.va.api.health.queenelizabeth.ee.mock.faults.ServiceFaultClientException;
import gov.va.api.health.queenelizabeth.ee.mock.faults.ServiceFaultServerException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Mock the EE response entity to load a sample file associated with the specified ICN.
 *
 * <p>If the ICN file sample can not be found then a PersonNotFound fault is simulated.
 *
 * <p>Other faults can be simulated by specifying the associated fault enum key as an ICN.
 */
@Component
public class MockEeSummaryResponse {

  @SneakyThrows
  public String getEeSummaryRequest(String icn) {
    Assert.notNull(icn, "The icn must not be null");
    // First check if this is a fault key.
    // If a fault is not found then assume this is an actual icn.
    final FaultEnum fault = FaultEnum.getFaultEnum(icn);
    if (fault != null) {
      // If connection refused then throw server exception to simulate the condition.
      throw new ServiceFaultServerException(fault.getMessage(), fault.getDetail());
    }
    // Attempt to load a string for the response from a sample file.
    // If file not found then a person not found exception is simulated.
    String response;
    try {
      response =
          new String(
              Files.readAllBytes(Paths.get("src", "test", "resources", "samples", icn + ".xml")));
    } catch (Exception e) {
      throw new ServiceFaultClientException(
          "PERSON_NOT_FOUND",
          "Unable to invoke SOA ESR due to: Unable to invoke service with EntityFinder arg: HealthAPISvcUsr:CommunityCareInfo due to exception message: Invalid VPID and root cause stack trace: gov.va.med.person.idmgmt.exceptions.VPIDException: Invalid VPID checkdigit: 123456");
    }
    return response;
  }

  /**
   * The fault enum is the simulated faults. Note the fault samples available often show the same
   * string for fault string and detail.
   *
   * <p>To simulate additional faults, simply add a new enumeration value and have unit test use the
   * key as an ICN in the request.
   *
   * <p>This is probably not correct, but the mock is configured to emulate the samples provided.
   */
  @AllArgsConstructor
  public enum FaultEnum {
    CONNECTION_REFUSED(
        "faultConnectionRefused",
        "Unable to invoke SOA ESR due to: Failed to locate remote EJB [ejb/PersonServiceBean]; nested exception is javax.naming.CommunicationException: t3://vaausappesr801.aac.va.gov:7402: [RJVM:000575]Destination 10.226.85.177, 7402 unreachable.; nested exception is: java.net.ConnectException: Connection refused; [RJVM:000576]No available router to destination.; nested exception is: java.rmi.ConnectException: [RJVM:000576]No available router to destination. [Root exception is java.net.ConnectException: t3://vaausappesr801.aac.va.gov:7402: [RJVM:000575]Destination 10.226.85.177, 7402 unreachable.; nested exception is: java.net.ConnectException: Connection refused; [RJVM:000576]No available router to destination.; nested exception is: java.rmi.ConnectException: [RJVM:000576]No available router to destination.]",
        "Unable to invoke SOA ESR due to: Failed to locate remote EJB [ejb/PersonServiceBean]; nested exception is javax.naming.CommunicationException: t3://vaausappesr801.aac.va.gov:7402: [RJVM:000575]Destination 10.226.85.177, 7402 unreachable.; nested exception is: java.net.ConnectException: Connection refused; [RJVM:000576]No available router to destination.; nested exception is: java.rmi.ConnectException: [RJVM:000576]No available router to destination. [Root exception is java.net.ConnectException: t3://vaausappesr801.aac.va.gov:7402: [RJVM:000575]Destination 10.226.85.177, 7402 unreachable.; nested exception is: java.net.ConnectException: Connection refused; [RJVM:000576]No available router to destination.; nested exception is: java.rmi.ConnectException: [RJVM:000576]No available router to destination.]");

    @Getter private String key;

    @Getter private String message;

    @Getter private String detail;

    /**
     * Get the fault enum for the specified key.
     *
     * @param key The key for which to search.
     * @return The enum for the key or null if not found.
     */
    public static FaultEnum getFaultEnum(String key) {
      if (key != null) {
        for (FaultEnum fault : FaultEnum.values()) {
          if (key.equals(fault.getKey())) {
            return fault;
          }
        }
      }
      return null;
    }
  }
}
