#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
LOCAL_PROPERTIES="$ROOT_DIR/local.properties"
APP_ID="com.example.nicardmanagementapp"
MAIN_ACTIVITY="ae.network.nicardmanagementsdk.sample.MainActivity"

resolve_sdk_dir() {
    if [[ -n "${ANDROID_HOME:-}" ]]; then
        printf '%s\n' "$ANDROID_HOME"
        return 0
    fi

    if [[ -n "${ANDROID_SDK_ROOT:-}" ]]; then
        printf '%s\n' "$ANDROID_SDK_ROOT"
        return 0
    fi

    if [[ -f "$LOCAL_PROPERTIES" ]]; then
        awk -F= '/^sdk.dir=/{print $2}' "$LOCAL_PROPERTIES" | sed 's#\\:#:#g'
        return 0
    fi

    return 1
}

SDK_DIR="$(resolve_sdk_dir)"
ADB="$SDK_DIR/platform-tools/adb"

if [[ -z "$SDK_DIR" || ! -x "$ADB" ]]; then
    echo "Android SDK not found. Set ANDROID_HOME or ANDROID_SDK_ROOT, or define sdk.dir in local.properties." >&2
    exit 1
fi

DEVICE_SERIAL="$($ADB devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')"

if [[ -z "$DEVICE_SERIAL" ]]; then
    echo "No running Android device or emulator detected." >&2
    echo "Start an emulator first, then rerun this script." >&2
    exit 1
fi

echo "Using device: $DEVICE_SERIAL"
echo "Cleaning temporary package staging area on the device..."
$ADB -s "$DEVICE_SERIAL" shell 'rm -f /data/local/tmp/* 2>/dev/null || true'

echo "Removing known stale sample package if present..."
$ADB -s "$DEVICE_SERIAL" uninstall com.nicardmanagementexample >/dev/null 2>&1 || true

echo "Available /data space after cleanup:"
$ADB -s "$DEVICE_SERIAL" shell df -h /data || true

cd "$ROOT_DIR"

echo "Installing sample debug build..."
bash ./gradlew :sample:installDebug

echo "Launching sample app..."
$ADB -s "$DEVICE_SERIAL" shell am start -n "$APP_ID/$MAIN_ACTIVITY"
