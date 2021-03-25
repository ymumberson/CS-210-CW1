import java.util.ArrayList;
import java.util.HashMap;

/**
 * Models a stock exchange
 * @author Yoshan Mumberson 1911116
 *
 */
public class StockExchange {
	/**
	 * List of companies in the stock exchange, and how many stocks they have available
	 */
	private HashMap<Company, Float> companies;
	
	/**
	 * List of clients in the stock exchange
	 */
	private ArrayList<Client> clients;
	
	/**
	 * Blank constructor
	 */
	public StockExchange() {
		companies = new HashMap<Company, Float>();
		clients = new ArrayList<Client>();
	}
	
	/**
	 * Registers a company into the stock exchange
	 * @param company Company to register
	 * @param numberOfShares Number of shares the company has available
	 * @return True if successful, otherwise false
	 */
	public boolean registerCompany(Company company, float numberOfShares) {
		if (companies.containsKey(company)) {
			return false; //Company already exists
		} else {
			companies.put(company, numberOfShares);
			//System.out.println("New company registered!\n{"+company+"}\n");
			return true;
		}
	}
	
	/**
	 * Removes a company from the stock exchange
	 * @param company Company to remove
	 * @return True of successful, otherwise false
	 */
	public boolean deregisterCompany(Company company) {
		if (companies.containsKey(company)) {
			companies.remove(company);
			//System.out.println("Old company removed!\n{"+company+"}\n");
			return true;
		} else {
			return false;//Company doesn't exist
		}
	}
	
	/**
	 * Adds a client to the stock exchange
	 * @param client Client to add to the stock exchange
	 * @return True if successful, otherwise false
	 */
	public boolean addClient(Client client) {
		if (clients.contains(client)) {
			return false;//Client already exists
		} else {
			clients.add(client);
			//System.out.println("New client registered!\n{"+client+"}\n");
			return true;
		}
	}
	
	/**
	 * Removes a client from the stock exchange
	 * @param client Client to remove
	 * @return True if successful, otherwise false
	 */
	public boolean removeClient(Client client) {
		if (clients.contains(client)) {
			clients.remove(client);
			//System.out.println("Old client removed!\n{"+client+"}\n");
			return true;
		} else {
			return false;//Client doesn't exist
		}
	}
	
	/**
	 * Getter for clients
	 * @return List of all clients in the stock exchange
	 */
	public ArrayList<Client> getClients() {
		return this.clients;
	}
	
	/**
	 * Getter for companies
	 * @return List of all companies in the stock exchange
	 */
	public HashMap<Company, Float> getCompanies() {
		return this.companies;
	}
	
	/**
	 * Gets a random company from the stock exchange
	 * @return Random company from the stock exchange
	 */
	public Company getRandomCompany() {
		int randomID = (int)(Math.random() * companies.size());
		ArrayList<Company> ls = new ArrayList<Company>(companies.keySet());
		return ls.get(randomID);
	}
	
	/**
	 * Gets the maximum affordable shares from a company for a given balance
	 * @param c Company to buy shares from
	 * @param balance Balance of client
	 * @return Number of shares affordable
	 */
	public float getMaxAffordableShares(Company c, float balance) {
		float numAffordable = (float) Math.floor(balance / c.getPrice());
		float availableShares = (float) c.getAvailableShares();
		if (numAffordable <= availableShares) {
			return numAffordable;
		} else {
			return availableShares;
		}
	}
	
	/**
	 * Sets the price of a company, aborts if interrupted
	 * @param company Company
	 * @param price Price
	 */
	public void setPrice(Company company, float price) {
		try {
			company.acquireLock();
		} catch (InterruptedException e) {
			return;
		}
		company.setPrice(price);
		company.releaseLock();
	}
	
	/**
	 * Changes price of a company's shares by a given amount, aborts if interrupted
	 * @param company Company
	 * @param number Amount to change by
	 */
	public void changePriceBy(Company company, float number) {
		try {
			company.acquireLock();
		} catch (InterruptedException e) {
			return; //Aborts change if interrupted
		}
		float amount = company.getPrice() + number; //Read
		if (amount <= 0) {
			company.setPrice(0f); //Write
		} else {
			company.setPrice(amount); //Write
		}
		company.releaseLock();
	}
	
	/**
	 * Gets the total balance in circulation.<br>
	 * (For testing)
	 * @return Total balance in circulation
	 */
	public float getTotalBalance() {
		float totalBalance = 0;
		float shareBalance = 0;
		for (Client c: clients) {
			totalBalance += c.getBalance();
		}
		ArrayList<Company> comps = new ArrayList<Company>(companies.keySet());
		for (int i = 0; i<comps.size(); i++) {
			Company c = comps.get(i);
			shareBalance += c.getStoredBalance();
		}
		return shareBalance+totalBalance;
	}
	
	/**
	 * Gets the total number of shares in circulation<br>
	 * (For testing)
	 * @return Total number of shares in circulation
	 */
	public float getTotalShares() {
		float totalSharesSold = 0;
		float totalSharesUnsold = 0;
		for (Client c: clients) {
			totalSharesSold += c.getNumShares();
		}
		ArrayList<Company> comps = new ArrayList<Company>(companies.keySet());
		for (int i = 0; i<comps.size(); i++) {
			Company c = comps.get(i);
			totalSharesUnsold += c.getAvailableShares();
		}
		return totalSharesSold+totalSharesUnsold;
	}
	
	/**
	 * Converts the stock exchange to a readable string
	 */
	public String toString() {
		float totalBalance = 0;
		float shareBalance = 0;
		float totalSharesSold = 0;
		float totalSharesUnsold = 0;
		String str = "StockExchange:\nClients ->\n";
		for (Client c: clients) {
			str += c + "\n";
			totalBalance += c.getBalance();
			totalSharesSold += c.getNumShares();
		}
		str += "\nCompanies ->\n";
		ArrayList<Company> comps = new ArrayList<Company>(companies.keySet());
		for (int i = 0; i<comps.size(); i++) {
			Company c = comps.get(i);
			str += c + "\n";
			totalSharesUnsold += c.getAvailableShares();
			shareBalance += c.getStoredBalance();
		}
		
		str += "\nRemaining Balance: " + totalBalance + "\n" +
				"Share Balance: " + shareBalance + "\n" +
				"Total Balance: " + (totalBalance + shareBalance) + "\n" +
				"Shares Sold: " + totalSharesSold + "\n" +
				"Shares Unsold: " + totalSharesUnsold + "\n" +
				"Total Shares: " + (totalSharesUnsold+totalSharesSold) + "\n";
		return str;
	}
}
