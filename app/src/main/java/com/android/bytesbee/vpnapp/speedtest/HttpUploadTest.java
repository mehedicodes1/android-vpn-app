package com.android.bytesbee.vpnapp.speedtest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author erdigurbuz
 */
public class HttpUploadTest extends Thread {

    public final String fileURL;
    static int uploadedKByte = 0;
    double uploadElapsedTime = 0;
    boolean finished = false;
    double elapsedTime = 0;
    double finalUploadRate = 0.0;
    long startTime;

    public HttpUploadTest(String fileURL) {
        this.fileURL = fileURL;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd;
        try {
            bd = new BigDecimal(value);
        } catch (Exception ex) {
            return 0.0;
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean isFinished() {
        return finished;
    }

    public double getInstantUploadRate() {
        try {
            BigDecimal bd = new BigDecimal(uploadedKByte);
        } catch (Exception ex) {
            return 0.0;
        }

        if (uploadedKByte >= 0) {
            long now = System.currentTimeMillis();
            elapsedTime = (now - startTime) / 1000.0;
            return round(((uploadedKByte / 1000.0) * 8) / elapsedTime, 2);
        } else {
            return 0.0;
        }
    }

    public double getFinalUploadRate() {
        return round(finalUploadRate, 2);
    }

    @Override
    public void run() {
        try {
            URL url = new URL(fileURL);
            uploadedKByte = 0;
            startTime = System.currentTimeMillis();

            ExecutorService executor = Executors.newFixedThreadPool(4);
            for (int i = 0; i < 4; i++) {
                executor.execute(new HandlerUpload(url));
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }

            long now = System.currentTimeMillis();
            uploadElapsedTime = (now - startTime) / 1000.0;
            finalUploadRate = ((uploadedKByte / 1000.0) * 8) / uploadElapsedTime;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        finished = true;
    }
}

class HandlerUpload extends Thread {

    final URL url;

    public HandlerUpload(URL url) {
        this.url = url;
    }

    public void run() {
        byte[] buffer = new byte[150 * 1024];
        long startTime = System.currentTimeMillis();
        int timeout = 8;

        while (true) {

            try {
                HttpsURLConnection conn;
                Reader inputString = new StringReader(url.toString());
                BufferedReader reader = new BufferedReader(inputString);
                final String strHostName = reader.readLine();
                final String strVerifyHost = strHostName.split("://")[1].split(":")[0];

                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
//                conn.setHostnameVerifier((hostname, session) -> true);
                conn.setHostnameVerifier((hostname, session) -> {
//                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//                        Utils.sout("Hv:: " + url.getHost() + " << " + hostname + " >> " + session.getPeerHost());
//                        return hostname.equalsIgnoreCase("speedtest.netaccessinternet.com")
//                                || hostname.equalsIgnoreCase("ookla.softnetbroadband.com") || hostname.contains(url.getHost())
//                                || hostname.contains(session.getPeerHost());
                    return hostname.equals(strVerifyHost);
                });
                conn.connect();
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                dos.write(buffer, 0, buffer.length);
                dos.flush();

                conn.getResponseCode();

                HttpUploadTest.uploadedKByte += buffer.length / 1024.0;
                long endTime = System.currentTimeMillis();
                double uploadElapsedTime = (endTime - startTime) / 1000.0;
                if (uploadElapsedTime >= timeout) {
                    break;
                }

                dos.close();
                conn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}
