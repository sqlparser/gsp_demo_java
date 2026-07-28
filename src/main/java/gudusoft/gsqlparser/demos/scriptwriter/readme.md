## Description

Rebuilds a SQL statement from its parse tree. The demo parses a large, densely
formatted Oracle query, including optimiser hints, correlated scalar subqueries
and `CASE` expressions, then writes it back out.

This is the round-trip that any AST rewrite depends on: if the tree can be
regenerated faithfully, it can be modified first. See `modifysql` and
`sqlrefactor` for the modifying half.

## Usage

Takes no arguments; the query is inline in `scriptwriter.java`.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.scriptwriter.scriptwriter \
    -Dexec.classpathScope=compile
```

```text
select /*+ leading(dirs inpt ptbs dmpg) index(dirs IX_MNDHDIRS_03) use_nl(dmpg)  */
nvl(( select /*+index_desc(icdr PK_pmihicdr ) */
icdr.ordtype
 from
pam.pmihicdr icdr
 where icdr.instcd = :1 and ...
```

Edit the `sqltext` in `scriptwriter.java` to run it against your own SQL.
