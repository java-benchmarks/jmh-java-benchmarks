# Working with the JIT Compiler


## Overview

### JIT Compiler is the heart of the Java Virtual Machine
- nothing controls the performance of your app more than the JIT compiler 

### How the compiler works
- discusses the advantages and disadvantages of using a JIT compiler

### Some intermediate and advanced tunings of the compiler

### If an app is running slowly without any obvious reason
- Tunings of the compiler can help determine whether the compiler is at fault


### Machine Code
- CPUs can execute only a relatively few, specific instructions, which are 
  called Machine Code
- All programs that the CPU executes must therefore be translated into these 
  instructions

### Java Apps are compiled into an intermediate low-level language
- Called ___Java Bytecode___
- then run by the java binary
- Gives Java the platform independence of an interpreted language
- Java program is able to compile the code into the platform binary as the code 
  executes
    - because it is executing an idealized binary code 
    - This compilation occurs as the program is executed
        - it happens ___Just-in-Time___
- Still subject to platform dependencies

> subject to / subject <somebody or something> to something
> to make somebody/something experience, suffer or be affected by something, 
> usually something unpleasant 
> - to be subjected to ridicule
> - The city was subjected to heavy bombing 
> - The defence lawyers claimed that the prisoners had been subjected to cruel 
    and degrading treatment


### HotSpot Compilation

#### HotSpot is Oracle's JVM Implementation
- comes from the approach it takes toward compiling the code

#### In a typical program, only a small subset of code is executed frequently 
- and the performance of an app depends primarily on how fast those sections 
  of code are executed

#### The Critical Sections are known as the hot spots of the App
- The more the section of code is executed, the hotter that section is said to be

#### When the JVM executes code, it does not begin compiling the code immediately
- If the code is going to be executed only once, then compiling it is 
  essentially a wasted effort
    - The compiler can figure out which methods are called frequently enough 
        - to warrant their compilation
- The more times that the JVM executes a particular method or loop, the more 
  information it has about that code
    - Allows the JVM to make numerous optimizations when it compiles the code


### Registers and Main Memory

#### Retrieving a value from main memory is an expensive operation 
- that takes multiple cycles to complete

#### One Thread cannot see the value of a variable stored in the register 
- used by another thread
- Synchronization makes it possible to know exactly 
    - when the register is stored to main memory and available to other threads

#### Register usage is a general optimization of the Compiler
- and typically the JIT will aggressively use registers


### The Summary Of this Overview

#### Java is designed to take advantage of the platform independence of 
- scripting languages 
- and the native performance of compiled languages

#### A Java class file is compiled into an intermediate language (Java bytecodes) 
- that is then further compiled into assembly language by the JVM 

#### Compilation of the bytecodes into assembly language performs optimizations 
- that greatly improve performance


## Tiered Compilation

### Once upon a time, the JIT compiler came in two flavors
- the client and server compilers

### Today, all shipping JVMs include both compilers 
- though in common usage, they are usually referred to as ___Server JVMs___
- Despite being called server JVMs, the distinction between client and server 
  compilers persists
- both compilers are available to and used by the JVM

### Historically 
- ``` C1 ``` (compiler 1, client compiler) 
- ``` C2 ``` (compiler 2, server compiler)

### The primary difference between the two C1 and C2 is their Aggressiveness 
- in compiling code

### The C1 compiler begins compiling sooner than the C2 compiler does
- during the beginning of code execution, the C1 compiler will be faster 
    - because it will have compiled correspondingly more code than the C2 compiler

### The Engineering TRADE_OFF 
- is the knowledge the C2 compiler gains while it waits
- that knowledge allows the C2 compiler to make better optimizations 
  in the compiled code

### Code produced by the ``` C2 ``` is faster than that produced by the ``` C1 ```
- The benefit to that TRADE_OFF is based on
    - how long the program will run 
    - and how important the startup time of the program is

### Why Tiered Compilation
- Why there needed to be a choice at all (``` C1 ``` or ``` C2 ```) 
    - Couldn’t the JVM start with the ``` C1 ``` 
    - and then use the ``` C2 ``` as code gets hotter?

### Tiered Compilation is the technique all JVMs now use

### Tiered Compilation can be explicitly Disabled with the ``` -XX:-TieredCompilation ``` flag
- the default value of which is true


## Common Compiler Flags

### Overview

#### Two commonly used flags affect the JIT compiler

### Tuning the Code Cache

#### JVM holds the set of assembly-language instructions in the code cache
- When the JVM compiles code

#### The code cache has a fixed size

#### Once it has filled up, the JVM is not able to compile any additional code

