#!/bin/sh
# Compiles the code and tests, then runs every JUnit test. Run from the backend folder.
rm -rf test-out && mkdir test-out
javac -d test-out -cp "lib/*" $(find src/main/java src/test/java -name '*.java') || exit 1
java -jar lib/junit-platform-console-standalone-1.11.4.jar execute \
    --class-path "test-out:lib/gson-2.11.0.jar" --scan-class-path --details=tree
