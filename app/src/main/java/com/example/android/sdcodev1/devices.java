package com.example.android.sdcodev1;

import android.annotation.SuppressLint;
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
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sdcodev1.AES.ECDH;
import com.example.android.sdcodev1.AES.Util;
import com.example.android.sdcodev1.AES.org.spongycastle.jcajce.provider.symmetric.AES_IVAN;
import com.example.android.sdcodev1.Utilities.utils;

import org.spongycastle.jcajce.provider.symmetric.AES;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.UUID;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import static com.example.android.sdcodev1.AES.ECDH.doECDH;
import static com.example.android.sdcodev1.AES.ECDH.savePrivateKey;
import static com.example.android.sdcodev1.AES.ECDH.savePublicKey;
import static com.example.android.sdcodev1.AES.Util.byteArrayToString;
import static com.example.android.sdcodev1.AES.Util.bytesToHex;
import static com.example.android.sdcodev1.AES.Util.toHEX;

public class devices extends AppCompatActivity {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // Bluetooth and Views
    //ConexionSQLiteHelper connectionDataBase;
    ConexionSQLiteHelper connectionDB = new ConexionSQLiteHelper(this,"db_Keys",null,1);

    // variables for the bluetooth and threads
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

    // Handler
    @SuppressLint("HandlerLeak")
    private final Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg){
            String message = (String)msg.obj;
            // initialize textView
            TextView out2 = findViewById(R.id.out2);
            out2.append(message);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        out = findViewById(R.id.out);
    }

    @Override
    public void onStart() {

        super.onStart();
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


        // -------------------------------------- Key Generation ------------------


/////////////////////////EC Diffie-Hellman//////////////////////////////
            Security.addProvider(new BouncyCastleProvider());
//Security.insertProviderAt(arg0, arg1)
        KeyPairGenerator kpgen = null;
        try {
            kpgen = KeyPairGenerator.getInstance("ECDH", "SC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            kpgen.initialize(new ECGenParameterSpec("secp384r1"), new SecureRandom());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        KeyPair pairA = kpgen.generateKeyPair();
        KeyPair pairB = kpgen.generateKeyPair();

        //User A
        byte [] dataPrvA = new byte[0];
        byte[] dataPubB = new byte[0];
        try {
            dataPrvA = savePrivateKey(pairA.getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte [] dataPubA = new byte[0];
        try {
            dataPubA = savePublicKey(pairA.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            byte [] dataPrvB = savePrivateKey(pairB.getPrivate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dataPubB = savePublicKey(pairB.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        AES_IVAN aes= new AES_IVAN();
        String data="00112233445566778899aabbccddeeff";
        String k="000102030405060708090a0b0c0d0e0f";
        aes.setKey(k);
        String encrypted=aes.Encrypt(data);
        //String hex= Util.toHEX(encrypted.getBytes()).replace(" ","");
        //System.out.println("Encrypted Message: "+hex);

        String macA=aes.getMAC();
        //macA = macA + "\n";
        startDB();

////////////////////////////Preforms the ECDH key sharing/////////
            String keyA ="";
            String keyB;

        // --------------------------------------------------------------   this part is to send communication ------------------------------------------------
        try {
            // output and input stream object created here
            outStream = btSocket.getOutputStream();

        } catch (IOException e) {
            AlertBox("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }


        // -------------- TEST  to get public key from database-----------------
        /* Retrieve values from the database*/
        //connectionDataBase = new ConexionSQLiteHelper(getApplicationContext(),"db_Keys",null,1);
        String messageDB = getPublicKey();

        // **************************************** Sending and Receiving Data ********************************************
        //-------------------------------------- this part id to send communication --------------------------------------
        
        //boolean flag = true;
        boolean flag=true;
        boolean flagIn = true;
        String getNewMac = "";
        String publicKey = Arrays.toString(dataPubA) + "\n";
        String m = "hi \n";
        byte[] msgBuffer = new byte[1024];

        while(flagIn) {

            msgBuffer = m.getBytes();

            dataPubA = publicKey.getBytes();

//        if(flag == false) {
            try {
                outStream.write(msgBuffer);
                outStream.flush();

            } catch (IOException e) {
                String msg = "In onResume() and an exception occurred during write: " + e.getMessage(); // application breaks here
                if (address.equals("00:00:00:00:00:00"))
                    msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
                msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
                AlertBox("Fatal Error", msg);
            }
        //}
//        else {
//            try {
//                outStream.write(msgBuffer);
//                outStream.flush();
//                flag = false;
//            } catch (IOException e) {
//                String msg = "In onResume() and an exception occurred during write: " + e.getMessage(); // application breaks here
//                if (address.equals("00:00:00:00:00:00"))
//                    msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
//                msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
//                AlertBox("Fatal Error", msg);
//            }
//        }
        // -------------------------------------------------------------   this part is to receive communication   --------------------------------------------
        //ReadingThread myReadingThread = new ReadingThread();
        //myReadingThread.start();

        byte[] inmessage = new byte[1024];
        int MessageFrom ;

        try {
            tmpIn= btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
               //If Android app doesn't have workstation public key
            try {
                MessageFrom = tmpIn.read(inmessage);
                String text = new String(inmessage, 0, MessageFrom - 1);
                macA=text;
                m =macA; //text;
                        //preformKeyShare();
                m = m + "\n";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } // while loop
        //*********************************************************************************************************************************************
    }

    @Override
    public void onResume() {
        super.onResume();
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

        int numberKey = 4;
        String puKey = "Test with public key";
        String priKey = "Test 2";
        String sharedKey = "Test 4";

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
        //values.put(utils.TIME_STAMP_FIELD,"datetime()");

        // check this statement
        Long resultID = db.insert(utils.TABLE_KEY,utils.ID_FIELD,values);

        Toast.makeText(getApplicationContext(),"ID Registry: "+resultID,Toast.LENGTH_SHORT).show();

        // close the DB to avoid data corruption
        db.close();

    }
    //************************** SETTERS FOR DATABASE *********************************
    private void setPublicKey(String pukey){
        int numberKey = 1;
        String puKey = "Test";
        String targetID = "4";

        // create the object of the DB that will be used to write/read to DB
        SQLiteDatabase db = connectionDB.getWritableDatabase();

        // Object that will format the data to be inputed in the DB
        ContentValues values = new ContentValues();

        // Insert data into the DATA BASE
        values.put(utils.PUBLIC_KEY_FIELD,pukey);

        // check this statement
        db.update(utils.TABLE_KEY,values,utils.ID_FIELD+"="+targetID,null);
        //Long resultID = db.insert(utils.TABLE_KEY,utils.ID_FIELD,values);

        Toast.makeText(getApplicationContext(),"Updating public key registry: ",Toast.LENGTH_SHORT).show();

        // close the DB to avoid data corruption
        db.close();
    }
    private void setPrivateKey(String prikey){

        String targetID = "4";

        // create the object of the DB that will be used to write/read to DB
        SQLiteDatabase db = connectionDB.getWritableDatabase();

        // Object that will format the data to be inputed in the DB
        ContentValues values = new ContentValues();

        // Insert data into the DATA BASE
        values.put(utils.PRIVATE_KEY_FIELD,prikey);

        // check this statement
        db.update(utils.TABLE_KEY,values,null,null);
        //Long resultID = db.insert(utils.TABLE_KEY,utils.ID_FIELD,values);

        Toast.makeText(getApplicationContext(),"Updating public key registry: ",Toast.LENGTH_SHORT).show();

        // close the DB to avoid data corruption
        db.close();
    }

    //************************** GETTERS FOR DATABASE *********************************
    //                           GET PUBLIC KEY
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
    //                             GET PRIVATE KEY
    private String getPrivateKey() {

        SQLiteDatabase db = connectionDB.getReadableDatabase();
        // Value to be return from query
        String privateKey="";
        // organize the columns of the table
        String Columns[]={utils.ID_FIELD,utils.PUBLIC_KEY_FIELD,utils.PRIVATE_KEY_FIELD,utils.SHARED_KEY_FIELD};

        try{
            Cursor cursor=db.query(utils.TABLE_KEY,Columns,null,null,null,null,null);

            int privateKeyInt;
            privateKeyInt = cursor.getColumnIndex(utils.PRIVATE_KEY_FIELD);
            cursor.moveToLast();
            privateKey = cursor.getString(privateKeyInt);

            // close the DB to avoid data corruption
            db.close();
            cursor.close();
            return privateKey;
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Document doesnt exits",Toast.LENGTH_LONG).show();
            return "no record";
        }

    }
    //                            GET SHARED KEY
    private String getSharedKey() {

        SQLiteDatabase db = connectionDB.getReadableDatabase();
        // Value to be return from query
        String sharedKey="";
        // organize the columns of the table
        String Columns[]={utils.ID_FIELD,utils.PUBLIC_KEY_FIELD,utils.PRIVATE_KEY_FIELD,utils.SHARED_KEY_FIELD};

        try{
            Cursor cursor=db.query(utils.TABLE_KEY,Columns,null,null,null,null,null);

            int sharedKeyInt;
            sharedKeyInt = cursor.getColumnIndex(utils.SHARED_KEY_FIELD);
            cursor.moveToFirst();
            sharedKey = cursor.getString(sharedKeyInt);

            // close the DB to avoid data corruption
            db.close();
            cursor.close();
            return sharedKey;
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Document doesnt exits",Toast.LENGTH_LONG).show();
            return "no record";
        }

    }
    // --------------------------- Support functions for AES -----------------
    public static String Check(String m1, String m2){
        String c;
        if(m1.equals(m2)){
            c="YES";
        }
        else c="NO";
        return c;
    }
    public void generateKeys() throws Exception{

        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        KeyPairGenerator kpgen = KeyPairGenerator.getInstance("ECDH", "SC");
         //breaks at this line
        kpgen.initialize(new ECGenParameterSpec("prime192v1"), new SecureRandom());
        KeyPair pairA = kpgen.generateKeyPair();

        //User A
//        byte [] dataPrvA = ECDH.savePrivateKey(pairA.getPrivate());
//        byte [] dataPubA = ECDH.savePublicKey(pairA.getPublic());
//
//        // print public and private keys
//        String privKey = Util.bytesToHex(dataPrvA);
//        out.append("\n Hello from keys \n");
//        System.out.println("gel");
//        System.out.println("UserA Pub: " + Util.bytesToHex(dataPubA));

    }
   /* public void preformKeyShare(byte[]dataPrvA,byte[] dataPubB) throws Exception {

        String keyA=doECDH("UserA SharedKey: ", dataPrvA, dataPubB);
        AES_IVAN aes = new AES_IVAN();             // init AES encrypted class
        String data = "00112233445566778899aabbccddeeff";
        //out.append("Original text : " + data);
        byte[] in = new byte[data.length()];
        //User A
        aes.setKey(keyA);  // choose password
        String encrypted = aes.Encrypt(data);
        String hex = toHEX(encrypted.getBytes()).replace(" ", "");
        //out.append("Encrypted text UserA: " + hex);

        //Store MAC code
        String mac =aes.getMAC() + "\n";
        setPublicKey(mac);

    }*/

    //}
    // --------------------------- Thread ------------------------------------
    class  ReadingThread extends Thread{
        private final InputStream mmInStream;
        public ReadingThread(){
            out.append("\n   in Thread   \n");
            InputStream tmpIn = null;
            try {
                tmpIn = btSocket.getInputStream();
            } catch (IOException e) {     }
                mmInStream = tmpIn;
            }

        public void run(){
            // initialize the handler to send messages to main thread
            String message = "hello from run \n";
            Message msg = Message.obtain();
            out.append("\n   in runnable method   \n");
            do {
                byte[] inmessage = new byte[1024];
                int MessageFrom;
                try {
                    MessageFrom = mmInStream.read(inmessage);
                    String text = new String(inmessage, 0, MessageFrom - 1);
                    message = text;
                    out.append("\n" + text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                msg.obj = message;
                msg.setTarget(handler);
                msg.sendToTarget();
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (mmInStream != null);
            out.append("\n   socket has failed   \n");
        }
    }



}