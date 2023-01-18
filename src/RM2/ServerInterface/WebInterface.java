package RM2.ServerInterface;


import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface WebInterface {
    
   
	public String bookAppointment(String patientID, String appointmentID, String appointmentType);

	public String cancelappointment(String patientID, String appointmentID, String appointmentType);

	public String swapAppointment(String patientID, String newappointmentID, String newappointmentType,
			String oldappointmentID, String oldappointmentType);

	public String addAppointment(String appointmentID, String appointmentType, int capacity);

	public String removeAppointment(String appointmentID, String appointmentType);

	public String listAppointmentAvailability(String appointmentType);

	public String getBookingSchedule(String patientID);

}
