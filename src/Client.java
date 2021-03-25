import java.util.ArrayList;
import java.util.HashMap;

/**
 * Models a client in a stock exchange system
 * @author Yoshan Mumberson 1911116
 *
 */
public class Client implements Runnable {
	/**
	 * Shares of the client
	 */
	private HashMap<Company, Float> shares;
	
	/**
	 * Balance of the client
	 */
	private float balance;
	
	/**
	 * Name (ID) of the client
	 */
	private String name;
	
	/**
	 * Pointer to the stockExchange that the client belongs to
	 */
	private StockExchange stockExchange;
	
	/**
	 * Blank constructor
	 */
	public Client() {
	}
	
	/**
	 * Constructor
	 * @param name Name of client
	 * @param stockExchange Pointer to stock exchange that the client belongs to
	 */
	public Client(String name, StockExchange stockExchange) {
		this.name = name;
		this.balance = 100f; //Default value for testing
		this.shares = new HashMap<Company,Float>();
		this.stockExchange = stockExchange;
	}
	
	/**
	 * Getter for client's shares
	 * @return Shares of the client
	 */
	public HashMap<Company,Float> getStocks() {
		return this.shares;
	}
	
	/**
	 * Setter for shares
	 * @param company Company the shares belong to
	 * @param numberOfShares Number of shares
	 */
	public void setStocks(Company company, float numberOfShares) {
		this.shares.put(company, numberOfShares);
	}
	
	/**
	 * Increments the number of stocks that the client owns for a given company.<br>
	 * Can also decrement if given a negative number.
	 * @param c Company the shares belong to.
	 * @param numShares Number of shares.
	 */
	private void incrementStocks(Company c, float numShares) {
		if (shares.containsKey(c)) {
			float total = shares.get(c) + numShares;
			if (total <= 0f) {
				shares.remove(c);
			} else {
				shares.put(c, total);
			}
		} else {
			setStocks(c,numShares);
		}
	}
	
	/**
	 * Buys shares from a company.
	 * @param company Company to buy shares from.
	 * @param numberOfShares Number of shares to buy.
	 * @return True if successful, false otherwise.
	 * @throws InterruptedException Throws if interrupted while buying.
	 */
	public boolean buy(Company company, float numberOfShares) throws InterruptedException {
		//System.out.println("Client[" + name + "] is attempting to buy " + numberOfShares + " shares from " + company.getName() + ".");
		company.acquireLock();
		
		//If can afford
		if (company.getPrice()*numberOfShares > balance) {
			//System.out.println("Client[" + name + "] can't afford " + numberOfShares + " shares from " + company.getName() + ".");
			company.releaseLock();
			return false;
		}
		
		if (company.decrementAvailableShares(numberOfShares)) {
			//System.out.println("Client[" + name + "] successfully bought " + numberOfShares + " shares from " + company.getName() + " for " + (numberOfShares*company.getPrice()) + ".");
			incrementStocks(company,numberOfShares);
			balance-= company.getPrice()*numberOfShares;
			company.releaseLock();
			return true;
		} else {
			//System.out.println("Client[" + name + "] failed in buying " + numberOfShares + " shares from " + company.getName() + ".");
			company.releaseLock();
			return false;
		}
	}
	
	/**
	 * Sells shares back to a company.<br>
	 * Fails if client doesn't own enough shares from the given company.
	 * @param company Company to shares to.
	 * @param numberOfShares Number of shares to sell.
	 * @return
	 * @throws InterruptedException Throws if interrupted while selling.
	 */
	public boolean sell(Company company, float numberOfShares) throws InterruptedException {
		//System.out.println("Client[" + name + "] is attempting to sell " + numberOfShares + " shares to " + company.getName() + ".");
		
		if (!owns(company, numberOfShares)) {
			//System.out.println("Client[" + name + "] doesn't own " + numberOfShares + " shares from " + company.getName() + ".");
			return false;
		}
		
		company.acquireLock();
		
		if (company.incrementAvailableShares(numberOfShares)) {
			//System.out.println("Client[" + name + "] successfully sold " + numberOfShares + " shares to " + company.getName() + " for " + (company.getPrice()*numberOfShares) + ".");
			incrementStocks(company,-numberOfShares);
			balance+= company.getPrice()*numberOfShares;
			company.releaseLock();
			return true;
		} else {
			company.releaseLock();
			return false;
		}
	}
	
