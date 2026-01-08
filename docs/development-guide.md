# Development Guide - SentinelAI

## Prerequisites
- **Android Studio**: Ladybug or newer.
- **JDK**: 17+.
- **Android SDK**: API 34+ (for development), runs on API 26+.

## Setup
1. Clone the repository.
2. Open `android-demo-sentinel-ai` in Android Studio.
3. Sync Gradle projects.

## Running the App
- Use a physical device for full testing (emulators cannot run LINE/WhatsApp easily).
- Enable **Accessibility Service** for SentinelAI in the device settings.

## Core Implementation Details
- **Entry Point**: `com.example.demo_sentinel_ai.MainActivity`
- **Background Service**: `com.example.demo_sentinel_ai.service.SentinelAccessibilityService`
- **Detection Logic**: `com.example.demo_sentinel_ai.analysis.ScamPatternAnalyzer`
- **Warning UI**: `com.example.demo_sentinel_ai.ui.screens.WarningScreen`

## Permissions Required
- `BIND_ACCESSIBILITY_SERVICE`: To monitor chat apps.
- `POST_NOTIFICATIONS`: To show warnings.
- `SYSTEM_ALERT_WINDOW`: For overlay UI (if needed).
- `READ_PHONE_STATE`: To detect calls.

## Testing
- Use `adb logcat -s SentinelAI` to view service logs.
- Simulate scams by typing keywords (e.g., "การันตีกำไร", "guaranteed profit") in supported chat apps.

