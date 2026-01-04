# SentinelAI Product Requirements Document (PRD)

## Product Vision
SentinelAI is an Android-native service that uses behavioral foresight to interrupt users before they authorize payments to scammers. It correlates real-time signals such as chat grooming patterns, recipient location mismatches, relationship age, and transaction intent to deliver a calm, explainable intervention at the moment of vulnerability. It does not block, accuse, or demand compliance. It creates clarity.

---

## Target User
### Primary User
Young professionals (22–35) in Thailand and SEA who:
- use LINE / Messenger / WhatsApp heavily
- transact via QR payments and instant transfers
- are exposed to social engineering via chat channels

### Why They’re Vulnerable
- High trust formed via messaging apps
- Instant payment rails remove friction
- Emotional manipulation overrides rational warnings

---

## Core Job-To-Be-Done
**“Help me avoid sending money to a scammer when I believe I’m helping someone I trust.”**

Not:
- “Catch criminals”
- “Teach me security”
- “Block my payment”

---

## In-Scope for Demo (Must-Haves)
✅ **One Unforgettable Moment**
- User scans a QR to pay
- System interrupts *before confirmation* with a contextual warning

✅ **Chat Grooming Context**
- Simulated chat feed shows scam patterns and escalation
- Risk score and “relationship age” displayed

✅ **Google Maps Location Mismatch Signal**
- Compare user location vs recipient/merchant routing location
- Mismatch becomes a credibility signal, not an accusation

✅ **Explainable Intervention UI**
- Android system notification (not in-app popup)
- Tap-through explanation screen
- 2–3 Socratic questions
- Cooling-off recommendation

✅ **Gemini 3 Usage (Demo)**
- Flash: classify scam language / tactics quickly
- Pro: generate calm explanation and Socratic questions

---

## Out-of-Scope (Deliberate)
❌ Blocking payments  
❌ Full banking integrations or real transaction hooks  
❌ National-scale accuracy claims  
❌ Merchant guilt labeling  
❌ Knox “on-device inference” claims (future only)  
❌ True cross-platform support (Android-first)

---

## Demo Success Criteria
Success is not benchmark accuracy. Success is:

1. **Judge comprehension:** they can explain it in one sentence after watching.
2. **Memorability:** they remember the interruption moment.
3. **Inevitability:** “Why isn’t every phone doing this?”
4. **Credibility:** you clearly label what’s mocked vs real.
5. **Tone:** respectful, non-accusatory, agency-preserving.

---

## Non-Goals
- No “national fraud detection system” claims
- No “we saved X baht” claims
- No production security guarantees
- No invasive surveillance framing

---

## Future Implementation (Mention Only)
- Real Accessibility Services ingestion across chat apps
- Knox hardening path
- Bank risk graph enrichment (KBTG signals)
- On-device inference evolution
