package org.angels.ubilabs.androidthings_thingworx_demo.control;


import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.android.things.pio.I2cDevice;

import org.angels.ubilabs.androidthings_thingworx_demo.model.WeatherData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class I2CManager {
    private static final String TAG = I2CManager.class.getSimpleName();

    private Handler handler;
    private HandlerThread handlerThread;


    private WeatherData weatherData;
    private I2cDevice device;
    private int startAddress;


    private static final long PUBLISH_INTERVAL_MS = TimeUnit.SECONDS.toMillis(3);

    public I2CManager(WeatherData weatherData, I2cDevice device, int startAddress) throws IOException {
        this.weatherData = weatherData;
        this.device = device;
        this.startAddress = startAddress;

        handlerThread = new HandlerThread("I2CThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public void start() {
        handler.post(publishRunnable);
    }


    public void close() {
        handler.removeCallbacks(publishRunnable);
        handlerThread.quitSafely();
    }

    private Runnable publishRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                readCalibration(device, startAddress);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                handler.postDelayed(publishRunnable, PUBLISH_INTERVAL_MS);
            }
        }
    };

    // Read a register block
    private byte[] readCalibration(I2cDevice device, int startAddress) throws IOException {
        // Read three consecutive register values
        byte[] data = new byte[7];
        device.readRegBuffer(startAddress, data, data.length);
        String dataString = new String(data);
        int ts = dataString.indexOf('T');
        int te = dataString.indexOf('H');
        int he = dataString.indexOf('E');
        if (ts >= 0 && te >= 0 && he >= 0) {
            weatherData.addTemperature(Integer.valueOf(dataString.substring(ts + 1, te)));
            weatherData.addHumidity(Integer.valueOf(dataString.substring(te + 1, he)));
        }
        Log.d(TAG, "Current T: " + weatherData.getCurrentTemperature() + ", H: " + weatherData.getCurrentHumidity());
        return data;
    }
}
