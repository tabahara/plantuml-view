#!/bin/bash
./gradlew clean distZip
cp build/distributions/plantuml-view-1.0.zip .
# ./gradlew run -Pargs="test/testfile.puml"
