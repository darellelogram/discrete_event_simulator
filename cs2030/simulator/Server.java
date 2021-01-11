package cs2030.simulator;
import java.util.PriorityQueue;
import java.lang.Comparable;

/** 
 * Models a server that can either be a human or a self-collection counter.
 * Human servers randomly decide to take rests and for random amounts of time.
 * This behaviour is determined by a random generation system whose statistics
 * are fed in via a call to a static method in the Event class.
 * Self-collection counters by default never rest.
 * All servers maintain an ordered queue of waiting customers.
 */
public class Server implements Comparable<Server> {
    public int id;

    /** Tracks the next time the Server is free to serve a Customer. */
    private double free_after;

    /** 
     * Stores the random generator. 
     * This will enable the random generation of service times
     * when servers are serving customers.
     */
    private static RandomGenerator rg;

    /** Stores a queue of customers for human servers */
    private PriorityQueue<Customer> customerQ;

    /** Tracks the length of a human server's queue. */
    private int Qlen;

    /** Stores any server's probability of resting. */
    private static double probRest;

    /** Tracks if the server is human or a self-checkout counter. */
    private boolean isHuman;

    /** Stores a shared queue of customers for self-checkout counters. */
    private static PriorityQueue<Customer> sharedSelfCheckoutQ;

    /** Tracks the length of the shared queue for self-checkout counters. */
    private static int sharedQlen;

    /** Tracks the total number of human servers. */
    private static int nHumanServers;
    
    /** 
     * Private constructor that returns a new Server object.
     * @param id The Server's identification number.
     * @param isHuman Whether the Server to be instantiated 
     * is human or a self-checkout counter. 
     */
    private Server(int id, boolean isHuman) {
        this.id = id;
        this.free_after = 0;
        this.Qlen = 0;
        this.customerQ = new PriorityQueue<>();
        this.isHuman = isHuman;
        if (!isHuman) {
            Server.sharedSelfCheckoutQ = new PriorityQueue<>();
            Server.sharedQlen = 0;
        }
    }
    
    /** 
     * Static factory method that returns a new human Server object.
     * It also updates number of human servers.
     * @param id The Server's identification number. 
     */
    public static Server genHumanServer(int id) {
        Server.nHumanServers++;
        return new Server(id, true);
    }
    
    /** 
     * Static factory method that returns a new Server object 
     * that is a self-checkout counter.
     * @param id The Server's identification number.
     */
    public static Server genSelfCheckoutCounter(int id) {
        return new Server(id, false);
    }
    
    /** 
     * Initializes Server's static RandomGenerator.
     * @param rg The RandomGenerator object to be used.
     * @param probRest The probability that any given Server will rest.
     */
    public static void setRg(RandomGenerator rg, double probRest) {
        Server.rg = rg;
        Server.probRest = probRest;
    }
    
    /** 
     * Returns the time it takes for the Server to
     * serve a customer. 
     */
    public static double genServiceTime() {
        return rg.genServiceTime();
    }
    
    /** 
     * Returns whether the server needs to rest.
     * Return value depends on the output of 
     * Server's RandomGenerator rg and probRest, the 
     * probability that any given server will rest.
     * If the server is a self-checkout counter,
     * false is always returned.
     */
    public boolean needsToRest() {
        if (this.isHuman) {
            return Server.rg.genRandomRest() < Server.probRest;
        } else {
            return false;
        }
    }
    
    /** 
     * Updates the states of the server when resting.
     * @param currTime The time the server starts to rest
     * @return The time when the server finishes resting as a double.
     */
    public double restsFrom(double currTime) {
        double restPeriod = Server.rg.genRestPeriod();
        this.free_after = currTime + restPeriod;
        return this.free_after;
    }
    
    public int get_id() {
        return this.id;
    }
    
    /** 
     * Returns the length of the server's queue.
     * If this server is human, 
     * the instance's Qlen variable is returned.
     * If this server is a self-checkout counter,
     * the static sharedQlen variable is returned. 
     */
    public int get_Qlen() {
        if (this.isHuman) {
            return this.Qlen;
        } else {
            return Server.sharedQlen;
        }
    }
    
