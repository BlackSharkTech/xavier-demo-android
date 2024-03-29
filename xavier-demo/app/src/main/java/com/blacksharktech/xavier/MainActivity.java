package com.blacksharktech.xavier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.blacksharktech.xavierlib.Customization;
import com.blacksharktech.xavierlib.XavierActivity;
import com.blacksharktech.xavierlib.XavierError;
import com.blacksharktech.xavierlib.XavierSDK;

public class MainActivity extends AppCompatActivity {

    private static final int XAVIER_RESULT = 1234;

    private Customization customization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCustomXavierUI();

        Button button = findViewById(R.id.startDemoButton);
        button.setOnClickListener(v -> {
            Intent xavierActivity = new Intent(MainActivity.this, XavierActivity.class);

            XavierSDK.getInstance().setAppKey("$2a$12$NxGfKYhw8TuhXGTLGnvwD.C9RN799n3WgEHlZ2XqTEYwb65zuubLe");
            XavierSDK.getInstance().setCustomization(customization);

            startActivityForResult(xavierActivity, XAVIER_RESULT);
        });
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
}