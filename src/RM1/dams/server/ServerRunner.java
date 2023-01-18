package RM1.dams.server;

import RM1.ReplicaManager1;

public class ServerRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				try {
					MontrealPublisherServer.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
		
		new Thread() {
			@Override
			public void run() {
				try {
					QuebecPublisherServer.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
		
		new Thread() {
			@Override
			public void run() {
				try {
					SherbrookePublisherServer.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

}