	/**
	 * Checks if the client owns a given number of shares from a given company
	 * @param c Company that the shares belong to
	 * @param numShares Number of shares
	 * @return True if client owns the given number of shares from the given company
	 */
	private boolean owns(Company c, float numShares) {
		return shares.containsKey(c) && shares.get(c) >= numShares;
	}
	
	/**
	 * Buys stocks from a company when the company's price drops to a limit
	 * @param company Company to buy shares from
	 * @param numberOfShares Number of shares
	 * @param limit Low price to buy shares at (or lower)
	 * @return True if successful, else false
	 * @throws InterruptedException Throws if interrupted while waiting to buy
	 */
	public boolean buyLow(Company company, float numberOfShares, float limit) throws InterruptedException {
		company.acquireLock();
		//Wait for price drop
		if (company.getPrice() > limit) {
			company.releaseLock();
			//System.out.println("Client[" + name + "] is going to wait.");
			company.waitForPriceToDrop(limit);
			company.acquireLock();
		}
		
		
		//If can afford
		if (company.getPrice()*numberOfShares > balance) {
			//System.out.println("Client[" + name + "] can't afford " + numberOfShares + " shares from " + company.getName() + ".");
			company.releaseLock();
			return false;
		}
		
		//Doing the buying
		if (company.decrementAvailableShares(numberOfShares)) {
			incrementStocks(company,numberOfShares);
			balance-= company.getPrice()*numberOfShares;
			company.releaseLock();
			//System.out.println("Client[" + name + "] successfully bought " + numberOfShares + " shares from " + company.getName() + ".");
			return true;
		} else {
			company.releaseLock();
			//System.out.println("Client[" + name + "] failed in buying " + numberOfShares + " shares from " + company.getName() + ".");
			return false;
		}
		
	}
	
	/**
	 * Sells shares back to a company once the company's price rises to a given limit
	 * @param company Company to sell shares to
	 * @param numberOfShares Number of shares to sell
	 * @param limit High price to sell shares at (or higher)
	 * @return True if successful, else false
	 * @throws InterruptedException Throws if interrupted while waiting to sell
	 */
	public boolean sellHigh(Company company, float numberOfShares, float limit) throws InterruptedException {
		if (!owns(company, numberOfShares)) {
			//System.out.println("Client[" + name + "] doesn't own " + numberOfShares + " shares from " + company.getName() + ".");
			return false;
		}
		
		company.acquireLock();
		//Wait for price rise
		if (company.getPrice() < limit) {
			company.releaseLock();
			//System.out.println("Client[" + name + "] is going to wait.");
			company.waitForPriceToRise(limit);
			company.acquireLock();
		}
		
		//Do the selling
		if (company.incrementAvailableShares(numberOfShares)) {
			//System.out.println("Client[" + name + "] successfully sold " + numberOfShares + " shares to " + company.getName() + " for " + (company.getPrice()*numberOfShares) + ".");
			incrementStocks(company,-numberOfShares);
			balance+= company.getPrice()*numberOfShares;
			company.releaseLock();
			return true;
		} else {
			company.releaseLock();
			return false;
		}
	}
	
	/**
	 * Adds money to the client's balance
	 * @param amount Amount of money to add
	 * @return False if amount is negative, otherwise true
	 */
	public boolean deposit(float amount) {
		if (amount < 0) {
			return false;
		} else {
			balance += amount;
			return true;
		}
	}
	
	/**
	 * Subtracts money from the client's balance
	 * @param amount Amount of money to subtract
	 * @return False if amount is negative, otherwise true
	 */
	public boolean withdraw(float amount) {
		if (amount < 0) {
			return false;
		} else {
			balance += amount;
			return true;
		}
	}
	
