package FrontEnd;

import OutputConfiguration.Message;

public interface FEInterface {
    void sendRMBug(int RmNumber);

    void sendRMDown(int RmNumber);

    int sendRequestToSequencer(Message myRequest);

    void sendRetryRequest(Message myRequest);
}