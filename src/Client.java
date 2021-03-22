import java.util.ArrayList;
import java.util.HashMap;

public class Client implements Runnable {
	private HashMap<Company, Float> shares;
	private float balance;
	private String name;
	private StockExchange stockExchange;
	
	public Client() {
	}
	
	public Client(String name, StockExchange stockExchange) {
		this.name = name;
		this.balance = 100f;
		this.shares = new HashMap<Company,Float>();
		this.stockExchange = stockExchange;
	}
	
	public HashMap<Company,Float> getStocks() {
		return this.shares;
	}
	
	public void setStocks(Company company, float numberOfShares) {
		this.shares.put(company, numberOfShares);
	}
	
	private void incrementStocks(Company c, float numShares) {
		if (shares.containsKey(c)) {
			shares.put(c, shares.get(c) + numShares);
		} else {
			setStocks(c,numShares);
		}
	}
	
	public boolean buy(Company company, float numberOfShares) {
		System.out.println("Client[" + name + "] is attempting to buy " + numberOfShares + " shares from " + company.getName() + ".");
		company.acquireLock();
		
		//If can afford
		if (company.getPrice()*numberOfShares > balance) {
			System.out.println("Client[" + name + "] can't afford " + numberOfShares + " shares from " + company.getName() + ".");
			company.releaseLock();
			return false;
		}
		
		if (company.decrementAvailableShares((int) numberOfShares)) {
			System.out.println("Client[" + name + "] successfully bought " + numberOfShares + " shares from " + company.getName() + " for �" + (numberOfShares*company.getPrice()) + ".");
			incrementStocks(company,numberOfShares);
			balance-= company.getPrice()*numberOfShares;
			company.releaseLock();
			return true;
		} else {
			System.out.println("Client[" + name + "] failed in buying " + numberOfShares + " shares from " + company.getName() + ".");
			company.releaseLock();
			return false;
		}
	}
	
	public boolean sell(Company company, float numberOfShares) {
		System.out.println("Client[" + name + "] is attempting to sell " + numberOfShares + " shares to " + company.getName() + ".");
		company.acquireLock();
		if (company.incrementAvailableShares((int) numberOfShares)) {
			System.out.println("Client[" + name + "] successfully sold " + numberOfShares + " shares to " + company.getName() + " for �" + (company.getPrice()*numberOfShares) + ".");
			incrementStocks(company,-numberOfShares);
			balance+= company.getPrice()*numberOfShares;
			company.releaseLock();
			return true;
		} else {
			company.releaseLock();
			return false;
		}
	}
	
	public boolean buyLow(Company company, float numberOfShares, float limit) {
		company.acquireLock();
		if (company.getPrice() > limit) {
			company.releaseLock();
			System.out.println("Client[" + name + "] is going to wait.");
			company.waitForPriceToDrop(limit);
			company.acquireLock();
		}
		
		
		//If can afford
		if (company.getPrice()*numberOfShares > balance) {
			System.out.println("Client[" + name + "] can't afford " + numberOfShares + " shares from " + company.getName() + ".");
			company.releaseLock();
			return false;
		}
		
		//Doing the buying
		if (company.decrementAvailableShares((int) numberOfShares)) {
			incrementStocks(company,numberOfShares);
			balance-= company.getPrice()*numberOfShares;
			company.releaseLock();
			System.out.println("Client[" + name + "] successfully bought " + numberOfShares + " shares from " + company.getName() + ".");
			return true;
		} else {
			company.releaseLock();
			System.out.println("Client[" + name + "] failed in buying " + numberOfShares + " shares from " + company.getName() + ".");
			return false;
		}
		
	}
	
	public boolean sellHigh(Company company, float numberOfShares, float limit) {
		return false;//temp
	}
	
	public boolean deposit(float amount) {
		balance+=amount;
		return true;//temp
	}
	
	public boolean withdraw(float amount) {
		balance-=amount;
		return true;//temp
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName(String name) {
		return this.name;
	}
	
	public float getBalance() {
		return this.balance;
	}
	
	public float getNumShares() {
		float totalShares = 0;
		ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
		for (int i = 0; i<companies.size(); i++) {
			totalShares += shares.get(companies.get(i));
		}
		return totalShares;
	}
	
	public float getShareBalance() {
		float totalValue = 0;
		ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
		for (int i = 0; i<companies.size(); i++) {
			Company c = companies.get(i);
			totalValue += shares.get(c) * c.getPrice();
		}
		return totalValue;
	}
	
	@Override
	public void run() {
		System.out.println("Starting client[" + name + "].");
		
		//testBuySell();
		testBuyLowSellHigh();
		
		System.out.println("Finishing client[" + name + "].");
	}
	
	public void testBuyLowSellHigh() {
		Company c = stockExchange.getRandomCompany();
		int sharesToBuy = (int) (Math.random() * 11);
		float p = (float) Math.random() * 50;
		System.out.println("Client[" + name + "] wants " + c.getName() + "'s price to drop below " + p + ".");
		buyLow(c,sharesToBuy,p);
		System.out.println("Client[" + name + "] has bought their shares.");
	}
	
	private void testBuySell() {
		for (int i=0; i<100; i++) {
			int rnd = (int)(Math.random()*2);
			//System.out.println(rnd);
			switch (rnd) {
			case 0:
				buyRandomShares();
				break;
			case 1:
				sellRandomShares();
				break;
			default:
				buyRandomShares();	
			}
		}
	}
	
	private void buyRandomShares() {
		Company c = stockExchange.getRandomCompany();
		//int sharesToBuy = (int) (Math.random() * stockExchange.getMaxAffordableShares(c, balance));
		int sharesToBuy = (int) (Math.random() * 11);
			//System.out.println("Client[" + name + "] is attempting to buy " + sharesToBuy + " shares from " + c.getName() + ".");
		if (buy(c, sharesToBuy)) {
			//System.out.println("Client[" + name + "] successfully bought " + sharesToBuy + " shares from " + c.getName() + ".");
		} else {
			//System.out.println("Client[" + name + "] failed in buying " + sharesToBuy + " shares from " + c.getName() + ".");
		}
	}
	
	private void sellRandomShares() {
		ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
		
		if (companies.isEmpty()) {return;}
		
		int rnd = (int)(Math.random()*companies.size());
		Company c = companies.get(rnd);
		int numSharesToSell = (int)(Math.random()*shares.get(c));
		//System.out.println("Client[" + name + "] is attempting to sell " + numSharesToSell + " shares to " + c.getName() + ".");
		sell(c, numSharesToSell);
		//System.out.println("Client[" + name + "] successfully sold " + numSharesToSell + " shares to " + c.getName() + ".");
	}
	
	public String toString() {
		String str = "Client[" + name + "],\n- Balance: " + balance + ",";
		if (shares.isEmpty()) {
			str += "\n- No shares owned.";
		} else {
			ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
			for (int i = 0; i<companies.size(); i++) {
				str += "\n- " + shares.get(companies.get(i)) + " shares at " + companies.get(i).getName() + " (total= " + (companies.get(i).getPrice()*shares.get(companies.get(i))) + ")";
			}
		}
		return str;
	}
}
