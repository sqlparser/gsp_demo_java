package dynamicsql;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.mssql.*;
import junit.framework.TestCase;

public class testSQLServerExec  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROCEDURE [dbo].[usp_SearchProducts]  \n" +
                "(\n" +
                "\t  @ProductID\t\t\tNVARCHAR(50) = NULL\t\n" +
                "\t ,@Name\t\t\t\t\tNVARCHAR(100) = NULL\t\n" +
                "\t ,@ProductNumber        NVARCHAR(100) = NULL\t\n" +
                "\t ,@Color\t\t\t\tNVARCHAR(100) = NULL\t\n" +
                "\t \n" +
                "\t\n" +
                ")\n" +
                "AS          \n" +
                "BEGIN      \n" +
                "\tSET NOCOUNT ON;  \n" +
                " \n" +
                "\tDECLARE @SQL\t\t\t\t\t\t\tVARCHAR(MAX)\n" +
                "\tDECLARE @ProductIDFilter\t\t\t\tVARCHAR(MAX)\n" +
                "\tDECLARE @NameFilter\t\t\t\t\t\tVARCHAR(MAX)\n" +
                "\tDECLARE @ProductNumberFilter\t\t\tVARCHAR(MAX)\n" +
                "\tDECLARE @ColorFilter\t\t\t\t\tVARCHAR(MAX)\n" +
                "\tDECLARE @all                            VARCHAR(2)   = '-1'\n" +
                "\t\n" +
                " \n" +
                "\tSET @ProductIDFilter = CASE WHEN @ProductID IS NULL OR @ProductID = 0 \n" +
                "\tTHEN '''' + @all + ''' = ''' + @all + '''' \n" +
                "\tELSE 'ProductID = ''' +  @ProductID + '''' \n" +
                "\tEND\n" +
                " \n" +
                "\tSET @NameFilter = CASE WHEN @Name IS NULL OR @Name = ''\n" +
                "\tTHEN '''' + @all + ''' = ''' + @all + '''' \n" +
                "\tELSE 'Name like ''%' + @Name + '%''' \n" +
                "\tEND\n" +
                " \n" +
                "\tSET @ProductNumberFilter = CASE WHEN @ProductNumber IS NULL OR @ProductNumber = ''\n" +
                "\tTHEN '''' + @all + ''' = ''' + @all + '''' \n" +
                "\tELSE 'ProductNumber like ''%' + @ProductNumber + '%''' \n" +
                "\tEND\n" +
                " \n" +
                "\tSET @ColorFilter = CASE WHEN @Color IS NULL OR @Color = ''\n" +
                "\tTHEN '''' + @all + ''' = ''' + @all + '''' \n" +
                "\tELSE 'Color like ''' + @Color + '''' \n" +
                "\tEND\n" +
                " \n" +
                "\t\n" +
                " \n" +
                "\t\t  SET @SQL = 'SELECT ProductID\n" +
                "\t\t\t\t\t\t,Name\n" +
                "\t\t\t\t\t\t,ProductNumber\n" +
                "\t\t\t\t\t\t,Color\n" +
                "\t\t\t\t\t\t,StandardCost\n" +
                "\t\t\t\t\t\t,Size\n" +
                "\t\t\t\t\t\t,Weight\n" +
                "\t\t\t\t\tFROM SalesLT.Product\n" +
                "\t\t\tWHERE ' + @ProductIDFilter\n" +
                "\t\t\t+ ' AND ' + @NameFilter + ''\n" +
                "\t\t\t+ ' AND ' + @ProductNumberFilter + ''\n" +
                "\t\t\t+ ' AND ' + @ColorFilter + ''\n" +
                "\t\t\t\n" +
                " \n" +
                "\t\t\tPRINT (@sql)\n" +
                "\t\t\tEXEC(@sql)\n" +
                "\t\t\t\n" +
                " \n" +
                "END";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);

        getSQLServerExecSQLTextVisitor nodeVisitor = new getSQLServerExecSQLTextVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(nodeVisitor);
        assertTrue(nodeVisitor.sqlText.equalsIgnoreCase("SELECT ProductID\n" +
                "\t\t\t\t\t\t,Name\n" +
                "\t\t\t\t\t\t,ProductNumber\n" +
                "\t\t\t\t\t\t,Color\n" +
                "\t\t\t\t\t\t,StandardCost\n" +
                "\t\t\t\t\t\t,Size\n" +
                "\t\t\t\t\t\t,Weight\n" +
                "\t\t\t\t\tFROM SalesLT.Product\n" +
                "\t\t\tWHERE '-1' = '-1' AND '-1' = '-1' AND '-1' = '-1' AND '-1' = '-1'"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DECLARE @SQL_QUERY NVARCHAR(128)\n" +
                "SET @SQL_QUERY = N'SELECT id, name, price FROM Books WHERE price > 4000'\n" +
                "EXECUTE sp_executesql @SQL_QUERY";
        assertTrue(sqlparser.parse() == 0);

        getSQLServerExecSQLTextVisitor nodeVisitor = new getSQLServerExecSQLTextVisitor();
        sqlparser.sqlstatements.acceptChildren(nodeVisitor);
        assertTrue(nodeVisitor.sqlText.equalsIgnoreCase("SELECT id, name, price FROM Books WHERE price > 4000"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROCEDURE testschema.TestProcWithResultSet\n" +
                " AS\n" +
                " begin\n" +
                " \n" +
                " DECLARE @SQL varchar(8000),@interval INT,@handle UNIQUEIDENTIFIER = NULL,@message_type_name SYSNAME\n" +
                " SET @SQL = 'SELECT deptno , [Department Name] from TestCatalog.TestSchema.TestTableDept ttd'\n" +
                " EXEC (@SQL)\n" +
                " WITH RESULT SETS\n" +
                " (\n" +
                "      ( deptno INT, DepartmentName VARCHAR(150))\n" +
                "    )\n" +
                "    \n" +
                "     SET @handle = 678\n" +
                "    \n" +
                "   BEGIN TRANSACTION\n" +
                "    BEGIN CONVERSATION TIMER (@handle) TIMEOUT = @interval;\n" +
                "    COMMIT TRANSACTION;\n" +
                " end;";
        assertTrue(sqlparser.parse() == 0);

        getSQLServerExecSQLTextVisitor nodeVisitor = new getSQLServerExecSQLTextVisitor();
        sqlparser.sqlstatements.acceptChildren(nodeVisitor);
        //System.out.println(nodeVisitor.sqlText);
       assertTrue(nodeVisitor.sqlText.equalsIgnoreCase("SELECT deptno , [Department Name] from TestCatalog.TestSchema.TestTableDept ttd"));
    }

}

class getSQLServerExecSQLTextVisitor extends TParseTreeVisitor {
    public String sqlText;

    public void preVisit(TMssqlExecute node) {
        sqlText = node.getSqlText();
    }
}
