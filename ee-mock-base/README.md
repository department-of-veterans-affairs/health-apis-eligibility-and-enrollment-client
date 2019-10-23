# ee-mock-base

Provides base configuration and endpoint classes used to configure a Mock eeSummary SOAP service that provides custom client and server detail fault handling via a custom exception resolver.

Mock services only need to provide their custom implementation to obtain a response for an ICN.

The WS Configurer Adapter to can be optionally configured to use a `SecurityInterceptor` to use a callback handler to perform `UsernameToken` validation.

## Usage

1. Import the `MockEeSummarySoapServiceConfig.class` in the application.

2. Mock application must simply provide an implementation of `AbstractMockEeSummaryResponse`, specifically the `obtainResponse` method to provide a response associated with an ICN.  

3. Optional: if the `SecurityInterceptor` `UsernameToken` validation is desired add the following application.properties:

```
ee.header.username=HealthAPISvcUsr
ee.header.password=PASSWORD
```

## Known Issues/Concerns

1. Much of this module could be moved out of the `ee-parent-api` repo for other uses.  For example, the `MockEeSummarySoapServiceWsConfigurerAdapter` contains hard coded values specific to the definition of a `mockee` service but could be refactored as a configurable or abstract class.  Additionally, the `faults` package and the `SecurityInterceptor` is EE agnostic.
