# FinLedger

Full-stack personal finance tracker.

## Purpose
FinLedger allows user to:
- Track income and expenses
- Maintain account balances via a ledger
- Subscribe to premium features (Stripe test mode)
- Receive AI-generated insights

This project emphasizes **clean architecture**, **domain modeling**, and **fintech-relevant engineering practices**.

## Tech Stack
- Backend: Java 21,Spring Boot 4.0.2, PostgreSQL
- Frontend: React, TypeScript
- Stripe (Test Mode)
- AI Insight Service (simulated)

## Running locally
- Backend: `mvn spring-boot:run`
- Frontend: `npm run dev`

## Architecture Overview
- **Ledger domain**: Accounts, Transactions, LedgerEntries (double-entry accounting)
- **Billing domain**: Stripe integration and subscription management
- **Insights domain**: AI-generated financial suggestions

Refer to each folder for more details.