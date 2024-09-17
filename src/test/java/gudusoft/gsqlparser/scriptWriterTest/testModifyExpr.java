package gudusoft.gsqlparser.scriptWriterTest;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;
import gudusoft.gsqlparser.scriptWriter.TScriptGenerator;

/**
 * Code illustrates how to modify expression.
 */
public class testModifyExpr extends TestCase {

    private TGSqlParser parser = null;
    private TScriptGenerator scriptGenerator = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvoracle);
        scriptGenerator = new TScriptGenerator();
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    /**
     * set the status of an expression to be removed from the parent expression.
     */
    public void testRemoveObjectExpr() {
        TExpression expression = parser.parseExpression("columnA");
        assertTrue(expression.getExpressionType() == EExpressionType.simple_object_name_t);
        expression.remove();
        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
    }

    /**
     * Remove an expression which includes a function call from the parent expression.
     */
    public void testRemoveFunctionCallExpr() {
        TExpression expression = parser.parseExpression("fx(columnA)");
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);
        expression.remove();
        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
    }

    /**
     * Remove <code>columnC</code> in <code>fx(columnA,columnB,fx2(1+columnC))</code> cause the whole function
     * removed from the parent expression.
     */
    public void testRemoveColumnInFunctionCall() {
        TExpression expression = parser.parseExpression("fx(columnA,columnB,fx2(1+columnC))");
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);

        TExpressionList resultList = expression.searchColumn("columnC");
        assertTrue(resultList.size() == 1);
        TExpression columnCExpr = resultList.getExpression(0);
        columnCExpr.remove();

        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
    }

    /**
     * Remove <code>columnB</code> in
     * {@code
     * columnA+(columnB*2)+columnC
     * }
     * will cause the whole expression removed from the parent expression or parse tree node.
     */
    public void testSearchColumn() {
        TExpression expression = parser.parseExpression("columnA+(columnB*2)+columnC");
        TExpressionList resultList = expression.searchColumn("columnB");
        assertTrue(resultList.size() == 1);
        TExpression columnBExpr = resultList.getExpression(0);
        assertTrue(columnBExpr.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(columnBExpr.toString().equalsIgnoreCase("columnB"));

        assertTrue(expression.toScript().equalsIgnoreCase("columnA + (columnB * 2) + columnC"));
        columnBExpr.remove();
        System.out.println(expression.toScript());
        //assertTrue(expression.toScript().equalsIgnoreCase(""));
    }

    /**
     * Remove <code>columnA</code> in
     * <pre>
     * {@code
     * columnA+(columnB*2)>columnC
     * }
     * </pre>
     * will cause the whole expression removed from the parent expression or parse tree node.
     */
    public void testColumnInComparision() {
        TExpression expression = parser.parseExpression("columnA+(columnB*2)>columnC");
        TExpressionList resultList = expression.searchColumn("columnA");
        assertTrue(resultList.size() == 1);
        TExpression columnAExpr = resultList.getExpression(0);
        assertTrue(columnAExpr.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(columnAExpr.toString().equalsIgnoreCase("columnA"));

        assertTrue(expression.toScript().equalsIgnoreCase("columnA + (columnB * 2) > columnC"));
        columnAExpr.remove();
        assertTrue(expression.toScript().equalsIgnoreCase(""));
    }

    /**
     * Remove <code>columnA</code> in
     * <pre>{@code columnA+(columnB*2)>columnC and columnD=columnE-9}</pre>
     * will cause the expression
     * <pre>
     * {@code columnA+(columnB*2)>columnC}
     * </pre>
     * removed from the parent expression and keep this expression unchange:
     * <pre>
     * {@code columnD = columnE - 9}
     * </pre>
     */
    public void testColumnInAndOr() {
        TExpression expression = parser.parseExpression("columnA+(columnB*2)>columnC and columnD=columnE-9");
        TExpressionList resultList = expression.searchColumn("columnA");
        assertTrue(resultList.size() == 1);
        TExpression columnAExpr = resultList.getExpression(0);
        assertTrue(columnAExpr.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(columnAExpr.toString().equalsIgnoreCase("columnA"));

        assertTrue(expression.toScript().equalsIgnoreCase("columnA + (columnB * 2) > columnC  and  columnD = columnE - 9"));
        columnAExpr.remove();
        assertTrue(expression.toScript().equalsIgnoreCase("columnD = columnE - 9"));
    }

    /**
     * Remove column: <code>application_location_id</code> from the where condition,
     * <pre>
     * {@code
     *     (pal.application_location_id = pualr.application_location_id +
     *                        AND pu.jbp_uid = pualr.jbp_uid" +
     *                        AND pu.username = 'USERID')
     * }
     * </pre>
     * Keep the following condition unchanged:
     * <pre>
     *     {@code
     *     (pu.jbp_uid = pualr.jbp_uid  and  pu.username = 'USERID')
     *     }
     * </pre>
     */
    public void testColumnInAndOr1(){
        parser.sqltext = "select *\n" +
                "from table1 pal, table2 pualr, table3 pu\n" +
                "WHERE  (pal.application_location_id = pualr.application_location_id \n" +
                "         AND pu.jbp_uid = pualr.jbp_uid \n" +
                "         AND pu.username = 'USERID')";
        int ret = parser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression = selectSqlStatement.getWhereClause().getCondition();

        TExpressionList resultList = expression.searchColumn("application_location_id");
        assertTrue(resultList.size() == 2);
        TExpression expression1 = resultList.getExpression(0);
        assertTrue(expression1.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(expression1.toString().equalsIgnoreCase("pal.application_location_id"));
        expression1.remove();
        assertTrue(expression.toScript().equalsIgnoreCase("(pu.jbp_uid = pualr.jbp_uid  and  pu.username = 'USERID')"));
    }


    /**
     * Remove the right operand of a condition in where clause.
     * <pre>
     *     {@code
     *     m.id = ?  and  m.id = altname.id(+)  and  m.id = ccu.id(+)
     *     }
     * </pre>
     * After remove the right operand, the condition become like this:
     * <pre>
     *     {@code
     *     m.id = ?  and  m.id = altname.id(+)
     *     }
     * </pre>
     * then, remove the right operand again, the condition become like this:
     * <pre>
     *     {@code
     *     m.id = ?
     *     }
     * </pre>
     */
    public void testColumnInAndOr2(){
        parser.sqltext = "SELECT m.*, \n" +
                "       altname.last_name  last_name_student, \n" +
                "       altname.first_name first_name_student, \n" +
                "       ccu.date_joined, \n" +
                "       ccu.last_login, \n" +
                "       ccu.photo_id, \n" +
                "       ccu.last_updated \n" +
                "FROM   summit.mstr m, \n" +
                "       summit.alt_name altname, \n" +
                "       smmtccon.ccn_user ccu \n" +
                "WHERE  m.id =?\n" +
                "       AND m.id = altname.id(+) \n" +
                "       AND m.id = ccu.id(+) \n" +
                "       AND altname.grad_name_ind(+) = '*'";
        int ret = parser.parse();

        assertTrue(ret == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression = selectSqlStatement.getWhereClause().getCondition();

        expression.getRightOperand().remove();
        assertTrue(expression.toScript().equalsIgnoreCase("m.id = ?  and  m.id = altname.id(+)  and  m.id = ccu.id(+)"));
        expression.getRightOperand().remove();
        assertTrue(expression.toScript().equalsIgnoreCase("m.id = ?  and  m.id = altname.id(+)"));
        expression.getRightOperand().remove();
        assertTrue(expression.toScript().equalsIgnoreCase("m.id = ?"));
    }

    /**
     * Remove those columns from the condition: lst.soort_adres, nat.prs_id, adr.id, prs.id
     * <pre>
     *     {@code
     *   pas.soort_adres = lst.soort_adres
     *   and prs.id(+) = nat.prs_id
     *   and adr.id = pas.adr_id
     *   and prs.id = pas.prs_id
     *     and lst.persoonssoort = 'PERSOON'
     *      and pas.einddatumrelatie is null;
     *     }
     * </pre>
     * The result condition is:
     * <pre>
     *     {@code
     *     lst.persoonssoort = 'PERSOON'
     *            and pas.einddatumrelatie is null"
     *     }
     * </pre>
     */
    public void testColumnInAndOr3(){
        parser.sqltext = "select *\n" +
                "from  ods_trf_pnb_stuf_lijst_adrsrt2 lst\n" +
                "\t\t, ods_stg_pnb_stuf_pers_adr pas\n" +
                "\t\t, ods_stg_pnb_stuf_pers_nat nat\n" +
                "\t\t, ods_stg_pnb_stuf_adr adr\n" +
                "\t\t, ods_stg_pnb_stuf_np prs\n" +
                "where \n" +
                "\tpas.soort_adres = lst.soort_adres\n" +
                "\tand prs.id(+) = nat.prs_id\n" +
                "\tand adr.id = pas.adr_id\n" +
                "\tand prs.id = pas.prs_id\n" +
                "  and lst.persoonssoort = 'PERSOON'\n" +
                "   and pas.einddatumrelatie is null";
        int ret = parser.parse();
        assertTrue(ret == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)parser.sqlstatements.get(0);

        TExpression expression = selectSqlStatement.getWhereClause().getCondition();

        TExpressionList resultList = expression.searchColumn("lst.soort_adres");
        assertTrue(resultList.size() == 1);
        TExpression expression1 = resultList.getExpression(0);
        assertTrue(expression1.getExpressionType() == EExpressionType.simple_object_name_t);
        expression1.remove();

        resultList = expression.searchColumn("nat.prs_id");
        assertTrue(resultList.size() == 1);
        expression1 = resultList.getExpression(0);
        assertTrue(expression1.getExpressionType() == EExpressionType.simple_object_name_t);
        expression1.remove();

        resultList = expression.searchColumn("adr.id");
        assertTrue(resultList.size() == 1);
        expression1 = resultList.getExpression(0);
        assertTrue(expression1.getExpressionType() == EExpressionType.simple_object_name_t);
        expression1.remove();

        resultList = expression.searchColumn("prs.id");
        assertTrue(resultList.size() == 1);
        expression1 = resultList.getExpression(0);
        assertTrue(expression1.getExpressionType() == EExpressionType.simple_object_name_t);
        expression1.remove();

        assertTrue(expression.toScript().trim().equalsIgnoreCase("lst.persoonssoort = \'PERSOON\'  and  pas.einddatumrelatie is  null"));
       // assertTrue(expression.toScript().equalsIgnoreCase("(pu.jbp_uid = pualr.jbp_uid  and  pu.username = 'USERID')"));
    }

}
