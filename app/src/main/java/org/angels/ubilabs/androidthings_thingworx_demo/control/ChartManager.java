package org.angels.ubilabs.androidthings_thingworx_demo.control;


import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;

import org.angels.ubilabs.androidthings_thingworx_demo.control.I2CManager;
import org.angels.ubilabs.androidthings_thingworx_demo.model.WeatherData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartManager {
    private static final String TAG = I2CManager.class.getSimpleName();

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private WeatherData weatherData;
    private LineChartView lineChartView;

    private static final long PUBLISH_INTERVAL_MS = TimeUnit.SECONDS.toMillis(5);

    public ChartManager(WeatherData weatherData, LineChartView lineChartView) throws IOException {
        this.weatherData = weatherData;
        this.lineChartView = lineChartView;

        mHandlerThread = new HandlerThread("ChartManager");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public void start() {
        mHandler.post(mPublishRunnable);
    }

    public void close() {
        mHandler.removeCallbacks(mPublishRunnable);
        mHandlerThread.quitSafely();
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                refreshData(lineChartView);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mHandler.postDelayed(mPublishRunnable, PUBLISH_INTERVAL_MS);
            }
        }
    };

    // Read a register block
    private void refreshData(LineChartView lineChartView) throws IOException {
        List<Integer> temperatures = weatherData.getTemperatures();
        List<Integer> humidities = weatherData.getHumidities();
        if (temperatures.size() != 0 && humidities.size() != 0) {
            //温度
            int maxT = -1;
            int minT = 1000;
            List<Line> lineList = new ArrayList<>();
            List<PointValue> temperatureValuesList;
            Line temperatureLine;
            temperatureValuesList = new ArrayList<>();
            temperatureLine = new Line(temperatureValuesList).setColor(Color.BLUE).setCubic(true);
            temperatureLine.setHasPoints(false);
            temperatureLine.setHasLines(true);
            for (int i = 0; i < temperatures.size(); i++) {
                int currentT = temperatures.get(i);
                if (maxT < currentT) {
                    maxT = currentT;
                }
                if (minT > currentT) {
                    minT = currentT;
                }
                temperatureValuesList.add(new PointValue(i, currentT));
            }
            lineList.add(temperatureLine);

            //最后显示点
            temperatureValuesList = new ArrayList<>();
            temperatureLine = new Line(temperatureValuesList).setColor(Color.BLACK).setCubic(true);
            temperatureLine.setHasPoints(true);
            temperatureLine.setHasLines(false);
            temperatureLine.setHasLabels(true);
            temperatureValuesList.add(new PointValue(temperatures.size() - 1, temperatures.get(temperatures.size() - 1)));
            lineList.add(temperatureLine);

            //调整线位置
            temperatureValuesList = new ArrayList<>();
            temperatureLine = new Line(temperatureValuesList).setColor(Color.WHITE).setCubic(false);
            temperatureLine.setHasPoints(false);
            temperatureLine.setHasLines(false);
            temperatureValuesList.add(new PointValue(temperatures.size() - 1, minT - 20));
            temperatureValuesList.add(new PointValue(temperatures.size() - 1, maxT + 20));
            lineList.add(temperatureLine);

            //湿度
            int maxH = -1;
            int minH = 1000;
            List<PointValue> humidityValuesList;
            Line humidityLine;
            humidityValuesList = new ArrayList<>();
            humidityLine = new Line(humidityValuesList).setColor(Color.GREEN).setCubic(true);
            humidityLine.setHasPoints(false);
            humidityLine.setHasLines(true);
            for (int i = 0; i < humidities.size(); i++) {
                int currentH = humidities.get(i);
                if (maxH < currentH) {
                    maxH = currentH;
                }
                if (minH > currentH) {
                    minH = currentH;
                }
                humidityValuesList.add(new PointValue(i, currentH));
            }
            lineList.add(humidityLine);

            //最后显示点
            humidityValuesList = new ArrayList<>();
            humidityLine = new Line(humidityValuesList).setColor(Color.BLACK).setCubic(true);
            humidityLine.setHasPoints(true);
            humidityLine.setHasLines(false);
            humidityLine.setHasLabels(true);
            humidityValuesList.add(new PointValue(humidities.size() - 1, humidities.get(humidities.size() - 1)));
            lineList.add(humidityLine);

            //调整线位置
            humidityValuesList = new ArrayList<>();
            humidityLine = new Line(humidityValuesList).setColor(Color.WHITE).setCubic(false);
            humidityLine.setHasPoints(false);
            humidityLine.setHasLines(false);
            humidityValuesList.add(new PointValue(humidities.size() - 1, minH - 20));
            humidityValuesList.add(new PointValue(humidities.size() - 1, maxH + 20));
            lineList.add(humidityLine);

            LineChartData data = new LineChartData();
            data.setLines(lineList);

            //坐标轴L
            Axis axisY1 = new Axis();//Y1轴
            axisY1.setHasLines(true);
            axisY1.setTextColor(Color.BLACK);
            axisY1.setName("Temperature/oC");
            axisY1.setTextColor(Color.BLUE);
            axisY1.setMaxLabelChars(4);
            data.setAxisYLeft(axisY1);

            //坐标轴L
            Axis axisY2 = new Axis();//Y1轴
            axisY2.setHasLines(true);
            axisY2.setTextColor(Color.BLACK);
            axisY2.setName("Humidity/%RH");
            axisY2.setTextColor(Color.GREEN);
            axisY2.setMaxLabelChars(4);
            data.setAxisYRight(axisY2);

            lineChartView.setLineChartData(data);
        }
    }
}
