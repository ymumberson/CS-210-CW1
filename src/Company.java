import java.util.concurrent.Semaphore;

/**
 * Models a company in a stock exchange
 * @author Yoshan Mumberson 1911116
 *
 */
public class Company {
	/**
	 * Name of the company
	 */
	private String name;
	
	/**
	 * Total number of shares of the company
	 */
	private float totalNumberOfShares;
	
	/**
	 * Available number of shares of the company
	 */
	private float availableNumberOfShares;
	
	/**
	 * Price of shares of the company
	 */
	private float price;
	
	/**
	 * Company's lock for reading and writing<br>
	 * Lock should be acquired when necessary to avoid race conditions, and released afterwards
	 */
	private Semaphore lock;
	
	/**
	 * Represents debt from buying shares back from clients<br>
	 * (Just for testing)
	 */
	private float storedBalance;
	
	/**
	 * Blank constructor
	 */
	public Company() {
		
	}
	
	/**
	 * Contructor
	 * @param name Name
	 * @param totalShares Total number of shares
	 * @param availableShares Available number of shares
	 * @param price Price of shares
	 */
	public Company(String name, float totalShares, float availableShares, float price) {
		this.setName(name);
		this.setTotalShares(totalShares);
		this.setAvailableShares(availableShares);
		this.setPrice(price);
		this.lock = new Semaphore(1);
	}
	
	/**
	 * Setter for name
	 * @param name Name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Getter for name
	 * @return Name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Setter for total number of shares
	 * @param number Total number of shares
	 */
	public synchronized void setTotalShares(float number) {
		this.totalNumberOfShares = number;
	}
	
	/**
	 * Getter for total number of shares
	 * @return Total number of shares
	 */
	public float getTotalShares() {
		return this.totalNumberOfShares;
	}
	
	/**
	 * Setter for available number of shares
	 * @param number Available number of shares
	 */
	public synchronized void setAvailableShares(float number) {
		this.availableNumberOfShares = number;
	}
	
	/**
	 * Getter for available number of shares
	 * @return Available number of shares
	 */
	public float getAvailableShares() {
		return this.availableNumberOfShares;
	}
	
	/**
	 * Increments available number of shares
	 * @param n Number to increment by
	 * @return False if n is negative or if increment would be greater than total shares, otherwise true
	 */
	public synchronized boolean incrementAvailableShares(float n) {
		if (n < 0) {return false;}
		if (availableNumberOfShares+n <= totalNumberOfShares) {
			availableNumberOfShares+=n;
			storedBalance-=price*n;
			//System.out.println(name + " just regained " + n + " shares! Now " + availableNumberOfShares + " shares left!");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Decrements available number of shares
	 * @param n Number to decrement by
	 * @return False if n is negative or if decrement would be less than zero, otherwise true
	 */
	public synchronized boolean decrementAvailableShares(float n) {
		if (n < 0) {return false;}
		if (availableNumberOfShares-n >= 0) {
			availableNumberOfShares-=n;
			storedBalance+= price*n;
			//System.out.println(name + " just sold " + n + " shares! Only " + availableNumberOfShares + " shares left!");
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Attempts to acquire the company's lock
	 * @return True of successful, otherwise false
	 */
	public boolean tryAcquire() {
		return lock.tryAcquire();
	}
	
	/**
	 * Acquires the company's lock
	 * @throws InterruptedException Throws if interrupted while waiting for the lock to become available
	 */
	public void acquireLock() throws InterruptedException {
		lock.acquire();
	}
	
	/**
	 * Releases the company's lock
	 */
	public void releaseLock() {
		lock.release();
	}
	
	/**
	 * Waits for the company's price to drop to a given limit before proceeding
	 * @param amnt Limit for the price to drop below or equal to
	 * @throws InterruptedException If the current thread is interrupted
	 */
	public synchronized void waitForPriceToDrop(float amnt) throws InterruptedException {
		while (price > amnt) {
			wait();
		}
	}
	
	/**
	 * Waits for the company's price to rise to a given limit before proceeding
	 * @param amnt Limit for price to rise above or equal to
	 * @throws InterruptedException If the current thread is interrupted
	 */
	public synchronized void waitForPriceToRise(float amnt) throws InterruptedException {
		while (price < amnt) {
			wait();
		}
	}
	
	/**
	 * Sets the companies price and notifies any threads waiting on the company
	 * @param number Price of shares
	 */
	public synchronized void setPrice(float number) {
		this.price = number;
		notifyAll();
	}
	
	/**
	 * Getter for price
	 * @return Price of shares
	 */
	public float getPrice() {
		return this.price;
	}
	
	/**
	 * Getter for stored balance
	 * @return Stored balance
	 */
	public float getStoredBalance() {
		return this.storedBalance;
	}
	
	/**
	 * Converts the company to a readable string
	 */
	public String toString() {
		return name + ",\n" +
				"- Total Shares: " + totalNumberOfShares + ",\n" +
				"- Available Shares: " + availableNumberOfShares + ",\n" +
				"- Price: " + price + ",\n" +
				"- Stored Balance: " + storedBalance + ".";
	}
}
