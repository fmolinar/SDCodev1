package com.example.android.sdcodev1.Utilities;

public class utils {

    // Table keys fields constants
    public static final String TABLE_KEY = "key";
    public static final String ID_FIELD = "id";
    public static final String PUBLIC_KEY_FIELD = "publicKey";
    public static final String PRIVATE_KEY_FIELD = "privateKey";
    public static final String SHARED_KEY_FIELD = "sharedKey";
    public static final String TIME_STAMP_FIELD = "timeStamp";

    public static final String CREATE_TABLE_USER="CREATE TABLE "+TABLE_KEY+"("+ID_FIELD+" INTEGER, "+PUBLIC_KEY_FIELD+" TEXT, "+PRIVATE_KEY_FIELD+" TEXT, "+SHARED_KEY_FIELD+" TEXT)";
//    public static final String CREATE_TABLE_USER="CREATE TABLE "+TABLE_KEY+"("+ID_FIELD+" INTEGER, "
//            +PUBLIC_KEY_FIELD+" TEXT, "+PRIVATE_KEY_FIELD+" TEXT, "+SHARED_KEY_FIELD+" TEXT, "
//            +TIME_STAMP_FIELD+" TEXT)";

}
