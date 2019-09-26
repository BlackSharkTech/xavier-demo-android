# How to use Xavier

The following code is a simplified way of starting and getting results from Xavier.

`MainActivity.java` provides a slightly more complex example.

## Start Xavier

```java
// Set your app key
XavierSDK.getInstance().setAppKey("YOUR APP KEY HERE!"); 

// Start Xavier like a normal activity
Intent xavierActivity = new Intent(MainActivity.this, XavierActivity.class);
startActivityForResult(xavierActivity, 1);  
```

**Make sure you replace "YOUR APP KEY HERE" with your app key above!**

## Receive results from Xavier
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1){  // this value must match the value you used in `startActivityForResult()`
        if(resultCode == RESULT_OK) {

            // Xavier completed successfully - unpack data
            
            // List of all parsed MRZ fields
            HashMap<XavierField, String> mrzFields = (HashMap<XavierField, String>) data.getSerializableExtra(XavierActivity.DOCUMENT_INFO);
            
            // document photo (converted to bitmap)
            byte[] bytes = data.getByteArrayExtra(XavierActivity.DOCUMENT_IMAGE);
            Bitmap bitmap = PhotoUtil.convertByteArrayToBitmap(bytes);
            
        } else if(resultCode == RESULT_CANCELED){
        
            // Xavier did not complete successfully (user cancelled, error occurred, etc)
           
            // In this example, we show the error in a Toast, if one occurred
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

### Intent Extras

#### `XavierActivity.DOCUMENT_IMAGE`

Type: byte array.

The image taken of the travel document (the image within the bounds of the bounding box). Can be converted into a bitmap (see example above).

#### `XavierActivity.DOCUMENT_INFO`

Type: `HashMap<XavierField, String>` (serializable extra). 

A map of all parsed MRZ fields. See [Result Data Breakdown](./result_data_breakdown.md) for a list of all available fields. 

#### `XavierActivity.ERROR`

Type: XavierError (enum/serializable extra)

This enum covers known error states (exceptions or invalid data).
 - UNKNOWN: This is our generic/default error. See the developer console for more information.
 - LICENSE_INVALID: This happens when the app's license is invalid. One of a few things could cause this:
    - Your license key is misspelled
    - Your license has expired
    - Your license's status has not been verified and you aren't connected to the internet
 - PERMISSIONS: This happens when the user hasn't accepted the required permissions. The next time they attempt to use the app, they'll be asked again. The android dialog makes this clear.
 - PACKAGE_NAME_NOT_FOUND: This occurs when the package's name isn't found. This should never reach the user. It's caused by an error in the package build process.
 - CAMERA_ORIENTATION: This error occurs whenever the user rapidly changes the camera from portrait to landscape and vice versa. While in production we recommend (silently) relaunching the XavierActivity whenever this happens. In testing, this will tell you if your device is changing orientation at unsafe speeds. If you see this error and that's not happening, there is a problem with your software or the hardware that determines your device's orientation.

The following errors all come from an inability to access the device camera. These are useful to your users, so you need to display a message to match them.
 
 - CAMERA_DISABLED: The device has disabled the camera. While the error can't provide specifics, it's almost certainly under the user's control.
 - CAMERA_DISCONNECTED: If your device's camera hardware is broken or (in the case of external cameras) disconnected, Xavier will report this error.
 - CAMERA_IN_USE: In this case, the camera is currently being used by another app and the device does not support this.
 - CAMERA_MAX_IN_USE: Some devices have a limit on how many camera instances are allowed at a time. This error is for letting the user know that they've hit that limit.
 - CAMERA_GENERIC: This error happens when the camera is "in the error state," but the device isn't able to determine which of the above states best describes the situation. Like the above, though, it's generally a hardware-related issue.

In our demo app, we've supplied messages for most of these errors and put them in a toast whenever they occur. While the first section's messages aren't intended to reach the user, you will inevitably encounter some of them. We recommend using these toasts or similar dialogs for all errors, at least in development mode.