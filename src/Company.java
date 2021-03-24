import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Company {
	private String name;
	private float totalNumberOfShares;
	private float availableNumberOfShares;
	private float price;
	private Semaphore lock;
	private float storedBalance;
	private ArrayList<Float> notifyPrices;
	
	public Company() {
		
	}
	
	public Company(String name, float totalShares, float availableShares, float price) {
		this.setName(name);
		this.setTotalShares(totalShares);
		this.setAvailableShares(availableShares);
		this.setPrice(price);
		this.lock = new Semaphore(1);
		this.notifyPrices = new ArrayList<Float>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public synchronized void setTotalShares(float number) {
		this.totalNumberOfShares = number;
	}
	
	public float getTotalShares() {
		return this.totalNumberOfShares;
	}
	
	public synchronized void setAvailableShares(float number) {
		this.availableNumberOfShares = number;
	}
	
	public float getAvailableShares() {
		return this.availableNumberOfShares;
	}
	
	public synchronized boolean incrementAvailableShares(float n) {
		if (availableNumberOfShares+n <= totalNumberOfShares) {
			availableNumberOfShares+=n;
			storedBalance-=price*n;
			System.out.println(name + " just regained " + n + " shares! Now "
					+ availableNumberOfShares + " shares left!");
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean decrementAvailableShares(float n) {
		if (availableNumberOfShares-n >= 0) {
			availableNumberOfShares-=n;
			storedBalance+= price*n;
			System.out.println(name + " just sold " + n + " shares! Only "
					+ availableNumberOfShares + " shares left!");
			return true;
		} else {
			return false;
		}
	}
	
	public boolean tryAcquire() {
		return lock.tryAcquire();
	}
	
	public void acquireLock() {
		try {
			lock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Thread was interrupted!");
			e.printStackTrace();
		}
	}
	
	public void releaseLock() {
		lock.release();
	}
	
	public synchronized void waitForPriceToDrop(float amnt) {
		while (price > amnt) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalMonitorStateException e2) {
				System.out.println("<CRASH> Illegal monitor state!" + Thread.currentThread().getName());
				//lock.release();
				System.exit(0);
			}
		}
	}
	
	public synchronized void waitForPriceToRise(float amnt) {
		while (price < amnt) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalMonitorStateException e2) {
				System.out.println("<CRASH> Illegal monitor state!" + Thread.currentThread().getName());
				//lock.release();
				System.exit(0);
			}
		}
	}
	
	public synchronized void setPrice(float number) {
		this.price = number;
		//System.out.println(name + "'s price has changed to " + number + "!");
		notifyAll();
		//System.out.println(name + "'s price has been changed to " + number + ".");
	}
	
	public float getPrice() {
		return this.price;
	}
	
	public float getStoredBalance() {
		return this.storedBalance;
	}
	
	public String toString() {
		return name + ",\n" +
				"- Total Shares: " + totalNumberOfShares + ",\n" +
				"- Available Shares: " + availableNumberOfShares + ",\n" +
				"- Price: " + price + ",\n" +
				"- Stored Balance: " + storedBalance + ".";
	}
}
