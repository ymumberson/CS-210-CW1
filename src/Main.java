
public class Main {
	public static void main(String[] args) throws InterruptedException {
		//Setup
		int numClients = 5;
		StockExchange s = new StockExchange();
		Company comp1 = new Company("Company 1", 10, 10, 3.14f);
		Company comp2 = new Company("Company 2", 25, 25, 2.03f);
		Company comp3 = new Company("Company 3", 100, 100, 0.87f);
		Client[] clients = new Client[numClients];
		Thread[] threads = new Thread[numClients];
		for (int i=0; i<numClients; i++) {
			clients[i] = new Client(String.valueOf(i),s);
			threads[i] = new Thread(clients[i]);
			threads[i].setDaemon(true);
			s.addClient(clients[i]);
		}
		//Client client1 = new Client("Andy",s);
		//Thread t1 = new Thread(client1);
		//t1.setDaemon(true);
		s.registerCompany(comp1, comp1.getAvailableShares());
		s.registerCompany(comp2, comp2.getAvailableShares());
		s.registerCompany(comp3, comp3.getAvailableShares());
		//s.addClient(client1);
		
		System.out.println(s);
		
		//Starting processes
		for (int i=0; i<numClients; i++) {
			threads[i].start();
		}
		//t1.start();
		
		//Finishing processes
		for (int i=0; i<numClients; i++) {
			threads[i].join();
		}
		System.out.println("Execution has finished.\n");
		System.out.println(s);
		//t1.join();
	}
}
