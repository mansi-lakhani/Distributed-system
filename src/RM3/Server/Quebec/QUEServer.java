package RM3.Server.Quebec;

import RM3.utils.Constants;

public class QUEServer {
    public static void main(String[] args){
        // start all servers
        Runnable server1 = () -> {
            try{
                new QUEServerInstance(Constants.QUEBEC_SERVER, args);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        };
        Thread server1Thread = new Thread(server1);
        server1Thread.start();
    } 
}

