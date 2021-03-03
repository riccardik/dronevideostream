package com.dji.videostreamdecodingsample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.android.CameraBridgeViewBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ConnectionActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();


    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mTextModelAvailable;
    private Button mBtnOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        initUI();

        // Register the broadcast receiver for receiving the device connection's changes.
        IntentFilter filter = new IntentFilter();
        filter.addAction(VideoDecodingApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        updateTitleBar();

    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void initUI() {

       /* mTextConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mTextModelAvailable = (TextView) findViewById(R.id.text_connection_status);*/
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(this);
        mBtnOpen.setEnabled(true);

    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshSDKRelativeUI();
        }
    };

    private void updateTitleBar() {
        boolean ret = false;
        BaseProduct product = VideoDecodingApplication.getProductInstance();
        if (product != null) {
            if(product.isConnected()) {
                //The product is connected
                showToast(VideoDecodingApplication.getProductInstance().getModel() + " Conectado");
                ret = true;
            } else {
                if(product instanceof Aircraft) {
                    Aircraft aircraft = (Aircraft)product;
                    if(aircraft.getRemoteController() != null && aircraft.getRemoteController().isConnected()) {
                        // The product is not connected, but the remote controller is connected
                        showToast("only RC Connected");
                        ret = true;
                    }
                }
            }
        }

        if(!ret) {
            // The product or the remote controller are not connected.
            showToast("Disconnected");
        }
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ConnectionActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateVersion() {
        String version = null;
        if(VideoDecodingApplication.getProductInstance() != null) {
            version = VideoDecodingApplication.getProductInstance().getFirmwarePackageVersion();
        }

       /* if(version == null) {
            mTextModelAvailable.setText("N/A"); //Firmware version:
        } else {
            mTextModelAvailable.setText(version); //"Firmware version: " +
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_open: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    private void refreshSDKRelativeUI() {

        BaseProduct mProduct = VideoDecodingApplication.getProductInstance();
        Log.v(TAG, "refreshSDKRelativeUI");

        if (null != mProduct && mProduct.isConnected()) {
            Log.v(TAG, "refreshSDK: True");
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
            //mTextConnectionStatus.setText("Status: " + str + " conectado");
            updateVersion();

           /** if (null != mProduct.getModel()) {
                mTextProduct.setText("" + mProduct.getModel().getDisplayName());
            } else {
                mTextProduct.setText(R.string.product_information);
            }*/
        } else {
            Log.v(TAG, "refreshSDK: False");
            mBtnOpen.setEnabled(true);

           // mTextProduct.setText(R.string.product_information);
         //   mTextConnectionStatus.setText(R.string.connection_loose);
        }
    }

}
