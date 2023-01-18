package RM3.DataModel;

import java.util.ArrayList;
import java.util.List;
import RM3.utils.Constants;

public class AppointmentModel {
    private String appointmentType;
    private String appointmentId;
    private String appointmentServer;
    private int appointmentCapacity;
    private String appointmentDate;
    private String appointmentTimeSlot;
    private List<String> registeredClients;

    public AppointmentModel(String appointmentId, String appointmentType, int appointmentCapacity) {
        this.appointmentId = appointmentId;
        this.appointmentType = appointmentType;
        this.appointmentCapacity = appointmentCapacity;
        this.appointmentTimeSlot = detectAppointmentTimeSlot(appointmentId);
        this.appointmentServer = detectAppointmentServer(appointmentId);
        this.appointmentDate = detectAppointmentDate(appointmentId);
        registeredClients = new ArrayList<>();
    }

    /**
     * @return String return the appointmentType
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * @param appointmentType the appointmentType to set
     */
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    /**
     * @return String return the appointmentId
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * @param appointmentId the appointmentId to set
     */
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * @return String return the appointmentServer
     */
    public String getAppointmentServer() {
        return appointmentServer;
    }

    /**
     * @param appointmentServer the appointmentServer to set
     */
    public void setAppointmentServer(String appointmentServer) {
        this.appointmentServer = appointmentServer;
    }

    /**
     * @return int return the appointmentCapacity
     */
    public int getAppointmentCapacity() {
        return appointmentCapacity;
    }

    /**
     * @param appointmentCapacity the appointmentCapacity to set
     */
    public void setAppointmentCapacity(int appointmentCapacity) {
        this.appointmentCapacity = appointmentCapacity;
    }

    /**
     * @return String return the appointmentDate
     */
    public String getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * @param appointmentDate the appointmentDate to set
     */
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * @return String return the appointmentTimeSlot
     */
    public String getAppointmentTimeSlot() {
        return appointmentTimeSlot;
    }

    /**
     * @param appointmentTimeSlot the appointmentTimeSlot to set
     */
    public void setAppointmentTimeSlot(String appointmentTimeSlot) {
        this.appointmentTimeSlot = appointmentTimeSlot;
    }

    /**
     * @return List<String> return the registeredClients
     */
    public List<String> getRegisteredClients() {
        return registeredClients;
    }

    /**
     * @param registeredClients the registeredClients to set
     */
    public void setRegisteredClients(List<String> registeredClients) {
        this.registeredClients = registeredClients;
    }

    public static String detectAppointmentTimeSlot(String appointmentId) {
        if (appointmentId.substring(3, 4).equalsIgnoreCase("M")) {
            return Constants.APPOINTMENT_MORNING;
        } else if (appointmentId.substring(3, 4).equalsIgnoreCase("A")) {
            return Constants.APPOINTMENT_AFTERNOON;
        } else {
            return Constants.APPOINTMENT_EVENING;
        }
    }

    public static String detectAppointmentServer(String appointmentId) {
        if (appointmentId.substring(0, 3).equalsIgnoreCase(Constants.MONTREAL_SERVER)) {
            return Constants.APPOINTMENT_SERVER_MONTREAL;
        } else if (appointmentId.substring(0, 3).equalsIgnoreCase(Constants.QUEBEC_SERVER)) {
            return Constants.APPOINTMENT_SERVER_QUEBEC;
        } else {
            return Constants.APPOINTMENT_SERVER_SHERBROOK;
        }
    }

    public static String detectAppointmentDate(String appointmentId) {
        System.out.println("appointmentId.substring(3, 5)" + appointmentId.substring(4, 6) + "/" + appointmentId.substring(6, 8) + "/"
        + appointmentId.substring(8, 10));
        return appointmentId.substring(4, 6) + "/" + appointmentId.substring(6, 8) + "/"
                + appointmentId.substring(8, 10);
    }

    public boolean isFull() {
        return getAppointmentCapacity() == registeredClients.size();
    }

    public int addRegisteredUserId(String registeredUserId) {
        if (!isFull()) {
            if (registeredClients.contains(registeredUserId)) {
                return Constants.ALREADY_REGISTERED;
            } else {
                registeredClients.add(registeredUserId);
                return Constants.ADD_SUCCESS;
            }
        } else {
            return Constants.EVENT_FULL;
        }
    }

    public boolean removeRegisteredUserId(String registeredClientID) {
        return registeredClients.remove(registeredClientID);
    }

}