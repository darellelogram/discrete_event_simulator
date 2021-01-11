import java.util.Scanner;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.stream.Stream;
import java.util.Optional;
import cs2030.simulator.RandomGenerator;
import cs2030.simulator.Customer;
import cs2030.simulator.Action;
import cs2030.simulator.Server;
import cs2030.simulator.Event;

class Main {
    /** converts a double to a String representation with 3 decimal places. */
    public static String fdouble(double d) {
        return String.format("%.3f", d);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int seed = sc.nextInt();
        int nServers = sc.nextInt();
        int nSelfCheckouts = sc.nextInt();
        int Qmax = sc.nextInt();
        int nCustomers = sc.nextInt();
        double arrivalrate = sc.nextDouble();
        double servicerate = sc.nextDouble();
        double restingrate = sc.nextDouble();
        double probresting = sc.nextDouble();
        double probGreedy = sc.nextDouble();
        
        /** Set the parameters for the Random Generator in the system. */
        Event.setRg(seed, arrivalrate, servicerate, 
                restingrate, probresting, probGreedy);
        
        /** Add Servers to list of Servers. */
        List<Server> servers = new ArrayList<>(nServers + nSelfCheckouts);
        for (int i = 1; i <= nServers; i++) {
            servers.add(Server.genHumanServer(i));
        }
        for (int i = nServers + 1; i <= nServers + nSelfCheckouts; i++) {
            servers.add(Server.genSelfCheckoutCounter(i));
        }

        /** Add arriving customers to list of Customers. */
        List<Customer> customers = new ArrayList<>(nCustomers);
        for (int i = 1; i <= nCustomers; i++) {
            customers.add(Customer.genArrival(i));
        }
        sc.close();

        PriorityQueue<Event> eventQ = new PriorityQueue<>();
        
        /** Adding arrivals of customers to eventQ. */
        for (int i = 0; i < customers.size(); i++) {
            Customer currCustomer = customers.get(i);
            eventQ.add(new Event(currCustomer.get_arrivaltime(), 
                        currCustomer, Action.ARRIVES));
        }
        
        /** loop through queue of Events. */
        while (eventQ.size() > 0) {
            Event currEvent = eventQ.poll();
            Action currAction = currEvent.get_action();
            double currTime = currEvent.get_time();

            /** 
             * For SERVER_REST or SERVER_BACK,
             * no Customer is involved in the logic.
             * So in this case, currCustomer would be null.
             * no need to print out these events either.
            */
            Customer currCustomer = currEvent.get_customer();
            if (currAction != Action.SERVER_REST && currAction != Action.SERVER_BACK) {
                System.out.println(currEvent.toString());
            }

            /** For LEAVES and ARRIVES, no Server is involved in the logic. */
            Server currServer = null; 
            if (currAction == Action.SERVED || 
                currAction == Action.WAITS || 
                currAction == Action.DONE || 
                currAction == Action.SERVER_REST || 
                currAction == Action.SERVER_BACK) {
                currServer = currEvent.get_server();
            }            

            /** 
             * possible flow paths:
             * Arrives - Served - Done
             * Arrives - Waits - Served - Done
             * Arrives - Leaves 
             */

            if (currAction == Action.ARRIVES) {
                /** 
                 * When customer arrives, first thing they do is to 
                 * check if there are any idle servers.
                 * If not, check for any non-full queues.
                 * If there are non-full queues, 
                 * typical customers join the first one
                 * but greedy customers join the one with the shortest queue.
                 * If all queues are full, the customer leaves.
                 */
                Server nextServer = servers.stream()
                    .filter(server -> server.canServe(currCustomer))
                    .findFirst()
                    .orElse(servers.stream()
                                   .filter(server -> server.get_Qlen() < Qmax)
                                   .findFirst()
                                   .orElse(null));

                if (nextServer == null) {
                    eventQ.add(new Event(currTime, currCustomer, Action.LEAVES));
                } else if (nextServer.canServe(currCustomer)) {
                    eventQ.add(new Event(currTime, currCustomer, nextServer, Action.SERVED));
                } else if (nextServer.get_Qlen() < Qmax) {
                    if (currCustomer.isGreedy()) {
                        /** The class Server implements Comparable<Server>,
                         * and their natural ordering is as follows 
                         * (from most important to least important):
                         * 1) the length of their queues
                         * 2) their id
                         */
                        nextServer = servers.stream()
                                            .sorted()
                                            .findFirst()
                                            .orElse(null);
                    }
                    if (nextServer == null) {
                        System.out.println("list of Servers is empty");
                    }
                    eventQ.add(new Event(currTime, currCustomer, nextServer, Action.WAITS));
                }                               

            } else if (currAction == Action.SERVED) {
                /**
                 * serve() updates the states of the Server
                 * and returns the time when service is done.
                 * serve() removes the customer from the server's queue
                 * and the server's next available time is updated.
                 */
                double doneTime = currServer.serve(currCustomer, currTime);

                eventQ.add(new Event(doneTime, currCustomer, currServer, Action.DONE));

            } else if (currAction == Action.WAITS) {
                /** Customer decides to wait and is added to Server's queue. */
                currServer.addToQ(currCustomer);
            } else if (currAction == Action.DONE) {
                /**
                 * If the server needs to rest,
                 * a new event is scheduled with the action SERVER_REST,
                 * and the server temporarily ignores the customers 
                 * waiting in the queue.
                 * If the server does not need to rest, 
                 * and if there are other customers waiting,
                 * the server immediately serves the next customer in line.
                 * So the SERVED event is scheduled  
                 * with the same timestamp as the current event.
                 * Otherwise, nothing is done.
                 */
                if (currServer.needsToRest()) {
                    eventQ.add(new Event(currTime, currServer, Action.SERVER_REST)); 
                } else if (currServer.get_Qlen() > 0) {
                    Customer nextCustomer = currServer.get_customerQ().peek();
                    eventQ.add(new Event(currTime, nextCustomer, currServer, Action.SERVED));
                }
            } else if (currAction == Action.SERVER_REST) {
                /** restsFrom() updates the variable tracking
                 * the next time th server is free and 
                 * returns the time the server's rest ends.
                 * Then, a new SERVER_BACK is scheduled immediately,
                 * with the same timestamp as the current event.
                 */
                double restEnd = currServer.restsFrom(currTime);
                eventQ.add(new Event(restEnd, currServer, Action.SERVER_BACK));
            } else if (currAction == Action.SERVER_BACK) {
                if (currServer.get_Qlen() > 0) {
                    Customer nextCustomer = currServer.get_customerQ().peek();
                    eventQ.add(new Event(currTime, nextCustomer, currServer, Action.SERVED));
                }
            }
        }
        /** Prints system statistics. */
        System.out.println("[" + fdouble(Event.get_avgWait()) + " " + 
                Event.get_nServed() + " " + Event.get_nNotServed() + "]");

    }
}
