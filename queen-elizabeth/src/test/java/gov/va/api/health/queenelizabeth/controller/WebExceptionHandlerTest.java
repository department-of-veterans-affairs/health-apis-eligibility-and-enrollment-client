package gov.va.api.health.queenelizabeth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.queenelizabeth.ee.Eligibilities;
import gov.va.api.health.queenelizabeth.ee.SoapMessageGenerator;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

@RunWith(Parameterized.class)
public class WebExceptionHandlerTest {

  @Parameterized.Parameter(0)
  public HttpStatus httpStatus;

  @Parameterized.Parameter(1)
  public Exception exception;

  @Mock Eligibilities eligibilities;

  private WebExceptionHandler webExceptionHandler;

  private QueenElizabethV1ApiController controller;

  @Parameterized.Parameters(name = "(index):[0] - [1]")
  public static List<Object[]> parameters() {
    SoapMessageGenerator soapMessageGenerator =
        SoapMessageGenerator.builder()
            .eeUsername("eeTestUsername")
            .eePassword("eeTestPassword")
            .eeRequestName("eeTestRequestName")
            .id("1010101010V666666")
            .build();
    return Arrays.asList(
        test(HttpStatus.BAD_REQUEST, new Eligibilities.MissingIcnValue(soapMessageGenerator)),
        test(
            HttpStatus.BAD_REQUEST,
            new javax.validation.ConstraintViolationException(new HashSet<>())),
        test(
            HttpStatus.INTERNAL_SERVER_ERROR,
            new Eligibilities.RequestFailed(soapMessageGenerator, "")),
        test(HttpStatus.INTERNAL_SERVER_ERROR, new RuntimeException()));
  }

  private static Object[] test(HttpStatus status, Exception exception) {
    return new Object[] {status, exception};
  }

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    controller = new QueenElizabethV1ApiController(eligibilities);
    webExceptionHandler = new WebExceptionHandler();
  }

  private ExceptionHandlerExceptionResolver createExceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver =
        new ExceptionHandlerExceptionResolver() {

          @Override
          protected ServletInvocableHandlerMethod getExceptionHandlerMethod(
              HandlerMethod handlerMethod, Exception ex) {
            Method method =
                new ExceptionHandlerMethodResolver(WebExceptionHandler.class).resolveMethod(ex);
            assertThat(method).isNotNull();
            return new ServletInvocableHandlerMethod(webExceptionHandler, method);
          }
        };
    exceptionResolver
        .getMessageConverters()
        .add(new MappingJackson2HttpMessageConverter(JacksonConfig.createMapper()));
    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }

  @Test
  @SneakyThrows
  public void expectStatus() {
    when(eligibilities.request(Mockito.any())).thenThrow(exception);
    MockMvc mvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setHandlerExceptionResolvers(createExceptionResolver())
            .setMessageConverters()
            .build();
    mvc.perform(get("/api/v1/eligibilityandenrollment?id=1010101010V666666"))
        .andExpect(status().is(httpStatus.value()))
        .andExpect(jsonPath("type", equalTo(exception.getClass().getSimpleName())));
  }
}
