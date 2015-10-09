import java.util.HashMap;
import java.util.Set;

public class Banker {
	
	private int totalUnits;
	private int unitsLeft;
	private HashMap<Thread, int[]> claimMap; 
	
	public Banker(int nUnits){
		this.totalUnits = nUnits;
		unitsLeft = totalUnits;
		this.claimMap = new HashMap();
	}
	
	public void setClaim(int nUnits){
		if(nUnits < 0 || nUnits > unitsLeft || claimMap.containsKey(Thread.currentThread())){
			System.exit(1);
		}
		int[] temp = claimMap.get(Thread.currentThread());
		temp[0] = nUnits;
		temp[1] = 0;
		claimMap.put(Thread.currentThread(), temp);
		System.out.println("Thread " + Thread.currentThread().getName()
				+ "sets  a claim for " + nUnits + " units.");
		
	}
	
	public synchronized boolean request(int nUnits){
		
		if(!(claimMap.containsKey(Thread.currentThread()))){
			return false;
		}
		else
		{
			int temp[] = claimMap.get(Thread.currentThread());
			if(temp[0] > nUnits || nUnits < 0){
				return false;
			}else{
		
				System.out.println("Thead " + Thread.currentThread().getName()
						+ "requests " + nUnits  + " units.");
				HashMap<Thread, int[]> claimMapCopy = claimMap;
				while(true){
					if(safeState(unitsLeft, claimMapCopy)){
						System.out.println("Thread " + Thread.currentThread().getName()
							+ " has " + nUnits + " units allocated. ");
						unitsLeft = unitsLeft - nUnits;
						return true;
					}
			
				System.out.println("Thead " + Thread.currentThread().getName()
						+ " waits.");
				try {
					Thread.currentThread().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
				System.out.println("Thread " + Thread.currentThread().getName()
						+ " awakened.");
				}
			}
		}
	}

	public synchronized void release(int nUnits){
		Thread t = Thread.currentThread();
		int[] temp = claimMap.get(t);
		if(nUnits < 0 || !(claimMap.containsKey(t)) || temp[0] == 0){
			System.exit(1);
		}
		System.out.println("Thread " + Thread.currentThread().getName()
				+ "releases " + nUnits + " units.");
		temp[1] = temp[1] - nUnits;
		this.unitsLeft += nUnits;
		claimMap.put(t,temp);
		notifyAll();
		return;
	}
	
	public int allocated(){
		
		int[] temp = claimMap.get(Thread.currentThread());
		return temp[1];
		
	}
	
	public int remaining(){
		
		int[] temp = claimMap.get(Thread.currentThread());
		return temp[0];
	}
	
	public boolean safeState(int units, HashMap<Thread, int[]> claimMap){
		
		int unitsOnHand = units;
		int size = claimMap.size();
		int[] allocation = new int[size];
		int[] remaining = new int[size];
		Set<Thread> threadSet = claimMap.keySet();
		int count = 0;
		for(Thread t : threadSet){
			int[] temp = claimMap.get(t);
			allocation[count] = temp[1];
			remaining[count] = temp[0];
			count++;
		}
		for (int i = 0; i < allocation.length - 1; i++){
			
			if(remaining[i] > unitsOnHand){
				return false;
			}
			else
			{
				units += allocation[i];
				
			}
		}
		return true;
	}
	
}