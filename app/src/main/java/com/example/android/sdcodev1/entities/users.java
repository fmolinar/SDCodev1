package com.example.android.sdcodev1.entities;


// Structure of the table for the SQLite DB

public class users {
    private Integer id;
    private byte publicKey;
    private byte privateKey;
    private byte sharedKey;

    public users(Integer id, byte publicKey, byte privateKey, byte sharedKey) {
        this.id = id;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.sharedKey = sharedKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte publicKey) {
        this.publicKey = publicKey;
    }

    public byte getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte privateKey) {
        this.privateKey = privateKey;
    }

    public byte getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(byte sharedKey) {
        this.sharedKey = sharedKey;
    }
}
