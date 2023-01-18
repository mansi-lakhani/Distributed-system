package OutputConfiguration;

public class RMResponse {

	public static final String ADDAPPOINTMENT_INVALID_APPOINTMENTID = "Invalid appointment Id";
	public static final String ADDAPPOINTMENT_SUCCESSFULL = "Appointment added successfully";
	public static final String ADDAPPOINTMENT_ALREADY_PRESENT = "Already present Appintment id and Appointment type";
	public static final String REMOVEAPPOINTMENT_FAILED = "No appointment is available with given data";
	public static final String REMOVEAPPOINTMENT_SUCCESSFULL = "Appointment removed successfully";
	public static final String BOOKAPPOINTMENT_SUCCESSFULL = "Appointment booked successfully";
	public static final String BOOKAPPOINTMENT_WEEKLY_LIMIT = "Appointment book Weekly limit reached";
	public static final String CANCELAPPOINTMENT_FAILED = "Cancel appointment failed";
	public static final String CANCELAPPOINTMENT_SUCCESS = "Cancel appointment successfull";
	public static final String SWAPAPPOINTMENT_SUCCESSFULL = "Appointment swapped successfully";
	public static final String SWAPAPPOINTMENT_FAILED = "Failed swap appointment";

	public static String sendResponse(boolean success, String methodName, String output) {
		if (success) {
			return "Success " + methodName + ": " + output;
		}
		else {
			return "Failed " + methodName + ": " + output;
		}
	}

}
