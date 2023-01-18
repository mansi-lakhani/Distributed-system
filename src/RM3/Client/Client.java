package RM3.Client;

import RM3.Logger.Logger;
import RM3.utils.Constants;

import java.util.Scanner;

public class Client {
    public static final int USER_TYPE_PATIENT = 1;
    public static final int USER_TYPE_ADMIN = 2;
    public static final int CUSTOMER_BOOK_APPOINTMENT = 1;
    public static final int CUSTOMER_GET_BOOKING_SCHEDULE = 2;
    public static final int CUSTOMER_CANCEL_APPOINTMENT = 3;
    public static final int CUSTOMER_SWAP_APPOINTMENT = 4;
    public static final int CUSTOMER_LOGOUT = 5;
    public static final int ADMIN_ADD_APPOINTMENT = 1;
    public static final int ADMIN_REMOVE_APPOINTMENT = 2;
    public static final int ADMIN_LIST_APPOINTMENT_AVAILABILITY = 3;
    public static final int ADMIN_BOOK_APPOINTMENT = 4;
    public static final int ADMIN_GET_BOOKING_SCHEDULE = 5;
    public static final int ADMIN_CANCEL_APPOINTMENT = 6;
    public static final int ADMIN_SWAP_APPOINTMENT = 7;
    public static final int ADMIN_LOGOUT = 8;
    public static final int SHUTDOWN = 0;

    static Scanner input;

    public static MTLHealthCareImplService mtlimpl;
    public static SHEHealthCareImplService sheimpl;
    public static QUEHealthCareImplService queimpl;

    public static void main(String[] args) throws Exception {
        try {
            init();
        } catch (Exception e) {
            System.out.println("Client init exception: " + e);
            e.printStackTrace();
        }
    }

