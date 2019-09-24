# Console Message

## Scanned MRZ Information

Xavier Android SDK prints out several messages to the console in debug build for troubleshooting purposes. This includes the information of the scanned MRZ (valid and invalid) information on a per-field basis. Below is an example of how the information will look like in the console:

```
============================
ParsedLine number="0"  length="30"  value="VBUSA99310025<9EA00000002840<<"
ParsedLine number="1"  length="30"  value="7609239M0910123MEXEAC2004285<3"
ParsedLine number="2"  length="30"  value="AVILES<MONTANEZ<<MARIO<JOSE<<<"
============================
ParsedField name="DOCUMENT_NUMBER_CHECK_DIGIT"  value="9"  correctedValue="9"
ParsedField name="DATE_BIRTH"  value="760923"  correctedValue="760923"
ParsedField name="DATE_EXPIRATION"  value="091012"  correctedValue="091012"
ParsedField name="OPTIONAL_DATA_2"  value="EAC2004285<"  correctedValue="EAC2004285<"
ParsedField name="DOCUMENT_TYPE"  value="V"  correctedValue="V"
ParsedField name="SURNAME"  value="AVILES MONTANEZ"  correctedValue="AVILES MONTANEZ"
ParsedField name="OPTIONAL_DATA"  value="EA00000002840<<"  correctedValue="EA00000002840<<"
ParsedField name="GIVEN_NAME"  value="MARIO JOSE"  correctedValue="MARIO JOSE"
ParsedField name="COUNTRY_CITIZEN"  value="MEX"  correctedValue="MEX"
ParsedField name="DATE_EXPIRATION_CHECK_DIGIT"  value="3"  correctedValue="3"
ParsedField name="SEX"  value="M"  correctedValue="M"
ParsedField name="COMPOSITE_CHECK_DIGIT"  value="3"  correctedValue="3"
ParsedField name="COUNTRY_ISSUE"  value="USA"  correctedValue="USA"
ParsedField name="DOCUMENT_SUBTYPE"  value="B"  correctedValue="B"
ParsedField name="DOCUMENT_NUMBER"  value="99310025<"  correctedValue="99310025<"
ParsedField name="DATE_BIRTH_CHECK_DIGIT"  value="9"  correctedValue="9"
============================
```