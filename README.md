# Kotlin compiler fuzzer and reduktor
Requirements:
* NodeJS

Usage:
* Compile
  * ./compile.sh
* Start
  * gradlew runBBF - to start fuzzing
  
All options (backends, dir for results, etc.) specifies in bbf.conf file

### Instrumentation notes

* The class `com.stepanov.bbf.coverage.CompilerInstrumentation` is duplicated in both the Java agent module and in the main project. Their contents should be kept identical.
* `-javaagent:<path-to-instrumenter-jar>` VM option should be present in order for the compiler to be instrumented.
* A new instrumenter jar can be assembled with the `kotlinc-instrumenter/jar` Gradle task. The new jar's default parent directory is the main project's `src/main/resources`.

### Notes on YouTrack dataset evaluation

An attempt to evaluate the fault localization-based code deduplication method was made. The entry point can be found [here](src/test/kotlin/com/stepanov/bbf/isolation/testbed/IsolationTestbed.kt): it's a big mess in terms of configuring it, but oh well. Four parameters were deemed important enough to try varying:

* whether starting samples were reduced or not
* coverage level (method-wise or branch instruction-wise) of instrumentation
* evaluation method of program entity's "suspiciousness"
* a fraction of each bug's suspiciousness ranking that is used during duplicate search

The dataset itself (in both original and reduced forms) can be found at `isolation-evaluation/samples-backup` along with the notes on how I process it a  bit. [This YouTrack crawler ](https://github.com/FenstonSingel/kt-youtrack-crawler) was used for obtaining the dataset. Copy the directory as `isolation-evaluation/samples` if you plan on using the testbed mentioned above.

34.2 GiB (23.0 GiB in archived form) of intermediate artifacts such as mutants, coverages, and FL results are also (probably) available per request. The contents of the archive should be extracted to the `isolation-evaluation` folder for further use.