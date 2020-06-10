#!/usr/bin/env bash

WSDL_LOCATION='https://vaww.esrstage1a.aac.va.gov:8443/esr-ws/spring-ws/getEESummary/eeSummary.wsdl'

WORKING_DIR=$(readlink -f $(dirname $0))
WSDL_DIR="$WORKING_DIR/ee-artifacts/src/main/resources/META-INF/wsdl"

OLD_WSDL="$WSDL_DIR/eeSummary_old.wsdl"
EE_WSDL="$WSDL_DIR/eeSummary.wsdl"


mv $EE_WSDL $OLD_WSDL

CURL_STATUS=$(curl -sk -o $EE_WSDL -w '%{http_code}' $WSDL_LOCATION)

if [ "$CURL_STATUS" != "200" ]; then
  mv $OLD_WSDL $EE_WSDL
  echo "$CURL_STATUS Failed Updating Wsdl"
  exit 1
fi

sed -i 's/maxOccurs="unbounded"/maxOccurs="1000"/g' $EE_WSDL

echo "Wsdl Was Updated"

rm $OLD_WSDL

exit 0
