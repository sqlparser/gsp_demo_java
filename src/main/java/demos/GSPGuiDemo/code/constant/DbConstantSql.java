package com.gudusoft.format.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * CemB
 */
public interface   DbConstantSql {

    String MySQLSampleSQL = "SELECT\n" +
            "  salesperson.name,\n" +
            "  -- find maximum sale size for this salesperson\n" +
            "  (SELECT MAX(amount) AS amount\n" +
            "    FROM all_sales\n" +
            "    WHERE all_sales.salesperson_id = salesperson.id)\n" +
            "  AS amount,\n" +
            "  -- find customer for this maximum size\n" +
            "  (SELECT customer_name\n" +
            "    FROM all_sales\n" +
            "    WHERE all_sales.salesperson_id = salesperson.id\n" +
            "    AND all_sales.amount =\n" +
            "         -- find maximum size, again\n" +
            "         (SELECT MAX(amount) AS amount\n" +
            "           FROM all_sales\n" +
            "           WHERE all_sales.salesperson_id = salesperson.id))\n" +
            "  AS customer_name\n" +
            "FROM\n" +
            "  salesperson;";
    String OracleSampleSQL = "SELECT o.order_id oid, o.customer_id cid, o.order_total ottl,\n" +
            "o.sales_rep_id sid, c.credit_limit cl, c.cust_email cem\n" +
            "FROM orders o, customers c\n" +
            "WHERE o.customer_id = c.customer_id;";
    String DB2SampleSQL= "SELECT D.DEPTNO, D.DEPTNAME,\n" +
            "EMPINFO.AVGSAL, EMPINFO.EMPCOUNT\n" +
            "FROM DEPT D,\n" +
            "TABLE (SELECT AVG(E.SALARY) AS AVGSAL,\n" +
            "COUNT(*) AS EMPCOUNT\n" +
            "FROM EMP E\n" +
            "WHERE E.WORKDEPT = D.DEPTNO)\n" +
            "AS EMPINFO;";
    String PostgreSQLSampleSQL = "create view v2 as \n" +
            "SELECT distributors.name\n" +
            "FROM distributors\n" +
            "WHERE distributors.name LIKE 'W%'\n" +
            "UNION\n" +
            "SELECT actors.name\n" +
            "FROM actors\n" +
            "WHERE actors.name LIKE 'W%';";
    String SQLServerSampleSQL = "GO  \n" +
            "CREATE PROCEDURE dbo.uspGetEmployeeSales   \n" +
            "AS   \n" +
            "    SET NOCOUNT ON;  \n" +
            "    SELECT 'PROCEDURE', sp.BusinessEntityID, c.LastName,   \n" +
            "        sp.SalesYTD   \n" +
            "    FROM Sales.SalesPerson AS sp    \n" +
            "    INNER JOIN Person.Person AS c  \n" +
            "        ON sp.BusinessEntityID = c.BusinessEntityID  \n" +
            "    WHERE sp.BusinessEntityID LIKE '2%'  \n" +
            "    ORDER BY sp.BusinessEntityID, c.LastName; ";
    String SnowflakeSampleSQL = "insert into employees(first_name, last_name, workphone, city,postal_code)\n" +
            "  select\n" +
            "    contractor_first,contractor_last,worknum,null,zip_code\n" +
            "  from contractors\n" +
            "  where contains(worknum,'650');";
    String CouchbaseSampleSQL = "SELECT t1.country, array_agg(t1.city), sum(t1.city_cnt) as apnum\n" +
            "FROM (SELECT city, city_cnt, array_agg(airportname) as apnames, country\n" +
            "      FROM `travel-sample` WHERE type = \"airport\"\n" +
            "      GROUP BY city, country LETTING city_cnt = count(city) ) AS t1\n" +
            "WHERE t1.city_cnt > 5\n" +
            "GROUP BY t1.country;";
    String DaxSampleSQL = "SELECT * \n" +
            "FROM DimProduct\n" +
            "WHERE Color = 'Red' AND ListPrice > 1000";
    String GreenplumSampleSQL = "SELECT region,\n" +
            "    product,\n" +
            "    SUM(quantity) AS product_units,\n" +
            "    SUM(amount) AS product_sales\n" +
            "FROM orders\n" +
            "WHERE region IN (SELECT region FROM top_regions)\n" +
            "GROUP BY region, product;";
    String HanaSampleSQL = "MERGE INTO \"my_schema\".t1 USING \"my_schema\".t2 ON \"my_schema\".t1.a = \"my_schema\".t2.a\n" +
            " WHEN MATCHED THEN UPDATE SET \"my_schema\".t1.b = \"my_schema\".t2.b\n" +
            " WHEN NOT MATCHED THEN INSERT VALUES(\"my_schema\".t2.a, \"my_schema\".t2.b);\n" +
            " \n" +
            " MERGE INTO T1 USING T2 ON T1.A = T2.A\n" +
            " WHEN MATCHED AND T1.A > 1 THEN UPDATE SET B = T2.B\n" +
            " WHEN NOT MATCHED AND T2.A > 3 THEN INSERT VALUES (T2.A, T2.B);";
    String HiveSampleSQL = "SELECT a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key2);";
    String ImpalaSampleSQL = "CREATE VIEW v7 (c1 COMMENT 'Comment for c1', c2) COMMENT 'Comment for v7' AS SELECT t1.c1, t1.c2 FROM t1;";
    String InformixSampleSQL = "INSERT INTO t2(f1, f2)\n" +
            "SELECT t1.f1, t1.f2 FROM t1\n" +
            "WHERE NOT EXISTS\n" +
            "(SELECT f1, f2 FROM t2\n" +
            "WHERE t2.f1 = t1.f1);";
    String MdxSampleSQL = "select\n" +
            "{\n" +
            "\t([Measures].[Sales Amount])\n" +
            "}on columns,\n" +
            "non empty{\n" +
            "order(\n" +
            "{\n" +
            "\t([Dim Product].[Product Line].members-[Dim Product].[Product Line].[All])\n" +
            "},\n" +
            "([Measures].[Sales Amount]),\n" +
            "desc\n" +
            ")}on rows\n" +
            "from [Adventure Works DW2008R2]";
    String NetezzaSampleSQL = "SELECT *\n" +
            "FROM employee JOIN manager ON emp_mgr = mgr_id \n" +
            "\tJOIN mgr_cnt ON emp_mgr = mgr_id \n" +
            "WHERE emp_id != mgr_id\n" +
            "ORDER BY mgr_dept;";
    String OpenedgeSampleSQL = "Update Orderline\n" +
            "SET (Itemnum) =\n" +
            "(Select Itemnum\n" +
            "FROM Item\n" +
            "WHERE Itemname = 'Tennis balls')\n" +
            "WHERE Ordernum = 20;";
    String RedshiftSampleSQL = "SELECT venuename, \n" +
            "       venuecity, \n" +
            "       venuestate, \n" +
            "       Sum(qtysold)   AS venue_qty, \n" +
            "       Sum(pricepaid) AS venue_sales \n" +
            "FROM   sales, \n" +
            "       venue, \n" +
            "       event \n" +
            "WHERE  venue.venueid = event.venueid \n" +
            "       AND event.eventid = sales.eventid \n" +
            "       AND venuename IN(SELECT venuename \n" +
            "                        FROM   top_venues) \n" +
            "GROUP  BY venuename, \n" +
            "          venuecity, \n" +
            "          venuestate \n" +
            "ORDER  BY venuename;";
    String SybaseSampleSQL = "insert newpublishers (pub_id, pub_name)\n" +
            "select pub_id, pub_name\n" +
            "from publishers\n" +
            "where pub_name=\"New Age Data\"";
    String TeradataSampleSQL = "INSERT INTO promotion (deptnum, empname, yearsexp)\n" +
            "SELECT deptno, name, yrsexp\n" +
            "FROM employee\n" +
            "WHERE yrsexp > 10 ;";
    String VerticaSampleSQL = "CREATE VIEW myview AS\n" +
            "SELECT SUM(annual_income), customer_state\n" +
            "FROM public.customer_dimension\n" +
            "WHERE customer_key IN\n" +
            "(SELECT customer_key\n" +
            "FROM store.store_sales_fact)\n" +
            "GROUP BY customer_state\n" +
            "ORDER BY customer_state ASC;";

