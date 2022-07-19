package com.blacksharktech.xavier;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.widget.Button;
import android.widget.Toast;

import com.blacksharktech.xavierlib.CameraInterface;
import com.blacksharktech.xavierlib.BaseCameraManager;
import com.blacksharktech.xavierlib.Customization;
import com.blacksharktech.xavierlib.XavierActivity;
import com.blacksharktech.xavierlib.XavierError;
import com.blacksharktech.xavierlib.XavierSDK;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int XAVIER_RESULT = 1234;

    private Customization customization;
    private BaseCameraManager baseCameraManager;
    private CameraManager cameraManager;
    private String cameraId;
    private CameraDevice cameraDevice;
    private Size previewSize;
    private CameraCaptureSession cameraCaptureSession;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest captureRequest;
    public static final String ERROR = "ERROR";

    private static final String[] ALL_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA
    };

    private static final int PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Need to request permissions when using an external camera
        ActivityCompat.requestPermissions(this, ALL_PERMISSIONS, PERMISSIONS_REQUEST_CAMERA);

        initCustomXavierUI();
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        baseCameraManager = new BaseCameraManager(new CameraInterface() {
            @Override
            public void openCamera() {
                openBackgroundThread();
                setUpCamera();
                openCameraHelper();
            }

            @Override
            public void closeCamera() {
                closeCameraHelper();
                closeBackgroundThread();
            }

            @Override
            public void setSurface(SurfaceTexture surface) {
                setSurfaceHelper(surface);
            }

            @Override
            public Size getOutputSize() {
                return previewSize;
            }
        });

        Button button = findViewById(R.id.startDemoButton);
        button.setOnClickListener(v -> {
            Intent xavierActivity = new Intent(MainActivity.this, XavierActivity.class);

            XavierSDK.getInstance().setAppKey("$2a$12$NxGfKYhw8TuhXGTLGnvwD.C9RN799n3WgEHlZ2XqTEYwb65zuubLe");
            XavierSDK.getInstance().setCustomization(customization);
            XavierSDK.getInstance().setBaseCameraManager(baseCameraManager);

            startActivityForResult(xavierActivity, XAVIER_RESULT);
        });
    }

    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            MainActivity.this.cameraDevice = cameraDevice;
            baseCameraManager.isOpened();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            baseCameraManager.isDisconnected();
            MainActivity.this.cameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            baseCameraManager.hasError();
            MainActivity.this.cameraDevice = null;
        }
    };

    private void setSurfaceHelper(SurfaceTexture previewSurface) {
        try {
            captureRequestBuilder = MainActivity.this.cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface surface = new Surface(previewSurface);
            captureRequestBuilder.addTarget(surface);

            MainActivity.this.cameraDevice.createCaptureSession(Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (MainActivity.this.cameraDevice == null) {
                                Log.i("Xavier", "cameraDevice is null...");
                                return;
                            }

                            try {
                                captureRequest = captureRequestBuilder.build();
                                MainActivity.this.cameraCaptureSession = cameraCaptureSession;
                                MainActivity.this.cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                                baseCameraManager.hasError(e.getMessage());
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            baseCameraManager.hasError(e.getMessage());
        }
    }

    // Initializes the camera
    private void setUpCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_BACK) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    Size[] sizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                    previewSize = chooseOptimalSize(sizes);

                    this.cameraId = cameraId;

                    Log.i("Xavier", "Setup camera...");
                    Log.i("Xavier", cameraId);
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            baseCameraManager.hasError(e.getMessage());
        }
    }

    private void openCameraHelper() {
        try {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                try{
                    cameraManager.openCamera(cameraId, stateCallback, backgroundHandler);
                    Log.i("Xavier", "Opened camera...");
                }
                catch (IllegalArgumentException e) {
                    Intent intent = new Intent();
                    intent.putExtra(ERROR, XavierError.CAMERA_DISABLED);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            baseCameraManager.hasError(e.getMessage());
        }
    }

    private void closeCameraHelper() {

        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
            Log.i("Xavier", "Closed camera device...");
        }
    }

    public void initCustomXavierUI() {

        customization = new Customization();

        customization.flashOffButtonColor = Color.LTGRAY;
        customization.flashOnButtonColor = Color.WHITE;

        // More customization options are available!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == XAVIER_RESULT){
            if(resultCode == RESULT_OK) {

                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra(XavierActivity.DOCUMENT_INFO, data.getSerializableExtra(XavierActivity.DOCUMENT_INFO));
                intent.putExtra(XavierActivity.DOCUMENT_IMAGE, data.getByteArrayExtra(XavierActivity.DOCUMENT_IMAGE));
                startActivity(intent);

            } else if(resultCode == RESULT_CANCELED){
                if(data != null) {
                    Log.d("Xavier", "RESULT_CANCELED Data: " + data.getSerializableExtra(XavierActivity.ERROR));
                    XavierError error = (XavierError) data.getSerializableExtra(XavierActivity.ERROR);
                    String errorMessage = (String) data.getSerializableExtra(XavierActivity.ERROR_MESSAGE);

                    if (error != null) {
                        Toast.makeText(this, getErrorMessage(error), Toast.LENGTH_SHORT).show();
                        if (errorMessage != null) {
                            Log.i("Xavier", errorMessage);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_about){
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private String getErrorMessage(XavierError error) {
        switch (error) {
            case CAMERA_DISABLED:
                return getString(R.string.camErrorDisabled);
            case CAMERA_DISCONNECTED:
                return getString(R.string.camErrorDisconnected);
            case CAMERA_IN_USE:
                return getString(R.string.camErrorInUse);
            case CAMERA_MAX_IN_USE:
                return getString(R.string.camErrorMaxInUse);
            case CAMERA_GENERIC:
                return getString(R.string.camErrorDefault);
            case EXTERNAL_CAMERA_DISCONNECTED:
                return getString(R.string.extCamErrorDisconnected);
            case EXTERNAL_CAMERA_GENERIC:
                return getString(R.string.extCamErrorDefault);
            case EXTERNAL_CAMERA_NOT_CONNECTED:
                return getString(R.string.extCamErrorNotConnected);
            case LICENSE_INVALID:
                return getString(R.string.invalidLicense);
            case PERMISSIONS:
                return getString(R.string.permissionsError);
            case PACKAGE_NAME_NOT_FOUND:
                return getString(R.string.packageNotFound);
            default:
                return getString(R.string.defaultError);
        }
    }

    private Size chooseOptimalSize(Size[] outputSizes) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        double preferredRatio = height / (double) width;
        Size currentOptimalSize = outputSizes[0]; //set current to the first element
        double currentOptimalRatio = currentOptimalSize.getWidth() / (double) currentOptimalSize.getHeight();
        for (Size currentSize : outputSizes) {
            double currentRatio = currentSize.getWidth() / (double) currentSize.getHeight();
            //check if the ratio difference is smaller
            if (Math.abs(preferredRatio - currentRatio) < Math.abs(preferredRatio - currentOptimalRatio)) {
                currentOptimalSize = currentSize;
                currentOptimalRatio = currentRatio;
            }
        }
        return currentOptimalSize;
    }
}