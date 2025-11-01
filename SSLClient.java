import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public abstract class SSLClient {
    
    private final int SERVER_PORT;
    private final String SERVER_IP;

    protected SSLSocket mSocket;
    private SSLContext mContext;
    private InputStream mInput;
    private OutputStream mOutput;
    private boolean isRunning = false;
    private boolean mConnected = false;

    public SSLClient(String serverIP, int serverPort) {

        this.SERVER_IP = serverIP;
        this.SERVER_PORT = serverPort;

        load();
    }

    private void load() {

        try {

            String textData = Files.readString(new File(EnvironmentVars.KeystorePasswordFile).toPath());
            char[] ksPassword = textData.replaceAll("[\\r\\n]+", "").toCharArray();
            
            KeyStore ks = KeyStore.getInstance("PKCS12");
            KeyStore ts = KeyStore.getInstance("PKCS12");
            ts.load(new FileInputStream(EnvironmentVars.TruststoreFile), ksPassword);
            ks.load(new FileInputStream(EnvironmentVars.KeystoreFile), ksPassword);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ts);
            kmf.init(ks, ksPassword);

            mContext = SSLContext.getInstance("TLS");
            mContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        }
        catch(Exception _e) { System.out.println("SSLClient Loadinf Exception: " + _e.getMessage()); }
    }

    public void connect() {

        try {
            
            SSLSocketFactory ssf = mContext.getSocketFactory();
            mSocket = (SSLSocket)ssf.createSocket(SERVER_IP, SERVER_PORT);
            mSocket.startHandshake();

            if(!mSocket.isConnected()) {

                System.out.println("Socket Not Connected!");
                return;
            }

            isRunning = true;
            mInput = mSocket.getInputStream();
            mOutput = mSocket.getOutputStream();
            System.out.println("SERVER: Connection Received, Waiting Auth");

            new Thread(new Runnable() { @Override public void run() { receive(); }}).start();
            onConnected();
        }
        catch(Exception _e) { System.out.println("SSLClient Connect Error: " + _e.getMessage()); }
    }
    
    public void send(NetworkPacket _pck) {

        try {

            if(mOutput != null) {

                byte[] buffer = _pck.prepare();
                mOutput.write(buffer);
                mOutput.flush();
            }
        }
        catch(Exception _e) { System.out.println("Client Sending Exception: " + _e.getMessage()); }
    }

    private void receive() {

        try {

            if(mInput == null)
                return;

            ArrayList<Byte> receivedBuffer = new ArrayList<>();
            while(receivedBuffer.size() < 4) {

                byte val = (byte)mInput.read();
                if(val == -1)
                    disconnect("Null byte received!");
                
                    receivedBuffer.add(val);
            }
            
            int pckLength = ByteBuffer.allocate(4).put(UtilsManager.ToByteArray(receivedBuffer)).getInt(0);
            while(receivedBuffer.size() < pckLength + 4) {

                byte val = (byte)mInput.read();
                if(val == -1)
                    disconnect("Null byte received!");
                
                receivedBuffer.add(val);
            }

            onPacketReceived(new NetworkPacket(receivedBuffer));
            receive();
        }
        catch(Exception _e) { System.out.println("Client Receiving Error: " + _e.getMessage()); }
    }

    public void disconnect(String _err) {

        try {

            if(mSocket != null) {

                mInput.close();
                mOutput.close();
                mSocket.close();
            }           

            mInput = null;
            mOutput = null;
            mSocket = null;

            return;
        }
        catch(Exception _e) { System.out.println("Client Disconnected: " + _err); }
    } 

    protected abstract void onConnected();
    protected abstract void onDisconnected();
    protected abstract void onPacketReceived(NetworkPacket _pck);
}
