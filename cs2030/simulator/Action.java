package cs2030.simulator;
import java.lang.Comparable;

/** 
 * Enumerates the different actions a Customer could undertake. 
 * Each type of action has a natural priority in the system, 
 * according to the possible flow paths laid out in the looping logic in Main.
 * possible flow paths:
 * 
 * <p>Arrives - Served - Done</p>
 *
 * <p>Arrives - Waits - Served - Done</p>
 *
 * <p>Arrives - Leaves</p>
 *
 * A Server taking a rest can randomly occur after each Done event, 
 * otherwise, it does not interfere with this logic.
 * Each enumerated Action is instantiated with an integer,
 * representing its inherent natural priority. 
 * The lower the integer, the higher the priority in the discrete event system.
 * For example, if a customer arrives, and there happens to be an idle server, 
 * there would be 2 events scheduled at the same timestamp:
 *
 * <p>the ARRIVES event of the second customer, and</p>
 * 
 * <p>and the SERVES event of the second customer.</p>
 * 
 * It is obvious that the ARRIVES event should take precedence
 * over the SERVES event. Similarly, if a customer arrives, 
 * and all servers have full queues, the customer would leave immediately,
 * at the same timestamp as his arrival event. 
 * This priority ordering makes for easy comparison of Events,
 * ensuring that the sequence of events is not jumbled up 
 * in the priority queue of Events instantiated in Main,
 * helping to effectively model the system.
 */
public enum Action implements Comparable<Action> {
    /** 
     * Models a Server taking a rest. When a Server chooses to rest,
     * this takes precedence over all other things, and he ignores any 
     * other Events occuring, such as a new Customer arriving, or
     * a waiting queue of Customers. Thus, this has the highest priority.
     */
    SERVER_REST(0),

    /** 
     * Models a Server coming back from a break. Tied with SERVER_REST
     * for highest priority. The Server has to come back from
     * resting before he can do anything else, like serve a customer.
     */
    SERVER_BACK(1),

    /** 
     * Models a Customer arriving.      
     * For any given Customer entering the system, they have to ARRIVE first,
     * before doing anything else, like leaving, waiting, or getting served. 
     * Thus this has the highest priority among all Actions pertaining to Customers.
     */
    ARRIVES(2),

    /** 
     * Models a Customer leaving. The only time a Customer would leave is after arriving. 
     * Therefore, it has to have lower priority than ARRIVES.
     * The priority of the LEAVES Action in relation to WAITS, SERVED and DONE does not matter,
     * as these events never occur in the same flow of events as LEAVES does.
     */
    LEAVES(3),

    /** 
     * Models a Customer waiting in queue for service. 
     * WAITS has to come after a Customer arrives, but before that same Customer is SERVED.
     * Therefore, it has lower priority than ARRIVES but higher priority than SERVED.
     */
    WAITS(4),

    /** 
     * Models a Customer reaching the front of the queue and being served. 
     * SERVED events can only be scheduled under either of these two contexts: 
     * directly after a customer ARRIVES, or after the customer WAITS in queue.
     * Therefore, it has lower priority than both ARRIVES and WAITS.
     */
    SERVED(5),

    /** 
     * Models a Customer done being served. Being DONE receiving service is the last 
     * possible thing that could happen to a Customer entering the system.
     */
    DONE(6);
    
    /** Tracks how much priority each type of Action should be given by the system. */
    private final int priority;
    
    /** Constructor returns an Action associated with its particular priority. */
    Action(int p) {
        this.priority = p;
    }
    
    /** Returns an integer representing the priority of the Action. */
    public int get_priority() {
        return priority;
    }

}

