# DNA Sequence Reconstruction | Scala & Parallel Computing

A functional programming project that explores sequential and parallel algorithms for reconstructing unknown DNA sequences using an oracle-based approach. The project implements multiple algorithmic strategies, suffix trees, and parallel computation techniques to improve efficiency and evaluate performance.

## Overview

The **DNA Sequence Reconstruction Problem (DSRP)** consists of reconstructing an unknown DNA sequence \(S\) of length \(N\), over the alphabet `{a, c, g, t}`, using an oracle that answers whether a given sequence is a substring of \(S\).

Instead of directly accessing the original sequence, algorithms must progressively identify valid candidates through oracle queries.

This project explores different algorithmic approaches to solve the problem, combining functional programming, efficient data structures, and parallel computing.

### Key Features

- Multiple sequential reconstruction algorithms with increasing levels of optimization.
- Parallel implementations using data parallelism and task parallelism.
- Trie and suffix tree structures for efficient candidate representation and filtering.
- Configurable parallelization thresholds to balance computational overhead and performance.
- Performance benchmarking with ScalaMeter.
- CSV generation for analyzing execution times and speedup.

## Algorithms

The project implements five sequential approaches and their corresponding parallel versions.

| Algorithm | Description |
|---|---|
| **Naive** | Explores all possible DNA sequences of length N. |
| **Improved** | Builds and filters candidate sets incrementally using oracle queries. |
| **Turbo** | Combines smaller candidate sequences to construct longer ones. |
| **Enhanced Turbo** | Reduces unnecessary oracle queries through improved candidate filtering. |
| **Accelerated Turbo** | Uses suffix trees to improve candidate representation and filtering. |

### Parallel Implementations

Parallel versions of the algorithms leverage two approaches:

- **Data Parallelism:** Uses Scala parallel collections (`.par`) to distribute candidate generation and filtering operations.
- **Task Parallelism:** Uses `ForkJoinPool`, `RecursiveTask`, and custom parallel execution primitives to divide computational workloads.

A configurable threshold determines when parallel execution is activated, allowing smaller workloads to remain sequential and avoiding unnecessary parallelization overhead.

## Project Structure

The main implementation is organized into the following Scala packages:

```text
src/main/scala/
│
├── Oraculo/
│   └── package.scala
│
├── ArbolSufijos/
│   └── package.scala
│
├── ReconstCadenas/
│   └── package.scala
│
├── common/
│   └── package.scala
│
├── ReconstCadenasPar/
│   └── package.scala
│
└── TestRunner/
    └── package.scala
```

| Package | Responsibility |
|---|---|
| `Oraculo` | Defines the DNA alphabet, oracle interface, and oracle creation. |
| `ArbolSufijos` | Implements tries and suffix tree operations for candidate management. |
| `ReconstCadenas` | Contains the sequential reconstruction algorithms. |
| `common` | Provides task parallelism primitives based on ForkJoinPool. |
| `ReconstCadenasPar` | Contains the parallel implementations of the reconstruction algorithms. |
| `TestRunner` | Executes performance benchmarks and exports results to CSV files. |

## Technologies

- **Language:** Scala 3.3.6
- **Build Tool:** sbt
- **Parallel Computing:** Scala Parallel Collections, ForkJoinPool
- **Benchmarking:** ScalaMeter
- **Testing:** MUnit

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- sbt

### Compilation

Clone the repository:

```bash
git clone https://github.com/juliana0623/proyecto-reconstruccion-cadenas.git

cd proyecto-reconstruccion-cadenas
```

Compile the project:

```bash
sbt compile
```

## Running the Algorithms

The reconstruction algorithms can be called by providing the target sequence length and an oracle.

For example:

```scala
val sequence = "gaatccagat".toList

val oracle = Oraculo.crearOraculo(1)(sequence)

// Sequential implementation
val result = ReconstCadenas
  .reconstruirCadenaTurboMejorada(sequence.length, oracle)

// Parallel implementation (threshold = 1000)
val parallelResult = ReconstCadenasPar
  .reconstruirCadenaTurboMejoradaPar(1000)(sequence.length, oracle)
```

The project also provides implementations for the other reconstruction strategies, both sequential and parallel.

## Performance Benchmarking

The `TestRunner` module uses **ScalaMeter** to evaluate the performance of sequential and parallel implementations.

For each input size, it:

1. Generates a random DNA sequence of length \(N = 2^k\).
2. Creates the corresponding oracle.
3. Measures the execution time of the enhanced turbo sequential algorithm.
4. Measures the execution time of its parallel implementation.
5. Calculates the speedup.

The speedup is calculated as:

\[
\text{Speedup} = \frac{T_{\text{sequential}}}{T_{\text{parallel}}}
\]

Where:
- \(T_{\text{sequential}}\) is the sequential execution time.
- \(T_{\text{parallel}}\) is the parallel execution time.

### Running the Benchmark

Execute:

```bash
sbt run
```

If prompted, select `TestRunner` as the main application.

The benchmark prints execution times and speedup values to the console and generates CSV files in the project root:

```text
resultados_acelerada_YYYYMMDD_HHMMSS.csv
```

These files can be used to create performance comparison tables and visualizations.

## Performance Considerations

Parallel execution does not necessarily improve performance for every input size. Creating and managing parallel tasks introduces computational overhead.

The configurable threshold controls when parallel execution is activated:

- **Small workloads:** Sequential execution avoids unnecessary parallelization overhead.
- **Large workloads:** Parallel execution distributes candidate generation and filtering across multiple workers.

Experimenting with different thresholds helps identify a suitable balance between task management overhead and execution speedup.

## Concepts Explored

This project provided practical experience with:

- Functional Programming and immutable collections.
- Recursive algorithms and higher-order functions.
- Algorithm design and optimization.
- Tries and suffix trees.
- Data Parallelism and Task Parallelism.
- Concurrent execution with ForkJoinPool.
- Performance benchmarking and computational analysis.

---

**Developed as an academic project at Universidad del Valle.**
