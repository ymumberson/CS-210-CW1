import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class StockExchange {
	private HashMap<Company, Float> companies;
	private ArrayList<Client> clients;
	
	public StockExchange() {
		companies = new HashMap<Company, Float>();
		clients = new ArrayList<Client>();
	}
	
	public boolean registerCompany(Company company, float numberOfShares) {
		if (companies.containsKey(company)) {
			return false; //Company already exists
		} else {
			companies.put(company, numberOfShares);
			//System.out.println("New company registered!\n{"+company+"}\n");
			return true;
		}
	}
	
	public boolean deregisterCompany(Company company) {
		if (companies.containsKey(company)) {
			companies.remove(company);
			//System.out.println("Old company removed!\n{"+company+"}\n");
			return true;
		} else {
			return false;//Company doesn't exist
		}
	}
	
	public boolean addClient(Client client) {
		if (clients.contains(client)) {
			return false;//Client already exists
		} else {
			clients.add(client);
			//System.out.println("New client registered!\n{"+client+"}\n");
			return true;
		}
	}
	
	public boolean removeClient(Client client) {
		if (clients.contains(client)) {
			clients.remove(client);
			//System.out.println("Old client removed!\n{"+client+"}\n");
			return true;
		} else {
			return false;//Client doesn't exist
		}
	}
	
	public ArrayList<Client> getClients() {
		return this.clients;
	}
	
	public HashMap<Company, Float> getCompanies() {
		return this.companies;
	}
	
	public Company getRandomCompany() {
		int randomID = (int)(Math.random() * companies.size());
		ArrayList<Company> ls = new ArrayList<Company>(companies.keySet());
		return ls.get(randomID);
	}
	
	public int getMaxAffordableShares(Company c, float balance) {
		int numAffordable = (int) Math.floor(balance / c.getPrice());
		int availableShares = (int) c.getAvailableShares();
		if (numAffordable <= availableShares) {
			return numAffordable;
		} else {
			return availableShares;
		}
	}
	
	public void setPrice(Company company, float price) {
		company.acquireLock();
		company.setPrice(price);
		company.releaseLock();
	}
	
	//TODO potential race conditions
	public void changePriceBy(Company company, float number) {
		company.acquireLock();
		float amount = company.getPrice() + number; //Read
		if (amount <= 0) {
			company.setPrice(0f); //Write
		} else {
			company.setPrice(amount); //Write
		}
		company.releaseLock();
	}
	
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
