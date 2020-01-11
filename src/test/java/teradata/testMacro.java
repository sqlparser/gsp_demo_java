package teradata;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.teradata.TTeradataCreateMacro;
import gudusoft.gsqlparser.stmt.teradata.TTeradataLock;
import junit.framework.TestCase;


public class testMacro extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = " REPLACE MACRO macroName (startdt date, enddt date, WLInd char(30))\n" +
                "AS\n" +
                "(Locking row for access\n" +
                "Sel col1,col2,col3 from table1,table2,table3;);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatacreatemacro);
        TTeradataCreateMacro createMacro = (TTeradataCreateMacro)sqlparser.sqlstatements.get(0);
        assertTrue(createMacro.getMacroName().toString().equalsIgnoreCase("macroName"));
        assertTrue(createMacro.getParameterDeclarations().size() == 3);
        TParameterDeclaration parameterDeclaration = createMacro.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("startdt"));
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.date_t);
        assertTrue(createMacro.getBodyStatements().size() == 1);
        assertTrue(createMacro.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstteradatalock);
        TTeradataLock lock = (TTeradataLock)createMacro.getBodyStatements().get(0);
        assertTrue(lock.getSqlRequest().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)lock.getSqlRequest();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("table1"));

    }

}
