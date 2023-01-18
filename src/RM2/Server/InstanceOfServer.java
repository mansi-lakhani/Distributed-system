package RM2.Server;

import RM2.DataModel.AppointmentModel;
import RM2.ServerInterface.AppointmentManager;
import RM2.datastorage.DataLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.xml.ws.Endpoint;

public class InstanceOfServer 
{

	 private String serverID;
	    private String serverName;
	    private String serverEndpoint;
	    private int serverUdpPort;

    public InstanceOfServer(String serverID) throws Exception 
    {
        this.serverID = serverID;
        switch (serverID)
        {
	        case "MTL":
	            serverName = AppointmentManager.APPOINTMENT_SERVER_MONTREAL;
	            serverUdpPort = AppointmentManager.Montreal_Server_Port;
	            serverEndpoint = "http://localhost:7644/montreal";
	            break;
	        case "QUE":
	            serverName = AppointmentManager.APPOINTMENT_SERVER_QUEBEC;
	            serverUdpPort = AppointmentManager.Quebec_Server_Port;
	            serverEndpoint = "http://localhost:7644/quebec";
	            break;
	        case "SHE":
	            serverName = AppointmentManager.APPOINTMENT_SERVER_SHERBROOK;
	            serverUdpPort = AppointmentManager.Sherbrooke_Server_Port;
	            serverEndpoint = "http://localhost:7644/sherbrook";
	            break;
        }

        try {
            System.out.println(serverName + " RM2.Server Started...");
            DataLogger.serverLog(serverID, " RM2.Server Started...");
            AppointmentManager service = new AppointmentManager(serverID, serverName);

            Endpoint endpoint = Endpoint.publish(serverEndpoint, service);

            System.out.println(serverName + " RM2.Server is Up & Running");
            DataLogger.serverLog(serverID, " RM2.Server is Up & Running");

//            addTestData(server);
            Runnable task = () -> {
                listenForRequest(service, serverUdpPort, serverName, serverID);
            };
            Thread thread = new Thread(task);
            thread.start();

        } catch (Exception e) {
//            System.err.println("Exception: " + e);
            e.printStackTrace(System.out);
            DataLogger.serverLog(serverID, "Exception: " + e);
        }
    }

    private void addTestData(AppointmentManager remoteObject) 
    {
        switch (serverID)
        {
            case "MTL":
                remoteObject.addNewappointment("MTLA010121", AppointmentModel.PHYSICIAN, 2);
                remoteObject.addNewappointment("MTLA080828", AppointmentModel.DENTAL, 2);
                remoteObject.addNewappointment("MTLE222222", AppointmentModel.SURGEON, 1);
                remoteObject.addNewappointment("MTLA111122", AppointmentModel.DENTAL, 12);
                break;
            case "QUE":
                remoteObject.addNewPatientToClients("QUEP1234");
                remoteObject.addNewPatientToClients("QUEP4321");
                break;
            case "SHE":
                remoteObject.addNewappointment("SHEE110620", AppointmentModel.PHYSICIAN, 1);
                remoteObject.addNewappointment("SHEE080620", AppointmentModel.PHYSICIAN, 1);
                break;
        }
    }

    private static void listenForRequest(AppointmentManager obj, int serverUdpPort, String serverName, String serverID) 
    {
        DatagramSocket aSocket = null;
        String sendingResult = "";
        try 
        {
            aSocket = new DatagramSocket(serverUdpPort);
            byte[] buffer = new byte[1000];
            System.out.println(serverName + " UDP RM2.Server Started at port " + aSocket.getLocalPort() + " ............");
            DataLogger.serverLog(serverID, " UDP RM2.Server Started at port " + aSocket.getLocalPort());
            while (true) 
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String sentence = new String(request.getData(), 0,
                        request.getLength());
                String[] parts = sentence.split(";");
                String method = parts[0];
                String patientID = parts[1];
                String appointmentType = parts[2];
                String appointmentID = parts[3];
                if (method.equalsIgnoreCase("removeAppointment"))
                {
                    DataLogger.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.removeAppointmentUDP(appointmentID, appointmentType, patientID);
                    sendingResult = result + ";";
                } 
                else if (method.equalsIgnoreCase("listAppointmentAvailability")) 
                {
                    DataLogger.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.listAppointmentAvailabilityUDP(appointmentType);
                    sendingResult = result + ";";
                } 
                else if (method.equalsIgnoreCase("bookAppointment")) 
                {
                    DataLogger.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.bookAppointment(patientID, appointmentID, appointmentType);
                    sendingResult = result + ";";
                } 
                else if (method.equalsIgnoreCase("cancelAppointment")) 
                {
                    DataLogger.serverLog(serverID, patientID, " UDP request received " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", " ...");
                    String result = obj.cancelappointment(patientID, appointmentID, appointmentType);
                    sendingResult = result + ";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);
                DataLogger.serverLog(serverID, patientID, " UDP reply sent " + method + " ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", sendingResult);
            }
        } 
        catch (SocketException e) 
        {
            System.out.println("SocketException: " + e.getMessage());
        } 
        catch (IOException e)
        {
            System.out.println("IOException: " + e.getMessage());
        } 
        finally
        {
            if (aSocket != null)
                aSocket.close();
        }
    }
}
