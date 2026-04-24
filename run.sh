#!/bin/bash

echo "============================================"
echo "   Code Snippet Manager - Quick Start"
echo "============================================"
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher"
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven"
    exit 1
fi

echo "Building project..."
mvn clean package -q

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

echo
echo "Starting Code Snippet Manager..."
echo

java -jar target/code-snippet-manager-1.0-SNAPSHOT.jar
