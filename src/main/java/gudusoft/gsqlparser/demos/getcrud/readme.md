## Description
Get how table is effected(select/delete/insert/update) in the SQL statement.

```sql
UPDATE Customers
SET ContactName = 'Alfred Schmidt', City= 'Frankfurt'
WHERE CustomerID = 1;
```

Result:
```
CRUD: update, Table:Customers
	Columns:
		ContactName
		City
		CustomerID

Summary
Customers	0(s)	0(c)	0(d)	0(i)	1(u)
```


## Usage
`java getcrud sqlfile.sql`


