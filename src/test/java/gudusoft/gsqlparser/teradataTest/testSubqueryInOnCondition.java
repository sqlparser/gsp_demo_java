package gudusoft.gsqlparser.teradataTest;
/*
 * Date: 13-2-5
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSubqueryInOnCondition extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE VIEW AUDEV01_BUS_TRFM_VIEW.VTX_371_DSUBSDM_ICTPSTWR_01 AS\n" +
                "      SELECT\n" +
                "         AT_CAI.Subs_ID,\n" +
                "         BUS_DRVR.Prd_Dt,\n" +
                "         AT_CAI.Prd_Cvrg_Start_Dt,\n" +
                "         AT_CAI.Prd_Cvrg_End_Dt,\n" +
                "         SUM(CASE WHEN AT_DUH.Usg_Nm_Lvl1 ='L1Voice'  \n" +
                "         \tTHEN ZEROIFNULL(AT_ICT.Chrgbl_Rev_AUD_Amt ) \n" +
                "                   END) AS B_Algn_ICT_Rev_Vc_ExGST_A,\n" +
                "         SUM(CASE WHEN AT_DUH.Usg_Nm_Lvl1 = 'L1SMS' \n" +
                "         \tTHEN ZEROIFNULL(AT_ICT.Chrgbl_Rev_AUD_Amt ) \n" +
                "                   END) AS B_Algn_ICT_Rev_SMS_ExGST_A,\n" +
                "         SUM(CASE WHEN AT_DUH.Usg_Nm_Lvl1 ='L1Voice' \n" +
                "         \tTHEN ZEROIFNULL(AT_ICT.Chrgbl_Cost_AUD_Amt  ) \n" +
                "                   END) AS B_Algn_ICT_Cost_Vc_ExGST_A,\n" +
                "         SUM(CASE WHEN AT_DUH.Usg_Nm_Lvl1 = 'L1SMS' \n" +
                "         \tTHEN ZEROIFNULL(AT_ICT.Chrgbl_Cost_AUD_Amt  ) \n" +
                "                   END) AS B_Algn_ICT_Cost_SMS_ExGST_A\n" +
                "      FROM\n" +
                "         AUDEV01_BUS_VIEW.BUS_MEAS_ICT_RTL AS AT_ICT RIGHT JOIN \n" +
                "         AUDEV01_PRE_BUS_VIEW.W_SUBS_DRCT_CAI_USG_DTS AS AT_CAI\n" +
                "            ON\n" +
                "            (\n" +
                "               AT_ICT.Subs_Id = AT_CAI.Subs_ID\n" +
                "               AND AT_ICT.Prd_Dt BETWEEN AT_CAI.Prd_Cvrg_Start_Dt\n" +
                "               AND AT_CAI.Prd_Cvrg_End_Dt -1\n" +
                "               AND AT_ICT.INT_ACCT_FLG = 'N'\n" +
                "               AND AT_ICT.Acct_Type_Cd = 'Postpay'\n" +
                "               AND AT_ICT.Brnd_Cd = 'Vodafone'\n" +
                "            ) LEFT JOIN \n" +
                "         AUDEV01_MODEL_VIEW.GSMSI_USG_CLASS_ASSN AS AT_GUCA\n" +
                "            ON\n" +
                "            (\n" +
                "               AT_ICT.GSMSI_Key = AT_GUCA.GSMSI_Key\n" +
                "               AND AT_GUCA.GSMSI_Usg_Class_End_Dt = DATE '9999-12-31'\n" +
                "               AND AT_GUCA.Usg_Class_Key IN (\n" +
                "                  SELECT\n" +
                "                     AT_UC.Usg_Class_Key\n" +
                "                  FROM\n" +
                "                     AUDEV01_MODEL_VIEW.USG_CLASS AS AT_UC\n" +
                "                  WHERE\n" +
                "                     AT_UC.Usg_Class_Type_Cd = 'FINANCE'\n" +
                "               )\n" +
                "            ) LEFT JOIN \n" +
                "         AUDEV01_PRE_BUS_VIEW.W_BUS_TM_DRVR AS BUS_DRVR\n" +
                "            ON\n" +
                "            (\n" +
                "               BUS_DRVR.Prd_Type_Cd = 'MONTH'\n" +
                "               AND BUS_DRVR.Subj_Area_Cd = 'SDM'\n" +
                "            ) LEFT JOIN \n" +
                "         AUDEV01_BUS_VIEW.DIM_USG_HIER AS AT_DUH\n" +
                "            ON\n" +
                "            (\n" +
                "               AT_GUCA.Usg_Class_Key = AT_DUH.Usg_Class_Key\n" +
                "            )\n" +
                "      WHERE\n" +
                "         AT_CAI.Subs_ID IS NOT NULL\n" +
                "         AND AT_CAI.Subs_ID > 0\n" +
                "      GROUP BY\n" +
                "         1,\n" +
                "         2,\n" +
                "         3,\n" +
                "         4;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement view = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = view.getSubquery();

        TJoin join = select.joins.getJoin(0);
        TJoinItem joinItem1 = join.getJoinItems().getJoinItem(1);
        TExpression expression = joinItem1.getOnCondition();
        assertTrue(expression.getExpressionType() == EExpressionType.parenthesis_t);
        TExpression inSideExpr = expression.getLeftOperand();
        assertTrue(inSideExpr.getRightOperand().getRightOperand().getExpressionType() == EExpressionType.subquery_t);
        TSelectSqlStatement subQuery = inSideExpr.getRightOperand().getRightOperand().getSubQuery();
        assertTrue(subQuery.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("AT_UC.Usg_Class_Key"));

    }

}
