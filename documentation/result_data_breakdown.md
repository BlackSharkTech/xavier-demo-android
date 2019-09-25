# Result Data Breakdown

On the callback, the results are returned in the keys `XavierActivity.DOCUMENT_INFO` and `XavierActivity.DOCUMENT_IMAGE`.

`XavierActivity.DOCUMENT_IMAGE` holds the image of the document taking when the valid MRZ was read in a byteArray

```java
    byte[] bytes = getIntent().getByteArrayExtra(XavierActivity.DOCUMENT_IMAGE);
    Bitmap bitmap = PhotoUtil.convertByteArrayToBitmap(bytes);
    ImageView iv = findViewById(R.id.imageResult);
```

`XavierActivity.DOCUMENT_INFO` contains all the parsed MRZ elements returned in `Hashmap<XavierField, String>`

```java
    HashMap<XavierField, String> result = (HashMap<XavierField, String>) getIntent().getSerializableExtra(XavierActivity.DOCUMENT_INFO);
    TextView givenName = findViewById(R.id.given_name);
    givenName.setText(result.get(XavierField.GIVEN_NAME));
```

See `ResultsActivity.java` for more examples.

## XavierField

#### `DOCUMENT_TYPE`

The document type.

#### `COUNTRY_CITIZEN`

The code of the country the traveler is a citizen of. Also known as nationality

#### `GIVEN_NAME`

The given or first name of the traveler.

#### `SURNAME`

The surname, last name, or family name of the traveler

#### `DOCUMENT_NUMBER`

The document number of the traveler.

#### `COUNTRY_ISSUE`

The issuing country code for the document.

#### `DATE_BIRTH`

The date of birth of the traveler. This is returned in **YYMMDD** format

#### `SEX`

The sex of the traveler.

#### `DATE_EXPIRATION` 

The date of expiration of the document. This is returned in **YYMMDD** format

#### `OPTIONAL_DATA` (optional field)

The optional data for two or three line MRZs.

#### `OPTIONAL_DATA_2` (optional field)

The optional data for three line MRZs.

#### `STATE_ISSUE`

The the issuing state code for Enhanced Driver Licneses.

#### `RAW_MRZ`

The unparsed MRZ read by Xavier

#### `DOCUMENT_NUMBER_CHECK_DIGIT`

The check digit for the document number

#### `DATE_BIRTH_CHECK_DIGIT`

The check digit for the date of birth

#### `DATE_EXPIRATION_CHECK_DIGIT`

The checkdigit for the expiration date of the document.

#### `OPTIONAL_DATA_CHECK_DIGIT` (optional field)

The check digit for the optional data

#### `COMPOSITE_CHECK_DIGIT`

The check digit over the document number, birth datem expiration date, optional data, and their check digits

## Console Message

Xavier provide the information of the scanned MRZ on a per-field basis. The information is printed out onto the console as a regular string. The main purpose for this data is to provide the developers, who will be using the SDK, with detailed information on the scanned MRZ (valid or invalid). 

The data will include the scanned MRZ lines in their raw forms, the name of the fields from the list of `XavierField` noted above and their values (raw and corrected).

Below is an example of how the information will look like in the console:

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

Additional data related to the scan will also be printed out. These additional information will only be printed out when a valid MRZ is scanned:

* Time spent scanning: The time the scanner spent actively searching for MRZ (in ms).
* Number of candidates: The number of MRZ found. This is for both valid and invalid MRZ.
* Number of scans: The number of scans Xavier took until the valid MRZ.

```
I/Xavier: Ended scanner at Wed Sep 25 15:23:17 EDT 2019
    Time spent scanning: 7270 ms
    Number of candidates: 1
    Number of scans: 14
```