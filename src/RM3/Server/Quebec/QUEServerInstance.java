package RM3.Server.Quebec;

import RM3.utils.Constants;
import RM3.Logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.xml.ws.Endpoint;

public class QUEServerInstance {

    private String serverID;
    private String serverName;
    private int serverUdpPort;
    private String serverUrl;

    public QUEServerInstance(String serverId, String[] args) throws Exception {
        this.serverID = serverId;
        serverName = Constants.APPOINTMENT_SERVER_QUEBEC;
        serverUdpPort = Constants.QUEBEC_SERVER_PORT;
        serverUrl = Constants.QUE_ADDRESS;
        try {
            QUEHealthCareImpl servant = new QUEHealthCareImpl(serverID, serverName);
            System.out.println(serverName + " Server is Up & Running");
            Logger.serverLog(serverID, " Server is Up & Running");

            // addTestData(servant);
            Runnable task = () -> {
                listenForRequest(servant, serverUdpPort, serverName, serverID);
            };
            Thread thread = new Thread(task);
            thread.start();

            Endpoint.publish(serverUrl, servant);
        } catch (Exception e) {
            // System.err.println("Exception: " + e);
            e.printStackTrace(System.out);
            Logger.serverLog(serverID, "Exception: " + e);
        }

    }

    private static void listenForRequest(QUEHealthCareImpl obj, int serverUdpPort, String serverName, String serverID) {
        DatagramSocket aSocket = null;
        String sendingResult = "";
        try {
            aSocket = new DatagramSocket(serverUdpPort);
            byte[] buffer = new byte[1000];
            System.out.println(serverName + " UDP Server Started at port " + aSocket.getLocalPort() + " ............");
            Logger.serverLog(serverID, " UDP Server Started at port " + aSocket.getLocalPort());
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String sentence = new String(request.getData(), 0,
                        request.getLength());
                String[] parts = sentence.split(";");
                String method = parts[0];
                String customerID = parts[1];
                String eventType = parts[2];
                String eventID = parts[3];
                if (method.equalsIgnoreCase("removeAppointment")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ",
                            " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                    String result = obj.removeEventUDP(eventID, eventType, customerID);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("listAppointmentAvailability")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ",
                            " eventType: " + eventType + " ", " ...");
                    String result = obj.listAppointmentAvailabilityUDP(eventType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("bookAppointment")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ",
                            " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                    String result = obj.bookAppointment(customerID, eventID, eventType);
                    sendingResult = result + ";";
                } else if (method.equalsIgnoreCase("cancelAppointment")) {
                    Logger.serverLog(serverID, customerID, " UDP request received " + method + " ",
                            " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                    String result = obj.cancelAppointment(customerID, eventID, eventType);
                    sendingResult = result + ";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
                Logger.serverLog(serverID, customerID, " UDP reply sent " + method + " ",
                        " eventID: " + eventID + " eventType: " + eventType + " ", sendingResult);
            }
        } catch (SocketException e) {
            System.err.println("SocketException: " + e);
            e.printStackTrace(System.out);
        } catch (IOException e) {
            System.err.println("IOException: " + e);
            e.printStackTrace(System.out);
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }
}

