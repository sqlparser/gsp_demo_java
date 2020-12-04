package mdx;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.mdx.*;
import gudusoft.gsqlparser.stmt.mdx.TMdxSelect;
import junit.framework.TestCase;


public class testIIF extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "select \n" +
                "iif([Measures].[Orders Count]<>0, \n" +
                "    [Measures].[Unit Price BAD],\n" +
                "    0) on 1\n" +
                "from t";
        int i = sqlparser.parse();
        assertTrue( i== 0);

        TMdxSelect select = (TMdxSelect)sqlparser.sqlstatements.get(0);
        TMdxAxisNode axisNode = select.getAxes().getElement(0);
        TMdxFunctionNode functionNode = (TMdxFunctionNode)axisNode.getExpNode();
        assertTrue(functionNode.getArguments().size() == 3);
        for(int j=0;j<functionNode.getArguments().size();j++){
            TMdxExpNode expNode = functionNode.getArguments().getElement(j);
           // System.out.println(expNode.toString());
        }

      }
}

