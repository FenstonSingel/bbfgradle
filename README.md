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
* A new instrumenter jar can be assembled with the `kotlinc-instrumenter/jar` Gradle task. The new jar's default new position is the main project's `src/main/resources`.


