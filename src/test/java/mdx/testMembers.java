package mdx;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.mdx.*;
import gudusoft.gsqlparser.stmt.mdx.TMdxSelect;
import junit.framework.TestCase;

public class testMembers extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "with\n" +
                "member MTDActual as ([Measures].[Life Submitted Policy Count], [DateTool].[Period].[Month To Date])\n" +
                "member MTDGoal as ([Measures].[Distributor Goal Amt], [Goal Type].[Goal Type].[All].[Life Product Policy Goal], [DateTool].[Period].[Month To Date])\n" +
                "member YTDActual as ([Measures].[Life Submitted Policy Count], [DateTool].[Period].[Year To Date])\n" +
                "member YTDGoal as ([Measures].[Distributor Goal Amt], [Goal Type].[Goal Type].[All].[Life Product Policy Goal], [DateTool].[Period].[Year To Date])\n" +
                "member Prev10PrdsActual as ([Measures].[Life Submitted Policy Count], [DateTool].[Period].[Prev 10 Prds Avg])\n" +
                "member Prev10PrdsGoal as ([Measures].[Distributor Goal Amt], [Goal Type].[Goal Type].[All].[Life Product Policy Goal], [DateTool].[Period].[Prev 10 Prds Avg])\n" +
                "\n" +
                "select {\n" +
                "        MTDActual\n" +
                "       ,MTDGoal\n" +
                "       ,YTDActual\n" +
                "       ,YTDGoal\n" +
                "       ,Prev10PrdsActual\n" +
                "       ,Prev10PrdsGoal\n" +
                "       } on columns,\n" +
                "except([Product].[Life Sales Product Group].[Life Sales Product Group].members, {[Product].[Life Sales Product Group].[Life Sales Product Group].[]}) on rows\n" +
                "from\n" +
                "    [Life Analytics]\n" +
                "where\n" +
                "    ORDER(\n" +
                "    NONEMPTY(\n" +
                "    [Transaction Date].[Hierarchy - Month].[Date].members,\n" +
                "    [Measures].[Life Delivered Premium]),[Transaction Date].[Hierarchy - Month].currentmember.member_key,\n" +
                "    BDESC).item(@TransactionDateIndex)";
        int i = sqlparser.parse();
        assertTrue( i== 0);
        TMdxSelect mdxSelect = (TMdxSelect)sqlparser.sqlstatements.get(0);
        TMdxWhereNode whereNode = mdxSelect.getWhere();
        TMdxExpNode where = whereNode.getFilter();
        assertTrue(where instanceof TMdxFunctionNode);
        TMdxFunctionNode functionNode = (TMdxFunctionNode)where;
        assertTrue(functionNode.getExpSyntax() == EMdxExpSyntax.Method);
        assertTrue(functionNode.getFunctionName().equalsIgnoreCase("item"));
        assertTrue(functionNode.getArguments().size() == 2);
        assertTrue(functionNode.getArguments().getElement(1).toString().equalsIgnoreCase("@TransactionDateIndex"));
        TMdxExpNode objectExpr = functionNode.getArguments().getElement(0);
        assertTrue(objectExpr instanceof TMdxFunctionNode);
        TMdxFunctionNode orderFunction = (TMdxFunctionNode)objectExpr;
        assertTrue(orderFunction.getFunctionName().equalsIgnoreCase("ORDER"));
        assertTrue(orderFunction.getExpSyntax() == EMdxExpSyntax.Function);
        //System.out.println(orderFunction.getArguments().size());
        assertTrue(orderFunction.getArguments().size() == 3);
        TMdxExpNode firstArg = orderFunction.getArguments().getElement(0);
        assertTrue(firstArg instanceof TMdxFunctionNode);
        TMdxFunctionNode nonemptyFunction = (TMdxFunctionNode)firstArg;
        assertTrue(nonemptyFunction.getFunctionName().equalsIgnoreCase("NONEMPTY"));
        assertTrue(nonemptyFunction.getExpSyntax() == EMdxExpSyntax.Function);
        assertTrue(nonemptyFunction.getArguments().size() == 2);
        firstArg = nonemptyFunction.getArguments().getElement(0);
       // System.out.println(firstArg.getClass().toString());
        TMdxIdentifierNode members = (TMdxIdentifierNode)firstArg;
        assertTrue(members.getSegmentList().size() == 4);
        assertTrue(members.getSegmentList().getElement(0).getName().equalsIgnoreCase("[Transaction Date]"));
        assertTrue(members.getSegmentList().getElement(1).getName().equalsIgnoreCase("[Hierarchy - Month]"));
        assertTrue(members.getSegmentList().getElement(2).getName().equalsIgnoreCase("[Date]"));
        assertTrue(members.getSegmentList().getElement(3).getName().equalsIgnoreCase("members"));
        TMdxExpNode secondArg = orderFunction.getArguments().getElement(1);
        members = (TMdxIdentifierNode)secondArg;
        assertTrue(members.getSegmentList().size() == 4);
        assertTrue(members.getSegmentList().getElement(0).getName().equalsIgnoreCase("[Transaction Date]"));
        assertTrue(members.getSegmentList().getElement(1).getName().equalsIgnoreCase("[Hierarchy - Month]"));
        assertTrue(members.getSegmentList().getElement(2).getName().equalsIgnoreCase("currentmember"));
        assertTrue(members.getSegmentList().getElement(3).getName().equalsIgnoreCase("member_key"));
    }
}
