package databricks;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testStruct extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = "SELECT CAST(map(struct('Hello', 'World'), 'Greeting') AS MAP<STRUCT<w1:string, w2:string>, string>);\n";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TFunctionCall functionCall = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr().getFunctionCall();
        assertTrue(functionCall.getFunctionType() == EFunctionType.cast_t);
        TTypeName castType = functionCall.getTypename();
        //System.out.println(castType.toString());
        assertTrue(castType.getDataType() == EDataType.map_t);
        assertTrue(castType.getKeyType().getDataType() == EDataType.struct_t);
        assertTrue(castType.getValueType().getDataType() == EDataType.string_t);

        TTypeName structType = castType.getKeyType();
        assertTrue(structType.getColumnDefList().size() == 2);
        assertTrue(structType.getColumnDefList().getColumn(0).getColumnName().toString().equalsIgnoreCase("w1"));
        assertTrue(structType.getColumnDefList().getColumn(0).getDatatype().getDataType() == EDataType.string_t);
    }
}
