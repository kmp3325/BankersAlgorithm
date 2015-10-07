public class Banker {
	
	private int totalUnits;
	private int unitsLeft;
	
	public Banker(int nUnits){
		this.totalUnits = nUnits;
		unitsLeft = totalUnits;
	}
	
	public void setClaim(int nUnits){
		if(nUnits < 0 || nUnits > unitsLeft){
			System.exit(1);
		}
		System.out.println("Thread " + Thread.currentThread().getName()
				+ "sets  a claim for " + nUnits + " units.");
	}
	
	public boolean request(int nUnits){
		if(nUnits < 0 || nUnits > unitsLeft){
			System.exit(1);
		}
		System.out.println("Thead " + Thread.currentThread().getName()
				+ "requests " + nUnits  + " units.");
		
		return true;
	}

	public void release(int nUnits){
		if(nUnits < 0){
			System.exit(1);
		}
		System.out.println("Thread " + Thread.currentThread().getName()
				+ "releases " + nUnits + " units.");
		this.unitsLeft += nUnits;
		notifyAll();
		return;
	}
	
	public int allocated(){
		
		return 0;
	}
	
	public int remaining(){
		
		return unitsLeft;
	}
	
	public boolean checkSafe(int unitsLeft){
		return true;
	}
	
}