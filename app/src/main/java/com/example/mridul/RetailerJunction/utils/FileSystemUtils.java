package com.example.mridul.RetailerJunction.utils;

import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class FileSystemUtils {

    public static String getSHA256Sum(File file) {
        if(file == null)
            return null;

        try {
            InputStream in = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            try {
                DigestInputStream dis = new DigestInputStream(in, md);
                byte[] buffer = new byte[4096];
                while (dis.read(buffer) != -1) {
                    //
                }
                dis.close();
            } finally {
                in.close();
            }

            byte[] digest = md.digest();
            return convertToHex(digest);

        }catch (IOException ioe) {
            //TODO

        }catch(NoSuchAlgorithmException nsae) {
            //TODO

        }
        return null;
    }

    public static String convertToHex(byte[] bytes) {

        StringBuffer sb = new StringBuffer();
        String hex = null;

        hex = Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT);

        sb.append(hex);
        return sb.toString();
    }

    public  static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;
    }

}
