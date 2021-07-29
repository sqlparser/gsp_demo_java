package visitor.acceptChildren;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TIntoClause;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import junit.framework.TestCase;
import org.junit.Assert;

public class TestAcceptChildren extends TestCase {

    public void testIntoClause() {
        EDbVendor dbVendor = EDbVendor.dbvmssql;
        TGSqlParser sqlparser = new TGSqlParser(dbVendor);

        sqlparser.sqltext = "Select * Into new_table_name from old_table_name;";
        int ret = sqlparser.parse();
        Assert.assertEquals(0, ret);

        TCustomSqlStatement sqlStatement;
        for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
            sqlStatement = sqlparser.sqlstatements.get(i);
            sqlStatement.acceptChildren(new TParseTreeVisitor() {
                public void preVisit(TIntoClause clause) {
                    Assert.assertEquals(clause.toString(), "Into new_table_name");
                }
            });
        }
    }

}