#### The potential issue caused by too small code cache
- Some hot methods will get compiled, but others will not
- the application will end up running a lot of (very slow) interpreted code

#### No good mechanism to figure out how much code cache a particular apps need
- A typical option is to simply double or quadruple the default

#### The maximum size of the code cache is set via the 
- ``` -XX:ReservedCodeCacheSize=N ``` flag
- N is the default just mentioned for the particular compiler

#### The code cache is managed like most memory in the JVM
- there is an initial size (specified by -XX:InitialCodeCacheSize=N)

#### Allocation of the code cache size starts at the initial size 
- and increases as the cache fills up
- The initial size of the code cache is 2496 KB
- The default maximum size is 240 MB
- Resizing the cache happens in the background 
    - and doesn’t really affect performance
- Setting the ``` ReservedCodeCacheSize ``` size 
    -(i.e., setting the maximum code cache size) is all that is generally needed
- If a 1 GB code cache size is specified, the JVM will reserve 1 GB of native memory
    - That memory isn't allocated until needed 
    - but it is still reserved

### In Java 11, the code cache is segmented into three parts:
- Nonmethod code
- Profiled code
- Nonprofiled code

#### By default, the code cache is sized the same way (up to 240 MB)
- The nonmethod code segment is allocated space according to the number of 
  compiler threads
    - On a machine with four CPUs, it will be about 5.5 MB
    - The other two segments then equally divide the remaining total code cache
- Rarely need to tune these segments individually

``` 
-XX:NonNMethodCodeHeapSize=N: for the nonmethod code
-XX:ProfiledCodeHapSize=N for the profiled code
-XX:NonProfiledCodeHapSize=N for the nonprofiled code
```


### Inspecting the Compilation Process

#### ``` -XX:+PrintCompilation ``` Flag - which by default is false
- Gives us visibility into the workings of the compiler

#### If ``` -XX:+PrintCompilation ``` is enabled 
- every time a method (or loop) is compiled, the JVM prints out a line with 
  information about what has just been compiled 

#### The Log Formate
- ``` timestamp compilation_id attributes (tiered_level) method_name size deopt ```
    - The ``` timestamp ``` is the time after the compilation has finished
        - relative to 0, which is when the JVM started
    - The ``` compilation_id ``` is an internal task ID
    - The ``` attributes ``` field is a series of five characters 
        - that indicates the state of the code being compiled
        - If a particular attribute applies to the given compilation, 
            - the character shown in the following list is printed 
            - otherwise, a space is printed for that attribute 
            - the five-character attribute string may appear as two or more 
              items separated by space
        ```
          % The compilation is OSR (on-stack replacement)
          s The method is synchronized 
          ! The method has an exception handler 
          b Compilation occurred in blocking mode 
          n Compilation occurred for a wrapper to a native method
        ```

#### Compile Asynchronously
- When the JVM decides that a certain method should be compiled
    - that method is placed in a queue
- The JVM then continues interpreting the method
- At the next time the method is called, the JVM will execute the compiled 
  version of the method 
- For a long-running loop
    - the loop itself should be compiled and will queue that code for compilation
    - When the code for the loop has finished compiling
        - the JVM replaces the code (on stack)
        - and the next iteration of the loop will execute the much faster 
          compiled version of the code

#### Blocking Mode
- The blocking flag will never be printed by default in current versions of Java 
    - it indicates that compilation did not occur in the background

#### the native attribute indicates that the JVM generated compiled code 
- to facilitate the call into a native method

#### ``` tiered_level ``` will be blank if tiered compilation has been disabled

#### The name of the method being compiled 
- (or the method containing the loop being compiled for OSR) 
- which is printed as ClassName::method 

#### The size (in bytes) of the code being compiled
- the size of the Java bytecodes
- not the size of the compiled code

#### Deoptimization has occurreda 
- message at the end of the compilation line will indicate that some sort of 
  deoptimization has occurred


### Inspecting Compilation with ``` jstat ```

#### Supplies summary information about the number of methods compiled
- ``` jstat -compiler 28716 ```
- 28716 is the process-id
- Compiled Failed Invalid   Time   FailedType FailedMethod
        94      0       0   0.01   0

#### Get information about the last method that is compiled
- ``` jstat -printcompilation 28716 ```

#### Get information about the last method that is compiled in every 1000ms
- ``` jstat -printcompilation 28716 1000 ```
- Compiled  Size  Type    Method
        94    10     1    java/util/regex/Matcher groupCount
        97    35     1    java/nio/CharBuffer arrayOffset

