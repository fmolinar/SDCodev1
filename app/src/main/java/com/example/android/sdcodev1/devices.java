package com.example.android.sdcodev1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;




public class devices extends AppCompatActivity {
    TextView out;
    private static final int REQUEST_ENABLE_BT= 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket  = null;
    private OutputStream outStream = null; // to be changed by textView

    // WELL known Java Server Side Parameters Universally unique identifier (UUID)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
   // Insert your server's MAC address
    // THIS ONE HAS TO BE CORRECT, OTHERWISE IT WILL CRASH THE APPLICATION
    private static String address = "BC:A8:A6:B4:1A:DA";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        out = (TextView) findViewById(R.id.out);
        
        out.setText("\n ... In on Create()...");
        
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBTState();
    }
    @Override
    public void onStart() {

        super.onStart();
        out.setText("\n ...In onStart()...");
    }

    @Override
    public void onResume() {
        super.onResume();
        out.setText(address); // display the target MAC address

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // initialize the phone bluetooth socket
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            AlertBox("Fatal Error","In onResume() and socket create failed"+e.getMessage());
        }
        btAdapter.cancelDiscovery();

        // establish the connection
        try {
            btSocket.connect();
            out.setText("\n...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                AlertBox("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        out.setText("\n...Sending message to server...");

        //create a data stream to talk to the server
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            AlertBox("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }

        // create message to be send
        String  message = "Hello world message\n";
        // encode the message for transmission
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            AlertBox("Fatal Error", msg);
        }


    }
    @Override
    public void onPause() {
        super.onPause();

        out.setText("\n...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                AlertBox("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            AlertBox("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        out.setText("\n...In onStop()...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        out.setText("\n...In onDestroy()...");
    }
    // ------------------------- SUPPORT FUNCTIONS --------------------------

    // Check for Bluetooth support and then check to make sure it is turned on
    private void CheckBTState() {
        if(btAdapter == null)
        {
            AlertBox("Fatal Error","Bluetooth is NOT supported in this device");
        }
        else {
            //Prompt user to turn on Bluetooth
            Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void AlertBox( String title, String message ){
        new AlertDialog.Builder(this)
                .setTitle( title )
                .setMessage( message + " Press OK to exit." )
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).show();
    }


}
