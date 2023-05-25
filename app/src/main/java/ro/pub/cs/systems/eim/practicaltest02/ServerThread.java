package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ServerThread extends Thread{
    private int port = 0;
    private ServerSocket serverSocket = null;

    private Updating updateing = new Updating();;

    private HashMap<String, String> data = new HashMap<String, String>();

    class Updating extends Thread {


        private String pageSource = "";
        private HttpClient httpClient = new DefaultHttpClient();
        private LocalDateTime now = null;

        @Override
        public void run() {



            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if (now == null) {
                        // first time the server and this thread starts
                        // get api data

                        HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                        HttpResponse httpGetResponse = httpClient.execute(httpGet);
                        HttpEntity httpGetEntity = httpGetResponse.getEntity();
                        if (httpGetEntity != null) {
                            pageSource = EntityUtils.toString(httpGetEntity);

                        }
                        Log.i(Constants.TAG, "getting first rates");

                        if (pageSource == null) {
                            Log.e(Constants.TAG, "[UPDATING THREAD] Error getting the information from the webservice!");
                            return;
                        } else {
                            Log.i(Constants.TAG, pageSource);
                            Document document = Jsoup.parse(pageSource);
                        }

                        now = LocalDateTime.now();
                        Log.i(Constants.TAG, "set now to: " + now);
                    } else {
                        Duration duration = Duration.between(LocalDateTime.now(), now);
//                      Log.i(Constants.TAG, "elseeeee");
                        if (duration.toMillis() > 300) {
                            Log.i(Constants.TAG, "300 passed");
                        }
                    }

                }
            } catch (ClientProtocolException cp) {
                Log.e(Constants.TAG, "[UPDATING THREAD] An exception has occurred: " + cp.getMessage());
                if (Constants.DEBUG) {
                    cp.printStackTrace();
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[UPDATING THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }

        }

        public void stopThread() {
            interrupt();
        }
    }

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public HashMap<String, String> getData() {
        return data;
    }

    @Override
    public void run() {
        updateing.start();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());

                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader == null || printWriter == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                    return;
                }

                String key = bufferedReader.readLine();
                String value = bufferedReader.readLine();
                int mode = Integer.parseInt(bufferedReader.readLine());

                if (mode == 1) {
                    // puts in hashmap
                    data.put(key, value);
                    Log.i(Constants.TAG, "added (key, value) to hashmap: (" + key + " " + value + ")");
                } else if (mode == 0 && data.containsKey(key)) {
                    Log.i(Constants.TAG, "get value from hashmap: (" + key + " " + data.get(value) + ")");
                }
            }
        } catch (ClientProtocolException cp) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + cp.getMessage());
            if (Constants.DEBUG) {
                cp.printStackTrace();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void stopThread() {
        updateing.stopThread();
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
