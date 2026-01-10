# Data Models - Android Application

## ScamDetection
Stores metadata about a detected scam event.

| Field | Type | Description |
|-------|------|-------------|
| sourceApp | String | Package name of the app where the detection occurred |
| chatPartner | String? | Name/ID of the person being chatted with |
| riskScore | Int | Computed risk score |
| riskLevel | RiskLevel | Low, Medium, High, or Critical |
| matchedPatterns | List<String> | Keywords that triggered the detection |
| suspiciousText | String | Snippet of text that was flagged |
| screenshot | Bitmap? | Screenshot captured at the moment of detection |
| timestamp | Long | When the detection occurred |

## RiskLevel
Enumeration representing the severity of the detected threat.

- **LOW**: Initial suspicious signs.
- **MEDIUM**: Common scam patterns detected.
- **HIGH**: Strong indicators of fraudulent activity.
- **CRITICAL**: Immediate threat identified.