	/**
	 * Setter for name
	 * @param name New value for name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getter for name
	 * @return Client's name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Getter for balance
	 * @return Client's balance
	 */
	public float getBalance() {
		return this.balance;
	}
	
	/**
	 * Gets total amount of shares that the client owns
	 * @return Total amount of shares the client owns
	 */
	public float getNumShares() {
		float totalShares = 0;
		ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
		for (int i = 0; i<companies.size(); i++) {
			totalShares += shares.get(companies.get(i));
		}
		return totalShares;
	}
	
	/**
	 * Gets the total value of all of the shares that the client owns
	 * @return total value of all of the shares that the client owns
	 */
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
		//System.out.println("Starting client[" + name + "].");
		
		try {
			//for (int i=0; i<100; i++) {
			while (true) {
				//testBuySell();
				//testBuyLowSellHigh();
				randomRun();	
			}	
		} catch (InterruptedException e) {
			//System.out.println("Client[" + name + "] was interrupted!");
		}
		
		
		//System.out.println("Finishing client[" + name + "].");
	}
	
	/**
	 * Simulates a random run of buying shares, selling shares, buying low and selling high
	 * @throws InterruptedException Throws if interrupted while running
	 */
	public void randomRun() throws InterruptedException {
			int rnd = (int)(Math.random()*4);
			//System.out.println(rnd);
			switch (rnd) {
			case 0:
				buyRandomShares();
				break;
			case 1:
				sellRandomShares();
				break;
			case 2:
				randomBuyLow();
				break;
			case 3:
				randomSellHigh();
				break;
			default:
				buyRandomShares();	
			}
	}
	
	/**
	 * Tests buying low and selling high
	 * @throws InterruptedException Throws if interrupted while running
	 */
	public void testBuyLowSellHigh() throws InterruptedException {
		randomBuyLow();
		randomSellHigh();
	}
	
	/**
	 * Tests buying low
	 * @throws InterruptedException Throws if interrupted while running
	 */
	private void randomBuyLow() throws InterruptedException {
		Company c = stockExchange.getRandomCompany();
		float sharesToBuy = (float)(Math.random() * 11f);
		float p = (float) Math.random() * 50f;
		//System.out.println("Client[" + name + "] wants " + c.getName() + "'s price to drop below " + p + ".");
		buyLow(c,sharesToBuy,p);
	}
	
	/**
	 * Tests selling high
	 * @throws InterruptedException Throws if interrupted while running
	 */
	private void randomSellHigh() throws InterruptedException {
		ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
		if (companies.isEmpty()) {return;}
		int rnd = (int)(Math.random()*companies.size());
		Company c = companies.get(rnd);
		float numSharesToSell = (float)(Math.random()*shares.get(c));
		float p = (float) Math.random() * 100f;
		//System.out.println("Client[" + name + "] wants " + c.getName() + "'s price to raise above " + p + ".");
		sellHigh(c,numSharesToSell,p);
	}
	
	/**
	 * Tests randomly either a buy or a sell
	 * @throws InterruptedException Throws if interrupted while running
	 */
	private void testBuySell() throws InterruptedException {
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
	
	/**
	 * Buys a random share
	 * @throws InterruptedException Throws if interrupted while running
	 */
	private void buyRandomShares() throws InterruptedException {
		Company c = stockExchange.getRandomCompany();
		float sharesToBuy = (float) (Math.random() * 11f);
		buy(c, sharesToBuy);
	}
	
	/**
	 * Sells a random share
	 * @throws InterruptedException Throws if interrupted while running
	 */
	private void sellRandomShares() throws InterruptedException {
		ArrayList<Company> companies = new ArrayList<Company>(shares.keySet());
		
		if (companies.isEmpty()) {return;}
		
		int rnd = (int)(Math.random()*companies.size());
		Company c = companies.get(rnd);
		float numSharesToSell = (float)(Math.random()*shares.get(c));
		sell(c, numSharesToSell);
	}
	
	/**
	 * Converts the client to a readable string
	 */
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
