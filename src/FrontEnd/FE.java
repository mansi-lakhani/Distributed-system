package FrontEnd;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import Configuration.ConfigApplication;
import FrontEnd.server.FEMethodInterface;
import FrontEnd.server.FEMethodInterfaceHelper;
import OutputConfiguration.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class FE {

	public static void main(String[] args) {
		try {
			FEImplementation servant = new FEImplementation();

			Runnable task = () -> {
				listenForUDPResponses(servant);
			};
			Thread thread = new Thread(task);
			thread.start();

			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			servant.setORB(orb);

			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);
			FEMethodInterface href = FEMethodInterfaceHelper.narrow(ref);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			NameComponent[] path = ncRef.to_name("FrontEnd");
			ncRef.rebind(path, href);

			System.out.println("FrontEnd Server is Running..");

			while (true) {
				orb.run();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

	public static int sendUnicastToSequencer(Message requestFromClient) {
		
		System.out.println("FE: SEND UNICAST");
		DatagramSocket socket = null;
		int sequenceID = 0;

		try {
			socket = new DatagramSocket();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(requestFromClient);

			byte[] data = outputStream.toByteArray();

			InetAddress host = InetAddress.getLocalHost();

			DatagramPacket request = new DatagramPacket(data, data.length, host,
					ConfigApplication.UNICAST_PORT_FE_TO_SEQUENCER);
			socket.send(request);
			socket.setSoTimeout(ConfigApplication.TIMEOUT_MSECOND);

			System.out.println("Message sent from FE to Sequencer: " + requestFromClient);

			DatagramPacket response = new DatagramPacket(data, data.length);
			socket.receive(response);
			
			byte[] rdata = response.getData();
			ByteArrayInputStream in = new ByteArrayInputStream(rdata);
			ObjectInputStream is = new ObjectInputStream(in);

			Message receivedMessage = (Message) is.readObject();
			System.out.println("Message object received to FE: " + receivedMessage);
			
			return receivedMessage.getSequenceId();

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return sequenceID;
	}

	public static void sendMulticastFaultMessageToRms(Message message) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(message);

			byte[] data = outputStream.toByteArray();

			InetAddress aHost = InetAddress.getByName(ConfigApplication.MULTICAST_IP_SEQUENCER_TO_RM);

			DatagramPacket request = new DatagramPacket(data, data.length, aHost,
					ConfigApplication.MULTICAST_PORT_SEQUENCER_TO_RM);
			socket.send(request);

			System.out.println("Fault Message multicast to RMs.");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void listenForUDPResponses(FEImplementation servant) {
		DatagramSocket socket = null;
		try {

			InetAddress address = InetAddress.getByName(ConfigApplication.FE_IP);
			socket = new DatagramSocket(ConfigApplication.FE_PORT, address);
			byte[] buffer = new byte[1000];
			System.out.println("Frontend Started..");

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);

				byte[] data = request.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				try {
					Message receivedMessage = (Message) is.readObject();
					System.out.println("Received Message: " + receivedMessage);

					System.out.println("Adding response to FrontEndImplementation:");
					servant.addReceivedResponse(receivedMessage);

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
			e.printStackTrace();
		}
	}
}