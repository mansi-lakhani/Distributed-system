package Client;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import Configuration.ConfigApplication;
import FrontEnd.server.FEMethodInterface;
import FrontEnd.server.FEMethodInterfaceHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Client {
	private static String adminID = "MTLA0000";
	private static FEMethodInterface feMethodInt;

	public static void main(String[] args) throws Exception {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			init(ncRef);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init(NamingContextExt ncRef) throws Exception {
		Scanner scanner = new Scanner(System.in);
		String logFileName = adminID + ".log";
		boolean exit = false;
		
		feMethodInt = FEMethodInterfaceHelper.narrow(ncRef.resolve_str(getServerID(adminID)));

		while (!exit) {
			try {
				
				System.out.println("===========================================");
				System.out.println("                   MENU                    ");
				System.out.println("===========================================");
				System.out.println("1. Add Appointment");
				System.out.println("2. Remove Appointment");
				System.out.println("3. List Appointment Availability");
				System.out.println("4. Book Appointment");
				System.out.println("5. Get Appointment SChedule");
				System.out.println("6. Cancel Appointment");
				System.out.println("7. Swap Appointment");
				System.out.println("8. Exit");
				System.out.println("");

				System.out.print("Enter your choice > ");
				int choice = scanner.nextInt();

				String requestDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
						.format(Calendar.getInstance().getTime());

				if (choice == 1) {
					String appointmentType = getAppointmentType();
					String appointmentId = getAppointmentId();
					int capacity = getCapacity();
					
					String serverResponse = feMethodInt.addAppointment(adminID, appointmentId, appointmentType,
							capacity);

					System.out.println("SERVER: "+serverResponse);
					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(appointmentId);
					requestParameters.add(appointmentType.toString());
					requestParameters.add(capacity + "");

					boolean completed = serverResponse.contains("Succss");

					addRecordIntoLogFile(logFileName, requestDate, "Add appointment", requestParameters, completed,
							serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 2) {
					String appointmentType = getAppointmentType();
					String appointmentId = getAppointmentId();
					String serverResponse = feMethodInt.removeAppointment(adminID, appointmentId, appointmentType);

					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(appointmentId);
					requestParameters.add(appointmentType.toString());

					boolean completed = serverResponse.contains("Success");

					addRecordIntoLogFile(logFileName, requestDate, "Remove appointment", requestParameters, completed,
							serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 3) {
					String appointmentType = getAppointmentType();
					String serverResponse = feMethodInt.listAppointmentAvailability(adminID, appointmentType);

					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(appointmentType.toString());

					boolean completed = serverResponse.contains("Success");

					addRecordIntoLogFile(logFileName, requestDate, "List appointment", requestParameters, completed,
							serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 4) {
					String appointmentType = getAppointmentType();
					String appointmentId = getAppointmentId();

					String serverResponse = feMethodInt.bookAppointment(adminID, appointmentId, appointmentType);

					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(adminID);
					requestParameters.add(appointmentId);
					requestParameters.add(appointmentType.toString());

					boolean completed = serverResponse.contains("Success");

					addRecordIntoLogFile(logFileName, requestDate, "Book appointment by " + adminID, requestParameters,
							completed, serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 5) {
					String serverResponse = feMethodInt.getAppointmentSchedule(adminID);

					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(adminID);

					boolean completed = serverResponse.contains("Success");

					addRecordIntoLogFile(logFileName, requestDate, "Get Appointment Schedule", requestParameters,
							completed, serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 6) {
					String appointmentId = getAppointmentId();
					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(adminID);
					requestParameters.add(appointmentId);

					String serverResponse = feMethodInt.cancelAppointment(adminID, appointmentId);
					boolean completed = serverResponse.contains("Success");

					addRecordIntoLogFile(logFileName, requestDate, "Cancel Appointment Schedule", requestParameters,
							completed, serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 7) {
					System.out.println("OLD APPOINTMENT DETAILS");
					String oldAppointmentId = getAppointmentId();
					String oldAppointmentType = getAppointmentType();
					System.out.println("NEW APPOINTMENT ID");
					String newAppointmentId = getAppointmentId();
					String newAppointmentType = getAppointmentType();
					List<String> requestParameters = new ArrayList<String>();
					requestParameters.add(adminID);
					requestParameters.add(oldAppointmentId);
					requestParameters.add(oldAppointmentType.toString());
					requestParameters.add(newAppointmentId);
					requestParameters.add(newAppointmentType.toString());

					String serverResponse = feMethodInt.swapAppointment(adminID, oldAppointmentId, oldAppointmentType,
							newAppointmentId, newAppointmentType);
					boolean completed = serverResponse.contains("Success");

					addRecordIntoLogFile(logFileName, requestDate, "Swap Appointment Schedule", requestParameters,
							completed, serverResponse);

					System.out.println();
					System.out.println(serverResponse);
				} else if (choice == 8) {
					exit = true;
				} else {
					System.out.println("Not valid option. Try again.");
				}

			} catch (Exception e) {
				System.out.println("Not valid option. Try again.");
			}
		}
	}

	private static String getServerID(String userID) {
		return "FrontEnd";
	}

	private static int getCapacity() {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Capacity > ");
		int capacity = scanner.nextInt();

		return capacity;
	}

	private static String getAppointmentId() {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter appointmentID [(MTL|SHE|QUE)(A|E|M)(MMDDYY)] > ");
		String appointmentId = scanner.next();

		while (!appointmentId
				.matches("(MTL|SHE|QUE)(A|E|M)(0[1-9]|1[0-9]|2[0-9]|3[01])(0[1-9]|11|12)(0[1-9]|1[0-9]|2[0-2])")) {
			System.out.print("Invalid ID Try again. Enter appointmentID [(MTL|SHE|QUE)(A|E|M)(MMDDYY)] > ");
			appointmentId = scanner.next();
		}
		return appointmentId;
	}

	private static String getAppointmentType() {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		System.out.println("===========================================");
		System.out.println("            Appointment Type               ");
		System.out.println("===========================================");
		System.out.println("1. Physician");
		System.out.println("2. Surgeon");
		System.out.println("3. Dental");
		System.out.println("4. Exit");
		System.out.println("");

		System.out.print("Enter your choice > ");
		while (!exit) {

			int choice = scanner.nextInt();

			if (choice == 1) {
				return ConfigApplication.PHYSICIAN;
			} else if (choice == 2) {
				return ConfigApplication.SURGEON;
			} else if (choice == 3) {
				return ConfigApplication.DENTAL;
			} else {
				System.out.println("Try again. Enter your choice > ");
			}
		}

		return null;
	}

	public static void addRecordIntoLogFile(String fileName, String requestDate, String requestType,
			List<String> requestParameter, boolean completed, String response) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		try {

			File f = new File(ConfigApplication.ADMIN_LOG_FILE_PATH + fileName);
			if (!f.exists()) {
				new File(ConfigApplication.ADMIN_LOG_FILE_PATH).mkdirs();
				f.createNewFile();
			}

			fw = new FileWriter(ConfigApplication.ADMIN_LOG_FILE_PATH + fileName, true);
			bw = new BufferedWriter(fw);
			pw = new PrintWriter(bw);
			pw.println(requestDate + " | " + requestType + " | " + requestParameter.toString() + " | " + response
					+ " | " + completed);
			pw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				pw.close();
				bw.close();
				fw.close();
			} catch (IOException io) {
			}
		}

	}

	private static boolean verifyAdminDetails(String adminID) {
		// TODO Auto-generated method stub
		if (adminID.startsWith("MTLA") && adminID.length() == 8) {
			return true;
		}

		if (adminID.startsWith("QUEA") && adminID.length() == 8) {
			return true;
		}

		if (adminID.startsWith("SHEA") && adminID.length() == 8) {
			return true;
		}

		return false;
	}
}