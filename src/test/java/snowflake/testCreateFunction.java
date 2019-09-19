package test.snowflake;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.snowflake.TSnowflakeCreateFunctionStmt;
import junit.framework.TestCase;


public class testCreateFunction extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE or replace FUNCTION function1() RETURNS \n" +
                "TABLE (SERIAL_NUM nvarchar, STATUS_CD nvarchar) \n" +
                "AS \n" +
                "'select SERIAL_NUM, STATUS_CD from s_asset';";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TSnowflakeCreateFunctionStmt createFunction = (TSnowflakeCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getFunctionDefinition().equalsIgnoreCase("'select SERIAL_NUM, STATUS_CD from s_asset'"));

        TGSqlParser sqlparser2 = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser2.sqltext = createFunction.getFunctionDefinition();
        int iRet = sqlparser2.parse();
        // Since the definition of a function may not always a valid SQL statement, it maybe a Javascript, or a SQL expression
        // so we need to check whether the function definition is parsed correct before get the detailed SQL statement info.
        if (iRet == 0){
            sqlStatement = sqlparser2.sqlstatements.get(0);
            assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstselect);
        }
    }
}