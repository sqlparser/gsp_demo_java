package scriptWriter;

import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ELiteralType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TTypeName;
import junit.framework.TestCase;

/**
 * Code illustrates how to create SQL Server datatype from the scratch.
 */
public class testSQLServerDataType extends TestCase
{

    public void testInt( )
    {
        TTypeName typeName = new TTypeName(EDataType.bigint_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql,typeName.toScript(), "bigint"));

        typeName = new TTypeName(EDataType.int_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "int"));

        typeName = new TTypeName(EDataType.smallint_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "smallint"));

        typeName = new TTypeName(EDataType.tinyint_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "tinyint"));
    }

    public void testDecimal( )
    {
        TTypeName typeName = new TTypeName(EDataType.decimal_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "decimal"));

        typeName = new TTypeName(EDataType.decimal_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("10"))
                ,null);
        //System.out.print(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "decimal(10)"));

        typeName = new TTypeName(EDataType.decimal_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("10"))
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("2")));
        //System.out.print(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "decimal(10,2)"));
    }

    public void testBit( ) {
        TTypeName typeName = new TTypeName(EDataType.bit_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "bit"));
    }

    public void testMoney( ) {
        TTypeName typeName = new TTypeName(EDataType.money_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "money"));
        typeName = new TTypeName(EDataType.smallmoney_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "smallmoney"));
    }

    public void testFloat( ) {
        TTypeName typeName = new TTypeName(EDataType.float_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "float"));

        typeName = new TTypeName(EDataType.float_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("24"))
                );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "float(24)"));
    }

    public void testReal( ) {
        TTypeName typeName = new TTypeName(EDataType.real_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "real"));
    }

    public void testDate( ) {
        TTypeName typeName = new TTypeName(EDataType.date_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "date"));
    }

    public void testDatetimeoffset( ) {
        TTypeName typeName = new TTypeName(EDataType.datetimeoffset_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "datetimeoffset"));
        typeName = new TTypeName(EDataType.datetimeoffset_t);
        typeName.setFractionalSecondsPrecision(new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "datetimeoffset(10)"));
    }

    public void testChar( ) {
        TTypeName typeName = new TTypeName(EDataType.char_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "char"));
        typeName = new TTypeName(EDataType.character_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "character"));
        typeName = new TTypeName(EDataType.char_t,new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "char(10)"));
        typeName = new TTypeName(EDataType.character_t,new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "character(10)"));

        typeName = new TTypeName(EDataType.varchar_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "varchar"));
        typeName = new TTypeName(EDataType.varchar_t,new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "varchar(10)"));
        typeName = new TTypeName(EDataType.varchar_t,new TConstant(ELiteralType.etString,new TSourceToken("max")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "varchar(max)"));
//        typeName = new TTypeName(EDataType.charvarying_t);
//        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "charvarying"));
//        typeName = new TTypeName(EDataType.charactervarying_t);
//        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "charvarying"));

    }

    public void testNChar( ) {
        TTypeName typeName = new TTypeName(EDataType.nchar_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "nchar"));

        typeName = new TTypeName(EDataType.nvarchar_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "nvarchar"));
        typeName = new TTypeName(EDataType.nvarchar_t,new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "nvarchar(10)"));
        typeName = new TTypeName(EDataType.nvarchar_t,new TConstant(ELiteralType.etString,new TSourceToken("max")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "nvarchar(max)"));

    }

    public void testText( ) {
        TTypeName typeName = new TTypeName(EDataType.text_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "text"));
        typeName = new TTypeName(EDataType.image_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "image"));
        typeName = new TTypeName(EDataType.ntext_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "ntext"));
    }

    public void testBinary( ) {
        TTypeName typeName = new TTypeName(EDataType.binary_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "binary"));
        typeName = new TTypeName(EDataType.binary_t,new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "binary(10)"));

        typeName = new TTypeName(EDataType.varbinary_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "varbinary"));
        typeName = new TTypeName(EDataType.varbinary_t,new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "varbinary(10)"));
        typeName = new TTypeName(EDataType.varbinary_t,new TConstant(ELiteralType.etString,new TSourceToken("max")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "varbinary(max)"));

    }

    public void testRowversion( ) {
        TTypeName typeName = new TTypeName(EDataType.rowversion_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "rowversion"));
    }

    public void testTimestamp( ) {
        TTypeName typeName = new TTypeName(EDataType.timestamp_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "timestamp"));
    }

    public void testUniqueIdentifier( ) {
        TTypeName typeName = new TTypeName(EDataType.uniqueidentifier_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "uniqueidentifier"));
    }

    public void testSql_variant( ) {
        TTypeName typeName = new TTypeName(EDataType.sql_variant_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvmssql, typeName.toScript(), "sql_variant"));
    }

}
