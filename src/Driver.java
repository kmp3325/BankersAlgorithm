public class Driver {
    static int clientsN = 0;
    public static void main (String[] args) {
        Banker banker = new Banker();
        Client[] clients = new Client[clientsN];
        for (int i = 0; i < clientsN; i++) {
            clients[i] = new Client("name", banker, 0, 0, long minSleepMillis, long maxSleepMillis)
        }
        Client[] clients = new Client[]{
                new Client(),
                new Client()
        };
        for (Client c : clients) {
            c.start();
        }
    }
}
