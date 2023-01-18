package RM3.Server.Quebec;

import RM3.DataModel.AppointmentModel;
import RM3.DataModel.UserModel;
import java.util.*;
import RM3.Logger.Logger;
import java.io.IOException;
import RM3.utils.Constants;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class QUEHealthCareImpl {
    private String serverID;
    private String serverName;

    // HashMap<appointmentType, HashMap <appointmentId, Appointment>>
    private Map<String, Map<String, AppointmentModel>> appointments;
    // HashMap<CustomerID, HashMap <appointmentType, List<appointmentId>>>
    private Map<String, Map<String, List<String>>> userAppointments;
    // HashMap<UserId, User>
    private Map<String, UserModel> serverClients;


    public QUEHealthCareImpl(String serverID, String serverName) {
        super();
        this.serverID = serverID;
        this.serverName = serverName;
        appointments = new ConcurrentHashMap<>();
        appointments.put(Constants.DENTAL, new ConcurrentHashMap<>());
        appointments.put(Constants.PHYSICIAN, new ConcurrentHashMap<>());
        appointments.put(Constants.SURGEON, new ConcurrentHashMap<>());
        userAppointments = new ConcurrentHashMap<>();
        serverClients = new ConcurrentHashMap<>();
//        addTestData();
    }

    @WebMethod
    public String addAppointment(String appointmentId, String appointmentType, int appointmentCapacity) {
        String response;
        if (isAppointmentOfThisServer(appointmentId)) {
            if (appointentExists(appointmentType, appointmentId)) {
                if (appointments.get(appointmentType).get(appointmentId)
                        .getAppointmentCapacity() <= appointmentCapacity) {
                    appointments.get(appointmentType).get(appointmentId).setAppointmentCapacity(appointmentCapacity);
                    response = "Success: Appointment " + appointmentId + " Capacity increased to "
                            + appointmentCapacity;
                    try {
                        Logger.serverLog(serverID, "null", " addAppointment ",
                                " appointmentId: " + appointmentId + " appointmentType: " + appointmentType
                                        + " appointmentCapacity " + appointmentCapacity + " ",
                                response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    response = "Failed: Appointment Already Exists, Cannot Decrease Booking Capacity";
                    try {
                        Logger.serverLog(serverID, "null", " addAppointment ",
                                " appointmentId: " + appointmentId + " appointmentType: " + appointmentType
                                        + " appointmentCapacity " + appointmentCapacity + " ",
                                response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            } else {
                AppointmentModel newAppointment = new AppointmentModel(appointmentId, appointmentType,
                        appointmentCapacity);
                Map<String, AppointmentModel> appointmentHashMap = appointments.get(appointmentType);
                appointmentHashMap.put(appointmentId, newAppointment);
                appointments.put(appointmentType, appointmentHashMap);
                response = "Success: Appointment " + appointmentId + " added successfully";
                try {
                    Logger.serverLog(serverID, "null", " addAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType
                                    + " appointmentCapacity " + appointmentCapacity + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        } else {
            response = "Failed: Cannot Add Appointment to servers other than " + serverName;
            try {
                Logger.serverLog(serverID, "null", " addAppointment ", " appointmentId: " + appointmentId
                        + " appointmentType: " + appointmentType + " appointmentCapacity " + appointmentCapacity + " ",
                        response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @WebMethod
    public String removeAppointment(String appointmentId, String appointmentType) {
        String response;
        if (isAppointmentOfThisServer(appointmentId)) {
            if (appointentExists(appointmentType, appointmentId)) {
                // List<String> registeredClients =
                // appointments.get(appointmentType).get(appointmentId).getRegisteredClients();
                appointments.get(appointmentType).remove(appointmentId);
                // addCustomersToNextSameEvent(appointmentId, appointmentType,
                // registeredClients);
                response = "Success: Appointment " + appointmentId + " Removed Successfully";
                try {
                    Logger.serverLog(serverID, "null", " removeAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: Appointment " + appointmentId + " Does Not Exist";
                try {
                    Logger.serverLog(serverID, "null", " removeAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        } else {
            response = "Failed: Cannot Remove Appointment from servers other than " + serverName;
            try {
                Logger.serverLog(serverID, "null", " removeAppointment ",
                        " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @WebMethod
    public String listAppointmentAvailability(String appointmentType) {
        String response;
        Map<String, AppointmentModel> appointmentList = appointments.get(appointmentType);
        System.out.println("appointmentList" + appointmentList);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + appointmentType + ":\n");
        if (appointmentList.size() == 0) {
            builder.append("No Appointments of Type " + appointmentType + "\n");
        } else {
            for (AppointmentModel appointment : appointmentList.values()) {
                System.out.println("appointment" + appointment.getAppointmentCapacity());
                builder.append(appointment.getAppointmentCapacity() + " || " + appointment.getAppointmentDate() + " || " + appointment.getAppointmentTimeSlot());
            }
        }
        builder.append("\n=====================================\n");
        String otherServer1, otherServer2;
        if (serverID.equals("MTL")) {
            otherServer1 = sendUDPMessage(Constants.SHERBROOKE_SERVER_PORT, "listAppointmentAvailability", "null",
                    appointmentType, "null");
            otherServer2 = sendUDPMessage(Constants.QUEBEC_SERVER_PORT, "listAppointmentAvailability", "null",
                    appointmentType, "null");
        } else if (serverID.equals("SHE")) {
            otherServer1 = sendUDPMessage(Constants.QUEBEC_SERVER_PORT, "listAppointmentAvailability", "null",
                    appointmentType, "null");
            otherServer2 = sendUDPMessage(Constants.MONTREAL_SERVER_PORT, "listAppointmentAvailability", "null",
                    appointmentType, "null");
        } else {
            otherServer1 = sendUDPMessage(Constants.MONTREAL_SERVER_PORT, "listAppointmentAvailability", "null",
                    appointmentType, "null");
            otherServer2 = sendUDPMessage(Constants.SHERBROOKE_SERVER_PORT, "listAppointmentAvailability", "null",
                    appointmentType, "null");
        }
        builder.append(otherServer1).append(otherServer2);
        response = builder.toString();
        try {
            Logger.serverLog(serverID, "null", " listAppointmentAvailability ",
                    " appointmentType: " + appointmentType + " ", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @WebMethod
    public String bookAppointment(String userId, String appointmentId, String appointmentType) {
        String response;
        checkClientExists(userId);
        if (isAppointmentOfThisServer(appointmentId)) {
            AppointmentModel bookedAppointment = appointments.get(appointmentType).get(appointmentId);
            if (bookedAppointment == null) {
                response = "Failed: Appointment " + appointmentId + " Does not exists";
                try {
                    Logger.serverLog(serverID, userId, " bookAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
            if (!bookedAppointment.isFull()) {
                if (userAppointments.containsKey(userId)) {
                    if (userAppointments.get(userId).containsKey(appointmentType)) {
                        if (!userHasAppointment(userId, appointmentType, appointmentId)) {
                            if (isUserOfThisServer(userId))
                                userAppointments.get(userId).get(appointmentType).add(appointmentId);
                        } else {
                            response = "Failed: Appointment " + appointmentId + " Already Booked";
                            try {
                                Logger.serverLog(serverID, userId, " bookAppointment ", " appointmentId: "
                                        + appointmentId + " appointmentType: " + appointmentType + " ", response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return response;
                        }
                    } else {
                        if (isUserOfThisServer(userId))
                            addappointmentTypeAndBookAppointment(userId, appointmentType, appointmentId);
                    }
                } else {
                    if (isUserOfThisServer(userId))
                        addUserandAppointment(userId, appointmentType, appointmentId);
                }
                if (appointments.get(appointmentType).get(appointmentId)
                        .addRegisteredUserId(userId) == Constants.ADD_SUCCESS) {
                    response = "Success: Appointment " + appointmentId + " Booked Successfully";
                } else if (appointments.get(appointmentType).get(appointmentId)
                        .addRegisteredUserId(userId) == Constants.EVENT_FULL) {
                    response = "Failed: Appointment " + appointmentId + " is Full";
                } else {
                    response = "Failed: Cannot Add You To Appointment " + appointmentId;
                }
                try {
                    Logger.serverLog(serverID, userId, " bookAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: Appointment " + appointmentId + " is Full";
                try {
                    Logger.serverLog(serverID, userId, " bookAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        } else {
            if (userHasAppointment(userId, appointmentType, appointmentId)) {
                String serverResponse = "Failed: Appointment " + appointmentId + " Already Booked";
                try {
                    Logger.serverLog(serverID, userId, " bookAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return serverResponse;
            }
            if (exceedWeeklyLimit(userId, appointmentId.substring(4))) {
                String serverResponse = sendUDPMessage(getServerPort(appointmentId.substring(0, 3)), "bookAppointment",
                        userId, appointmentType, appointmentId);
                if (serverResponse.startsWith("Success:")) {
                    if (userAppointments.get(userId).containsKey(appointmentType)) {
                        userAppointments.get(userId).get(appointmentType).add(appointmentId);
                    } else {
                        List<String> temp = new ArrayList<>();
                        temp.add(appointmentId);
                        userAppointments.get(userId).put(appointmentType, temp);
                    }
                }
                try {
                    Logger.serverLog(serverID, userId, " bookAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            serverResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return serverResponse;
            } else {
                response = "Failed: You Cannot Book Appointment in Other Servers For This Week(Max Weekly Limit = 3)";
                try {
                    Logger.serverLog(serverID, userId, " bookAppointment ",
                            " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
    }

    @WebMethod
    public String getBookingSchedule(String userId) {
        String response;
        if (!checkClientExists(userId)) {
            response = "Booking Schedule Empty For " + userId;
            try {
                Logger.serverLog(serverID, userId, " getBookingSchedule ", "null", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        Map<String, List<String>> appointmentList = userAppointments.get(userId);
        if (appointmentList.size() == 0) {
            response = "Booking Schedule Empty For " + userId;
            try {
                Logger.serverLog(serverID, userId, " getBookingSchedule ", "null", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        StringBuilder builder = new StringBuilder();
        for (String appointmentType : appointmentList.keySet()) {
            builder.append(appointmentType + ":\n");
            for (String appointmentId : appointmentList.get(appointmentType)) {
                builder.append(appointmentId + " ||");
            }
            builder.append("\n=====================================\n");
        }
        response = builder.toString();
        try {
            Logger.serverLog(serverID, userId, " getBookingSchedule ", "null", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @WebMethod
    public String cancelAppointment(String userId, String appointmentId, String appointmentType) {
        String response;
        if (isAppointmentOfThisServer(appointmentId)) {
            if (isUserOfThisServer(userId)) {
                if (!checkClientExists(userId)) {
                    response = "Failed: You " + userId + " Are Not Registered in " + appointmentId;
                    try {
                        Logger.serverLog(serverID, userId, " cancelAppointment ",
                                " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                                response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    if (removeAppointmentIfExists(userId, appointmentType, appointmentId)) {
                        appointments.get(appointmentType).get(appointmentId).removeRegisteredUserId(userId);
                        response = "Success: Appointment " + appointmentId + " Canceled for " + userId;
                        try {
                            Logger.serverLog(serverID, userId, " cancelAppointment ",
                                    " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                                    response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    } else {
                        response = "Failed: You " + userId + " Are Not Registered in " + appointmentId;
                        try {
                            Logger.serverLog(serverID, userId, " cancelAppointment ",
                                    " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                                    response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    }
                }
            } else {
                if (appointments.get(appointmentType).get(appointmentId).removeRegisteredUserId(userId)) {
                    response = "Success: Appointment " + appointmentId + " Canceled for " + userId;
                    try {
                        Logger.serverLog(serverID, userId, " cancelAppointment ",
                                " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                                response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    response = "Failed: You " + userId + " Are Not Registered in " + appointmentId;
                    try {
                        Logger.serverLog(serverID, userId, " cancelAppointment ",
                                " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                                response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            }
        } else {
            if (isUserOfThisServer(userId)) {
                if (checkClientExists(userId)) {
                    if (removeAppointmentIfExists(userId, appointmentType, appointmentId)) {
                        response = sendUDPMessage(getServerPort(appointmentId.substring(0, 3)), "cancelAppointment",
                                userId, appointmentType, appointmentId);
                        try {
                            Logger.serverLog(serverID, userId, " cancelAppointment ",
                                    " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ",
                                    response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return response;
                    }
                }
            }
            response = "Failed: You " + userId + " Are Not Registered in " + appointmentId;
            try {
                Logger.serverLog(serverID, userId, " cancelAppointment ",
                        " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ", response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    @WebMethod
    public String swapAppointmet(String userId, String newappointmentId, String newappointmentType,
            String oldappointmentId, String oldappointmentType) {
        String response;
        if (!checkClientExists(userId)) {
            response = "Failed: You " + userId + " Are Not Registered in " + oldappointmentId;
            try {
                Logger.serverLog(serverID, userId, " swapAppointmet ",
                        " oldappointmentId: " + oldappointmentId + " oldappointmentType: " + oldappointmentType
                                + " newappointmentId: " + newappointmentId + " newappointmentType: "
                                + newappointmentType + " ",
                        response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } else {
            if (userHasAppointment(userId, oldappointmentType, oldappointmentId)) {
                String bookResp = "Failed: did not send book request for your newEvent " + newappointmentId;
                String cancelResp = "Failed: did not send cancel request for your oldEvent " + oldappointmentId;
                synchronized (this) {
                    if (onTheSameWeek(newappointmentId.substring(4), oldappointmentId)
                            && !exceedWeeklyLimit(userId, newappointmentId.substring(4))) {
                        cancelResp = cancelAppointment(userId, oldappointmentId, oldappointmentType);
                        System.out.println("cancelResp" + cancelResp);
                        if (cancelResp.startsWith("Success:")) {
                            bookResp = bookAppointment(userId, newappointmentId, newappointmentType);
                            System.out.println("bookResp" + bookResp);
                        }
                    } else {
                        bookResp = bookAppointment(userId, newappointmentId, newappointmentType);
                        if (bookResp.startsWith("Success:")) {
                            cancelResp = cancelAppointment(userId, oldappointmentId, oldappointmentType);
                        }
                    }
                }
                if (bookResp.startsWith("Success:") && cancelResp.startsWith("Success:")) {
                    response = "Success: Appointment " + oldappointmentId + " swapped with " + newappointmentId;
                } else if (bookResp.startsWith("Success:") && cancelResp.startsWith("Failed:")) {
                    cancelAppointment(userId, newappointmentId, newappointmentType);
                    response = "Failed: Your oldEvent " + oldappointmentId + " Could not be Canceled reason: "
                            + cancelResp;
                } else if (bookResp.startsWith("Failed:") && cancelResp.startsWith("Success:")) {
                    // hope this won't happen, but just in case.
                    String resp1 = bookAppointment(userId, oldappointmentId, oldappointmentType);
                    response = "Failed: Your newEvent " + newappointmentId + " Could not be Booked reason: " + bookResp
                            + " And your old appointment Rolling back: " + resp1;
                } else {
                    response = "Failed: on Both newEvent " + newappointmentId + " Booking reason: " + bookResp
                            + " and oldEvent " + oldappointmentId + " Canceling reason: " + cancelResp;
                }
                try {
                    Logger.serverLog(serverID, userId, " swapAppointmet ",
                            " oldappointmentId: " + oldappointmentId + " oldappointmentType: " + oldappointmentType
                                    + " newappointmentId: " + newappointmentId + " newappointmentType: "
                                    + newappointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } else {
                response = "Failed: You " + userId + " Are Not Registered in " + oldappointmentId;
                try {
                    Logger.serverLog(serverID, userId, " swapAppointmet ",
                            " oldappointmentId: " + oldappointmentId + " oldappointmentType: " + oldappointmentType
                                    + " newappointmentId: " + newappointmentId + " newappointmentType: "
                                    + newappointmentType + " ",
                            response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }
        }
    }

    private synchronized boolean isAppointmentOfThisServer(String appointmentId) {
        return AppointmentModel.detectAppointmentServer(appointmentId).equals(this.serverName);
    }

    private synchronized boolean appointentExists(String appointmentType, String appointmentId) {
        return appointments.get(appointmentType).containsKey(appointmentId);
    }

    private String sendUDPMessage(int serverPort, String method, String userId, String appointmentType,
            String appointmentId) {
        DatagramSocket socket = null;
        String result = "";
        String dataFromClient = method + ";" + userId + ";" + appointmentType + ";" + appointmentId;
        try {
            Logger.serverLog(serverID, userId, " UDP request sent " + method + " ",
                    " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ", " ... ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), host, serverPort);
            socket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            socket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split(";");
            result = parts[0];
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (socket != null)
                socket.close();
        }
        try {
            Logger.serverLog(serverID, userId, " UDP reply received" + method + " ",
                    " appointmentId: " + appointmentId + " appointmentType: " + appointmentType + " ", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private synchronized boolean checkClientExists(String userId) {
        if (!serverClients.containsKey(userId)) {
            addNewCustomerToClients(userId);
            return false;
        } else {
            return true;
        }
    }

    public void addNewCustomerToClients(String userId) {
        UserModel newCustomer = new UserModel(userId);
        serverClients.put(newCustomer.getUserId(), newCustomer);
        userAppointments.put(newCustomer.getUserId(), new ConcurrentHashMap<>());
    }

    private synchronized boolean userHasAppointment(String userId, String appointmentType, String appointmentId) {
        if (userAppointments.get(userId).containsKey(appointmentType)) {
            return userAppointments.get(userId).get(appointmentType).contains(appointmentId);
        } else {
            return false;
        }
    }

    private boolean isUserOfThisServer(String userId) {
        return userId.substring(0, 3).equals(serverID);
    }

    private synchronized void addappointmentTypeAndBookAppointment(String userId, String appointmentType,
            String appointmentId) {
        List<String> temp = new ArrayList<>();
        temp.add(appointmentId);
        userAppointments.get(userId).put(appointmentType, temp);
    }

    private synchronized void addUserandAppointment(String userId, String appointmentType, String appointmentId) {
        Map<String, List<String>> temp = new ConcurrentHashMap<>();
        List<String> temp2 = new ArrayList<>();
        temp2.add(appointmentId);
        temp.put(appointmentType, temp2);
        userAppointments.put(userId, temp);
    }

    private boolean removeAppointmentIfExists(String userId, String appointmentType, String appointmentId) {
        if (userAppointments.get(userId).containsKey(appointmentType)) {
            return userAppointments.get(userId).get(appointmentType).remove(appointmentId);
        } else {
            return false;
        }
    }

    private static int getServerPort(String branchAcronym) {
        if (branchAcronym.equalsIgnoreCase(Constants.MONTREAL_SERVER)) {
            return Constants.MONTREAL_SERVER_PORT;
        } else if (branchAcronym.equalsIgnoreCase(Constants.SHERBROOKE_SERVER)) {
            return Constants.SHERBROOKE_SERVER_PORT;
        } else if (branchAcronym.equalsIgnoreCase(Constants.QUEBEC_SERVER)) {
            return Constants.QUEBEC_SERVER_PORT;
        }
        return 1;
    }

    private boolean exceedWeeklyLimit(String userId, String appointmentDate) {
        int limit = 0;
        for (int i = 0; i < 3; i++) {
            List<String> registeredIDs = new ArrayList<>();
            switch (i) {
                case 0:
                    if (userAppointments.get(userId).containsKey(Constants.PHYSICIAN)) {
                        registeredIDs = userAppointments.get(userId).get(Constants.PHYSICIAN);
                    }
                    break;
                case 1:
                    if (userAppointments.get(userId).containsKey(Constants.SURGEON)) {
                        registeredIDs = userAppointments.get(userId).get(Constants.SURGEON);
                    }
                    break;
                case 2:
                    if (userAppointments.get(userId).containsKey(Constants.DENTAL)) {
                        registeredIDs = userAppointments.get(userId).get(Constants.DENTAL);
                    }
                    break;
            }
            for (String appointmentId : registeredIDs) {
                if (onTheSameWeek(appointmentDate, appointmentId) && !isAppointmentOfThisServer(appointmentId)) {
                    limit++;
                }
                if (limit == 3)
                    return false;
            }
        }
        return true;
    }

    private boolean onTheSameWeek(String newAppointmentDate, String appointmentId) {
        if (appointmentId.substring(6, 8).equals(newAppointmentDate.substring(2, 4))
                && appointmentId.substring(8, 10).equals(newAppointmentDate.substring(4, 6))) {
            int week1 = Integer.parseInt(appointmentId.substring(4, 6)) / 7;
            int week2 = Integer.parseInt(newAppointmentDate.substring(0, 2)) / 7;
            return week1 == week2;
        } else {
            return false;
        }
    }

    /**
     * for udp calls only
     *
     * @param oldAppointmentId
     * @param appointmentType
     * @param userId
     * @return
     */
    public String removeEventUDP(String oldAppointmentId, String appointmentType, String userId) {
        if (!checkClientExists(userId)) {
            return "Failed: You " + userId + " Are Not Registered in " + oldAppointmentId;
        } else {
            if (removeAppointmentIfExists(userId, appointmentType, oldAppointmentId)) {
                return "Success: appointment " + oldAppointmentId + " Was Removed from " + userId + " Schedule";
            } else {
                return "Failed: You " + userId + " Are Not Registered in " + oldAppointmentId;
            }
        }
    }

    /**
     * for UDP calls only
     *
     * @param appointmentType
     * @return
     */
    public String listAppointmentAvailabilityUDP(String appointmentType) {
        Map<String, AppointmentModel> events = appointments.get(appointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName + " Server " + appointmentType + ":\n");
        if (events.size() == 0) {
            builder.append("No Events of Type " + appointmentType);
        } else {
            for (AppointmentModel appointment :
                    events.values()) {
                        builder.append(appointment.getAppointmentCapacity() + " || " + appointment.getAppointmentDate() + " || " + appointment.getAppointmentTimeSlot());
            }
        }
        builder.append("\n=====================================\n");
        return builder.toString();
    }



}

