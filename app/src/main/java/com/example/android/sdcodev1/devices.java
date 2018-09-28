package com.example.android.sdcodev1;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class devices extends AppCompatActivity {

    private static final String TAG = "devices";

    BluetoothAdapter mBluetoothAdapter; // bluetooth adapter
    Button btnEnableDisable_Discoverable; // bluetooth receiver

    // create a broadcastReceiver for action_found
    // Debug purposes
    private final BroadcastReceiver mBroadcastReciver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,mBluetoothAdapter.ERROR);

                switch (state)
                {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"mBroadcastReceiver1: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                // Debug purposes
                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        Button btnONOFF = (Button)findViewById(R.id.btnONOFF); //create Button object
        btnEnableDisable_Discoverable = (Button)findViewById(R.id.btnDiscoverable_on_off);

/*  The BluetoothAdapter lets you perform fundamental Bluetooth tasks, such as initiate device discovery, query a list of bonded (paired) devices*/
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

/* The above snippet creates an instance of View.OnClickListener and wires the listener to the button
using setOnClickListener(View.OnClickListener). As a result, the system executes the code you write in onClick(View) after the user presses the button. */
        btnONOFF.setOnClickListener(new View.OnClickListener() { // View.OnClickListener() Interface definition for a callback to be invoked when a view is clicked.
            @Override
            public void onClick(View v) {
                enableDisableBT();
            }
        });
    }
    public void enableDisableBT(){
        if(mBluetoothAdapter == null){ // device cant use bluetooth
            Log.d(TAG,"enableDisableBT: does not have BT capabilities");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            // debug purposes
            IntentFilter BTintent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciver1, BTintent);
        }
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            // debug purposes
            IntentFilter BTintent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReciver1, BTintent);
        }
    }

    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);
    }
}
