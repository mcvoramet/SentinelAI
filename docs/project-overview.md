# Project Overview - SentinelAI

## Purpose
SentinelAI is an Android-native service designed to prevent digital fraud by monitoring messaging apps (LINE, WhatsApp, Messenger) for scam patterns and providing behavioral interventions. It aims to catch scams during the grooming phase before any payment occurs.

## Tech Stack
| Category | Technology |
|----------|------------|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material 3) |
| Architecture | Accessibility Service based monitoring |
| Build System | Gradle (Kotlin DSL) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 (Android 15) |

## Core Features
- **Real-time Chat Monitoring**: Uses Android Accessibility Services to read visible screen text and identify fraud patterns.
- **Scam Detection Engine**: Weighted keyword matching in Thai and English to calculate risk levels.
- **Behavioral Intervention**: High-impact warning screens with Socratic questioning to help users recognize fraud.
- **On-Demand Verification**: (Planned) QR code and phone number verification.

## Repository Structure
- `android-demo-sentinel-ai/`: Main Android application codebase.
- `_bmad/`: BMAD Method configuration and workflows.
- `docs/`: Project documentation and analysis reports.
- `_bmad-output/`: Planning and implementation artifacts.

