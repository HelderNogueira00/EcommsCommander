package com.ecomms.commander;

import org.checkerframework.checker.units.qual.Length;

public class NetworkCommander extends SSLClient {
    
    class Commands {

        public static final int Auth = 0;
        public static final int Disconnect = 1;
        public static final int AgentConnected = 2;
        public static final int Connectivity = 3;
        public static final int OnPlayRequest = 4;
        public static final int OnTransferStart = 5;
        public static final int OnTransferData = 6;
        public static final int OnTransferEnd = 7;
        public static final int FileExists = 8;
        public static final int FileExistsResult = 9;
        public static final int OnNotification = 13;
    }

    private static NetworkCommander INSTANCE;

    public NetworkCommander() {

        super("10.8.0.1", 4520);
        INSTANCE = this;
        System.out.println("AGENT: Commander OK " + NetworkCommander.INSTANCE);
    }

    protected void onConnected() {}
    protected void onDisconnected() {}
    protected void onPacketReceived(NetworkPacket _pck) {

        int length = _pck.readInt();
        int commandID = _pck.readInt();

        switch(commandID) {

            case Commands.Auth: onAuthentication(_pck); break;
            case Commands.Connectivity: onConnectivity(_pck); break;
            case Commands.AgentConnected: onAgentConnected(_pck); break;
            case Commands.FileExists: onFileExistsReceived(_pck); break;
            case Commands.OnTransferData: onTransferData(_pck); break;
        }
    }

    private void onTransferData(NetworkPacket _pck) {

        int itemID = _pck.readInt();
        int bufferLength = _pck.readInt();
        byte[] buffer = _pck.readBytes(bufferLength);

        long writtenLength = DownloadsManager.getInstance().getNewItem(itemID).writeBlock(buffer);
        NetworkPacket pck = new NetworkPacket(Commands.OnTransferData);
        pck.write(itemID);
        pck.write(writtenLength);
        send(pck);
    }

    private void onAuthentication(NetworkPacket _pck) {

        int method = _pck.readInt();
        if(method == 0) {

            String authToken = new String(SerializationManager.ReadFile(EnvironmentVars.TokenFile)).replaceAll("[\\r\\n]+", "");
            NetworkPacket pck = new NetworkPacket(Commands.Auth);
            pck.write(authToken);
            send(pck);
        }
        else {

            if(_pck.readInt() == EnumsList.AUTHENTICATION_GRANTED) {

                System.out.println("SERVER: Authentication was Granted!");
                return;
            }
            
            System.out.println("SERVER: Authentication was Denied, Disconnecting!");
        }
    }

    private void onAgentConnected(NetworkPacket _pck) {

        int type = _pck.readInt();
        String connMessage = "";

        switch(type) {

            case EnumsList.AGENT_COMMANDER: connMessage = "Connected as Commander Agent."; break;
            case EnumsList.AGENT_ANDROID_REMOTE: connMessage = "Connected as Android Remote Agent."; break;
        }

        System.out.println("SERVER: " + connMessage);  
        requestConnectivityLevel(); 
    }

    private void onConnectivity(NetworkPacket _pck) {

        int connLevel = _pck.readInt();
        String connMessage = "";

        switch(connLevel) {

            case EnumsList.CONNECTIVITY_POOR: connMessage = "Poor Connectivity With Server."; break;
            case EnumsList.CONNECTIVITY_MEDIUM: connMessage = "Ok Connectivity With Server."; break;
            case EnumsList.CONNECTIVITY_GOOD: connMessage = "Good Connectivity With Server"; break;
        }

        System.out.println("SERVER: " + connMessage);        
    }

    public void onFileExistsReceived(NetworkPacket _pck) {

        String filePath = _pck.readString();
        boolean exists = SerializationManager.FileExists(filePath);

        NetworkPacket pck = new NetworkPacket(Commands.FileExistsResult);
        pck.write(filePath);
        pck.write(exists);
        send(pck);
    }

    public void onTransferStart(NetworkPacket _pck) {

        int itemID = _pck.readInt();
        String path = _pck.readString();
        long length = _pck.readLong();
        int blockSize = _pck.readInt();

        if(SerializationManager.FileExists(path)) {

            //Send End
            return;
        }

        NewItem item = new NewItem(itemID, length, path);
        DownloadsManager.getInstance().addItem(item);

        NetworkPacket pck = new NetworkPacket(Commands.OnTransferStart);
        pck.write(itemID);
        pck.write(true);
        send(pck);
    }


    public void requestConnectivityLevel() {

        NetworkPacket pck = new NetworkPacket(Commands.Connectivity);
        send(pck);
    }

    public boolean isActive() { return mSocket != null; }
    public static NetworkCommander getInstance() { return INSTANCE; }
}
