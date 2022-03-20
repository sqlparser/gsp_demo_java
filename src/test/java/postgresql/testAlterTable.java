package postgresql;
/*
 * Date: 13-8-2
 */

import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EConstraintType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAlterTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "ALTER TABLE test\n" +
                "    ADD CONSTRAINT pkey PRIMARY KEY (id);";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alter = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        TAlterTableOption alterTableOption = alter.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddConstraint);

        TConstraint constraint = alterTableOption.getTableConstraint();
        assertTrue(constraint.getConstraint_type() == EConstraintType.primary_key );
        assertTrue(constraint.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("id"));
    }

    public void testOwner(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "ALTER TABLE public.test OWNER TO bigking;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alter = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alter.getTableName().toString().equalsIgnoreCase("public.test"));
        TAlterTableOption alterTableOption = alter.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.ownerTo);
        assertTrue(alterTableOption.getNewOwnerName().toString().equalsIgnoreCase("bigking"));
    }

}
