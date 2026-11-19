#!/bin/sh
# Builds and starts the API on http://localhost:8080. Run from the backend folder.
./build.sh && java -cp "out:lib/*" store.Main "$@"
