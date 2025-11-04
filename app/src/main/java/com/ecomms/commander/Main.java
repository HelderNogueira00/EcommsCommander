package com.ecomms.commander;

public class Main {

    private static NetworkCommander commander;
    private static APIManager apiManager;

    public static void main(String[] _args) {

        commander = new NetworkCommander();
        commander.connect();
        
        apiManager = new APIManager();
        apiManager.processCommands();
    }

    public void getGreeting() {

        System.out.println("Running Ecomms Commander ...");
    }
}