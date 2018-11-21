package com.blacksharktech.xavier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.blacksharktech.xavierlib.Customization;
import com.blacksharktech.xavierlib.XavierActivity;
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

            XavierSDK.getInstance().setAppKey("PUT IN YOUR LICENSE KEY HERE");
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
                    String error = data.getStringExtra(XavierActivity.ERROR);
                    if (error != null) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
}
