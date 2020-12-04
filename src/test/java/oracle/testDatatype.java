package oracle;
/*
 * Date: 13-9-3
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlRecordTypeDefStmt;
import junit.framework.TestCase;

public class testDatatype extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace function FUNC0\n" +
                "return simple_integer\n" +
                "is\n" +
                "M_SIMPLE_INTEGER simple_integer := 2147483645;\n" +
                "type TYP11 is record ( M1 simple_integer );\n" +
                "begin\n" +
                "return M_SIMPLE_INTEGER;\n" +
                "end;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction function = (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(function.getDeclareStatements().size() == 2);
        TPlsqlRecordTypeDefStmt recordTypeDefStmt =  (TPlsqlRecordTypeDefStmt)function.getDeclareStatements().get(1);
        assertTrue(recordTypeDefStmt.getTypeName().toString().equalsIgnoreCase("TYP11"));
        TParameterDeclaration pd = recordTypeDefStmt.getFieldDeclarations().getParameterDeclarationItem(0);
        assertTrue(pd.getParameterName().toString().equalsIgnoreCase("M1"));
      //  System.out.println(pd.getDataType().getDataType());
        assertTrue(pd.getDataType().getDataType() == EDataType.simple_integer_t);
    }
}
