# JMH

## The Help
- ### ``` -h ```
    - To display the help content only

## How to Run

### Run All Benchmarks
- The default behavior: -wi 20 -i 20 -f 10

``` 
mvn clean install
java -jar target/benchmarks.jar #default behavior -wi 20 -i 20 -f 10
java -jar target/benchmarks.jar -wi 5 -i 5 -f 1
```

### Run Individual Benchmarks

``` 
java -jar target/benchmarks.jar "StringInternBenchmark"
java -jar target/benchmarks.jar -f 0 -wi 2 -i 3 "StringInternBenchmark"
```

## List all benchmarks
- ### ``` java -jar target/benchmarks.jar -l ```


## List all parameters 
- ### State class fields with ``` @Param ``` 
- ### ``` java -jar target/benchmarks.jar -p ```


## List all benchmarks and parameters
- ### ``` java -jar target/benchmarks.jar -lp ```


## List all machine-readable result formats
- ### ``` java -jar target/benchmarks.jar -lrf ```
- ### Available formats: text, csv, scsv, json, latex


## JMH Mode

### ``` @BenchmarkMode(Mode.All)```
- #### all the benchmark modes
- #### This is mostly useful for internal JMH testing 

### ``` @BenchmarkMode(Mode.AverageTime) ```
- #### Average time per operation 

### ``` @BenchmarkMode(Mode.SampleTime) ```
- #### Samples (採樣) the time for each operation

### ``` @BenchmarkMode(Mode.SingleShotTime) ```
- #### Measures the time for a single operation

### ``` @BenchmarkMode(Mode.Throughput) ```
- #### Operations per unit of time

### Command line Args for ``` Mode ```
- ``` -bm <mode> ``` 
  - [Throughput / thrpt] 
  - [AverageTime / avgt] 
  - [SampleTime / sample] 
  - [SingleShotTime / ss] 
  - [All / all]


## The Time Unit of Output 
- ``` @OutputTimeUnit(TimeUnit.NANOSECONDS) ```
- ### Provides the default time unit to report the results 
- ### May be put at Benchmark method 
- ### OR put at the enclosing class instance
- ### May be overridden with the runtime options
- ### Command Line Args for ``` @OutputTimeUnit ```
    - ``` -tu <TU> ```
    - Override time unit in benchmark results
    - Available time units are: 
        - [m, s, ms, us, ns] 


## To Exculde Benchmark Methods
- ### ``` -e <regexp+> ```
    - Benchmarks to exclude from the run 


## Make JMH ``` fail ``` Immediately
- ### ``` -foe <bool> ```
    - Should JMH fail immediately if any benchmark had experienced an 
      unrecoverable error? 
    - Helps to make quick sanity tests for benchmark suites, 
    - Make the automated runs with checking error codes
    - Output stack trace 


## Force GC
- ### ``` -gc <bool> ``` 
    - Should JMH force GC between iterations? 
    - Forcing the GC may help to lower the noise in GC-heavy benchmarks 
    - At the expense of jeopardizing GC ergonomics decisions 
    - Use with care


## Specify Iteration Times
- ### ``` -i <int> ```
    - Specify the number of measurement iterations to do
    - Measurement iterations are counted towards (計入) the benchmark score 

## Specify JVM
  - ### ``` jvm <string> ```
      - Use given JVM for runs
      - This option only affects forked runs


## Use given JVM Arguments
  - ### ``` -jvmArgs <string>  ```
      - Use given JVM arguments
      - Most options are inherited from the host VM options
      - but in some cases you want to pass the options only to a forked VM
      - Either single space-separated option line
      - or multiple options are accepted
      - This option only affects forked runs

  ``` 
  java -jar target/benchmarks.jar -f 0 -e testIntern -jvmArgs "-Xms64M -Xmx512M"
  ```


## Specify Benchmark Parameters
- ``` @Param ``` field of ``` @State ``` class

  ``` 
    @State(Scope.Thread)
    public static class InternState {
        @Param({"100"})
        private int strAmt;
        public int getStrAmt() {
            return this.strAmt;
        }
        // other class content ... ...
    }
  ```

- ### ``` -p <param={v,}*> ``` to override the value of parameter
    - Benchmark parameters
    - This option is expected to be used once per parameter
    - Parameter name and parameter values should be separated with equals sign
    - Parameter values should be separated with commas
    - ``` java -jar target/benchmarks.jar -p strAmt=100,150 ```


## Minimum time for each Measurement Iteration
- ### ``` -r <time> ```
    - Minimum time to spend at each measurement iteration 
    - Benchmarks may generally run longer than iteration duration
    - ``` java -jar target/benchmarks.jar -r 5 ```


## Minimum time for each Warmup Iteration
- ### ``` -w <time> ```
    - Minimum time to spend at each Warmup iteration
    - Benchmarks may generally run longer than iteration duration
    - ``` java -jar target/benchmarks.jar -w 5 ```


## Specify Output Format
- ### ``` -rf <type> ```
    - Format type for machine-readable results 
    - These results are written to a separate file (see ``` -rff ```) 
    - See the list of available result formats with ``` -lrf ```


