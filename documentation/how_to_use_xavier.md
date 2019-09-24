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

A map of all parsed MRZ fields. See [Result Data Breakdown](https://github.com/BlackSharkTech/xavier-demo-android/blob/master/documentation/result_data_breakdown.md) for a list of all available fields. 

#### `XavierActivity.ERROR`

Type: XavierError (enum/serializable extra)

This enum covers known errors states (exceptions or invalid data). These are its values:
 - UNKNOWN: This is our generic/default error, which comes from truly unexpected behavior. None of these should reach the user.
 - LICENSE_INVALID: This happens when the app's license is invalid. Sometimes this is because of some issue with an internet connection, but if it happens with any regularity it's likely on your end.
 - PERMISSIONS: This happens when the user hasn't accepted the required permissions. The next time they attempt to use the app, they'll be asked again.
 - PACKAGE_NAME_NOT_FOUND: Happens when the package's name isn't found. Again, none of these should reach the user. If you're seeing this error, something is wrong with how you're building your app.
 - CAMERA_ORIENTATION: This error occurs whenever the user rapidly changes the camera from portrait to landscape and vice versa. While in production we recommend (silently) relaunching the activity whenever this happens, in testing this will tell you if your usage or application is changing orientation at unsafe speeds.
 - CAMERA_GENERIC: The following errors all come from an inability to access the device camera.
 - CAMERA_DISABLED: The device has (somehow) disabled the camera. We don't know how, but it's almost certainly under the user's control. We recommend giving them some sort of feedback (toast, dialog, etc) so that they can fix it.
 - CAMERA_DISCONNECTED: Rare, but also likely the user's fault. Either their device is broken or they've disconnected an external camera while using the app. Please tell them not to do that.
 - CAMERA_IN_USE: In this case, the camera is currently being used by another app and the device doesn't support it. Not much you can do about this other than tell the user to stop split-screening your app with another camera-using application (or however else this happens).
 - CAMERA_MAX_IN_USE: Some devices have a limit on how many usages of the camera are allowed at a time. This error is for letting the user know that they've hit that limit.

In our demo app, we've supplied messages for most of these errors and put them in a toast whenever they occur. We recommend that you not always display messages related to these errors in production. Some (especially those related to the camera) will need fixes performed by the user, but others exist strictly to inform your development team.