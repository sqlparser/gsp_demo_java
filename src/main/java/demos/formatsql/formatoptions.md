#### 1.opearateSourceToken

**usage**: If it is true, the GSP operate the source tokens. If not, the GSP will ignore the operation request.

Type: boolean

Optional values: [true, false]

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID ,Customers.CustomerName, Orders.OrderDate FROM   Orders INNER JOIN Customers ON Orders. CustomerID=Customers.CustomerID;

```

#### 2.selectColumnlistStyle

**usage**: If it is AsStacked,every select column will start a new line. If it is AsWrapped, select column will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `AsWrapped`, SQL format:

```sql
SELECT Orders.OrderID ,Customers.CustomerName, Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

#### 3.selectColumnlistComma

**usage**: This option will change select statement comma position and white space.

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma]

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
SELECT Orders.OrderID
       , Customers.CustomerName
       , Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `LfBeforeComma`, SQL format:

```sql
SELECT Orders.OrderID
       ,Customers.CustomerName
       ,Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

#### 4.selectItemInNewLine

**usage**: If it is true, select token will create a new line.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `true`, SQL format:

```sql
SELECT
  Orders.OrderID,
  Customers.CustomerName,
  Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

#### 5.alignAliasInSelectList

**usage**: If it is true, select alias will be vertical alignment.

Type: boolean

Optional values: [true, false]

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT home_address AS h,
       age          AS a
FROM   person;
```

- When the value is `false`, SQL format:

```sql
SELECT home_address AS h,
       age AS a
FROM   person;
```

#### 6.treatDistinctAsVirtualColumn

**usage**: If it is true, `Distinct` will be treated a virtual column.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT DISTINCT Company
FROM   Orders;
```

- When the value is `true`, SQL format:

```sql
SELECT DISTINCT 
       Company
FROM   Orders;
```

#### 7.selectFromclauseStyle

**usage**: If it is AsStacked,from clause will start a new line. If it is AsWrapped, from clause will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
SELECT name,
       salary
FROM   person,
       company,
       employee;
```

- When the value is `AsWrapped`, SQL format:

```sql
SELECT name,
       salary
FROM   person, company, employee;
```

#### 8.selectFromclauseComma

**usage**: This option will change from clause comma position and white space.

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma]

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
SELECT name,
       salary
FROM   person,
       company,
       employee;
```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
SELECT name,
       salary
FROM   person
       , company
       , employee;
```

- When the value is `LfBeforeComma`, SQL format:

```sql
SELECT name,
       salary
FROM   person
       ,company
       ,employee;
```

#### 9.fromClauseInNewLine

**usage**: If it is true, from token will create a new line.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT name,
       salary
FROM   person,
       company,
       employee;
```

- When the value is `true`, SQL format:

```sql
SELECT name,
       salary
FROM  
  person,
  company,
  employee;
```

#### 10.selectFromclauseJoinOnInNewline

**usage**: If it is true, `join` and `on` will be different line.

Type: boolean

Optional values: [true, false]

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers ON Orders. CustomerID = Customers.CustomerID;

```

#### 11.alignJoinWithFromKeyword

**usage**: If it is true, join will vertical align with from token.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `true`, SQL format:

```sql
SELECT     Orders.OrderID,
           Customers.CustomerName,
           Orders.OrderDate
FROM       Orders
INNER JOIN Customers
           ON Orders. CustomerID = Customers.CustomerID;

```

#### 12.andOrUnderWhere

**usage**: if it is true,in the where clause, the 'and' and 'or' keyword will under the 'where'

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT name,
       age
FROM   person
WHERE  age > 30
       AND sex = 'male'
       OR grade = '1'
```

- When the value is `true`, SQL format:

```sql
SELECT name,
       age
FROM   person
WHERE  age > 30
   AND sex = 'male'
    OR grade = '1'
```

#### 13.insertColumnlistStyle

**usage**: If it is AsStacked, every insert column will start a new line. If it is AsWrapped, insert column will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
INSERT INTO company
            (name,
             user_id,
             office)
VALUES        ('John',
               123,
               'Lloyds Office'), ('Billy',
                                  125,
                                  'London Office'), ('Miranda',
                                                     126,
                                                     'Bristol Office');
```

- When the value is `AsWrapped`, SQL format:

```sql
INSERT INTO company
            (name, user_id, office)
VALUES        ('John',
               123,
               'Lloyds Office'), ('Billy',
                                  125,
                                  'London Office'), ('Miranda',
                                                     126,
                                                     'Bristol Office');
```

#### 14.insertValuelistStyle

**usage**: If it is AsStacked, every insert value will start a new line. If it is AsWrapped, insert value will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
INSERT INTO company
            (name,
             user_id,
             office)
VALUES        ('John',
               123,
               'Lloyds Office'), ('Billy',
                                  125,
                                  'London Office'), ('Miranda',
                                                     126,
                                                     'Bristol Office');
```

- When the value is `AsWrapped`, SQL format:

```sql
INSERT INTO company
            (name,
             user_id,
             office)
VALUES        ('John', 123, 'Lloyds Office'), ('Billy', 125, 'London Office'), ('Miranda', 126, 'Bristol Office');
```
