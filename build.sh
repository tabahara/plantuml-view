#!/bin/bash
./gradlew clean distZip
cp build/distributions/plantuml-view.zip .
# ./gradlew run -Pargs="test/testfile.puml"