#### The Failed Compilation Log
- timestamp compile_id COMPILE SKIPPED: reason
- Indicates that something has gone wrong 
    - with the compilation of the given method
- Reasons: 
    - Code Cache Filled 
        - Should be increased using the ``` ReservedCodeCache ``` flag
    - Concurrent Classloading
        - The class was modified as it was being compiled
        - The JVM will compile it again later
        - Should expect to see the method recompiled later in the log
        - Refactor the code into something simpler that the compiler can handle


### Tiered Compilation Levels

#### There are five levels of compilation
- The C1 compiler has three levels
- All methods start at level 0
- If a method runs often enough, it will get compiled at level 4
    - Tier-0 Interpreted code
    - Tier-1 Simple C1 compiled code
    - Tier-2 Limited C1 compiled code
    - Tier-3 Full C1 compiled code
    - Tier-4 C2 compiled code

#### The most frequent path
- The C1 compiler waits to compile something until it has information about 
    - how the code is used that it can leverage to perform optimizations
- If the C2 compiler queue is full, methods will be pulled from the C2 queue 
    - and compiled at level 2, which is the level at which the C1 compiler 
      uses the invocation and back-edge counters
    - This gets the method compiled more quickly
    - The method will later be compiled at level 3 
        - After the C1 compiler has gathered profile information
        - and finally compiled at level 4 when the C2 compiler queue is less busy
- If the C1 compiler queue is full 
    - A method that is scheduled for compilation  at level 3 
        - May become eligible for level 4 compilation while still waiting to be 
          compiled at level 3
        - In this case, it is quickly compiled to level 2 
            - and then transitioned to level 4
- Trivial methods may start in either level 2 or 3 but then go to level 1 
    - because of their trivial nature
- If the C2 compiler for some reason cannot compile the code 
    - It will also go to level 1
    - When code is deoptimized, it goes to level 0

#### The best Compilation Level Case for Performance 
- Happens when methods are compiled as expected:
    - tier 0 → tier 3 → tier 4
- If methods frequently get compiled into Tier-2 and extra CPU cycles are available
    - Consider increasing the number of compiler threads to reduce the size of 
      the C2 compiler queue
- If no extra CPU cycles are available, all you can do is attempt to reduce the 
  size of the application


### Deoptimization

> Deoptimization means that the compiler has to UNDO a previous compilation
> - The effect is that the performance of the application will be reduced

#### Deoptimization occurs in two cases
- When code is made not entrant 
- When code is made zombie

#### Not Entrant Code
- Two things cause code to be made not entrant
    - One is due to the way classes and interfaces work 
    - and one is an implementation detail of tiered compilation

#### Deoptimizing Zombie Code

> Compiled code is held in a fixedsize code cache

- Remove zombie code
    - When zombie methods are identified, the code in question can be removed 
      from the code cache 
        - making room for other classes to be compiled


### Summary of Common Compiler Flags

#### The best way to gain visibility into how code is being compiled is 
 - by enabling ``` PrintCompilation ```

#### Output from Enabling ``` PrintCompilation ``` can be used to make sure 
- that compilation is proceeding as expected

#### Tiered Compilation can operate at five distinct levels among the two compilers

#### Deoptimization: the process by which the JVM replaces previously compiled code
- This usually happens in the context of C2 code replacing C1 code
- but it can happen because of changes in the execution profile of an app


## Advanced Compiler Flags

> These flags Should NOT generally Be Used

### Compilation Thresholds

> In current JVMs, tuning the threshold never really makes sense

#### Compilation is based on two counters in the JVM
- the number of times the method has been called 
- the number of times any loops in the method have branched back
    - Branching back: the number of times a loop has completed execution

#### Standard Compilation
- Whe executing a Java method, JVM checks the sum of those two counters 
  - and decides whether the method is eligible for compilation
  - If it is, the method is queued for compilation

#### Every time a loop completes an execution 
- the branching counter is incremented and inspected 
- If the branching counter has exceeded its individual threshold 
    - the loop (and not the entire method) becomes eligible for compilation

#### Tunings Thresholds
>  When Tiered Compilation is Disabled
- Standard Compilation is triggered 
    - by the value of the ``` -XX:CompileThreshold=N ``` flag 
    - The default value of ``` N ``` is ``` 10000 ```
- Changing the value of the ``` CompileThreshold ``` flag 
    - will cause the compiler to choose to compile the code sooner (or later) 
      than it normally would have

> When Tiered Compilation is Enabled (as it normally is) 
> ``` -XX:CompileThreshold=N ``` Does Nothing At All 
- This flag is really just a holdover from JDK 7 and earlier days
- The tuning is no longer recommended

