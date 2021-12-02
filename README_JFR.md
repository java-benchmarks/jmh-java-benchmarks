# Java Flight Recorder

## Java Flight Recorder (JFR) is a Feature of the JVM 
- Performs lightweight performance analysis of Apps while they are running
- JFR data is a history of events in the JVM 
    - that can be used to diagnose the past performance and operations of the JVM

> JFR was originally a feature of the JRockit JVM from BEA Systems

> In JDK 11, JFR is available in open source JVMs including the AdoptOpenJDK JVMs


## How JFR Works
- A set of events is enabled
- Each time a selected event occurs, data about that event is saved (memory/file)
- The data stream is held in a circular buffer
    - so only the most recent events are available
- The kind of events / the size of the circular buffer / where to store
    - controlled via 
        - various arguments to the JVM  
        - tools,  ``` jcmd ``` commands


## JFR is set up so that it has very low overhead
- An impact below 1% of the program’s performance
- That overhead will change 
    - as more events are enabled
    - as the threshold at which events are reported is changed


## Java Mission Control

### Java Mission Control ``` jmc ```
- The usual Tool to examine JFR recordings 
- JDK 11 can use ``` jmc ``` version 7


## JFR Code View


## Overview of JFR Events

### JFR events come directly from the JVM

### They offer a level of visibility into an application 
  - that no other tool can provide
  - In Java 11, about 131 event types can be monitored with JFR

### Event Types

- Classloading
    - Number of classes loaded and unloaded
    - ___Which classloader loaded the class (Only by JFR)___
        - ___time required to load an individual class___
- Thread statistics
    - Number of threads created and destroyed; thread dumps
    - ___Which threads are blocked on locks (Only by JFR)___
        - ___the specific lock they are blocked on___
- Throwables
    - Throwable classes used by the application
    - ___Number of exceptions and errors thrown (Only by JFR)___ 
        - ___the stack trace of their creation___
- TLAB Allocation
    - Number of allocations in the heap 
        - and size of thread-local allocation buffers (TLABs)
    - ___Specific objects allocated in the heap (Only by JFR)___
        - ___and the stack trace where they are allocated___
- File and Socket I/O
    - Time spent performing I/O
    - ___Time spent per read/write call (Only by JFR)___
        - ___the specific file or socket taking a long time to read or write___
- Monitor Blocked
    - Threads waiting for a monitor
    - ___Specific threads blocked on specific monitors (Only by JFR)___
        - ___nd the length of time they are blocked___
- Code cache
    - Size of code cache and how much it contains
    - ___Methods removed from the code cache, code cache configuration (Only by JFR)___
- Code Compilation
    - Which methods are compiled, on-stack replacement (OSR) compilation 
        - and length of time to compile
- Garbage Collection
    - Times for GC, including individual phases; sizes of generations
- Profiling
    - Instrumenting and sampling profiles
    - JFR profile provides a good high-order overview


## Enabling JFR

### JFR is initially Disabled 

### Add the flag ``` -XX:+FlightRecorder ``` to the command line of the App to Enable It
- This enables JFR as a feature
- But no recordings will be made until the recording process itself is enabled
    - That can occur either through a GUI or via the command line
- In Oracle’s JDK 8, must also specify this flag (prior to the FlightRecorder flag):
    - ```-XX:+UnlockCommercialFeatures ``` (default: false)
- If forget to include these flags 
    - Can use jinfo to change their values and enable JFR
- If using ``` jmc ``` to start a recording 
    - It will automatically change these values in the target JVM if necessary

### Enabling JFR via Java Mission Control
- The easiest way to enable recording of a local app is 
    - through the Java Mission Control GUI (jmc)

### Flight Recordings are made in one of two modes
- A fixed duration (1 minute in this case) 
- Continuously
    - Best for reactive analysis
        - Lets the JVM keep the most recent events and then dump out a 
          recording in response to an event
    - A circular buffer is utilized
    - The buffer will contain the most recent events that are within the desired 
      duration and size

### Enabling JFR via the Command Line
- After enabling JFR (with the -XX:+FlightRecorder option)
- In JDK 8, the default recording parameters can be controlled when the JVM 
  starts by using the ``` -XX:+FlightRecorderOptions=string ``` parameter

#### Different ways to control: 
- How and when the actual recording should happen

#### For more flexibility, all options can be controlled with ``` jcmd ``` 
- during a run

#### To start a flight recording
``` jcmd process_id JFR.start [options_list] ```


## Selecting JFR Events

### JFR provides the best possible visibility into the JVM 
- since it is built into the JVM itself

### JFR introduces some level of overhead into an Apps 
- For routine use, JFR can be enabled to gather a substantial amount of 
  information with low overhead

### JFR is useful in performance analysis 

### JFR is useful when enabled on a production system 
- so that you can examine the events that led up to a failure

> lead up to  / lead up to something
> to be an introduction to or the cause of something 
> - the weeks leading up to the exam 
> - the events leading up to the strike


## 