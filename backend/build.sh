#!/bin/sh
# Compiles the backend into out/. Run from the backend folder.
rm -rf out && javac -d out -cp "lib/*" $(find src/main/java -name '*.java')
