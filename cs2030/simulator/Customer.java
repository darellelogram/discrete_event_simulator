package cs2030.simulator;
import java.lang.Comparable;
import java.lang.Math;

/** 
 * Models a customer in the system.
 * A customer can either be typical or greedy.
 * If all servers are busy,
 * typical customers pick the first queue that's not full, while
 * greedy customers scan all queues and join the shortest one.
 * If all queues are full, both typical and greedy customers would leave.
 * This tendency for greediness is determined by a random generation 
 * system whose statistics are fed in via a call to a static method 
 * in the Event class.
 */
public class Customer implements Comparable<Customer> {
    /** Unique identification number for every customer. */
    private final int id;
    /** Tracks when the customer arrived. */
    private final double arrivaltime;
    /** Tracks the arrival time of the previous customer. */
    private static double prevArrivalTime;
    /** Stores the Random Generator object to simulate random behaviour. */
    private static RandomGenerator rg;
    /** The probability of the customer being greedy.
      * Greedy customers seek out the shortest queues instead of queueing at
      * the first queue they see. */
    private static double probGreedy;
    /** Tracks whether the customer is greedy. */
    private boolean isGreedy;
    
    /** 
     * Private constructor that returns a new Customer.
     * @param id The unique identification number to be 
     * given to the instantiated Customer.
     * @param at The time at which the instantiated 
     * customer is scheduled to arrive.
     * @param isGreedy Whether the Customer to be instantiated is greedy.
     */
    private Customer(int id, double at, boolean isGreedy) {
        this.id = id;
        this.arrivaltime = at;
        this.isGreedy = isGreedy;
    }
    
    /**
     * Static factory method that decides if the new instance 
     * of Customer is greedy, and then returns a new Customer.
     * The static Random Generator object stored by the Customer 
     * class is used to determine whether the Customer generated is greedy. 
     * It calls the private constructor to instantiate the Customer.
     * @param id The unique identification number to be 
     * given to the instantiated Customer.
     */
    public static Customer genArrival(int id) {
        double at = 0;
        if (id != 1) {
            at = Customer.prevArrivalTime + rg.genInterArrivalTime();
        }
        Customer.prevArrivalTime = at;
        boolean isGreedy = Customer.rg.genCustomerType() < Customer.probGreedy;
        return new Customer(id, at, isGreedy);
    }
    
    /**
     * Static method to set the Random Generator object to be stored
     * as a static attribute of the Customer class.
     * This object will be used to simulate random behaviour, 
     * i.e. whether the customer is greedy.
     */
    public static void setRg(RandomGenerator rg, double probGreedy) {
        Customer.rg = rg;
        Customer.probGreedy = probGreedy;
    }

    /** Returns the unique identification number of the current instance of Customer. */
    public int get_id() {
        return this.id;
    }
    
    /** Returns the time of arrival of the current instance of Customer. */
    public double get_arrivaltime() {
        return this.arrivaltime;
    }
    
    /** 
     * Compares this customer instance with the specified customer instance for order
     * in terms of the customer's position in a queue waiting to be served.
     * Returns a negative integer, zero or a positive integer, 
     * as this Event is less than, equal to or greater than the specified object.
     * Naturally, customers who arrive first are given higher priority.
     * If there is a tie, customers with the smaller id number get the higher priority.
     * Since id numbers are unique, there can be no further ties.
     * @param other The other Customer to compare to.
     */
    @Override
    public int compareTo(Customer other) {
        int timeDiff = (int) Math.signum(this.get_arrivaltime() 
                - other.get_arrivaltime());
        if (timeDiff != 0) {
            return timeDiff;
        }
        int idDiff = this.get_id() - other.get_id();
        return idDiff;
    }
    
    /** Returns whether the current instance of Customer is greedy. */
    public boolean isGreedy() {
        return this.isGreedy;
    }

    @Override
    public String toString() {
        if (this.isGreedy) {
            return this.id + "(greedy)";
        } else {
            return Integer.toString(this.id);
        }
    }
            
            
}
