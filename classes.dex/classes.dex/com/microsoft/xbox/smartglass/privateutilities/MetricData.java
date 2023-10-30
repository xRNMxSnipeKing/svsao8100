package com.microsoft.xbox.smartglass.privateutilities;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class MetricData {
    static String TAG = "MetricData";
    public String ConnectionType;
    public String DeviceType;
    public String Environment;
    public long JoinSessionLatency;
    public String MethodName;
    public long RpsLatency;
    public String Version;
    public long XstsLatency;
    private long startMillis;

    public MetricData(String deviceType, String methodName) {
        this.DeviceType = deviceType;
        this.MethodName = methodName;
    }

    public void Start() {
        this.startMillis = System.currentTimeMillis();
    }

    public long Stop() {
        return System.currentTimeMillis() - this.startMillis;
    }

    public static void PostAllMetrics(final MetricData metricData) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String latency = String.format("%d,%d,%d", new Object[]{Long.valueOf(metricData.RpsLatency), Long.valueOf(metricData.XstsLatency), Long.valueOf(metricData.JoinSessionLatency)});
                    Log.d(MetricData.TAG, String.format("FullURL: %1$s", new Object[]{"http://sgMetrics.cloudapp.net/LatencyData/Create"}));
                    HttpClient client = HttpClient.create();
                    HttpPost post = new HttpPost("http://sgMetrics.cloudapp.net/LatencyData/Create");
                    post.addHeader("Content-Type", "application/x-www-form-urlencoded");
                    post.setEntity(new StringEntity(String.format("methodName=%1$s&deviceType=%2$s&connectionType=%3$s&latency=%4$s&version=%5$s&environment=%6$s", new Object[]{metricData.MethodName, metricData.DeviceType, metricData.ConnectionType, latency, metricData.Version, metricData.Environment}), "UTF-8"));
                    BufferedReader rd = new BufferedReader(new InputStreamReader(client.execute(post).getStream()));
                    StringBuffer body = new StringBuffer();
                    while (true) {
                        String line = rd.readLine();
                        if (line != null) {
                            body.append(line);
                            body.append('\r');
                        } else {
                            Log.d(MetricData.TAG, body.toString());
                            return;
                        }
                    }
                } catch (Exception ex) {
                    Log.e(MetricData.TAG, ex.getMessage());
                }
            }
        }).start();
    }
}
