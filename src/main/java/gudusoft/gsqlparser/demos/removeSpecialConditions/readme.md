## Description

Removes conditions matching particular shapes from a `WHERE` clause, using
`ExpressionChecker` to decide what qualifies.

**This demo has no `main()`** — unlike its neighbours it is a class you call,
not a program you run.

## Usage

```java
RemoveCondition rc = new RemoveCondition(sqlFile, EDbVendor.dbvoracle, ...);
String result = rc.getRemoveResult();
```

Both a `File` and a `String` constructor are available.

The worked examples live in the test rather than in a `main()`:

```bash
mvn test -Dtest=testRemoveSpecialConditions
```

`src/test/java/gudusoft/gsqlparser/removeSpecialConditionsTest/testRemoveSpecialConditions.java`
is the best place to see what it does to real input.

See `removeCondition` for the simpler, runnable version.
