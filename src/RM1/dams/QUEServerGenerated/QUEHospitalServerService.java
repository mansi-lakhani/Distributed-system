
package RM1.dams.QUEServerGenerated;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "QUEHospitalServerService", targetNamespace = "http://server.dams.RM1/", wsdlLocation = "file:/C:/Users/mauli/Desktop/MACS/COMP%206231%20-%20Distributed%20System%20Design/Assignments/COMP%206231%20-%20DAMS%20Project/src/RM1/dams/server/QuebecWSDL.wsdl")
public class QUEHospitalServerService
    extends Service
{

    private final static URL QUEHOSPITALSERVERSERVICE_WSDL_LOCATION;
    private final static WebServiceException QUEHOSPITALSERVERSERVICE_EXCEPTION;
    private final static QName QUEHOSPITALSERVERSERVICE_QNAME = new QName("http://server.dams.RM1/", "QUEHospitalServerService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/Users/mauli/Desktop/MACS/COMP%206231%20-%20Distributed%20System%20Design/Assignments/COMP%206231%20-%20DAMS%20Project/src/RM1/dams/server/QuebecWSDL.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        QUEHOSPITALSERVERSERVICE_WSDL_LOCATION = url;
        QUEHOSPITALSERVERSERVICE_EXCEPTION = e;
    }

    public QUEHospitalServerService() {
        super(__getWsdlLocation(), QUEHOSPITALSERVERSERVICE_QNAME);
    }

    public QUEHospitalServerService(WebServiceFeature... features) {
        super(__getWsdlLocation(), QUEHOSPITALSERVERSERVICE_QNAME, features);
    }

    public QUEHospitalServerService(URL wsdlLocation) {
        super(wsdlLocation, QUEHOSPITALSERVERSERVICE_QNAME);
    }

    public QUEHospitalServerService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, QUEHOSPITALSERVERSERVICE_QNAME, features);
    }

    public QUEHospitalServerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public QUEHospitalServerService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns QUEHospitalServer
     */
    @WebEndpoint(name = "QUEHospitalServerPort")
    public QUEHospitalServer getQUEHospitalServerPort() {
        return super.getPort(new QName("http://server.dams.RM1/", "QUEHospitalServerPort"), QUEHospitalServer.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns QUEHospitalServer
     */
    @WebEndpoint(name = "QUEHospitalServerPort")
    public QUEHospitalServer getQUEHospitalServerPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://server.dams.RM1/", "QUEHospitalServerPort"), QUEHospitalServer.class, features);
    }

    private static URL __getWsdlLocation() {
        if (QUEHOSPITALSERVERSERVICE_EXCEPTION!= null) {
            throw QUEHOSPITALSERVERSERVICE_EXCEPTION;
        }
        return QUEHOSPITALSERVERSERVICE_WSDL_LOCATION;
    }

}