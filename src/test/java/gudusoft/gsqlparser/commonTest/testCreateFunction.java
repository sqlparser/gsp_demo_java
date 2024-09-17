package gudusoft.gsqlparser.commonTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlReturn;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.EFunctionReturnsType.frtInlineTableValue;

public class testCreateFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsparksql);
        sqlparser.sqltext = "CREATE FUNCTION simple_udf AS 'SimpleUdf'\n" +
                "    USING JAR '/tmp/SimpleUdf.jar';";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt stmt = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getFunctionName().toString().equalsIgnoreCase("simple_udf"));
        assertTrue(stmt.getClassName().equalsIgnoreCase("'SimpleUdf'"));
        assertTrue(stmt.getResourceType().equalsIgnoreCase("JAR"));
        assertTrue(stmt.getResourceURI().equalsIgnoreCase("'/tmp/SimpleUdf.jar'"));
    }

    public void testReturnTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE FUNCTION udfProductInYear (\n" +
                "@model_year INT\n" +
                ")\n" +
                "RETURNS TABLE\n" +
                "AS\n" +
                "RETURN\n" +
                "SELECT \n" +
                "product_name,\n" +
                "model_year,\n" +
                "list_price\n" +
                "FROM\n" +
                "production.products\n" +
                "WHERE\n" +
                "model_year = @model_year;";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt stmt = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getFunctionName().toString().equalsIgnoreCase("udfProductInYear"));
        assertTrue(stmt.getReturnsType() == frtInlineTableValue);
        if (stmt.getReturnsType() == frtInlineTableValue){
            TMssqlReturn mssqlReturn = stmt.getReturnStmt();
            TSelectSqlStatement subquery = mssqlReturn.getSubquery();
            if (subquery != null){
                assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("production.products"));
            }
        }
    }

}
