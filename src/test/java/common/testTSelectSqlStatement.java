package common;
/*
 * Date: 2010-10-9
 * Time: 10:04:35
 */

import gudusoft.gsqlparser.EExpressionType;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testTSelectSqlStatement extends TestCase {

    public void testDistinct(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT distinct t.f1, f2 from t where t.f3 = f4";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getSelectDistinct().getDistinctType() == TBaseType.dtDistinct);
    }


    public void testDb2StartToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "WITH\n" +
                " DEPT_MGR AS\n" +
                "  ( SELECT DEPTNO, DEPTNAME, EMPNO, LASTNAME, FIRSTNME, PHONENO\n" +
                "     FROM DEPARTMENT D, EMPLOYEE E\n" +
                "      WHERE D.MGRNO=E.EMPNO AND E.JOB='MANAGER'\n" +
                "  ),\n" +
                "\n" +
                " DEPT_NO_MGR AS\n" +
                "  ( SELECT DEPTNO, DEPTNAME, MGRNO AS EMPNO\n" +
                "      FROM DEPARTMENT\n" +
                "   EXCEPT ALL\n" +
                "    SELECT DEPTNO, DEPTNAME, EMPNO\n" +
                "      FROM DEPT_MGR\n" +
                "  ),\n" +
                "\n" +
                " MGR_NO_DEPT (DEPTNO, EMPNO, LASTNAME, FIRSTNME, PHONENO) AS\n" +
                "  ( SELECT WORKDEPT, EMPNO, LASTNAME, FIRSTNME, PHONENO\n" +
                "      FROM EMPLOYEE\n" +
                "       WHERE JOB='MANAGER'\n" +
                "   EXCEPT ALL\n" +
                "    SELECT DEPTNO,EMPNO, LASTNAME, FIRSTNME, PHONENO\n" +
                "      FROM DEPT_MGR\n" +
                "  )\n" +
                "\n" +
                "SELECT DEPTNO, DEPTNAME, EMPNO, LASTNAME, FIRSTNME, PHONENO\n" +
                "  FROM DEPT_MGR\n" +
                "UNION ALL\n" +
                "SELECT DEPTNO, DEPTNAME, EMPNO,\n" +
                "       CAST(NULL AS VARCHAR(15)) AS LASTNAME,\n" +
                "       CAST(NULL AS VARCHAR(12)) AS FIRSTNME,\n" +
                "       CAST(NULL AS CHAR(4)) AS PHONENO\n" +
                "  FROM DEPT_NO_MGR\n" +
                "UNION ALL\n" +
                "\n" +
                "SELECT DEPTNO,\n" +
                "       CAST(NULL AS VARCHAR(29)) AS DEPTNAME,\n" +
                "       EMPNO, LASTNAME, FIRSTNME, PHONENO\n" +
                "  FROM MGR_NO_DEPT\n" +
                "ORDER BY 4;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement leftselect = select.getLeftStmt();
        TSelectSqlStatement rightselect = select.getRightStmt();
        assertTrue(leftselect.getStartToken().toString().equalsIgnoreCase("select"));
        assertTrue(rightselect.getEndToken().toString().equalsIgnoreCase("MGR_NO_DEPT"));
        assertTrue(select.getOrderbyClause().toString().equalsIgnoreCase("ORDER BY 4"));

    }

    public void testDb2ValuesStartToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "SELECT deptnumb, deptname FROM   org WHERE  deptnumb < 20 UNION VALUES(7, 'New Deptname');";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement leftselect = select.getLeftStmt();
        TSelectSqlStatement rightselect = select.getRightStmt();
        assertTrue(leftselect.getStartToken().toString().equalsIgnoreCase("select"));
        assertTrue(rightselect.getStartToken().toString().equalsIgnoreCase("VALUES"));
        assertTrue(rightselect.getEndToken().toString().equalsIgnoreCase(")"));
    }

    public void testTeradataQuery(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "WITH RECURSIVE dt(a,b,c,d) AS\n" +
                "(SELECT a1, b1,a1-b1,0\n" +
                "FROM t1\n" +
                "UNION ALL\n" +
                "SELECT addend1, addend2, mysum,d+1\n" +
                "FROM dt,table (add2int(dt.a,dt.b)) AS tf\n" +
                "WHERE d < 2)\n" +
                "SELECT *\n" +
                "FROM dt;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(select.getStartToken().toString().equalsIgnoreCase("with"));
        assertTrue(select.getSelectToken().toString().equalsIgnoreCase("select"));

        // System.out.println(select.toString());

        TCTEList cteList = select.getCteList();
        TCTE cte = cteList.getCTE(0);
        TSelectSqlStatement subQuery = cte.getSubquery();
        TSelectSqlStatement leftselect = subQuery.getLeftStmt();
        TSelectSqlStatement rightselect = subQuery.getRightStmt();

        assertTrue(leftselect.getEndToken().toString().equalsIgnoreCase("t1"));
        assertTrue(rightselect.getEndToken().toString().equalsIgnoreCase("2"));

    }

    public void testTeradataJoins(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT skills.skillname, employee.empno FROM skills LEFT OUTER JOIN employee ON skills.skillno = employee.skillno;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.joins.getStartToken().toString().equalsIgnoreCase("skills"));
        
    }

    public void testTeradataEndToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT x1 FROM   table_1 WHERE  (x1,y1) IN (SELECT * FROM table_2 UNION SELECT * FROM table_3);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause where = select.getWhereClause();
        TExpression expr = where.getCondition();
        assertTrue(expr.getExpressionType() == EExpressionType.in_t);

        TExpression  inExpr = expr.getRightOperand();
        TSelectSqlStatement subquery = inExpr.getSubQuery();

        TSelectSqlStatement leftstmt = subquery.getLeftStmt();
        TSelectSqlStatement rightstmt = subquery.getRightStmt();

        assertTrue(leftstmt.getEndToken().toString().equalsIgnoreCase("table_2"));
        assertTrue(rightstmt.getEndToken().toString().equalsIgnoreCase("table_3"));

    }



}
