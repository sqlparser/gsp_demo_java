package common;
/*
 * Date: 11-1-26
 * Time: ����10:49
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParameterDeclarationList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateFunction;
import gudusoft.gsqlparser.stmt.mssql.TMssqlReturn;
import junit.framework.TestCase;

public class testTMssqlCreateFunction extends TestCase {

    public void testParameters(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE FUNCTION Sales.fn_SalesByStore (@storeid int)\n" +
                "RETURNS TABLE\n" +
                "AS\n" +
                "RETURN \n" +
                "(\n" +
                "    SELECT P.ProductID, P.Name, SUM(SD.LineTotal) AS 'YTD Total'\n" +
                "    FROM Production.Product AS P \n" +
                "      JOIN Sales.SalesOrderDetail AS SD ON SD.ProductID = P.ProductID\n" +
                "      JOIN Sales.SalesOrderHeader AS SH ON SH.SalesOrderID = SD.SalesOrderID\n" +
                "    WHERE SH.CustomerID = @storeid\n" +
                "    GROUP BY P.ProductID, P.Name\n" +
                "); ";

        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateFunction createFunction = (TMssqlCreateFunction)sqlparser.sqlstatements.get(0);
        TParameterDeclarationList parameters  = createFunction.getParameterDeclarations();
        assertTrue(parameters.getStartToken().toString().equalsIgnoreCase("@storeid"));
        assertTrue(parameters.getEndToken().toString().equalsIgnoreCase("int"));

    }

    public void testReturnStmt(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE FUNCTION Sales.fn_SalesByStore (@storeid int)\n" +
                "RETURNS TABLE\n" +
                "AS\n" +
                "RETURN \n" +
                "(\n" +
                "    SELECT P.ProductID, P.Name, SUM(SD.LineTotal) AS 'YTD Total'\n" +
                "    FROM Production.Product AS P \n" +
                "      JOIN Sales.SalesOrderDetail AS SD ON SD.ProductID = P.ProductID\n" +
                "      JOIN Sales.SalesOrderHeader AS SH ON SH.SalesOrderID = SD.SalesOrderID\n" +
                "    WHERE SH.CustomerID = @storeid\n" +
                "    GROUP BY P.ProductID, P.Name\n" +
                "); ";

        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateFunction createFunction = (TMssqlCreateFunction)sqlparser.sqlstatements.get(0);

        assertTrue(createFunction.getBodyStatements().size() == 1);

        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.getStartToken().toString().equalsIgnoreCase("return"));
        assertTrue(stmt.getEndToken().toString().equalsIgnoreCase(")"));

        TExpression expr = ((TMssqlReturn)stmt).getReturnExpr();
        assertTrue(expr.getStartToken().toString().equalsIgnoreCase("("));
        assertTrue(expr.getEndToken().toString().equalsIgnoreCase(")"));

        TSelectSqlStatement select = expr.getSubQuery();
        assertTrue(select.getStartToken().toString().equalsIgnoreCase("("));
        assertTrue(select.getEndToken().toString().equalsIgnoreCase(")"));
        assertTrue(select.getSelectToken().toString().equalsIgnoreCase("select"));

    }

    public void testBlockStmt(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE FUNCTION Inffkfrom(@tbID  INT,\n" +
                "                          @colID SMALLINT)\n" +
                "returns VARCHAR(2000)\n" +
                "AS\n" +
                "  BEGIN\n" +
                "     DECLARE @r VARCHAR(2000),\n" +
                "             @a VARCHAR(200)\n" +
                "\n" +
                "     SELECT @r = '',\n" +
                "            @a = ''\n" +
                "\n" +
                "     DECLARE cs CURSOR FOR\n" +
                "       SELECT fkfrom=CONVERT(VARCHAR(200), Object_name(rkeyid)+'.'+r.[name])\n" +
                "       FROM   sysforeignkeys c\n" +
                "              JOIN syscolumns f ON c.fkeyid = f.[id]\n" +
                "                                   AND c.fkey = f.colid\n" +
                "              JOIN syscolumns r ON c.rkeyid = r.[id]\n" +
                "                                   AND c.rkey = r.colid\n" +
                "       WHERE\n" +
                "         fkeyid = @tbID\n" +
                "         AND fkey = @colID\n" +
                "       ORDER  BY\n" +
                "         keyno\n" +
                "\n" +
                "     OPEN cs\n" +
                "\n" +
                "     FETCH next FROM cs INTO @a\n" +
                "\n" +
                "     WHILE @@FETCH_STATUS = 0\n" +
                "       BEGIN\n" +
                "          SELECT @r = @r + CASE\n" +
                "                             WHEN Len(@r) > 0 THEN ', '\n" +
                "                             ELSE ''\n" +
                "                           END + @a\n" +
                "\n" +
                "          FETCH next FROM cs INTO @a\n" +
                "       END\n" +
                "\n" +
                "     CLOSE cs\n" +
                "\n" +
                "     DEALLOCATE cs\n" +
                "\n" +
                "     RETURN( @r )\n" +
                "  END \n" +
                "";

        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateFunction createFunction = (TMssqlCreateFunction)sqlparser.sqlstatements.get(0);

        assertTrue(createFunction.getBodyStatements().size() == 1);

        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.getStartToken().toString().equalsIgnoreCase("begin"));
        assertTrue(stmt.getEndToken().toString().equalsIgnoreCase("end"));

        TMssqlBlock block = (TMssqlBlock)createFunction.getBodyStatements().get(0);

        assertTrue(block.getBodyStatements().size() == 9);

        TCustomSqlStatement stmt1 = block.getBodyStatements().get(0);

        assertTrue(stmt1.getStartToken().toString().equalsIgnoreCase("DECLARE"));
        assertTrue(stmt1.getEndToken().toString().equalsIgnoreCase(")"));

        TCustomSqlStatement stmt9 = block.getBodyStatements().get(8);
        assertTrue(stmt9.getStartToken().toString().equalsIgnoreCase("RETURN"));
        assertTrue(stmt9.getEndToken().toString().equalsIgnoreCase(")"));

    }

    public void testBlockStmt2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "CREATE FUNCTION dbo.isoweek (@DATE datetime)\n" +
                "RETURNS INT\n" +
                "WITH EXECUTE AS caller\n" +
                "AS\n" +
                "BEGIN\n" +
                "     DECLARE @ISOweek INT\n" +
                "     \n" +
                "     \n" +
                "     SET @ISOweek= datepart(wk,@DATE)+1\n" +
                "          -datepart(wk,CAST(datepart(yy,@DATE) AS CHAR(4))+'0104')\n" +
                "          \n" +
                "--Special cases: Jan 1-3 may belong to the previous year\n" +
                "     IF (@ISOweek=0)\n" +
                "          SET @ISOweek=dbo.isoweek(CAST(datepart(yy,@DATE)-1\n" +
                "               AS CHAR(4))+'12'+ CAST(24+datepart(DAY,@DATE) AS CHAR(2)))+1\n" +
                "--Special case: Dec 29-31 may belong to the next year\n" +
                "     IF ((datepart(mm,@DATE)=12) AND\n" +
                "          ((datepart(dd,@DATE)-datepart(dw,@DATE))>= 28))\n" +
                "          SET @ISOweek=1\n" +
                "          \n" +
                "          \n" +
                "          \n" +
                "          \n" +
                "     RETURN(@ISOweek)\n" +
                "END;\n" +
                "GO ";

        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateFunction createFunction = (TMssqlCreateFunction)sqlparser.sqlstatements.get(0);

        assertTrue(createFunction.getBodyStatements().size() == 1);
        TCustomSqlStatement stmt = createFunction.getBodyStatements().get(0);
        assertTrue(stmt.getStartToken().toString().equalsIgnoreCase("begin"));
        assertTrue(stmt.getEndToken().toString().equalsIgnoreCase("end"));

        TMssqlBlock block = (TMssqlBlock)createFunction.getBodyStatements().get(0);
        assertTrue(block.getBodyStatements().size() == 5);


        TCustomSqlStatement stmt0 = block.getBodyStatements().get(0);
        assertTrue(stmt0.getStartToken().toString().equalsIgnoreCase("DECLARE"));
        assertTrue(stmt0.getEndToken().toString().equalsIgnoreCase("int"));

        TCustomSqlStatement stmt1 = block.getBodyStatements().get(1);
        assertTrue(stmt1.getStartToken().toString().equalsIgnoreCase("set"));
        assertTrue(stmt1.getEndToken().toString().equalsIgnoreCase(")"));

        TCustomSqlStatement stmt2 = block.getBodyStatements().get(2);
        assertTrue(stmt2.getStartToken().toString().equalsIgnoreCase("if"));
        assertTrue(stmt2.getEndToken().toString().equalsIgnoreCase("1"));
       // System.out.println(stmt2.getEndToken().toString());

        TCustomSqlStatement stmt3 = block.getBodyStatements().get(3);
        assertTrue(stmt3.getStartToken().toString().equalsIgnoreCase("if"));
        assertTrue(stmt3.getEndToken().toString().equalsIgnoreCase("1"));

        TCustomSqlStatement stmt4 = block.getBodyStatements().get(4);
        assertTrue(stmt4.getStartToken().toString().equalsIgnoreCase("RETURN"));
        assertTrue(stmt4.getEndToken().toString().equalsIgnoreCase(")"));


    }

}
