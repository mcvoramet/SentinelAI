<div align="center">

# 🛡️ SentinelAI

### *Behavioral Foresight for Scam Prevention*

**Stop scams before they happen — not after.**

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84? style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Gemini AI](https://img.shields.io/badge/Powered%20by-Gemini%203-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://deepmind.google/technologies/gemini/)
[![Made in Thailand](https://img.shields.io/badge/Made%20in-Thailand%20🇹🇭-ED1C24?style=for-the-badge)](https://github.com/mcvoramet/SentinelAI)

---

*"Fraud detection looks backward at transactions.   
SentinelAI looks sideways at the context only the device can see."*

</div>

---

## 🎯 The Problem

> **By the time banks detect fraud, it's already too late.**

Every fraud detection system today makes the same mistake: **they look at the transaction.**

Banks see a normal payment.  The user authorized it. The device authenticated it. Nothing abnormal registers. But the **context was there the whole time** — on the device. 

### The Willing Victim Paradox
- ✅ The victim authorizes the payment  
- ✅ The bank sees nothing wrong  
- ✅ The payment is "authorized" by every measure  
- ❌ **The bank cannot safely block it**

---

## 💡 Our Solution

<div align="center">

### SentinelAI is a device-level threat detector that stops scams *before the victim knows they're in danger.*

</div>

| What We Do | What We Don't Do |
|: ---|:---|
| 🔍 Device-native behavioral monitoring | ❌ QR code scanner (users ignore commodity warnings) |
| 🤝 Relationship timeline modeling | ❌ Chatbot that lectures people |
| 📊 Cross-signal correlation | ❌ Bank fraud detection (too late) |
| ⚡ Proactive intervention at vulnerability | ❌ Block payments (preserves user agency) |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    📱 SIGNAL CAPTURE LAYER                       │
│         Accessibility Services • QR Decode • Location            │
└─────────────────────────────────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                  🧠 BEHAVIORAL RISK ENGINE                       │
│              Rules + Correlation + Timeline Model                │
└─────────────────────────────────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                    🤖 AI REASONING LAYER                         │
│     Gemini 3 Flash (Classification) • Pro (Explanation)         │
└─────────────────────────────────────────────────────────────────┘
                                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                   🖥️ INTERVENTION UI LAYER                       │
│         System Notification → Explanation Screen                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## ⚡ How It Works — The Demo Moment (90 seconds)

<table>
<tr>
<td width="80" align="center"><b>🎬</b></td>
<td><b>Setup (10s)</b></td>
<td>"This is Niran.  He's been chatting with someone for 3 weeks."</td>
</tr>
<tr>
<td align="center"><b>💬</b></td>
<td><b>Chat Context (15s)</b></td>
<td>LINE conversation shows escalation and scam patterns</td>
</tr>
<tr>
<td align="center"><b>📱</b></td>
<td><b>The Moment (30s)</b></td>
<td>Niran opens banking app and scans a QR code</td>
</tr>
<tr>
<td align="center"><b>🛡️</b></td>
<td><b>The Interrupt (20s)</b></td>
<td>Before confirmation, SentinelAI notification appears with evidence</td>
</tr>
<tr>
<td align="center"><b>🤔</b></td>
<td><b>Tap-Through (10s)</b></td>
<td>Explanation screen with Socratic questions and cool-off option</td>
</tr>
<tr>
<td align="center"><b>✅</b></td>
<td><b>Close (5s)</b></td>
<td>"The user pauses.  The scam fails.  Niran keeps his money."</td>
</tr>
</table>

---

## 🔬 AI-Powered Intelligence

### Gemini 3 Flash — Fast Classification
```json
{
  "is_scam_language": true,
  "tactics":  ["urgency", "secrecy", "payment_coaching"],
  "confidence": 87
}
```

### Gemini 3 Pro — Calm Explanation
```json
{
  "headline": "This payment looks risky based on your recent chat.",
  "reasons":  [
    "You have known this contact for 21 days.",
    "The QR routes to Rayong while you are in Bangkok.",
    "We detected coaching and secrecy language."
  ],
  "questions": [
    "Can you call them right now to confirm?",
    "Why would they need your help moving money?"
  ],
  "cooloff": "Pause for 10 minutes before sending."
}
```

---

## 🎯 Target Users

<div align="center">

### Young professionals (22–35) in Thailand & SEA

</div>

| Profile | Why They're Vulnerable |
|:---|:---|
| 💬 Heavy LINE / Messenger / WhatsApp users | High trust formed via messaging apps |
| 💸 Use QR payments & instant transfers | Instant payment rails remove friction |
| 📱 Exposed to social engineering via chat | Emotional manipulation overrides rational warnings |

---

## 🛡️ Core Signals We Capture

| Signal | Description |
|:---:|:---|
| 💬 **Chat Grooming** | Scam phrases, escalation patterns, relationship age |
| 📍 **Location Mismatch** | User location vs. payment routing destination |
| ⏱️ **Relationship Timeline** | Days known, message frequency, request-to-pay proximity |
| 🎯 **Behavioral Baseline** | How this user normally pays |

---

## 🏆 Why This Wins

<div align="center">

| 🌐 Web Apps | 🏦 Banks | 🛡️ SentinelAI |
|:---:|:---:|:---:|
| ❌ No cross-app context | ❌ Only see transactions | ✅ Full device context |
| ❌ No authority to interrupt | ❌ Too late to intervene | ✅ Real-time intervention |
| ❌ Generic warnings | ❌ Can't block authorized | ✅ Contextual evidence |

</div>

### Defensibility Moat
- 🔒 **Android system-level access** — Accessibility Services for cross-app signals
- 📢 **System notifications** — Appear above other apps
- ⚡ **Low-latency device context** — Chat, location, behavior in real-time

---

## 🚀 Future Roadmap

| Phase | Features |
|:---|:---|
| 🔜 **Near Term** | Real Accessibility Services ingestion across chat apps |
| 📱 **Samsung Integration** | Knox hardening path for Galaxy devices |
| 🏦 **Bank Partnerships** | KBTG risk graph enrichment |
| 🌏 **Regional Expansion** | Scale across Southeast Asia |

---

## 📊 Demo vs Production

| Component | Real in Demo | Mocked in Demo |
|:---|:---:|:---:|
| Notification + UI | ✅ | — |
| Gemini API Calls | ✅ | Fallback available |
| Chat Capture | — | ✅ Preloaded dataset |
| Banking App Integration | — | ✅ Simulated flow |
| Maps Geolocation | ✅ | ✅ Either works |

---

## 📁 Repository Structure

```
SentinelAI/
├── 01_PRD_SentinelAI.md          # Product Requirements Document
├── 02_Storytelling_SentinelAI.md # Narrative & Demo Script
├── 03_Architecture_SentinelAI. md # Technical Architecture
└── README.md                      # You are here! 
```

---

<div align="center">

## 🎯 Our Job-To-Be-Done

### *"Help me avoid sending money to a scammer when I believe I'm helping someone I trust."*

---

### Built with ❤️ for the fight against scams

**SentinelAI** — *Behavioral Foresight, Not Transaction Fraud*

---

[![GitHub Stars](https://img.shields.io/github/stars/mcvoramet/SentinelAI? style=social)](https://github.com/mcvoramet/SentinelAI)
[![GitHub Forks](https://img.shields.io/github/forks/mcvoramet/SentinelAI?style=social)](https://github.com/mcvoramet/SentinelAI)

</div>
