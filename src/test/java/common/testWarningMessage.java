package common;

import gudusoft.gsqlparser.*;
import junit.framework.TestCase;

public class testWarningMessage extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select a,b from emp,dept";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sql = (TCustomSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(sql.getSyntaxHints().size() == 2);
        TSyntaxError syntaxHint = sql.getSyntaxHints().get(0);
        //System.out.println(syntaxHint.tokentext+":"+syntaxHint.errortype+":"+syntaxHint.errorno+":"+syntaxHint.hint);
        assertTrue(syntaxHint.errortype == EErrorType.sphint);
        assertTrue(syntaxHint.errorno == TBaseType.MSG_HINT_FIND_ORPHAN_COLUMN);
        assertTrue(syntaxHint.tokentext.equalsIgnoreCase("a"));
        syntaxHint = sql.getSyntaxHints().get(1);
        //System.out.println(syntaxHint.tokentext+":"+syntaxHint.errortype+":"+syntaxHint.errorno+":"+syntaxHint.hint);
        assertTrue(syntaxHint.errortype == EErrorType.sphint);
        assertTrue(syntaxHint.errorno == TBaseType.MSG_HINT_FIND_ORPHAN_COLUMN);
        assertTrue(syntaxHint.tokentext.equalsIgnoreCase("b"));

    }
}
