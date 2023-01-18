package RM2.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminandPatientInterface extends Remote 

{
    
    String bookAppointment(String patientID, String appointmentID, String appointmentType) throws RemoteException;

    String getBookingSchedule(String patientID) throws RemoteException;

    String cancelappointment(String patientID, String appointmentID, String appointmentType) throws RemoteException;
     
    String swapAppointment(String patientID, String newAppointmentID, String newAppointmentType,
			String oldAppointmentID, String oldAppointmentType) throws RemoteException;
	
}
