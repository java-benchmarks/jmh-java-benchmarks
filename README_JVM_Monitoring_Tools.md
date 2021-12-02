# Java Monitoring Tools
- To gain insight into the JVM itself 
- Come with the JDK


## Basic Information about a Running JVM Process

### ``` jps ```
- Quickly Find  Java Application's Process ID
- The output example:

  ``` 
  10596 org.eclipse.equinox.launcher_1.5.300.v20190213-1655.jar
  10472 RemoteMavenServer36
  1144 Launcher
  3128 Workbench
  8328 Jps
  10524
  7580 org.eclipse.equinox.launcher_1.5.300.v20190213-1655.jar
  ``` 

### ``` jcmd ```
- This tool is used to send diagnostic command requests to the JVM
    - Being useful for 
        - controlling Java Flight Recordings 
        - troubleshoot 
        - diagnose JVM and Java Applications
- Must be used on the same machine where the JVM is running 
    - and have the same effective user and group identifiers that were used to 
      launch the JVM
- Prints basic class, thread, and JVM information for a Java process
- Can be used to find the basic JVM information
    - including the value of all the tuning flags for a running app

#### Uptime
- ``` jcmd 3128 VM.uptime ```
``` 
3128:
764.364 s
```

#### System Properties
- ``` jcmd <process_id> VM.system_properties ```
- ``` jinfo -sysprops <process_id> ```
- Includes all properties set on the command line with a -D option
- Any properties dynamically added by the application
- The set of default properties for the JVM

#### JVM Version
- ``` jcmd <process_id> VM.version ```

#### JVM Command Line
- ``` jcmd <process_id> VM.command_line ```
- Shows which tuning flags were specified directly on the command line

#### JVM Tuning Flags
- ``` jcmd <process_id> VM.flags [-all] ```
- ``` -XX:+PrintFlagsFinal ```
- ``` java <other_options> -XX:+PrintFlagsFinal -version ```
- Find out Default Flag Values
  ``` 
    ...Hundreds of lines of output, including...
    uintx InitialHeapSize := 4169431040 {product}
    intx InlineSmallCode = 2000 {pd product}
    uintx G1PeriodicGCInterval = 0 {manageable} {default}
  ```
- The tuning flags in effect for an application
- Shows which flags were set on the command line
- Shows some flags that were set directly by the JVM
- The ``` -all ``` option lists every flag within the JVM
- Output all flags to a file
    ``` 
    jcmd <process_id> VM.flags -all | grep -i ...> /c/dev/projects/all_tuning_flags.txt
    ```
- Useful for determining the default ergonomic settings of flags on a particular 
  platform

#### Figure Out the PLATFORM_SPECIFIC Defaults of Tuning Flags
- ``` := ``` A non-default value is in use for the flag in question
    - The flag's value was specified directly on the command line
    - Some other option indirectly changed that option
    - The JVM calculated the default value ergonomically
- ``` = ``` The value is the default value for this version of the JVM
- Default values for some flags may be different on different platforms 
    - ``` intx InlineSmallCode = 2000 {pd product} ```
- ``` {product} ``` means that the default setting of the flag is uniform 
    across all platforms 
- ``` {pd product} ``` means platform-dependent 
- ``` {manageable} ``` value can be changed dynamically during runtime
- ``` C2 diagnostic ``` the flag provides diagnostic output for the compiler 
    engineers to understand how the compiler is functioning


### ``` jinfo ```
- Useful for inspecting (and in some cases changing) individual flags 
- #### Provides visibility into the system properties of the JVM
- #### Allows some system properties to be set dynamically 
    - Allows certain flag values to be changed during execution of the program
    - Suitable for scripting
- #### To retrieve the values of all the flags in the process
    - ``` jinfo -flags 16704 ```
    - With the ``` -flags ``` option, ``` jinfo ``` provides information about 
      all flags
    - otherwise, it prints only those specified on the command line
- #### Inspect the value of an individual flag
  - ``` jinfo -flag InitialHeapSize <process_id> ```
    ``` 
    $ jinfo -flag InitialHeapSize 16704
      -XX:InitialHeapSize=532676608 
    ```
- #### Turn on/off ``` manageable ``` flags 
    - ``` jinfo -flag -HeapDumpAfterFullGC 16704 ``` Turn off ``` HeapDumpAfterFullGC ```
    - ``` jinfo -flag +HeapDumpAfterFullGC 16704 ``` Turn on ``` HeapDumpAfterFullGC ```
    - ``` jinfo -flag HeapDumpAfterFullGC 16704 ``` 
        - Inspect the value of ``` HeapDumpAfterFullGC ```


## Thread Information

### Fetch information about Running Threads 
- ``` jconsole ``` 
- ``` jvisualvm ``` 

### Obtain Stacks of Running Threads
#### Using ``` jstack process_id ```
```
jstack 30236
  ... Lots of output showing each thread's stack ...
```
#### Using ``` jcmd process_id Thread.print ```
``` 
jcmd 30236 Thread.print
```

## Class Information
- Information about the number of classes in use by an application
### Using ``` jconsole ```
### Using ``` jstat ```

``` 
jstat -options
    -class
    -compiler
    -gc
    -gccapacity
    -gccause
    -gcmetacapacity
    -gcnew
    -gcnewcapacity
    -gcold
    -gcoldcapacity
    -gcutil
    -printcompilation
λ jstat -class 30236
Loaded      Bytes         Unloaded      Bytes     Time
  2091     4211.6                0        0.0     0.17
```


## Live GC Analysis
### ``` jconsole ``` displays live graphs of the heap usage
### ``` jcmd ``` allows GC operations to be performed 
### ``` jmap ```  can print heap summaries or information 
- on the permanent generation 
- create a heap dump 
### ``` jstat ``` produces a lot of views 
  - of what the garbage collector is doing


## Heap Dump Postprocessing 
### Heap dumps can be captured from 
- The ``` jvisualvm ``` GUI
- The command line using ``` jcmd ``` or ``` jmap ```
### The ``` heap dump ``` is a snapshot of the heap 
- that can be analyzed with various tools like 
    - ``` jvisualvm ```
    - the Eclipse Memory Analyzer Tool