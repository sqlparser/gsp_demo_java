package mdx;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.mdx.TMdxAxisNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxCaseNode;
import gudusoft.gsqlparser.nodes.mdx.TMdxWithMemberNode;
import gudusoft.gsqlparser.stmt.mdx.TMdxCreateMember;
import gudusoft.gsqlparser.stmt.mdx.TMdxSelect;
import junit.framework.TestCase;


public class testCreateMember extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "CREATE MEMBER CURRENTCUBE.Measures.[_Internet Current Quarter Sales Performance Status] \n" +
                "AS 'Case When IsEmpty(KpiValue(\"Internet Current Quarter Sales Performance\")) Then Null When KpiValue(\"Internet Current Quarter Sales Performance\") < 1 Then -1 When KpiValue(\"Internet Current Quarter Sales Performance\") >= 1 And KpiValue(\"Internet Current Quarter Sales Performance\") < 1.07 Then 0 Else 1 End', \n" +
                "ASSOCIATED_MEASURE_GROUP = 'Internet Sales';";
        int i = sqlparser.parse();
        assertTrue( i== 0);
        TMdxCreateMember createMember = (TMdxCreateMember)sqlparser.sqlstatements.get(0);
        TMdxWithMemberNode withMemberNode = createMember.getSpecification();

        String newQuery = "select "+TBaseType.getStringInsideLiteral(withMemberNode.getExprNode().toString())+" on 1 from t";
        sqlparser.sqltext = newQuery;
        i = sqlparser.parse();
        assertTrue( i== 0);
        //System.out.println(sqlparser.sqlstatements.get(0).sqlstatementtype);
        TMdxSelect select = (TMdxSelect)sqlparser.sqlstatements.get(0);
        TMdxAxisNode axisNode = select.getAxes().getElement(0);
        TMdxCaseNode caseNode = (TMdxCaseNode)axisNode.getExpNode();

        assertTrue(caseNode.getWhenList().size() == 3);

    }
}
