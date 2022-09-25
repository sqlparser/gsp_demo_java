package bigquery;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import junit.framework.TestCase;

public class testCreateTableFunction extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE OR REPLACE TABLE FUNCTION mydataset.names_by_year(y INT64)\n" +
                "      AS\n" +
                "      SELECT year, name, SUM(number) AS total\n" +
                "      FROM `usa_names.usa_1910_current`\n" +
                "      WHERE year = y\n" +
                "      GROUP BY year, name;";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunctionStmt =  (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunctionStmt.getFunctionName().toString().equalsIgnoreCase("mydataset.names_by_year"));
        assertTrue(createFunctionStmt.getParameterDeclarations().getParameterDeclarationItem(0).getParameterName().toString().equalsIgnoreCase("y"));
        assertTrue(createFunctionStmt.getReturnsType() == EFunctionReturnsType.frtInlineTableValue);
        assertTrue(createFunctionStmt.getSqlQuery().getResultColumnList().size() ==3);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);

        sqlparser.sqltext = "CREATE OR REPLACE TABLE FUNCTION mydataset.names_by_year(y INT64)\n" +
                "RETURNS TABLE<name STRING, year INT64, total INT64>\n" +
                "AS\n" +
                "  SELECT year, name, SUM(number) AS total\n" +
                "  FROM `bigquery-public-data.usa_names.usa_1910_current`\n" +
                "  WHERE year = y\n" +
                "  GROUP BY year, name;";

        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunctionStmt =  (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createFunctionStmt.getFunctionName().toString().equalsIgnoreCase("mydataset.names_by_year"));
        assertTrue(createFunctionStmt.getParameterDeclarations().getParameterDeclarationItem(0).getParameterName().toString().equalsIgnoreCase("y"));
        assertTrue(createFunctionStmt.getReturnsType() == EFunctionReturnsType.frtInlineTableValue);
        assertTrue(createFunctionStmt.getSqlQuery().getResultColumnList().size() ==3);
        TTypeName returnType  =  createFunctionStmt.getReturnDataType();
        assertTrue(returnType.getDataType() == EDataType.table_t);
        assertTrue(returnType.getColumnDefList().getColumn(0).getColumnName().toString().equalsIgnoreCase("name"));
        assertTrue(returnType.getColumnDefList().getColumn(0).getDatatype().getDataType() == EDataType.string_t);
        assertTrue(returnType.getColumnDefList().getColumn(1).getColumnName().toString().equalsIgnoreCase("year"));
        assertTrue(returnType.getColumnDefList().getColumn(1).getDatatype().getDataType() == EDataType.int64_t);
    }

}
