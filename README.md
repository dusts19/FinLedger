# FinLedger

Full-stack personal finance tracker.

## Purpose
FinLedger allows user to:
- Track income and expenses across multiple accounts.
- Maintain accurate account balances via a double-entry ledger
- Subscribe to premium features (Stripe test mode)
- Receive AI-generated financial insights

This project emphasizes **clean architecture**, **domain modeling**, and **fintech-relevant engineering practices**.

## Tech Stack
- Backend: Java 21,Spring Boot 4.0.2, PostgreSQL
- Frontend: React, TypeScript
- Stripe (Test Mode)
- AI Insight Service (simulated)

## Running locally
- Backend: 
    `cd backend-spring/finledger`
    `mvn spring-boot:run`
- Frontend:
    `cd frontend-next`
    `npm install`
    `npm run dev`

## Architecture Overview
- **Ledger domain**: Accounts, Transactions, LedgerEntries implementing double-entry accounting
- **Billing domain**: Stripe integration and subscription management
- **Insights domain**: AI-generated financial suggestions


## Notes
All backend logic is fully tested via unit and integration tests.
Backend follows domain-driven design with strong emphasis on immutability and validation.

Refer to each folder for more details.