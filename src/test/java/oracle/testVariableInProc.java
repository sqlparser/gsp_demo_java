package oracle;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.sqlenv.TDDLSQLEnv;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateProcedure;
import junit.framework.TestCase;

public class testVariableInProc extends TestCase {

    public static void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        sqlparser.sqltext = "CREATE TABLE MARKETDATA_TRADE\n" +
                "(\n" +
                "    tradeID              NUMBER,\n" +
                "    chappyDataID         VARCHAR2,\n" +
                "    strumentID           NUMBER\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE MARKETDATA_TRADE_2\n" +
                "(\n" +
                "    tradeID              NUMBER,\n" +
                "    chappyDataID         VARCHAR2,\n" +
                "    strumentID           NUMBER\n" +
                ");\n" +
                "\n" +
                "CREATE PROCEDURE doSomething\n" +
                "AS\n" +
                "    v_row MARKETDATA_TRADE%ROWTYPE;\n" +
                "BEGIN\n" +
                "\n" +
                "    SELECT * INTO v_row FROM MARKETDATA_TRADE WHERE tradeID = 1;\n" +
                "\n" +
                "    persistTrade(v_row.tradeID, v_row.chappyDataID, v_row.strumentID);\n" +
                "\n" +
                "END doSomething;\n" +
                "\n" +
                "CREATE PROCEDURE persistTrade(tradeID OUT NUMBER, chappyDataID IN VARCHAR2, instrumentID OUT NUMBER)\n" +
                "AS\n" +
                "    instrumentCount NUMBER;\n" +
                "BEGIN\n" +
                "    SELECT MARKETDATA_TRADE_SEQ.NEXTVAL INTO tradeID FROM dual;\n" +
                "\n" +
                "    SELECT COUNT(*) INTO instrumentCount\n" +
                "    FROM instrument_mastersecurity m\n" +
                "    WHERE m.cusip = theCusip;\n" +
                "\n" +
                "    IF instrumentCount > 0 THEN\n" +
                "        SELECT m.id INTO instrumentID\n" +
                "        FROM instrument_mastersecurity m\n" +
                "        WHERE m.cusip = theCusip;\n" +
                "    END IF;\n" +
                "\n" +
                "    INSERT INTO MARKETDATA_TRADE_2 VALUES(tradeID, instrumentID, chappyDataID);\n" +
                "\n" +
                "END persistTrade;";

        assertTrue(sqlparser.parse() == 0);
        TPlsqlCreateProcedure procedure = (TPlsqlCreateProcedure) sqlparser.sqlstatements.get(3);
        TCustomSqlStatement sqlStatement = procedure.getBodyStatements().get(3);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement) sqlStatement;
        TResultColumn value0 = insert.getValues().getMultiTarget(0).getColumnList().getResultColumn(0);
        TObjectName variable0 = value0.getExpr().getObjectOperand();
        assertTrue (variable0.getDbObjectType() == EDbObjectType.variable);
        assertTrue(variable0.getSourceTable() == null);

    }

    public void test2(){
        String sql = "CREATE TABLE MARKETDATA_TRADE\n" +
                "(\n" +
                "    tradeID              NUMBER,\n" +
                "    chappyDataID         VARCHAR2,\n" +
                "    strumentID           NUMBER\n" +
                ");\n" +
                "\n" +
                "CREATE TABLE MARKETDATA_TRADE_2\n" +
                "(\n" +
                "    tradeID              NUMBER,\n" +
                "    chappyDataID         VARCHAR2,\n" +
                "    strumentID           NUMBER\n" +
                ");\n" +
                "\n" +
                "CREATE PROCEDURE doSomething\n" +
                "AS\n" +
                "    v_row MARKETDATA_TRADE%ROWTYPE;\n" +
                "BEGIN\n" +
                "\n" +
                "    SELECT * INTO v_row FROM MARKETDATA_TRADE WHERE tradeID = 1;\n" +
                "\n" +
                "    persistTrade(v_row.tradeID, v_row.chappyDataID, v_row.strumentID);\n" +
                "\n" +
                "END doSomething;\n" +
                "\n" +
                "CREATE PROCEDURE persistTrade(tradeID OUT NUMBER, chappyDataID IN VARCHAR2, instrumentID OUT NUMBER)\n" +
                "AS\n" +
                "    instrumentCount NUMBER;\n" +
                "BEGIN\n" +
                "    SELECT MARKETDATA_TRADE_SEQ.NEXTVAL INTO tradeID FROM dual;\n" +
                "\n" +
                "    SELECT COUNT(*) INTO instrumentCount\n" +
                "    FROM instrument_mastersecurity m\n" +
                "    WHERE m.cusip = theCusip;\n" +
                "\n" +
                "    IF instrumentCount > 0 THEN\n" +
                "        SELECT m.id INTO instrumentID\n" +
                "        FROM instrument_mastersecurity m\n" +
                "        WHERE m.cusip = theCusip;\n" +
                "    END IF;\n" +
                "\n" +
                "    INSERT INTO MARKETDATA_TRADE_2 VALUES(tradeID, instrumentID, chappyDataID);\n" +
                "\n" +
                "END persistTrade;";
        TDDLSQLEnv ddlSQLEnv = new TDDLSQLEnv(null, null, null, null, EDbVendor.dbvoracle, sql);
        ddlSQLEnv.initSQLEnv();
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = sql;
        sqlparser.setSqlEnv(ddlSQLEnv);
        assertTrue(sqlparser.parse() == 0);
        TPlsqlCreateProcedure procedure = (TPlsqlCreateProcedure) sqlparser.sqlstatements.get(3);
        TCustomSqlStatement sqlStatement = procedure.getBodyStatements().get(3);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement) sqlStatement;
        TResultColumn value0 = insert.getValues().getMultiTarget(0).getColumnList().getResultColumn(0);
        TObjectName variable0 = value0.getExpr().getObjectOperand();
        assertTrue (variable0.getDbObjectType() == EDbObjectType.variable);
        assertTrue(variable0.getSourceTable() == null);
    }
}
