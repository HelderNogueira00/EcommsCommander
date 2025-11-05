package com.ecomms.commander;
import java.io.FileOutputStream;

public class APIManager {

    private static APIManager INSTANCE = null;
    public APIManager() {

        INSTANCE = this;
    }

    public void processCommands() {

        CommandsManager commands = new CommandsManager(NetworkCommander.getInstance());
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                while (true) {
        
                    if(NetworkCommander.getInstance() == null)
                        continue;
        
                    try {
                        
                        byte[] buffer = SerializationManager.ReadFile(EnvironmentVars.CommandsFile);
                        String command = new String(buffer).replaceAll("[\\r\\n]+", "").toLowerCase();
            
                        if(!command.equals("")) {

                            System.out.println("Current Command: " + command);
                            SerializationManager.WriteFile(EnvironmentVars.CommandsFile, "", false);

                            if(command.contains("play"))
                                commands.sendPlayLocal(command);
                                                                
                        }
            
                        Thread.sleep(1000);
                    }
                    catch(Exception _e) { System.out.println("Processing Commands Error: " + _e.getMessage()); }
                }
            }
        }).start();
    }

    public static APIManager getInstance() { return INSTANCE; }
}
