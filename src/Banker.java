import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Banker {
	
	private final int totalUnits;
	private int unitsLeft;
	private final Map<Thread, int[]> claimMap;
	
	public Banker(int nUnits){
		this.totalUnits = nUnits;
		unitsLeft = totalUnits;
		this.claimMap = new HashMap();
	}
	
	public synchronized void setClaim(int nUnits){
		if(nUnits < 0 || nUnits > unitsLeft || claimMap.containsKey(Thread.currentThread())){
			System.out.println(Thread.currentThread().getName() + " is claiming an improper amount of resources.");
			System.exit(1);
		}
		claimMap.put(Thread.currentThread(), new int[] {nUnits, 0});
		System.out.println(Thread.currentThread().getName() + " sets a claim for " + nUnits + " units.");
	}
	
	public synchronized boolean request(int nUnits){
		if(!(claimMap.containsKey(Thread.currentThread()) || claimMap.get(Thread.currentThread())[0] > nUnits || nUnits < 0)) {
			System.out.println(Thread.currentThread().getName() + " is requesting an improper amount of resources.");
			System.exit(1);
		}

		System.out.println(Thread.currentThread().getName() + " requests " + nUnits  + " units.");

		Map<Thread, int[]> claimMapClone = clone(claimMap);
		claimMapClone.get(Thread.currentThread())[1] += nUnits;

		if (isStateSafe(unitsLeft, clone(claimMap))) {
			System.out.println(Thread.currentThread().getName() + " has " + nUnits + " units allocated. ");
			claimMap.get(Thread.currentThread())[1] += nUnits;
			return true;
		}

		System.out.println(Thread.currentThread().getName() + " waits.");
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println(Thread.currentThread().getName() + " awakened.");
		return request(nUnits);
	}

	private boolean isStateSafe(int nUnitsOnHand, Map<Thread, int[]> claimMap) {
		List<int[]> claimPairs = new ArrayList<>(claimMap.values());

		claimPairs.sort((pairA, pairB) -> {
			int remainingA = pairA[0] - pairA[1];
			int remainingB = pairB[0] - pairB[1];
			return remainingA - remainingB;
		});

		for (int i = 0; i < claimPairs.size() - 1; i++) {
			if (claimPairs.get(i)[0] - claimPairs.get(i)[1] > nUnitsOnHand) {
				return false;
			}
			nUnitsOnHand += claimPairs.get(i)[1];
		}

		return true;
	}

	private Map<Thread, int[]> clone(Map<Thread, int[]> toClone) {
		Map<Thread, int[]> clone = new HashMap<>();
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
			System.out.println(Thread.currentThread().getName() + " is releasing an improper amount of resources.");
			System.exit(1);
		}

		System.out.println(Thread.currentThread().getName() + " releases " + nUnits + " units.");
		temp[1] = temp[1] - nUnits;
		unitsLeft += nUnits;
		notifyAll();
		return;
	}
	
	public synchronized int allocated(){
		return claimMap.get(Thread.currentThread())[1];

	}
	
	public synchronized int remaining(){
		int[] temp = claimMap.get(Thread.currentThread());
		return temp[0] - temp[1];
	}
}
