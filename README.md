#  Java & MySQL Inventory & Transaction Management System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Pattern-DAO%20%7C%20Singleton-blue?style=for-the-badge)

A high-reliability, console-driven backend inventory and order processing engine. The system is engineered to showcase core software engineering practices: strict relational database modeling, thread-safe transactional integrity (ACID), and robust protection against SQL injection vulnerabilities using enterprise JDBC standards.

---

##  Key Architectural Pillars

* **Explicit ACID Transaction Control:** Manages end-to-end checkout lifecycles. Stock deductions, order headers, and line-item records operate within manual atomic transaction boundaries (`commit` / `rollback`), completely eliminating race conditions and partial write anomalies.
* **SQL Injection Immunity:** Replaces fragile dynamic string concatenation with parameterized `PreparedStatement` interfaces across all write and analytical read layers.
* **Normalized Relational Schema (3NF):** Designed with foreign key referential integrity constraints, automated cascade deletions, and index-optimized junction entities.
* **Complex Data Aggregation:** Built-in analytical reporting pipelines leveraging multi-table inner joins, grouping sets, and arithmetic aggregations for product sales velocity.
* **Defensive Error Handling:** Multi-layer exception routing catching low-level `SQLException` events and executing automated rollbacks to preserve database consistency.

---

##  System Architecture & Workflow

```text
       [ Console Client / CLI ]
                  │
                  ▼
         [ Service Layer ]
     (OrderService / ReportService)
                  │
                  ▼
       [ JDBC Connection Layer ]
       (PreparedStatement / AutoCommit=false)
                  │
        ┌─────────┴─────────┐
        ▼                   ▼
 [ Commit on Success ] [ Rollback on Failure ]
        │                   │
        └─────────┬─────────┘
                  ▼
      [ MySQL 8.0 Relational DB ]
