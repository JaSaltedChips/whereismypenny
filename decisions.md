# Where's My Penny — Milestone 1 Decision Record

Planning reference date: 2026-10-06. Revisit Play Store and SDK requirements before submission.

---

## Environment

| Item | Decision |
|---|---|
| App name | Where's My Penny |
| Application ID | `app.harsha.wheresmypenny` (permanent — never changes after publishing) |
| Source authoring | Windows: `E:\claude\allgemein\android-app-expense-monitor` |
| Build machine | Separate Linux PC/VM; code shared via Git |
| Min SDK | API 26 — Android 8.0 (Oreo) |
| Target SDK | Current Play Store requirement — confirm at build time |
| Language stack | Kotlin, Jetpack Compose, Material 3, Room |

---

## Account Types

Three types, identical to the desktop reference:

| Type | Behaviour | Balance meaning |
|---|---|---|
| `BANK` | Asset account | Money available (positive = you have it) |
| `CASH` | Asset account | Cash on hand (positive = you have it) |
| `CREDIT_CARD` | Liability account | Amount owed (positive = you owe it) |

**Credit card balance formula:**
`opening_balance + charges − payments`

`opening_balance` represents initial debt owed on the day tracking begins (0 if starting fresh).
Purchases increase the balance; TRANSFER payments reduce it; payments are never counted as expenses.

**Archiving vs deletion:** Accounts with any transaction history are archived (`is_active = false`), never deleted. Archived accounts are hidden from entry screens but preserved in history and reports.

---

## Transaction Types

Three types. Amounts are always positive integers (minor currency units, e.g. euro cents).

| Type | From account | To account | Category |
|---|---|---|---|
| `EXPENSE` | Any account type | — | Expense category (required) |
| `INCOME` | — | BANK or CASH only (not CREDIT_CARD) | Income category (required) |
| `TRANSFER` | Any account | Any different account | — |

**Validation rules:**
- EXPENSE: `from_account` required, `to_account` must be absent.
- INCOME: `to_account` required and must not be CREDIT_CARD, `from_account` must be absent.
- TRANSFER: both `from_account` and `to_account` required and different.
- Same-account transfer is rejected.

---

## Refunds

**Decision: INCOME with a reserved "Refund" income category.**

A refund (e.g. shop returns money after a previous purchase) is recorded as an INCOME transaction to the account that receives the money, using the pre-seeded "Refund" income category.

- The "Refund" category is pre-seeded and cannot be deleted.
- Reports show refunds as a distinct row in the income section, labelled "Refund".
- Refunds do not net against the original expense category in v1.
- This is acknowledged as a v1 simplification; it means net income appears higher by the refund amount. The label "Refund" makes the nature visible.

---

## Opening Balances and Dates

- Opening balance for BANK and CASH: non-negative (≥ 0). Zero if starting fresh.
- Opening balance for CREDIT_CARD: non-negative, represents initial debt on the opening date.
- Opening date: the day tracking begins for that account. Transactions before this date are not entered.
- Future-dated entries: allowed. The user sets the date; the app does not restrict it.
- Corrections: edit or delete any transaction. All affected balances recalculate from stored transactions (no cached running totals in v1).

---

## Ledger and Currency

- One ledger in v1. All accounts share the same app-wide currency.
- User selects the currency once during first-time setup (ISO 4217 code, e.g. EUR, USD, INR).
- Currency selection can be changed in settings **only while there are no transactions**; once transactions exist it is locked. Changing a label or account name never reinterprets monetary amounts.
- No exchange rates, no mixed-currency totals, no multi-ledger in v1.

---

## Backup and Restore

- Format: plaintext JSON, versioned (field `"backup_version": 1`).
- The app displays a clear warning before sharing: "This file contains your unencrypted financial data. Only share it with apps or locations you trust."
- Restore: validates the full backup before touching live data. Atomic or staged; current data preserved on any failure. User confirms before replacement.
- Malformed, oversized, or wrong-version backups are rejected with a plain error message.
- Android system cloud backup: the database file is excluded via `android:allowBackup` and `backup_rules.xml`. The user backs up manually using the in-app export only.
- No automatic app-managed cloud backup.
- Encryption: plaintext in v1. May be added in a later version.

