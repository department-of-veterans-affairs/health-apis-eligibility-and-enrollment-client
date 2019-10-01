The samples are used either by the unit tests or were samples obtained somehow (presumably by a live system?):

* eeFault.xml, sample fault not used by unit tests but left as an example.

* eeFaultBlankFault.jks, sample fault used by unit test to test a blank fault string "    ".
NOTE: this file must have extension 'jks' as a silly workaround to prevent the xml formatter from clobbering the blank fault string.

* eeFaultNoFault.xml, sample soap envelope without a fault used by unit test.

* eeFaultNullFault.xml, sample fault used by unit test to test a null fault string.

* getEESummaryResponse.xml, sample soap getEESummaryResponse soap envelope not used by unit tests but left as an example.

* getEeSummaryResponseBody.xml, sample soap getEESummaryResponse message body used by unit test.

* personNotFound.xml, sample fault not used by unit tests but left as an example.
