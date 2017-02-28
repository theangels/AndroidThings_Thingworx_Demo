package org.angels.ubilabs.androidthings_thingworx_demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends Activity {
    private static final String TAG = HomeActivity.class.getSimpleName();

    // I2C Device Name
    private static final String I2C_DEVICE_NAME = "I2C1";
    // I2C Slave Address
    private static final int I2C_ADDRESS = 8;

    private I2cDevice i2cDevice;

    private I2CManager i2CManager;

    private Publisher publisher;

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

        List<Integer> weatherData = new ArrayList<>(Arrays.asList(0, 0));
//        List<Integer> temperature = new ArrayList<>();
//        List<Integer> humidity = new ArrayList<>();


        // Attempt to access the I2C device
        try {
            PeripheralManagerService manager = new PeripheralManagerService();
            i2cDevice = manager.openI2cDevice(I2C_DEVICE_NAME, I2C_ADDRESS);

            i2CManager = new I2CManager(weatherData, i2cDevice, I2C_ADDRESS);
            i2CManager.start();

            publisher = new Publisher(this, weatherData);
            publisher.start();

//            LineChartView lineChartView = (LineChartView) findViewById(R.id.chart);
//            chartManager = new ChartManager(temperature, humidity, lineChartView);
//            chartManager.start();

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

        if (publisher != null) {
            publisher.close();
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