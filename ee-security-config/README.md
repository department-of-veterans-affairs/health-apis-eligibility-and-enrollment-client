# ee-security-config

Configuration beans to facilitate WS Security Configurations.

1. WsSecurityHeaderConfig - configuration bean to hold configured WS Security Header settings for SOAP Messages with the values from property file.

2. SslContextConfig - configuration bean to hold the SSL Context using a configured trust store.

## Usage

### SslContextConfig

1. Import the `SslContextConfig.class` in the application.
2. Specify the following application.properties:
```
ee.truststore.path=test-truststore.jks
ee.truststore.password=secret
```

### WsSecurityHeaderConfig

1. Import the `WsSecurityHeaderConfig.class` in the application.
2. Specify the following application.properties:
```
# Commented properties are optional and show the default values used if not specified.
#ee.header.namespace=wsse
#ee.header.schema=http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd
ee.header.username=HealthAPISvcUsr
ee.header.password=PASSWORD
```

## Known Issues/Concerns

1. Other than the application.property names (for backwards compatibility) used by these configuration beans, this module is EE agnostic and could theoretically be moved out of the `ee-apis-parent` repo for other uses.
