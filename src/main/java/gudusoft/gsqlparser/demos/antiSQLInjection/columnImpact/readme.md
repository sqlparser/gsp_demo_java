## Description
This tool provides the capability to match source and target columns to extract 
the column-level dataflow (lineage), even if the sources are in a subquery. 

This would be very useful to provide impact analysis feature to ETL mappings in data warehouses and marts.

This tool only process SELECT and create view statement.

Let's take this simple SQL for example:
```sql
SELECT a.deptno "Department", 
       a.num_emp/b.total_count "Employees", 
       a.sal_sum/b.total_sal "Salary"
  FROM
(SELECT deptno, COUNT(*) num_emp, SUM(SAL) sal_sum
    FROM scott.emp
    GROUP BY deptno) a,
(SELECT COUNT(*) total_count, SUM(sal) total_sal
    FROM scott.emp) b
```

Run this tool with default option(/d option is enabled), the full path of dependency will be generated:
```
Search Department <<column_1>>
--> a.deptno
 --> emp.deptno
  --> emp.deptno(group by)
   --> emp.deptno

Search Employees <<column_2>>
--> a.num_emp
--> b.total_count

Search a.num_emp
 --> num_emp(alias)
  --> emp.*
   --> emp.deptno(group by)
    --> emp.deptno
  --> aggregate function COUNT(*)
  --> table scott.emp
   --> emp.deptno(group by)
    --> emp.deptno

Search b.total_count
 --> total_count(alias)
  --> emp.*
  --> aggregate function COUNT(*)
  --> table scott.emp

Search Salary <<column_3>>
--> a.sal_sum
--> b.total_sal

Search a.sal_sum
 --> sal_sum(alias)
  --> emp.SAL
   --> emp.deptno(group by)
    --> emp.deptno
  --> aggregate function SUM(SAL)
  --> table scott.emp
  --> emp.SAL
   --> emp.deptno(group by)
    --> emp.deptno
   --> emp.deptno(group by)
    --> emp.deptno

Search b.total_sal
 --> total_sal(alias)
  --> emp.sal
  --> aggregate function SUM(sal)
  --> table scott.emp
  --> emp.sal
```


## Usage
`java ColumnImpact scriptfile [/d]|[/s [/xml] [/c]]|[/v] [/o <output file path>] [/t <database type>]`

#### /s, only list the final result, omit all the intermediate process. 
```
Department depends on: scott.emp.deptno
Employees depends on: scott.emp(total count of record influences the result value), 
                      scott.emp.deptno(because it is in group by clause)
Salary depends on: scott.emp.sal, scott.emp.deptno
```

  - /xml option is valid only used together with /s option.
  - /c option is valid only used together with /s option, list source column in the select list only.
	```
	Department depends on: scott.emp.deptno
    Employees depends on: scott.emp
    Salary depends on: scott.emp.sal
	```


#### /v, this option works only when the input SQL is create view statement. 

## Online live demo
Please try your own SQL with [this online live demo](http://www.sqlparser.com/livedemo_redirect.php?demo_id=columnImpact&utm_source=github-gsp-demo&utm_medium=text-general)

## Related projects
If you need to handle more complicated SQL and get all dataflow information of table and columns,
please check this demo [Dataflow Analyzer](../dlineage#dataflowanalyzer).

## Changes
-  [2012-01-11, first version](https://github.com/sqlparser/wings/issues/1)