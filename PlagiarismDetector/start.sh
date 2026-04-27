#!/bin/bash
echo "===================================================="
echo "  Smart Plagiarism Detection System"
echo "  PlagCoders · TCS-408 · JAVA-IV-T167"
echo "===================================================="
echo

# Check Java
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed."
    echo "        Download from: https://adoptium.net"
    exit 1
fi

# Download H2 if missing
if [ ! -f "lib/h2.jar" ]; then
    echo "[SETUP] Downloading H2 database driver (one-time, ~2 MB)..."
    curl -L -A "PlagCoders/1.0" \
        "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar" \
        -o "lib/h2.jar"
    if [ ! -f "lib/h2.jar" ] || [ $(wc -c < "lib/h2.jar") -lt 1000 ]; then
        echo
        echo "[ERROR] Download failed. Please do it manually:"
        echo "  1. Open: https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar"
        echo "  2. Save the file as  h2.jar"
        echo "  3. Put it inside the  lib/  folder"
        echo "  4. Run ./start.sh again"
        exit 1
    fi
    echo "[OK] H2 driver ready!"
    echo
fi

echo "[OK] Launching..."
java -cp "PlagiarismDetector.jar:lib/h2.jar" com.plagcoders.PlagiarismApp
