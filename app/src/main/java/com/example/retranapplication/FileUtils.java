package com.example.retranapplication;

import android.os.Environment;

import java.io.File;

public class FileUtils {
    public static boolean isSdOk(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }
    public static File getRootFile(){
        File sd = Environment.getExternalStorageDirectory();
        File rootFile = new File(sd,"Tem");
        if (!rootFile.exists()){
            rootFile.mkdirs();
        }
        return rootFile;
    }
}
