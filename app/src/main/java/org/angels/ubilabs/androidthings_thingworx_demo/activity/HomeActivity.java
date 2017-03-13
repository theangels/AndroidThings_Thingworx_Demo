package org.angels.ubilabs.androidthings_thingworx_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import org.angels.ubilabs.androidthings_thingworx_demo.R;
import org.angels.ubilabs.androidthings_thingworx_demo.control.ChartManager;
import org.angels.ubilabs.androidthings_thingworx_demo.control.I2CManager;
import org.angels.ubilabs.androidthings_thingworx_demo.control.PublishManager;
import org.angels.ubilabs.androidthings_thingworx_demo.model.WeatherData;

import java.io.IOException;

import lecho.lib.hellocharts.view.LineChartView;


public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    // I2C Device Name
    private static final String I2C_DEVICE_NAME = "I2C1";
    // I2C Slave Address
    private static final int I2C_ADDRESS = 8;

    private I2cDevice i2cDevice;

    private I2CManager i2CManager;

    private PublishManager publishManager;

    private ChartManager chartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        WeatherData weatherData = new WeatherData(20);

        // Attempt to access the I2C device
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            i2cDevice = manager.openI2cDevice(I2C_DEVICE_NAME, I2C_ADDRESS);

            i2CManager = new I2CManager(weatherData, i2cDevice, I2C_ADDRESS);
            i2CManager.start();

            publishManager = new PublishManager(this, weatherData);
            publishManager.start();

            LineChartView lineChartView = (LineChartView) findViewById(R.id.chart);
            chartManager = new ChartManager(weatherData, lineChartView);
            chartManager.start();

        } catch (IOException e) {
            Log.w(TAG, "Unable to access UART device", e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (i2CManager != null) {
            i2CManager.close();
        }

        if (publishManager != null) {
            publishManager.close();
        }

        if (chartManager != null) {
            chartManager.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (i2cDevice != null) {
            try {
                i2cDevice.close();
                i2cDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close I2C device", e);
            }
        }


        if (i2CManager != null) {
            i2CManager.close();
            i2CManager = null;
        }

        if (chartManager != null) {
            chartManager.close();
            chartManager = null;
        }
    }
}