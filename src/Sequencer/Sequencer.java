package Sequencer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import Configuration.ConfigApplication;
import OutputConfiguration.Message;

public class Sequencer {
	private static int sequencerId = 0;

	public static void main(String[] args) {
		DatagramSocket socket = null;
		try {			
			socket = new DatagramSocket(ConfigApplication.SEQUENCER_PORT);

			byte[] data = new byte[1000];
			System.out.println("Sequencer UDP Server Started..");

			while (true) {
				DatagramPacket request = new DatagramPacket(data, data.length);
				socket.receive(request);

				data = request.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);

				Message receivedMessage = (Message) is.readObject();

				System.out.println("Received Message: " + receivedMessage);
				receivedMessage = sendMessageObject(receivedMessage);

				ByteArrayOutputStream b = new ByteArrayOutputStream();
				ObjectOutputStream o = new ObjectOutputStream(b);
				o.writeObject(receivedMessage);

				byte[] responseData = b.toByteArray();
				InetAddress host = request.getAddress();
				int port = request.getPort();

				System.out.println("Sent back to FE: " + receivedMessage);
				DatagramPacket response = new DatagramPacket(responseData, responseData.length, host, port);
				socket.send(response);
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("ClassNotFound: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	public static Message sendMessageObject(Message message) {
		if (message.getMessageType().equals(ConfigApplication.MESSAGE) && message.getSequenceId() <= 0) {
			Sequencer.sequencerId = Sequencer.sequencerId + 1;
			message.setSequenceId(Sequencer.sequencerId);
		}
		
		System.out.println("Send Response Multicast: " + message);
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();

			InetAddress host = InetAddress.getByName(ConfigApplication.MULTICAST_IP_SEQUENCER_TO_RM);

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(message);

			byte[] data = b.toByteArray();

			DatagramPacket request = new DatagramPacket(data, data.length, host,
					ConfigApplication.MULTICAST_PORT_SEQUENCER_TO_RM);
			socket.send(request);
			
			
			return message;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
}