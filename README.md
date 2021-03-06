# Extended Petri net and its applications 

 ![Example of Petri Net (Source: Wikipedia)](./petrinet_wikipedia.png)

Implementation of a package with thread-safe Petri net representation and two programs to modelling distributed system with it.
Project for the course "[Concurrent programming](https://usosweb.mimuw.edu.pl/kontroler.php?_action=katalog2%2Fprzedmioty%2FpokazPrzedmiot&prz_kod=1000-213bPW&lang=en)" at my Computer Science studies. 

### What Petri Net is?
According to Wikipedia: https://en.wikipedia.org/wiki/Petri_net
> A Petri net consists of places, transitions, and arcs. Arcs run from a place to a transition or vice versa, never between places or between transitions. The places from which an arc runs to a transition are called the input places of the transition; the places to which arcs run from a transition are called the output places of the transition.
> Graphically, places in a Petri net may contain a discrete number of marks called tokens. Any distribution of tokens over the places will represent a configuration of the net called a marking. In an abstract sense relating to a Petri net diagram, a transition of a Petri net may fire if it is enabled, i.e. there are sufficient tokens in all of its input places; when the transition fires, it consumes the required input tokens, and creates tokens in its output places. A firing is atomic, i.e. a single non-interruptible step.
> Unless an execution policy is defined, the execution of Petri nets is nondeterministic: when multiple transitions are enabled at the same time, they will fire in any order.
> Since firing is nondeterministic, and multiple tokens may be present anywhere in the net (even in the same place), Petri nets are well suited for modeling the concurrent behavior of distributed systems. 

If you are interested in seeing how it works, I __really(!)__ recommend you this interactive tutorial:
* https://www.informatik.uni-hamburg.de/TGI/PetriNets/introductions/aalst/

## `petrinet` package 
A core part of this repository is package `petrinet`. It contains two required classes: `PetriNet` and `Transition`. 
The whole package is documented in Doxygen-like comments. 

According to the requirements, there are two most important functions:
* `reachable(Collection<Transition<T>> transitions)` - return set of all possible marking reachable in net with given transitions
* `fire(transitions)` - fire one available transition from given, thread wait if none is available
 
It is safely run those functions concurrently.

I also add `TransitionBuilder` class to easier construction of Transition objects (Builder design pattern).


## Modelled with Petri net
This repository contains two programs using implemented by me Petri net package. They were part of an assignment and model a disturbed systems.



#### Alternator

The realisation of mutual exclusion with additional requirement: threads must not get critical section two times in a row.

Through 30 seconds 3 threads fire their transitions in Petri net to get access to the critical section, where they print their name and single dot.

Transitions in program:
![Transitions in Alternator](./petrinet/src/alternator/Petrinet_alternator.jpg)


#### Multiplicator
The program sets the number of tokens in two Places in net depending on numbers `A` and `B` read from standard input. Then create a constant number of threads, whose concurrently fire set of Transitions in this net.
Finally, when only one Transition will be available, the number of tokens in particular Place in net will be equal to `A * B`. Program prints result of multiplication to standard output.

Transitions in program:
![Transitions in Multiplicator](./petrinet/src/multiplicator/Petrinet_multiplicator.jpg)