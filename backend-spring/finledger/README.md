# Backend - FinLedger

## Purpose
The backend handles domain logic for the FinLedger app:
- Account management (aggregate root: Account)
- Double-entry ledger (Transaction + LedgerEntry)
- Subscription management (Stripe)
- AI insights generation

## Running
1. Ensure PostgreSQL is running
2. `cd backend`
3. `mvn spring-boot:run`

## Tests
Run all backend tests:
