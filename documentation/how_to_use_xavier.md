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

Type: String. 

Contains an error message, if a known error occurred.
