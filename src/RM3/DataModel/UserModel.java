package RM3.DataModel;

import RM3.utils.Constants;

public class UserModel {
    private String userType;
    private String userId;
    private String userServer;

    public UserModel(String userId) {
        this.userId = userId;
        this.userType = detectClientType();
        this.userServer = detectClientServer();
    }

    /**
     * @return String return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType the userType to set
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * @return String return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return String return the userServer
     */
    public String getUserServer() {
        return userServer;
    }

    /**
     * @param userServer the userServer to set
     */
    public void setUserServer(String userServer) {
        this.userServer = userServer;
    }

    private String detectClientServer() {
        if (userId.substring(0, 3).equalsIgnoreCase("MTL")) {
            return Constants.APPOINTMENT_SERVER_MONTREAL;
        } else if (userId.substring(0, 3).equalsIgnoreCase("QUE")) {
            return Constants.APPOINTMENT_SERVER_QUEBEC;
        } else {
            return Constants.APPOINTMENT_SERVER_SHERBROOK;
        }
    }

    private String detectClientType() {
        if (userId.substring(3, 4).equalsIgnoreCase("A")) {
            return Constants.USER_ADMIN;
        } else {
            return Constants.USER_PATIENT;
        }
    }

}
