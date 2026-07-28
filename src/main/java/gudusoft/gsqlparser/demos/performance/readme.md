## Description

Two demos about the cost of creating parsers rather than the cost of parsing.

Building a `TGSqlParser` loads the grammar tables for its dialect, which is by
far the most expensive part of a single parse — roughly a second in the run
below. Pooling parsers amortises that away, which matters for a service parsing
many statements.

| Class | What it does |
|-------|--------------|
| `ParserPoolDemo` | Walks through the pool's behaviour: initialisation cost, then reuse |
| `ParserPoolBenchmark` | Timed comparison of pooled versus non-pooled parsing |

## Usage

Neither takes arguments.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.performance.ParserPoolDemo \
    -Dexec.classpathScope=compile
```

```text
========================================
    SQL Parser Pool Demonstration
========================================

=== Initialization Phase ===
Loading grammar tables (one-time cost)...
Grammar tables loaded in 907 ms
```

`ParserPoolBenchmark` runs longer; the API it exercises is
`TParserPoolFactory`.
