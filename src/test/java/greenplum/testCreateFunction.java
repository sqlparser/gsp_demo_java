package test.greenplum;
/*
 * Date: 13-12-26
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.postgresql.TPostgresqlCreateFunction;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE FUNCTION complex_add(complex, complex)\n" +
                "RETURNS complex\n" +
                "AS 'filename', 'complex_add'\n" +
                "LANGUAGE C IMMUTABLE STRICT;";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("complex_add"));
        assertTrue(createFunction.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = (TParameterDeclaration)createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.generic_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.generic_t);
        assertTrue(createFunction.getProcedureLanguage().toString().equalsIgnoreCase("C"));
        assertTrue(createFunction.getObjfile().toString().equalsIgnoreCase("'filename'"));
        assertTrue(createFunction.getLinkSymbol().toString().equalsIgnoreCase("'complex_add'"));
    }

}
