package com.example.android.sdcodev1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sdcodev1.Utilities.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;





public class devices extends AppCompatActivity {

    // Bluetooth and Views
    //ConexionSQLiteHelper connectionDataBase;
    ConexionSQLiteHelper connectionDB = new ConexionSQLiteHelper(this,"db_Keys",null,1);

    //
    TextView out;
    private static final int REQUEST_ENABLE_BT= 1;
    private BluetoothDevice device; // added
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket  = null;
    private OutputStream outStream = null; // to be changed by textView
    private InputStream tmpIn = null; // changed to receive data

    int sdk = Integer.parseInt(Build.VERSION.SDK);

    // WELL known Java Server Side Parameters Universally unique identifier (UUID)
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Insert your server's MAC address
    // THIS ONE HAS TO BE CORRECT, OTHERWISE IT WILL CRASH THE APPLICATION
    private static String address = "BC:A8:A6:B4:1A:DA";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        out = findViewById(R.id.out);
        out.append("\n ... In on Create()...");

    }



    @Override
    public void onStart() {

        super.onStart();
        //startDB();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckBTState();
        out.append("\n ...In onStart()...");

        //BluetoothDevice device = btAdapter.getRemoteDevice(address);
        device = btAdapter.getRemoteDevice(address);

        // initialize the phone bluetooth socket
        // before check the core android sdk and determine type of socket
        if(sdk < 17)
        {
            try {
                btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                AlertBox("Fatal Error","In onResume() and socket create failed"+e.getMessage());
            }
            btAdapter.cancelDiscovery();
        }


        // establish the connection
        try {
            btSocket.connect();
            out.append("\n...Connection established and data link opened...");
        } catch (IOException e) {}
            /*try {
                btSocket.close();
            } catch (IOException e2) {
                AlertBox("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }

        }*/

        out.append("\n...Sending message to server...");

        //create a data stream to talk to the server

        // --------------------------------------------------------------   this part is to send communication ------------------------------------------------
        try {
            // output stream object created here
            outStream = btSocket.getOutputStream();

        } catch (IOException e) {
            AlertBox("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }

        // create message to be send
        String  message = "Hello world message\n";
        // encode the message for transmission

        // -------------- TEST -----------------
        /* Retrieve values from the database*/
        //connectionDataBase = new ConexionSQLiteHelper(getApplicationContext(),"db_Keys",null,1);
        //String messageDB = getPublicKey();
        //out.append(messageDB);
        //--------------------------------------

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

        // -------------------------------------------------------------   this part is to receive communication   --------------------------------------------
        try {
            tmpIn= btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ReadingThread myReadingThread = new ReadingThread();
        //myReadingThread.start();
        byte[] inmessage = new byte[1024];
        int MessageFrom ;

        try {
            tmpIn= btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MessageFrom = tmpIn.read(inmessage);
            String text = new String(inmessage,0,MessageFrom-1);
            out.append("\n" + text);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        out.append(address); // display the target MAC address

       /* try {
            btSocket.connect();
            out.append("\n...Connection established and data link opened...");
        } catch (IOException e) {}*/

    }



    @Override
    public void onPause() {
        super.onPause();

        out.append("\n...In onPause()...");

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
        out.append("\n...In onStop()...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        out.append("\n...In onDestroy()...");
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

    // ------------------------ SUPPORT FUNCTIONS FOR DATABASE ------------------------

    private void startDB() {

        int numberKey = 1;
        String puKey = "Test";
        String priKey = "Test 2";
        String sharedKey = "Test 3";

        /* Create the database SQLite */
        // Create the connection to the DB
        //ConexionSQLiteHelper connectionDB = new ConexionSQLiteHelper(this,"db_Keys",null,1);

        // create the object of the DB that will be used to write/read to DB
        SQLiteDatabase db = connectionDB.getWritableDatabase();

        // Object that will format the data to be inputed in the DB
        ContentValues values = new ContentValues();

        // Insert data into the DATA BASE
        values.put(utils.ID_FIELD,numberKey);
        values.put(utils.PUBLIC_KEY_FIELD,puKey);
        values.put(utils.PRIVATE_KEY_FIELD,priKey);
        values.put(utils.SHARED_KEY_FIELD,sharedKey);

        // check this statement
        Long resultID = db.insert(utils.TABLE_KEY,utils.ID_FIELD,values);

        Toast.makeText(getApplicationContext(),"ID Registry: "+resultID,Toast.LENGTH_SHORT).show();

        // close the DB to avoid data corruption
        db.close();

    }
    private String getPublicKey() {

        SQLiteDatabase db = connectionDB.getReadableDatabase();
        // Value to be return from query
        String publicKey="";
        // organize the columns of the table
        String Columns[]={utils.ID_FIELD,utils.PUBLIC_KEY_FIELD,utils.PRIVATE_KEY_FIELD,utils.SHARED_KEY_FIELD};

        try{
            Cursor cursor=db.query(utils.TABLE_KEY,Columns,null,null,null,null,null);

            int publicKeyInt;
            publicKeyInt = cursor.getColumnIndex(utils.PUBLIC_KEY_FIELD);
            cursor.moveToFirst();
            publicKey = cursor.getString(publicKeyInt);

            // close the DB to avoid data corruption
            db.close();
            cursor.close();
            return publicKey;
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Document doesnt exits",Toast.LENGTH_LONG).show();
            return "no record";
        }




    }
    // --------------------------- Thread ------------------------
    class  ReadingThread extends Thread{
        public ReadingThread(){

        }

        public void run(){
            out.append("\n   IN RUN   \n");
            int bytes;
            byte[] buffer = new byte[1024];
            while (true) {



                try {

                    int c = tmpIn.read();
//
                    if(c != -1)
                    {
                        bytes = tmpIn.read(buffer);            //read bytes from input buffer
                        String readMessage = new String(buffer, 0, bytes);
                        //Send the obtained bytes to the UI Activity via handler
                        out.setText("\n" + readMessage);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //try {
//                    //bytes = tmpIn.read(buffer);            //read bytes from input buffer
//                     int c = tmpIn.read();
////
//                    if(c != -1)
//                     {
//                         bytes = tmpIn.read(buffer);            //read bytes from input buffer
//                         String readMessage = new String(buffer, 0, bytes);
//                         //Send the obtained bytes to the UI Activity via handler
//                         out.setText("\n" + readMessage);
//                     }

//                } catch (IOException e) {
//                    e.printStackTrace();
//                    break;
              //  }
                //out.append("reading buffer\n");
            }
        }
    }



}