## Write machine-readable results to File
- ### ``` -rff <filename> ``` 
    - Write machine-readable results to a given file 
    - The file format is controlled by -rf option
    - See the list of result formats for available formats 
    - ``` java -jar target/benchmarks.jar -rf csv -rff result.csv ```


## Output to File
- ### ```  -o <filename> ```
    - Redirect human-readable output to a given file


## Timeout Benchmarks' Iteration
- ### ``` -to <time> ```
    - Timeout for benchmark iteration 
    - After reaching this timeout, JMH will try to interrupt the running tasks 
    - Non-cooperating benchmarks may ignore this timeout


## Override time unit in benchmark Results
- ### ``` -tu <TU> ``` 
    - Override time unit in benchmark results 
    - Available time units are: ``` [m, s, ms, us, ns] ```


## Specify Minimum Time for Warmup
- ### ``` -w <time> ``` 
    - Minimum time to spend at each warmup iteration 
    - Benchmarks may generally run longer than iteration duration 


## Specify the Number of Warmup Iteration
- ### ``` -wi <int> ``` 
    - Number of warmup iterations to do 
    - Warmup iterations are not counted towards the benchmark score


## Warmup Mode
- ### ``` -wm <mode> ``` 
    - Warmup mode for warming up selected benchmarks
    - Warmup modes are 
        - ``` INDI ``` Warmup each benchmark individually, then measure it 
        - ``` BULK ``` Warmup all benchmarks first, then do all the measurements
        - ``` BULK_INDI ``` Warmup all benchmarks first, then re-warmup each 
            benchmark individually, then measure it 


## To Fork Benchmarks
### @Fork sets the default forking parameters for
- the benchmark class
- a benchmark method
### @Fork may be overridden with the runtime options
- #### ``` -f <int> ```
    - How many times to fork a single benchmark
    - Use 0 to disable forking altogether
    - Warning: disabling forking may have detrimental impact on benchmark
      and infrastructure reliability
        - might want to use different warmup mode instead


## ``` @State ``` for Benchmark Parameters
- ### Initialize variables that the benchmark code needs
    - Such variables are called "state" variables
    - Declared in special state classes
    - An instance of that state class can then be provided as parameter to the 
      benchmark method
- ### Not be part of the code which benchmark measures
- ### State Scope
    - Thread: Each thread running the benchmark will create its own instance of 
      the state object
    - Group: Each thread group running the benchmark will create its own 
      instance of the state object
    - Benchmark: All threads running the benchmark share the same state object
- ### State Class Requirements
    - #### The class must be declared ``` public ```
    - #### If the class is a nested class, it must be declared ``` static ```
    - #### The class must have a public no-arg constructor
    - #### Annotate the class with the ``` @State ``` annotation
- ### State class method with ``` @Setup ``` 
    - Should be called to set up the state object before it is passed to the 
      benchmark method
    - Not included in the benchmark runtime measurements
- ### State class method with ``` @TearDown ```
    - Should be called to clean up the state object after the benchmark has been 
      executed
    - Not included in the benchmark runtime measurements
- ### Could have more than one ``` @Setup ``` method and ``` @TearDown ``` method
- ### Levels for ``` @Setup ``` and ``` @TearDown ```
    - #### ``` Level.Trial ```
        - Called once for each time for each full run of the benchmark
        - A full run means a full "fork" including all warmup and benchmark 
          iterations
    - #### ``` Level.Iteration ```
        - Method is called once for each iteration of the benchmark
    - #### ``` Level.Invocation ``` ___NOT RECOMMENDED___
        - Called once for each call to the benchmark method


    ``` 
    @State(Scope.Thread)
    public static class InternState {

        @Param({"100"})
        private int strAmt;
        private String[] strArray;
        private ConcurrentHashMap<String,String> map;

        @Setup(Level.Iteration)
        public void doSetup() {
            strArray = new String[strAmt];
            for (int i = 0; i < strAmt; i++) {
                strArray[i] = RandomStringUtils.randomAlphanumeric(17);
            }
            map = new ConcurrentHashMap<>();
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {
            for (int i = 0; i < strAmt; i++) {
                strArray[i] = null;
            }
            strArray = null;
            map.clear();
            map = null;
        }

        public int getStrAmt() {
            return this.strAmt;
        }

        public String[] getStrArray() {
            return this.strArray;
        }

        public ConcurrentHashMap<String, String> getMap() {
            return this.map;
        }

    }//: End of class InternState
    ```


## Passing the Final Calculated Value to a Black Hole
- ### In order to avoid ___Dead Code Elimination___ 


## Avoiding Constant Folding
- ### Must not hardcode constants into benchmark methods 
- ### The input to calculations should come from a ``` State Object ``` 
    - to make it harder for the JVM to see that the calculations are based on 
      constants

``` 
@Benchmark
public void testIntern(Blackhole blackhole, InternState internState) {
    String[] strArr = internState.getStrArray();
    for (int i = 0; i < internState.getStrAmt(); i++) {
        String t = strArr[i].intern();
        blackhole.consume(t);
    }
}
```