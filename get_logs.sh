#!/bin/bash

REMOTE_DIR="/storage/emulated/0/Android/data/xyz.raincards.dev/files"
LOCAL_DIR="./logs"

mkdir -p "$LOCAL_DIR"

# Get list of .log files from the device
log_files=$(adb shell ls "$REMOTE_DIR"/*.log 2>/dev/null)

if [ -z "$log_files" ]; then
  echo "No .log files found in $REMOTE_DIR on the device."
  exit 1
fi

# Loop through and pull each log file
for file in $log_files; do
  # Remove carriage return if present
  file=$(echo "$file" | tr -d '\r')
  filename=$(basename "$file")
  echo "Pulling $filename..."
  adb pull "$file" "$LOCAL_DIR/$filename"
done

echo "All log files have been pulled to $LOCAL_DIR."
