import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SSLClient {
    
    private final int SERVER_PORT;
    private final String SERVER_IP;

    private SSLSocket mSocket;
    private SSLContext mContext;
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

            if(!mSocket.isConnected()) {

                System.out.println("Socket Not Connected!");
                return;
            }

            System.out.println("Agent Connected");
    
            while(true) {
                
            }
        }
        catch(Exception _e) { System.out.println("SSLClient Connect Error: " + _e.getMessage()); }
    }
}