    public static void init() throws Exception {
        input = new Scanner(System.in);
        String userID;
        System.out.println("*************************************");
        System.out.println("Please Enter your UserID:");
        userID = input.next().trim().toUpperCase();
        Logger.clientLog(userID, " login attempt");
        switch (checkUserType(userID)) {
            case USER_TYPE_PATIENT:
                try {
                    System.out.println("Customer Login successful (" + userID + ")");
                    Logger.clientLog(userID, " Customer Login successful");
                    customer(userID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case USER_TYPE_ADMIN:
                try {
                    System.out.println("Admin Login successful (" + userID + ")");
                    Logger.clientLog(userID, " Admin Login successful");
                    manager(userID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("!!UserID is not in correct format");
                Logger.clientLog(userID, " UserID is not in correct format");
                Logger.deleteALogFile(userID);
                init();
        }
    }

    private static String askForPatientIDFromAdmin(String branchAcronym) {
        System.out.println("Please enter a userId(Within " + branchAcronym + " Server):");
        String userID = input.next().trim().toUpperCase();
        if (checkUserType(userID) != USER_TYPE_PATIENT || !userID.substring(0, 3).equals(branchAcronym)) {
            return askForPatientIDFromAdmin(branchAcronym);
        } else {
            return userID;
        }
    }

    private static void printMenu(int userType) {
        System.out.println("*************************************");
        System.out.println("Please choose an option below:");
        if (userType == USER_TYPE_PATIENT) {
            System.out.println("1.Book Appointment");
            System.out.println("2.Get Booking Schedule");
            System.out.println("3.Cancel Appointment");
            System.out.println("4.Swap Appointment");
            System.out.println("5.Logout");
            System.out.println("0.ShutDown");
        } else if (userType == USER_TYPE_ADMIN) {
            System.out.println("1.Add Appointment");
            System.out.println("2.Remove Appointment");
            System.out.println("3.List Appointment Availability");
            System.out.println("4.Book Appointment");
            System.out.println("5.Get Booking Schedule");
            System.out.println("6.Cancel Appointment");
            System.out.println("7.Swap Appointment");
            System.out.println("8.Logout");
            System.out.println("0.ShutDown");
        }
    }

    private static String promptForEventType() {
        System.out.println("*************************************");
        System.out.println("Please choose an appointmentType below:");
        System.out.println("1.Physician");
        System.out.println("2.Surgeon");
        System.out.println("3.Dental");
        switch (input.nextInt()) {
            case 1:
                return Constants.PHYSICIAN;
            case 2:
                return Constants.SURGEON;
            case 3:
                return Constants.DENTAL;
        }
        return promptForEventType();
    }

    private static String promptForEventID() {
        System.out.println("*************************************");
        System.out.println("Please enter the AppointmentId (e.g MTLM190120)");
        String eventID = input.next().trim().toUpperCase();
        if (eventID.length() == 10) {
            if (eventID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    eventID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    eventID.substring(0, 3).equalsIgnoreCase("QUE")) {
                if (eventID.substring(3, 4).equalsIgnoreCase("M") ||
                        eventID.substring(3, 4).equalsIgnoreCase("A") ||
                        eventID.substring(3, 4).equalsIgnoreCase("E")) {
                    return eventID;
                }
            }
        }
        return promptForEventID();
    }

    private static int promptForCapacity() {
        System.out.println("*************************************");
        System.out.println("Please enter the booking capacity:");
        return input.nextInt();
    }

    private static String getServerID(String userID) {
        String branchAcronym = userID.substring(0, 3);
        if (branchAcronym.equalsIgnoreCase("MTL")) {
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("SHE")) {
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("QUE")) {
            return branchAcronym;
        } else {
            return "1";
        }
    }

    private static int checkUserType(String userID) {
        if (userID.length() == 8) {
            if (userID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    userID.substring(0, 3).equalsIgnoreCase("QUE") ||
                    userID.substring(0, 3).equalsIgnoreCase("SHE")) {
                if (userID.substring(3, 4).equalsIgnoreCase("P")) {
                    return USER_TYPE_PATIENT;
                } else if (userID.substring(3, 4).equalsIgnoreCase("A")) {
                    return USER_TYPE_ADMIN;
                }
            }
        }
        return 0;
    }

    private static void customer(String userId) throws Exception {
        String serverID = getServerID(userId);
        if (serverID.equals("1")) {
            init();
        }
        if (serverID.equalsIgnoreCase("MTL")) {
            mtlimpl = new MTLHealthCareImplService();
        } else if (serverID.equalsIgnoreCase("SHE")) {
            sheimpl = new SHEHealthCareImplService();
        } else if (serverID.equalsIgnoreCase("QUE")) {
            queimpl = new QUEHealthCareImplService();
        }
        boolean repeat = true;
        printMenu(USER_TYPE_PATIENT);
        int menuSelection = input.nextInt();
        String appointmentType;
        String eventID;
        String serverResponse;
        switch (menuSelection) {
            case CUSTOMER_BOOK_APPOINTMENT:
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(userId, " attempting to bookAppointment");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().bookAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(userId, " bookAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().bookAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(userId, " bookAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().bookAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(userId, " bookAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case CUSTOMER_GET_BOOKING_SCHEDULE:
                Logger.clientLog(userId, " attempting to getBookingSchedule");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().getBookingSchedule(userId);
                    Logger.clientLog(userId, " bookAppointment", " null ", serverResponse);
                    System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().getBookingSchedule(userId);
                    Logger.clientLog(userId, " bookAppointment", " null ", serverResponse);
                    System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().getBookingSchedule(userId);
                    Logger.clientLog(userId, " bookAppointment", " null ", serverResponse);
                    System.out.println(serverResponse);
                }

                break;
            case CUSTOMER_CANCEL_APPOINTMENT:
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(userId, " attempting to cancelAppointment");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().cancelAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(userId, " bookAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().cancelAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(userId, " bookAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().cancelAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(userId, " bookAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case CUSTOMER_SWAP_APPOINTMENT:
                System.out.println("Please Enter the OLD event to be replaced");
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                System.out.println("Please Enter the NEW event to be replaced");
                String newAppointmentType = promptForEventType();
                String newEventID = promptForEventID();
                Logger.clientLog(userId, " attempting to swapAppointmet");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().swapAppointmet(userId, newEventID, newAppointmentType,
                            eventID, appointmentType);
                    Logger.clientLog(
                            userId, " swapAppointmet", " oldAppointmentId: " + eventID + " oldAppointmentType: " + appointmentType
                                    + " newEventID: " + newEventID + " newAppointmentType: " + newAppointmentType + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().swapAppointmet(userId, newEventID, newAppointmentType,
                            eventID, appointmentType);
                    Logger.clientLog(
                            userId, " swapAppointmet", " oldAppointmentId: " + eventID + " oldAppointmentType: " + appointmentType
                                    + " newEventID: " + newEventID + " newAppointmentType: " + newAppointmentType + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().swapAppointmet(userId, newEventID, newAppointmentType,
                            eventID, appointmentType);
                    Logger.clientLog(
                            userId, " swapAppointmet", " oldAppointmentId: " + eventID + " oldAppointmentType: " + appointmentType
                                    + " newEventID: " + newEventID + " newAppointmentType: " + newAppointmentType + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case CUSTOMER_LOGOUT:
                repeat = false;
                Logger.clientLog(userId, " attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            customer(userId);
        }
    }

    private static void manager(String eventManagerID) throws Exception {
        String serverID = getServerID(eventManagerID);
        if (serverID.equals("1")) {
            init();
        }
        if (serverID.equalsIgnoreCase("MTL")) {
            mtlimpl = new MTLHealthCareImplService();
        } else if (serverID.equalsIgnoreCase("SHE")) {
            sheimpl = new SHEHealthCareImplService();
        } else if (serverID.equalsIgnoreCase("QUE")) {
            queimpl = new QUEHealthCareImplService();
        }
        boolean repeat = true;
        printMenu(USER_TYPE_ADMIN);
        String userId;
        String appointmentType;
        String eventID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case ADMIN_ADD_APPOINTMENT:
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                capacity = promptForCapacity();
                Logger.clientLog(eventManagerID, " attempting to addAppointment");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().addAppointment(eventID, appointmentType,
                            capacity);
                    Logger.clientLog(eventManagerID, " addAppointment", " eventID: " + eventID + " appointmentType: "
                            + appointmentType + " eventCapacity: " + capacity + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().addAppointment(eventID, appointmentType,
                            capacity);
                    Logger.clientLog(eventManagerID, " addAppointment", " eventID: " + eventID + " appointmentType: "
                            + appointmentType + " eventCapacity: " + capacity + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().addAppointment(eventID, appointmentType,
                            capacity);
                    Logger.clientLog(eventManagerID, " addAppointment", " eventID: " + eventID + " appointmentType: "
                            + appointmentType + " eventCapacity: " + capacity + " ", serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case ADMIN_REMOVE_APPOINTMENT:
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to removeAppointment");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().removeAppointment(eventID, appointmentType);
                    Logger.clientLog(eventManagerID, " removeAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().removeAppointment(eventID, appointmentType);
                    Logger.clientLog(eventManagerID, " removeAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().removeAppointment(eventID, appointmentType);
                    Logger.clientLog(eventManagerID, " removeAppointment",
                            " eventID: " + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case ADMIN_LIST_APPOINTMENT_AVAILABILITY:
                appointmentType = promptForEventType();
                Logger.clientLog(eventManagerID, " attempting to listAppointmentAvailability");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().listAppointmentAvailability(appointmentType);
                    Logger.clientLog(eventManagerID, " listAppointmentAvailability",
                            " appointmentType: " + appointmentType + " ", serverResponse);
                    System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().listAppointmentAvailability(appointmentType);
                    Logger.clientLog(eventManagerID, " listAppointmentAvailability",
                            " appointmentType: " + appointmentType + " ", serverResponse);
                    System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().listAppointmentAvailability(appointmentType);
                    Logger.clientLog(eventManagerID, " listAppointmentAvailability",
                            " appointmentType: " + appointmentType + " ", serverResponse);
                    System.out.println(serverResponse);
                }

                break;
            case ADMIN_BOOK_APPOINTMENT:
                userId = askForPatientIDFromAdmin(eventManagerID.substring(0, 3));
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to bookAppointment");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().bookAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(eventManagerID, " bookAppointment", " userId: " + userId + " eventID: "
                            + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().bookAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(eventManagerID, " bookAppointment", " userId: " + userId + " eventID: "
                            + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().bookAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(eventManagerID, " bookAppointment", " userId: " + userId + " eventID: "
                            + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case ADMIN_GET_BOOKING_SCHEDULE:
                userId = askForPatientIDFromAdmin(eventManagerID.substring(0, 3));
                Logger.clientLog(eventManagerID, " attempting to getBookingSchedule");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().getBookingSchedule(userId);
                    Logger.clientLog(eventManagerID, " getBookingSchedule", " userId: " + userId + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().getBookingSchedule(userId);
                    Logger.clientLog(eventManagerID, " getBookingSchedule", " userId: " + userId + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().getBookingSchedule(userId);
                    Logger.clientLog(eventManagerID, " getBookingSchedule", " userId: " + userId + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case ADMIN_CANCEL_APPOINTMENT:
                userId = askForPatientIDFromAdmin(eventManagerID.substring(0, 3));
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to cancelAppointment");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().cancelAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(eventManagerID, " cancelAppointment", " userId: " + userId + " eventID: "
                            + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().cancelAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(eventManagerID, " cancelAppointment", " userId: " + userId + " eventID: "
                            + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().cancelAppointment(userId, eventID,
                            appointmentType);
                    Logger.clientLog(eventManagerID, " cancelAppointment", " userId: " + userId + " eventID: "
                            + eventID + " appointmentType: " + appointmentType + " ", serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case ADMIN_SWAP_APPOINTMENT:
                userId = askForPatientIDFromAdmin(eventManagerID.substring(0, 3));
                System.out.println("Please Enter the OLD event to be swapped");
                appointmentType = promptForEventType();
                eventID = promptForEventID();
                System.out.println("Please Enter the NEW event to be swapped");
                String newAppointmentType = promptForEventType();
                String newEventID = promptForEventID();
                Logger.clientLog(eventManagerID, " attempting to swapAppointmet");
                if (serverID.equalsIgnoreCase("MTL")) {
                    serverResponse = mtlimpl.getMTLHealthCareImplPort().swapAppointmet(userId, newEventID, newAppointmentType,
                            eventID, appointmentType);
                    Logger.clientLog(eventManagerID, " swapAppointmet",
                            " userId: " + userId + " oldAppointmentId: " + eventID + " oldAppointmentType: "
                                    + appointmentType
                                    + " newEventID: " + newEventID + " newAppointmentType: " + newAppointmentType + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("SHE")) {
                    serverResponse = sheimpl.getSHEHealthCareImplPort().swapAppointmet(userId, newEventID, newAppointmentType,
                            eventID, appointmentType);
                    Logger.clientLog(eventManagerID, " swapAppointmet",
                            " userId: " + userId + " oldAppointmentId: " + eventID + " oldAppointmentType: "
                                    + appointmentType
                                    + " newEventID: " + newEventID + " newAppointmentType: " + newAppointmentType + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                } else if (serverID.equalsIgnoreCase("QUE")) {
                    serverResponse = queimpl.getQUEHealthCareImplPort().swapAppointmet(userId, newEventID, newAppointmentType,
                            eventID, appointmentType);
                    Logger.clientLog(eventManagerID, " swapAppointmet",
                            " userId: " + userId + " oldAppointmentId: " + eventID + " oldAppointmentType: "
                                    + appointmentType
                                    + " newEventID: " + newEventID + " newAppointmentType: " + newAppointmentType + " ",
                            serverResponse);
                            System.out.println(serverResponse);
                }

                break;
            case ADMIN_LOGOUT:
                repeat = false;
                Logger.clientLog(eventManagerID, "attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            manager(eventManagerID);
        }
    }

}
