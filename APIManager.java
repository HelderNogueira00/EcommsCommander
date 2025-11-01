import java.io.FileOutputStream;

public class APIManager {

    private static APIManager INSTANCE = null;
    public APIManager() {

        INSTANCE = this;
    }

    public void processCommands() {

        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                while (true) {
        
                    if(NetworkCommander.getInstance() == null)
                        continue;
        
                    try {
                        
                        byte[] buffer = SerializationManager.ReadFile(EnvironmentVars.CommandsFile);
                        String command = new String(buffer).replaceAll("[\\r\\n]+", "");
            
                        if(!command.equals("")) {

                            System.out.println("Current Command: " + command);
                            SerializationManager.WriteFile(EnvironmentVars.CommandsFile, "", false);
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
