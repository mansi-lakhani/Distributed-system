package FrontEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.omg.CORBA.ORB;

import Configuration.ConfigApplication;
import FrontEnd.server.FEMethodInterfacePOA;
import OutputConfiguration.Message;

public class FEImplementation extends FEMethodInterfacePOA implements FEInterface {
	private ORB orb;

	private final static int REPLICA_COUNT = 3;

	private static long DYNAMIC_TIMEOUT = 10000;
	private static int Rm1NoResponseCount = 0;
	private static int Rm2NoResponseCount = 0;
	private static int Rm3NoResponseCount = 0;
	private long responseTime = DYNAMIC_TIMEOUT;
	private long startTime;
	private CountDownLatch latch;
	private List<Message> responses = new ArrayList<>();

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	public FEImplementation() {
		super();
	}

	@Override
	public void sendRMBug(int replicaNumber) {
		// TODO Auto-generated method stub
		System.out.println("RM" + replicaNumber + " has a bug");

		Message errorMessage = new Message(replicaNumber, ConfigApplication.RM1_BUG);
		FE.sendUnicastToSequencer(errorMessage);
	}

	@Override
	public void sendRMDown(int replicaNumber) {
		// TODO Auto-generated method stub
		System.out.println("RM" + replicaNumber + " is a down");

		if (replicaNumber == 1) {
			Message errorMessage = new Message(replicaNumber, ConfigApplication.RM1_DOWN);
			FE.sendUnicastToSequencer(errorMessage);
		} else if (replicaNumber == 2) {
			Message errorMessage = new Message(replicaNumber, ConfigApplication.RM2_DOWN);
			FE.sendUnicastToSequencer(errorMessage);
		} else if (replicaNumber == 3) {
			Message errorMessage = new Message(replicaNumber, ConfigApplication.RM3_DOWN);
			FE.sendUnicastToSequencer(errorMessage);
		}
	}

	@Override
	public int sendRequestToSequencer(Message myRequest) {
		// TODO Auto-generated method stub

		return FE.sendUnicastToSequencer(myRequest);
	}

	@Override
	public void sendRetryRequest(Message myRequest) {
		// TODO Auto-generated method stub
		System.out.println("No Response: retry request");
		FE.sendUnicastToSequencer(myRequest);
	}

	@Override
	public String addAppointment(String patientId, String appointmentID, String appointmentType, int capacity) {
		// TODO Auto-generated method stub
		System.out.println("ADD APPOINTMENT CALLED");
		Message message = new Message();
		message.setUserID(patientId);
		message.setOldAppointmentId(appointmentID);
		message.setOldAppointmentType(appointmentType);
		message.setBookingCapacity(capacity);
		message.setMethodCall(ConfigApplication.ADD_APPOINTMENT);
		message.setMessageType(ConfigApplication.MESSAGE);
		message.setSequenceId(sendUdpUnicastToSequencer(message));

		System.out.println("Add Appointment: " + message);
		return validateResponses(message);
	}

	private String validateResponses(Message myRequest) {
		String resp = "";
		int count = (int) latch.getCount();
		if (count >= 0 && count <= 2) {
			resp = findMajorityResponse(myRequest);
		} else if (count == REPLICA_COUNT) {
			resp = "Fail: No response from any server";
			System.out.println(resp);
			if (myRequest.haveRetries()) {
				myRequest.countRetry();
				resp = retryRequest(myRequest);
			}
			
			for (int i = 1; i < REPLICA_COUNT; i++) {
				checkIfRMDown(i);
			}
		}

		return resp;
	}

	private String retryRequest(Message myRequest) {
		System.out.println("FE Implementation:retryRequest>>>" + myRequest.toString());
		startTime = System.nanoTime();
		sendRequestToSequencer(myRequest);
		latch = new CountDownLatch(REPLICA_COUNT);
		waitForResponse();
		return validateResponses(myRequest);
	}

	private String findMajorityResponse(Message myRequest) {
		Message res1 = null;
		Message res2 = null;
		Message res3 = null;

		System.out.println("RESPONSES:: " + responses);

		for (Message response : responses) {
			if (response.getSequenceId() == myRequest.getSequenceId()) {
				if (response.getRMNumber() == 1) {
					res1 = response;
				}
				if (response.getRMNumber() == 2) {
					res2 = response;
				}
				if (response.getRMNumber() == 3) {
					res3 = response;
				}

			}
		}

		if (res1 == null) {
			checkIfRMDown(1);
		} else {
			Rm1NoResponseCount = 0;
			return res1.getResponse();
		}

		if (res2 == null) {
			checkIfRMDown(2);
		} else {
			Rm2NoResponseCount = 0;
			return res2.getResponse();
		}

		if (res3 == null) {
			checkIfRMDown(3);
		} else {
			Rm3NoResponseCount = 0;
			return res3.getResponse();
		}

		return "Failed: No Response found";
	}

