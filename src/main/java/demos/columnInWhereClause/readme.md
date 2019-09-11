## Description
Decode the SQL where clause to get columns used in the where condition.

```sql
Select firstname, lastname, age 
from Clients 
where State = "CA" and  City = "Hollywood"
```

the output is
```
column: State
Operator: =
value: "CA"

column: City
Operator: =
value: "Hollywood"
```

## Usage
`java ColumnInWhereClause`


