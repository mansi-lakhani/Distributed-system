package RM3.Server.Montreal;

import RM3.utils.Constants;

public class MTLServer {
    public static void main(String[] args){
        // start all servers
        Runnable server1 = () -> {
            try{
                new MTLServerInstance(Constants.MONTREAL_SERVER, args);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        };
        Thread server1Thread = new Thread(server1);
        server1Thread.start();
    } 
}
