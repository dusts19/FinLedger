# Backend - FinLedger

## Purpose
The backend handles the core domain logic for the FinLedger application, including:
- Account management (aggregate root: `Account`)
- Double-entry ledger: `Transaction` + `LedgerEntry`
- Subscription management via Stripe (test mode)
- AI-generated insights (simulated)

## Prerequisites
- Java 21
- Maven
- PostgreSQL running locally

## Running the backend
1. Ensure PostgreSQL is running and configured
2. Navigate to the backend folder:
    `cd backend`
3. Start the application:
    `mvn spring-boot:run`

## Running Tests
Run all backend tests:
    `mvn test`

## Notes
Ledger transactions follow double-entry accounting rules (debits = credits).
The backend uses domain-driven design principles.