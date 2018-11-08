# Customization 

Customize Xavier to fit the theme of your app. You can change the colors, text and icons to make Xavier's look and feel match your app.

### Example
```java
// 1. Initialize Customization object
Customization customization = new Customization();

// 2. Customize!
customization.boundingBoxSearchingColor = Color.RED;
customization.flashOffButtonColor = Color.LTGRAY;
customization.flashOnButtonColor = Color.WHITE;

// 3. Set customization
XavierSDK.getInstance().setCustomization(customization);

// 4. Start Xavier
XavierSDK.getInstance().setAppKey("YOUR APP KEY HERE!"); // TODO update this with YOUR app key

Intent xavierActivity = new Intent(MainActivity.this, XavierActivity.class);
startActivityForResult(xavierActivity, XAVIER_RESULT);
```

See `MainActivity.java` for a real example of how to customize your Xavier experience.

## Bounding Box

The bounding box is the rectangle in the camera view that the user puts their document in.

#### `public int boundingBoxSearchingColor`

This defines the color of the box while Xavier is searching for a valid MRZ. The default is `Color.WHITE`. 

#### `public int boundingBoxFoundColor`

This defines the color of the box while Xavier has found a valid MRZ. The default is `Color.GREEN`.

## X (close) Button

The close button is the button at the top left of the screen. Clicking this button will quit Xavier.

#### `public int closeButtonColor`

This defines the color of the close button. The default is `Color.WHITE`.

#### `public boolean closeButtonEnabled`

This defines whether the close button is enabled or not. If not enabled, the button is not visible and the only way to exit Xavier is via the hardware back button. The default is `true`.

## Flash Button

The flash button is button at the top right of the screen. Clicking this button toggles the flashlight/torch.

#### `public int flashOnButtonColor`

This defines the color of the flashlight icon when the flashlight is ON. The default is `Color.YELLOW`.

#### `public int flashOnButtonImage`

This defines the icon for the flashlight when the flashlight is ON. The default is `R.drawable.ic_flash_on` which is from Material Design. 

#### `public int flashOffButtonColor`

This defines the color of the flashlight icon when the flashlight is OFF. The default is `Color.WHITE`.

#### `public int flashOffButtonImage`

This defines the icon for the flashlight when the flashlight is OFF. The default is `R.drawable.ic_flash_off` which is from Material Design.

#### `public boolean flashButtonEnabled`

This defines whether the flash button is enabled or not. If not enabled, the button is not visible and the flashlight cannot be enabled. The default is `true`.

## Instruction Text

The instruction text is the text under the bounding box.

#### `public boolean instructionTextEnabled`

This defines whether the instructions text is enabled or not. This text is only visible in portrait mode. If not enabled, there is no text under the bounding box. The default is `true`.

#### `public String instructionText`

This defines the text for the text under the bounding box. The default is `Position the document in the box above.`.
    
#### `public int instructionTextColor`

This defines the color of the instruction text. The default is `Color.WHITE`.

#### `public Typeface instructionTextFont`

This defines the font for the instruction text. The default is `Typeface.DEFAULT`.

## Camera Negative Space Background

The camera negative space background is the space surrounding the bounding box.

#### `public boolean cameraNegativeSpaceBackgroundEnabled`

This defines whether the camera negative space background is enabled or not. If not enabled, the space surrounding the bounding box is completely transparent. The default is `true`.

#### `public int cameraNegativeSpaceBackgroundColor`

This defines the color (and opacity) for the camera negative space background. The default is `Color.parseColor("#99000000")`.

