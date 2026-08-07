# Modify a SQL AST and regenerate SQL

This demo implements a small application-layer SQL policy gate. It parses one
`SELECT`, removes the restricted `internal_note` output column, adds the
server-controlled tenant predicate `o.tenant_id = ?`, regenerates the SQL, and
parses the generated statement again before reporting success.

The existing filter contains `OR`, so the demo builds a parenthesis node around
that expression before joining the tenant predicate with `AND`. This preserves
the original boolean meaning and illustrates why AST modification is safer and
more maintainable than regular-expression or string replacement.

Run it from the repository root:

```bash
mvn -q exec:java \
  -Dexec.mainClass=gudusoft.gsqlparser.demos.modifySqlAst.ModifySqlAst
```

The output shows the original SQL, each policy decision, the regenerated SQL,
and the second-parse validation result. `ModifySqlAst.rewrite(...)` is public so
the policy flow can be called from an HTTP handler, query service, report
builder, or other code that receives SQL before database execution.

## Production use

Treat this as a focused integration template, not a complete SQL firewall. In a
production gate, expand the policy to validate allowed schemas, tables,
functions, subqueries, statement shapes, and resolved identifiers. Reject the
request when the transformation cannot be proven safe. Bind the tenant value
through the database driver; never concatenate it into the predicate.

The AST gate complements rather than replaces parameter binding, least-
privilege database accounts, database authorization or row-level security,
timeouts, resource limits, and audit logging.
