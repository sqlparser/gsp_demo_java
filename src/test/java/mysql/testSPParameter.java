package mysql;
/*
 * Date: 12-5-7
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import junit.framework.TestCase;

public class testSPParameter extends TestCase {

    public void test1(){
      //System.out.println(TBaseType.versionid);
      //System.out.println(TBaseType.releaseDate);
      TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
      sqlparser.sqltext = "CREATE DEFINER=`sa`@`%` PROCEDURE `test2`(IN `in` VARCHAR(255), OUT `out` tinyint, INOUT `inout` tinyint) BEGIN SELECT city, phone FROM offices WHERE country = `in`; END";

//      sqlparser.sqltext = "CREATE PROCEDURE `test2`(IN `in` VARCHAR(255), OUT `out` tinyint, INOUT `inout` tinyint) \n" +
//              "BEGIN \n" +
//              "SELECT city, phone FROM offices WHERE country = `in`; \n" +
//              "END";

      int ret = sqlparser.parse();
      if (ret == 0) {
         TCustomSqlStatement sql = sqlparser.sqlstatements.get(0);
         //System.out.println("SQL Statement: " + sql.sqlstatementtype);
          assertTrue(sql.sqlstatementtype == ESqlStatementType.sstcreateprocedure);

          TCreateProcedureStmt procedure = (TCreateProcedureStmt) sql;
         //System.out.println("Procedure name: " + procedure.getProcedureName().toString());
         //System.out.println("Parameters:");

          TParameterDeclaration param0 = procedure.getParameterDeclarations().getParameterDeclarationItem(0);
          TParameterDeclaration param1 = procedure.getParameterDeclarations().getParameterDeclarationItem(1);
          TParameterDeclaration param2 = procedure.getParameterDeclarations().getParameterDeclarationItem(2);
          assertTrue(param0.getParameterName().toString().equalsIgnoreCase("`in`"));
          assertTrue(param0.getDataType().toString().equalsIgnoreCase("VARCHAR(255)"));
          assertTrue(param0.getMode() == 1);

          assertTrue(param1.getParameterName().toString().equalsIgnoreCase("`out`"));
          assertTrue(param1.getDataType().toString().equalsIgnoreCase("tinyint"));
          assertTrue(param1.getMode() == 2);

          assertTrue(param2.getParameterName().toString().equalsIgnoreCase("`inout`"));
          assertTrue(param2.getDataType().toString().equalsIgnoreCase("tinyint"));
          assertTrue(param2.getMode() == 3);

         TParameterDeclaration param = null;
         for (int i = 0; i < procedure.getParameterDeclarations().size(); i++) {
            param = procedure.getParameterDeclarations().getParameterDeclarationItem(i);
//            System.out.println("\tName:" + param.getParameterName().toString());
//            System.out.println("\tDatatype:" + param.getDataType().toString());
//            System.out.println("\tIN/OUT:" + param.getMode());
         }
      } else {
         System.out.println(sqlparser.getErrormessage());
      }
    }

}
