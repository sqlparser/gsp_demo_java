package removeSpecialConditions;

import demos.removeSpecialConditions.RemoveCondition;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testRemoveSpecialConditions extends TestCase{
    public void test1() {

        String sql = "SELECT\n" +
                "        SUMMIT.MSTR.ID AS \"ID\" ,\n" +
                "                SUMMIT.GIFT.GIFT_DATEDB AS \"SGF.GIFT_DATEDB\" ,\n" +
                "                SUMMIT.GIFT.GIFT_AMT AS \"SGF.GIFT_AMT\" ,\n" +
                "                SUMMIT.GIFT.PREM_VAL AS \"SGF.PREM_VAL\",\n" +
                "                SUMMIT.GIFT.GIFT_ACTG_AMT AS \"SGF.GIFT_ACTG_AMT\",\n" +
                "                SUMMIT.FUND.FUND_TITLE AS \"SDF.FUND_TITLE\"\n" +
                "        FROM\n" +
                "        UHELP.SLCT_CD ,\n" +
                "                SUMMIT.MSTR ,\n" +
                "                UHELP.CMPGN_CD ,\n" +
                "                SUMMIT.FUND ,\n" +
                "                SUMMIT.GIFT\n" +
                "        WHERE\n" +
                "                (\n" +
                "                        SUMMIT.GIFT.GIFT_DATEDB >= '$From_Date$' AND SUMMIT.GIFT.GIFT_DATEDB <= '$To_Date$'\n" +
                "                )\n" +
                "        AND\n" +
                "        SUMMIT.FUND.FUND_ACCT = SUMMIT.GIFT.FUND_ACCT AND\n" +
                "        SUMMIT.GIFT.ID = SUMMIT.MSTR.ID AND\n" +
                "        SUMMIT.GIFT.SLCT_CD = UHELP.SLCT_CD.SLCT_CD AND\n" +
                "        SUMMIT.GIFT.GIFT_CMPGN_CD = UHELP.CMPGN_CD.CMPGN_CD AND\n" +
                "        SUMMIT.FUND.FUND_DEPT_ATTRB in ('$OUMI_Departments$')\n" +
                "\n" +
                "        and 0 =\n" +
                "\n" +
                "                coalesce((select sum(g.gift_amt)\n" +
                "                        FROM\n" +
                "                        SUMMIT.FUND f,\n" +
                "                        SUMMIT.GIFT g\n" +
                "                        WHERE\n" +
                "                                (\n" +
                "                                        g.GIFT_DATEDB < '$From_Date$'\n" +
                "                                )\n" +
                "                        AND\n" +
                "                        f.FUND_ACCT = g.FUND_ACCT AND\n" +
                "                        g.ID = SUMMIT.MSTR.ID AND\n" +
                "                        f.FUND_DEPT_ATTRB in ('$OUMI_Departments$')\n" +
                "                ),0)";

        String result = "SELECT\n" +
                "        SUMMIT.MSTR.ID AS \"ID\" ,\n" +
                "                SUMMIT.GIFT.GIFT_DATEDB AS \"SGF.GIFT_DATEDB\" ,\n" +
                "                SUMMIT.GIFT.GIFT_AMT AS \"SGF.GIFT_AMT\" ,\n" +
                "                SUMMIT.GIFT.PREM_VAL AS \"SGF.PREM_VAL\",\n" +
                "                SUMMIT.GIFT.GIFT_ACTG_AMT AS \"SGF.GIFT_ACTG_AMT\",\n" +
                "                SUMMIT.FUND.FUND_TITLE AS \"SDF.FUND_TITLE\"\n" +
                "        FROM\n" +
                "        UHELP.SLCT_CD ,\n" +
                "                SUMMIT.MSTR ,\n" +
                "                UHELP.CMPGN_CD ,\n" +
                "                SUMMIT.FUND ,\n" +
                "                SUMMIT.GIFT\n" +
                "        WHERE\n" +
                "                SUMMIT.FUND.FUND_ACCT = SUMMIT.GIFT.FUND_ACCT AND\n" +
                "        SUMMIT.GIFT.ID = SUMMIT.MSTR.ID AND\n" +
                "        SUMMIT.GIFT.SLCT_CD = UHELP.SLCT_CD.SLCT_CD AND\n" +
                "        SUMMIT.GIFT.GIFT_CMPGN_CD = UHELP.CMPGN_CD.CMPGN_CD\n" +
                "\n" +
                "        and 0 =\n" +
                "\n" +
                "                coalesce((select sum(g.gift_amt)\n" +
                "                        FROM\n" +
                "                        SUMMIT.FUND f,\n" +
                "                        SUMMIT.GIFT g\n" +
                "                        WHERE\n" +
                "                                f.FUND_ACCT = g.FUND_ACCT AND\n" +
                "                        g.ID = SUMMIT.MSTR.ID\n" +
                "                ),0)";

        ArrayList<String> rmvColumnListWithout$ = new ArrayList<String>();
        rmvColumnListWithout$.add("$From_Date$");
        rmvColumnListWithout$.add("$To_Date$");
        rmvColumnListWithout$.add("$OUMI_Departments$");

        EDbVendor vendor = TGSqlParser.getDBVendorByName("mssql");

        RemoveCondition rc = new RemoveCondition(sql,vendor,rmvColumnListWithout$);

        sql = rc.getRemoveResult();

        assertTrue(result.equals(sql));
    }

    public void test2() {

        String sql = "SELECT\n" +
                "        SUMMIT.MSTR.ID AS \"ID\" ,\n" +
                "                SUMMIT.GIFT.GIFT_DATEDB AS \"SGF.GIFT_DATEDB\" ,\n" +
                "                SUMMIT.GIFT.GIFT_AMT AS \"SGF.GIFT_AMT\" ,\n" +
                "                SUMMIT.GIFT.PREM_VAL AS \"SGF.PREM_VAL\",\n" +
                "                SUMMIT.GIFT.GIFT_ACTG_AMT AS \"SGF.GIFT_ACTG_AMT\",\n" +
                "                SUMMIT.FUND.FUND_TITLE AS \"SDF.FUND_TITLE\"\n" +
                "        FROM\n" +
                "        UHELP.SLCT_CD ,\n" +
                "                SUMMIT.MSTR ,\n" +
                "                UHELP.CMPGN_CD ,\n" +
                "                SUMMIT.FUND ,\n" +
                "                SUMMIT.GIFT\n" +
                "        WHERE\n" +
                "                (\n" +
                "                        SUMMIT.GIFT.GIFT_DATEDB >= '$From_Date$' AND SUMMIT.GIFT.GIFT_DATEDB <= '$To_Date$'\n" +
                "                )\n" +
                "        AND\n" +
                "        SUMMIT.FUND.FUND_ACCT = SUMMIT.GIFT.FUND_ACCT AND\n" +
                "        SUMMIT.GIFT.ID = SUMMIT.MSTR.ID AND\n" +
                "        SUMMIT.GIFT.SLCT_CD = UHELP.SLCT_CD.SLCT_CD AND\n" +
                "        SUMMIT.GIFT.GIFT_CMPGN_CD = UHELP.CMPGN_CD.CMPGN_CD AND\n" +
                "        SUMMIT.FUND.FUND_DEPT_ATTRB in ('$OUMI_Departments$')\n" +
                "\n" +
                "        and 0 =\n" +
                "\n" +
                "                coalesce((select sum(g.gift_amt)\n" +
                "                        FROM\n" +
                "                        SUMMIT.FUND f,\n" +
                "                        SUMMIT.GIFT g\n" +
                "                        WHERE\n" +
                "                                (\n" +
                "                                        g.GIFT_DATEDB < '$From_Date$'\n" +
                "                                )\n" +
                "                        AND\n" +
                "                        f.FUND_ACCT = g.FUND_ACCT AND\n" +
                "                        g.ID = SUMMIT.MSTR.ID AND\n" +
                "                        f.FUND_DEPT_ATTRB in ('$OUMI_Departments$')\n" +
                "                ),0)";

        String result = "SELECT\n" +
                "        SUMMIT.MSTR.ID AS \"ID\" ,\n" +
                "                SUMMIT.GIFT.GIFT_DATEDB AS \"SGF.GIFT_DATEDB\" ,\n" +
                "                SUMMIT.GIFT.GIFT_AMT AS \"SGF.GIFT_AMT\" ,\n" +
                "                SUMMIT.GIFT.PREM_VAL AS \"SGF.PREM_VAL\",\n" +
                "                SUMMIT.GIFT.GIFT_ACTG_AMT AS \"SGF.GIFT_ACTG_AMT\",\n" +
                "                SUMMIT.FUND.FUND_TITLE AS \"SDF.FUND_TITLE\"\n" +
                "        FROM\n" +
                "        UHELP.SLCT_CD ,\n" +
                "                SUMMIT.MSTR ,\n" +
                "                UHELP.CMPGN_CD ,\n" +
                "                SUMMIT.FUND ,\n" +
                "                SUMMIT.GIFT\n" +
                "        WHERE\n" +
                "                SUMMIT.FUND.FUND_ACCT = SUMMIT.GIFT.FUND_ACCT AND\n" +
                "        SUMMIT.GIFT.ID = SUMMIT.MSTR.ID AND\n" +
                "        SUMMIT.GIFT.SLCT_CD = UHELP.SLCT_CD.SLCT_CD AND\n" +
                "        SUMMIT.GIFT.GIFT_CMPGN_CD = UHELP.CMPGN_CD.CMPGN_CD\n" +
                "\n" +
                "        and 0 =\n" +
                "\n" +
                "                coalesce((select sum(g.gift_amt)\n" +
                "                        FROM\n" +
                "                        SUMMIT.FUND f,\n" +
                "                        SUMMIT.GIFT g\n" +
                "                        WHERE\n" +
                "                                f.FUND_ACCT = g.FUND_ACCT AND\n" +
                "                        g.ID = SUMMIT.MSTR.ID\n" +
                "                ),0)";

        ArrayList<String> rmvColumnListWithout$ = new ArrayList<String>();
        rmvColumnListWithout$.add("$From_Date$");
        rmvColumnListWithout$.add("$To_Date$");
        rmvColumnListWithout$.add("$OUMI_Departments$");

        EDbVendor vendor = TGSqlParser.getDBVendorByName("oracle");

        RemoveCondition rc = new RemoveCondition(sql,vendor,rmvColumnListWithout$);

        sql = rc.getRemoveResult();

        assertTrue(result.equals(sql));
    }
    public void test3() {

        String sql = "(\n" +
                "        SELECT\n" +
                "        u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', ' ' AS 'GROUP_NAME', 0 AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', u.DISABLE_ATTRB AS 'USER_DISABLE_ATTRB', ' ' as 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON u.USER_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE ft ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0 and u.USER_NAME in ('$User$') and u.DISABLE_ATTRB IN ('$UserDisableAttrb$') and case when su.DISPLAY is null then 'U' else su.DISPLAY end in ('$Function_Status$')\n" +
                "\n" +
                "        UNION\n" +
                "\n" +
                "        SELECT u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', g.GROUP_NAME AS 'GROUP_NAME', gu.PRIORITY AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', ' ' as 'USER_DISABLE_ATTRB', gu.DISABLE_ATTRB AS 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_GROUPINGS g ON u.USER_NAME=g.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USERS gu ON gu.USER_NAME=g.GROUP_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON g.GROUP_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE FT ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0 and gu.USER_TYPE=1 and u.user_name in ('$User$') and u.DISABLE_ATTRB IN ('$UserDisableAttrb$') and g.group_name is not null and case when su.DISPLAY is null then 'U' else su.DISPLAY end in ('$Function_Status$') and case when gu.DISABLE_ATTRB is null then 'N' else gu.DISABLE_ATTRB end in ('$GroupDisableAttrb$')\n" +
                "        )\n" +
                "        order by 1, 6, 7, 8, 13";

        String result = "(\n" +
                "        SELECT\n" +
                "        u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', ' ' AS 'GROUP_NAME', 0 AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', u.DISABLE_ATTRB AS 'USER_DISABLE_ATTRB', ' ' as 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON u.USER_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE ft ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0\n" +
                "\n" +
                "        UNION\n" +
                "\n" +
                "        SELECT u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', g.GROUP_NAME AS 'GROUP_NAME', gu.PRIORITY AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', ' ' as 'USER_DISABLE_ATTRB', gu.DISABLE_ATTRB AS 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_GROUPINGS g ON u.USER_NAME=g.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USERS gu ON gu.USER_NAME=g.GROUP_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON g.GROUP_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE FT ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0 and gu.USER_TYPE=1 and g.group_name is not null\n" +
                "        )\n" +
                "        order by 1, 6, 7, 8, 13";

        ArrayList<String> rmvColumnListWithout$ = new ArrayList<String>();
        rmvColumnListWithout$.add("$Function_Status$");
        rmvColumnListWithout$.add("$User$");
        rmvColumnListWithout$.add("$UserDisableAttrb$");
        rmvColumnListWithout$.add("$GroupDisableAttrb$");
        EDbVendor vendor = TGSqlParser.getDBVendorByName("mssql");

        RemoveCondition rc = new RemoveCondition(sql,vendor,rmvColumnListWithout$);

        sql = rc.getRemoveResult();

        assertTrue(result.equals(sql));
    }

    public void test4() {

        String sql = "(\n" +
                "        SELECT\n" +
                "        u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', ' ' AS 'GROUP_NAME', 0 AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', u.DISABLE_ATTRB AS 'USER_DISABLE_ATTRB', ' ' as 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON u.USER_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE ft ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0 and u.USER_NAME in ('$User$') and u.DISABLE_ATTRB IN ('$UserDisableAttrb$') and case when su.DISPLAY is null then 'U' else su.DISPLAY end in ('$Function_Status$')\n" +
                "\n" +
                "        UNION\n" +
                "\n" +
                "        SELECT u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', g.GROUP_NAME AS 'GROUP_NAME', gu.PRIORITY AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', ' ' as 'USER_DISABLE_ATTRB', gu.DISABLE_ATTRB AS 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_GROUPINGS g ON u.USER_NAME=g.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USERS gu ON gu.USER_NAME=g.GROUP_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON g.GROUP_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE FT ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0 and gu.USER_TYPE=1 and u.user_name in ('$User$') and u.DISABLE_ATTRB IN ('$UserDisableAttrb$') and g.group_name is not null and case when su.DISPLAY is null then 'U' else su.DISPLAY end in ('$Function_Status$') and case when gu.DISABLE_ATTRB is null then 'N' else gu.DISABLE_ATTRB end in ('$GroupDisableAttrb$')\n" +
                "        )\n" +
                "        order by 1, 6, 7, 8, 13";

        String result = "(\n" +
                "        SELECT\n" +
                "        u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', ' ' AS 'GROUP_NAME', 0 AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', u.DISABLE_ATTRB AS 'USER_DISABLE_ATTRB', ' ' as 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON u.USER_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE ft ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0\n" +
                "\n" +
                "        UNION\n" +
                "\n" +
                "        SELECT u.USER_NAME AS 'USER_NAME', u.DESCRIPTION AS 'DESCRIPTION', CASE WHEN u.USER_TYPE='1' THEN 'Y' ELSE 'N' END as 'IS_A_GROUP', g.GROUP_NAME AS 'GROUP_NAME', gu.PRIORITY AS 'PRIORITY', am.MODULE_NAME AS 'MODULE_NAME', sm.SUB_MODULE_NAME AS 'SUB_MODULE_NAME', mf.FUNCTION_NAME AS 'FUNCTION_NAME', ft.FNCTN_TYPE_DESC AS 'FNCTN_TYPE_DESC', case when su.DISPLAY='U' then 'Enabled' when su.DISPLAY='F' then 'Disabled' end as 'DISPLAY', ' ' as 'USER_DISABLE_ATTRB', gu.DISABLE_ATTRB AS 'GROUP_DISABLE_ATTRB', pl.PAGE_ID AS 'PAGE_ID'\n" +
                "        FROM SMMTSEC.SECURITY_USERS u\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_GROUPINGS g ON u.USER_NAME=g.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USERS gu ON gu.USER_NAME=g.GROUP_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.SECURITY_USER_PAGE su ON g.GROUP_NAME=su.USER_NAME\n" +
                "               LEFT OUTER JOIN SMMTSEC.FUNCTION_PAGE_LIST pl ON su.PAGE_ID = pl.PAGE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FUNCTIONS mf ON pl.FUNCTION_ID = mf.FUNCTION_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.SUB_MODULE sm ON sm.SUB_MODULE_ID = mf.SUB_MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.APP_MODULE am ON am.MODULE_ID = sm.MODULE_ID\n" +
                "               LEFT OUTER JOIN SMMTSEC.MODULE_FNCTN_TYPE FT ON mf.FNCTN_TYPE_ID = ft.FNCTN_TYPE_ID\n" +
                "        WHERE u.USER_TYPE = 0 and gu.USER_TYPE=1 and g.group_name is not null\n" +
                "        )\n" +
                "        order by 1, 6, 7, 8, 13";

        ArrayList<String> rmvColumnListWithout$ = new ArrayList<String>();
        rmvColumnListWithout$.add("$Function_Status$");
        rmvColumnListWithout$.add("$User$");
        rmvColumnListWithout$.add("$UserDisableAttrb$");
        rmvColumnListWithout$.add("$GroupDisableAttrb$");
        EDbVendor vendor = TGSqlParser.getDBVendorByName("oracle");

        RemoveCondition rc = new RemoveCondition(sql,vendor,rmvColumnListWithout$);

        sql = rc.getRemoveResult();

        assertTrue(result.equals(sql));
    }
}
