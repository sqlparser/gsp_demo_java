package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;


public class testSubqueryFunction extends TestCase {

    public void test( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvsnowflake );
        sqlparser.sqltext = "SELECT a.deptno \"Department\",\n"
                + "a.num_emp/b.total_count \"Employees\",\n"
                + "a.sal_sum/b.total_sal \"Salary\"\n"
                + "FROM\n"
                + "(SELECT deptno, COUNT(*) num_emp, SUM(SAL) sal_sum\n"
                + "FROM scott.emp\n"
                + "GROUP BY deptno) a,\n"
                + "(SELECT COUNT(*) total_count, SUM(sal) total_sal\n"
                + "FROM scott.emp) b";
        assertTrue( sqlparser.parse( ) == 0 );

        TCustomSqlStatement setStmt = (TCustomSqlStatement) sqlparser.sqlstatements
                .get( 0 );
//        assertTrue( setStmt.tables.getTable( 0 )
//                .getSubquery( )
//                .getResultColumnList( )
//                .getResultColumn( 1 )
//                .getExpr( )
//                .toString( ) == null );
//
//        assertTrue( setStmt.tables.getTable( 0 )
//                .getSubquery( )
//                .getResultColumnList( )
//                .getResultColumn( 1 )
//                .getExpr( )
//                .getFunctionCall( ).toString( ) == null );

        assertTrue(setStmt.tables.getTable(0)
                .getSubquery()
                .getResultColumnList()
                .getResultColumn(1).getExpr().toString().equalsIgnoreCase("COUNT(*)"));
    }


}