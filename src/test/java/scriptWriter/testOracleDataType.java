package scriptWriter;


import gudusoft.gsqlparser.EDataType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ELiteralType;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TTypeName;
import junit.framework.TestCase;

/**
 * Code illustrates how to create Oracle datatype from the scratch.
 */
public class testOracleDataType extends TestCase
{

    public void testChar( )
    {
        TTypeName typeName = new TTypeName(EDataType.char_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle,typeName.toScript(), "char"));

        typeName.setLength(new TConstant(ELiteralType.etNumber,new TSourceToken("10")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "char(10)"));

        typeName = new TTypeName(EDataType.varchar2_t);
        typeName.setLength(new TConstant(ELiteralType.etNumber,new TSourceToken("20")));
        typeName.setByteUnit(true);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "varchar2(20 byte)"));

    }

    public void testLob( )
    {
        TTypeName typeName = new TTypeName(EDataType.clob_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle,typeName.toScript(), "clob"));

        typeName = new TTypeName(EDataType.blob_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "blob"));

        typeName = new TTypeName(EDataType.nclob_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "nclob"));

        typeName = new TTypeName(EDataType.bfile_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "bfile"));
    }

    public void testLongRaw( )
    {
        TTypeName typeName = new TTypeName(EDataType.long_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle,typeName.toScript(), "long"));

        typeName = new TTypeName(EDataType.raw_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "raw"));

        typeName = new TTypeName(EDataType.raw_t,new TConstant(ELiteralType.etNumber,new TSourceToken("20")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "raw(20)"));

        typeName = new TTypeName(EDataType.long_raw_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "long raw"));

    }

    public void testNumber( )
    {
        TTypeName typeName = new TTypeName(EDataType.number_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle,typeName.toScript(), "number"));

        typeName = new TTypeName(EDataType.number_t
                    ,new TConstant(ELiteralType.etNumber,new TSourceToken("20"))
                    ,null);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "number(20)"));

        typeName = new TTypeName(EDataType.number_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("20"))
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("2")));
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "number(20,2)"));

    }

    public void testDateTime( )
    {
        TTypeName typeName = new TTypeName(EDataType.date_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle,typeName.toScript(), "date"));

        typeName = new TTypeName(EDataType.timestamp_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("20"))
                ,null);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "timestamp(20)"));

        typeName = new TTypeName(EDataType.timestamp_with_time_zone_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("20"))
                );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "timestamp(20) with time zone"));
    }

    public void testInterval( )
    {
        TTypeName typeName = new TTypeName(EDataType.interval_year_to_month_t);
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle,typeName.toScript(), "interval year to month"));

        typeName = new TTypeName(EDataType.interval_year_to_month_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("20"))
                );
        //System.out.println(typeName.toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "interval year (20) to month"));

        typeName = new TTypeName(EDataType.interval_day_to_second_t);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "interval day to second"));

        typeName = new TTypeName(EDataType.interval_day_to_second_t
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("20"))
                ,new TConstant(ELiteralType.etNumber,new TSourceToken("40"))
        );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, typeName.toScript(), "interval day (20) to second (40)"));
    }
}
