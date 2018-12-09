package com.example.android.sdcodev1.AES;




import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;

public class ECDH

{

    public static byte [] savePublicKey (PublicKey key) throws Exception
    {
        //return key.getEncoded();
        //String path="C://Users//ivanvasquez//Desktop//SeniorDesign//UserB";
        ECPublicKey eckey = (ECPublicKey)key;
        //System.out.println(eckey.getFormat());
        byte [] Pukey= eckey.getQ().getEncoded(true);

        return Pukey;

    }

    public static PublicKey loadPublicKey (byte [] data) throws Exception
    {
		/*KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
		return kf.generatePublic(new X509EncodedKeySpec(data));*/

        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime192v1");//Change to 128 currently 192
        ECPublicKeySpec pubKey = new ECPublicKeySpec(
                params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "SC");

        //System.out.println("In ECDH class"+kf.generatePublic(pubKey));
        return kf.generatePublic(pubKey);
    }

    public static byte [] savePrivateKey (PrivateKey key) throws Exception
    {
        //return key.getEncoded();

        ECPrivateKey eckey = (ECPrivateKey)key;
        return eckey.getD().toByteArray();
    }

    public static PrivateKey loadPrivateKey (byte [] data) throws Exception
    {
        //KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        //return kf.generatePrivate(new PKCS8EncodedKeySpec(data));

        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("prime192v1");
        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "SC");
        return kf.generatePrivate(prvkey);
    }

    public static String doECDH (String name, byte[] dataPrv, byte[] dataPub) throws Exception
    {
        KeyAgreement KeyAgree = KeyAgreement.getInstance("ECDH", "SC");
        KeyAgree.init(loadPrivateKey(dataPrv));
        KeyAgree.doPhase(loadPublicKey(dataPub), true);
        byte [] secret = KeyAgree.generateSecret();
        String a=Util.bytesToHex(secret);
        System.out.println(name + Util.bytesToHex(secret));
        return a;
    }


}