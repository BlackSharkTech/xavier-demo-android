# Release Notes
## 2.1.0 **Important**: This update will require changes to your code!
* Improved error handling to display user-friendly messages when the application encountered an error.
  * This will break your error handling. We've moved from reporting arrows via a string to reporting errors via an enum. See [MainActivity](../xavier-demo/app/src/main/java/com/blacksharktech/xavier/MainActivity.java) to see how we use the new [XavierError](./using-xavier.md#enum:-xavieractivity.error) enum.
* Allowed error messages to be editable through `strings.xml`.
* Started printing scanned MRZ on a per field basis to the console for debugging purpose.
* Made changes to documentation to improve the provided information and readability.

## 2.1.1
* Fixed a bug with the flash icon where it displays the wrong flash status when the app is resumed from being inactive.
* Added demo app license key

## 2.1.2
* Updated library to use AndroidX, brings compatibility with SDK 30

## 2.1.3
* Brings compatibility with SDK 32

## 2.1.4
* Adds the ability to use an external camera

## 2.1.5b
* Updated MRZ parsing logic to handle more countries

## 2.1.6
* Registers scans
