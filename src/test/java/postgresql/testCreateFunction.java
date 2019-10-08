package test.postgresql;
/*
 * Date: 13-11-29
 */

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.postgresql.TPostgresqlCreateFunction;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {

    public void testLanguagePython(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION public.s2_cellid_contains(a bigint, b bigint)\n" +
                " RETURNS boolean\n" +
                " LANGUAGE plpython3u\n" +
                " IMMUTABLE STRICT\n" +
                "AS $function$\n" +
                "  import s2sphere\n" +
                "  id_a = s2sphere.CellId(int.from_bytes(a.to_bytes(8, 'big', signed=True), 'big', signed=False))\n" +
                "  id_b = s2sphere.CellId(int.from_bytes(b.to_bytes(8, 'big', signed=True), 'big', signed=False))\n" +
                "  return id_a.contains(id_b)\n" +
                "$function$";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("public.s2_cellid_contains"));
        assertTrue(createFunction.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameterDeclaration = (TParameterDeclaration)createFunction.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameterDeclaration.getParameterName().toString().equalsIgnoreCase("a"));
        assertTrue(parameterDeclaration.getDataType().getDataType() == EDataType.bigint_t);
        assertTrue(createFunction.getReturnDataType().getDataType() == EDataType.boolean_t);
        assertTrue(createFunction.getProcedureLanguage().toString().equalsIgnoreCase("plpython3u"));
    }

    public void testDeclare(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE FUNCTION somefunc() RETURNS integer AS $$\n" +
                "<< outerblock >>\n" +
                "DECLARE\n" +
                "    quantity integer := 30;\n" +
                "BEGIN\n" +
                "    RAISE NOTICE 'Quantity here is %', quantity;  -- Prints 30\n" +
                "    quantity := 50;\n" +
                "    --\n" +
                "    -- Create a subblock\n" +
                "    --\n" +
                "    DECLARE\n" +
                "        quantity integer := 80;\n" +
                "    BEGIN\n" +
                "        RAISE NOTICE 'Quantity here is %', quantity;  -- Prints 80\n" +
                "        RAISE NOTICE 'Outer quantity here is %', outerblock.quantity;  -- Prints 50\n" +
                "    END;\n" +
                "\n" +
                "    RAISE NOTICE 'Quantity here is %', quantity;  -- Prints 50\n" +
                "\n" +
                "    RETURN quantity;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlCreateFunction createFunction = (TPostgresqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("somefunc"));
        assertTrue(createFunction.getDeclareStatements().size() == 1);
        TVarDeclStmt declareVariable = (TVarDeclStmt)createFunction.getDeclareStatements().get(0);
        assertTrue(declareVariable.getElementName().toString().equalsIgnoreCase("quantity"));
        assertTrue(createFunction.getBodyStatements().size()==5);
    }

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
