# hr_mini

A small Oracle HR warehouse — staging tables, a view, three loads and an
update — written so that **column-level lineage across a schema is runnable on
the trial parser**.

```bash
mvn package -DskipTests
java -jar target/gsp_demo_java-1.0-SNAPSHOT-dlineage.jar \
     /f samples/dlineageBasic/oracle/hr_mini/hr_mini.sql /t oracle /json
```

120 relationships on parser 4.2.6: 82 direct (`fdd`) and 38 indirect (`fdr`).
`/tableLineage /csv` gives the same flow at table level.

## Why it exists

The other directories under `samples/dlineageBasic/` hold real vendor schema
dumps, and **every one of them is over the trial parser's 10,000-byte limit** —
`hr_cre.sql` is 10,481 bytes, `pcus_v3.sql` is 99,139. Point the lineage demo
at one of those and you get a `<dlineage>` document whose only content is an
`<error>` saying the trial build cannot process a query that size. It looks
like "no lineage found". A first-time evaluation in August 2026 hit exactly
that and concluded schema-scale lineage did not work.

This file is 5,212 bytes, so it fits, and it is written to exercise the parts
that make lineage interesting rather than to be a realistic HR schema:

| construct | where |
|---|---|
| view over a `LEFT JOIN` | `v_employee_enriched` |
| expression columns (concatenation, `LOWER`, `NVL`) | the same view |
| aggregate into a dimension | `dim_department` load, `COUNT` + `GROUP BY` |
| a CTE feeding an aggregate | `fact_compensation` load |
| `CASE WHEN` over an aggregate, and over a column comparison | `pay_band`, `above_job_floor` |
| correlated subquery in an `UPDATE` | the final `UPDATE dim_department` |

All the DDL is in the file, so every column resolves without a
`metadata.json`. That is the "put the CREATE TABLE statements in the same
script" answer to ambiguous columns, worked through at schema scale — see
[the lineage demo's readme](../../../../src/main/java/gudusoft/gsqlparser/demos/dlineage/readme.md).

`.github/scripts/smoke-dlineage-jar.sh` runs this file in CI and fails if it
ever reaches 10,000 bytes, if it stops producing relationships, or if the
output starts carrying the trial-limit error. Keep additions small.
