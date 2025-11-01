public class NetworkCommander extends SSLClient {
    
    class Commands {

        public static final int Auth = 0;
        public static final int Disconnect = 1;
        public static final int AgentConnected = 2;
        public static final int Connectivity = 3;
    }

    private static NetworkCommander INSTANCE;

    public NetworkCommander() {

        super("10.8.0.1", 4520);
        INSTANCE = this;   
    }

    protected void onConnected() {}
    protected void onDisconnected() {}
    protected void onPacketReceived(NetworkPacket _pck) {

        int length = _pck.readInt();
        int commandID = _pck.readInt();

        switch(commandID) {

            case Commands.Auth -> onAuthentication(_pck);
            case Commands.Connectivity -> onConnectivity(_pck);
            case Commands.AgentConnected -> onAgentConnected(_pck);
        }
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

            System.out.println("Agent Auth Result: " + _pck.readInt());
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

    public void requestConnectivityLevel() {

        NetworkPacket pck = new NetworkPacket(Commands.Connectivity);
        send(pck);
    }

    public static NetworkCommander getInstance() { return INSTANCE; }
}
