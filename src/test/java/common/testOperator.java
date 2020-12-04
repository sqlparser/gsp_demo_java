package common;
/*
 * Date: 12-9-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testOperator extends TestCase {

        public void testNotEqual(){
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvoracle),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvmssql),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvmysql),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvdb2),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvpostgresql),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvsybase),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvteradata),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvnetezza),"!=");
    }

    void checkNotEqualCode( TGSqlParser sqlparser,String op){
        sqlparser.sqltext = "select * from dual where 1"+op+"1";
        sqlparser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression condition = select.getWhereClause().getCondition();
        assertTrue(condition.getOperatorToken().tokencode == TBaseType.not_equal);
    }

}
