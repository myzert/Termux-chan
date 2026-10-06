#!/bin/bash
# Termux-chan Build Script

echo "==============================="
echo " Termux-chan Build Helper"
echo "==============================="

if [ ! -f "gradlew" ]; then
    echo "Error: gradlew not found. Are you in the root of the project?"
    exit 1
fi

chmod +x gradlew

echo "1) Build Debug APK (Testing)"
echo "2) Build Release APK (Production)"
echo "3) Clean Project"
read -p "Select an option [1-3]: " choice

case $choice in
    1)
        echo "Building Debug APK..."
        ./gradlew assembleDebug
        echo "Done! APKs are located in app/build/outputs/apk/debug/"
        ;;
    2)
        echo "Building Release APK..."
        ./gradlew assembleRelease
        echo "Done! APKs are located in app/build/outputs/apk/release/"
        ;;
    3)
        echo "Cleaning project..."
        ./gradlew clean
        ;;
    *)
        echo "Invalid option."
        exit 1
        ;;
esac
