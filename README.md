# Constraint-Satisfaction Sudoku Engine & Relational Database Layer

![Language](https://img.shields.io/badge/language-Java-blue.svg)
![Type](https://img.shields.io/badge/computation-Constraint--Satisfaction-brightgreen.svg)
![Architecture](https://img.shields.io/badge/architecture-Modular--Data--Layer-orange.svg)

A high-performance constraint-satisfaction puzzle-matrix solver and a thread-safe local relational database client management framework implemented in pure Java. This architecture splits into two primary micro-engines: a highly optimized backtracking matrix recursive search layer to evaluate systemic state grids, and a multi-threaded database connectivity portal built to manage transactional entries.

---

## Key Design Principles

* **Advanced Backtracking Heuristics:** Solves 9x9 cellular grids using an optimized depth-first constraint propagation algorithm, minimizing search-space depth via immediate row, column, and sub-grid validation tracking.
* **Smart Pruning Execution:** Implements predictive optimization strategies by sorting search candidates based on cell-specific degrees of restriction, preventing unviable branch executions early in the recursive stack.
* **Decoupled Data Architecture:** Strictly isolates spatial transformation grids, relational schema configurations, and graphical view models via clean object-oriented data bounds.
* **Transactional Integrity Management:** Leverages abstract connection pooling interfaces to securely stream schema updates, row injections, and cross-table lookups without exposing hardcoded system credentials.

---

## Technical Architecture & Core Modules

### 1. `Sudoku` (Matrix Constraint Core)
The algorithmic powerhouse tasked with modeling and processing cellular boards.
* **Recursive State Propagation:** Iterates through highly dense multi-dimensional layout states, placing values and using instant validation loops to verify grid compliance rules.
* **Solution Matrix Multiplicity:** Engineered to not only find a single completed matrix state, but also stream and count all mathematically possible grid variations under custom anchor parameters.

### 2. `Database Controller & Admin Layer`
A data abstraction module engineered to query, cache, and mutate persistent table schemas.
* **Safe Driver Resolution:** Features dynamic class-path lookup routing to swap between relational engines (e.g., MySQL, H2) based on live environment configurations.
* **Sanitized Query Delivery:** Abstracts low-level database operations using prepared statement pipelines to handle batch processing and ensure optimal execution speeds.

### 3. `SudokuFrame & Interactive Client`
An asynchronous graphical dashboard built on the Swing architecture to map out cell updates in real time.
* **Event-Driven Grid Mutations:** Decouples heavy computational matrix processing from the main UI thread to ensure continuous responsiveness during complex multi-solution queries.

---

## Usage & Deployment Execution

The architecture runs natively on standard Java runtimes with zero external compilation dependencies.

### Algorithmic Execution
To run the automated constraint satisfaction solver via the terminal:
```bash
# Compile solver source trees
javac -d bin src/sudoku/**/*.java

# Execute main solver test runner
java -cp bin sudoku.SudokuMain