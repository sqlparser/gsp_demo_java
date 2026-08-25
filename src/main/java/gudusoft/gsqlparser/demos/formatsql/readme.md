## Description
Tidy and improve SQL readability with different format options. 
The output can be in html format as well.

```sql
WITH upd AS (
  UPDATE employees SET sales_count = sales_count + 1 WHERE id =
    (SELECT sales_person FROM accounts WHERE name = 'Acme Corporation')
    RETURNING * 
)
INSERT INTO employees_log SELECT *, current_timestamp FROM upd;";
```

formatted SQL
```sql
WITH upd
     AS ( UPDATE employees
SET    sales_count = sales_count + 1
WHERE  ID = (SELECT sales_person
             FROM   accounts
             WHERE  NAME = 'Acme Corporation') RETURNING * ) 
  INSERT INTO employees_log
  SELECT *,
         Current_timestamp
  FROM   upd;
```

## Usage

The demo takes a bare filename — no `/f`:

```bash
mvn package -DskipTests
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.formatsql.formatsql \
    -Dexec.args="your.sql"
```

Add `/tolerant` to keep formatting a file that contains statements the parser
rejects. `samples/formatsql/mixed-valid-invalid.sql` is checked in for exactly
that: without the flag it stops at the bad statement and tells you to add it,
and with it you get the formatted output plus `Formatter status:
OK_WITH_RECOVERY`.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.formatsql.formatsql \
    -Dexec.args="samples/formatsql/mixed-valid-invalid.sql /tolerant"
```

`formatsqlInHtml` in this directory writes the same output as HTML.

## [Format options](formatoptions.md)
 