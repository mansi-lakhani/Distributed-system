
package RM3.Client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "SHEHealthCareImpl", targetNamespace = "http://Sherbrooke.Server/")
public interface SHEHealthCareImpl {


    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "addAppointment", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.AddAppointment")
    @ResponseWrapper(localName = "addAppointmentResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.AddAppointmentResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/addAppointmentRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/addAppointmentResponse")
    public String addAppointment(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        int arg2);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getBookingSchedule", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.GetBookingSchedule")
    @ResponseWrapper(localName = "getBookingScheduleResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.GetBookingScheduleResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/getBookingScheduleRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/getBookingScheduleResponse")
    public String getBookingSchedule(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg3
     * @param arg2
     * @param arg4
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "swapAppointmet", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.SwapEvent")
    @ResponseWrapper(localName = "swapEventResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.SwapEventResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/swapEventRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/swapEventResponse")
    public String swapAppointmet(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2,
        @WebParam(name = "arg3", targetNamespace = "")
        String arg3,
        @WebParam(name = "arg4", targetNamespace = "")
        String arg4);

    /**
     * 
     * @param arg0
     */
    @WebMethod
    @RequestWrapper(localName = "addNewCustomerToClients", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.AddNewCustomerToClients")
    @ResponseWrapper(localName = "addNewCustomerToClientsResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.AddNewCustomerToClientsResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/addNewCustomerToClientsRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/addNewCustomerToClientsResponse")
    public void addNewCustomerToClients(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "removeAppointment", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.RemoveAppointment")
    @ResponseWrapper(localName = "removeAppointmentResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.RemoveAppointmentResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/removeAppointmentRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/removeAppointmentResponse")
    public String removeAppointment(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "listAppointmentAvailability", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.ListAppointmentAvailability")
    @ResponseWrapper(localName = "listAppointmentAvailabilityResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.ListAppointmentAvailabilityResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/listAppointmentAvailabilityRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/listAppointmentAvailabilityResponse")
    public String listAppointmentAvailability(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "bookAppointment", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.BookAppointment")
    @ResponseWrapper(localName = "bookAppointmentResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.BookAppointmentResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/bookAppointmentRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/bookAppointmentResponse")
    public String bookAppointment(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "cancelAppointment", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.CancelAppointment")
    @ResponseWrapper(localName = "cancelAppointmentResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.CancelAppointmentResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/cancelAppointmentRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/cancelAppointmentResponse")
    public String cancelAppointment(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2);

    /**
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "removeEventUDP", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.RemoveEventUDP")
    @ResponseWrapper(localName = "removeEventUDPResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.RemoveEventUDPResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/removeEventUDPRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/removeEventUDPResponse")
    public String removeEventUDP(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        String arg1,
        @WebParam(name = "arg2", targetNamespace = "")
        String arg2);

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "listAppointmentAvailabilityUDP", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.ListAppointmentAvailabilityUDP")
    @ResponseWrapper(localName = "listAppointmentAvailabilityUDPResponse", targetNamespace = "http://Sherbrooke.Server/", className = "server.sherbrooke.ListAppointmentAvailabilityUDPResponse")
    @Action(input = "http://Sherbrooke.Server/SHEHealthCareImpl/listAppointmentAvailabilityUDPRequest", output = "http://Sherbrooke.Server/SHEHealthCareImpl/listAppointmentAvailabilityUDPResponse")
    public String listAppointmentAvailabilityUDP(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

}