#### Even if an app runs forever, still won't compile all code eventually
- Methods and loops are decreased over time also
- Periodically (specifically, when the JVM reaches a safepoint), the value of 
  each counter is reduced

> The counters are a relative measure of the recent hotness of the method or loop

#### To get C1 to compile a method more quickly
- ``` -XX:Tier3InvocationThreshold=N ``` (default 200)
- Little practical benefit exists

#### To get C2 to compile a method more quickly
- ``` -XX:Tier4InvocationThreshold=N ``` (default 5000)
- Little practical benefit exists

#### Quick Summary of Advanced Compiler Flags

- The thresholds at which methods (or loops) get compiled are set via tunable 
  parameters
- Without tiered compilation, it sometimes made sense to adjust those thresholds
    - but with tiered compilation, this tuning is no longer recommended


### Compilation Threads

#### Eligible methods (or loops) for Compilation will be Queued 
  - The queue is processed by one or more background threads
  - These queues are not strictly first in, first out
  - Methods whose invocation counters are higher have priority

#### The C1 and C2 compilers have different Queues
- Each of which is processed by (potentially multiple) different threads

#### The number of threads is based on a complex formula of logarithms

#### Default number of C1 and C2 Compiler Threads for Tiered Compilation

| CPUs | C1 Threads | C2 Threads |
|------|------------|------------|
| 1    | 1          | 1          |
| 2    | 1          | 1          |
| 4    | 1          | 2          |
| 8    | 1          | 2          |
| 16   | 2          | 6          |
| 32   | 3          | 7          |  
| 64   | 4          | 8          |
| 128  | 4          | 10         |

#### Adjust the number of compiler threads
- ``` -XX:CICompilerCount=N ``` flag 
- ``` N ``` is the total number of threads the JVM will use to process the queue(s)
- For tiered compilation, one-third (but at least one) will be used to process 
  the C1 compiler queue
- The remaining threads (but also at least one) will be used to process the C2 
  compiler queue
- The default value of that flag is the sum of the two columns in the preceding 
  table
- If tiered compilation is disabled, only the given number of C2 compiler 
  threads are started

#### When to consider Adjusting 
- JDK8 in Docker Container
- Single CPU

#### Process Queues Asynchronously
- ``` -XX:+BackgroundCompilation ``` flag, ``` true ``` by default
- If it's set to ``` false ```, when a method is eligible for compilation
    - code that wants to execute it will wait until it is in fact compiled 
      (rather than continuing to execute in the interpreter)
- Background compilation is also disabled when ``` -Xbatch ``` is specified

#### Summary of Compilation Threads
- Compilation occurs asynchronously for methods that are placed on the 
  compilation queue
- The queue is not strictly ordered
    - hot methods are compiled before other methods in the queue
    - This is another reason compilation IDs can appear out of order in the 
      compilation log


### Inlining

#### One of the most important optimizations the compiler makes 
- is to ___INLINE METHODS___

> The overhead for invoking setter and getter method calls is quite high
> - especially relative to the amount of code in the method

#### JVMs routinely perform ___CODE INLINING___ for these setter and getter methods

``` 
public class Point {
    private int x, y;
    public void getX() { return x; }
    public void setX(int i) { x = i; }
}

```

``` 
Point p = getPoint();
p.setX(p.getX() * 2);
```

#### After Inling
``` 
Point p = getPoint();
p.x = p.x * 2;
```

#### Inlining is enabled by default

#### Inlining can be disabled using the ``` -XX:-Inline ``` Flag

#### The basic decision about whether to inline a method depends on 
- how hot it is and its size

#### If a method is eligible for inlining because it is called frequently
- It will be inlined only if its bytecode size is less than 325 bytes 
    - or whatever is specified as the ``` -XX:MaxFreqInlineSize=N ``` flag

#### Otherwise, it is eligible for inlining only if it is smaller than 35 bytes
- or whatever is specified as the ``` -XX:MaxInlineSize=N ``` flag
- The net effect of tuning the ``` MaxInlineSize ``` flag is that 
    - it might reduce the warm-up time needed for a test 
    - but it is unlikely that it will have a big impact on a long-running app

#### Quick Summary of Inlining
- Inlining is the most beneficial optimization the compiler can make
    - particularly for object-oriented code where attributes are well 
      encapsulated
- Tuning the inlining flags is rarely needed 
    - and recommendations to do so often fail to account for the relationship 
      between normal inlining and frequent inlining 
- Make sure to account for both cases when investigating the effects of inlining


### Escape Analysis

> The C2 compiler performs aggressive optimizations if escape analysis is enabled
>   - ``` -XX:+DoEscapeAnalysis ```, which is true by default

