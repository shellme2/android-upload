package com.bbk.bfcupload.bfcuploadtestdemo.util;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private static void copyFromAssets(Context context, File file, String assetsName){
        AssetManager am=context.getAssets();
        InputStream in=null;
        OutputStream out=null;
        try {
            in=am.open(assetsName);
            out=new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            while ((in.read(buffer)) != -1) {
                out.write(buffer);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void copyFromAssets2(Context context,File file){
        copyFromAssets(context, file, "test2.zip");
    }
}
