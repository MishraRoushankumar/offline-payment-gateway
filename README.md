# Offline Payment Gateway

An experimental payment gateway designed to support payment requests from
devices without direct internet connectivity by using nearby devices as
communication relays.

The project explores backend, distributed systems, networking, and fintech
concepts such as reliable message delivery, transaction idempotency,
cryptographic security, and financial ledger consistency.

## Motivation

Traditional online payment flows require the user's device to communicate
with the payment infrastructure.

This project explores an alternative transport model:

    Offline Device
          |
       Bluetooth
          |
       Relay
          |
       Relay
          |
     Internet Relay
          |
          v
    Payment Gateway
          |
          v
      Demo Bank

The payment itself is still verified and settled by the bank. The mesh
primarily provides a communication path for an otherwise offline device.

## Planned Features

- Offline payment request queuing
- Bluetooth/mesh-based message forwarding
- Multi-hop payment request delivery
- End-to-end encrypted payment payloads
- AES-GCM payload encryption
- RSA-based encryption key protection
- Digital signatures for payment authorization
- Transaction IDs and idempotent payment processing
- Retry and delivery-attempt tracking
- Duplicate message detection
- Payment transaction state machine
- PostgreSQL-based financial ledger
- Redis-based idempotency and temporary state
- Demo bank server
- Failure and concurrency testing

## Technology Stack

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Redis
- Spring Security
- Maven

## System Scope

This is an educational prototype inspired by offline-resilient digital
payment systems.

It is **not an implementation of UPI** and is not intended to connect to
real banking or UPI infrastructure.

The initial version focuses on:

1. Reliable delivery of payment requests
2. Secure communication through untrusted relay devices
3. Idempotent transaction processing
4. Correct financial state management
5. Failure and retry handling

Offline double-spending prevention and integration with real payment rails
are outside the initial scope.

## Project Status

🚧 Under development

The project is currently in the initial setup phase.

## License

To be added.