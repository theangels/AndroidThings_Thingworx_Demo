package org.angels.ubilabs.androidthings_thingworx_demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Publisher {
    private static final String TAG = Publisher.class.getSimpleName();

    private Context context;
    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private List<Integer> weatherData;

    private static final long PUBLISH_INTERVAL_MS = TimeUnit.SECONDS.toMillis(4);

    Publisher(Context context, List<Integer> weatherData) throws IOException {
        this.context = context;
        this.weatherData = weatherData;

        mHandlerThread = new HandlerThread("pubsubPublisherThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    void start() {
        mHandler.post(mPublishRunnable);
    }

    void close() {
        mHandler.removeCallbacks(mPublishRunnable);
        mHandlerThread.quitSafely();
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
                Log.e(TAG, "no active network");
                return;
            }

            String result = "";
            URL url;
            try {
                String urlString = BuildConfig.ThingworxIP + "/Thingworx/Things/" + BuildConfig.Things + "/Properties/*?appKey=" + BuildConfig.AppKey;
                url = new URL(urlString);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestProperty("content-type", "application/json");
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setConnectTimeout(5 * 1000);
                //设置请求方式为 PUT
                urlConn.setRequestMethod("PUT");

                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setRequestProperty("Accept", "application/json");

                urlConn.setRequestProperty("Charset", "UTF-8");


                DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
                //写入请求参数
                //这里要注意的是，在构造JSON字符串的时候，实践证明，最好不要使用单引号，而是用“\”进行转义，否则会报错
                // 关于这一点在上面给出的参考文章里面有说明
                String jsonParam = "{" +
                        "\"Temperature\":" + weatherData.get(0) + "," +
                        "\"Humidity\":" + weatherData.get(1) +
                        "}";
                dos.writeBytes(jsonParam);
                dos.flush();
                dos.close();

                if (urlConn.getResponseCode() == 200) {
                    InputStreamReader isr = new InputStreamReader(urlConn.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    String inputLine;
                    while ((inputLine = br.readLine()) != null) {
                        result += inputLine;
                    }
                    isr.close();
                    urlConn.disconnect();
                } else {
                    Log.e(TAG, "连接失败!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Log.e(TAG, result);
                mHandler.postDelayed(mPublishRunnable, PUBLISH_INTERVAL_MS);
            }
        }
    };
}