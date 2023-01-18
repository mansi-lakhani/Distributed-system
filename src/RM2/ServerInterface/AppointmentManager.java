package RM2.ServerInterface;

import RM2.DataModel.AppointmentModel;
import RM2.DataModel.UserModel;
import RM2.Interface.AdminInterface;
import RM2.Interface.AdminandPatientInterface;
import RM2.datastorage.DataLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(endpointInterface = "RM2.ServerInterface.WebInterface")
@SOAPBinding(style = SOAPBinding.Style.RPC)

public class AppointmentManager implements WebInterface

{
    public static final int Montreal_Server_Port = 6531;
    public static final int Quebec_Server_Port = 2211;
    public static final int Sherbrooke_Server_Port = 8188;
    public static final String APPOINTMENT_SERVER_SHERBROOK = "SHERBROOK";
    public static final String APPOINTMENT_SERVER_QUEBEC = "QUEBEC";
    public static final String APPOINTMENT_SERVER_MONTREAL = "MONTREAL";
    private String serverID;
    private String serverName;
    
    private Map<String, Map<String, AppointmentModel>> allAppointments;
    
    private Map<String, Map<String, List<String>>> clientAppointments;
    
    private Map<String, UserModel> serverClients;

    public AppointmentManager(String serverID, String serverName) throws RemoteException 
    
    {
        super();
        this.serverID = serverID;
        this.serverName = serverName;
        allAppointments = new ConcurrentHashMap<>();
        allAppointments.put(AppointmentModel.PHYSICIAN, new ConcurrentHashMap<>());
        allAppointments.put(AppointmentModel.SURGEON, new ConcurrentHashMap<>());
        allAppointments.put(AppointmentModel.DENTAL, new ConcurrentHashMap<>());
        clientAppointments = new ConcurrentHashMap<>();
        serverClients = new ConcurrentHashMap<>();

    }

    private void addTestData() 
    
    {

        UserModel tPatient = new UserModel(serverID + "C1111");

        serverClients.put(tPatient.getClientID(), tPatient);
        clientAppointments.put(tPatient.getClientID(), new ConcurrentHashMap<>());

        AppointmentModel sampleConf = new AppointmentModel(AppointmentModel.PHYSICIAN, serverID + "M010120", 5);
        sampleConf.addRegisteredClientID(tPatient.getClientID());
        clientAppointments.get(tPatient.getClientID()).put(sampleConf.getAppointmentType(), new ArrayList<>());
        clientAppointments.get(tPatient.getClientID()).get(sampleConf.getAppointmentType()).add(sampleConf.getAppointmentID());

        AppointmentModel sampleTrade = new AppointmentModel(AppointmentModel.DENTAL, serverID + "A020220", 15);
        sampleTrade.addRegisteredClientID(tPatient.getClientID());
        clientAppointments.get(tPatient.getClientID()).put(sampleTrade.getAppointmentType(), new ArrayList<>());
        clientAppointments.get(tPatient.getClientID()).get(sampleTrade.getAppointmentType()).add(sampleTrade.getAppointmentID());

        AppointmentModel sampleSemi = new AppointmentModel(AppointmentModel.SURGEON, serverID + "E030320", 20);
        sampleSemi.addRegisteredClientID(tPatient.getClientID());
        clientAppointments.get(tPatient.getClientID()).put(sampleSemi.getAppointmentType(), new ArrayList<>());
        clientAppointments.get(tPatient.getClientID()).get(sampleSemi.getAppointmentType()).add(sampleSemi.getAppointmentID());

        allAppointments.get(AppointmentModel.PHYSICIAN).put(sampleConf.getAppointmentID(), sampleConf);
        allAppointments.get(AppointmentModel.DENTAL).put(sampleTrade.getAppointmentID(), sampleTrade);
        allAppointments.get(AppointmentModel.SURGEON).put(sampleSemi.getAppointmentID(), sampleSemi);
    }

