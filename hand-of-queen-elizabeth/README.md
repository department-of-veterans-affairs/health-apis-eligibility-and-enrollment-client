# hand-of-queen-elizabeth

Services to expose `eeSummary.wsdl` SOAP Services encapsulated by a QueenElizabethService class.

## Services

The QueenElizabethService class provides an API to call the following services:

* getEeSummary soap service - returns instance of GetEESummaryResponse for requested ICN.  

  If called with a list of ICN the getEeSummary will return a list of associated GetEESummaryResponse.

  NOTE: the encapsulating eeSummary soap service methods are named for consistency with the service. However, legacy requestEligibility methods are provided here, but deprecated. This may be a measure that is unnecessary and if so we should eliminate these deprecated methods.

* NOTE: other SOAP services specified by the wsdl are not implemented but the QueenElizabethService could be extended for each service by:

  * implement appropriate configuration class(es).
  * implement appropriate fault handler(s).
  * add encapsulating method for SOAP Service call to QueenElizabethService.

## Queen Elizabeth Service Usage 

The QueenElizabethService is provided to an application that desired to perform SOAP Service calls to eeSummary.  

1. Application must import the configuration class as shown in example (or use auto-configure if desired).
   
   ```
   @SpringBootApplication
   @Import({
     QueenElizabethConfig.class
   })
   public class MyApplication {
     public static void main(String[] args) {
       SpringApplication.run(MyApplication.class, args);
     }
   }
   ```

2. Application must provide these properties in `application.properties`:

   NOTE: see `src/test/resources/application.properties` for example values.

   ```
   # EE Endpoint Base URL
   ee.endpoint.url - the endpoint url is technically optional but appears to be required for a live interface as the wsdl may be out of date
   
   # EE Request Name.
   ee.request.name - required request name.
   
   # EE Security Header.
   # The security header is technically optional to run a mock but appears to be required for a live interface.
   # Commented properties are optional and show the default values used if not specified.
   #ee.header.namespace=wsse
   #ee.header.schema=http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd
   ee.header.username - user name.
   ee.header.password - password.
   
   # EE Test trust store.
   # The trust store is technically optional to run a mock but appears to be required for a live interface.
   ee.truststore.path - trust store path as found in resources class path.
   ee.truststore.password - associated trust store password.
   ```


## Example SOAP Service Responses

Test of the interface required some sample responses for nominal and off-nominal use cases.  See `src/main/resources/samples/README.txt` for further detail.


## Known Issues/Concerns

1. The SSL Context Algorithm used is 'TLS'.  It is not clear if this is entirely correct.  See SslContextConfig.java for further detail.

2. Integration tests run a mock eeSummary SOAP Service to test the messaging round trip for both nominal and off-nominal request scenarios.

   This required:
   * access to a schema associated with the `eeSummary.wsdl`. Rather than create the eeSummary.xsd by hand, the ee-artifacts project should probably be updated to dynamically pull the `eeSummary.xsd` from the `eeSummary.wsdl` and package/expose as appropriate to subsequent mock service project(s) that may require an associated schema.  This xsd and wsdl now is duplicated in this project and mock-ee.

   * implementing gov.va.api.health.queenelizabeth.ee.mock test package that contains classes very similar to mock-ee.  See `MockEeSummarySoapServiceWsConfigurerAdapter.java` for further detail.

3. Exception messages were refactored as I thought were originally implemented (wrapping reason `faultString`) but could be enhanced to capture fault codes and fault detail in a more clear message structure.  

4. Note that the `lombok.config` is configured to generate old style getters and setters.  This seemed to be needed for the configuration classes to be properly populated from application properties.

5. To eliminate an SOAP XML parsing warning the following file seemed to be required:
  `src/main/resources/META-INF/services/javax.xml.soap.SAAJMetaFactory`
