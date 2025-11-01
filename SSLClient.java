import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLClient {
    
    private final int SERVER_PORT;
    private final String SERVER_IP;

    private SSLSocket mSocket;
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
            System.out.println("Agent Connected");

            receive();
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

            byte[] lengthBuffer = new byte[4];
            mInput.read(lengthBuffer);

            int length = ByteBuffer.allocate(4).put(lengthBuffer).getInt();
            System.out.println("Packet Length: " + length);

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
        }
        catch(Exception _e) { System.out.println("Client Disconnected: " + _err); }
    } 

}
