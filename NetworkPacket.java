
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NetworkPacket {

    private int readIndex = 0;
    private ArrayList<Byte> mBuffer = new ArrayList<>();
    private boolean isFinalized = false;

    public NetworkPacket(int _commandID) {

        readIndex = 0;
        isFinalized = false;
        mBuffer = new ArrayList<>();
        write(_commandID);
    }

    public NetworkPacket(ArrayList<Byte> _packetBuffer) {

        readIndex = 0;
        isFinalized = true;
        mBuffer = _packetBuffer;
    }

    public byte[] prepare() {

        if(isFinalized) {

            System.out.print("Packet Already Finalized");
            return null;
        }

        ArrayList<Byte> finalBuffer = new ArrayList<>();
        byte[] lengthBuffer = ByteBuffer.allocate(4).putInt(mBuffer.size()).array();

        for(int n = 0; n < 4; n++)
            finalBuffer.add(lengthBuffer[n]);

        for(int n = 0; n < mBuffer.size(); n++)
            finalBuffer.add(mBuffer.get(n));
        
        isFinalized = true;
        return UtilsManager.ToByteArray(finalBuffer);
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

        return ByteBuffer.allocate(4).put(readFromBuffer(4)).getInt(0);
    }
    public float readFloat() {

        return ByteBuffer.allocate(4).put(readFromBuffer(4)).getFloat(0);
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