    private static int getServerPort(String zzz) 
    {
        if (zzz.equalsIgnoreCase("MTL")) 
        {
            return Montreal_Server_Port;
        } else if (zzz.equalsIgnoreCase("SHE")) 
        {
            return Sherbrooke_Server_Port;
        } else if (zzz.equalsIgnoreCase("QUE")) 
        {
            return Quebec_Server_Port;
        }
        return 1;
    }

    @Override
    public String addAppointment(String appointmentID, String appointmentType, int bookingCapacity)
    {
        String response;
        if (allAppointments.get(appointmentType).containsKey(appointmentID))
        {
            if (allAppointments.get(appointmentType).get(appointmentID).getAppointmentCapacity() <= bookingCapacity) {
                allAppointments.get(appointmentType).get(appointmentID).setAppointmentCapacity(bookingCapacity);
                response = "Success: Appointment " + appointmentID + " Capacity increased to " + bookingCapacity;
                try 
                {
                    DataLogger.serverLog(serverID, "null", " RMI addAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " bookingCapacity " + bookingCapacity + " ", response);
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return response;
            }
            else 
            {
                response = "Failed: Appointment Already Exists, Cannot Decrease Booking Capacity";
                try 
                {
                    DataLogger.serverLog(serverID, "null", " RMI addAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " bookingCapacity " + bookingCapacity + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
        if (AppointmentModel.detectAppointmentServer(appointmentID).equals(serverName)) 
        {
            AppointmentModel appointment = new AppointmentModel(appointmentType, appointmentID, bookingCapacity);
            Map<String, AppointmentModel> appointmentHashMap = allAppointments.get(appointmentType);
            appointmentHashMap.put(appointmentID, appointment);
            allAppointments.put(appointmentType, appointmentHashMap);
            response = "Success: Appointment " + appointmentID + " added successfully";
            try 
            {
                DataLogger.serverLog(serverID, "null", " RMI addAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " bookingCapacity " + bookingCapacity + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } else 
        {
            response = "Failed: Cannot Add Appointment to servers other than " + serverName;
            try 
            {
                DataLogger.serverLog(serverID, "null", " RMI addAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " bookingCapacity " + bookingCapacity + " ", response);
            } catch (IOException e) 
            {
                e.printStackTrace();
            }
            return response;
        }
    }

    @Override
    public String removeAppointment(String appointmentID, String appointmentType) 
    {
        String response;
        if (AppointmentModel.detectAppointmentServer(appointmentID).equals(serverName)) 
        {
            if (allAppointments.get(appointmentType).containsKey(appointmentID)) 
            {
                List<String> registeredClients = allAppointments.get(appointmentType).get(appointmentID).getRegisteredClientIDs();
                allAppointments.get(appointmentType).remove(appointmentID);
                addPatientsToNextSameAppointment(appointmentID, appointmentType, registeredClients);
                response = "Success: Appointment Removed Successfully";
                try 
                {
                    DataLogger.serverLog(serverID, "null", " RMI removeAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return response;
            } 
            else 
            {
                response = "Failed: Appointment " + appointmentID + " Does Not Exist";
                try 
                {
                    DataLogger.serverLog(serverID, "null", " RMI removeAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return response;
            }
        } 
        else 
        {
            response = "Failed: Cannot Remove Appointment from servers other than " + serverName;
            try 
            {
                DataLogger.serverLog(serverID, "null", " RMI removeAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
            } catch (IOException e) 
            {
                e.printStackTrace();
            }
            return response;
        }
    }

    @Override
    public String listAppointmentAvailability(String appointmentType) 
    {
        String response;
        Map<String, AppointmentModel> appointments = allAppointments.get(appointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " RM2.Server " + appointmentType + ":\n");
        if (appointments.size() == 0) 
        {
            builder.append("No appointments of Type " + appointmentType);
        } else
        {
            for (AppointmentModel appointment :
                    appointments.values()) 
            {
                builder.append(appointment.toString() + " || ");
            }
            builder.append("\n=====================================\n");
        }
        String otherServer1, otherServer2;
        if (serverID.equals("MTL")) 
        {
            otherServer1 = sendUDPMessage(Sherbrooke_Server_Port, "listAppointmentAvailability", "null", appointmentType, "null");
            otherServer2 = sendUDPMessage(Quebec_Server_Port, "listAppointmentAvailability", "null", appointmentType, "null");
        } 
        else if (serverID.equals("SHE")) {
            otherServer1 = sendUDPMessage(Quebec_Server_Port, "listAppointmentAvailability", "null", appointmentType, "null");
            otherServer2 = sendUDPMessage(Montreal_Server_Port, "listAppointmentAvailability", "null", appointmentType, "null");
        } 
        else 
        {
            otherServer1 = sendUDPMessage(Montreal_Server_Port, "listAppointmentAvailability", "null", appointmentType, "null");
            otherServer2 = sendUDPMessage(Sherbrooke_Server_Port, "listAppointmentAvailability", "null", appointmentType, "null");
        }
        builder.append(otherServer1).append(otherServer2);
        response = builder.toString();
        try 
        {
            DataLogger.serverLog(serverID, "null", " RMI listappointmentAvailability ", " AppointmentType: " + appointmentType + " ", response);
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String bookAppointment(String patientID, String appointmentID, String appointmentType) 
    {
        String response;
        if (!serverClients.containsKey(patientID)) 
        {
            addNewPatientToClients(patientID);
        }
        if (AppointmentModel.detectAppointmentServer(appointmentID).equals(serverName))
        {
            AppointmentModel bookedappointment = allAppointments.get(appointmentType).get(appointmentID);
            if (!bookedappointment.isFull()) 
            {
                if (clientAppointments.containsKey(patientID)) 
                {
                    if (clientAppointments.get(patientID).containsKey(appointmentType)) 
                    {
                        if (!clientAppointments.get(patientID).get(appointmentType).contains(appointmentID)) 
                        {
                            clientAppointments.get(patientID).get(appointmentType).add(appointmentID);
                        } else 
                        {
                            response = "Failed: appointment " + appointmentID + " Already Booked";
                            try 
                            {
                                DataLogger.serverLog(serverID, patientID, " RMI bookAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
                            } catch (IOException e) 
                            {
                                e.printStackTrace();
                            }
                            return response;
                        }
                    } 
                    else 
                    {
                        List<String> temp = new ArrayList<>();
                        temp.add(appointmentID);
                        clientAppointments.get(patientID).put(appointmentType, temp);
                    }
                } 
                else 
                {
                    Map<String, List<String>> temp = new ConcurrentHashMap<>();
                    List<String> temp2 = new ArrayList<>();
                    temp2.add(appointmentID);
                    temp.put(appointmentType, temp2);
                    clientAppointments.put(patientID, temp);
                }
                if (allAppointments.get(appointmentType).get(appointmentID).addRegisteredClientID(patientID) == AppointmentModel.ADD_SUCCESS) {
                    response = "Success: appointment " + appointmentID + " Booked Successfully";
                } else if (allAppointments.get(appointmentType).get(appointmentID).addRegisteredClientID(patientID) == AppointmentModel.APPOINTMENT_FULL) {
                    response = "Failed: appointment " + appointmentID + " is Full";
                } else {
                    response = "Failed: Cannot Add You To Appointment " + appointmentID;
                }
                try {
                    DataLogger.serverLog(serverID, patientID, " RMI bookAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } 
            else 
            {
                response = "Failed: This Appointment " + appointmentID + " is Full";
                try 
                {
                    DataLogger.serverLog(serverID, patientID, " RMI bookAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return response;
            }
        } 
        else
        {
            if (!exceedWeeklyLimit(patientID, appointmentID.substring(4))) 
            {
                String serverResponse = sendUDPMessage(getServerPort(appointmentID.substring(0, 3)), "bookAppointment", patientID, appointmentType, appointmentID);
                if (serverResponse.startsWith("Success:")) 
                {
                    if (clientAppointments.get(patientID).containsKey(appointmentType)) 
                    {
                        clientAppointments.get(patientID).get(appointmentType).add(appointmentID);
                    } else 
                    {
                        List<String> temp = new ArrayList<>();
                        temp.add(appointmentID);
                        clientAppointments.get(patientID).put(appointmentType, temp);
                    }
                }
                try 
                {
                    DataLogger.serverLog(serverID, patientID, " RMI bookAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return serverResponse;
            } 
            else 
            {
                response = "Failed: You Cannot Book Appointment in Other Servers For This Week(Max Weekly Limit = 3)";
                try 
                {
                    DataLogger.serverLog(serverID, patientID, "  bookAppointment ", " AppointmentID: " + appointmentID + " AppointmentType: " + appointmentType + " ", response);
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return response;
            }
        }
    }

    @Override
    public String getBookingSchedule(String patientID) 
    {
        String response;
        if (!serverClients.containsKey(patientID)) 
        {
            addNewPatientToClients(patientID);
            response = "Booking Schedule Empty For " + patientID;
            try 
            {
                DataLogger.serverLog(serverID, patientID, " RMI getBookingSchedule ", "null", response);
            } catch (IOException e) 
            {
                e.printStackTrace();
            }
            return response;
        }
        Map<String, List<String>> appointments = clientAppointments.get(patientID);
        if (appointments.size() == 0) 
        {
            response = "Booking Schedule Empty For " + patientID;
            try 
            {
                DataLogger.serverLog(serverID, patientID, " RMI getBookingSchedule ", "null", response);
            } catch (IOException e) 
            {
                e.printStackTrace();
            }
            return response;
        }
        StringBuilder builder = new StringBuilder();
        for (String appointmentType :
                appointments.keySet()) 
        {
            builder.append(appointmentType + ":\n");
            for (String appointmentID :
                    appointments.get(appointmentType)) 
            {
                builder.append(appointmentID + " ||");
            }
            builder.append("\n=====================================\n");
        }
        response = builder.toString();
        try
        {
            DataLogger.serverLog(serverID, patientID, " RMI getBookingSchedule ", "null", response);
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String cancelappointment(String patientID, String appointmentID, String appointmentType) 
    {
        String response;
        if (AppointmentModel.detectAppointmentServer(appointmentID).equals(serverName)) 
        {
            if (patientID.substring(0, 3).equals(serverID)) {
                if (!serverClients.containsKey(patientID)) {
                    addNewPatientToClients(patientID);
                    response = "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
                    try {
                        DataLogger.serverLog(serverID, patientID, "cancelAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    if (clientAppointments.get(patientID).get(appointmentType).remove(appointmentID)) {
                        allAppointments.get(appointmentType).get(appointmentID).removeRegisteredClientID(patientID);
                        response = "Success: appointment " + appointmentID + " Canceled for " + patientID;
                        try {
                            DataLogger.serverLog(serverID, patientID, "  cancelAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    } else {
                        response = "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
                        try {
                            DataLogger.serverLog(serverID, patientID, "  cancelAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    }
                }
            } else {
                if (allAppointments.get(appointmentType).get(appointmentID).removeRegisteredClientID(patientID)) {
                    response = "Success: Appointment " + appointmentID + " Canceled for " + patientID;
                    try {
                        DataLogger.serverLog(serverID, patientID, " RMI cancelAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    response = "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
                    try {
                        DataLogger.serverLog(serverID, patientID, "  cancelAppointment ", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            }
        } else {
            if (patientID.substring(0, 3).equals(serverID)) {
                if (!serverClients.containsKey(patientID)) {
                    addNewPatientToClients(patientID);
                } else {
                    if (clientAppointments.get(patientID).get(appointmentType).remove(appointmentID)) {
                        return sendUDPMessage(getServerPort(appointmentID.substring(0, 3)), "cancelAppointmeent", patientID, appointmentType, appointmentID);
                    }
                }
            }
            return "Failed: You " + patientID + " Are Not Registered in " + appointmentID;
        }
    }

    
    public String removeAppointmentUDP(String previousappointmentID, String appointmentType, String patientID) throws RemoteException {
        if (!serverClients.containsKey(patientID)) {
            addNewPatientToClients(patientID);
            return "Failed: You " + patientID + " Are Not Registered in " + previousappointmentID;
        } else {
            if (clientAppointments.get(patientID).get(appointmentType).remove(previousappointmentID)) {
                return "Success: appointment " + previousappointmentID + " Was Removed from " + patientID + " Schedule";
            } else {
                return "Failed: You " + patientID + " Are Not Registered in " + previousappointmentID;
            }
        }
    }

    /**
     * for UDP calls only
     *
     * @param appointmentType
     * @return
     * @throws RemoteException
     */
    public String listAppointmentAvailabilityUDP(String appointmentType) throws RemoteException {
        Map<String, AppointmentModel> appointments = allAppointments.get(appointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " RM2.Server " + appointmentType + ":\n");
        if (appointments.size() == 0) {
            builder.append("No Appointments of Type " + appointmentType);
        } else {
            for (AppointmentModel appointment :
                    appointments.values()) {
                builder.append(appointment.toString() + " || ");
            }
        }
        builder.append("\n=====================================\n");
        return builder.toString();
    }

    private String sendUDPMessage(int serverPort, String method, String patientID, String appointmentType, String appointmentId) {
        DatagramSocket aSocket = null;
        String result = "";
        String dataFromClient = method + ";" + patientID + ";" + appointmentType + ";" + appointmentId;
        try {
            DataLogger.serverLog(serverID, patientID, " UDP request sent " + method + " ", " appointmentID: " + appointmentId + " appointmentType: " + appointmentType + " ", " ... ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split(";");
            result = parts[0];
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        try {
            DataLogger.serverLog(serverID, patientID, " UDP reply received" + method + " ", " appointmentID: " + appointmentId + " appointmentType: " + appointmentType + " ", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private String getNextSameappointment(Set<String> keySet, String appointmentType, String previousappointmentID) {
        List<String> sortedIDs = new ArrayList<String>(keySet);
        sortedIDs.add(previousappointmentID);
        Collections.sort(sortedIDs, new Comparator<String>() {
            @Override
            public int compare(String ID1, String ID2) {
                Integer timeSlot1 = 0;
                switch (ID1.substring(3, 4).toUpperCase()) {
                    case "M":
                        timeSlot1 = 1;
                        break;
                    case "A":
                        timeSlot1 = 2;
                        break;
                    case "E":
                        timeSlot1 = 3;
                        break;
                }
                Integer timeSlot2 = 0;
                switch (ID2.substring(3, 4).toUpperCase()) {
                    case "M":
                        timeSlot2 = 1;
                        break;
                    case "A":
                        timeSlot2 = 2;
                        break;
                    case "E":
                        timeSlot2 = 3;
                        break;
                }
                Integer date1 = Integer.parseInt(ID1.substring(8, 10) + ID1.substring(6, 8) + ID1.substring(4, 6));
                Integer date2 = Integer.parseInt(ID2.substring(8, 10) + ID2.substring(6, 8) + ID2.substring(4, 6));
                int dateCompare = date1.compareTo(date2);
                int timeSlotCompare = timeSlot1.compareTo(timeSlot2);
                if (dateCompare == 0) {
                    return ((timeSlotCompare == 0) ? dateCompare : timeSlotCompare);
                } else {
                    return dateCompare;
                }
            }
        });
        int index = sortedIDs.indexOf(previousappointmentID) + 1;
        for (int i = index; i < sortedIDs.size(); i++) {
            if (!allAppointments.get(appointmentType).get(sortedIDs.get(i)).isFull()) {
                return sortedIDs.get(i);
            }
        }
        return "Failed";
    }

    private boolean exceedWeeklyLimit(String patientID, String appointmentDate) {
        int limit = 0;
        for (int i = 0; i < 3; i++) {
            List<String> registeredIDs = new ArrayList<>();
            switch (i) 
            {
                case 0:
                    if (clientAppointments.get(patientID).containsKey(AppointmentModel.PHYSICIAN)) {
                        registeredIDs = clientAppointments.get(patientID).get(AppointmentModel.PHYSICIAN);
                    }
                    break;
                case 1:
                    if (clientAppointments.get(patientID).containsKey(AppointmentModel.SURGEON)) {
                        registeredIDs = clientAppointments.get(patientID).get(AppointmentModel.SURGEON);
                    }
                    break;
                case 2:
                    if (clientAppointments.get(patientID).containsKey(AppointmentModel.DENTAL)) {
                        registeredIDs = clientAppointments.get(patientID).get(AppointmentModel.DENTAL);
                    }
                    break;
            }
            for (String appointmentID :
                    registeredIDs) {
                if (appointmentID.substring(6, 8).equals(appointmentDate.substring(2, 4)) && appointmentID.substring(8, 10).equals(appointmentDate.substring(4, 6))) {
                    int week1 = Integer.parseInt(appointmentID.substring(4, 6)) / 7;
                    int week2 = Integer.parseInt(appointmentDate.substring(0, 2)) / 7;

                    if (week1 == week2) {
                        limit++;
                    }
                }
                if (limit == 3)
                    return true;
            }
        }
        return false;
    }
    
    
    
    
    
    
    

    private void addPatientsToNextSameAppointment(String oldAppointmentID, String appointmentType, List<String> registeredClients)  {
        for (String patientID :
                registeredClients) {
            if (patientID.substring(0, 3).equals(serverID)) {
                clientAppointments.get(patientID).get(appointmentType).remove(oldAppointmentID);
                String nextSameAppointmentResult = getNextSameappointment(allAppointments.get(appointmentType).keySet(), appointmentType, oldAppointmentID);
                if (nextSameAppointmentResult.equals("Failed")) {
                    return;
                } else {
                    bookAppointment(patientID, nextSameAppointmentResult, appointmentType);
                }
            } else {
                sendUDPMessage(getServerPort(patientID.substring(0, 3)), "removeappointment", patientID, appointmentType, oldAppointmentID);
            }
        }
    }

    public Map<String, Map<String, AppointmentModel>> getAllappointments() {
        return allAppointments;
    }

    public Map<String, Map<String, List<String>>> getClientappointments() {
        return clientAppointments;
    }

    public Map<String, UserModel> getServerClients() {
        return serverClients;
    }

    public void addNewappointment(String appointmentID, String appointmentType, int capacity) {
        AppointmentModel sampleConf = new AppointmentModel(appointmentType, appointmentID, capacity);
        allAppointments.get(appointmentType).put(appointmentID, sampleConf);
    }

    public void addNewPatientToClients(String patientID) 
    {
        UserModel newPatient = new UserModel(patientID);
        serverClients.put(newPatient.getClientID(), newPatient);
        clientAppointments.put(newPatient.getClientID(), new ConcurrentHashMap<>());
    }
    
    private boolean onTheSameWeek(String newAppointmentDate, String appointmentID) {
        if (appointmentID.substring(6, 8).equals(newAppointmentDate.substring(2, 4)) && appointmentID.substring(8, 10).equals(newAppointmentDate.substring(4, 6))) {
            int week1 = Integer.parseInt(appointmentID.substring(4, 6)) / 7;
            int week2 = Integer.parseInt(newAppointmentDate.substring(0, 2)) / 7;
//                    int diff = Math.abs(day2 - day1);
            return week1 == week2;
        } else {
            return false;
        }
    }
 private synchronized boolean clientHasAppointment(String patientID, String appointmentType, String appointmentID) {
        if (clientAppointments.get(patientID).containsKey(appointmentType)) {
            return clientAppointments.get(patientID).get(appointmentType).contains(appointmentID);
        } else {
            return false;
        }
    }
 private synchronized boolean checkClientExists(String patientID) {
        if (!serverClients.containsKey(patientID)) {
            addNewPatientToClients(patientID);
            return false;
        } else {
            return true;
        }
    }
 
 @Override
 public String swapAppointment(String patientID, String newAppointmentID, String newAppointmentType,
			String oldAppointmentID, String oldAppointmentType) 
 {
	 
	 String response;
     if (!checkClientExists(patientID)) {
         response = "Failed: You " + patientID + " Are Not Registered in " + oldAppointmentID;
         try {
             DataLogger.serverLog(serverID, patientID, " CORBA swapEvent ", " oldAppoiontmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", response);
         } catch (IOException e) {
             e.printStackTrace();
         }
         return response;
     } else {
         if (clientHasAppointment(patientID, oldAppointmentType, oldAppointmentID)) {
             String bookResp = "Failed: did not send book request for your newAppointment " + newAppointmentID;
             String cancelResp = "Failed: did not send cancel request for your oldAppointment " + oldAppointmentID;
             synchronized (this) {
                 if (onTheSameWeek(newAppointmentID.substring(4), oldAppointmentID) && !exceedWeeklyLimit(patientID, newAppointmentID.substring(4))) {
                     cancelResp = cancelappointment(patientID, oldAppointmentID, oldAppointmentType);
                     if (cancelResp.startsWith("Success:")) {
                         bookResp = bookAppointment(patientID, newAppointmentID, newAppointmentType);
                     }
                 } else {
                     bookResp = bookAppointment(patientID, newAppointmentID, newAppointmentType);
                     if (bookResp.startsWith("Success:")) {
                         cancelResp = cancelappointment(patientID, oldAppointmentID, oldAppointmentType);
                     }
                 }
             }
             if (bookResp.startsWith("Success:") && cancelResp.startsWith("Success:")) {
                 response = "Success: Appointment " + oldAppointmentID + " swapped with " + newAppointmentID;
             } else if (bookResp.startsWith("Success:") && cancelResp.startsWith("Failed:")) {
                 cancelappointment(patientID, newAppointmentID, newAppointmentType);
                 response = "Failed: Your oldAppointment " + oldAppointmentID + " Could not be Canceled reason: " + cancelResp;
             } else if (bookResp.startsWith("Failed:") && cancelResp.startsWith("Success:")) {
                 //hope this won't happen, but just in case.
                 String resp1 = bookAppointment(patientID, oldAppointmentID, oldAppointmentType);
                 response = "Failed: Your newAppointment " + newAppointmentID + " Could not be Booked reason: " + bookResp + " And your old appointment Rolling back: " + resp1;
             } else {
                 response = "Failed: on Both newAppointment " + newAppointmentID + " Booking reason: " + bookResp + " and oldAppointment " + oldAppointmentID + " Canceling reason: " + cancelResp;
             }
             try {
                 DataLogger.serverLog(serverID, patientID, " CORBA swapEvent ", " oldAppointmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newEventType: " + newAppointmentType + " ", response);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             return response;
         } else {
             response = "Failed: You " + patientID + " Are Not Registered in " + oldAppointmentID;
             try {
                 DataLogger.serverLog(serverID, patientID, " CORBA swapAppointment ", " oldAppointmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", response);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             return response;
         }
     }
		
	
	 
 }
     
    
    
    
    
    
}
