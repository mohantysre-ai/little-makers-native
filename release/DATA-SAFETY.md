# Native edition Data safety worksheet — not submitted

Scope: exact native Android 2.1.1 build, com.littlemakers.village. This does not cover the web/Capacitor app or its older release worksheets.

| Console item | Proposed answer / evidence |
|---|---|
| Does the app collect or share user data? | No, if final release retains the inspected behavior: no network permission, no external intents, no SDKs transmitting data. Confirm against final artifact and runtime observation. |
| Local data | Saved games, completed IDs/timestamps, stars/coins, purchases/equipment, settings and salted parent PIN hash. On-device-only processing. Describe in privacy policy even though it is excluded from off-device collection reporting. |
| Ads | No. No ad SDK or cross-promotion. |
| Accounts | No online accounts or account creation. A local settings PIN is not an account. Account-deletion URL requirement is not applicable to this implementation. |
| Data deletion | Parent-gated in-app reset removes gameplay/economy records; Android Clear storage removes all records including settings/PIN. No server copy. |
| Security claims | No independent review, no end-to-end encryption, no separately encrypted database. Do not answer security questions by inventing safeguards. |
| Audience | For intended ages 4–8, assess and declare “5 and under” and “6–8.” Adults using parent settings do not make the child app adults-only. |
| IARC | Accurately answer questions about current content. No observed violence, gambling, chat, location, ads or money purchases. Google/IARC assigns the rating; do not invent it. |
| App access | Child content and offline privacy need no login. Initial parent gate lets a reviewer create a six-digit PIN. Provide review steps below. |
| Review access steps | Fresh install > Parent space > create and repeat a tester-chosen six-digit PIN > Continue. Return with same PIN. Forgotten PIN: Android Clear storage, with data-loss warning. Privacy policy opens from Home without PIN. Never share a real family's PIN. |

Every change to SDKs, uploads, billing, external links or account features requires a fresh review and updated declarations. No form has been submitted to Play Console.

Official references checked 12 October 2026:
- https://support.google.com/googleplay/android-developer/answer/10787469
- https://support.google.com/googleplay/android-developer/answer/9893335
- https://support.google.com/googleplay/android-developer/answer/10144311
