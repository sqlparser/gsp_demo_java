package visitor.acceptChildren;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.hana.TTimeTravel;
import gudusoft.gsqlparser.nodes.hive.THiveHintClause;
import gudusoft.gsqlparser.nodes.hive.THiveTransformClause;
import gudusoft.gsqlparser.nodes.mssql.TOptionClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;
import org.junit.Assert;

public class TestAcceptChildren extends TestCase {

    public void testHiveHintClause() {
        EDbVendor dbVendor = EDbVendor.dbvhive;
        TGSqlParser sqlparser = new TGSqlParser(dbVendor);

        sqlparser.sqltext = "select /*+ MAPJOIN(time_dim) */ count(*) from\n" +
                "store_sales join time_dim on (ss_sold_time_sk = t_time_sk);";
        int ret = sqlparser.parse();
        Assert.assertEquals(0, ret);

        TSelectSqlStatement sqlStatement;
        for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
            sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(i);
            sqlStatement.acceptChildren(new TParseTreeVisitor() {
                public void preVisit(THiveHintClause clause) {
                    Assert.assertEquals(clause.toString(), "/*+ MAPJOIN(time_dim) */");
                }
            });
        }
    }
//
//    public void testTransformClause() {
//        EDbVendor dbVendor = EDbVendor.dbvhive;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "SELECT  \n" +
//                "  TRANSFORM (foo, bar)  \n" +
//                "  USING 'python add_mapper.py'  \n" +
//                "  AS (foo string, bar map<string,int>)  \n" +
//                "FROM t3;  \n";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(THiveTransformClause clause) {
//                Assert.assertEquals(clause.toString(), "(foo, bar)  \n" +
//                        "  USING 'python add_mapper.py'  \n" +
//                        "  AS (foo string, bar map<string,int>)");
//            }
//        });
//    }
//
//    public void testIntoClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmssql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "Select * Into new_table_name from old_table_name;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TIntoClause clause) {
//                Assert.assertEquals(clause.toString(), "Into new_table_name");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testSelectDistinctClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmysql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select DISTINCT(c) from t;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TSelectDistinct clause) {
//                Assert.assertEquals(clause.toString(), "DISTINCT(c)");
//            }
//        });
//    }
//
//    public void testJoinsClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmysql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select a.* from t1 as a inner join t2 as b on a.id = b.id;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TJoin clause) {
//                Assert.assertEquals(clause.toString(), "t1 as a inner join t2 as b on a.id = b.id");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testSampleClause() {
//        EDbVendor dbVendor = EDbVendor.dbvoracle;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select count(*) from t1 sample (10);";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//
//        Assert.assertNotNull(sqlStatement.getSampleClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TSampleClause clause) {
//                Assert.assertEquals(clause.toString(), "sample (10)");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testTeradataWithClause() {
//        EDbVendor dbVendor = EDbVendor.dbvteradata;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "with RECURSIVE c(n) as (select 1   union all select n + 1 from c where n < 10)  select n from c;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getTeradataWithClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TTeradataWithClause clause) {
//                Assert.assertEquals(clause.toString(), "c(n) as (select 1   union all select n + 1 from c where n < 10)");
//            }
//        });
//    }
//
//
//    public void testLockingClauses() {
//        EDbVendor dbVendor = EDbVendor.dbvpostgresql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select empno,ename,job,mgr,sal from emp,dept where emp.deptno=dept.deptno and empno=7369 for update of emp.empno;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TLockingClause clause) {
//                Assert.assertEquals(clause.toString(), "for update of emp.empno");
//            }
//        });
//    }
//
//    public void testSortBy() {
//        EDbVendor dbVendor = EDbVendor.dbvhive;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select * from stu sort by gradedesc;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TSortBy clause) {
//                Assert.assertEquals(clause.toString(), "sort by gradedesc");
//            }
//        });
//    }
//
//    public void testClusterBy() {
//        EDbVendor dbVendor = EDbVendor.dbvhive;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select * from stu cluster by class;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TClusterBy clause) {
//                Assert.assertEquals(clause.toString(), "cluster by class");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testWindowClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmysql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "SELECT\n" +
//                "  DISTINCT year, country,\n" +
//                "  FIRST_VALUE(year) OVER (w ORDER BY year ASC) AS first,\n" +
//                "  FIRST_VALUE(year) OVER (w ORDER BY year DESC) AS last\n" +
//                "FROM sales\n" +
//                "WINDOW w AS (PARTITION BY country);\n";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TWindowClause clause) {
//                Assert.assertEquals(clause.toString(), "WINDOW w AS (PARTITION BY country)");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testIntoTableClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmssql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select * Into new_table_name from old_table_name ;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getIntoTableClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TIntoTableClause clause) {
//                Assert.assertEquals(clause.toString(), "cluster by class");
//            }
//        });
//    }
//
//
//    public void testDistributeBy() {
//        EDbVendor dbVendor = EDbVendor.dbvhive;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select * from stu distribute by class sort by grade ;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TDistributeBy clause) {
//                Assert.assertEquals(clause.toString(), "distribute by class");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testIsolationClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmysql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select @@global.tx_isolation;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getIsolationClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TIsolationClause clause) {
//                Assert.assertEquals(clause.toString(), "@@global");
//            }
//        });
//    }
//
//    public void testOptionClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmssql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "SELECT a.keyId,a.info, SUM(a.qty) AS qtyAll  \n" +
//                "FROM  [maomao365.com_A]  a  \n" +
//                "WHERE qty >1    \n" +
//                "GROUP BY a.keyId,a.info \n" +
//                "ORDER BY a.keyId,a.info\n" +
//                "OPTION (HASH GROUP,FAST 10);";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TOptionClause clause) {
//                Assert.assertEquals(clause.toString(), "OPTION (HASH GROUP,FAST 10)");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testOffsetClause() {
//        EDbVendor dbVendor = EDbVendor.dbvmysql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "SELECT * from t limit 0 offset 1";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getOffsetClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TOffsetClause clause) {
//                Assert.assertEquals(clause.toString(), "offset 1");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testFetchFirstClause() {
//        EDbVendor dbVendor = EDbVendor.dbvpostgresql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "SELECT *\n" +
//                "FROM t\n" +
//                "ORDER BY score DESC\n" +
//                "FETCH FIRST 2 ROWS ONLY;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getFetchFirstClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TFetchFirstClause clause) {
//                Assert.assertEquals(clause.toString(), "FETCH FIRST 2");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testTimeTravel() {
//        EDbVendor dbVendor = EDbVendor.dbvmysql;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "select count(1) from BK_KHXX where DateDiff(dd,CreateTime,getdate())=0";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getTimeTravel());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(TTimeTravel clause) {
//                Assert.assertEquals(clause.toString(), "DateDiff(dd,CreateTime,getdate())");
//            }
//        });
//    }
//
//    // TODO 需改进
//    public void testHintClause() {
//        EDbVendor dbVendor = EDbVendor.dbvoracle;
//        TGSqlParser sqlparser = new TGSqlParser(dbVendor);
//
//        sqlparser.sqltext = "SELECT /*+ INDEX_DESC(e emp_name_ix) */ *   FROM employees e;";
//        int ret = sqlparser.parse();
//        Assert.assertEquals(0, ret);
//
//        TSelectSqlStatement sqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
//        Assert.assertNotNull(sqlStatement.getHintClause());
//        sqlStatement.acceptChildren(new TParseTreeVisitor() {
//            public void preVisit(THintClause clause) {
//                Assert.assertEquals(clause.toString(), "/*+ INDEX_DESC(e emp_name_ix) */");
//            }
//        });
//    }
}