import FrontEnd.FE;
import RM1.ReplicaManager1;
//import RM2.ReplicaManager2;
import Sequencer.Sequencer;

public class Run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				try {
					ReplicaManager1.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
//		new Thread() {
//			@Override
//			public void run() {
//				try {
//					ReplicaManager2.main(args);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					FE.main(args);
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
					Sequencer.main(args);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}.start();
		
	}

}
