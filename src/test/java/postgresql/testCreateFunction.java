package test.postgresql;
/*
 * Date: 13-11-29
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.nodes.TTableElementList;
import gudusoft.gsqlparser.stmt.postgresql.TPostgresqlCreateFunction;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION sales_tax(subtotal int) RETURNS real AS $$\n" +
                "BEGIN\n" +
                "    RETURN subtotal * 0.06;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql; ";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("sales_tax"));
        assertTrue(createFunction.getParameterDeclarations().size() == 1);
        TParameterDeclaration parameterDeclaration = (TParameterDeclaration)createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("subtotal"));
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.int_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.real_t);
        assertTrue(createFunction.getProcedureLanguage().toString().equalsIgnoreCase("plpgsql"));
    }

    public void testReturnTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION extended_sales(p_itemno int)\n" +
                "RETURNS TABLE(quantity int, total numeric) AS $$\n" +
                "BEGIN\n" +
                "RETURN QUERY SELECT quantity, quantity\n" +
                "*\n" +
                "price FROM sales\n" +
                "WHERE itemno = p_itemno;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("extended_sales"));
        TParameterDeclaration parameterDeclaration = (TParameterDeclaration)createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("p_itemno"));
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.int_t);
        assertTrue(createFunction.getReturnMode() == TBaseType.function_return_table);
        TTableElementList tls = createFunction.getReturnTableDefinitions();
        assertTrue(tls.size() == 2);

        TColumnDefinition cd = tls.getTableElement(0).getColumnDefinition();
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("quantity"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.int_t);

        cd = tls.getTableElement(1).getColumnDefinition();
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("total"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.numeric_t);

    }

}
