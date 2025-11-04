package com.ecomms.commander;
import java.util.ArrayList;

public class UtilsManager {
    
    public static byte[] ToByteArray(ArrayList<Byte> _buffer) {

        byte[] buffer = new byte[_buffer.size()];
        for(int n = 0; n < buffer.length; n++)
            buffer[n] = _buffer.get(n);

        return buffer;
    }
}
