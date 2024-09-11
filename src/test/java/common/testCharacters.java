package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testCharacters extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION LIST_REP_FN(I_TMPID IN VARCHAR2) RETURN types.ref_cursor\n" +
                "AS ret_cur types.ref_cursor;\n" +
                "     v_sql VARCHAR2(3999);\n" +
                "BEGIN\n" +
                "\n" +
                "v_sql := ' SELECT '\n" +
                "            ||' TMPBR '\n" +
                "            ||' ,REPLACE(REPLACE(REPLACE(REPLACE(TMPCN,''｛'',''{''),''｝'',''}''),''，'','',''),''％'',''%'') AS TMPCN '\n" +
                "            ||' ,DISPBR '\n" +
                "            ||' FROM CUSTOMTMPLT '\n" +
                "            ||' WHERE TMPID LIKE '||chr(39)||I_TMPID||'%'||chr(39);\n" +
                "\n" +
                "    OPEN ret_cur FOR v_sql;\n" +
                "    RETURN ret_cur;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);
       // System.out.println(sqlparser.getSqlstatements().get(0).toString());
        assertTrue(sqlparser.getSqlstatements().get(0).toString().contains("''｛'',''{''),''｝'',''}''),''，'','',''),''％'',''%''"));
    }
}
