import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

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
		
		if(!(claimMap.containsKey(Thread.currentThread()) || claimMap.get(Thread.currentThread())[0] > nUnits || nUnits < 0)){
			System.exit(1);
		}
		else
		{
			int temp[] = claimMap.get(Thread.currentThread());
	
			System.out.println("Thead " + Thread.currentThread().getName()
					+ "requests " + nUnits  + " units.");
			@SuppressWarnings("unchecked")
			HashMap<Thread, int[]> claimMapCopy = (HashMap<Thread, int[]>) claimMap.clone();
			while(true){
				if(safeState(unitsLeft - nUnits, claimMapCopy.values())){
					System.out.println("Thread " + Thread.currentThread().getName()
						+ " has " + nUnits + " units allocated. ");
					unitsLeft = unitsLeft - nUnits;
					// Give units to claim
					return true;
				}
			
				System.out.println("Thead " + Thread.currentThread().getName()
						+ " waits.");
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
				System.out.println("Thread " + Thread.currentThread().getName()
						+ " awakened.");
			}
		}
		return true;
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
	
	public boolean safeState(int units, Collection<int[]> claimMap){
		
		ArrayList<int[]> sortedList = new ArrayList<int[]>(claimMap);
		sortedList.sort(new Comparator<int[]>(){

			@Override
			public int compare(int[] arg0, int[] arg1) {
				if(arg0[0] - arg0[1] > arg1[0] - arg1[1]){
					return 1;
				}
				if(arg0[0] - arg0[1] < arg1[0] - arg1[1]){
					return -1;
				}
				if(arg0[0] - arg0[1] == arg1[0] - arg1[1]){
					return 0;
				}
			}
			
		});
		for (int i = 0; i < sortedList.size() - 1; i++){
			
			if(sortedList.get(i)[0] > unitsOnHand){
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
