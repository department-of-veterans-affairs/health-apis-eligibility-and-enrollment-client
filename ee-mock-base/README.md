# ee-mock-base

 A WS Configurer Adapter to configure a Mock eeSummary SOAP service that provides custom client and server detail fault handling via a custom exception resolver.

 This adapter can be optionally configured to use a `SecurityInterceptor` to use a callback handler to perform `UsernameToken` validation.

## Usage

1. Import the `MockEeSummarySoapServiceWsConfigurerAdapterConfig.class` in the application.

2. Optional: if the `SecurityInterceptor` `UsernameToken` validation is desired add the following application.properties:

```
ee.header.username=HealthAPISvcUsr
ee.header.password=PASSWORD
```

## Known Issues/Concerns

1. Other than the `MockEeSummarySoapServiceWsConfigurerAdapter` containing hard coded values specific to the definition of a `mockee` service, this module is EE agnostic.  The bulk of the `MockEeSummarySoapServiceWsConfigurerAdapter` could be refactored as a configurable or abstract class, the `faults` package, and the `SecurityInterceptor` could theoretically be moved out of the `ee-parent-api` repo for other uses.
