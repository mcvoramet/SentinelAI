# SentinelAI Android Architecture Breakdown (Demo-First)

## High-Level Architecture
```
Signal Capture Layer
  (Accessibility Services, QR decode, Location)
        ↓
Behavioral Risk Engine
  (rules + correlation)
        ↓
AI Reasoning Layer
  (Gemini 3 Flash for classification, Pro for explanation)
        ↓
Intervention UI Layer
  (System Notification → Explanation Screen)
```

---

## 1) Signal Capture Layer (Demo-Safe)
### Chat Monitoring
- **Real path (future):** Accessibility Services reads on-screen text / events in chat apps.
- **Demo path:** preloaded conversation JSON with timestamps, sender metadata, and extracted phrases.

Outputs:
- scam phrase matches
- escalation rate
- relationship age (days since first message)
- chat risk score

### QR Scan Trigger
- **Real path (future):** intercept QR scan intent or detect “pay/confirm” UI via Accessibility events.
- **Demo path:** mock QR decoder returns a payload (recipient, amount, merchant id).

Outputs:
- `QR_PAYMENT_INITIATED` event
- decoded recipient/routing metadata

### Google Maps Geolocation (QR Context Check)
Purpose: convert “recipient routing” into a **geographic plausibility signal**.

- **Real path:** device GPS via Fused Location Provider + Google Maps Geocoding/Places (or a lookup service mapping IDs to locations).
- **Demo path:** hardcoded lat/lng for user and recipient with computed distance.

Outputs:
- distance km
- mismatch level (low/medium/high)
- human explanation string: “routes to Rayong while you are in Bangkok”

---

## 2) Behavioral Risk Engine
Principle: do cheap, deterministic gating first. Only call LLM when needed.

### Rule-Based Gating
Examples:
- New relationship + large transfer intent = escalate
- Chat scam score high + location mismatch high = escalate
- New contact alone = mild warning

Outputs:
- risk level (LOW / MEDIUM / HIGH / CRITICAL)
- triggers list (e.g., `chat_scam_language`, `location_mismatch`, `new_contact`)
- evidence bundle for explanation

### Relationship Timeline Model (Demo)
Compute:
- days known
- message count
- escalation bursts
- “request-to-pay proximity” (how soon payment request appears after intimacy/pressure)

---

## 3) AI Reasoning Layer (Gemini 3)
### Gemini 3 Flash (Fast Path)
Use for:
- classify message as scam tactics (urgency, secrecy, coaching)
- produce concise structured output

Return format (example):
```json
{
  "is_scam_language": true,
  "tactics": ["urgency", "secrecy", "payment_coaching"],
  "confidence": 87
}
```

### Gemini 3 Pro (Explanation Path)
Use for:
- calm explanation using only provided evidence
- Socratic questions
- cooling-off recommendation

Return format (example):
```json
{
  "headline": "This payment looks risky based on your recent chat and location mismatch.",
  "reasons": [
    "You have known this contact for 21 days.",
    "The QR routes to Rayong while you are in Bangkok.",
    "We detected coaching and secrecy language in your chat."
  ],
  "questions": [
    "Can you call them right now to confirm?",
    "Why would they need your help moving money?",
    "If it’s urgent, why did they wait weeks to ask?"
  ],
  "cooloff": "Pause for 10 minutes before sending."
}
```

### Hallucination Guardrails
- “Use only evidence we provide.”
- “Never accuse, only suggest verification.”
- “Never invent stats.”
- “Keep output short.”

---

## 4) Intervention UI Layer
### System Notification (The Wow Moment)
- Must appear above the payment flow.
- Contains 2–3 evidence bullets, not generic warnings.
- Tap launches Explanation screen.

### Explanation Screen
- Three evidence signals (relationship age, location mismatch, chat tactics)
- 2–3 Socratic questions
- Cool-off recommendation
- Buttons: “Pause”, “Cancel”, “Proceed anyway”

---

## What’s Real vs Mocked (Be Explicit)
| Component | Real in Demo | Mocked in Demo |
|---|---:|---:|
| Notification + UI | ✅ | — |
| Gemini calls | ✅ (optional, if stable) | ✅ fallback canned responses |
| Chat capture | — | ✅ preloaded dataset |
| Banking app integration | — | ✅ simulated payment flow |
| Maps geolocation | ✅ or ✅ mocked lat/lng | ✅ allowed |

---

## Why Web Cannot Replicate This
- No cross-app visibility into chat + bank UI context
- No OS-level authority to interrupt at scan/confirm time
- No synchronous, in-flow intervention without app switching
