package common;

import gudusoft.gsqlparser.EDbObjectType;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;

/*
 * Date: 2010-11-5
 * Time: 14:12:58
. */

public class testTSourceToken extends TestCase {

    public void testDatatype(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create table a (f1 double precision,f2 real(9), f3 timestamp(1) with time zone)";
        assertTrue(sqlparser.parse() == 0);
        TSourceToken st = null;
        for(int i=0;i<sqlparser.sourcetokenlist.size();i++){
            st = sqlparser.sourcetokenlist.get(i);
            if (st.astext.equalsIgnoreCase("double")){
                assertTrue(st.getDbObjType() == TObjectName.ttobjDatatype);
            }

            if (st.astext.equalsIgnoreCase("real")){
                assertTrue(st.getDbObjType() == TObjectName.ttobjDatatype);
            }

            if (st.astext.equalsIgnoreCase("1")){
                assertTrue(st.getDbObjType() != TObjectName.ttobjDatatype);
            }

            if (st.astext.equalsIgnoreCase("zone")){
                assertTrue(st.getDbObjType() == TObjectName.ttobjDatatype);
            }

        }
    }

    public void testDbObject(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace view test4 as\n" +
                "\n" +
                "select t.account_name, t.account_number b_alias, c.CHECK_NUMBER\n" +
                "\n" +
                "from  ss.AP10_BANK_ACCOUNTS t, AP10_CHECKS c\n" +
                "\n" +
                "where t.Account_Name = c.BANK_ACCOUNT_NAME\n" +
                "\n" +
                "and t.bank_name = 'Bank of Mars'\n" +
                "\n" +
                "and t.branch_city = 'Giant Crater'\n" +
                "\n" +
                "order by t.account_name asc;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = createView.getSubquery();
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        //assertTrue(column.getExpr().getObjectOperand().getObjectToken().getDbObjType() == TObjectName.ttObjTableAlias);
        assertTrue(column.getExpr().getObjectOperand().getObjectToken().getDbObjectType() == EDbObjectType.table_alias);
        assertTrue(column.getExpr().getObjectOperand().getPartToken().getDbObjType() == TObjectName.ttobjColumn);

        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TAliasClause columnAlias = column1.getAliasClause();
        assertTrue(columnAlias.getAliasName().getObjectToken().getDbObjType() == TObjectName.ttobjColumnAlias);

        TTable table0 = select.tables.getTable(0);
        assertTrue(table0.getTableName().getSchemaToken().getDbObjType() == TObjectName.ttobjSchemaName);
        assertTrue(table0.getTableName().getObjectToken().getDbObjType() == TObjectName.ttobjTable);

        TAliasClause tableAlias = table0.getAliasClause();
        //assert(tableAlias.getAliasName().getObjectToken().getDbObjType() == TObjectName.ttObjTableAlias);
        assert(tableAlias.getAliasName().getObjectToken().getDbObjectType() == EDbObjectType.table_alias);
    }

}
