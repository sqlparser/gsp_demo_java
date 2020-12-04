package common;
/*
 * Date: 2010-8-30
 * Time: 15:09:38
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;

public class testMssqlProcedure extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "CREATE PROCEDURE titles_sum @TITLE varchar(40) = '%', @SUM money OUTPUT\n" +
                "AS\n" +
                "SELECT 'Title Name' = title\n" +
                "FROM titles \n" +
                "WHERE title LIKE @TITLE \n" +
                "SELECT @SUM = SUM(price)\n" +
                "FROM titles\n" +
                "WHERE title LIKE @TITLE";
        assertTrue(parser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)parser.sqlstatements.get(0);
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("titles_sum"));
        assertTrue(createProcedure.getParameterDeclarations().size() == 2);
        TParameterDeclaration parameter1 = createProcedure.getParameterDeclarations().getParameterDeclarationItem(0);
        assertTrue(parameter1.getParameterName().toString().equalsIgnoreCase("@TITLE"));
        assertTrue(parameter1.getDataType().toString().equalsIgnoreCase("varchar(40)"));
        assertTrue(parameter1.getHowtoSetValue() == TBaseType.howtoSetValue_assign);
        assertTrue(parameter1.getDefaultValue().toString().equalsIgnoreCase("'%'"));

        TParameterDeclaration parameter2 = createProcedure.getParameterDeclarations().getParameterDeclarationItem(1);
        assertTrue(parameter2.getParameterName().toString().equalsIgnoreCase("@SUM"));
        assertTrue(parameter2.getDataType().toString().equalsIgnoreCase("money"));
        assertTrue(parameter2.getMode() == TBaseType.parameter_mode_output);

        assertTrue(createProcedure.getBodyStatements().size() == 2);

    }    
}
