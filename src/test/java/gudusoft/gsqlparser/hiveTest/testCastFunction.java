package gudusoft.gsqlparser.hiveTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCastFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "create table nikesh.tournament_base_registration as\n" +
                "select distinct process_date,cast(userid as bigint) as user_id,\n" +
                "cast(channelid as int) as reg_channel_id,\n" +
                "cast(str2['ptype'] as int) as ptype,\n" +
                "cast(str2['pkr'] as int) as pkr,\n" +
                "cast(str4['tid'] as bigint) as tid,\n" +
                "str4['trnmnt_name'] as trnmnt_name,\n" +
                "cast(str5['join_via'] as int)  as join_via,\n" +
                "cast(from_unixtime(unix_timestamp(timestamp,'yyyy-MM-dd hh:mm:ss')) as timestamp)  as time_of_reg,\n" +
                "\n" +
                "case when str5['join_type'] = 'Join' then 0\n" +
                " when str5['join_type'] = 'Waitlist' then 1 end as wait_list_ind\n" +
                "\n" +
                "from taxonomy.gameplay a , nikesh.tournament_base b\n" +
                "where cast(str4['tid'] as bigint) = b.id and process_date between date_sub('##v_start_date',60) and '##v_end_date'\n" +
                "and event in ('join_click_response') and str5['response_type'] = 'Success';";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = createTableSqlStatement.getSubQuery();
        assertTrue(select.getResultColumnList().getResultColumn(2).toString().equalsIgnoreCase("cast(channelid as int)"));
        assertTrue(select.getResultColumnList().getResultColumn(2).getAliasClause().toString().equalsIgnoreCase("reg_channel_id"));

    }

}
