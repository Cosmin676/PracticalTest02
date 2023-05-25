package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;
    private Button getButton = null;
    private Button putButton = null;
    private Button connectButton = null;

    // Client widgets
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText keyEditText = null;
    private EditText valueEditText = null;
    //    private Spinner informationTypeSpinner = null;
    private Button getWeatherForecastButton = null;
    private TextView weatherForecastTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private PutButtonClickListener putButtonClickListener = new PutButtonClickListener();
    private class PutButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String value = valueEditText.getText().toString();
            String key = keyEditText.getText().toString();
            if (key == null || key.isEmpty()){
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), key, value, 1);
            clientThread.start();
        }
    }


    private GetButtonClickListener getButtonClickListener = new GetButtonClickListener();
    private class GetButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String key = keyEditText.getText().toString();
            String value = valueEditText.getText().toString();
            if (key == null || key.isEmpty()){
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), key, value, 1);
            clientThread.start();
        }
    }

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);

        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        getButton = (Button)findViewById(R.id.get_button);
        getButton.setOnClickListener(getButtonClickListener);

        putButton = (Button)findViewById(R.id.put_button);
        putButton.setOnClickListener(putButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);

        keyEditText = (EditText)findViewById(R.id.key_edit_text);
        valueEditText = (EditText)findViewById(R.id.value_edit_text);

        weatherForecastTextView = (TextView)findViewById(R.id.weather_forecast_text_view);
    }

    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}