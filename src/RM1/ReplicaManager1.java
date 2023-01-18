package RM1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import Configuration.ConfigApplication;
import OutputConfiguration.Message;
import RM1.dams.MTLServerGenerated.MTLHospitalServerService;
import RM1.dams.QUEServerGenerated.QUEHospitalServerService;
import RM1.dams.SHEServerGenerated.SHEHospitalServerService;

import RM1.dams.model.Configuration;
import RM1.dams.server.MontrealPublisherServer;
import RM1.dams.server.QuebecPublisherServer;
import RM1.dams.server.ServerRunner;
import RM1.dams.server.SherbrookePublisherServer;

public class ReplicaManager1 {
	public static ConcurrentHashMap<Integer, Message> concurrentMessageHashMap = new ConcurrentHashMap<Integer, Message>();
	public static Queue<Message> messageQueue = new ConcurrentLinkedDeque<Message>();

	public static int lastSequenceID = 1;
	private static boolean serversFlag = true;
	public static Thread startServersThread;

	private static MTLHospitalServerService mtlServerService = new MTLHospitalServerService();
	private static QUEHospitalServerService queServerService = new QUEHospitalServerService();
	private static SHEHospitalServerService sheServerService = new SHEHospitalServerService();

	public static void main(String[] args) throws Exception {
		Runnable receiveFETask = () -> {
			try {
				receiveResponseFromSequencer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread receiveFEThead = new Thread(receiveFETask);
		receiveFEThead.start();

		Runnable startServers = () -> {
			try {
				ServerRunner.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		startServersThread = new Thread(startServers);
		startServersThread.start();

	}

	private static void receiveResponseFromSequencer() throws Exception {
		MulticastSocket socket = null;
		try {
			Runnable executeRequests = () -> {
				try {
					executeAllRequests();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
			Thread executeRequestsThread = new Thread(executeRequests);
			executeRequestsThread.start();

			socket = new MulticastSocket(ConfigApplication.MULTICAST_PORT_FE_TO_RM);
			socket.joinGroup(InetAddress.getByName(ConfigApplication.MULTICAST_IP_SEQUENCER_TO_RM));

			byte[] buffer = new byte[1024];
			System.out.println("RM1 Muticast UDP Server Started on port " + ConfigApplication.MULTICAST_PORT_FE_TO_RM);

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);

				byte[] data = request.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				try {
					Message receivedMessage = (Message) is.readObject();
					System.out.println("Message object received from Sequencer to RM1 = " + receivedMessage);

					if (receivedMessage.getMessageType().equalsIgnoreCase(ConfigApplication.MESSAGE)) {
						messageProcessRequest(receivedMessage);
					} else if (receivedMessage.getMessageType().equalsIgnoreCase(ConfigApplication.SYNCREQUEST)) {
						syncMessage(receivedMessage);
					} else if (receivedMessage.getMessageType().equalsIgnoreCase(ConfigApplication.INITRM)) {
//						initial_send_list(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]);
					} else if (receivedMessage.getMessageType().equalsIgnoreCase(ConfigApplication.RM1_DOWN)) {
						reloadRM1Down();
					}

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	private static void reloadRM1Down() {
		// TODO Auto-generated method stub
		Runnable crash_task = () -> {
			try {
				serversFlag = false;
				startServersThread.stop();
				
				System.out.println("RM1 shutdown all Server");
				Thread.sleep(5000);

				System.out.println("RM1 is reloading all server Database");
				reloadServers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		Thread handleThread = new Thread(crash_task);
		handleThread.start();
		try {
			handleThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("RM1 handled the crash!");
		serversFlag = true;

	}

	private static void messageProcessRequest(Message receivedMessage) {
		// TODO Auto-generated method stub
		Message sendRMMessage = new Message(receivedMessage);

		sendRMMessage.setMessageType(ConfigApplication.SYNCREQUEST);
		sendMulticastRMtoRM(sendRMMessage);

		if (receivedMessage.getSequenceId() - lastSequenceID > 1) {
			Message initial_message = new Message(0, "Null", ConfigApplication.INITRM, Integer.toString(lastSequenceID),
					Integer.toString(receivedMessage.getSequenceId()), "RM1", "Null", "Null", "Null", 0);
			System.out.println("RM1 send request to update its message list. from:" + lastSequenceID + "To:"
					+ receivedMessage.getSequenceId());

			sendMulticastRMtoRM(initial_message);
		}

		System.out.println("is adding queue:" + receivedMessage + "|| lastSequence>>>" + lastSequenceID);
		messageQueue.add(receivedMessage);
		concurrentMessageHashMap.put(receivedMessage.getSequenceId(), receivedMessage);
	}

	private static void syncMessage(Message receivedMessage) {
		// TODO Auto-generated method stub
		if (!concurrentMessageHashMap.containsKey(receivedMessage.getSequenceId())) {
			concurrentMessageHashMap.put(receivedMessage.getSequenceId(), receivedMessage);
		}
	}

	private static void sendMulticastRMtoRM(Message message) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(message);

			byte[] data = outputStream.toByteArray();

			InetAddress aHost = InetAddress.getByName(ConfigApplication.UNICAST_IP_RM_TO_FE);

			DatagramPacket request = new DatagramPacket(data, data.length, aHost,
					ConfigApplication.MULTICAST_PORT_RM_TO_RM);
			socket.send(request);

			System.out.println("Multicaste message from RM1 to other replica managers:" + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void executeAllRequests() throws Exception {

		while (true) {
			synchronized (ReplicaManager1.class) {
				Iterator<Message> itr = messageQueue.iterator();
				while (itr.hasNext()) {
					Message data = itr.next();

					if (data.getSequenceId() == lastSequenceID && serversFlag) {
						System.out.println("RM1 is executing message request. Detail:" + data);
						String response = requestToServers(data);
						data.setResponse(response);
						data.setRMNumber("RM1");

						System.out.println("SENT MESSAGE TO FE:: " + data);
						lastSequenceID += 1;
						sendMessageToFE(data);
						itr.remove();
					}
				}

			}
		}
	}

	private static String requestToServers(Message message) throws Exception {

		System.out.println("REQUEST TO SERVER RM1:: " + message);
		System.out.println("=====================================");

		if (message.userID.substring(3, 4).equalsIgnoreCase("A")) {
			if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.ADD_APPOINTMENT)) {

				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().addAppointment(message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getBookingCapacity());
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().addAppointmentQUE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getBookingCapacity());
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().addAppointmentSHE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getBookingCapacity());
				}
				System.out.println(response);
				return response;
			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.REMOVE_APPOINTMENT)) {

				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().removeAppointment(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().removeAppointmentQUE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().removeAppointmentSHE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				}
				System.out.println(response);
				return response;
			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.LIST_APPOINTMENT)) {
				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().listAppointmentAvailability(
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().listAppointmentAvailabilityQUE(
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().listAppointmentAvailabilitySHE(
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				}
				System.out.println(response);
				return response;

			}
		} else if (message.userID.substring(3, 4).equalsIgnoreCase("P")) {
			if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.ADD_APPOINTMENT)) {

				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().addAppointment(message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getBookingCapacity());
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().addAppointmentQUE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getBookingCapacity());
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().addAppointmentSHE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getBookingCapacity());
				}
				System.out.println(response);
				return response;
			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.REMOVE_APPOINTMENT)) {

				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().removeAppointment(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().removeAppointmentQUE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().removeAppointmentSHE(
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				}
				System.out.println(response);
				return response;
			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.LIST_APPOINTMENT)) {
				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().listAppointmentAvailability(
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().listAppointmentAvailabilityQUE(
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().listAppointmentAvailabilitySHE(
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				}
				System.out.println(response);
				return response;

			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.BOOK_APPOINTMENT)) {
				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().bookAppointment(message.userID,
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().bookAppointmentQUE(message.userID,
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().bookAppointmentSHE(message.userID,
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()));
				}
				System.out.println(response);
				return response;

			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.GET_APPOINTMENT)) {
				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().getAppointmentSchedule(message.userID);
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().getAppointmentScheduleQUE(message.userID);
					Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType());
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().getAppointmentScheduleSHE(message.userID);
				}
				System.out.println(response);
				return response;
			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.CANCEL_APPOINTMENT)) {
				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().cancelAppointment(message.userID,
							message.getOldAppointmentId());
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().cancelAppointmentQUE(message.userID,
							message.getOldAppointmentId());
					Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType());
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().cancelAppointmentSHE(message.userID,
							message.getOldAppointmentId());
				}
				System.out.println(response);
				return response;
			} else if (message.getMethodCall().equalsIgnoreCase(ConfigApplication.SWAP_APPOINTMENT)) {
				String response = "";
				if (message.getUserID().substring(0, 3).equals("MTL")) {
					response = mtlServerService.getMTLHospitalServerPort().swapAppointment(message.userID,
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getNewAppointmentID(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getNewAppointmentType()));
				} else if (message.getUserID().substring(0, 3).equals("QUE")) {
					response = queServerService.getQUEHospitalServerPort().swapAppointmentQUE(message.userID,
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getNewAppointmentID(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getNewAppointmentType()));
					Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType());
				} else if (message.getUserID().substring(0, 3).equals("SHE")) {
					response = sheServerService.getSHEHospitalServerPort().swapAppointmentSHE(message.userID,
							message.getOldAppointmentId(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getOldAppointmentType()),
							message.getNewAppointmentID(),
							Configuration.MAP_OF_APPOINTMENT_TYPE.get(message.getNewAppointmentType()));
				}
				System.out.println(response);
				return response;
			}
		}
		return "Not correct method call for " + message.userID.substring(0, 3);
	}

	public static void sendMessageToFE(Message message) {
		System.out.println("Message sent to FE:" + message);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(ConfigApplication.RM_PORT);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(message);

			byte[] data = outputStream.toByteArray();

			InetAddress aHost = InetAddress.getByName(ConfigApplication.UNICAST_IP_RM_TO_FE);

			DatagramPacket request = new DatagramPacket(data, data.length, aHost,
					ConfigApplication.UNICAST_PORT_RM_TO_FE);
			socket.send(request);

			System.out.println("Message sent from RM1 to FE: " + message);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}

	}

	public static void reloadServers() throws Exception {
		startServersThread.start();
		
		for (ConcurrentHashMap.Entry<Integer, Message> entry : concurrentMessageHashMap.entrySet()) {
			System.out.println("Recovery Mode in RM1 is executing" + entry.getValue().toString());
			requestToServers(entry.getValue());
			if (entry.getValue().sequenceId >= lastSequenceID) {
				lastSequenceID = entry.getValue().getSequenceId() + 1;
			}
		}
		messageQueue.clear();
	}
}