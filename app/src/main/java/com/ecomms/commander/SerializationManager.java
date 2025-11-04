package com.ecomms.commander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class SerializationManager {
    
    public static byte[] ReadFile(String _path) {

        byte[] buffer = null;
        try {

            File f = new File(_path);
            ArrayList<Byte> tempBuffer = new ArrayList<>();

            if(f.exists()) {

                FileInputStream in = new FileInputStream(_path);
                while(tempBuffer.size() < f.length())
                    tempBuffer.add((byte)in.read());

                in.close();
                buffer = new byte[tempBuffer.size()];
                
                for(int n = 0; n < tempBuffer.size(); n++)
                    buffer[n] = tempBuffer.get(n);        
            }
        }
        catch(Exception _e) { System.out.println("Reading File Error: " + _e.getMessage()); }
        return buffer;
    }

    public static void WriteFile(String _path, String _content, boolean _append) {

        try {

            File f = new File(_path);
            FileOutputStream fos = new FileOutputStream(f, _append);
            fos.write(_content.getBytes());
            fos.close();
        }
        catch(Exception _e) { System.out.println("Writing File Error: " + _e.getMessage()); }
    }

    public static boolean FileExists(String _path) {

        File f = new File(_path);
        return f.exists();
    }
}
