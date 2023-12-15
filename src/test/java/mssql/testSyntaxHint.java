package mssql;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import junit.framework.TestCase;

public class testSyntaxHint extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqlfilename = gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"mssql/octopai/3243.sql";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateview);
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getSyntaxHints().size() == 14);
    }

}
