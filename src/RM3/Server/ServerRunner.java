package RM3.Server;

import RM3.Server.Montreal.*;
import RM3.Server.Quebec.*;
import RM3.Server.Sherbrooke.*;
import RM3.utils.*;

public class ServerRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				try {
					new MTLServerInstance(Constants.MONTREAL_SERVER, args);
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
					new QUEServerInstance(Constants.QUEBEC_SERVER, args);
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
					new SHEServerInstance(Constants.SHERBROOKE_SERVER, args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
	}

}