---

## Language and Translations

- Initial release: English only.
- All user-visible strings in Android string resources — no hardcoded strings in code or layouts.
- Pluralization via `<plurals>` resources.
- Layouts must be RTL-aware from day one (use start/end instead of left/right).
- Translation to additional languages is later work, not a blocker.

---

## Representative Accounting Examples

These examples define agreed expected outputs. Unit tests must reproduce them exactly.

**Assumptions:** currency EUR, cents as minor units.

### Example 1 — Basic expense

| Step | Transaction | BANK balance (cents) |
|---|---|---|
| Setup | Opening balance: 100,000 (€1,000.00) | 100,000 |
| 1 | EXPENSE €45.00 Groceries from BANK | 95,500 |
| 2 | EXPENSE €12.50 Transport from BANK | 94,250 |

Expected: BANK = 94,250 cents (€942.50)

### Example 2 — Credit card charge, partial payment, full payment

| Step | Transaction | BANK (cents) | CARD balance / owed (cents) |
|---|---|---|---|
| Setup | BANK opening 200,000; CARD opening 0 | 200,000 | 0 |
| 1 | EXPENSE €80.00 Shopping from CARD | 200,000 | 8,000 |
| 2 | EXPENSE €20.00 Eating out from CARD | 200,000 | 10,000 |
| 3 | TRANSFER €50.00 BANK → CARD (partial payment) | 195,000 | 5,000 |
| 4 | TRANSFER €50.00 BANK → CARD (full clearance) | 190,000 | 0 |

Monthly report for this period:
- INCOME: 0
- EXPENSE: 10,000 (€100.00 — the two card charges)
- TRANSFER: 10,000 (€100.00 — payments, not counted as income or expense)
- NET: −10,000

### Example 3 — Income, refund, and net

| Step | Transaction | BANK (cents) |
|---|---|---|
| Setup | Opening 50,000 | 50,000 |
| 1 | INCOME €3,000.00 Salary to BANK | 350,000 |
| 2 | EXPENSE €45.00 Groceries from BANK | 345,500 |
| 3 | INCOME €10.00 Refund to BANK (category: Refund) | 346,500 |

Monthly report:
- INCOME: 301,000 (Salary 300,000 + Refund 10,000) — Refund shown as separate category
- EXPENSE: 4,500 (Groceries)
- NET: +296,500 (€2,965.00)

### Example 4 — Transfer does not inflate income or expense

| Step | Transaction | BANK-A | BANK-B |
|---|---|---|---|
| Setup | BANK-A opening 100,000; BANK-B opening 0 | 100,000 | 0 |
| 1 | TRANSFER €40.00 BANK-A → BANK-B | 96,000 | 4,000 |

Monthly report:
- INCOME: 0
- EXPENSE: 0
- TRANSFER: 4,000 (shown separately, not in net calculation)
- NET: 0

### Example 5 — Opening balance with existing credit card debt

| Step | Transaction | CARD balance / owed (cents) |
|---|---|---|
| Setup | CARD opening_balance 15,000 (€150.00 existing debt) | 15,000 |
| 1 | EXPENSE €30.00 from CARD | 18,000 |
| 2 | TRANSFER €18,000 BANK → CARD (full payment) | 0 |

---

## Unresolved Items (carry to later milestones)

- Target SDK level: confirm current Play Store requirement at build time.
- Chart library selection: validate export, accessibility, offline, and license at Milestone 2.
- Database encryption: assess before Milestone 10 (security audit).
- Android Keystore key invalidation policy: assess before Milestone 10.
- App icon and store artwork: fictional data only — needed at Milestone 12.
- Support email and privacy policy URL: needed before closed testing (Milestone 11).
- Publisher identity, Impressum, GDPR, and EU trader disclosure: assess before submission.
- Physical Android device access for Play verification: plan before Milestone 11.
- Play Store closed-testing participant recruitment: 12 testers × 14 days minimum — start early.
