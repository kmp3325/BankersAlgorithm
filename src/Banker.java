import java.util.*;

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
		int[] temp = new int[2];
		temp[0] = nUnits;
		temp[1] = 0;
		claimMap.put(Thread.currentThread(), temp);
		System.out.println(Thread.currentThread().getName()
				+ " sets a claim for " + nUnits + " units.");
		
	}
	
	public synchronized boolean request(int nUnits){
		
		if(!(claimMap.containsKey(Thread.currentThread()) || claimMap.get(Thread.currentThread())[0] > nUnits || nUnits < 0)){
			System.exit(1);
		}
		else
		{
			System.out.println(Thread.currentThread().getName()
					+ " requests " + nUnits  + " units.");

			while(true){
				HashMap<Thread, int[]> claimMapCopy = clone(claimMap);
				int[] temp = claimMapCopy.get(Thread.currentThread());
				temp[1] += nUnits;
				claimMapCopy.put(Thread.currentThread(), temp);
				if(safeState(unitsLeft - nUnits, claimMapCopy.values())){
					System.out.println(Thread.currentThread().getName()
						+ " has " + nUnits + " units allocated. ");
					unitsLeft = unitsLeft - nUnits;
					temp[1] += nUnits;
					claimMap.put(Thread.currentThread(), temp);
					return true;
				}
			
				System.out.println(Thread.currentThread().getName()
						+ " waits.");
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
				System.out.println(Thread.currentThread().getName()
						+ " awakened.");
			}
		}
		return true;
	}

	private HashMap<Thread, int[]> clone(HashMap<Thread, int[]> toClone) {
		HashMap<Thread, int[]> clone = new HashMap<Thread, int[]>();
		for (Map.Entry<Thread, int[]> entry : toClone.entrySet()) {
			int[] temp = new int[2];
			temp[0] = entry.getValue()[0];
			temp[1] = entry.getValue()[1];
			clone.put(entry.getKey(), temp);
		}
		return clone;
	}

	public synchronized void release(int nUnits){
		Thread t = Thread.currentThread();
		int[] temp = claimMap.get(t);
		if(nUnits < 0 || !(claimMap.containsKey(t)) || temp[0] == 0){
			System.exit(1);
		}
		System.out.println(Thread.currentThread().getName()
				+ " releases " + nUnits + " units.");
		temp[1] = temp[1] - nUnits;
		this.unitsLeft += nUnits;
		claimMap.put(t,temp);
		notifyAll();
		return;
	}
	
	public synchronized int allocated(){
		
		int[] temp = claimMap.get(Thread.currentThread());
		return temp[1];
		
	}
	
	public synchronized int remaining(){
		
		int[] temp = claimMap.get(Thread.currentThread());
		return temp[0] - temp[1];
	}
	
	public boolean safeState(int units, Collection<int[]> claimMap){
		
		ArrayList<int[]> sortedList = new ArrayList<int[]>(claimMap);
		sortedList.sort(new Comparator<int[]>(){

			@Override
			public int compare(int[] arg0, int[] arg1) {
				if(arg0[0] - arg0[1] > arg1[0] - arg1[1]){
					return -1;
				}
				if(arg0[0] - arg0[1] < arg1[0] - arg1[1]){
					return 1;
				}
				if(arg0[0] - arg0[1] == arg1[0] - arg1[1]){
					return 0;
				}
				else{
					return -10000;
				}
			}
			
		});
		for (int i = 0; i < sortedList.size() - 1; i++){
			
			if(sortedList.get(i)[0] - sortedList.get(i)[1] > units){
				return false;
			}
			else
			{
				units += sortedList.get(i)[1];
				
			}
		}
		return true;
	}
	
}
