# FinLedger

Backend-focused fintech ledger built with Spring Boot and domain-driven design.

FinLedger is a full-stack personal finance tracker built around a **double-entry accounting ledger**. The project demonstrate backend-focused engineering practices including domain-driven design (DDD), layered architecture, and strong domain invariants.

---

## Purpose

FinLedger allows users to:

- Track income and expenses across multiple accounts.
- Maintain accurate account balances using double-entry accounting
- Record and reverse financial transactions

This project emphasizes **clean architecture**, **domain modeling**, and **fintech-style ledger design**.

---

## Tech Stack

**Backend**
- Java 21
- Spring Boot
- H2 (can switch tot PostgreSQL)
- JPA / Hibernate
- JUnit, Mockito, AssertJ

**Frontend (planned)**
- React
- TypeScript

---

## Project Structure
finledger/
│
├── backend/    Spring Boot ledger API
│
├── frontend/   Planned React frontend
│
└── README.md

The backend contains the core financial ledger implementation including:

- Account management
- Journal entries and journal lines
- Double-entry transaction enforcement
- Balance calculation
- REST API endpoints

---

## Running locally

### Backend
    `cd backend-spring/finledger`
    `mvn spring-boot:run`
### Frontend (planned)
    `cd frontend-next`
    `npm install`
    `npm run dev`

---

## Architecture Overview

The backend follows a layered architecture:

api/            REST controllers and DTOs
application/    Application services
domain/         Core business logic and aggregates
infrastructure/ Persistence adapters (JPA repositories)

The ledger enforces strict accounting rules:

- Transactions must contain at least two lines
- Debit must equal credits
- All lines share the same currency
- Posted transactions are immutable

---

## Notes

- The backend includes extensive unit and integration tests
- Domain rules are enforced through domain models rather than controllers
- The project focuses on backend architecture and financial ledger correctness

For more details see the backend documentation in `/backend`.