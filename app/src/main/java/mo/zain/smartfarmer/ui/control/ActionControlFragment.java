package mo.zain.smartfarmer.ui.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.UUID;

import mo.zain.smartfarmer.R;


public class ActionControlFragment extends Fragment {

    // Widgets
    public Button On, Off,btnDis;
    public TextView dataTW;
    public String address = null;

    // Bluetooth
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBTConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Thread workerThread,ManualThread;
    byte[] generalBuffer;
    int generalBufferPosition;
    volatile boolean stopWorker;

    Double ForStop;
    ToggleButton aSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_action_control, container, false);
        address = getArguments().getString("device_address").toString();
        btnDis = view.findViewById(R.id.dis_btn);
        dataTW = view.findViewById(R.id.TW_data);
        On = view.findViewById(R.id.on_btn);
        aSwitch=view.findViewById(R.id.switch1);
        Off =view.findViewById(R.id.off_btn);

        new ConnectBT().execute(); // Connection class


        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        On.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                turnOnLed();      //method to turn on
            }
        });

        Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                turnOffLed();   //method to turn off
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    beginListenForData();
                } else {

                    beginManualListenForData();
                    //beginListenMainForData();
                }
            }
        });

        return view;
    }

    private void turnOffLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream()
                        .write("0".getBytes());
            }
            catch (IOException e)
            {
            }
        }
    }
    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream()
                        .write("1".getBytes());
            }
            catch (IOException e)
            {
            }
        }
    }

    // Disconnection
    private void disconnect() {
        if (btSocket != null) { // If bluetooth socket is taken then disconnect
            try {
                btSocket.close(); // Close bluetooth connection
            }
            catch (IOException e) {
                toast("Error Closing Socket");
            }
        }
        //Back
    }

    private void toast(String s) {
        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
    }

    public void beginListenForData() {
        final Handler handler = new Handler(); // Interacts between this thread and UI thread
        final byte delimiter = 35; // ASCII code for (#) end of transmission

        stopWorker = false;
        generalBufferPosition = 0;
        generalBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {


                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {

                        int bytesAvailable = btSocket.getInputStream().available(); // Received bytes by bluetooth module

                        if (bytesAvailable > 0) {
                            byte[] packet = new byte[bytesAvailable];
                            btSocket.getInputStream().read(packet);

                            for (int i=0; i<bytesAvailable; i++) {
                                byte b = packet[i];
                                if (b == delimiter) { // If found a # print on screen
                                    byte[] arrivedBytes = new byte[generalBufferPosition];
                                    System.arraycopy(generalBuffer, 0, arrivedBytes, 0, arrivedBytes.length);
                                    final String data = new String(arrivedBytes, "US-ASCII"); // Decode from bytes to string
                                    generalBufferPosition = 0;


                                    try{
                                        ForStop=Double.parseDouble(data);
                                        if (ForStop<=700.00)
                                        {
                                            turnOffLed();
                                        }else
                                        {
                                            turnOnLed();
                                        }

                                    } catch(NumberFormatException ex){
                                    }
                                    handler.post(new Runnable() {
                                        public void run() {
                                            try {
                                                dataTW.setText(String.valueOf(ForStop));
                                            }catch (Exception e)
                                            {

                                            }
                                        }
                                    });

                                }
                                else { // If there is no # add bytes to buffer
                                    generalBuffer[generalBufferPosition++] = b;
                                }
                            }
                        }

                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        if (ManualThread!=null)
        {
            ManualThread.interrupt();
        }

        workerThread.start();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> { // UI thread

        private boolean connectionSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getContext(), "Connecting...", "Please wait!"); // Connection loading dialog
        }

        @Override
        protected Void doInBackground(Void... devices) { // Connect with bluetooth socket

            try {

                if (btSocket == null || !isBTConnected) { // If socket is not taken or device not connected
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address); // Connect to the chosen MAC address
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID); // This connection is not secure (mitm attacks)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery(); // Discovery process is heavy
                    btSocket.connect();
                }
            }
            catch (IOException e) {
                connectionSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) { // After doInBackground
            super.onPostExecute(result);

            if (!connectionSuccess) {
                toast("Connection Failed. Try again.");
                //finish();
            }
            else {
                toast("Connected.");
                beginManualListenForData();

                isBTConnected = true;
            }
            progress.dismiss();
        }
    }

    public void beginManualListenForData() {
        final Handler handler = new Handler(); // Interacts between this thread and UI thread
        final byte delimiter = 35; // ASCII code for (#) end of transmission

        stopWorker = false;
        generalBufferPosition = 0;
        generalBuffer = new byte[1024];
        ManualThread = new Thread(new Runnable() {
            public void run() {


                while (!Thread.currentThread().isInterrupted()
                        && !stopWorker) {
                    try {


                        int bytesAvailable = btSocket.getInputStream().available(); // Received bytes by bluetooth module

                        if (bytesAvailable > 0) {
                            byte[] packet = new byte[bytesAvailable];
                            btSocket.getInputStream().read(packet);

                            for (int i=0; i<bytesAvailable; i++) {
                                byte b = packet[i];
                                if (b == delimiter) { // If found a # print on screen
                                    byte[] arrivedBytes = new byte[generalBufferPosition];
                                    System.arraycopy(generalBuffer, 0, arrivedBytes, 0, arrivedBytes.length);
                                    final String data = new String(arrivedBytes, "US-ASCII"); // Decode from bytes to string
                                    generalBufferPosition = 0;
                                    try{
                                        ForStop=Double.parseDouble(data);
                                        turnOffLed();

                                    } catch(NumberFormatException ex){
                                    }

                                    handler.post(new Runnable() {
                                        public void run() {

                                            dataTW.setText(String.valueOf(ForStop)); // Print on screen

                                        }
                                    });
                                }
                                else { // If there is no # add bytes to buffer
                                    generalBuffer[generalBufferPosition++] = b;
                                }
                            }
                        }

                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });
        if (workerThread!=null)
        {
            workerThread.interrupt();
        }
        ManualThread.start();
    }

}