   Map<String,String> sampleSql=new HashMap<String,String>(){
       {
           put(DbConstant.Couchbase,CouchbaseSampleSQL);
           put(DbConstant.Dax,DaxSampleSQL);
           put(DbConstant.DB2,DB2SampleSQL);
           put(DbConstant.Greenplum,GreenplumSampleSQL);
           put(DbConstant.Hana,HanaSampleSQL);
           put(DbConstant.Hive,HiveSampleSQL);
           put(DbConstant.Impala,ImpalaSampleSQL);
           put(DbConstant.Informix,InformixSampleSQL);
           put(DbConstant.Mdx,MdxSampleSQL);
           put(DbConstant.MySQL,MySQLSampleSQL);
           put(DbConstant.Netezza,NetezzaSampleSQL);
           put(DbConstant.Openedge, OpenedgeSampleSQL);
           put(DbConstant.Oracle, OracleSampleSQL);
           put(DbConstant.PostgreSQL, PostgreSQLSampleSQL);
           put(DbConstant.Redshift, RedshiftSampleSQL);
           put(DbConstant.Snowflake, SnowflakeSampleSQL);
           put(DbConstant.SQLServer, SQLServerSampleSQL);
           put(DbConstant.Sybase, SybaseSampleSQL);
           put(DbConstant.Teradata, TeradataSampleSQL);
           put(DbConstant.Vertica, VerticaSampleSQL);

       }
   };



}
