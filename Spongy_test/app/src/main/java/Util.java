import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class Util {

    //......................................................................
    // utility conversions between byte, short and int arrays

    public static byte[] short2byte (short[] sa) {
        int length = sa.length;
        byte[] ba = new byte[length * 2];
        for (int i = 0, j = 0, k; i < length; ) {
            k = sa[i++];
            ba[j++] = (byte)((k >>> 8) & 0xFF);
            ba[j++] = (byte)( k        & 0xFF);
        }
        return (ba);
    }

    //......................................................................
    public static short[] byte2short (byte[] ba) {
        int length = ba.length;
        short[] sa = new short[length / 2];
        for (int i = 0, j = 0; j < length / 2; ) {
            sa[j++] = (short)(((ba[i++] & 0xFF) <<  8) |
                    ((ba[i++] & 0xFF)      ));
        }
        return (sa);
    }

    //......................................................................
    public static byte[] int2byte (int[] ia) {
        int length = ia.length;
        byte[] ba = new byte[length * 4];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ia[i++];
            ba[j++] = (byte)((k >>>24) & 0xFF);
            ba[j++] = (byte)((k >>>16) & 0xFF);
            ba[j++] = (byte)((k >>> 8) & 0xFF);
            ba[j++] = (byte)( k        & 0xFF);
        }
        return (ba);
    }

    //......................................................................
    public static int[] byte2int (byte[] ba) {
        int length = ba.length;
        int[] ia = new int[length / 4];
        for (int i = 0, j = 0; j < length / 4; ) {
            ia[j++] = (((ba[i++] & 0xFF) << 24) |
                    ((ba[i++] & 0xFF) << 16) |
                    ((ba[i++] & 0xFF) <<  8) |
                    ((ba[i++] & 0xFF)      ));
        }
        return (ia);
    }

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    //......................................................................
    // utility methods (adapted from cryptix.util.core.Hex class)

    /** array mapping hex value (0-15) to corresponding hex digit (0-9a-f). */
    public static final char[] HEX_DIGITS = {
            '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };


    public static String toHEX (byte[] ba) {
        int length = ba.length;
        char[] buf = new char[length * 3];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ba[i++];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[ k        & 0x0F];
            buf[j++] = ' ';
        }
        return new String(buf);
    }


    public static String toHEX (short[] ia) {
        int length = ia.length;
        char[] buf = new char[length * 5];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ia[i++];
            buf[j++] = HEX_DIGITS[(k >>>12) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 8) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[ k        & 0x0F];
            buf[j++] = ' ';
        }
        return new String(buf);
    }

    public static String toHEX (int[] ia) {
        int length = ia.length;
        char[] buf = new char[length * 10];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ia[i++];
            buf[j++] = HEX_DIGITS[(k >>>28) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>24) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>20) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>16) & 0x0F];
            buf[j++] = ' ';
            buf[j++] = HEX_DIGITS[(k >>>12) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 8) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[ k        & 0x0F];
            buf[j++] = ' ';
        }
        return new String(buf);
    }


    public static String toHEX1 (byte b) {
        char[] buf = new char[2];
        int j = 0;
        buf[j++] = HEX_DIGITS[(b >>> 4) & 0x0F];
        buf[j++] = HEX_DIGITS[ b        & 0x0F];
        return new String(buf);
    }


    public static String toHEX1 (byte[] ba) {
        int length = ba.length;
        char[] buf = new char[length * 2];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ba[i++];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[ k        & 0x0F];
        }
        return new String(buf);
    }


    public static String toHEX1 (short[] ia) {
        int length = ia.length;
        char[] buf = new char[length * 4];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ia[i++];
            buf[j++] = HEX_DIGITS[(k >>>12) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 8) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[ k        & 0x0F];
        }
        return new String(buf);
    }


    public static String toHEX1 (int i) {
        char[] buf = new char[8];
        int j = 0;
        buf[j++] = HEX_DIGITS[(i >>>28) & 0x0F];
        buf[j++] = HEX_DIGITS[(i >>>24) & 0x0F];
        buf[j++] = HEX_DIGITS[(i >>>20) & 0x0F];
        buf[j++] = HEX_DIGITS[(i >>>16) & 0x0F];
        buf[j++] = HEX_DIGITS[(i >>>12) & 0x0F];
        buf[j++] = HEX_DIGITS[(i >>> 8) & 0x0F];
        buf[j++] = HEX_DIGITS[(i >>> 4) & 0x0F];
        buf[j++] = HEX_DIGITS[ i        & 0x0F];
        return new String(buf);
    }


    public static String toHEX1 (int[] ia) {
        int length = ia.length;
        char[] buf = new char[length * 8];
        for (int i = 0, j = 0, k; i < length; ) {
            k = ia[i++];
            buf[j++] = HEX_DIGITS[(k >>>28) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>24) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>20) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>16) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>>12) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 8) & 0x0F];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[ k        & 0x0F];
        }
        return new String(buf);
    }


    //......................................................................

    public static byte[] hex2byte(String hex) {
        int len = hex.length();
        byte[] buf = new byte[((len + 1) / 2)];

        int i = 0, j = 0;
        if ((len % 2) == 1)
            buf[j++] = (byte) hexDigit(hex.charAt(i++));

        while (i < len) {
            buf[j++] = (byte) ((hexDigit(hex.charAt(i++)) << 4) |
                    hexDigit(hex.charAt(i++)));
        }
        return buf;
    }


    public static String byteArrayToString(byte[] data) {
        String res = "";
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<data.length; i++) {
            int n = (int) data[i];
            if(n<0) n += 256;
            sb.append((char) n);
        }
        res = sb.toString();
        return res;
    }

    public static byte[] stringToByteArray(String s){
        byte[] temp = new byte[s.length()];
        for(int i=0;i<s.length();i++){
            temp[i] = (byte) s.charAt(i);
        }
        return temp;
    }

    public static String intArrayToString(int[]t){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<t.length;i++){
            sb.append((char)t[i]);
        }
        return sb.toString();
    }

    //......................................................................

    public static boolean isHex(String hex) {
        int len = hex.length();
        int i = 0;
        char ch;

        while (i < len) {
            ch = hex.charAt(i++);
            if (! ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') ||
                    (ch >= 'a' && ch <= 'f'))) return false;
        }
        return true;
    }


    public static int hexDigit(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;

        return(0);	// any other char is treated as 0
    }


    public static byte[] readContentIntoByteArray(File file)
    {
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
            for (int i = 0; i < bFile.length; i++)
            {
                System.out.print((char) bFile[i]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Utils: "+bFile);
        return bFile;
    }


    ////////////////////////////////////Wrting and Reading to files for byte array////////////////////
//Referenced http://www.javapractices.com/topic/TopicAction.do?Id=245 to write and read////////////
    public static byte[] read(String inputFileName){
        System.out.println("Reading in binary file named : " + inputFileName);
        File file = new File(inputFileName);

        byte[] result = new byte[(int)file.length()];
        try {
            InputStream input = null;
            try {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while(totalBytesRead < result.length){
                    int bytesRemaining = result.length - totalBytesRead;
                    //input.read() returns -1, 0, or more :
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0){
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
/*
the above style is a bit tricky: it places bytes into the 'result' array;
'result' is an output parameter;
the while loop usually has a single iteration only.
*/

            }
            finally {

                input.close();
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        }
        catch (IOException ex) {
            System.out.println(ex);
        }
        return result;
    }

    /**
     Write a byte array to the given file.
     Writing binary data is significantly simpler than reading it.
     */
    public static void write(byte[] input, String outputFileName){

        try {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(outputFileName));
                output.write(input);
            }
            finally {
                output.close();
            }
        }
        catch(FileNotFoundException ex){
            System.out.println("File not found.");
        }
        catch(IOException ex){
            System.out.println(ex);
        }
    }

}


