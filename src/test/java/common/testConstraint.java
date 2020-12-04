package common;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testConstraint extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE TABLE [dbo].[TestTable]\n" +
                "(\n" +
                "                [AutoId]    [int] IDENTITY (1, 1)   NOT NULL,\n" +
                "                [UserName]  [nvarchar] (256)        NOT NULL unique,\n" +
                "                [TagID] [int] NULL\n" +
                "                                CONSTRAINT [FK_EPODirSortRules_FK] FOREIGN KEY\n" +
                "                                REFERENCES [dbo].[Tag] ([TagID])\n" +
                "                                ON UPDATE CASCADE\n" +
                "                                ON DELETE CASCADE\n" +
                ") ON [PRIMARY]";
        assertTrue(sqlparser.parse() == 0);
        TCreateTableSqlStatement stmt = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        for ( int i = 0; i < stmt.getColumnList( ).size( ); i++ )
           {
               TColumnDefinition columnDefinition = stmt.getColumnList()
                       .getColumn(i);
               TConstraintList constraint = columnDefinition.getConstraints();

               for ( int j = 0 ; j < constraint.size(); j++ )
               {
                   TConstraint cst = constraint.getConstraint(j);
                   if( (cst.getConstraint_type() == EConstraintType.reference)
                           ||(cst.getConstraint_type() == EConstraintType.foreign_key)
                       )
                       if (cst.getKeyActions() != null){
                           TKeyAction keyAction = cst.getKeyActions().getElement(0);
                           assertTrue(keyAction.getActionType() == EKeyActionType.update);
                           TKeyReference keyReference = keyAction.getKeyReference();
                           assertTrue(keyReference.getReferenceType() == EKeyReferenceType.cascade);

                           TKeyAction keyAction1 = cst.getKeyActions().getElement(1);
                           assertTrue(keyAction1.getActionType() == EKeyActionType.delete);
                           TKeyReference keyReference1 = keyAction1.getKeyReference();
                           assertTrue(keyReference1.getReferenceType() == EKeyReferenceType.cascade);
                       }

               }
           }

    //    assertTrue(c.getColumnList().getObjectName(0).toString().equalsIgnoreCase("\"ID\""));
    }

}
