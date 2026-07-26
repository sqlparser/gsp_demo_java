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
`java formatsql sqlfile.sql`

## [Format options](formatoptions.md)
 