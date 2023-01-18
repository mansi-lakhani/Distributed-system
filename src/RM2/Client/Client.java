package RM2.Client;

import RM2.DataModel.AppointmentModel;
import RM2.Interface.AdminInterface;
import RM2.Interface.AdminandPatientInterface;
import RM2.ServerInterface.WebInterface;
import RM2.datastorage.DataLogger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Scanner;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import javax.xml.ws.Service;

public class Client {
	public static final int USER_PATIENT = 1;
    public static final int USER_ADMIN = 2;
    public static final int PATIENT_BOOK_APPOINTMENT = 1;
    public static final int PATIENT_GET_BOOKING_SCHEDULE = 2;
    public static final int PATIENT_CANCEL_APPOINTMENT = 3;
    public static final int PATIENT_LOGOUT = 4;
    public static final int PATIENT_SWAP_APPOINTMENT = 5;
    public static final int ADMIN_ADD_APPOINTMENT = 1;
    public static final int ADMIN_REMOVE_APPOINTMENT = 2;
    public static final int ADMIN_LIST_APPOINTMENT_AVAILABILITY = 3;
    public static final int ADMIN_BOOK_APPOINTMENT = 4;
    public static final int ADMIN_GET_BOOKING_SCHEDULE = 5;
    public static final int ADMIN_CANCEL_APPOINTMENT = 6;
    public static final int ADMIN_LOGOUT = 7;
    public static final int ADMIN_SWAP_APPOINTMENT = 8;
    
    public static Service montrealService;
    public static Service sherbrookService;
    public static Service quebecService;
    private static WebInterface obj;
    static Scanner input;

