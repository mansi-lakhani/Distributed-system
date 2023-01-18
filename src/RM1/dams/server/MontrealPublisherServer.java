package RM1.dams.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.xml.ws.Endpoint;

import RM1.dams.model.AppointmentType;
import RM1.dams.model.Configuration;

public class MontrealPublisherServer {
	private static MTLHospitalServer serverObj = null;
	private static String montrealNaming = "http://localhost:8789/MTLServer";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			Runnable montrealServerRunnable = () -> {
				montrealUDPServer();
			};

			Thread montrealServerThread = new Thread(montrealServerRunnable);
			montrealServerThread.start();
			
			serverObj = new MTLHospitalServer();
			Endpoint.publish(montrealNaming, serverObj);
			
			System.out.println("Montreal server is running..");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void montrealUDPServer() {
		// TODO Auto-generated method stub
		boolean running = true;
		DatagramSocket socket = null;

		try {
			socket = new DatagramSocket(Configuration.MTL_LISTENER_PORT_NUMBER);

			while (running) {
				byte[] buffer = new byte[1024];

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				String received = new String(packet.getData(), 0, packet.getLength()).trim();
				System.out.println(received);
				String parameters = received.split(":")[1];
				String response = "";
				if (received.startsWith("listAppointment")) {
					AppointmentType appointmentType = Configuration.MAP_OF_APPOINTMENT_TYPE.get(parameters);
					response = serverObj.montrealListAppointmenOfType(appointmentType);
				} else if (received.startsWith("getSchedule")) {
					response = serverObj.montrealAppointmentScheduleOfPatientId(parameters);
				} else if (received.startsWith("bookAppointment")) {
					String[] otherParameters = parameters.split(",");
					AppointmentType appointmentType = Configuration.MAP_OF_APPOINTMENT_TYPE.get(otherParameters[2]);

					response = serverObj.montrealBookAppointment(otherParameters[0], otherParameters[1],
							appointmentType);
				} else if (received.startsWith("cancelSchedule")) {
					String[] otherParameters = parameters.split(",");
					response = serverObj.montrealCancelAppointmentOfPatientId(otherParameters[0], otherParameters[1]);
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
