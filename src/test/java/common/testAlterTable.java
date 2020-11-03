package test;
/*
 * Date: 11-4-14
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import junit.framework.TestCase;

public class testAlterTable extends TestCase {

    public void testMySQLAfterColumn(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "alter table qatest.movies ADD COLUMN viewCount SMALLINT AFTER description;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("qatest.movies"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(ato.getColumnPosition() == TAlterTableOption.COLUMN_POSITION_AFTER);
        assertTrue(ato.getAfterColumnName().toString().equalsIgnoreCase("description"));
    }

    public void testNetezzaSetPrivileges(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "ALTER TABLE distributors SET PRIVILEGES TO suppliers";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("distributors"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.setPrivileges);
        assertTrue(ato.getNewTableName().toString().equalsIgnoreCase("suppliers"));
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER TABLE \"ACLIPPROTOCOLMAPTEMPLATE\" ADD PRIMARY KEY (\"ID\") ENABLE;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("\"ACLIPPROTOCOLMAPTEMPLATE\""));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddConstraint);
        TConstraint c = (TConstraint) ato.getConstraintList().getConstraint(0);
        assertTrue(c.getConstraint_type() == EConstraintType.primary_key);

        assertTrue(c.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("\"ID\""));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER TABLE \"ACLIPPROTOCOLMAPTEMPLATE\" MODIFY (\"RULEACTION\" NOT NULL ENABLE);";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("\"ACLIPPROTOCOLMAPTEMPLATE\""));
        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);

        assertTrue(ato.getOptionType() == EAlterTableOptionType.ModifyColumn);

        TColumnDefinition cd = ato.getColumnDefinitionList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("\"RULEACTION\""));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER TABLE \"ACLIPPROTOCOLMAPTEMPLATE\" ADD CONSTRAINT \"ACLIPPROTOCOLMAPTEMPLATE_K\" UNIQUE (\"ACLTEMPLATENAME\", \"SOURCEIPGROUPTEMPLATENAME\", \"DESTINATIONIPGROUPTEMPLATENAME\", \"PROTOCOLGROUPTEMPLATENAME\", \"DIRECTION\", \"RULEACTION\") ENABLE;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("\"ACLIPPROTOCOLMAPTEMPLATE\""));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddConstraint);
        TConstraint c = (TConstraint) ato.getConstraintList().getConstraint(0);
        assertTrue(c.getConstraint_type() == EConstraintType.unique);
        assertTrue(c.getConstraintName().toString().equalsIgnoreCase("\"ACLIPPROTOCOLMAPTEMPLATE_K\""));
        assertTrue(c.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("\"ACLTEMPLATENAME\""));
        assertTrue(c.getColumnList().getElement(1).getColumnName().toString().equalsIgnoreCase("\"SOURCEIPGROUPTEMPLATENAME\""));
        assertTrue(c.getColumnList().getElement(2).getColumnName().toString().equalsIgnoreCase("\"DESTINATIONIPGROUPTEMPLATENAME\""));
        assertTrue(c.getColumnList().getElement(3).getColumnName().toString().equalsIgnoreCase("\"PROTOCOLGROUPTEMPLATENAME\""));
        assertTrue(c.getColumnList().getElement(4).getColumnName().toString().equalsIgnoreCase("\"DIRECTION\""));
        assertTrue(c.getColumnList().getElement(5).getColumnName().toString().equalsIgnoreCase("\"RULEACTION\""));
    }

    public void testMySQLDropIndex(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE jr_story DROP INDEX INK02_jr_story;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("jr_story"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.DropConstraintIndex);
        assertTrue(ato.getConstraintName().toString().equalsIgnoreCase("INK02_jr_story"));
    }

    public void testTeradataAddPrimaryKey(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "ALTER TABLE tbl_employee ADD PRIMARY KEY (employee_id)";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("tbl_employee"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddConstraint);
        TConstraint c = (TConstraint) ato.getConstraintList().getConstraint(0);
        assertTrue(c.getConstraint_type() == EConstraintType.primary_key);

        assertTrue(c.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("employee_id"));
    }

    public void testTeradataAddForeignKey(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "ALTER TABLE tbl_emp ADD FOREIGN KEY (DeptNo) REFERENCES tbl_dept(department_number)";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("tbl_emp"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddConstraint);
        TConstraint c = (TConstraint) ato.getConstraintList().getConstraint(0);
        assertTrue(c.getConstraint_type() == EConstraintType.foreign_key);

        assertTrue(c.getColumnList().getElement(0).getColumnName().toString().equalsIgnoreCase("DeptNo"));

        assertTrue(c.getReferencedObject().toString().equalsIgnoreCase("tbl_dept"));
        assertTrue(c.getReferencedColumnList().getObjectName(0).toString().equalsIgnoreCase("department_number"));
    }

    public void testMySQLAlterTableRename(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "alter table foo rename to bar;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("foo"));
        assertTrue(alterTable.getTableName().getTableString().equalsIgnoreCase("foo"));
        assertTrue(alterTable.getTableName().getTableToken().toString().equalsIgnoreCase("foo"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.RenameTable);
        assertTrue(ato.getNewTableName().toString().equalsIgnoreCase("bar"));
        assertTrue(ato.getNewTableName().getTableString().equalsIgnoreCase("bar"));
        assertTrue(ato.getNewTableName().getTableToken().toString().equalsIgnoreCase("bar"));

        assertTrue(alterTable.tables.size() == 2);
    }

    public void testMySQLAlterTableAddColumn(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE employee ADD street VARCHAR(30);";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("employee"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(ato.getColumnDefinitionList().size() == 1);
        TColumnDefinition cd = ato.getColumnDefinitionList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("street"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.varchar_t);
        assertTrue(cd.getDatatype().getLength().toString().equalsIgnoreCase("30"));
    }

    public void testMySQLAlterTableAddColumnComment(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE x\n" +
                "ADD COLUMN a VARCHAR(30) COMMENT \"abc\" FIRST";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("x"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(ato.getColumnDefinitionList().size() == 1);
        TColumnDefinition cd = ato.getColumnDefinitionList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("a"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.varchar_t);
        assertTrue(cd.getDatatype().getLength().toString().equalsIgnoreCase("30"));
        assertTrue(cd.getComment().toString().equalsIgnoreCase("\"abc\""));
    }

    public void testMySQLAlterTableAlterColumnSetDefault(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "alter table pet alter xyz set default 2";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("pet"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        //System.out.println(ato.getOptionType());
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AlterColumn);
        assertTrue(ato.getDefaultExpr().toString().equalsIgnoreCase("2"));
    }


    public void testMySQLAlterTableForeignKey(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "alter table test_3 add constraint fk foreign key(id) references city(id);";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("test_3"));

        TAlterTableOption ato = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        //System.out.println(ato.getOptionType());
        assertTrue(ato.getOptionType() == EAlterTableOptionType.AddConstraintFK);
        assertTrue(ato.getReferencedTable().getTableName().toString().equalsIgnoreCase("city"));
    }

    public void testMySQLAlterTableDropIndex(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE table_name DROP INDEX constraint_name;";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("table_name"));
        assertTrue(alterTable.getTableName().getTableString().equalsIgnoreCase("table_name"));

    }

    public void testMySQLAlterTableAddConstraint(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "ALTER TABLE contacts ADD CONSTRAINT contacts_unique UNIQUE (reference_number);";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("contacts"));
        assertTrue(alterTable.getAlterTableOptionList().size() == 1);
        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getConstraintName().toString().equalsIgnoreCase("contacts_unique"));
        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddConstraintUnique);
        assertTrue(alterTableOption.getIndexCols().getElement(0).getColumnName().toString().equalsIgnoreCase("reference_number"));
    }

    public void testMySQLAlterTableAddConstraint2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "Alter table table_name add Constraint constraint_name unique(column_name)";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("table_name"));
        assertTrue(alterTable.getAlterTableOptionList().size() == 1);
        for(int i=0;i<alterTable.getAlterTableOptionList().size();i++){
            TAlterTableOption tableOpt = alterTable.getAlterTableOptionList().getAlterTableOption(i);
            String constrName = tableOpt.getConstraintName().toString(); //getting nullpointerexception here
        }
    }

    public void testMySQLAlterTableAddForeignKey(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "Alter table test_3 add constraint fk foreign key(id) references city(id);";
        assertTrue(sqlparser.parse() == 0);
        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("test_3"));
        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(alterTableOption.getConstraintName().toString().equalsIgnoreCase("fk"));

        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddConstraintFK);
        // foreign key(id)
        assertTrue(alterTableOption.getIndexCols().getElement(0).getColumnName().toString().equalsIgnoreCase("id"));
        //city(id)
        assertTrue(alterTableOption.getReferencedTable().getTableName().toString().equalsIgnoreCase("city"));
        assertTrue(alterTableOption.getReferencedColumnList().getObjectName(0).toString().equalsIgnoreCase("id"));
    }

//    public void testMySQLAlterTableRename(){
//        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
//        sqlparser.sqltext = "Alter table test_3 add constraint fk foreign key(id) references city(id);";
//        assertTrue(sqlparser.parse() == 0);
//        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
//        assertTrue(alterTable.getTableName().toString().equalsIgnoreCase("test_3"));
//        TAlterTableOption alterTableOption = alterTable.getAlterTableOptionList().getAlterTableOption(0);
//        assertTrue(alterTableOption.getConstraintName().toString().equalsIgnoreCase("fk"));
//
//        assertTrue(alterTableOption.getOptionType() == EAlterTableOptionType.AddConstraintFK);
//        // foreign key(id)
//        assertTrue(alterTableOption.getIndexCols().getElement(0).getColumnName().toString().equalsIgnoreCase("id"));
//        //city(id)
//        assertTrue(alterTableOption.getReferencedTable().getTableName().toString().equalsIgnoreCase("city"));
//        assertTrue(alterTableOption.getReferencedColumnList().getObjectName(0).toString().equalsIgnoreCase("id"));
//    }

}
