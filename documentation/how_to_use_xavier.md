# How to use Xavier

## Getting Started

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
    if (requestCode == 1){  // this value must match the value you used in `startActivityForResult()`

        // Xavier completed successfully
        if (resultCode == RESULT_OK) {
            
            // List of all parsed MRZ fields
            HashMap<XavierField, String> mrzFields = (HashMap<XavierField, String>) data.getSerializableExtra(XavierActivity.DOCUMENT_INFO);
            
            // document photo (converted to bitmap)
            byte[] bytes = data.getByteArrayExtra(XavierActivity.DOCUMENT_IMAGE);
            Bitmap bitmap = PhotoUtil.convertByteArrayToBitmap(bytes);
            
        // User cancelled or an error occurred
        } else if(resultCode == RESULT_CANCELED){       
           
            // In this example, we show the error in a toast, if one occurred
            if(data != null) {
                String error = data.getStringExtra("ERROR");
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
```

## Further Exploration

The above example is a very simple example to get you up and running.  Take a look at our [demo application](../xavier-demo) to get a sense of a more complete implementation.   You can see a more developed version of the code above in [MainActivity.java](../xavier-demo/app/src/main/java/com/blacksharktech/xavier/MainActivity.java).


## Intent Extras

#### `XavierActivity.DOCUMENT_IMAGE`

Type: byte array.

The image taken of the travel document (the image within the bounds of the bounding box). Can be converted into a bitmap (see example above).

#### `XavierActivity.DOCUMENT_INFO`

Type: `HashMap<XavierField, String>` (serializable extra). 

A map of all parsed MRZ fields. See [Result Data Breakdown](./result_data_breakdown.md) for a list of all available fields. 

#### `XavierActivity.ERROR`

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


