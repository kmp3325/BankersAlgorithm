public class Driver {

    private final static int nResources = 5;
    private final static int nClients = 3;
    private final static int minSleepUnits = 0;
    private final static int maxSleepUnits = 0;

    public static void main (String[] args) {
        Banker banker = new Banker(nResources);
        Client[] clients = new Client[nClients];
        for (int i = 0; i < nClients; i++) {
            clients[i] = new Client("Client " + i, banker, 4, 10, minSleepUnits, maxSleepUnits);
        }
        for (Client c : clients) {
            c.start();
        }
        for (Client c : clients) {
            try {
                c.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
