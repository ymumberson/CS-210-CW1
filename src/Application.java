
public class Application {
	public static void main(String[] args) throws InterruptedException {
		//testBuySell();
		//testBuyLowSellHigh();
		
		int numPassed = 0;
		int numRuns = 100;
		for (int i=1; i<=numRuns; i++) {
			//if (crazyTest()) {
			if (normalTest()) {
				numPassed++;
			}
		}
		
		System.out.println("=====================================\n"
				+ numPassed + "/" + numRuns + " tests passed.");
		
	}
	
	public static boolean crazyTest() throws InterruptedException {
		int numClients = 100;
		int numCompanies = 10;
		int numPriceChanges = 10000;
		float totalSharesBefore;
		float totalSharesAfter;
		float totalBalanceBefore;
		float totalBalanceAfter;
		StockExchange s = new StockExchange();
//		Company comp1 = new Company("Company 1", 10, 10, 100f);
//		Company comp2 = new Company("Company 2", 25, 25, 100f);
//		Company comp3 = new Company("Company 3", 100, 100, 100f);
		Client[] clients = new Client[numClients];
		Company[] companies = new Company[numCompanies];
		Thread[] threads = new Thread[numClients];
		for (int i=0; i<numClients; i++) {
			clients[i] = new Client(String.valueOf(i),s);
			threads[i] = new Thread(clients[i]);
			threads[i].setDaemon(true);
			threads[i].setName("t" + i);
			s.addClient(clients[i]);
		}
		for (int i=0; i<numCompanies; i++) {
			companies[i] = new Company(("Company " + i), 100f, 100f, 100f);
			s.registerCompany(companies[i], 100f);
		}

//		s.registerCompany(comp1, comp1.getAvailableShares());
//		s.registerCompany(comp2, comp2.getAvailableShares());
//		s.registerCompany(comp3, comp3.getAvailableShares());
		
//		System.out.println(s);
		totalSharesBefore = s.getTotalShares();
		totalBalanceBefore = s.getTotalBalance();
		
		//Starting processes
		for (int i=0; i<numClients; i++) {
			threads[i].start();
		}
		//t1.start();
		
		for (int i=0; i<numPriceChanges; i++) {
//			s.setPrice(comp1, (float)Math.random()*100);
//			s.setPrice(comp2, (float)Math.random()*100);
//			s.setPrice(comp3, (float)Math.random()*100);
			for (int j=0; j<numCompanies; j++) {
				s.setPrice(companies[j], (float)Math.random()*100);
			}
		}
//		System.out.println("<Application> Finished price changes. </Application>");
		
		//System.out.println("Still " + Thread.activeCount() + " threads running!");
		//Thread.sleep(10000);
		if (Thread.activeCount() > 1) {
//			System.out.println((Thread.activeCount()-1) + " clients still running. Executing Interrupts!");
			for (int i=0; i<numClients; i++) {
				threads[i].interrupt();
			}
//			System.out.println(s);
		}
		
		//Finishing processes
		for (int i=0; i<numClients; i++) {
			threads[i].join();
		}
//		System.out.println("Execution has finished.\n");
//		System.out.println(s);
		
		totalSharesAfter = s.getTotalShares();
		totalBalanceAfter = s.getTotalBalance();
		
		//System.out.println("Total Shares Before: " + totalSharesBefore + ", Total Shares After: " + totalSharesAfter);
		//System.out.println("Total Balance Before: " + totalBalanceBefore + ", Total Balance After: " + totalBalanceAfter);
		
		
		float tolerence = 0.5f;
		//System.out.println("tolerence: " + tolerence);
		System.out.println("Balance Diff: " + Math.abs(totalBalanceAfter - totalBalanceBefore) + "/" + tolerence);
		
		if (Math.abs(totalSharesAfter - totalSharesBefore) > 0.05f) {return false;}
		if (Math.abs(totalBalanceAfter - totalBalanceBefore) > tolerence) {return false;}
		return true;
	}
	
	public static boolean normalTest() throws InterruptedException {
		int numClients = 100;
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

		s.registerCompany(comp1, comp1.getAvailableShares());
		s.registerCompany(comp2, comp2.getAvailableShares());
		s.registerCompany(comp3, comp3.getAvailableShares());
		
//		System.out.println(s);
		totalSharesBefore = s.getTotalShares();
		totalBalanceBefore = s.getTotalBalance();
		
		//Starting processes
		for (int i=0; i<numClients; i++) {
			threads[i].start();
		}
		//t1.start();
		
		for (int i=0; i<100000; i++) {
			s.setPrice(comp1, (float)Math.random()*100);
			s.setPrice(comp2, (float)Math.random()*100);
			s.setPrice(comp3, (float)Math.random()*100);
		}
		System.out.println("<Application> Finished price changes. </Application>");
		
		//System.out.println("Still " + Thread.activeCount() + " threads running!");
		//Thread.sleep(10000);
		if (Thread.activeCount() > 1) {
			System.out.println((Thread.activeCount()-1) + " clients still running. Executing Interrupts!");
			for (int i=0; i<numClients; i++) {
				threads[i].interrupt();
			}
			System.out.println(s);
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
}
