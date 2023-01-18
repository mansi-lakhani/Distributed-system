package RM3.Server.Sherbrooke;

import RM3.utils.Constants;

public class SHEServer {
    public static void main(String[] args){
        // start all servers
        Runnable server1 = () -> {
            try{
                new SHEServerInstance(Constants.SHERBROOKE_SERVER, args);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        };
        Thread server1Thread = new Thread(server1);
        server1Thread.start();
    } 
}
