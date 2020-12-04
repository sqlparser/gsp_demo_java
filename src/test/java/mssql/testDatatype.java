package mssql;
/*
 * Date: 12-5-23
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;
import junit.framework.TestCase;

public class testDatatype extends TestCase {

    public void test1(){
      //System.out.println(TBaseType.versionid);
      //System.out.println(TBaseType.releaseDate);
      TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
      String sp_sql =
      "CREATE PROCEDURE [dbo].[test]" +
      "        @OD1 AS datetime = '2000-01-01'," +
      "        @OD2 AS datetime = '2012-12-31'" +
      "      AS" +
      "      BEGIN" +
      "         select * from [sales_fact]" +
      "         where" +
      "         [sales_fact].[OrderDate] between @OD1 and @OD2" +
      "      END";
      sqlparser.sqltext = sp_sql;

      int ret = sqlparser.parse();
      if (ret == 0) {
         TCustomSqlStatement sql = sqlparser.sqlstatements.get(0);
         //System.out.println("SQL Statement: " + sql.sqlstatementtype);
         //System.out.println("Parameters:");

         TStoredProcedureSqlStatement procedure = (TStoredProcedureSqlStatement) sql;
         TParameterDeclaration param = null;
            param = procedure.getParameterDeclarations().getParameterDeclarationItem(0);
            assertTrue(param.getParameterName().toString().equalsIgnoreCase("@OD1"));
            assertTrue(param.getDataType().toString().equalsIgnoreCase("datetime"));
            assertTrue(param.getDataType().getDataType() == EDataType.datetime_t);
            assertTrue(param.getMode() == 0);
      } else {
         System.out.println(sqlparser.getErrormessage());
      }
    }


}
