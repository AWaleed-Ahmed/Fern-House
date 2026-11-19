#!/bin/sh
# Generates the Javadoc HTML into docs/. Open docs/index.html in a browser. Run from the backend folder.
rm -rf docs
javadoc -d docs -author -cp "lib/*" -sourcepath src/main/java -subpackages store \
    -doctitle "OEL Store API Documentation" -windowtitle "OEL Store"
