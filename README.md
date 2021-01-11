# discrete_event_simulator
This project aims to simulate discrete events, modelled by queues formed by customers at a restaurant. In this simulation, there are a fixed number of servers, and each customer takes a random amount of time to be served. We also take into account service staff who take breaks randomly, and for random amounts of time. Can be extrapolated to model other types of discrete events.

## Input to the program comprises (in order of presentation):
> an int value denoting the base seed for the RandomGenerator object  
an int value representing the number of servers  
an int value representing the number of self-checkout counters, Nself  
an int value for the maximum queue length, Qmax  
an int representing the number of customers (or the number of arrival events) to simulate  
a positive double parameter for the arrival rate, λ  
a positive double parameter for the service rate, μ  
a positive double parameter for the resting rate, ρ  
a double parameter for the probability of resting, Pr  
a double parameter for the probability of a greedy customer occurring, Pg  

### Test run:
```
$ echo "1 2 1 2 20 1.0 1.0 0.1 0.5 0.9" | java Main  
0.000 1(greedy) arrives  
0.000 1(greedy) served by server 1  
0.313 1(greedy) done serving by server 1  
0.314 2(greedy) arrives  
0.314 2(greedy) served by server 1  
0.417 2(greedy) done serving by server 1  
1.205 3(greedy) arrives  
1.205 3(greedy) served by server 2  
1.904 3(greedy) done serving by server 2  
2.776 4(greedy) arrives  
2.776 4(greedy) served by server 2  
2.791 4(greedy) done serving by server 2  
3.877 5(greedy) arrives  
3.877 5(greedy) served by server 1  
3.910 6(greedy) arrives  
3.910 6(greedy) served by server 2  
3.922 6(greedy) done serving by server 2  
4.031 5(greedy) done serving by server 1  
9.006 7(greedy) arrives  
9.006 7(greedy) served by server 1  
9.043 8(greedy) arrives  
9.043 8(greedy) served by server 2  
9.105 9(greedy) arrives  
9.105 9(greedy) served by self-check 3  
9.160 10 arrives  
9.160 10 waits to be served by server 1  
9.225 11(greedy) arrives  
9.225 11(greedy) waits to be served by server 2  
9.402 9(greedy) done serving by self-check 3  
10.148 12(greedy) arrives  
10.148 12(greedy) served by self-check 3  
10.200 12(greedy) done serving by self-check 3  
10.484 7(greedy) done serving by server 1  
10.484 10 served by server 1  
11.205 13(greedy) arrives  
11.205 13(greedy) served by self-check 3  
11.574 13(greedy) done serving by self-check 3  
11.636 8(greedy) done serving by server 2  
12.429 14(greedy) arrives  
12.429 14(greedy) served by self-check 3  
12.589 14(greedy) done serving by self-check 3  
13.109 15(greedy) arrives  
13.109 15(greedy) served by self-check 3  
13.974 10 done serving by server 1  
15.264 16 arrives  
15.264 16 served by server 1  
15.500 11(greedy) served by server 2  
15.524 17(greedy) arrives  
15.524 17(greedy) waits to be served by server 1  
15.940 18(greedy) arrives  
15.940 18(greedy) waits to be served by server 2  
15.978 15(greedy) done serving by self-check 3  
16.159 16 done serving by server 1  
16.159 17(greedy) served by server 1  
16.166 17(greedy) done serving by server 1  
16.543 11(greedy) done serving by server 2  
16.543 18(greedy) served by server 2  
17.119 18(greedy) done serving by server 2  
17.793 19(greedy) arrives  
17.793 19(greedy) served by server 2  
18.535 19(greedy) done serving by server 2  
18.765 20(greedy) arrives  
18.765 20(greedy) served by server 2  
21.773 20(greedy) done serving by server 2  
[0.442 20 0]  
```
