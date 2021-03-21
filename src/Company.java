import java.util.concurrent.Semaphore;

public class Company {
	private String name;
	private float totalNumberOfShares;
	private float availableNumberOfShares;
	private float price;
	private Semaphore lock;
	
	public Company() {
		
	}
	
	public Company(String name, float totalShares, float availableShares, float price) {
		this.setName(name);
		this.setTotalShares(totalShares);
		this.setAvailableShares(availableShares);
		this.setPrice(price);
		this.lock = new Semaphore(1);
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
	
	public synchronized boolean incrementAvailableShares(int n) {
		if (availableNumberOfShares+n <= totalNumberOfShares) {
			availableNumberOfShares+=n;
			System.out.println(name + " just regained " + n + " shares! Now "
					+ availableNumberOfShares + " shares left!");
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean decrementAvailableShares(int n) {
		if (availableNumberOfShares-n >= 0) {
			availableNumberOfShares-=n;
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
	
	public synchronized void setPrice(float number) {
		this.price = number;
	}
	
	public float getPrice() {
		return this.price;
	}
	
	public String toString() {
		return name + ",\n" +
				"- Total Shares: " + totalNumberOfShares + ",\n" +
				"- Available Shares: " + availableNumberOfShares + ",\n" +
				"- Price: " + price + ".";
	}
}
