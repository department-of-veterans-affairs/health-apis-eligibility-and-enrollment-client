package gov.va.api.health.queenelizabeth.ee;

import gov.va.api.health.queenelizabeth.ee.exceptions.MissingIcnValue;
import gov.va.api.health.queenelizabeth.ee.handlers.BaseQueenElizabethSoapHandler;
import gov.va.api.health.queenelizabeth.ee.handlers.GetEeSummaryRequestFaultSoapHandler;
import gov.va.api.health.queenelizabeth.ee.handlers.WsSecurityHeaderSoapHandler;
import gov.va.api.health.queenelizabeth.ee.impl.EeSummaryEndpointConfig;
import gov.va.api.health.queenelizabeth.ee.impl.GetEeSummaryRequestConfig;
import gov.va.med.esr.webservices.jaxws.schemas.EeSummaryPort;
import gov.va.med.esr.webservices.jaxws.schemas.EeSummaryPortService;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryRequest;
import gov.va.med.esr.webservices.jaxws.schemas.GetEESummaryResponse;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service class to wrap the eeSummary soap services. NOTE: at this time, only the getEeSummary soap
 * service is provided, but others could easily be added here as needed by implementing appropriate
 * configuration classes, fault handlers, and methods. Encapsulated eeSummary soap services are
 * named for consistency with the service.
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class QueenElizabethService {
  public static final String MISSING_ICN_MESSAGE = "ICN must be non-null and non-blank.";

  private final EeSummaryEndpointConfig config;

  private final GetEeSummaryRequestConfig getEeSummaryRequestConfig;

  private final GetEeSummaryRequestFaultSoapHandler getEeSummaryRequestFaultSoapHandler;

  private final WsSecurityHeaderSoapHandler wsSecurityHeaderSoapHandler;

  /**
   * Add handlers to the service binding.
   *
   * @param binding The service binding.
   */
  private void addHandlersToBinding(Binding binding, final BaseQueenElizabethSoapHandler handler) {
    // Add custom soap handlers.
    // Note the order added results in executed first on request/last on response.
    @SuppressWarnings("rawtypes")
    List<Handler> handlers = binding.getHandlerChain();
    handlers.add(handler);
    addOptionalWsSecurityHeaderSoapHandler(handlers);
    binding.setHandlerChain(handlers);
  }

  /**
   * The WsSecurityHeaderSoapHandler is optional but can be added if non-null.
   *
   * @param handlers List of handlers to add security handler.
   */
  @SuppressWarnings("rawtypes")
  private void addOptionalWsSecurityHeaderSoapHandler(List<Handler> handlers) {
    if (wsSecurityHeaderSoapHandler != null) {
      handlers.add(wsSecurityHeaderSoapHandler);
    }
  }

  /**
   * Request getEESummary from the getEESummary SOAP Service repeatedly for a list of ICN.
   *
   * @param icnList List of ICN.
   * @return Linked List of GetEESummaryResponse.
   */
  public List<GetEESummaryResponse> getEeSummary(final List<String> icnList) {
    List<GetEESummaryResponse> eeSummaryResponses = new ArrayList<>();
    for (String icn : icnList) {
      eeSummaryResponses.add(getEeSummary(icn));
    }
    return eeSummaryResponses;
  }

  /**
   * Request getEESummary from the getEESummary SOAP Service.
   *
   * @param icn Icn.
   * @return GetEESummaryResponse.
   */
  public GetEESummaryResponse getEeSummary(final String icn) {
    if ((icn == null) || icn.isBlank()) {
      throw new MissingIcnValue(MISSING_ICN_MESSAGE);
    }
    EeSummaryPort port = new EeSummaryPortService().getEeSummaryPortSoap11();
    overrideEndpointAddressProperty((BindingProvider) port);
    Binding binding = ((BindingProvider) port).getBinding();
    addHandlersToBinding(binding, getEeSummaryRequestFaultSoapHandler);
    return port.getEESummary(
        GetEESummaryRequest.builder()
            .key(icn)
            .requestName(getEeSummaryRequestConfig.getName())
            .build());
  }

  /**
   * Override the SOAP Service endpoint address (if configured).
   *
   * @param bindingProvider Binding provider to update.
   */
  public void overrideEndpointAddressProperty(BindingProvider bindingProvider) {
    if (config.getUrl() != null) {
      bindingProvider
          .getRequestContext()
          .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, config.getUrl());
    }
  }
}
