#!/usr/bin/env bash
echo "=================================================================="
echo "  TCET Mumbai - Industrial Program Mini-Project"
echo "  Full Stack Development using Java - Project Management System"
echo "=================================================================="

mkdir -p bin data

echo "[*] Compiling Java backend classes..."
javac -encoding UTF-8 -d bin src/com/tcet/pms/model/*.java src/com/tcet/pms/repository/*.java src/com/tcet/pms/service/*.java src/com/tcet/pms/util/*.java src/com/tcet/pms/controller/*.java src/com/tcet/pms/Main.java

if [ $? -ne 0 ]; then
    echo "[!] Compilation failed! Please ensure JDK is installed and in your PATH."
    exit 1
fi

echo "[+] Compilation successful!"
echo "[*] Launching Project Management System on http://localhost:8080 ..."

# Attempt to open browser across platforms
if which xdg-open > /dev/null; then
    xdg-open http://localhost:8080 &
elif which open > /dev/null; then
    open http://localhost:8080 &
fi

java -cp bin com.tcet.pms.Main 8080
