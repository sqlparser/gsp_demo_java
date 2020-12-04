package snowflake;


import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testVaraible extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "select\n" +
                "  v.$1, v.$2\n" +
                "from\n" +
                "    @my_stage( file_format => 'csv_format', pattern => '.*my_pattern.*') v;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        TExpression columnExpr = resultColumn.getExpr();
     //   System.out.println(columnExpr.getExpressionType() );
        assertTrue(columnExpr.getExpressionType() == EExpressionType.simple_object_name_t);
        TObjectName columnName = columnExpr.getObjectOperand();
        assertTrue(columnName.getDbObjectType() == EDbObjectType.column);
        assertTrue(columnName.getTableString().equalsIgnoreCase("v"));
        assertTrue(columnName.getPartString().equalsIgnoreCase("$1"));
        TTable table = select.tables.getTable(0);
        assertTrue(table.getTableName().toString().equalsIgnoreCase("my_stage"));
    }

}
