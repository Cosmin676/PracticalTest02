package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
    private String address;
    private int port;
    private Socket socket;
    private String value;
    private int mode;

    public ClientThread(String adress, int port, String value, int mode) {
        // mode = 1 -> put
        // mode = 0 -> get
        this.address = adress;
        this.port = port;
        this.value = value;
        this.mode = mode;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }


//            printWriter.println(curency);
//            printWriter.flush();
//            printWriter.flush();
//            String weatherInformation;
//            while ((weatherInformation = bufferedReader.readLine()) != null) {
//                final String finalizedWeateherInformation = weatherInformation;
//                weatherForecastTextView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        weatherForecastTextView.setText(finalizedWeateherInformation);
//                    }
//                });
//            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
