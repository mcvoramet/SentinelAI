# Source Tree Analysis - SentinelAI

## Project Structure
The project is organized as a monolithic Android application.

```
/Users/dre/SentinelAI/SentinelAI/
├── 01_PRD_SentinelAI.md              # Product Requirements Document
├── 02_Storytelling_SentinelAI.md     # Narrative and strategy
├── 03_Architecture_SentinelAI.md      # High-level architecture
├── README.md                          # Project overview
├── android-demo-sentinel-ai/          # Android project root
│   ├── app/                           # Main application module
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/example/demo_sentinel_ai/
│   │   │   │   │   ├── analysis/      # Scam detection logic
│   │   │   │   │   ├── model/         # Data models
│   │   │   │   │   ├── service/       # Accessibility service and detectors
│   │   │   │   │   └── ui/            # UI components and screens
│   │   │   │   └── res/               # Android resources
│   │   │   └── test/                  # Unit tests
│   ├── build.gradle.kts               # Root build config
│   └── settings.gradle.kts            # Project settings
└── docs/                              # Project knowledge and scan reports
```

## Critical Folders
- `analysis/`: Contains `ScamPatternAnalyzer.kt` which implements the core logic for identifying scam patterns in text.
- `service/`: Contains `SentinelAccessibilityService.kt`, the entry point for the background monitoring service.
- `service/detector/`: Contains specialized detectors for different messaging apps (Line, Messenger, WhatsApp).
- `ui/screens/`: Contains the `WarningScreen.kt`, the primary intervention UI.
- `model/`: Defines the data structures for detections and risk levels.

