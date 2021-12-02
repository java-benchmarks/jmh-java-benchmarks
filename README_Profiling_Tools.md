# Java Profiling Tools

## The TECH Note
- It makes sense to use different tools particularly if they are sampling profilers
    - Sampling profilers tend to show issues differently 
    - one may pinpoint performance issues better on some applications 
    - and worse on others
- Many common Java profiling tools are themselves written in Java
    - Work by attaching themselves to the app to be profiled
        - via a socket or via a native Java interface called the JVM Tool 
          Interface (JVMTI)
        - The target app and the profiling tool then exchange info about the 
          behavior of the target application
    - Pay attention to tuning the profiling tool just as tuning any other Java app
        - If the app being profiled is large, it can transfer quite a lot of 
          data to the profiling tool
        - The profiling tool must have a sufficiently large heap to handle the data
        - Run the profiling tool with a concurrent GC algorithm as well 
        - ill-timed full GC pauses in the profiling tool can cause the buffers 
          holding the data to overflow
- One of the pitfalls of profiling is that by introducing measurement into the 
  app, you are altering its performance characteristics
- Limiting the impact of profiling will lead to results that more closely model 
  the way the application behaves under usual circumstances
- Profiling happens in one of two modes
    - Sampling mode
    - Instrumented Mode


## Sampling Profilers
### The basic mode of profiling 
### Carries the least amount of overhead
### Can be subject to all sorts of errors (可能會出現各種錯誤)
### Can use asynchronous stack collection will have fewer measurement artifacts 
### Different sampling profiles behave differently 
  - each may be better for a particular application


## Instrumented Profilers
### Instrumented profilers yield more information about an App
- but could have a greater effect on the App than a sampling profiler
### Instrumented profilers should be set up to instrument small sections 
- of the code, a few classes or packages
    - That limits their impact on the App's performance


## Blocking Methods and Thread Timelines
### Most profilers will not report methods that are blocked
- those threads are shown as being idle
### To see the time spent in those blocking calls
- The time that a thread spends inside the wait() method
    - waiting for another thread to notify it 
    - is a significant determinant of the overall execution time of 
      many Apps
- Most Java-based profilers have filter sets and other options 
    - that can be tweaked to show or hide these blocking calls 
### Threads that are blocked may or may not be a source of a performance issue
- it is necessary to examine why they are blocked
- Blocked threads can be identified by 
    - the method that is blocking 
    - a timeline analysis of the thread

> determinant noun /dɪˈtɜːrmɪnənt/ A factor that decides whether or how 
> something happens


### Examine the Execution Patterns of Threads
- the Oracle Developer Studio profiling tool


## Native Profilers

### Tools that have the capability to Profile Native Code 
- async-profiler 
- Oracle Developer Studio

### Significant operations occur in native code 
- including within native libraries and native memory allocation
- Using the native profiler to track memory usage quickly pinpointed the 
  root cause

### Sometimes the Native Code is Unexpectedly Dominating Performance
- A profiler that understands native code will quickly show we're spending 
  too much time in GC
- If compilation threads are running and taking too much CPU 
    - a native-capable profiler will show that

### Native profilers provide visibility into 
- both the JVM code and the application code

### If a native profiler shows that time in GC dominates the CPU usage 
- tuning the collector is the right thing to do 

### If it shows significant time in the compilation threads 
- that is usually not affecting the App's performance


## Java Flight Recorder

### Java Flight Recorder (JFR) is a Feature of the JVM 
- that performs lightweight performance analysis of Apps while they are running
- JFR data is a history of events in the JVM 
    - that can be used to diagnose the past performance and operations of the JVM

### JFR was originally a feature of the JRockit JVM from BEA Systems
- In JDK 11, JFR is available in open source JVMs including the AdoptOpenJDK JVMs

### How JFR Works
- A set of events is enabled
- Each time a selected event occurs, data about that event is saved (memory/file)
- The data stream is held in a circular buffer, so only the most recent events 
  are available
- The kind of events / the size of the circular buffer / where to store
    - controlled via 
        - various arguments to the JVM  
        - tools,  ``` jcmd ``` commands

### JFR is set up so that it has very low overhead
- an impact below 1% of the program’s performance
- That overhead will change 
    - as more events are enabled
    - as the threshold at which events are reported is changed

### Java Mission Control

#### Java Mission Control ``` jmc ```
- The usual Tool to examine JFR recordings 
- JDK 11 can use ``` jmc ``` version 7

### JFR Overview
#### To understand why the G1 GC collector bailed out and performed a full GC 
- (was it due to promotion failure?)
#### How the JVM has adjusted the tenuring threshold 
#### Virtually any other piece of data about 
- How and Why GC behaved as it did 

### JFR Code View