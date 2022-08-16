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
                String errorMessage = (String) data.getSerializableExtra(XavierActivity.ERROR_MESSAGE);
                // Show the error message in a toast
                if (error != null) {
                    Toast.makeText(this, getErrorMessage(error), Toast.LENGTH_SHORT).show();
                    if (errorMessage != null) { //if a detailed error message is returned, log the message
                        Log.i("XAVIER", errorMessage);
                    }
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
1. `XavierActivity.ERROR_MESSAGE` - Returns a detailed string error message if the error code is CAMERA_GENERIC or EXTERNAL_CAMERA_GENERIC.

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
| COMPOSITE_CHECK_DIGIT | The check digit over the document number, birth date expiration date, optional data, and their check digits |
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
| STATE_ISSUE | The issuing state code for Enhanced Driver Licenses. |
| SURNAME | The surname, last name, or family name of the traveler |
| OPTIONAL_DATA_CHECK_DIGIT |  (optional field) The check digit for the optional data |

See [ResultsActivity.java](../xavier-demo/app/src/main/java/com/blacksharktech/xavier/ResultsActivity.java) for an in-depth example of handling the result data.


### Enum: `XavierActivity.ERROR`

Type: **XavierError** (enum/serializable extra)

The following errors are classified as **system errors** typically caused by a programming error.  They are not 
expected to be seen by the user.

| Value | Description |
| ----- | ----------- |
| LICENSE_INVALID | This happens when the app's license is invalid. One of a few things could cause this: 1) Your license key is misspelled, 2) Your license has expired, or 3) Your license's status has not been verified and you aren't connected to the internet |
| PERMISSIONS |  This happens when the user hasn't accepted the required permissions. The next time they attempt to use the app, they'll be asked again. The android dialog makes this clear. |
| PACKAGE_NAME_NOT_FOUND | This occurs when the package's name isn't found. This should never reach the user. It's caused by an error in the package build process. |



The following errors are caused by a problem accessing the device's camera. These errors should be displayed back to the user so they have an opportunity to correct the problem.

| Value | Description |
| ----- | ----------- |
| CAMERA_DISABLED | The device has disabled the camera. While the error can't provide specifics, it's almost certainly under the user's control. |
| CAMERA_DISCONNECTED | If your device's camera hardware is broken or (in the case of external cameras) disconnected, Xavier will report this error. |
| CAMERA_IN_USE | In this case, the camera is currently being used by another app and the device does not support this. |
| CAMERA_MAX_IN_USE | Some devices have a limit on how many camera instances are allowed at a time. This error is for letting the user know that they've hit that limit. |
| CAMERA_GENERIC | This error happens when the camera is "in the error state," but the device isn't able to determine which of the above states best describes the situation. Like the above, though, it's generally a hardware-related issue. When available, a specific error message will be passed back to the parent activity.|
| EXTERNAL_CAMERA_DISCONNECTED | Occurs when Xavier has been notified that the external camera has been disconnected. The user will need to connect the camera and restart the application.|
| EXTERNAL_CAMERA_GENERIC | Occurs when the external camera has returned an error. The developer will have the option to pass in a more specific string error message for when these errors occur.|
| EXTERNAL_CAMERA_NOT_CONNECTED | Occurs when the external camera is expected but the baseCameraManager for the external camera was not passed in via the XavierSDK. The developer should check that they configured and passed in the baseCameraManager properly.|

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
        case EXTERNAL_CAMERA_DISCONNECTED:
                return getString(R.string.extCamErrorDisconnected); //External Camera Error: Camera has been disconnected
        case EXTERNAL_CAMERA_GENERIC:
            return getString(R.string.extCamErrorDefault); //External Camera Error: check camera status
        case EXTERNAL_CAMERA_NOT_CONNECTED:
            return getString(R.string.extCamErrorNotConnected);//External Camera Error: Camera could not be recognized
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

Xavier provides the information of the scanned MRZ on a per-field basis. The information is printed out onto the console as a regular string. The main purpose for this data is to provide the developers, who will be using the SDK, with detailed information on the scanned MRZ (valid or invalid). 

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

## **Optional**: Using an External Camera with Xavier

In most cases, you will be using the phone's camera so Xavier will manage the camera internally. However, if there is a special USB camera you would like to use, follow these steps. Otherwise, no additional steps are necessary - you can skip to [Further Exploration](#further-exploration).

**Please Note:** When setting up the camera in your activity, you will need to set up a background thread using a HandlerThread approach. A complete example of using external camera with Xavier can be found in [external-camera-demo](https://github.com/BlackSharkTech/xavier-demo-android/blob/v2.1.4-External/xavier-demo/app/src/main/java/com/blacksharktech/xavier/MainActivity.java). 
<br/><br/>
### **BaseCameraManager Overview**
<br/>
If you want to use an external usb camera, you will need to manage the camera in your parent activity and set up a `BaseCameraManager` so Xavier can communicate with the camera. Xavier will use the BaseCameraManager to tell the parent activity when to open, close, and set the camera's target surface. In addition, Xavier will use the BaseCameraManager to retrieve the output size for the camera stream.

The BaseCameraManager class has two member variables, one `CameraInterface` and one `CameraListener`. **You will only need to create the CameraInterface when creating a new BaseCameraManager**. More information about both member variables can be found below.

#### **Member Variables Overview** 
- `CameraInterface`
    - Xavier will use this interface to tell the parent activity to open the camera, close the camera, set the camera's surface and get the preferred output size. 
    - Parent activity must provide a CameraInterface when creating a BaseCameraManager.

- `CameraListener` 
    - Used to detect and act on changes to the camera state including: opened, disconnected, and throwing errors.
    - Parent activity will not need to create or interact directly with the CameraListener. Xavier will create and attach the listener to the BaseCameraManager.

#### **Using BaseCameraManager Methods**
To indicate there have been changes to the camera's state (opened, disconnected, has error), you will need to call the following BaseCameraManager methods: 
- `isOpened()` 
- `isDisconnected()`
- `hasError()`
- `hasError(String error)`

Ideally, these methods should be called in the camera's StateCallback and anywhere in your code where the camera may produce an error. If there is not a camera StateCallback, call these methods whenever you will be notified of a camera state change. More detail about when to call these methods can be found below:

|Method | Reason to call|
|------| ------|
|isOpened()| Camera finished opening and is ready to be used |
|isDisconnected() | Camera has been disconnected |
|hasError() | Camera has produced an error;|
|hasError(String error) | Camera has produced an error; Return an error message|

<br/>

**The next three sections will explain how to create a BaseCameraManager, pass it to Xavier, and notify Xavier of camera state changes**

<br/>

### **1. How to Create a BaseCameraManager**

<br/>

To create a BaseCameraManager class, you will need to create an instance of `CameraInterface`. Override the CameraInterface methods with your own calls/implementation to set up and open the camera, close the camera, set surface, and get the output size. 

<details><summary>Example of Creating a BaseCameraManager</summary> 

```java
    /*
    BaseCameraManager needs to be created in order for the Xavier library
    to properly interact with the external camera.
    */
    baseCameraManager = new BaseCameraManager(new CameraInterface() {
    /**
     * This method should call other methods to open a background
     * thread for the camera, set up the camera, and open the
     * camera.
     */
    @Override
    public void openCamera() {
        openBackgroundThread();
        setUpCamera();
        openCameraHelper();
    }

    /**
     * This method should call other methods to close the camera
     * and the background thread.
     */
    @Override
    public void closeCamera() {
        closeCameraHelper();
        closeBackgroundThread();
    }

    /**
     * This method should call a method to set up the
     * camera's surface texture. This is used to display
     * the camera preview when using Xavier.
     * @param surface The SurfaceTexture from the camera
     */
    @Override
    public void setSurface(SurfaceTexture surface) {
        setSurfaceHelper(surface);
    }

    /**
     * This method returns a resolution for the camera preview
     * which is based on the camera's characteristics. In this example,
     * previewSize is set using chooseOptimalSize, which is called
     * in setUpCamera
     * @return Optimal preview size
     */
    @Override
    public Size getOutputSize() {
        return previewSize;
    }
});

```

</details>

<br/>

### **2. How to Pass a BaseCameraManager To Xavier**

<br/>

Once you have a BaseCameraManager setup, you will need to pass it to Xavier using the XavierSDK.

```java 
    XavierSDK.getInstance().setBaseCameraManager(baseCameraManager);
``` 

By default, Xavier will use the phone's built-in camera if the baseCameraManger is not provided or not accessible. If you do not want Xavier to access the phone's internal camera at all (even if you did not provide baseCameraManager or there was an issue accessing it), Set the `externalCameraOnly` flag to true. Xavier will return an **EXTERNAL_CAMERA_NOT_CONNECTED** error if the baseCameraManager was not provided or there was an error accessing it. 

```java
    XavierSdk.getInstance().hasExternalCameraOnly(true);
``` 
<br/>

### **3. How to Indicate Camera State Changes**

<br/>

Add calls to the baseCameraManager `isOpened()`, `isDisconnected()`, and `hasError(String message)` in your camera device's StateCallback and anywhere an error or disconnection may occur. For more information on when to use these methods reference [Using BaseCameraManager Methods](#using-basecameramanager-methods). 

<details><summary>Example of Calling BaseCameraManager methods</summary> 

```java
/**
 * State callback for the camera. There are methods that need to be
 * called when the camera is opened, disconnected, or has an error
 * that will work with the listener in BaseCameraManager.
 */
private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
    @Override
    public void onOpened(@NonNull CameraDevice cameraDevice) {
        MainActivity.this.cameraDevice = cameraDevice;
        // Need to tell the listener that the camera is opened using isOpened
        baseCameraManager.isOpened();
    }

    @Override
    public void onDisconnected(CameraDevice cameraDevice) {
        cameraDevice.close();
        // Need to tell the listener that the camera disconnected using isDisconnected
        baseCameraManager.isDisconnected();
        MainActivity.this.cameraDevice = null;
    }

    @Override
    public void onError(CameraDevice cameraDevice, int error) {
        cameraDevice.close();
        // Need to tell the listener that the camera is has an error using hasError
        // May also provide a string message using baseCameraManager.hasError(errorMessage)
        baseCameraManager.hasError();
        MainActivity.this.cameraDevice = null;
    }
};

```
</details>

<br/>

### **Wrapping Up**

<br/>

At this point, you should have: 
1. Set up a BaseCameraManager with a CameraInterface to communicate with the camera - [Step 1](#1-how-to-create-a-basecameramanager)
2. Passed a BaseCameraManager to Xavier using the XavierSDK - [Step 2](#2-how-to-pass-a-basecameramanager-to-xavier)
3. Updated your camera statecallback to notify Xavier of camera state changes using the BaseCameraManager methods - [Step 3](#3-how-to-indicate-camera-state-changes)

If that is not the case, please review the steps in [Using An External Camera With Xavier](#optional-using-an-external-camera-with-xavier). Example code is available at [external-camera-demo](https://github.com/BlackSharkTech/xavier-demo-android/blob/v2.1.4-External/xavier-demo/app/src/main/java/com/blacksharktech/xavier/MainActivity.java). 

If you have followed all of the steps, you are ready to start the XavierActivity with your external camera attached. **Happy Scanning!** 

## Further Exploration

The above example is a very simple example to get you up and running.  Take a look at our [demo application](../xavier-demo) to get a sense of a more complete implementation.   You can see a more complete version of the code above in [MainActivity.java](../xavier-demo/app/src/main/java/com/blacksharktech/xavier/MainActivity.java).
