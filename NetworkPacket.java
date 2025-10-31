
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkPacket {

    private int readIndex = 0;
    private ArrayList<Byte> mBuffer;
    private boolean isFinalized = false;

    public NetworkPacket(int _commandID) {

        write(_commandID);
        readIndex = 0;
        mBuffer = new ArrayList<>();
    }

    public NetworkPacket(ArrayList<Byte> _packetBuffer) {

        readIndex = 0;
        isFinalized = true;
        mBuffer = _packetBuffer;
    }

    public byte[] prepare() {

        if(isFinalized)
            return null;

        isFinalized = true;
        write(mBuffer.size());

        byte[] buffer = new byte[mBuffer.size()];
        for(int n = 0; n < mBuffer.size(); n++)
            buffer[n] = mBuffer.get(n);
        
        return buffer;
    }

    public void write(byte val) {

        byte[] buffer = new byte[1];
        buffer[0] = val;

        incrementBuffer(buffer);
    }
    public void write(byte[] val) {

        incrementBuffer(val);
    }
    public void write(int val) {

        incrementBuffer(ByteBuffer.allocate(4).putInt(val).array());
    }
    public void write(float val) {

        incrementBuffer(ByteBuffer.allocate(4).putFloat(val).array());
    }
    public void write(boolean val) {

        byte[] buffer = new byte[1];
        buffer[0] = (byte) (val ? 1 : 0);
    
        incrementBuffer(buffer);
    }
    public void write(String val) {

        write(val.length());
        incrementBuffer(val.getBytes(StandardCharsets.UTF_8));
    }

    public byte readByte() {

        return readFromBuffer(1)[0];
    }
    public byte[] readBytes(int size) {

        return readFromBuffer(size);
    }
    public int readInt() {

        return ByteBuffer.allocate(4).put(readFromBuffer(4)).getInt();
    }
    public float readFloat() {

        return ByteBuffer.allocate(4).put(readFromBuffer(4)).getFloat();
    }
    public boolean readBool() {

        return readFromBuffer(1)[0] == 1;
    }
    public String readString() {

        int length = readInt();
        return new String(readFromBuffer(length), StandardCharsets.UTF_8);
    }

    private void incrementBuffer(byte[] _buffer) {

        if(isFinalized)
            return;

        for(int n = 0; n < _buffer.length; n++)
            mBuffer.add(_buffer[n]);
    }

    private byte[] readFromBuffer(int size) {

        byte[] buffer = new byte[size];
        for(int n = 0; n < size; n++)
            buffer[n] = mBuffer.get(readIndex + n);

        readIndex += size;
        return buffer;
    }
}