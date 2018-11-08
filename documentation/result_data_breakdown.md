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