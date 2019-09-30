# How to use Xavier

## The Basics

The following code sets up your application key in **MainActivity.java** and initiates Xavier.  

**Note:** When you go to production, you must replace **"YOUR APP KEY HERE!"** with the application key we provide you 
upon purchase. If you are just evaluating the product, you can leave this value as is and the application will 
give a warning indicating that you are using an evaluation version of the application.  

```java
// Set your app key
XavierSDK.getInstance().setAppKey("YOUR APP KEY HERE!"); 

// Start Xavier like a normal activity
Intent xavierActivity = new Intent(MainActivity.this, XavierActivity.class);
startActivityForResult(xavierActivity, 1);  
```

## Receiving Results 

The following is an example of handling results from Xavier: 

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // This value must match the value you used in `startActivityForResult()`
    if (requestCode == XAVIER_RESULT){
        // Xavier completed successfully
        if(resultCode == RESULT_OK) {

            Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(XavierActivity.DOCUMENT_INFO, data.getSerializableExtra(XavierActivity.DOCUMENT_INFO));
            intent.putExtra(XavierActivity.DOCUMENT_IMAGE, data.getByteArrayExtra(XavierActivity.DOCUMENT_IMAGE));
            startActivity(intent);

        // User canceled or an error occurred
        } else if(resultCode == RESULT_CANCELED){
            if(data != null) {
                XavierError error = (XavierError) data.getSerializableExtra(XavierActivity.ERROR);
                // Show the error message in a toast
                if (error != null) {
                    Toast.makeText(this, getErrorMessage(error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
```

*Note: see [`getErrorMessage`](#geterrormessagexaviererror-error) below for more info. 

## Understanding the Result Information

On the callback, the results can be retrieved from the following keys: 

1. `XavierActivity.DOCUMENT_IMAGE` - Returns the raw image of the MRZ that was captured.
1.  `XavierActivity.DOCUMENT_INFO` - Returns the hashmap of fields retrieved from the MRZ.
1. `XavierActivity.ERROR` - Returns an error code.

The following are examples of how the data can be extracted.

### XavierActivity.DOCUMENT_IMAGE

**Type:** `byte[]`

The byte array contains the travel document image within the bounds of the bounding box. The following example shows how to convert this into a bitmap.

```java
    byte[] bytes = getIntent().getByteArrayExtra(XavierActivity.DOCUMENT_IMAGE);

    Bitmap bitmap = PhotoUtil.convertByteArrayToBitmap(bytes);
    ImageView iv = findViewById(R.id.imageResult);
```

### XavierActivity.DOCUMENT_INFO

**Type:** `HashMap<XavierField, String>` (serializable extra). 

Contains all the parsed MRZ elements returned in a hashmap.  The keys are enumerated by **XavierField**.  
The following is an example of extracting the GIVEN_NAME from the hashmap.

```java
    // Retrieves the hashmap into the hashmap named **result**:
    HashMap<XavierField, String> result = 
            (HashMap<XavierField, String>) getIntent()
                                           .getSerializableExtra(XavierActivity.DOCUMENT_INFO);

    TextView givenName = findViewById(R.id.given_name);
    givenName.setText(result.get(XavierField.GIVEN_NAME));
```

#### Enum: XavierField

XavierField enumerates all the possible keys in the result hashmap retrieved from `XavierActivity.DOCUMENT_INFO`.

| Value | Description |
| ----- | ----------- |
| COMPOSITE_CHECK_DIGIT | The check digit over the document number, birth datem expiration date, optional data, and their check digits |
| COUNTRY_CITIZEN | The code of the country the traveler is a citizen of. Also known as nationality |
| COUNTRY_ISSUE | The issuing country code for the document. |
| DATE_BIRTH | The date of birth of the traveler. This is returned in **YYMMDD** format |
| DATE_BIRTH_CHECK_DIGIT | The check digit for the date of birth |
| DATE_EXPIRATION |  The date of expiration of the document. This is returned in **YYMMDD** format |
| DATE_EXPIRATION_CHECK_DIGIT | The checkdigit for the expiration date of the document. |
| DOCUMENT_NUMBER | The document number of the traveler. |
| DOCUMENT_NUMBER_CHECK_DIGIT | The check digit for the document number |
| DOCUMENT_TYPE | The document type. |
| GIVEN_NAME | The given or first name of the traveler. |
| OPTIONAL_DATA |  (optional field) The optional data for two or three line MRZs. |
| OPTIONAL_DATA_2 |  (optional field) The optional data for three line MRZs. |
| RAW_MRZ | The unparsed MRZ read by Xavier |
| SEX | The sex of the traveler. |
| STATE_ISSUE | The the issuing state code for Enhanced Driver Licneses. |
| SURNAME | The surname, last name, or family name of the traveler |
| OPTIONAL_DATA_CHECK_DIGIT |  (optional field) The check digit for the optional data |

See [ResultsActivity.java](../xavier-demo/app/src/main/java/com/blacksharktech/xavier/ResultsActivity.java) for an in-depth example of handling the result data.


### `XavierActivity.ERROR`

Type: **XavierError** (enum/serializable extra)

The following errors are classified as **system errors** typically caused by a programming error.  They are not 
expected to be seen by the user.

| Value | Description |
| ----- | ----------- |
| UNKNOWN | This is our generic/default error. See the developer console for more information. |
| LICENSE_INVALID | This happens when the app's license is invalid. One of a few things could cause this: 1) Your license key is misspelled, 2) Your license has expired, or 3) Your license's status has not been verified and you aren't connected to the internet |
| PERMISSIONS |  This happens when the user hasn't accepted the required permissions. The next time they attempt to use the app, they'll be asked again. The android dialog makes this clear. |
| PACKAGE_NAME_NOT_FOUND | This occurs when the package's name isn't found. This should never reach the user. It's caused by an error in the package build process. |
|  CAMERA_ORIENTATION | This error occurs whenever the user rapidly changes the camera from portrait to landscape and vice versa. While in production we recommend (silently) relaunching the XavierActivity whenever this happens. In testing, this will tell you if your device is changing orientation at unsafe speeds. If you see this error and that's not happening, there is a problem with your software or the hardware that determines your device's orientation.|


The following errors are caused by a problem accessing the devices camera. These errors should be displayed back to the user so they have an opportunity to correct the problem.

| Value | Description |
| ----- | ----------- |
| CAMERA_DISABLED | The device has disabled the camera. While the error can't provide specifics, it's almost certainly under the user's control. |
| CAMERA_DISCONNECTED | If your device's camera hardware is broken or (in the case of external cameras) disconnected, Xavier will report this error. |
| CAMERA_IN_USE | In this case, the camera is currently being used by another app and the device does not support this. |
| CAMERA_MAX_IN_USE | Some devices have a limit on how many camera instances are allowed at a time. This error is for letting the user know that they've hit that limit. |
| CAMERA_GENERIC | This error happens when the camera is "in the error state," but the device isn't able to determine which of the above states best describes the situation. Like the above, though, it's generally a hardware-related issue. |

## getErrorMessage(XavierError error)

When the XavierActivity finishes and sends an error code, our demo app handles it by toasting a corresponding string. See [Receiving Results](#receiving-results) for context.
```java
private String getErrorMessage(XavierError error) {
    switch (error) {
        case CAMERA_DISABLED:
            return getString(R.string.camErrorDisabled); // Camera error: camera is disabled by device.
        case CAMERA_DISCONNECTED:
            return getString(R.string.camErrorDisconnected); // Camera error: camera is disconnected from device.
        case CAMERA_IN_USE:
            return getString(R.string.camErrorInUse); // Camera error: camera is already in use.
        case CAMERA_MAX_IN_USE:
            return getString(R.string.camErrorMaxInUse); // Camera error: too many camera instances open.
        case CAMERA_GENERIC:
            return getString(R.string.camErrorDefault); // Error: check camera status.
        case CAMERA_ORIENTATION:
            return getString(R.string.camErrorOrientation); // Too many orientation changes!
        case LICENSE_INVALID:
            return getString(R.string.invalidLicense); // This license is not valid for this device.
        case PERMISSIONS:
            return getString(R.string.permissionsError); // Permissions not accepted.
        case PACKAGE_NAME_NOT_FOUND:
            return getString(R.string.packageNotFound); // Package not found.
        default:
            return getString(R.string.defaultError); // Unknown error.
    }
}
```
You should filter errors and supply your own messages (use R.string.___ for localization reasons) much like in the above example. The strings in our strings.xml file are shown in comments and should **not** be hard-coded into your equivalent method.

### Console Messages

Xavier provide the information of the scanned MRZ on a per-field basis. The information is printed out onto the console as a regular string. The main purpose for this data is to provide the developers, who will be using the SDK, with detailed information on the scanned MRZ (valid or invalid). 

The data will include the scanned MRZ lines in their raw forms, the name of the fields from the list of `XavierField` noted above and their values (raw and corrected).

Below is an example of how the information will look in the console:

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

Additional data related to the scan will also be printed out. This additional information will only be printed out when a valid MRZ is scanned:

* Time spent scanning: The time the scanner spent actively searching for MRZ (in ms).
* Number of candidates: The number of potential MRZ found. This is for both valid and invalid MRZ.
* Number of scans: The number of scans Xavier took before finding the valid MRZ.

```
I/Xavier: Ended scanner at Wed Sep 25 15:23:17 EDT 2019
    Time spent scanning: 7270 ms
    Number of candidates: 1
    Number of scans: 14
```

## Further Exploration

The above example is a very simple example to get you up and running.  Take a look at our [demo application](../xavier-demo) to get a sense of a more complete implementation.   You can see a more complete version of the code above in [MainActivity.java](../xavier-demo/app/src/main/java/com/blacksharktech/xavier/MainActivity.java).

