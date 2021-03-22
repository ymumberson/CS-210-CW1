public class Test implements Runnable {
		Company c;
		Float p;
		public Test(Company c, float p) {this.c = c; this.p = p;}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("Waiting for price to drop <= " + p);
			c.waitForPriceToDrop(p);
			System.out.println("Price is now <= " + p);
		}
		
	}