	private void checkIfRMDown(int rmNumber) {
		DYNAMIC_TIMEOUT = 5000;
		if (rmNumber == 1) {
			Rm1NoResponseCount++;
			if (Rm1NoResponseCount == REPLICA_COUNT) {
				Rm1NoResponseCount = 0;
				sendRMDown(rmNumber);
			}
		} else if (rmNumber == 2) {
			Rm2NoResponseCount++;
			if (Rm2NoResponseCount == REPLICA_COUNT) {
				Rm2NoResponseCount = 0;
				sendRMDown(rmNumber);
			}

		} else if (rmNumber == 3) {
			Rm3NoResponseCount++;
			if (Rm3NoResponseCount == REPLICA_COUNT) {
				Rm3NoResponseCount = 0;
				sendRMDown(rmNumber);
			}
		}
		System.out.println();
		System.out.println("Check RM1 Down - Count: " + Rm1NoResponseCount);
		System.out.println("Check RM2 Down - Count: " + Rm2NoResponseCount);
		System.out.println("Check RM3 Down - Count: " + Rm3NoResponseCount);
	}

	private int sendUdpUnicastToSequencer(Message myRequest) {
		System.out.println("SEND UDP UNICAST");

		startTime = System.nanoTime();
		int sequenceNumber = sendRequestToSequencer(myRequest);
		myRequest.setSequenceId(sequenceNumber);
		latch = new CountDownLatch(3);
		waitForResponse();
		return sequenceNumber;
	}

	public void waitForResponse() {
		try {
			System.out.println("FE Implementation:waitForResponse>>>ResponsesRemain" + latch.getCount());
			boolean timeoutReached = latch.await(DYNAMIC_TIMEOUT, TimeUnit.MILLISECONDS);
			if (timeoutReached) {
				setDynamicTimout();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setDynamicTimout() {
		if (responseTime < 4000) {
			DYNAMIC_TIMEOUT = (DYNAMIC_TIMEOUT + (responseTime * 3)) / 2;
		} else {
			DYNAMIC_TIMEOUT = 10000;
		}
		System.out.println("FE Implementation:setDynamicTimout>>>" + DYNAMIC_TIMEOUT);
	}

	@Override
	public String removeAppointment(String patientId, String appointmentID, String appointmentType) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setUserID(patientId);
		message.setMethodCall(ConfigApplication.REMOVE_APPOINTMENT);
		message.setOldAppointmentId(appointmentID);
		message.setOldAppointmentType(appointmentType);
		message.setSequenceId(sendUdpUnicastToSequencer(message));

		System.out.println("FE RemoveAppointment: " + message);

		return validateResponses(message);
	}

	@Override
	public String listAppointmentAvailability(String patientId, String appointmentType) {
		// TODO Auto-generated method stub
		System.out.println("LIST APPOINTMENT CALLED");

		Message message = new Message();
		message.setUserID(patientId);
		message.setMethodCall(ConfigApplication.LIST_APPOINTMENT);
		message.setMessageType(ConfigApplication.MESSAGE);
		message.setOldAppointmentType(appointmentType);

		message.setSequenceId(sendUdpUnicastToSequencer(message));

		System.out.println("FE ListAppointment: " + message);

		return validateResponses(message);
	}

	@Override
	public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setUserID(patientID);
		message.setMethodCall(ConfigApplication.BOOK_APPOINTMENT);
		message.setMessageType(ConfigApplication.MESSAGE);
		message.setOldAppointmentId(appointmentID);
		message.setOldAppointmentType(appointmentType);
		sendUdpUnicastToSequencer(message);

		System.out.println("FE BookAppointment: " + message);

		return validateResponses(message);
	}

	@Override
	public String getAppointmentSchedule(String patientID) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setUserID(patientID);
		message.setMethodCall(ConfigApplication.GET_APPOINTMENT);
		message.setMessageType(ConfigApplication.MESSAGE);
		sendUdpUnicastToSequencer(message);

		System.out.println("FE GetAppointment: " + message);

		return validateResponses(message);
	}

	@Override
	public String cancelAppointment(String patientID, String appointmentID) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setUserID(patientID);
		message.setOldAppointmentId(appointmentID);
		message.setMethodCall(ConfigApplication.CANCEL_APPOINTMENT);
		message.setMessageType(ConfigApplication.MESSAGE);
		sendUdpUnicastToSequencer(message);

		System.out.println("FE CancelAppointment: " + message);

		return validateResponses(message);
	}

	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
			String newAppointmentID, String newAppointmentType) {
		// TODO Auto-generated method stub
		Message message = new Message();
		message.setUserID(patientID);
		message.setOldAppointmentId(oldAppointmentID);
		message.setOldAppointmentType(oldAppointmentType);
		message.setNewAppointmentID(newAppointmentID);
		message.setNewAppointmentType(newAppointmentType);
		message.setMessageType(ConfigApplication.MESSAGE);
		message.setMethodCall(ConfigApplication.SWAP_APPOINTMENT);
		sendUdpUnicastToSequencer(message);

		System.out.println("FE CancelAppointment: " + message);

		return validateResponses(message);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		orb.shutdown(false);
	}

	private void notifyOKCommandReceived() {
		latch.countDown();
		System.out.println("FE Implementation:notifyOKCommandReceived>>>Response Received: Remaining responses"
				+ latch.getCount());
	}

	public void addReceivedResponse(Message response) {
		long endTime = System.nanoTime();
		responseTime = (endTime - startTime) / 1000000;
		System.out.println("Current Response time is: " + responseTime);
		responses.add(response);
		notifyOKCommandReceived();
	}

}
