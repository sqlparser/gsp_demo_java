## Description
A simple demo to illustrate how to get table and join type from a select statement.

```sql
SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate
FROM Orders
INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID;
```

```
Join Type table Name
---------------------------------------
Inner Join	Customers
``` 

## Usage
`java getTableJoinType sqlfile.sql`


