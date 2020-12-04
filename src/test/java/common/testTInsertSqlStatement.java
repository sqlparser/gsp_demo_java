package common;
/*
 * Date: 2010-10-12
 * Time: 14:51:04
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testTInsertSqlStatement extends TestCase {

    public void testLocateValueKeyword(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "INSERT INTO departments\n" +
                "(department_id,department_name,manager_id)\n" +
                "VALUES(300,'Engineering',default);";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getValues().getStartToken().toString().equalsIgnoreCase("("));
        
    }

    public void testLocateInsertKeyword(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "WITH employeetemp (empid, lastname, firstname, phone,\n" +
                "                   address, city, stateprovince,\n" +
                "                   postalcode, currentflag)\n" +
                "AS (SELECT\n" +
                "        e.employeeid, c.lastname, c.firstname, c.phone,\n" +
                "        a.addressline1, a.city, sp.stateprovincecode,\n" +
                "        a.postalcode, e.currentflag\n" +
                "    FROM humanresources.employee e\n" +
                "        INNER JOIN humanresources.employeeaddress AS ea\n" +
                "        ON e.employeeid = ea.employeeid\n" +
                "        INNER JOIN person.address AS a\n" +
                "        ON ea.addressid = a.addressid\n" +
                "        INNER JOIN person.stateprovince AS sp\n" +
                "        ON a.stateprovinceid = sp.stateprovinceid\n" +
                "        INNER JOIN person.contact AS c\n" +
                "        ON e.contactid = c.contactid\n" +
                "    )\n" +
                "INSERT INTO humanresources.newemployee\n" +
                "    SELECT empid, lastname, firstname, phone,\n" +
                "           address, city, stateprovince, postalcode, currentflag\n" +
                "    FROM employeetemp; ";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getInsertToken().toString().equalsIgnoreCase("insert"));
    }

    public void testGetValues(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "INSERT INTO dbo.Departments\n" +
                "    VALUES (1, 'Human Resources', 'Margheim'),(2, 'Sales', 'Byham'),\n" +
                "           (3, 'Finance', 'Gill'),(4, 'Purchasing', 'Barber'),\n" +
                "           (5, 'Manufacturing', 'Brewer'); ";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        //Row Constructors is a new feature for SQL Server 2008,
        // so we introduce TMultiTargetList to support this feature
        
        TMultiTargetList targetList = insert.getValues();
        assertTrue(targetList.size() == 5);

        TMultiTarget target = targetList.getMultiTarget(0);
        TResultColumnList columnList = target.getColumnList();
        assertTrue(columnList.getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(columnList.getResultColumn(1).toString().equalsIgnoreCase("'Human Resources'"));
        assertTrue(columnList.getResultColumn(2).toString().equalsIgnoreCase("'Margheim'"));
               
        TMultiTarget target4 = targetList.getMultiTarget(4);
        TResultColumnList columnList1 = target4.getColumnList();

        assertTrue(columnList1.getResultColumn(0).toString().equalsIgnoreCase("5"));
        assertTrue(columnList1.getResultColumn(1).toString().equalsIgnoreCase("'Manufacturing'"));
        assertTrue(columnList1.getResultColumn(2).toString().equalsIgnoreCase("'Brewer'"));

    }

    public void testTeradata1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "INSERT INTO employee (name, empno, deptno, dob, sex, edlev)\n" +
                "VALUES ('SMITH T', 10021, 700, 460729, 'F', 16);";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TObjectNameList columnList = insert.getColumnList();
        assertTrue(columnList.size() == 6);
        columnList.getObjectName(0).toString().equalsIgnoreCase("name");
        columnList.getObjectName(5).toString().equalsIgnoreCase("edlev");

        TMultiTargetList targetList = insert.getValues();
        assertTrue(targetList.size() == 1);

        TMultiTarget target = targetList.getMultiTarget(0);
        TResultColumnList valueList = target.getColumnList();
        assertTrue(valueList.size() == 6);
        assertTrue(valueList.getResultColumn(0).getExpr().toString().equalsIgnoreCase("'SMITH T'"));
        assertTrue(valueList.getResultColumn(5).toString().equalsIgnoreCase("16"));


    }

    public void testTeradata2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "INSERT INTO employee (10005,'Orebo B',300,,,,'Nov 17 1957',\n" +
                "'M',,,18,);";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TMultiTargetList targetList = insert.getValues();
        assertTrue(targetList.size() == 1);
        TMultiTarget target = targetList.getMultiTarget(0);
        TResultColumnList valueList = target.getColumnList();
        assertTrue(valueList.size() == 12);
        valueList.getResultColumn(0).toString().equalsIgnoreCase("10005");
        assertTrue(valueList.getResultColumn(3).isPlaceHolder());
        valueList.getResultColumn(10).toString().equalsIgnoreCase("18");
        assertTrue(valueList.getResultColumn(11).isPlaceHolder());

        assertTrue(valueList.getEndToken().toString().equalsIgnoreCase("18"));


        sqlparser.sqltext = "INSERT INTO table_1 (column_1, column_2, column_2) VALUES\n" +
                "(,222,223);";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert1 = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TMultiTargetList targetList1 = insert1.getValues();
        assertTrue(targetList1.size() == 1);
        TMultiTarget target1 = targetList1.getMultiTarget(0);
        TResultColumnList valueList1 = target1.getColumnList();
        assertTrue(valueList1.size() == 3);
        assertTrue(valueList1.getResultColumn(0).isPlaceHolder());
        assertTrue(valueList1.getStartToken().toString().equalsIgnoreCase("222"));
        assertTrue(valueList1.getEndToken().toString().equalsIgnoreCase("223"));

    }

    public void testTeradata3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "INSERT INTO table_1 (column_1, column_2, column_2)\n" +
                "VALUES (,221,222);";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TMultiTargetList targetList = insert.getValues();
        assertTrue(targetList.size() == 1);
        TMultiTarget target = targetList.getMultiTarget(0);
        TResultColumnList valueList = target.getColumnList();
        assertTrue(valueList.size() == 3);

        assertTrue(valueList.getResultColumn(0).isPlaceHolder());
        valueList.getResultColumn(1).toString().equalsIgnoreCase("221");
        valueList.getResultColumn(2).toString().equalsIgnoreCase("222");

    }

    public void testTeradata4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "INSERT INTO table_1 (period, cal_dt)\n" +
                "select xxx1, xxx2 from table2;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getColumnList().size() == 2);

    }


    public void testDb2InsertGetQueryEndToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "INSERT INTO T2 (intcol1,identcol2)\n" +
                "SELECT intcol1,identcol2\n" +
                "FROM T1;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getSubQuery().getEndToken().toString().equalsIgnoreCase("T1"));
        assertTrue(insert.getInsertToken().toString().equalsIgnoreCase("insert"));
    }

    public void testTeradataInsert(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
       
        sqlparser.sqltext = "INSERT table1 (x1,y1) SELECT * FROM table_2 UNION SELECT x3,y3 FROM table_3;";

        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        TSelectSqlStatement select = insert.getSubQuery();
         assertTrue(select.getEndToken().toString().equalsIgnoreCase("table_3"));

        sqlparser.sqltext = "INSERT t2 (Cast('15h33m' AS TIME(0) FORMAT 'HHhMIm'));";

        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insert2 = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(insert2.getValues().getStartToken().toString().equalsIgnoreCase("cast"));
        assertTrue(insert2.getValues().getEndToken().toString().equalsIgnoreCase(")"));

        sqlparser.sqltext = "INSERT INTO char1 (c1) SELECT ((d1 (FORMAT 'YYYY/MM/DD'))) FROM date1;";

        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insert3 = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        TSelectSqlStatement select3 = insert3.getSubQuery();
       // System.out.println(select3.getEndToken().toString());
        assertTrue(select3.getEndToken().toString().equalsIgnoreCase("date1"));

    }

    public void testDb2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "INSERT INTO PROJECT (PROJNO, PROJNAME, DEPTNO, RESPEMP, PRSTDATE)\n" +
                "   VALUES('HG0023', 'NEW NETWORK', 'E11', '200280', CURRENT DATE)";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
      //  System.out.println(insert.toString());
//        assertTrue(insert.getSubQuery().getEndToken().toString().equalsIgnoreCase("T1"));
//        assertTrue(insert.getInsertToken().toString().equalsIgnoreCase("insert"));
    }


}
