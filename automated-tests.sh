#!/bin/sh

success=$(mvn clean test | grep "BUILD FAILURE")

echo $success

if [ -z $success ]; then
    echo "Test success - commit succesfull"
    exit 0 
else
    echo "Test failure - commit unsuccessfull"
    exit 1
fi
