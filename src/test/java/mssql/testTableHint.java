package mssql;
/*
 * Date: 12-4-13
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableHint;

import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUseDatabase;
import junit.framework.TestCase;

public class testTableHint extends TestCase {

        public void test2(){

                TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
                sqlparser.sqltext = "SELECT * FROM dbo.Employees WITH (NOLOCK) WHERE EmployeeID = 16000";
                assertTrue(sqlparser.parse() == 0);

                TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
                TTable table = select.tables.getTable(0);
                //System.out.println(table.getTableType());
                assertTrue(table.getTableType() == ETableSource.objectname);

                sqlparser.sqltext = "SELECT * FROM dbo.Employees (NOLOCK) WHERE EmployeeID = 16000";
                assertTrue(sqlparser.parse() == 0);

                select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
                table = select.tables.getTable(0);
               // System.out.println(table.getTableType());
                assertTrue(table.getTableType() == ETableSource.objectname);
        }


    StringBuffer sb = new StringBuffer(1024);

    protected void analyzeStmt(TCustomSqlStatement stmt){
        for(int i=0;i<stmt.tables.size();i++){
            TTable table = stmt.tables.getTable(i);
            if (table.isBaseTable())
            {

                if ( (stmt.dbvendor == EDbVendor.dbvmssql)
                        &&( (table.getFullName().equalsIgnoreCase("deleted"))
                            ||(table.getFullName().equalsIgnoreCase("inserted"))
                           )
                  ){
                    continue;
                }

                if (table.getTableHintList() == null){
                    //System.out.printf("No hint,table: %s\n",table.getFullName());
                    sb.append(String.format("No hint,table: %s\n",table.getFullName()));
                }else {
                    for(int j=0;j<table.getTableHintList().size();j++){
                        TTableHint tableHint = table.getTableHintList().getElement(j);
                       // System.out.printf("Hint: %s: table: %s\n",tableHint.toString(), table.getFullName());
                        sb.append(String.format("Hint: %s: table: %s\n",tableHint.toString(), table.getFullName()));
                    }
                }

            }

        }

        for (int i=0;i<stmt.getStatements().size();i++){
           analyzeStmt(stmt.getStatements().get(i));
        }
    }

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT x.job_seeker_id, \n" +
                "       x.job_posting_id, \n" +
                "       x.job_seeker_ref, \n" +
                "       x.supplier_code, \n" +
                "       x.status, \n" +
                "       x.submit_time, \n" +
                "       (SELECT z.supplier_name \n" +
                "        FROM   dbo.Get_supplier_name ('user_type', 'suppress_supplier_flag', \n" +
                "               'role_flag', \n" +
                "                       x.jp_status, 'msp_coordinator_flag', x.supplier_name) AS \n" +
                "               z) AS \n" +
                "       supplier_name, \n" +
                "       x.work_order_id, \n" +
                "       x.work_order_ref, \n" +
                "       x.score, \n" +
                "       x.job_seeker_name, \n" +
                "       ( CASE \n" +
                "           WHEN x.duplicate != 0 THEN 'label.possibleDuplicateSubmittalCode ' \n" +
                "           ELSE '' \n" +
                "         END ) + ( CASE \n" +
                "                     WHEN x.potential_match != 0 THEN \n" +
                "                     'label.possibleMatchToWorkerCode ' \n" +
                "                     ELSE '' \n" +
                "                   END ) + ( CASE \n" +
                "                               WHEN x.pendingapproval != 0 THEN \n" +
                "                               'label.pendingApprovalCode ' \n" +
                "                               ELSE '' \n" +
                "                             END ) + ( CASE \n" +
                "                                         WHEN x.pendingprequalification != 0 \n" +
                "                                       THEN \n" +
                "                                         'label.pendingPreQualificationCode ' \n" +
                "                                         ELSE '' \n" +
                "                                       END ) + ( CASE \n" +
                "       WHEN x.prequalified != 0 THEN 'label.preQualifiedCode ' \n" +
                "       ELSE '' \n" +
                "                                                 END ) + ( CASE \n" +
                "       WHEN x.rejected != 0 THEN 'label.rejectedCode ' \n" +
                "       ELSE '' \n" +
                "                                                           END ) + \n" +
                "       ( CASE \n" +
                "           WHEN x.donothireflag != 0 THEN 'label.possibleDoNotHireCode ' \n" +
                "           ELSE '' \n" +
                "         END ) \n" +
                "       AS flag, \n" +
                "       x.buyer_supplier_contract_id, \n" +
                "       x.currency, \n" +
                "       Rtrim(CONVERT(DECIMAL(15, 2), x.strate)) \n" +
                "       AS strate \n" +
                "FROM   (SELECT js.job_seeker_id, \n" +
                "               js.job_posting_id, \n" +
                "               js.job_seeker_ref, \n" +
                "               js.supplier_code, \n" +
                "               CASE \n" +
                "                 WHEN js.status = 6 THEN 88 \n" +
                "                 ELSE js.status \n" +
                "               END \n" +
                "                      AS status, \n" +
                "               js.submit_time, \n" +
                "               cn.name \n" +
                "                      AS supplier_name, \n" +
                "               wo.work_order_id \n" +
                "                      AS work_order_id, \n" +
                "               wo.work_order_ref \n" +
                "                      AS work_order_ref, \n" +
                "               CONVERT(DECIMAL(15, 2), (js.score_from_cost + \n" +
                "               js.score_from_availability \n" +
                "                      + \n" +
                "               js.score_from_qualification )) \n" +
                "                      AS score, \n" +
                "               Isnull((SELECT COUNT(1) \n" +
                "                       FROM   dbo.security_id_mapping wk (nolock) \n" +
                "                              LEFT JOIN dbo.job_seeker jsd (nolock) \n" +
                "                                ON wk.job_seeker_id = jsd.job_seeker_id \n" +
                "                       WHERE  wk.job_seeker_id != js.job_seeker_id \n" +
                "                              AND ( EXISTS(SELECT 1 \n" +
                "                                           FROM   dbo.security_grou1p (nolock) \n" +
                "                                                  sg1 \n" +
                "                                                  INNER JOIN dbo.security_grou2p \n" +
                "                                                             ( \n" +
                "                                                             nolock) sg2 \n" +
                "                                                    ON sg1.security_group_id = \n" +
                "                                                       sg2.security_group_id \n" +
                "                                                       AND sg1.buyer_code = \n" +
                "                                                           wk.buyer_code \n" +
                "                                                       AND sg2.buyer_code = \n" +
                "                                                           js.buyer_code) \n" +
                "                                     OR wk.buyer_code = js.buyer_code ) \n" +
                "                              AND wk.job_posting_id != js.job_posting_id \n" +
                "                              AND ( ( wk.security_id != '' \n" +
                "                                      AND wk2.security_id != '' \n" +
                "                                      AND wk.security_id = wk2.security_id ) \n" +
                "                                     OR ( wk2.last_name = wk.last_name \n" +
                "                                          AND wk2.first_name = wk.first_name ) ) \n" +
                "                              AND wk.draft_flag = 0 \n" +
                "                              AND ( 'job_seeker_visibility_flag' = 0 \n" +
                "                                     OR ( wk.draft_flag = 0 \n" +
                "                                          AND ( ( 'job_seeker_visibility_flag' = \n" +
                "                                                  1 \n" +
                "                                                  AND jsd.status NOT IN ( \n" +
                "                                                      'job_seeker_status' ) \n" +
                "                                                ) \n" +
                "                                                 OR 'person_id' = \n" +
                "                                                    Isnull(wk.coordinator_id, '' \n" +
                "                                                    ) \n" +
                "                                                 OR 'person_id' = \n" +
                "                                                    Isnull(wk.distributor_id, '' \n" +
                "                                                    ) \n" +
                "                                                 OR 1 = 'msp_coordinator_flag' ) \n" +
                "                                        ) )), \n" +
                "               0) AS \n" +
                "               potential_match, \n" +
                "               Isnull((SELECT TOP (1) 1 \n" +
                "                       FROM   dbo.security_id_mapping wk (nolock) \n" +
                "                              LEFT JOIN dbo.job_seeker jsd (nolock) \n" +
                "                                ON wk.job_seeker_id = jsd.job_seeker_id \n" +
                "                       WHERE  wk.job_posting_id = js.job_posting_id \n" +
                "                              AND ( ( wk.security_id != '' \n" +
                "                                      AND wk2.security_id != '' \n" +
                "                                      AND wk.security_id = wk2.security_id ) \n" +
                "                                     OR ( wk2.last_name = wk.last_name \n" +
                "                                          AND wk2.first_name = wk.first_name ) ) \n" +
                "                              AND wk.draft_flag = 0 \n" +
                "                              AND wk.job_seeker_id != js.job_seeker_id \n" +
                "                              AND ( 'job_seeker_visibility_flag' = 0 \n" +
                "                                     OR ( wk.draft_flag = 0 \n" +
                "                                          AND ( ( 'job_seeker_visibility_flag' = \n" +
                "                                                  1 \n" +
                "                                                  AND jsd.status NOT IN ( \n" +
                "                                                      'job_seeker_status' ) \n" +
                "                                                ) \n" +
                "                                                 OR 'person_id' = \n" +
                "                                                    Isnull(wk.coordinator_id, '' \n" +
                "                                                    ) \n" +
                "                                                 OR 'person_id' = \n" +
                "                                                    Isnull(wk.distributor_id, '' \n" +
                "                                                    ) \n" +
                "                                                 OR 1 = 'msp_coordinator_flag' ) \n" +
                "                                        ) )), \n" +
                "               0) AS \n" +
                "               duplicate, \n" +
                "               CASE \n" +
                "                 WHEN \n" +
                "       dbo.Show_candidate_name('config_enabled_allow_candidate_anonymity', \n" +
                "       js.status) = \n" +
                "       1 THEN js.display_name \n" +
                "                 ELSE '-' \n" +
                "               END \n" +
                "                      AS job_seeker_name, \n" +
                "               Isnull((SELECT TOP (1) 1 \n" +
                "                       FROM   dbo.workforce_template wft (nolock), \n" +
                "                              dbo.job_posting jp (nolock) \n" +
                "                       WHERE  js.workforce_id = wft.workforce_id \n" +
                "                              AND jp.job_posting_id = js.job_posting_id \n" +
                "                              AND jp.job_template_id = wft.job_template_id \n" +
                "                              AND wft.status = 31), 0) \n" +
                "                      AS pendingapproval, \n" +
                "               Isnull((SELECT TOP (1) 1 \n" +
                "                       FROM   dbo.workforce_template wft (nolock), \n" +
                "                              dbo.job_posting jp (nolock) \n" +
                "                       WHERE  js.workforce_id = wft.workforce_id \n" +
                "                              AND jp.job_posting_id = js.job_posting_id \n" +
                "                              AND jp.job_template_id = wft.job_template_id \n" +
                "                              AND wft.status = 82), 0) \n" +
                "                      AS pendingprequalification, \n" +
                "               Isnull((SELECT TOP (1) 1 \n" +
                "                       FROM   dbo.workforce_template wft (nolock), \n" +
                "                              dbo.job_posting jp (nolock) \n" +
                "                       WHERE  js.workforce_id = wft.workforce_id \n" +
                "                              AND jp.job_posting_id = js.job_posting_id \n" +
                "                              AND jp.job_template_id = wft.job_template_id \n" +
                "                              AND wft.status = 83), 0) \n" +
                "                      AS prequalified, \n" +
                "               Isnull((SELECT TOP (1) 1 \n" +
                "                       FROM   dbo.workforce_template wft (nolock), \n" +
                "                              dbo.job_posting jp (nolock) \n" +
                "                       WHERE  js.workforce_id = wft.workforce_id \n" +
                "                              AND jp.job_posting_id = js.job_posting_id \n" +
                "                              AND jp.job_template_id = wft.job_template_id \n" +
                "                              AND wft.status = 5), 0) \n" +
                "                      AS rejected, \n" +
                "               Isnull((SELECT TOP (1) 1 \n" +
                "                       FROM   dbo.security_id_mapping wk (nolock), \n" +
                "                              dbo.buyer_supplier_contract bsc WITH(nolock) \n" +
                "                       WHERE  bsc.buyer_code = wk.buyer_code \n" +
                "                              AND bsc.supplier_code = wk.supplier_code \n" +
                "                              AND bsc.active_flag = 1 \n" +
                "                              AND ( EXISTS(SELECT 1 \n" +
                "                                           FROM   dbo.security_group (nolock) \n" +
                "                                                  sg1 \n" +
                "                                                  INNER JOIN dbo.security_group \n" +
                "                                                             (nolock \n" +
                "                                                             ) sg2 \n" +
                "                                                    ON sg1.security_group_id = \n" +
                "                                                       sg2.security_group_id \n" +
                "                                                       AND sg1.buyer_code = \n" +
                "                                                           wk.buyer_code \n" +
                "                                                       AND sg2.buyer_code = \n" +
                "                                                           js.buyer_code) \n" +
                "                                     OR wk.buyer_code = js.buyer_code ) \n" +
                "                              AND js.job_seeker_id != wk.job_seeker_id \n" +
                "                              AND wk.rehire_flag = 0 \n" +
                "                              AND ( ( wk.security_id != '' \n" +
                "                                      AND wk2.security_id != '' \n" +
                "                                      AND wk.security_id = wk2.security_id ) \n" +
                "                                     OR ( wk2.last_name = wk.last_name \n" +
                "                                          AND wk2.first_name = wk.first_name ) ) \n" +
                "                              AND 1 = 'doNotHireFlag'), 0) \n" +
                "                      AS donothireflag, \n" +
                "               js.status \n" +
                "                      AS jp_status, \n" +
                "               bsco.buyer_supplier_contract_id, \n" +
                "               js.currency, \n" +
                "               jsrate.strate \n" +
                "        FROM   (((((dbo.job_seeker js (nolock) \n" +
                "                    INNER JOIN dbo.security_id_mapping wk2 (nolock) \n" +
                "                      ON wk2.job_seeker_id = js.job_seeker_id \n" +
                "                    INNER JOIN dbo.job_posting_view jp (nolock) \n" +
                "                      ON jp.job_posting_id = js.job_posting_id) \n" +
                "                   LEFT JOIN dbo.work_order wo (nolock) \n" +
                "                     ON wo.job_seeker_id = js.job_seeker_id \n" +
                "                        AND wo.SEQUENCE = 1) \n" +
                "                  LEFT JOIN dbo.buyer_supplier_contract bsco (nolock) \n" +
                "                    ON bsco.buyer_code = jp.buyer_code \n" +
                "                       AND bsco.supplier_code = js.supplier_code \n" +
                "                       AND bsco.active_flag = 1) \n" +
                "                 INNER JOIN dbo.company_name cn (nolock) \n" +
                "                   ON js.supplier_code = cn.company_code \n" +
                "                      AND CONVERT(DATETIME, \n" +
                "                          CONVERT(VARCHAR(12), Isnull(js.submit_time, \n" +
                "                          Getdate \n" +
                "                          ( \n" +
                "                          ) \n" +
                "                          ))) \n" +
                "                          BETWEEN cn.start_date AND cn.end_date) \n" +
                "                LEFT JOIN (SELECT ( rate )          AS strate, \n" +
                "                                  js1.job_seeker_id AS job_seeker_id \n" +
                "                           FROM   dbo.job_seeker_rate jsr(nolock), \n" +
                "                                  dbo.job_seeker js1(nolock), \n" +
                "                                  dbo.rate_code(nolock) \n" +
                "                           WHERE  js1.job_seeker_id = jsr.job_seeker_id \n" +
                "                                  AND rate_code.rate_code_id = jsr.rate_code_id \n" +
                "                                  AND rate_code.rate_unit = 'Hr' \n" +
                "                                  AND jsr.rate_category_id = \n" +
                "                                      js1.buyer_code + 'ST') AS \n" +
                "                          jsrate \n" +
                "                  ON js.job_seeker_id = jsrate.job_seeker_id) \n" +
                "        WHERE  js.job_posting_id = 'job_posting_id' \n" +
                "               AND js.status NOT IN ( 0, 17, 20 ) \n" +
                "               AND ( 'job_seeker_visibility_flag' = 0 \n" +
                "                      OR ( 'job_seeker_visibility_flag' = 1 \n" +
                "                           AND js.status NOT IN ( 'job_seeker_status' ) ) \n" +
                "                      OR 'person_id' = jp.coordinator_id \n" +
                "                      OR 'person_id' = jp.distributor_id \n" +
                "                      OR EXISTS (SELECT 'x' \n" +
                "                                 FROM   dbo.person (nolock) \n" +
                "                                 WHERE  person_id = 'person_id' \n" +
                "                                        AND msp_coordinator_flag = 1) ) \n" +
                "               AND EXISTS (SELECT 1 \n" +
                "                           FROM   dbo.job_posting_distribution jpd (nolock) \n" +
                "                           WHERE  jpd.job_posting_id = jp.job_posting_id \n" +
                "                                  AND jpd.supplier_code = js.supplier_code))AS x \n" +
                "ORDER  BY x.submit_time ";
        assertTrue(sqlparser.parse() == 0);

       // StringBuffer sb = new StringBuffer(1024);


       for (int i=0;i<sqlparser.sqlstatements.size();i++){
           analyzeStmt(sqlparser.sqlstatements.get(i));
       }

       // System.out.println(sqlparser.sqltext);


        assertTrue(sb.toString().trim().equalsIgnoreCase("Hint: nolock: table: dbo.job_seeker\n" +
                "Hint: nolock: table: dbo.security_id_mapping\n" +
                "Hint: nolock: table: dbo.job_posting_view\n" +
                "Hint: nolock: table: dbo.work_order\n" +
                "Hint: nolock: table: dbo.buyer_supplier_contract\n" +
                "Hint: nolock: table: dbo.company_name\n" +
                "Hint: nolock: table: dbo.job_seeker_rate\n" +
                "Hint: nolock: table: dbo.job_seeker\n" +
                "Hint: nolock: table: dbo.rate_code\n" +
                "Hint: nolock: table: dbo.security_id_mapping\n" +
                "Hint: nolock: table: dbo.job_seeker\n" +
                "Hint: nolock: table: dbo.security_grou1p\n" +
                "Hint: nolock: table: dbo.security_grou2p\n" +
                "Hint: nolock: table: dbo.security_id_mapping\n" +
                "Hint: nolock: table: dbo.job_seeker\n" +
                "Hint: nolock: table: dbo.workforce_template\n" +
                "Hint: nolock: table: dbo.job_posting\n" +
                "Hint: nolock: table: dbo.workforce_template\n" +
                "Hint: nolock: table: dbo.job_posting\n" +
                "Hint: nolock: table: dbo.workforce_template\n" +
                "Hint: nolock: table: dbo.job_posting\n" +
                "Hint: nolock: table: dbo.workforce_template\n" +
                "Hint: nolock: table: dbo.job_posting\n" +
                "Hint: nolock: table: dbo.security_id_mapping\n" +
                "Hint: nolock: table: dbo.buyer_supplier_contract\n" +
                "Hint: nolock: table: dbo.security_group\n" +
                "Hint: nolock: table: dbo.security_group\n" +
                "Hint: nolock: table: dbo.person\n" +
                "Hint: nolock: table: dbo.job_posting_distribution"));

    }
}
