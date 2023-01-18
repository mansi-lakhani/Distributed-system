package RM2.Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminInterface extends Remote 
{
    
    String addAppointment(String appointmentID, String appointmentType, int bookingCapacity) throws RemoteException;

    String removeAppointment(String appointmentID, String appointmentType) throws RemoteException;

    String listAppointmentAvailability(String appointmentType) throws RemoteException;
    
    String swapAppointment(String patientID, String newAppointmentID, String newAppointmentType,
			String oldAppointmentID, String oldAppointmentType) throws RemoteException;
   
}
