package cs2030.simulator;
import java.lang.Math;
import java.lang.Comparable;

/** 
 * Models a discrete event in the simulator. 
 * This class "binds" all the involved parties in the system together,
 * and helps to track the statistics of the system.
 */
public class Event implements Comparable<Event> {
    /** Tracks the time that the Event occurs. */
    private final double time;

    /** Stores the Customer that the Event pertains to. */
    private final Customer customer;
    
    /** Stores the Server that the Event pertains to. */
    private final Server server;

    /** Stores the Action that the Event instance entails. */
    private final Action action;
    
    /** Tracks the total number of Customers that have received service. */
    private static int nServed = 0;

    /** Tracks the total number of Customers that left without receiving service. */
    private static int nNotServed = 0;

    /** Tracks the total time spent waiting by Customers that received service. */
    private static double totalWait = 0;

    /** Tracks the average time spent waiting by Customers that received service. */
    private static double avgWait = 0;
    
    /** 
     * Constructor instantiates Events in the cases where 
     * Customers are served, wait or done.
     * SERVED, WAITS and DONE are the only Actions that
     * involve both a Customer and a Server. 
     * Thus this constructor has to instantiate an Event
     * with both a Customer and a Server.
     * Every time a Customer is SERVED, the statistics 
     * of the system are updated as follows:
     * The number of Customers served is incremented, and 
     * the average time spent waiting is re-calculated by adding
     * this Customer's wait time to the current total wait time,
     * and dividing this value by the number of Customers served.
     * @param time the time the Event occurs
     * @param customer the Customer that the Event pertains to
     * @param server the Server that the Event pertains to
     * @param action the Action that the Event entails
     */
    public Event(double time, Customer customer, Server server, Action action) {
        this.time = time;
        this.customer = customer;
        this.server = server;
        this.action = action;
        if (action == Action.SERVED) {
            Event.nServed++;
            Event.totalWait = Event.totalWait + 
                (time - customer.get_arrivaltime());
            Event.avgWait = Event.totalWait / (Event.nServed);
        }
    }
    
    /** 
     * Overloaded constructor to instantiate Events 
     * where a Customer arrives or leaves, 
     * where there is no Server involved.
     * The count of Customers not served is incremented
     * if the Customer LEAVES. 
     * Since there is no Server involved in the ARRIVES and LEAVES Actions, 
     * the constructor does not take a Server as a parameter.
     * @param time The time the Event occurs
     * @param customer The Customer that the Event pertains to
     * @param action The Action that the Event entails
     */
    public Event(double time, Customer customer, Action action) {
        this.time = time;
        this.customer = customer;
        this.action = action;
        if (action == Action.LEAVES) {
            Event.nNotServed++;
        }
        this.server = null; 
    }

    /** 
     * Overloaded constructor to instantiate Events
     * where a Server rests or comes back from resting,
     * where there is no Customer involved.
     * Since no Customer is involved in SERVER_REST and
     * SERVER_BACK, the constructor does not take a Customer
     * as a parameter.
     * @param time The time the Event occurs.
     * @param server The Server that that the Event pertains to
     * @param action The Action that the Event entails
     */
    public Event(double time, Server server, Action action) {
        this.time = time;
        this.customer = null;
        this.server = server;
        this.action = action;
    }
    
    /** 
     * Sets the parameters for random generation in this package.
     * These parameters are used to initialize random generation systems
     * in the Customer and Server classes. Within these classes,
     * these parameters are used to simulate the random behaviour of
     * Servers and Customers respectively.
     * @param seed the random seed.
     * @param arrivalRate the arrival rate of customers.
     * @param serviceRate the service rate of servers.
     * @param restingRate the resting rate of servers.
     * @param probRest the probability a given server rests. 
     * This will be fed to the Server class, where it will be stored
     * as a static attribute, allowing the class to simulate 
     * a Server's random decision to rest.
     * @param probGreedy the probability a given customer is greedy.
     * This will be fed to the Customer class, where it will be stored
     * as a static attribute, allowing the class to simulate randomly
     * occuring greedy Customers.
     */
    public static void setRg(int seed, double arrivalRate, 
            double serviceRate, double restingRate, 
            double probRest, double probGreedy) {
        RandomGenerator rg = new RandomGenerator(seed, arrivalRate, 
                serviceRate, restingRate);
        Server.setRg(rg, probRest);
        Customer.setRg(rg, probGreedy);
    }


    @Override
    public String toString() {
        String output = String.format("%.3f", this.time);
        if (this.action == Action.ARRIVES) {
            return output += " "
                + this.customer.toString()
                + " arrives";
        } else if (this.action == Action.SERVED) {
            return output += " "
                + this.customer.toString()
                + " served by "
                + this.server.toString();
        } else if (this.action == Action.LEAVES) {
            return output += " "
                + this.customer.toString()
                + " leaves";
        } else if (this.action == Action.DONE) {
            return output += " " 
                + this.customer.toString()
                + " done serving by "
                + this.server.toString();
        } else if (this.action == Action.WAITS) {
            return output += " "
                + this.customer.toString()
                + " waits to be served by "
                + this.server.toString();
        } else {
            System.err.println("Error: shouldn't be calling toString() on Event of type " + this.action);
            return null;
        }
    }
    
    /** Returns the time that the Event occurs. */
    public double get_time() {
        return this.time;
    }
    
    /** Returns the Customer instance associated with the Event. */
    public Customer get_customer() {
        return this.customer;
    }
    
    /** Returns the Server instance associated with the Event. */
    public Server get_server() {
        return this.server;
    }
    
    /** Returns the Action associated with the Event. */
    public Action get_action() {
        return this.action;
    }
    
    /** Returns the number of customer served in the system. */
    public static int get_nServed() {
        return Event.nServed;
    }
    
    /** 
     * Returns the number of customers who left 
     * without being served in the system.
     */
    public static int get_nNotServed() {
        return Event.nNotServed;
    }

    /** 
     * Returns the average amount of time customers who
     * received service spent waiting.
     */
    public static double get_avgWait() {
        return Event.avgWait;
    }
    
    /** 
     * Compares this event instance with the specified event instance for order.
     * Returns a negative integer, zero or a positive integer, 
     * as this Event is less than, equal to or greater than the specified object.
     * Events with an earlier time of occurence are higher in priority.
     * If there is a tie, Events whose customers have a smaller id are higher in priority.
     * If there is still a tie, the relative priority of the Actions 
     * associated with the Events is used to compare the Events.
     * @param other The other event to compare to.
     */
    @Override
    public int compareTo(Event other) {
        int timeDiff = (int) Math.signum(this.get_time() 
                - other.get_time());
        if (timeDiff != 0) {
            return timeDiff;
        }
        int idDiff = this.get_customer().get_id() 
            - other.get_customer().get_id();
        if (idDiff != 0) {
            return idDiff;
        }
        int priorityDiff = this.get_action().get_priority() - other.get_action().get_priority();
        return priorityDiff;
    }

}