``` 
public class Factorial {
    private BigInteger factorial;
    private int n;
    public Factorial(int n) {
        this.n = n;
    }
    public synchronized BigInteger getFactorial() {
        if (factorial == null)
            factorial = ...;
        return factorial;
    }
    public static void main(String... args) {
        List<BigInteger> list = new ArrayList<BigInteger>();
        for (int i = 0; i < 100; i++) {
            Factorial factorial = new Factorial(i);
            list.add(factorial.getFactorial());
       }
    }
}
```

#### The Optimizations
- It needn’t get a synchronization lock when calling the ``` getFactorial() ``` method
- It needn’t store the field n in memory; it can keep that value in a register
    - Similarly, it can store the factorial object reference in a register
- In fact, it needn’t allocate an actual factorial object at all
    - it can just keep track of the individual fields of the object

#### Escape Analysis can determine which optimizations are possible 
- and make the necessary changes in the compiled code

#### Escape analysis is enabled by default

#### Disabling escape analysis is likely not appropriate 

#### Disabling escape analysis is out of question
> out of the question: impossible or not allowed and therefore not worth discussing

#### Disabling escape analysis does not lead to more stable code

> Simplifying the code in question is the best course of action

> Simpler code will compile better

#### Quick Summary of Escape Analysis
- Escape analysis is the most sophisticated of the optimizations the compiler 
  can perform 
    - This is the kind of optimization that frequently causes microbenchmarks 
      to go awry
> awry adv., adj. /əˈraɪ/ if something goes awry, it does not happen in the way 
> that was planned


### CPU-Specific Code

#### CPU instruction sets are selected with the -XX:UseAVX=N argument
- where N is: 
    - 0 Use no AVX instructions 
    - 1 Use Intel AVX level 1 instructions (for Sandy Bridge and later processors)
    - 2 Use Intel AVX level 2 instructions (for Haswell and later processors)
    - 3 Use Intel AVX-512 instructions (for Knights Landing and later processors)
- The default value for this flag will depend on the processor running the JVM
    - JVM will detect the CPU and pick the highest supported value it can

#### In Java 11 on newer Intel processors 
- the default is to use 3 in versions up to 11.0.5
- the default is 2 in later versions

> It is a good idea to use the latest versions of Java 11


### Tiered Compilation Trade-Offs
- running in a Docker container with a small memory limit
- running in a cloud virtual machine that just doesn’t have quite enough memory
- running dozens of JVMs on your large machine 


### The javac Compiler
- JDK 11 introduces a new way of doing string concatenation that can be faster 
  than previous versions
    - it requires that code be recompiled in order to take advantage of it

#### But normally, ``` javac ``` compiler doesn’t really affect performance at all


### The GraalVM

#### The GraalVM is a new virtual machine 

#### It provides a means to run Java code, of course, 
- but also code from many other languages 

#### This universal virtual machine can also run 
- ``` JavaScript ``` 
- ``` Python ``` 
- ``` Ruby ``` 
- ``` R ```
- and traditional JVM bytecodes from Java and other languages 
    - that compile to JVM bytecodes (e.g., Scala, Kotlin, etc.) 

#### Graal comes in two editions 
- a full open source Community Edition (CE) 
- and a commercial Enterprise Edition (EE) 
- Each edition has binaries that support either Java 8 or Java 11

#### The traditional JVM contains a version of the GraalVM JIT, 
- depending on when the JVM was built


### Precompilation

#### Ahead-of-Time Compilation
- Removed as of Java 17

#### GraalVM Native Compilation 
- The GraalVM can produce full native executables that run without the JVM
- The GraalVM produces binaries that start up quite fast
    - particularly when comparing them to the running programs in the JVM
- However, in this mode the GraalVM does not optimize code as aggressively as 
  the C2 compiler 
    - so given a sufficiently longrunning application, the traditional JVM will 
      WIN OUT In The End
    - the GraalVM native binary does not compile classes using C2 during execution
- The memory footprint of a native program produced from the GraalVM starts out 
  significantly smaller than a traditional JVM 
    - However, by the time a program runs and expands the heap, this memory 
      advantage fades


### Summary for all

#### Don’t be afraid of small methods 
- and, in particular, getters and setters because they are easily inlined

#### Code that needs to be compiled sits in a compilation queue
- The more code in the queue, the longer the program will take to achieve 
  optimal performance

#### The simpler the code, the more optimizations that can be performed on it
- Profile feedback and escape analysis can yield much faster code
    - but complex loop structures and large methods limit their effectiveness