    /** 
     * Decrements the length of the server's queue.
     * If this server is human, 
     * the instance's Qlen variable is decremented.
     * If this server is a self-checkout counter,
     * the static sharedQlen variable is decremented. 
     */
    public void decQlen() {
        if (this.isHuman) {
            this.Qlen = Math.max(this.Qlen-1, 0);
        } else {
            Server.sharedQlen = Math.max(Server.sharedQlen-1, 0);
        }
    }

    /** 
     * Updates the states of the server when serving a customer.
     * The length of the queue is decremented, 
     * and the customer is removed from the waiting queue.
     * Prints an error message if the customer being served 
     * is not the first in the waiting queue.
     * @return The time when service is done.
     * @param customer The customer being served.
     * @param currTime The time that service begins.
     */
    public double serve(Customer customer, double currTime) { // Customer c
        PriorityQueue<Customer> Q;
        if (this.isHuman) {
            Q = this.customerQ;
        } else {
            Q = Server.sharedSelfCheckoutQ;
        }
        Q.poll();
        this.decQlen();
        if (Q.remove(customer)) {
            System.err.println("Error: Server is serving a Customer out of turn");
        }
        this.free_after = currTime + Server.genServiceTime();
        return this.free_after;
    }
    
    /** 
     * Checks if this server is available to serve the customer immediately. 
     * For this to be true, the server has to have no other customers waiting,
     * and the customer has to have arrived when the server is not occupied
     * @param customer The customer to check.
     */
    public boolean canServe(Customer customer) {
        boolean notBusy = customer.get_arrivaltime() >= this.free_after;
        if (this.isHuman) {
            return this.Qlen == 0 && notBusy;
        } else {
            return Server.sharedQlen == 0 && notBusy;
        }
    }
    
    /** 
     * Adds a customer to this server's queue.
     * If the server is human, the instance's attributes are updated.
     * If the server is a self-checkout counter, the static attributes are updated.
     * @param customer The customer to add to the queue.
     */
    public void addToQ(Customer customer) {
        if (this.isHuman) {
            this.Qlen++;
            this.customerQ.add(customer);
        } else {
            Server.sharedQlen++;
            Server.sharedSelfCheckoutQ.add(customer);
        }
    }
    
    /** 
     * Returns a PriorityQueue containing the customers
     * waiting in line for service from this server. 
     */
    public PriorityQueue<Customer> get_customerQ() {
        if (this.isHuman) {
            return this.customerQ;
        } else {
            return Server.sharedSelfCheckoutQ;
        }
    }
    
    /** Checks if this server is human or is a self-checkout counter. */
    public boolean isHuman() {
        return this.isHuman;
    }
    
    /** 
     * Returns the default id to represent that
     * a customer is waiting at a self-checkout counter.
     */
    public static int selfCheckoutId() {
        return Server.nHumanServers + 1;
    }

    /** 
     * Compares this server instance with the specified server instance, 
     * from the perspective of a greedy customer choosing whose queue to wait in.
     * Returns a negative integer, zero or a positive integer, 
     * as this Event is less than, equal to or greater than the specified object.
     * Servers with shorter queues are favoured over those with longer queues. 
     * If there are ties, servers with smaller id 
     * numbers are picked over those with larger id numbers.
     * Facilitates easy comparison when initializing queues of Servers.
     * This allows for convenient sorting and selection 
     * of servers in the case of a greedy customer.
     * @param other The other Server to compare with.
     */
    @Override
    public int compareTo(Server other) {
        int QDiff = this.get_Qlen() - other.get_Qlen();
        if (QDiff != 0) {
            return QDiff;
        }
        return this.get_id() - other.get_id();
    }

    @Override
    public String toString() {
        String output = "";
        if (this.isHuman) {
            output += "server ";
        } else {
            output += "self-check ";
        }
        output += Integer.toString(this.id);
        return output;
    }
}
