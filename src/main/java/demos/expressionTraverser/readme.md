## Description
Iterates the SQL expression using the built-in visitor in pre-order/in-order/post-order to extract table/column, operator, constant and value used in the expression.



```sql
SELECT * FROM Customers
WHERE Country='Germany' AND City='Berlin';
```

the output resulted in pre-order/in-order/post-order for where condition
```
pre order
class gudusoft.gsqlparser.nodes.TExpression Country='Germany' AND City='Berlin'
class gudusoft.gsqlparser.nodes.TExpression Country='Germany'
*class gudusoft.gsqlparser.nodes.TExpression Country
*class gudusoft.gsqlparser.nodes.TExpression 'Germany'
class gudusoft.gsqlparser.nodes.TExpression City='Berlin'
*class gudusoft.gsqlparser.nodes.TExpression City
*class gudusoft.gsqlparser.nodes.TExpression 'Berlin'

in order
*class gudusoft.gsqlparser.nodes.TExpression Country
class gudusoft.gsqlparser.nodes.TExpression Country='Germany'
*class gudusoft.gsqlparser.nodes.TExpression 'Germany'
class gudusoft.gsqlparser.nodes.TExpression Country='Germany' AND City='Berlin'
*class gudusoft.gsqlparser.nodes.TExpression City
class gudusoft.gsqlparser.nodes.TExpression City='Berlin'
*class gudusoft.gsqlparser.nodes.TExpression 'Berlin'

post order
*class gudusoft.gsqlparser.nodes.TExpression Country
*class gudusoft.gsqlparser.nodes.TExpression 'Germany'
class gudusoft.gsqlparser.nodes.TExpression Country='Germany'
*class gudusoft.gsqlparser.nodes.TExpression City
*class gudusoft.gsqlparser.nodes.TExpression 'Berlin'
class gudusoft.gsqlparser.nodes.TExpression City='Berlin'
class gudusoft.gsqlparser.nodes.TExpression Country='Germany' AND City='Berlin'
```

## Usage
`java expressionTraverser`


