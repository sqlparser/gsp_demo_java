## Description

Removes conditions from a `WHERE` clause through the parse tree, leaving the
rest of the statement, including its `GROUP BY`, intact.

Useful for stripping tenant or environment filters out of a query before
analysing it, or for producing the "unfiltered" form of a report query.

Compare `removeSpecialConditions`, which targets particular condition shapes,
and `sqlrefactor`, which cleans up redundant parentheses.

## Usage

Takes no arguments; the query is inline in `removeCondition.java`.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.removeCondition.removeCondition \
    -Dexec.classpathScope=compile
```

```text
SELECT SUM (d.amt)
FROM summit.cntrb_detail d
WHERE d.fund_coll_attrb IN ( 'ShanXi University' )
AND d.fund_acct IN ( 'Eclipse.org' )
GROUP BY d.id;
```

There is also a test covering this demo at
`src/test/java/gudusoft/gsqlparser/removeConditionTest/`.
