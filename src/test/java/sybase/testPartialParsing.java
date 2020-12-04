package sybase;
/*
 * Date: 12-9-20
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import junit.framework.TestCase;

public class testPartialParsing extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "create procedure showind @tabname varchar (30)\n" +
                "as\n" +
                "select sysobjects.name, sysindexes.name, indid\n" +
                "froms sysindexes, sysobjects\n" +
                "where sysobjects.name = @tabname\n" +
                "and sysobjects.id = sysindexes.id\n" +
                "\n" +
                "delete from b where b.f > 1";
        int i = sqlparser.parse() ;
        assertTrue(i != 0);
        //System.out.println(sqlparser.getErrormessage());
        TMssqlCreateProcedure procedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(procedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        assertTrue(procedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstinvalid);
        assertTrue(procedure.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstdelete);

        TDeleteSqlStatement delete = (TDeleteSqlStatement)procedure.getBodyStatements().get(2);
        assertTrue(delete.getTargetTable().toString().equalsIgnoreCase("b"));
        assertTrue(delete.getWhereClause().getCondition().getExpressionType() == EExpressionType.simple_comparison_t);

//        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)procedure.getBodyStatements().get(0);
//        System.out.println(selectSqlStatement.getStartToken().toString());
//        System.out.println(selectSqlStatement.getEndToken().toString());
//        TParseErrorSqlStatement errorSqlStatement = (TParseErrorSqlStatement)procedure.getBodyStatements().get(1);
//        System.out.println(errorSqlStatement.getStartToken().toString());
//        System.out.println(errorSqlStatement.getEndToken().toString());
    }

}
