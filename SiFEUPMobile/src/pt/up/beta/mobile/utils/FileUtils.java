package pt.up.beta.mobile.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

public class FileUtils {
    private FileUtils(){} //private constructor
    
    // our caching functions
    // Find the dir to save cached images
    public static File getCacheDirectory(Context context) {
    String sdState = android.os.Environment.getExternalStorageState();
        File cacheDir;

        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = context.getExternalCacheDir();
        } else
            cacheDir = context.getCacheDir();

        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir;
    }

    public static void writeFile(Bitmap bmp, File f) {
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
            }
        }
    }    
    
    public static void writeFile(String page, File f) {
        FileWriter out = null;

        try {
            out = new FileWriter(f);
            out.write(page);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }  
    public static String getStringFromFile(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuffer str = new StringBuffer();
            String line = br.readLine();
            while (line != null)
            {
                str.append(line);
                str.append("\n");
                line = br.readLine();
            }

            return str.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( br != null )
                try{
                    br.close();
                } catch ( Exception e ){
                    e.printStackTrace();
                }
        }
        return null;
    }
}
