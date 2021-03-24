
public class Application {
	public static void main(String[] args) throws InterruptedException {
		//testBuySell();
		testBuyLowSellHigh();
		
		int numPassed = 0;
		int numRuns = 10000;
		for (int i=0; i<numRuns; i++) {
			if (testBuyLowSellHigh()) {
				numPassed++;
			}
		}
		
		System.out.println("=====================================\n"
				+ numPassed + "/" + numRuns + " passed.");
		
//		Company comp1 = new Company("Company 1", 10, 10, 100f);
//		Test test1 = new Test(comp1,50f);
//		Test test2 = new Test(comp1,70f);
//		Thread t1 = new Thread(test1);
//		Thread t2 = new Thread(test2);
//		t1.start();
//		Thread.sleep(100);
//		t2.start();
//		
//		Thread.sleep(1000);
//		comp1.setPrice(60f);
//		
//		Thread.sleep(1000);
//		comp1.setPrice(50f);
//		
//		t1.join();
//		t2.join();
	}
	
	public static boolean testBuyLowSellHigh() throws InterruptedException {
		int numClients = 5;
		float totalSharesBefore;
		float totalSharesAfter;
		float totalBalanceBefore;
		float totalBalanceAfter;
		StockExchange s = new StockExchange();
		Company comp1 = new Company("Company 1", 10, 10, 100f);
		Company comp2 = new Company("Company 2", 25, 25, 100f);
		Company comp3 = new Company("Company 3", 100, 100, 100f);
		Client[] clients = new Client[numClients];
		Thread[] threads = new Thread[numClients];
		for (int i=0; i<numClients; i++) {
			clients[i] = new Client(String.valueOf(i),s);
			threads[i] = new Thread(clients[i]);
			threads[i].setDaemon(true);
			threads[i].setName("t" + i);
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
		totalSharesBefore = s.getTotalShares();
		totalBalanceBefore = s.getTotalBalance();
		
		//Starting processes
		for (int i=0; i<numClients; i++) {
			threads[i].start();
		}
		//t1.start();
		
		for (int i=0; i<1000; i++) {
			//System.out.println(i);
//			s.changePriceBy(comp1, 50-(float)Math.random()*100);
//			s.changePriceBy(comp2, 50-(float)Math.random()*100);
//			s.changePriceBy(comp3, 50-(float)Math.random()*100);
			s.setPrice(comp1, (float)Math.random()*100);
			s.setPrice(comp2, (float)Math.random()*100);
			s.setPrice(comp3, (float)Math.random()*100);
			//System.out.println(s);
		}
		System.out.println("<Application> Finished price changes. </Application>");
		
		//System.out.println("Still " + Thread.activeCount() + " threads running!");
		
		if (Thread.activeCount() > 1) {
			System.out.println((Thread.activeCount()-1) + " clients still running. Executing Interrupts!");
//			System.out.println(s);
//			System.exit(0);
			for (int i=0; i<numClients; i++) {
				threads[i].interrupt();
			}
//			System.out.println(s);
		}
		
		//Finishing processes
		for (int i=0; i<numClients; i++) {
			threads[i].join();
		}
		System.out.println("Execution has finished.\n");
		System.out.println(s);
		
		totalSharesAfter = s.getTotalShares();
		totalBalanceAfter = s.getTotalBalance();
		
		if (Math.abs(totalSharesAfter - totalSharesBefore) > 1) {return false;}
		if (Math.abs(totalBalanceAfter - totalBalanceBefore) > 1) {return false;}
		return true;
	}
	
	public static void testBuySell() throws InterruptedException {
		//Note that share balance is negative because money is being created by changing
		// the price of random companies. Total balance is still 500 so essentially companies are in debt
		
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
				
				for (int i=0; i<1000; i++) {
					//System.out.println(i);
//					comp1.setPrice((float)Math.random()*100);
//					comp2.setPrice((float)Math.random()*100);
//					comp3.setPrice((float)Math.random()*100);
					s.changePriceBy(comp1, 50-(float)Math.random()*100);
					s.changePriceBy(comp2, 50-(float)Math.random()*100);
					s.changePriceBy(comp3, 50-(float)Math.random()*100);
				}
				System.out.println("<Application> Finished price changes. </Application>");
				
				//Finishing processes
				for (int i=0; i<numClients; i++) {
					threads[i].join();
				}
				System.out.println("Execution has finished.\n");
				System.out.println(s);
				//t1.join();
	}
}
