# Adding Xavier to your App

1. Create a directory `libs` inside your project's `app` folder
2. Download the [library AAR file](../downloads/v2.0/xavierlib.aar)
3. Put the AAR file into the `libs` directory you just created
4. In your `build.gradle` at the root of your project, add the following:

```gradle
allprojects {
    repositories {
        ...

        flatDir{ dirs 'libs/' }   // add this line so gradle knows where to find the library
    }
}
```

5. In your `app/build.gradle` file, add the following:

```gradle
    dependencies {
        ...
        
        // Xavier library
        implementation(name:'xavierlib', ext:'aar')

        // Xavier dependencies
        implementation 'com.rmtheis:tess-two:9.0.0'
        implementation 'jp.co.cyberagent.android.gpuimage:gpuimage-library:1.3.0'
        implementation 'com.squareup.okhttp3:okhttp:3.11.0'
        implementation 'com.google.code.gson:gson:2.8.5'
    }
```