package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSparkSQL extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvsparksql);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testColumnArray() {
        doTest("select distinct process_date, \n" +
                        "cast(str2['ptype'] as int) as ptype, \n" +
                        "cast(str2['pkr'] as int) as pkr, \n" +
                        "cast(str2['maxplayers'] as bigint) as maxplayers, \n" +
                        "cast(str2['entry_type'] as int) as entry_type, \n" +
                        "cast(str2['efee'] as int) as efee, \n" +
                        "str4['trnmnt_name'] as trnmnt_name, \n" +
                        "cast(from_unixtime(cast(cast(str4['starttime_tid'] as bigint)/1000 as bigint)) as timestamp) as starttime_tid, \n" +
                        "cast(from_unixtime(cast(cast(str4['endtime_tid'] as bigint)/1000 as bigint)) as timestamp) as endtime_tid, \n" +
                        "cast(from_unixtime(cast(cast(str4['regstarttime'] as bigint)/1000 as bigint)) as timestamp) as regstartime, \n" +
                        "cast(from_unixtime(cast(cast(str4['regendtime'] as bigint)/1000 as bigint)) as timestamp) as regendtime, \n" +
                        "cast(str4['tid'] as bigint) as tid, \n" +
                        "cast(str4['no_of_rounds'] as int) as no_of_rounds, \n" +
                        "cast(str4['no_of_joined'] as int) as no_of_joined, \n" +
                        "cast(str4['no_of_waitlisted'] as int) as no_of_waitlisted, \n" +
                        "cast(str4['no_of_withdraw'] as int) as no_of_withdraw, \n" +
                        "str5['trnmnt_type'] as trnmnt_type, \n" +
                        "cast(str5['no_of_parents'] as int) as no_of_parents, \n" +
                        "cast(str5['rebuy_allowed'] as int) as rebuy_allowed, \n" +
                        "cast(str5['starting_chipstack'] as bigint) as starting_chipstack, \n" +
                        "cast(str6['service_fee'] as decimal(10,2)) as service_fee, \n" +
                        "str6['parent_info'] as parent_info, \n" +
                        "str6['round_info'] as round_info, \n" +
                        "str7['announced_cash_prize_pool'] as announced_cash_prize_pool, \n" +
                        "str7['announced_ticket_prize_pool'] as announced_ticket_prize_pool, \n" +
                        "str7['actual_cash_prize_pool'] as actual_cash_prize_pool \n" +
                        "from gameplay where process_date between to_date('2021-01-26') and to_date('2021-01-27') \n" +
                        "and event in ('tournament_complete') and cast(str2['pkr'] as int) = 7;",
                "Tables:\n" +
                        "gameplay\n" +
                        "\n" +
                        "Fields:\n" +
                        "gameplay.event\n" +
                        "gameplay.process_date\n" +
                        "gameplay.str2\n" +
                        "gameplay.str4\n" +
                        "gameplay.str5\n" +
                        "gameplay.str6\n" +
                        "gameplay.str7");
    }
}
