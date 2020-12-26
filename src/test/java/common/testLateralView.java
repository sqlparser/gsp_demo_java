import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.hive.THiveLateralView;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testLateralView extends TestCase {

    public void testLateralViewClause() {
        TGSqlParser tgSqlParser = new TGSqlParser(EDbVendor.dbvhive);

        tgSqlParser.sqltext = "SELECT pageid, adid FROM pageAds LATERAL VIEW explode(adid_list) adTable AS adid";

        assertTrue(tgSqlParser.parse() == 0);
        TSelectSqlStatement sqlstatements = (TSelectSqlStatement) tgSqlParser.getSqlstatements().get(0);
        TTableList tables = sqlstatements.getTables();
        TPTNodeList<THiveLateralView> lateralViewList = tables.getTable(0).getLateralViewList();
        assertTrue(lateralViewList.toString().equalsIgnoreCase("LATERAL VIEW explode(adid_list) adTable AS adid"));
        assertTrue(lateralViewList.getElement(0).toString().equalsIgnoreCase("LATERAL VIEW explode(adid_list) adTable AS adid"));

        TFunctionCall tFunctionCall = lateralViewList.getElement(0).getUdtf();
        assertTrue(tFunctionCall.toString().equalsIgnoreCase("explode(adid_list)"));

        TAliasClause tableAlias = lateralViewList.getElement(0).getTableAlias();
        assertTrue(tableAlias.toString().equalsIgnoreCase("adTable"));

        TObjectNameList columnAliasList = lateralViewList.getElement(0).getColumnAliasList();
        TObjectName element = (TObjectName) columnAliasList.getElement(0);
        assertTrue(element.toString().equalsIgnoreCase("adid"));
    }

}
