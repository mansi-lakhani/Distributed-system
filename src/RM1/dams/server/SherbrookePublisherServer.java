package RM1.dams.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.xml.ws.Endpoint;

import RM1.dams.model.AppointmentType;
import RM1.dams.model.Configuration;

public class SherbrookePublisherServer {
	private static SHEHospitalServer serverObj = null;
	private static String sherbrookeNaming = "http://localhost:8787/SHEServer";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Runnable sherbrookeServerRunnable = () -> {
				sherbrookeUDPServer();
			};

			Thread sherbrookeServerThread = new Thread(sherbrookeServerRunnable);
			sherbrookeServerThread.start();

			serverObj = new SHEHospitalServer();
			Endpoint.publish(sherbrookeNaming, serverObj);
			
			System.out.println("Sherbrooke server is running..");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void sherbrookeUDPServer() {
		// TODO Auto-generated method stub
		boolean running = true;
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(Configuration.SHE_LISTENER_PORT_NUMBER);

			while (running) {
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				String received = new String(packet.getData(), 0, packet.getLength()).trim();

				String parameters = received.split(":")[1];
				String response = "";
				if (received.startsWith("listAppointment")) {
					AppointmentType appointmentType = Configuration.MAP_OF_APPOINTMENT_TYPE.get(parameters);
					response = serverObj.sherbrookeListAppointmenOfType(appointmentType);
				} else if (received.startsWith("getSchedule")) {
					response = serverObj.sharebrookeAppointmentScheduleOfPatientId(parameters);
				} else if (received.startsWith("bookAppointment")) {
					String[] otherParameters = parameters.split(",");
					AppointmentType appointmentType = Configuration.MAP_OF_APPOINTMENT_TYPE.get(otherParameters[2]);
					response = serverObj.sherbrookeBookAppointment(otherParameters[0], otherParameters[1],
							appointmentType);
				} else if (received.startsWith("cancelSchedule")) {
					String[] otherParameters = parameters.split(",");
					response = serverObj.sherbrookeCancelAppointmentOfPatientId(otherParameters[0], otherParameters[1]);
				}

				buffer = response.getBytes();
				packet = new DatagramPacket(buffer, buffer.length, address, port);
				socket.send(packet);
			}
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
