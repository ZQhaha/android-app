package com.example.myapplication;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    protected static void copyDirectoryFromAssets(Context appCtx, String srcDir, String dstDir) {
        if (srcDir.isEmpty() || dstDir.isEmpty()) {
            return;
        }
        try {
            if (!new File(dstDir).exists()) {
                new File(dstDir).mkdirs();
            }
            for (String fileName : appCtx.getAssets().list(srcDir)) {
                String srcSubPath = srcDir + File.separator + fileName;
                String dstSubPath = dstDir + File.separator + fileName;
                if (new File(srcSubPath).isDirectory()) {
                    copyDirectoryFromAssets(appCtx, srcSubPath, dstSubPath);
                } else {
                    copyFileFromAssets(appCtx, srcSubPath, dstSubPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFileFromAssets(Context appCtx, String srcPath, String dstPath) {
        if (srcPath.isEmpty() || dstPath.isEmpty()) {
            return;
        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(appCtx.getAssets().open(srcPath));
            os = new BufferedOutputStream(new FileOutputStream(new File(dstPath)));
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static int getMaxIndex(float[] arr) {
        int maxIndex = 0;
        for (int i = 0; i < arr.length-1; i++) {
            if(arr[maxIndex]<arr[i+1]) {
                maxIndex = i+1;
            }
        }
        return maxIndex;
    }

}
