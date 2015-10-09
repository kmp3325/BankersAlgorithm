public class Client extends Thread {

    private final Banker banker;
    private final int nUnits;
    private final int nRequests;
    private final long minSleepMillis;
    private final long maxSleepMillis;

    public Client(String name, Banker banker, int nUnits, int nRequests, long minSleepMillis, long maxSleepMillis) {
        super(name);
        this.banker = banker;
        this.nUnits = nUnits;
        this.nRequests = nRequests;
        this.minSleepMillis = minSleepMillis;
        this.maxSleepMillis = maxSleepMillis;
    }

    public void run() {
        banker.setClaim(nUnits);
        for (int i = 0; i < nRequests; i++) {
            if (banker.remaining() == 0) {
                banker.release(banker.allocated());
            } else {
                banker.request((int) (Math.random() * banker.remaining()) + 1);
            }
            try {
                Thread.sleep((long)(Math.random() * (maxSleepMillis - minSleepMillis)) + minSleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        banker.release(banker.allocated());
    }
}
