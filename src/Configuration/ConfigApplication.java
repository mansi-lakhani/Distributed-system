package Configuration;

public class ConfigApplication {
	public final static int TIMEOUT_MSECOND = 1000;
	
	
	/**
	 * IP and Port of All Servers and Client
	 */
	public final static String FE_IP = "localhost";
	public final static int FE_PORT = 9999;
	
	public final static String SEQUENCER_IP = "192.168.2.17";
	public final static int SEQUENCER_PORT = 1333;
	
	public final static String MULTICAST_IP_SEQUENCER_TO_RM = "230.1.1.10";
	public final static int MULTICAST_PORT_SEQUENCER_TO_RM = 56942;
	
	public final static String UNICAST_IP_FE_TO_SEQUENCER = "192.168.2.17";
	public final static int UNICAST_PORT_FE_TO_SEQUENCER = 1333;
	
	public final static String MULTICAST_IP_FE_TO_RM = "230.1.1.10";
	public final static int MULTICAST_PORT_FE_TO_RM = 56942;

	public final static int UNICAST_PORT_RM_TO_FE = 9999;
	public final static String UNICAST_IP_RM_TO_FE = "localhost";

	public final static String MULTICAST_IP_RM_TO_RM = "232.224.202.171";
	public final static int MULTICAST_PORT_RM_TO_RM = 1235;
	
	public final static String RM_IP = "192.168.2.17";
	public final static int RM_PORT = 3698;
	

	/**
	 * Different types of appointment
	 */
	public final static String PHYSICIAN = "PHYSICIAN";
	public final static String SURGEON = "SURGEON";
	public final static String DENTAL = "DENTAL";

	
	/**
	 * Message Type in Message Object
	 */
	public final static String MESSAGE = "Message";
	public final static String SYNCREQUEST = "SyncRequest";
	public final static String INITRM = "Init RM";
	public final static String RM1_BUG = "RM1Bug";
	public final static String RM2_BUG = "RM2Bug";
	public final static String RM3_BUG = "RM3Bug";

	public final static String RM1_DOWN = "RM1DOWN";
	public final static String RM2_DOWN = "RM2DOWN";
	public final static String RM3_DOWN = "RM3DOWN";

	/**
	 * Method name
	 */
	public final static String ADD_APPOINTMENT = "AddAppointment";
	public final static String REMOVE_APPOINTMENT = "RemoveAppointment";
	public final static String LIST_APPOINTMENT = "ListAppointment";
	public final static String BOOK_APPOINTMENT = "BookAppointment";
	public final static String GET_APPOINTMENT = "GetAppointment";
	public final static String CANCEL_APPOINTMENT = "CancelAppointment";
	public final static String SWAP_APPOINTMENT = "SwapAppointment";
	
	

	/**
	 * Path
	 */
	public final static String LOG_FILE_PATH = "logs/servers/";
	public final static String ADMIN_LOG_FILE_PATH = "logs/admin/";
	public final static String PATIENT_LOG_FILE_PATH = "logs/patient/";
}