    public static void main(String[] args) throws Exception {
        URL montrealURL = new URL("http://localhost:7644/montreal?wsdl");
        QName montrealQName = new QName("http://ServerInterface.RM2/", "AppointmentManagerService");
        montrealService = Service.create(montrealURL, montrealQName);

        URL quebecURL = new URL("http://localhost:7644/quebec?wsdl");
        QName quebecQName = new QName("http://ServerInterface.RM2/", "AppointmentManagerService");
        quebecService = Service.create(quebecURL, quebecQName);

        URL sherbrookURL = new URL("http://localhost:7644/sherbrook?wsdl");
        QName sherbrookQName = new QName("http://ServerInterface.RM2/", "AppointmentManagerService");
        sherbrookService = Service.create(sherbrookURL, sherbrookQName);
        init();
    }
    
   

   
    
    
    public static void init() throws Exception {
        input = new Scanner(System.in);
        String userID;
        System.out.println("*************************************");
        System.out.println("Enter the user ID");
        
        
        userID = input.next().trim().toUpperCase();
        if (userID.equalsIgnoreCase("ConTest")) {
            
        } else {
            DataLogger.clientLog(userID, " login attempt");
            switch (checkUserType(userID)) {
                case USER_PATIENT:
                    try {
                        System.out.println("Patient Login successful (" + userID + ")");
                        DataLogger.clientLog(userID, " Customer Login successful");
                        patient(userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case USER_ADMIN:
                    try {
                        System.out.println("Admin Login successful (" + userID + ")");
                        DataLogger.clientLog(userID, " Manager Login successful");
                        admin(userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("!!UserID is not in correct format");
                    DataLogger.clientLog(userID, " UserID is not in correct format");
                    DataLogger.deleteALogFile(userID);
                    init();
            }
        }
    }
    
    
    private static String getServerID(String userID) {
        String branchAcronym = userID.substring(0, 3);
        if (branchAcronym.equalsIgnoreCase("MTL")) {
            obj = montrealService.getPort(WebInterface.class);
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("SHE")) {
            obj = sherbrookService.getPort(WebInterface.class);
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("QUE")) {
            obj = quebecService.getPort(WebInterface.class);
            return branchAcronym;
        }
        return "1";
    }

    private static int checkUserType(String userID) 
    {
        if (userID.length() == 8) 
        {
            if (userID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    userID.substring(0, 3).equalsIgnoreCase("QUE") ||
                    userID.substring(0, 3).equalsIgnoreCase("SHE")) 
            {
                if (userID.substring(3, 4).equalsIgnoreCase("P")) 
                {
                    return USER_PATIENT;
                } else if (userID.substring(3, 4).equalsIgnoreCase("A")) 
                {
                    return USER_ADMIN;
                }
            }
        }
        return 0;
    }

    private static void patient(String patientID) throws Exception 
    {
    	String serverID = getServerID(patientID);
        if (serverID.equals("1")) {
            init();
        }
        boolean repeat = true;
        ConsoleDisplay(USER_PATIENT);
        int menuSelection = input.nextInt();
        String appointmentType;
        String appointmentID;
        String serverResponse;
        String newappointmentID;
		String newappointmentType;
		String oldappointmentID;
		String oldappointmentType;
        switch (menuSelection) 
        {
        
            case PATIENT_BOOK_APPOINTMENT:
                appointmentType = GetAppointmentTypeFromUser();
                appointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(patientID, " attempting to book an appointment");
                serverResponse = obj.bookAppointment(patientID, appointmentID, appointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(patientID, " bookAppointment", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
                
            case PATIENT_GET_BOOKING_SCHEDULE:
                DataLogger.clientLog(patientID, " attempting to getBookingSchedule");
                serverResponse = obj.getBookingSchedule(patientID);
                System.out.println(serverResponse);
                DataLogger.clientLog(patientID, " bookappointment", " null ", serverResponse);
                break;
                
            case PATIENT_CANCEL_APPOINTMENT:
                appointmentType = GetAppointmentTypeFromUser();
                appointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(patientID, " attempting to cancelappointment");
                serverResponse = obj.cancelappointment(patientID, appointmentID, appointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(patientID, " bookappointment", " appointmentID: " + appointmentID + " appointmentType: " + appointmentType + " ", serverResponse);
                break;
                
            case PATIENT_SWAP_APPOINTMENT:
                System.out.println("Please Enter the NEW appointment to be replaced");
                newappointmentType = GetAppointmentTypeFromUser();
                newappointmentID = GetAppointmentIDFromUser();
                System.out.println("Please Enter the OLD appointment to be replaced");
                oldappointmentType = GetAppointmentTypeFromUser();
                oldappointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(patientID, " attempting to swapAppointment");
                serverResponse = obj.swapAppointment(patientID, newappointmentID, newappointmentType, oldappointmentID, oldappointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(patientID, " swapAppointment", " oldAppointmentID: " + oldappointmentID + " oldEventType: " + oldappointmentType + " newEventID: " + newappointmentID + " newEventType: " + newappointmentType + " ", serverResponse);
                break;    
                
            case PATIENT_LOGOUT:
                repeat = false;
                DataLogger.clientLog(patientID, " attempting to Logout");
                init();
                break;
        }
        if (repeat) 
        {
            patient(patientID);
        }
    }

    private static void admin(String adminID) throws Exception 
    {
    	String serverID = getServerID(adminID);
        if (serverID.equals("1")) {
            init();
        }
        boolean repeat = true;
        ConsoleDisplay(USER_ADMIN);
        String PatientID;
        String AppointmentType;
        String AppointmentID;
        String serverResponse;
        String newappointmentID;
		String newappointmentType;
		String oldappointmentID;
		String oldappointmentType;
        int capacity;
        int ConsoleOptionSelected = input.nextInt();
        switch (ConsoleOptionSelected)
        {
        
            case ADMIN_ADD_APPOINTMENT:
                AppointmentType = GetAppointmentTypeFromUser();
                AppointmentID = GetAppointmentIDFromUser();
                capacity = GetAppointmentCapacityFromAdmin();
                DataLogger.clientLog(adminID, " attempting to add an appointment");
                serverResponse = obj.addAppointment(AppointmentID, AppointmentType, capacity);
                System.out.println(serverResponse);
                DataLogger.clientLog(adminID, " addappointment", " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " appointmentCapacity: " + capacity + " ", serverResponse);
                break;
                
            case ADMIN_REMOVE_APPOINTMENT:
                AppointmentType = GetAppointmentTypeFromUser();
                AppointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(adminID, " attempting to removeappointment");
                serverResponse = obj.removeAppointment(AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(adminID, " removeappointment", " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", serverResponse);
                break;
                
            case ADMIN_LIST_APPOINTMENT_AVAILABILITY:
                AppointmentType = GetAppointmentTypeFromUser();
                DataLogger.clientLog(adminID, " attempting to listappointmentAvailability");
                serverResponse = obj.listAppointmentAvailability(AppointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(adminID, " listappointmentAvailability", " appointmentType: " + AppointmentType + " ", serverResponse);
                break;
                
            case ADMIN_BOOK_APPOINTMENT:
                PatientID = GetPatientIDFromAdmin(adminID.substring(0, 3));
                AppointmentType = GetAppointmentTypeFromUser();
                AppointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(adminID, " attempting to bookappointment");
                serverResponse = obj.bookAppointment(PatientID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(adminID, " bookappointment", " PatientID: " + PatientID + " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", serverResponse);
                break;
                
            case ADMIN_GET_BOOKING_SCHEDULE:
                PatientID = GetPatientIDFromAdmin(adminID.substring(0, 3));
                DataLogger.clientLog(adminID, " attempting to getBookingSchedule");
                serverResponse = obj.getBookingSchedule(PatientID);
                System.out.println(serverResponse);
                DataLogger.clientLog(adminID, " getBookingSchedule", " patientID: " + PatientID + " ", serverResponse);
                break;
                
            case ADMIN_CANCEL_APPOINTMENT:
                PatientID = GetPatientIDFromAdmin(adminID.substring(0, 3));
                AppointmentType = GetAppointmentTypeFromUser();
                AppointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(adminID, " attempting to cancelappointment");
                serverResponse = obj.cancelappointment(PatientID, AppointmentID, AppointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(adminID, " cancelAppointment", " patientID: " + PatientID + " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", serverResponse);
                break;
                
            case ADMIN_SWAP_APPOINTMENT:
            	PatientID = GetPatientIDFromAdmin(adminID.substring(0, 3));
                System.out.println("Please Enter the NEW appointment to be replaced");
                newappointmentType = GetAppointmentTypeFromUser();
                newappointmentID = GetAppointmentIDFromUser();
                System.out.println("Please Enter the OLD appointment to be replaced");
                oldappointmentType = GetAppointmentTypeFromUser();
                oldappointmentID = GetAppointmentIDFromUser();
                DataLogger.clientLog(PatientID, " attempting to swapAppointment");
                serverResponse = obj.swapAppointment(PatientID, newappointmentID, newappointmentType, oldappointmentID, oldappointmentType);
                System.out.println(serverResponse);
                DataLogger.clientLog(PatientID, " swapAppointment", " oldAppointmentID: " + oldappointmentID + " oldEventType: " + oldappointmentType + " newEventID: " + newappointmentID + " newEventType: " + newappointmentType + " ", serverResponse);
                break;    
                    
                
            case ADMIN_LOGOUT:
                repeat = false;
                DataLogger.clientLog(adminID, "attempting to Logout");
                init();
                break;
        }
        if (repeat)
        {
            admin(adminID);
        }
    }

    private static String GetPatientIDFromAdmin(String str) 
    {
        System.out.println("Please enter a patientID(Within " + str + " RM2.Server):");
        String userID = input.next().trim().toUpperCase();
        if (checkUserType(userID) != USER_PATIENT || !userID.substring(0, 3).equals(str)) 
        {
            return GetPatientIDFromAdmin(str);
        } else
        {
            return userID;
        }
    }

    private static void ConsoleDisplay(int userType)
    {
        System.out.println("==================================");
        System.out.println("Please choose an option below:");
        
        if (userType == USER_PATIENT) 
            
        {
            System.out.println("1.Book an appointment?");
            System.out.println("2.Get your Booking Schedule?");
            System.out.println("3.Cancel an appointment?");
            System.out.println("4.Logout??");
            System.out.println("5.Swap Appointment??");
        } 
        
        else if (userType == USER_ADMIN)
        
        {
            System.out.println("1.Add appointments?");
            System.out.println("2.Remove an appointment?");
            System.out.println("3.List appointment Availability");
            System.out.println("4.Book an appointment?");
            System.out.println("5.Get the Booking Schedule");
            System.out.println("6.Cancel an appointment?");
            System.out.println("7.Logout??");
            System.out.println("8.SWAP APPOINTMENT??");
        }
    }

    private static String GetAppointmentTypeFromUser() 
    {
        System.out.println(" ");
        System.out.println("Please choose the type of appointment:");
        System.out.println("1.Physician");
        System.out.println("2.Surgeon");
        System.out.println("3.Dental");
        switch (input.nextInt()) 
        {
            case 1:
                return AppointmentModel.PHYSICIAN;
            case 2:
                return AppointmentModel.SURGEON;
            case 3:
                return AppointmentModel.DENTAL;
        }
        
        return GetAppointmentTypeFromUser();
    }

    private static String GetAppointmentIDFromUser() 
    
    {
        System.out.println(" ");
        System.out.println("Please enter the appointmentID (e.g MTLM190120)");
        String appointmentID = input.next().trim().toUpperCase();
        if (appointmentID.length() == 10) 
        {
            if (appointmentID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    appointmentID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    appointmentID.substring(0, 3).equalsIgnoreCase("QUE")) 
            {
                if (appointmentID.substring(3, 4).equalsIgnoreCase("M") ||
                        appointmentID.substring(3, 4).equalsIgnoreCase("A") ||
                        appointmentID.substring(3, 4).equalsIgnoreCase("E")) 
                {
                    return appointmentID;
                }
            }
        }
        
        return GetAppointmentIDFromUser();
    }

    private static int GetAppointmentCapacityFromAdmin() 
    
    {
        System.out.println(" ");
        System.out.println("Please enter the booking capacity:");
        return input.nextInt();
    }
}
