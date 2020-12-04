package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.dax.TDaxFunction;
import gudusoft.gsqlparser.stmt.dax.TDaxExprStmt;
import junit.framework.TestCase;


public class testDaxFunction extends TestCase {

    public static void testISOCeiling(){
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvdax);
        sqlParser.sqltext = "=ISO.CEILING(-4.42,0.05)";
        int ret = sqlParser.parse();
        assertTrue(ret == 0);
        TCustomSqlStatement stmt = sqlParser.sqlstatements.get(0);
        assertTrue(stmt.sqlstatementtype == ESqlStatementType.sstdaxexpr);
        TDaxFunction function = (TDaxFunction)((TDaxExprStmt)stmt).getExpr().getFunctionCall();
        assertTrue(function.getFunctionName().toString().equalsIgnoreCase("ISO.CEILING"));
        assertTrue(function.getArgs().getExpression(0).toString().equalsIgnoreCase("-4.42"));
        assertTrue(function.getArgs().getExpression(1).toString().equalsIgnoreCase("0.05"));
    }

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvdax);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.runText(inputQuery);
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testEDate() {
        doTest("=EDATE([TransactionDate],3)","Tables:\n" +
                "default\n" +
                "\nFields:\n" +
                "default[TransactionDate]");
    }

    public static void testDay() {
        doTest("=DAY([Birthdate])",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[Birthdate]" );
    }

    public static void testMinX(){
        doTest("=CALENDAR (MINX (Sales, [Date]), MAXX (Forecast, [Date]))",
                "Tables:\n" +
                        "Forecast\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "Forecast[Date]\n" +
                        "Sales[Date]");
    }

    public static void testHour(){
        doTest("=HOUR('Orders'[TransactionTime])",
                "Tables:\n" +
                        "'Orders'\n" +
                        "\nFields:\n" +
                        "'Orders'[TransactionTime]" );
    }

    public static void testNow(){
        doTest("=NOW()+3.5",
                "Tables:\n" +
                        "\nFields:" );
    }

    public static void testYear(){
        doTest("=YEAR(TODAY())-1963",
                "Tables:\n" +
                        "\nFields:" );
    }

    public static void testYEARFRAC(){
        doTest("=YEARFRAC(Orders[TransactionDate],Orders[ShippingDate]) ",
                "Tables:\n" +
                        "Orders\n" +
                        "\nFields:\n" +
                        "Orders[ShippingDate]\n" +
                        "Orders[TransactionDate]" );
    }

    public static void testCLOSINGBALANCEMONTH(){
        doTest("=CLOSINGBALANCEMONTH(\n" +
                        "\tSUMX(ProductInventory,ProductInventory[UnitCost]*ProductInventory[UnitsBalance])\n" +
                        "\t,DateTime[DateKey]\n" +
                        "\t)",
                "Tables:\n" +
                        "DateTime\n" +
                        "ProductInventory\n" +
                        "\nFields:\n" +
                        "DateTime[DateKey]\n" +
                        "ProductInventory[UnitCost]\n" +
                        "ProductInventory[UnitsBalance]" );
    }

    public static void testDATEADD(){
        doTest("=DATEADD(DateTime[DateKey],-1,year)",
                "Tables:\n" +
                        "DateTime\n" +
                        "\nFields:\n" +
                        "DateTime[DateKey]" );
    }


    public static void testDATESINPERIOD(){
        doTest("= CALCULATE(SUM(InternetSales_USD[SalesAmount_USD]),DATESINPERIOD(DateTime[DateKey],DATE(2007,08,24),-21,day))  ",
                "Tables:\n" +
                        "DateTime\n" +
                        "InternetSales_USD\n" +
                        "\nFields:\n" +
                        "DateTime[DateKey]\n" +
                        "InternetSales_USD[SalesAmount_USD]" );
    }

    public static void testALL(){
        doTest("=SUMX(ResellerSales_USD, ResellerSales_USD[SalesAmount_USD])/SUMX(ALL(ResellerSales_USD), ResellerSales_USD[SalesAmount_USD])  ",
                "Tables:\n" +
                        "ResellerSales_USD\n" +
                        "\nFields:\n" +
                        "ResellerSales_USD[SalesAmount_USD]" );
    }

    public static void testALLEXCEPT(){
        doTest("=CALCULATE(SUM(ResellerSales_USD[SalesAmount_USD]), ALLEXCEPT(DateTime, DateTime[CalendarYear]))",
                "Tables:\n" +
                        "DateTime\n" +
                        "ResellerSales_USD\n" +
                        "\nFields:\n" +
                        "DateTime[CalendarYear]\n" +
                        "ResellerSales_USD[SalesAmount_USD]" );
    }

    public static void testALLNOBLANKROW(){
        doTest("= COUNTROWS(ALLNOBLANKROW('DateTime'))",
                "Tables:\n" +
                        "'DateTime'\n" +
                        "\nFields:" );
    }

    public static void testCALCULATETABLE(){
        doTest("=SUMX( CALCULATETABLE('InternetSales_USD', 'DateTime'[CalendarYear]=2006)  \n" +
                        "     , [SalesAmount_USD]) ",
                "Tables:\n" +
                        "'DateTime'\n" +
                        "'InternetSales_USD'\n" +
                        "\nFields:\n" +
                        "'DateTime'[CalendarYear]\n" +
                        "'InternetSales_USD'[SalesAmount_USD]" );
    }

    public static void testCROSSFILTER(){
        doTest("= CALCULATE([Distinct Count of ProductKey], CROSSFILTER(FactInternetSales[ProductKey], DimProduct[ProductKey] , Both))  ",
                "Tables:\n" +
                        "default\n" +
                        "DimProduct\n" +
                        "FactInternetSales\n" +
                        "\nFields:\n" +
                        "default[Distinct Count of ProductKey]\n" +
                        "DimProduct[ProductKey]\n" +
                        "FactInternetSales[ProductKey]" );
    }

    public static void testFILTER(){
        doTest("=SUMX(FILTER('InternetSales_USD', RELATED('SalesTerritory'[SalesTerritoryCountry])<>\"United States\")  \n" +
                        "     ,'InternetSales_USD'[SalesAmount_USD])",
                "Tables:\n" +
                        "'InternetSales_USD'\n" +
                        "'SalesTerritory'\n" +
                        "\nFields:\n" +
                        "'InternetSales_USD'[SalesAmount_USD]\n" +
                        "'SalesTerritory'[SalesTerritoryCountry]" );
    }

    public static void testRELATEDTABLE(){
        doTest("= SUMX( RELATEDTABLE('InternetSales_USD')  \n" +
                        "     , [SalesAmount_USD])  ",
                "Tables:\n" +
                        "'InternetSales_USD'\n" +
                        "\nFields:\n" +
                        "'InternetSales_USD'[SalesAmount_USD]" );
    }

    public static void testSUBSTITUTEWITHINDEX(){
        doTest("=SUBSTITUTEWITHINDEX('InternetSales_USD', [SalesAmount_USD], 'InternetSales_USD2')",
                "Tables:\n" +
                        "'InternetSales_USD'\n" +
                        "'InternetSales_USD2'\n" +
                        "\nFields:\n" +
                        "'InternetSales_USD'[SalesAmount_USD]\n" +
                        "'InternetSales_USD2'[SalesAmount_USD]" );
    }

    public static void testVALUES(){
        doTest("=COUNTROWS(VALUES('InternetSales_USD'[SalesOrderNumber])) ",
                "Tables:\n" +
                        "'InternetSales_USD'\n" +
                        "\nFields:\n" +
                        "'InternetSales_USD'[SalesOrderNumber]" );
    }

    public static void testVALUES2(){
        doTest("=COUNTROWS(VALUES([SalesOrderNumber])) ",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[SalesOrderNumber]" );
    }

    public static void testCONTAINS(){
        doTest("=CONTAINS(InternetSales, [ProductKey], 214, [CustomerKey], 11185)",
                "Tables:\n" +
                        "InternetSales\n" +
                        "\nFields:\n" +
                        "InternetSales[CustomerKey]\n" +
                        "InternetSales[ProductKey]" );
    }

    public static void testCUSTOMDATA(){
        doTest("=IF(CUSTOMDATA()=\"OK\", \"Correct Custom data in connection string\", \"No custom data in connection string property or unexpected value\") ",
                "Tables:\n" +
                        "\nFields:" );
    }

    public static void testISBLANK(){
        doTest("=IF( ISBLANK('CalculatedMeasures'[PreviousYearTotalSales])  \n" +
                        "   , BLANK()  \n" +
                        "   , ( 'CalculatedMeasures'[Total Sales]-'CalculatedMeasures'[PreviousYearTotalSales] )  \n" +
                        "      /'CalculatedMeasures'[PreviousYearTotalSales])",
                "Tables:\n" +
                        "'CalculatedMeasures'\n" +
                        "\nFields:\n" +
                        "'CalculatedMeasures'[PreviousYearTotalSales]\n" +
                        "'CalculatedMeasures'[Total Sales]" );
    }

    public static void testISONORAFTER(){
        doTest("=FILTER(Info, ISONORAFTER(Info[Country], \"IND\", ASC, Info[State], \"MH\", ASC))  ",
                "Tables:\n" +
                        "Info\n" +
                        "\nFields:\n" +
                        "Info[Country]\n" +
                        "Info[State]" );
    }
    public static void testLOOKUPVALUE(){
        doTest("=LOOKUPVALUE(Product[SafetyStockLevel], [ProductName], \" Mountain-400-W Silver, 46\")  ",
                "Tables:\n" +
                        "Product\n" +
                        "\nFields:\n" +
                        "Product[ProductName]\n" +
                        "Product[SafetyStockLevel]" );
    }
    public static void testAND(){
        doTest("= IF( AND(  SUM( 'InternetSales_USD'[SalesAmount_USD])  \n" +
                        "           >SUM('ResellerSales_USD'[SalesAmount_USD])  \n" +
                        "          , CALCULATE(SUM('InternetSales_USD'[SalesAmount_USD]), PREVIOUSYEAR('DateTime'[DateKey] ))   \n" +
                        "           >CALCULATE(SUM('ResellerSales_USD'[SalesAmount_USD]), PREVIOUSYEAR('DateTime'[DateKey] ))  \n" +
                        "          )  \n" +
                        "     , \"Internet Hit\"  \n" +
                        "     , \"\"  \n" +
                        "     ) ",
                "Tables:\n" +
                        "'DateTime'\n" +
                        "'InternetSales_USD'\n" +
                        "'ResellerSales_USD'\n" +
                        "\nFields:\n" +
                        "'DateTime'[DateKey]\n" +
                        "'InternetSales_USD'[SalesAmount_USD]\n" +
                        "'ResellerSales_USD'[SalesAmount_USD]" );
    }

    public static void testIF(){
        doTest("=IF([StateProvinceCode]= \"CA\" && ([MaritalStatus] = \"M\" || [NumberChildrenAtHome] >1),[City])  ",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[City]\n" +
                        "default[MaritalStatus]\n" +
                        "default[NumberChildrenAtHome]\n" +
                        "default[StateProvinceCode]" );
    }

    public static void testNOT(){
        doTest("=NOT([CalculatedColumn1])",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[CalculatedColumn1]" );
    }

    public static void testOR(){
        doTest("=IF(   OR(   CALCULATE(SUM('ResellerSales_USD'[SalesAmount_USD]), 'ProductSubcategory'[ProductSubcategoryName]=\"Touring Bikes\") > 1000000  \n" +
                        "         ,   CALCULATE(SUM('ResellerSales_USD'[SalesAmount_USD]), 'DateTime'[CalendarYear]=2007) > 2500000  \n" +
                        "         )  \n" +
                        "   , \"Circle of Excellence\"  \n" +
                        "   , \"\"  \n" +
                        "   )",
                "Tables:\n" +
                        "'DateTime'\n" +
                        "'ProductSubcategory'\n" +
                        "'ResellerSales_USD'\n" +
                        "\nFields:\n" +
                        "'DateTime'[CalendarYear]\n" +
                        "'ProductSubcategory'[ProductSubcategoryName]\n" +
                        "'ResellerSales_USD'[SalesAmount_USD]" );
    }


    public static void testABS(){
        doTest("=ABS([DealerPrice]-[ListPrice])  ",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[DealerPrice]\n" +
                        "default[ListPrice]" );
    }
    public static void testEXP(){
        doTest("=EXP([Power])  ",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[Power]" );
    }

    public static void testFLOOR(){
        doTest("=FLOOR(InternetSales[Total Product Cost],.5)",
                "Tables:\n" +
                        "InternetSales\n" +
                        "\nFields:\n" +
                        "InternetSales[Total Product Cost]" );
    }

    public static void testPRODUCT(){
        doTest("=PRODUCT( Annuity[AdjustedRates] ) ",
                "Tables:\n" +
                        "Annuity\n" +
                        "\nFields:\n" +
                        "Annuity[AdjustedRates]" );
    }

    public static void testPRODUCTX(){
        doTest("= [PresentValue]\t * PRODUCTX( AnnuityPeriods, 1+[FixedInterestRate] )  ",
                "Tables:\n" +
                        "AnnuityPeriods\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "AnnuityPeriods[FixedInterestRate]\n" +
                        "default[PresentValue]" );
    }

    public static void testSIGN(){
        doTest("=SIGN( ([Sale Price] - [Cost]) ) ",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[Cost]\n" +
                        "default[Sale Price]" );
    }

    public static void testSUMX(){
        doTest("=SUMX(FILTER(InternetSales, InternetSales[SalesTerritoryID]=5),[Freight])  ",
                "Tables:\n" +
                        "InternetSales\n" +
                        "\nFields:\n" +
                        "InternetSales[Freight]\n" +
                        "InternetSales[SalesTerritoryID]" );
    }

    public static void testDataTable(){
        doTest("=DataTable(\"Name\", STRING,  \n" +
                        "               \"Region\", STRING  \n" +
                        "               ,{  \n" +
                        "                        {\" User1\",\"East\"},  \n" +
                        "                        {\" User2\",\"East\"},  \n" +
                        "                        {\" User3\",\"West\"},  \n" +
                        "                        {\" User4\",\"West\"},  \n" +
                        "                        {\" User4\",\"East\"}  \n" +
                        "                }  \n" +
                        "           )",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default\"Name\"\n" +
                        "default\"Region\"" );
    }

    public static void testExcept(){
        doTest("=Except(States1, States2)",
                "Tables:\n" +
                        "States1\n" +
                        "States2\n" +
                        "\nFields:" );
    }

    public static void testGROUPBY(){
        doTest("=GROUPBY (  \n" +
                        "Sales,   \n" +
                        "Geography[Country],   \n" +
                        "Product[Category],   \n" +
                        "\"Total Sales\", SUMX( CURRENTGROUP(), Sales[Price] * Sales[Qty])  \n" +
                        ") ",
                "Tables:\n" +
                        "Geography\n" +
                        "Product\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "Geography[Country]\n" +
                        "Product[Category]\n" +
                        "Sales[Price]\n" +
                        "Sales[Qty]" );
    }

    public static void testSUMMARIZE(){
        doTest("=SUMMARIZE(ResellerSales_USD  \n" +
                        "      , DateTime[CalendarYear]  \n" +
                        "      , ProductCategory[ProductCategoryName]  \n" +
                        "      , \"Sales Amount (USD)\", SUM(ResellerSales_USD[SalesAmount_USD])  \n" +
                        "      , \"Discount Amount (USD)\", SUM(ResellerSales_USD[DiscountAmount])  \n" +
                        "      )",
                "Tables:\n" +
                        "DateTime\n" +
                        "ProductCategory\n" +
                        "ResellerSales_USD\n" +
                        "\nFields:\n" +
                        "DateTime[CalendarYear]\n" +
                        "ProductCategory[ProductCategoryName]\n" +
                        "ResellerSales_USD[DiscountAmount]\n" +
                        "ResellerSales_USD[SalesAmount_USD]" );
    }

    public static void testnaturalinnerjoin(){
        doTest("= naturalinnerjoin(ColourFruit,FruitPrice)",
                "Tables:\n" +
                        "ColourFruit\n" +
                        "FruitPrice\n" +
                        "\nFields:" );
    }

    public static void testSUMMARIZECOLUMNS(){
        doTest("=SUMMARIZECOLUMNS ( 'Sales Territory'[Category], FILTER('Customer', 'Customer' [First Name] = \"Alicia\") )",
                "Tables:\n" +
                        "'Customer'\n" +
                        "'Sales Territory'\n" +
                        "\nFields:\n" +
                        "'Customer'[First Name]\n" +
                        "'Sales Territory'[Category]" );
    }

    public static void testSUMMARIZECOLUMNS2(){
        doTest("=SUMMARIZECOLUMNS ( 'Sales Territory'[Category], 'Customer' [Education], FILTER('Customer', 'Customer'[First Name] = \"Alicia\") )",
                "Tables:\n" +
                        "'Customer'\n" +
                        "'Sales Territory'\n" +
                        "\nFields:\n" +
                        "'Customer'[Education]\n" +
                        "'Customer'[First Name]\n" +
                        "'Sales Territory'[Category]" );
    }

    public static void testSUMMARIZECOLUMNS3(){
        doTest("=SUMMARIZECOLUMNS( Sales[CustomerId], \"Total Qty\", IGNORE( SUM( Sales[Qty] ) ), \"BlankIfTotalQtyIsNot3\", IF( SUM( Sales[Qty] )=3, 3 ) )",
                "Tables:\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "Sales\"BlankIfTotalQtyIsNot3\"\n" +
                        "Sales\"Total Qty\"\n" +
                        "Sales[CustomerId]\n" +
                        "Sales[Qty]" );
    }

    public static void testSUMMARIZECOLUMNS4(){
        doTest("=SUMMARIZECOLUMNS (Regions[State], ROLLUPADDISSUBTOTAL ( Sales[CustomerId], \"IsCustomerSubtotal\" ), Sales[Date], \"Total Qty\", SUM( Sales[Qty] ))",
                "Tables:\n" +
                        "Regions\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "Regions\"Total Qty\"\n" +
                        "Regions[State]\n" +
                        "Sales\"IsCustomerSubtotal\"\n" +
                        "Sales[CustomerId]\n" +
                        "Sales[Date]\n" +
                        "Sales[Qty]" );
    }

    public static void testSUMMARIZECOLUMNS5(){
        doTest("=SUMMARIZECOLUMNS ( Regions[State], ROLLUPADDISSUBTOTAL ( Sales[CustomerId], \"IsCustomerSubtotal\" ), ROLLUPADDISSUBTOTAL ( Sales[Date], \"IsDateSubtotal\"), \"Total Qty\", SUM( Sales[Qty] ) )",
                "Tables:\n" +
                        "Regions\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "Regions\"Total Qty\"\n" +
                        "Regions[State]\n" +
                        "Sales\"IsCustomerSubtotal\"\n" +
                        "Sales\"IsDateSubtotal\"\n" +
                        "Sales[CustomerId]\n" +
                        "Sales[Date]\n" +
                        "Sales[Qty]" );
    }

    public static void testSUMMARIZECOLUMNS6(){
        doTest("=SUMMARIZECOLUMNS( \n" +
                        "\t\t\tROLLUPADDISSUBTOTAL( Sales[CustomerId], \"IsCustomerSubtotal\" )\n" +
                        "\t\t, ROLLUPADDISSUBTOTAL(\tROLLUPGROUP(Regions[City], Regions[State]) , \"IsCityStateSubtotal\")\n" +
                        "\t\t,\"Total Qty\", SUM( Sales[Qty] ) )",
                "Tables:\n" +
                        "default\n" +
                        "Regions\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "default\"IsCityStateSubtotal\"\n" +
                        "default\"Total Qty\"\n" +
                        "Regions[City]\n" +
                        "Regions[State]\n" +
                        "Sales\"IsCustomerSubtotal\"\n" +
                        "Sales[CustomerId]\n" +
                        "Sales[Qty]" );
    }

    public static void testPATH(){
        doTest("=PATH(Employee[EmployeeKey], Employee[ParentEmployeeKey])",
                "Tables:\n" +
                        "Employee\n" +
                        "\nFields:\n" +
                        "Employee[EmployeeKey]\n" +
                        "Employee[ParentEmployeeKey]" );
    }

    public static void testPATHCONTAINS(){
        doTest("=PATHCONTAINS(PATH(Employee[EmployeeKey], Employee[ParentEmployeeKey]), \"23\")  ",
                "Tables:\n" +
                        "Employee\n" +
                        "\nFields:\n" +
                        "Employee[EmployeeKey]\n" +
                        "Employee[ParentEmployeeKey]" );
    }

    public static void testPATHITEM(){
        doTest("=PATHITEM(PATH(Employee[EmployeeKey], Employee[ParentEmployeeKey]), 3, 1) ",
                "Tables:\n" +
                        "Employee\n" +
                        "\nFields:\n" +
                        "Employee[EmployeeKey]\n" +
                        "Employee[ParentEmployeeKey]" );
    }

    public static void testPATHITEMREVERSE(){
        doTest("=PATHITEMREVERSE(PATH(Employee[EmployeeKey], Employee[ParentEmployeeKey]), 3, 1)",
                "Tables:\n" +
                        "Employee\n" +
                        "\nFields:\n" +
                        "Employee[EmployeeKey]\n" +
                        "Employee[ParentEmployeeKey]" );
    }

    public static void testPATHLENGTH(){
        doTest("=PATHLENGTH(PATH(Employee[EmployeeKey], Employee[ParentEmployeeKey])) ",
                "Tables:\n" +
                        "Employee\n" +
                        "\nFields:\n" +
                        "Employee[EmployeeKey]\n" +
                        "Employee[ParentEmployeeKey]" );
    }

    public static void testADDCOLUMNS(){
        doTest("=ADDCOLUMNS(ProductCategory   \n" +
                        "               , \"Internet Sales\", SUMX(RELATEDTABLE(InternetSales_USD), InternetSales_USD[SalesAmount_USD])  \n" +
                        "               , \"Reseller Sales\", SUMX(RELATEDTABLE(ResellerSales_USD), ResellerSales_USD[SalesAmount_USD]))  ",
                "Tables:\n" +
                        "InternetSales_USD\n" +
                        "ProductCategory\n" +
                        "ResellerSales_USD\n" +
                        "\nFields:\n" +
                        "InternetSales_USD[SalesAmount_USD]\n" +
                        "ProductCategory\"Internet Sales\"\n" +
                        "ProductCategory\"Reseller Sales\"\n" +
                        "ResellerSales_USD[SalesAmount_USD]" );
    }

    public static void testAVERAGEA(){
        doTest("=AVERAGEA([Amount])",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[Amount]" );
    }

    public static void testCOUNTAX(){
        doTest("=COUNTAX(FILTER('Reseller',[Status]=\"Active\"),[Phone])  ",
                "Tables:\n" +
                        "'Reseller'\n" +
                        "\nFields:\n" +
                        "'Reseller'[Phone]\n" +
                        "'Reseller'[Status]" );
    }

    public static void testCOUNTROWS(){
        doTest("=COUNTROWS('Orders')",
                "Tables:\n" +
                        "'Orders'\n" +
                        "\nFields:" );
    }

    public static void testCOUNTX(){
        doTest("=COUNTX(FILTER(Product,RELATED(ProductSubcategory[EnglishProductSubcategoryName])=\"Caps\"), Product[ListPrice])",
                "Tables:\n" +
                        "Product\n" +
                        "ProductSubcategory\n" +
                        "\nFields:\n" +
                        "Product[ListPrice]\n" +
                        "ProductSubcategory[EnglishProductSubcategoryName]" );
    }

    public static void testGENERATE(){
        doTest("=GENERATE(  \n" +
                        "\tSUMMARIZE(SalesTerritory, SalesTerritory[SalesTerritoryGroup])  \n" +
                        "\t,SUMMARIZE(ProductCategory   \n" +
                        "\t\t\t\t\t\t, [ProductCategoryName]  \n" +
                        "\t\t\t\t\t\t, \"Reseller Sales\"\n" +
                        "\t\t\t\t\t\t, SUMX(RELATEDTABLE(ResellerSales_USD), ResellerSales_USD[SalesAmount_USD])  \n" +
                        "\t)  \n" +
                        ")",
                "Tables:\n" +
                        "ProductCategory\n" +
                        "ResellerSales_USD\n" +
                        "SalesTerritory\n" +
                        "\nFields:\n" +
                        "ProductCategory[ProductCategoryName]\n" +
                        "ResellerSales_USD[SalesAmount_USD]\n" +
                        "SalesTerritory[SalesTerritoryGroup]" );
    }

    public static void testMINX(){
        doTest("=MINX( FILTER(InternetSales, [SalesTerritoryKey] = 5),[Freight]) ",
                "Tables:\n" +
                        "InternetSales\n" +
                        "\nFields:\n" +
                        "InternetSales[Freight]\n" +
                        "InternetSales[SalesTerritoryKey]" );
    }

    public static void testRANKEQ(){
        doTest("=RANK.EQ(InternetSales_USD[SalesAmount_USD], InternetSales_USD[SalesAmount_USD]) ",
                "Tables:\n" +
                        "InternetSales_USD\n" +
                        "\nFields:\n" +
                        "InternetSales_USD[SalesAmount_USD]" );
    }

    public static void testROW(){
        doTest("=ROW(\"Internet Total Sales (USD)\", SUM(InternetSales_USD[SalesAmount_USD]),  \n" +
                        "         \"Resellers Total Sales (USD)\", SUM(ResellerSales_USD[SalesAmount_USD]))   ",
                "Tables:\n" +
                        "default\n" +
                        "InternetSales_USD\n" +
                        "ResellerSales_USD\n" +
                        "\nFields:\n" +
                        "default\"Internet Total Sales (USD)\"\n" +
                        "default\"Resellers Total Sales (USD)\"\n" +
                        "InternetSales_USD[SalesAmount_USD]\n" +
                        "ResellerSales_USD[SalesAmount_USD]" );
    }

    public static void testSAMPLE(){
        doTest("=SAMPLE(10,InternetSales_USD,[SalesAmount_USD]+1,asc)",
                "Tables:\n" +
                        "InternetSales_USD\n" +
                        "\nFields:\n" +
                        "InternetSales_USD[SalesAmount_USD]" );
    }

    public static void testSELECTCOLUMNS(){
        doTest("=SELECTCOLUMNS(Info, \"StateCountry\", [State]&\", \"&[Country])",
                "Tables:\n" +
                        "Info\n" +
                        "\nFields:\n" +
                        "Info\"StateCountry\"\n" +
                        "Info[Country]\n" +
                        "Info[State]" );
    }

    public static void testSTDEVXdotP(){
        doTest("=STDEVX.P(RELATEDTABLE(InternetSales_USD), InternetSales_USD[UnitPrice_USD] - (InternetSales_USD[DiscountAmount_USD]/InternetSales_USD[OrderQuantity])) ",
                "Tables:\n" +
                        "InternetSales_USD\n" +
                        "\nFields:\n" +
                        "InternetSales_USD[DiscountAmount_USD]\n" +
                        "InternetSales_USD[OrderQuantity]\n" +
                        "InternetSales_USD[UnitPrice_USD]" );
    }


    public static void testTOPN(){
        doTest("=SUMX(\n" +
                        "\t\t\tTOPN(10, SUMMARIZE(Product, [ProductKey], \"TotalSales\"\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t, SUMX(RELATED(InternetSales_USD[SalesAmount_USD]), InternetSales_USD[SalesAmount_USD]) \n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t+ SUMX(RELATED(ResellerSales_USD[SalesAmount_USD]), ResellerSales_USD[SalesAmount_USD])\n" +
                        "\t\t\t\t\t\t\t\t\t\t\t\t),1\n" +
                        "\t\t\t\t\t),1\n" +
                        "\t\t\t)",
                "Tables:\n" +
                        "InternetSales_USD\n" +
                        "Product\n" +
                        "ResellerSales_USD\n" +
                        "\nFields:\n" +
                        "InternetSales_USD[SalesAmount_USD]\n" +
                        "Product[ProductKey]\n" +
                        "ResellerSales_USD[SalesAmount_USD]" );
    }

    public static void testVARXdotP(){
        doTest("=VARX.P(InternetSales_USD, InternetSales_USD[UnitPrice_USD] -(InternetSales_USD[DiscountAmount_USD]/InternetSales_USD[OrderQuantity]))\n",
                "Tables:\n" +
                        "InternetSales_USD\n" +
                        "\nFields:\n" +
                        "InternetSales_USD[DiscountAmount_USD]\n" +
                        "InternetSales_USD[OrderQuantity]\n" +
                        "InternetSales_USD[UnitPrice_USD]" );
    }

    public static void testXNPV(){
        doTest("=XNPV( CashFlows, [Payment], [Date], 0.09 )  ",
                "Tables:\n" +
                        "CashFlows\n" +
                        "\nFields:\n" +
                        "CashFlows[Date]\n" +
                        "CashFlows[Payment]" );
    }

    public static void testCONCATENATEX(){
        doTest("=CONCATENATEX(Employees, [FirstName] & \" \" & [LastName], \",\")",
                "Tables:\n" +
                        "Employees\n" +
                        "\nFields:\n" +
                        "Employees[FirstName]\n" +
                        "Employees[LastName]" );
    }

    public static void testFIND(){
        doTest("=FIND(\"BMX\",\"line of BMX racing goods\")",
                "Tables:\n" +
                        "\nFields:" );
    }

    public static void testLEFT(){
        doTest("=CONCATENATE(LEFT('Reseller'[ResellerName]),LEFT([GeographyKey],3))",
                "Tables:\n" +
                        "'Reseller'\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "'Reseller'[ResellerName]\n" +
                        "default[GeographyKey]" );
    }


    public static void testLEN(){
        doTest("=LEN([AddressLine1])+LEN([AddressLin2]) ",
                "Tables:\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "default[AddressLin2]\n" +
                        "default[AddressLine1]" );
    }

    public static void testRIGHT(){
        doTest("=RIGHT('New Products'[ProductCode],[MyCount]) ",
                "Tables:\n" +
                        "'New Products'\n" +
                        "\nFields:\n" +
                        "'New Products'[MyCount]\n" +
                        "'New Products'[ProductCode]" );
    }

    public static void testEVALUATE(){
        doTest("EVALUATE ROW(  \n" +
                        "  \"$$ in WA\"  \n" +
                        "    , CALCULATE('Internet Sales'[Internet Total Sales]  \n" +
                        "                , 'Geography'[State Province Code]=\"WA\"  \n" +
                        "      )  \n" +
                        ", \"$$ in WA and OR\"  \n" +
                        "    , CALCULATE('Internet Sales'[Internet Total Sales]  \n" +
                        "               , 'Geography'[State Province Code]=\"WA\"   \n" +
                        "                 || 'Geography'[State Province Code]=\"OR\"  \n" +
                        "      )  \n" +
                        ", \"$$ in WA and BC\"  \n" +
                        "    , CALCULATE('Internet Sales'[Internet Total Sales]  \n" +
                        "               , 'Geography'[State Province Code]=\"WA\"   \n" +
                        "                 || 'Geography'[State Province Code]=\"BC\"  \n" +
                        "      )  \n" +
                        ", \"$$ in WA and OR ??\"  \n" +
                        "    , CALCULATE(  \n" +
                        "          CALCULATE('Internet Sales'[Internet Total Sales]  \n" +
                        "                    ,'Geography'[State Province Code]=\"WA\"   \n" +
                        "                      || 'Geography'[State Province Code]=\"OR\"  \n" +
                        "          )  \n" +
                        "          , 'Geography'[State Province Code]=\"WA\"   \n" +
                        "            || 'Geography'[State Province Code]=\"BC\"  \n" +
                        "      )  \n" +
                        ", \"$$ in WA !!\"  \n" +
                        "    , CALCULATE(  \n" +
                        "          CALCULATE('Internet Sales'[Internet Total Sales]  \n" +
                        "                   , KEEPFILTERS('Geography'[State Province Code]=\"WA\"   \n" +
                        "                              || 'Geography'[State Province Code]=\"OR\"  \n" +
                        "                     )  \n" +
                        "          )  \n" +
                        "          , 'Geography'[State Province Code]=\"WA\"   \n" +
                        "            || 'Geography'[State Province Code]=\"BC\"  \n" +
                        "      )  \n" +
                        ") ",
                "Tables:\n" +
                        "'Geography'\n" +
                        "'Internet Sales'\n" +
                        "default\n" +
                        "\nFields:\n" +
                        "'Geography'[State Province Code]\n" +
                        "'Internet Sales'[Internet Total Sales]\n" +
                        "default\"$$ in WA !!\"\n" +
                        "default\"$$ in WA and BC\"\n" +
                        "default\"$$ in WA and OR ??\"\n" +
                        "default\"$$ in WA and OR\"\n" +
                        "default\"$$ in WA\"" );
    }

    public static void testEVALUATE2(){
        doTest("EVALUATE\n" +
                        "CALCULATETABLE (\n" +
                        "    ADDCOLUMNS (\n" +
                        "        FILTER (\n" +
                        "            VALUES ( Product[Product Name] ),\n" +
                        "            [SalesAmount]\n" +
                        "                >= CALCULATE ( [SalesAmount], ALL ( Product ) ) * 0.01\n" +
                        "        ),\n" +
                        "        \"SalesOfProduct\", [SalesAmount]\n" +
                        "    ),\n" +
                        "    Product[Color] = \"Black\"\n" +
                        ")",
                "Tables:\n" +
                        "Product\n" +
                        "\nFields:\n" +
                        "Product\"SalesOfProduct\"\n" +
                        "Product[Color]\n" +
                        "Product[Product Name]\n" +
                        "Product[SalesAmount]" );
    }

    public static void testEVALUATE3(){
        doTest("EVALUATE\n" +
                        "CALCULATETABLE (\n" +
                        "    ADDCOLUMNS (\n" +
                        "        VAR\n" +
                        "            OnePercentOfSales = [SalesAmount1] * 0.01\n" +
                        "        RETURN\n" +
                        "            FILTER (\n" +
                        "                VALUES ( Product[Product Name] ),\n" +
                        "                [SalesAmount2] >= OnePercentOfSales\n" +
                        "            ),\n" +
                        "        \"SalesOfProduct\", [SalesAmount]\n" +
                        "    ),\n" +
                        "    Product[Color] = \"Black\")",
                "Tables:\n" +
                        "Product\n" +
                        "\nFields:\n" +
                        "Product\"SalesOfProduct\"\n" +
                        "Product[Color]\n" +
                        "Product[Product Name]\n" +
                        "Product[SalesAmount1]\n" +
                        "Product[SalesAmount2]\n" +
                        "Product[SalesAmount]" );
    }

    public static void testEVALUATE4(){
        doTest("[Growth %] :=\n" +
                        "VAR\n" +
                        "    CurrentSales = SUM ( Sales[Quantity] )\n" +
                        "VAR\n" +
                        "    SalesLastYear = CALCULATE (\n" +
                        "        SUM ( Sales[Quantity] ),\n" +
                        "        SAMEPERIODLASTYEAR ( 'Date'[Date] )\n" +
                        "    )\n" +
                        "RETURN\n" +
                        "    IF (\n" +
                        "        AND ( CurrentSales <> 0, SalesLastYear <> 0 ),\n" +
                        "        DIVIDE (\n" +
                        "            CurrentSales - SalesLastYear,\n" +
                        "            SalesLastYear\n" +
                        "        )\n" +
                        "    )",
                "Tables:\n" +
                        "'Date'\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "'Date'[Date]\n" +
                        "Sales[Quantity]" );
    }

    public static void testEVALUATE5(){
        doTest("[RedSalesLastYear] :=\n" +
                        "VAR\n" +
                        "    RedProducts = FILTER (\n" +
                        "        ALL ( Product[Color] ),\n" +
                        "        Product[Color] = \"Red\"\n" +
                        "    )\n" +
                        "VAR\n" +
                        "    LastYear = SAMEPERIODLASTYEAR ( 'Date'[Date] )\n" +
                        "RETURN\n" +
                        "    CALCULATE ( SUM ( Sales[Quantity] ), RedProducts, LastYear )",
                "Tables:\n" +
                        "'Date'\n" +
                        "Product\n" +
                        "Sales\n" +
                        "\nFields:\n" +
                        "'Date'[Date]\n" +
                        "Product[Color]\n" +
                        "Sales[Quantity]" );
    }

    public static void testEVALUATEDefine(){
        doTest("define  \n" +
                        "measure 'Reseller Sales'[Reseller Sales Amount]=sum('Reseller Sales'[Sales Amount])  \n" +
                        "measure 'Reseller Sales'[Reseller Grand Total]=calculate(sum('Reseller Sales'[Sales Amount]), ALL('Reseller Sales'))  \n" +
                        "measure 'Reseller Sales'[Reseller Visual Total]=calculate(sum('Reseller Sales'[Sales Amount]), ALLSELECTED())  \n" +
                        "measure 'Reseller Sales'[Reseller Visual Total for All of Calendar Year]=calculate(sum('Reseller Sales'[Sales Amount]), ALLSELECTED('Date'[Calendar Year]))  \n" +
                        "measure 'Reseller Sales'[Reseller Visual Total for All of Product Category Name]=calculate(sum('Reseller Sales'[Sales Amount]), ALLSELECTED('Product Category'[Product Category Name]))  \n" +
                        "evaluate  \n" +
                        "CalculateTable(  \n" +
                        "    //CT table expression  \n" +
                        "    summarize(   \n" +
                        "//summarize table expression  \n" +
                        "crossjoin(distinct('Product Category'[Product Category Name]), distinct('Date'[Calendar Year]))  \n" +
                        "//First Group by expression  \n" +
                        ", 'Product Category'[Product Category Name]  \n" +
                        "//Second Group by expression  \n" +
                        ", 'Date'[Calendar Year]  \n" +
                        "//Summary expressions  \n" +
                        ", \"Reseller Sales Amount\", [Reseller Sales Amount]  \n" +
                        ", \"Reseller Grand Total\", [Reseller Grand Total]  \n" +
                        ", \"Reseller Visual Total\", [Reseller Visual Total]  \n" +
                        ", \"Reseller Visual Total for All of Calendar Year\", [Reseller Visual Total for All of Calendar Year]  \n" +
                        ", \"Reseller Visual Total for All of Product Category Name\", [Reseller Visual Total for All of Product Category Name]  \n" +
                        ")  \n" +
                        "//CT filters  \n" +
                        ", 'Sales Territory'[Sales Territory Group]=\"Europe\", 'Promotion'[Promotion Type]=\"Volume Discount\"  \n" +
                        ")  \n" +
                        "order by [Product Category Name], [Calendar Year] ",
                "Tables:\n" +
                        "'Date'\n" +
                        "'Product Category'\n" +
                        "'Promotion'\n" +
                        "'Reseller Sales'\n" +
                        "'Sales Territory'\n" +
                        "crossjoin\n" +
                        "\nFields:\n" +
                        "'Date'[Calendar Year]\n" +
                        "'Product Category'[Product Category Name]\n" +
                        "'Promotion'[Promotion Type]\n" +
                        "'Reseller Sales'[Sales Amount]\n" +
                        "'Sales Territory'[Sales Territory Group]\n" +
                        "crossjoin[Calendar Year]\n" +
                        "crossjoin[Product Category Name]\n" +
                        "crossjoin[Reseller Grand Total]\n" +
                        "crossjoin[Reseller Sales Amount]\n" +
                        "crossjoin[Reseller Visual Total for All of Calendar Year]\n" +
                        "crossjoin[Reseller Visual Total for All of Product Category Name]\n" +
                        "crossjoin[Reseller Visual Total]" );
    }

}
