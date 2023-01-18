package OutputConfiguration;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	public String userID = "", messageType = "", ipAddressFE = "", methodCall = "";
	public String oldAppointmentId = "", oldAppointmentType = "", newAppointmentId = "", newAppointmentType = "";
	public int bookingCapacity = -1, sequenceId = 0, messageCount = 1, RMNumber = -1;
    private boolean isSuccess = false;
    private String response = "EMPTY";

	public Message(int sequenceId, String userID, String messageType, String methodCall, String oldAppointmentId,
			String oldAppointmentType, int bookingCapacity) {
		this.userID = userID;
		this.messageType = messageType;
		this.methodCall = methodCall;
		this.oldAppointmentId = oldAppointmentId;
		this.oldAppointmentType = oldAppointmentType;
		this.bookingCapacity = bookingCapacity;
		this.sequenceId = sequenceId;
		this.newAppointmentId = "";
		this.newAppointmentType = "";
	}

	public Message(int sequenceId, String ipAddressFE, String messageType, String methodCall, String userID,
			String newAppointmentId, String newAppointmentType, String oldAppointmentId, String oldAppointmentType,
			int bookingCapacity) {
		this.sequenceId = sequenceId;
		this.ipAddressFE = ipAddressFE;
		this.messageType = messageType;
		this.methodCall = methodCall;
		this.userID = userID;
		this.newAppointmentId = newAppointmentId;
		this.newAppointmentType = newAppointmentType;
		this.oldAppointmentId = oldAppointmentId;
		this.oldAppointmentType = oldAppointmentType;
		this.bookingCapacity = bookingCapacity;
	}

	public Message(Message msg) {
		this.sequenceId = msg.sequenceId;
		this.ipAddressFE = msg.ipAddressFE;
		this.messageType = msg.messageType;
		this.methodCall = msg.methodCall;
		this.userID = msg.userID;
		this.newAppointmentId = msg.newAppointmentId;
		this.newAppointmentType = msg.newAppointmentType;
		this.oldAppointmentId = msg.oldAppointmentId;
		this.oldAppointmentType = msg.oldAppointmentType;
		this.bookingCapacity = msg.bookingCapacity;
	}

	public Message() {
		this.sequenceId = 0;
		this.ipAddressFE = "";
		this.messageType = "";
		this.methodCall = "";
		this.userID = "";
		this.newAppointmentId = "";
		this.newAppointmentType = "";
		this.oldAppointmentId = "";
		this.oldAppointmentType = "";
		this.bookingCapacity = 0;
	}

	public Message(int rmNumber, String msgType) {
		// TODO Auto-generated constructor stub
		this.RMNumber = rmNumber;
		this.messageType = msgType;
	}

	public int getRMNumber() {
		return RMNumber;
	}

	public void setRMNumber(String RMNumberString) {
		if (RMNumberString.equalsIgnoreCase("RM1")) {
			this.RMNumber = 1;
		} else if (RMNumberString.equalsIgnoreCase("RM2")) {
			this.RMNumber = 2;
		} else if (RMNumberString.equalsIgnoreCase("RM3")) {
			this.RMNumber = 3;
		} else {
			this.RMNumber = 0;
		}
	}
	
	public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        isSuccess = response.contains("Success:");
        this.response = response;
    }

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getIpAddressFE() {
		return ipAddressFE;
	}

	public void setIpAddressFE(String ipAddressFE) {
		this.ipAddressFE = ipAddressFE;
	}

	public String getMethodCall() {
		return methodCall;
	}

	public void setMethodCall(String methodCall) {
		this.methodCall = methodCall;
	}

	public String getOldAppointmentId() {
		return oldAppointmentId;
	}

	public void setOldAppointmentId(String oldAppointmentId) {
		this.oldAppointmentId = oldAppointmentId;
	}

	public String getOldAppointmentType() {
		return oldAppointmentType;
	}

	public void setOldAppointmentType(String oldAppointmentType) {
		this.oldAppointmentType = oldAppointmentType;
	}

	public String getNewAppointmentID() {
		return newAppointmentId;
	}

	public void setNewAppointmentID(String newEventID) {
		this.newAppointmentId = newEventID;
	}

	public String getNewAppointmentType() {
		return newAppointmentType;
	}

	public void setNewAppointmentType(String newAppointmentType) {
		this.newAppointmentType = newAppointmentType;
	}

	public int getBookingCapacity() {
		return bookingCapacity;
	}

	public void setBookingCapacity(int bookingCapacity) {
		this.bookingCapacity = bookingCapacity;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public boolean haveRetries() {
		return messageCount > 0;
	}

	public void countRetry() {
		messageCount = messageCount - 1;
	}

	@Override
	public String toString() {
		String result = "Message [";

		if (!userID.equals("")) {
			result += "userId: " + userID + ", ";
		}
		
		if (!response.equals("EMPTY")) {
			result += "response: " + response + ", ";
		}


		if (!messageType.equals("")) {
			result += "messageType: " + messageType + ", ";
		}

		if (!ipAddressFE.equals("")) {
			result += "ipAddressFE: " + ipAddressFE + ", ";
		}

		if (!methodCall.equals("")) {
			result += "methodCall: " + methodCall + ", ";
		}

		if (!oldAppointmentId.equals("")) {
			result += "oldAppointmentId: " + oldAppointmentId + ", ";
		}

		if (!oldAppointmentType.equals("")) {
			result += "oldAppointmentType: " + oldAppointmentType + ", ";
		}

		if (!newAppointmentId.equals("")) {
			result += "newAppointmentID: " + newAppointmentId + ", ";
		}

		if (!newAppointmentType.equals("")) {
			result += "newAppointmentType: " + newAppointmentType + ", ";
		}

		if (bookingCapacity != 0) {
			result += "bookingCapacity: " + bookingCapacity + ", ";
		}

		result += "sequenceId: " + sequenceId + ", ";

		return result + "]";

	}

}