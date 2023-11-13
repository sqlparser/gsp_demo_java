package sparksql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TLateralView;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

import java.util.ArrayList;

public class testLateralView extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsparksql);
        sqlparser.sqltext = "insert into dwd_db.pty_cf_fin partition (sys_rs_id)\n" +
                "  (com_id, --机构编号\n" +
                "   pub_dt, --报告日期\n" +
                "   rept_merg_type, --报表合并类型\n" +
                "   acc_crse_no, --会计科目编号\n" +
                "   acc_crse_val, --会计科目值\n" +
                "   busi_date, --业务日期\n" +
                "   task_rs_id, --任务来源标识\n" +
                "   dm_created_time, --数据中台创建时间\n" +
                "   sys_rs_id --系统来源标识\n" +
                "   )\n" +
                "\n" +
                "select a.compcode as com_id,\n" +
                "       cast(a.enddate as int) as pub_dt,\n" +
                "       a.rept_merg_type, --1-合并，2-母公司\n" +
                "       k1 as acc_crse_no,\n" +
                "       v1 as acc_crse_val,\n" +
                "\t   date_format(current_timestamp,'yyyyMMdd') as BUSI_DATE , --业务日期\n" +
                "\t   'pty_bnk_cf_fin.hql' as task_rs_id,--任务来源标识\n" +
                "\t   current_timestamp() as dm_created_time, --数据中台创建时间\n" +
                "\t   'FC' as sys_rs_id--系统来源标识\n" +
                "  from (select t1.compcode,\n" +
                "               t1.begindate,\n" +
                "               t1.enddate,\n" +
                "               t1.rept_merg_type,\n" +
                "               t1.accstacode\n" +
                "          from (select case\n" +
                "\t\t\t\t\t\twhen t1.reporttype in ('1', '3') then '1'\n" +
                "\t\t\t\t\t\twhen t1.reporttype in ('2', '4') then '2'\n" +
                "\t\t\t\t\t    end as rept_merg_type,\n" +
                "\t\t\t\t\t\tt1.*,\n" +
                "\t\t\t\t\t\trow_number() over(partition by t1.compcode, \n" +
                "                                      t1.begindate, \n" +
                "                                      t1.enddate,\n" +
                "                                      case when t1.reporttype in ('1','3') then '1'\n" +
                "                                        when t1.reporttype in ('2','4') then '2' end, \n" +
                "                                      t1.accstacode order by t1.reporttype desc) as rn\n" +
                "\t\t\t\t from ods_db.fc_tq_fin_probcfstatementnew t1) t1\n" +
                "        ) a lateral view explode(col1) b as k1, v1\n" +
                "\t   where v1 is not null";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        lateralViewVisitor lvVisitor = new lateralViewVisitor();
        insert.acceptChildren(lvVisitor);
        assertTrue(lvVisitor.columnAlias.size() == 2);
    }
}

class lateralViewVisitor extends TParseTreeVisitor {
    ArrayList<String> columnAlias = new ArrayList<>();

    public void preVisit(TLateralView node){
        //System.out.println(node.getUdtf().toString());
        for(TObjectName on:node.getColumnAliasList()){
            columnAlias.add(on.toString());
        }
    }
}

