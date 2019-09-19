package test.mssql;
/*
 * Date: 12-3-20
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlBlock;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;
import junit.framework.TestCase;

public class testAlterTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "IF NOT EXISTS (SELECT * FROM [dbo].[TableA] WHERE id = OBJECT_ID(N'[dbo].[TableB]') AND name = N'Foo')\n" +
                "BEGIN\n" +
                "                ALTER TABLE [dbo].[TableB]\n" +
                "                                ADD [Foo] [int] NULL,\n" +
                "                                CONSTRAINT [ForeignKeyA] FOREIGN KEY([Foo])\n" +
                "                                                REFERENCES [dbo].[TableC] ([AutoID])\n" +
                "                                                ON UPDATE CASCADE\n" +
                "                                                ON DELETE CASCADE\n" +
                "END";
        assertTrue(sqlparser.parse() == 0);

        TMssqlIfElse ifElse = (TMssqlIfElse)sqlparser.sqlstatements.get(0);

        TMssqlBlock block = (TMssqlBlock)ifElse.getStmt();
        assertTrue(block.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstaltertable);
        TAlterTableStatement alterTableStatement = (TAlterTableStatement)block.getBodyStatements().get(0);
        assertTrue(alterTableStatement.getAlterTableOptionList().size() == 1);
        TAlterTableOption ao = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ao.getOptionType() == EAlterTableOptionType.AddColumn);
        assertTrue(ao.getColumnDefinitionList().size() == 1);
        TColumnDefinition cd = ao.getColumnDefinitionList().getColumn(0);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("[Foo]"));
        assertTrue(cd.getConstraints().size() == 1);
        TConstraint constraint = cd.getConstraints().getConstraint(0);
        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);
        assertTrue(constraint.getConstraintName().toString().equalsIgnoreCase("[ForeignKeyA]"));

//        assertTrue(alterTableStatement.getTableElementList().size() == 2);
//        TTableElement element0 =      alterTableStatement.getTableElementList().getTableElement(0);
//        TTableElement element1 =      alterTableStatement.getTableElementList().getTableElement(1);
//        assertTrue(element0.getType() == TTableElement.type_column_def);
//        TColumnDefinition columnDefinition = element0.getColumnDefinition();
//        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("[Foo]"));
//        assertTrue(element1.getType() == TTableElement.type_table_constraint);
//        TConstraint constraint = element1.getConstraint();
//        assertTrue(constraint.getConstraintName().toString().equalsIgnoreCase("[ForeignKeyA]"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "ALTER TABLE [dbo].[TableA] ADD\n" +
                "                CONSTRAINT [ConstraintA] FOREIGN KEY\n" +
                "                (\n" +
                "                                [ColumnA]\n" +
                "                ) REFERENCES [dbo].[TableB] (\n" +
                "                                [ColumnA]\n" +
                "                ) NOT FOR REPLICATION ,\n" +
                "                CONSTRAINT [ConstraintB] FOREIGN KEY\n" +
                "                (\n" +
                "                                [ColumnB]\n" +
                "                ) REFERENCES [dbo].[TableC] (\n" +
                "                                [ColumnA]\n" +
                "                ) ON DELETE CASCADE  ON UPDATE CASCADE";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);

        assertTrue(alterTableStatement.getAlterTableOptionList().size() == 1);
        TAlterTableOption ao = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
        assertTrue(ao.getOptionType() == EAlterTableOptionType.AddConstraint);
        assertTrue(ao.getConstraintList().size() == 2);
        TConstraint constraint1 = ao.getConstraintList().getConstraint(0);
        TConstraint constraint2 = ao.getConstraintList().getConstraint(1);
        assertTrue(constraint1.getConstraintName().toString().equalsIgnoreCase("[ConstraintA]"));
        assertTrue(constraint2.getConstraintName().toString().equalsIgnoreCase("[ConstraintB]"));

//        TColumnDefinition cd = ao.getColumnDefinitionList().getColumn(0);
//        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("[Foo]"));
//        assertTrue(cd.getConstraints().size() == 1);
//        TConstraint constraint = cd.getConstraints().getConstraint(0);
//        assertTrue(constraint.getConstraint_type() == EConstraintType.foreign_key);
//        assertTrue(constraint.getConstraintName().toString().equalsIgnoreCase("[ForeignKeyA]"));

//        assertTrue(alterTableStatement.getTableElementList().size() == 2);
//        TTableElement element0 =      alterTableStatement.getTableElementList().getTableElement(0);
//        TTableElement element1 =      alterTableStatement.getTableElementList().getTableElement(1);
//        assertTrue(element0.getType() == TTableElement.type_table_constraint);
//        assertTrue(element1.getType() == TTableElement.type_table_constraint);
//        TConstraint constraint = element1.getConstraint();
//        assertTrue(constraint.getConstraintName().toString().equalsIgnoreCase("[ConstraintB]"));

    }

  public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "ALTER TABLE FolderComment ADD CommentRSN INT NOT NULL DEFAULT NEXT VALUE FOR FolderCommentSeq";
        assertTrue(sqlparser.parse() == 0);

        TAlterTableStatement alterTableStatement = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
      assertTrue(alterTableStatement.getAlterTableOptionList().size() == 1);
      TAlterTableOption ao = alterTableStatement.getAlterTableOptionList().getAlterTableOption(0);
      assertTrue(ao.getOptionType() == EAlterTableOptionType.AddColumn);

//        assertTrue(alterTableStatement.getTableElementList().size() == 1);
//        TTableElement element0 =      alterTableStatement.getTableElementList().getTableElement(0);
//        assertTrue(element0.getType() == TTableElement.type_column_def);

        TColumnDefinition columnDefinition = ao.getColumnDefinitionList().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("CommentRSN"));
        TConstraint columnConstraint = columnDefinition.getConstraints().getConstraint(0);
        assertTrue(columnConstraint.getConstraint_type() == EConstraintType.notnull);
        columnConstraint = columnDefinition.getConstraints().getConstraint(1);
         assertTrue(columnConstraint.getConstraint_type() == EConstraintType.default_value);
         TExpression defaultValue = columnConstraint.getDefaultExpression();
        assertTrue(defaultValue.getExpressionType() == EExpressionType.next_value_for_t);
        assertTrue(defaultValue.getSequenceName().toString().equalsIgnoreCase("FolderCommentSeq"));

    }

}
