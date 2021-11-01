package gettablecolumn;
/*
 * Date: 15-4-23
 */

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.sqlenv.TSQLCatalog;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import junit.framework.TestCase;
import common.SqlFileList;
import common.gspCommon;

import java.io.*;


class TOracleServerEnv extends TSQLEnv {

    public TOracleServerEnv(){
        super(EDbVendor.dbvoracle);
        initSQLEnv();
    }

    @Override
    public void initSQLEnv() {

        // add a new database: db
        TSQLCatalog sqlCatalog = createSQLCatalog("db");
        // add a new schema: default
        TSQLSchema defaultSchema = sqlCatalog.createSchema("default");
        //add a new table: emp
        TSQLTable empTable = defaultSchema.createTable("emp");
        empTable.addColumn("ename");

        // add a new schema: DW
        TSQLSchema dwSchema = sqlCatalog.createSchema("DW");
        //add a new table: ImSysInfo_BC
        TSQLTable bcTable = dwSchema.createTable("ImSysInfo_BC");
        bcTable.addColumn("ACCT_ID");
        bcTable.addColumn("SystemOfRec");
        bcTable.addColumn("OpeningDate");

        //add a new table: AcctInfo_PT
        TSQLTable ptTab = dwSchema.createTable("AcctInfo_PT");
        ptTab.addColumn("SystemOfRec");
        ptTab.addColumn("OfficerCode");


    }
}

class myMetaDB implements IMetaDatabase {

    String columns[][] = {
        {"server","db","","emp","ename"},
        {"server","db","DW","ImSysInfo_BC","ACCT_ID"},
        {"server","db","DW","AcctInfo_PT","SystemOfRec"},
        {"server","db","DW","ImSysInfo_BC","SystemOfRec"},
        {"server","db","DW","AcctInfo_PT","OfficerCode"},
        {"server","db","DW","ImSysInfo_BC","OpeningDate"},
    };

    public boolean checkColumn(String server, String database,String schema, String table, String column){
       boolean bServer,bDatabase,bSchema,bTable,bColumn,bRet = false;
        for (int i=0; i<columns.length;i++){
            if ((server == null)||(server.length() == 0)){
                bServer = true;
            }else{
                bServer = columns[i][0].equalsIgnoreCase(server);
            }
            if (!bServer) continue;

            if ((database == null)||(database.length() == 0)){
                bDatabase = true;
            }else{
                bDatabase = columns[i][1].equalsIgnoreCase(database);
            }
            if (!bDatabase) continue;

            if ((schema == null)||(schema.length() == 0)){
                bSchema = true;
            }else{
                bSchema = columns[i][2].equalsIgnoreCase(schema);
            }

            if (!bSchema) continue;

            bTable = columns[i][3].equalsIgnoreCase(table);
            if (!bTable) continue;

            bColumn = columns[i][4].equalsIgnoreCase(column);
            if (!bColumn) continue;

            bRet =true;
            break;

        }

        return bRet;
    }

}


public class testNewTableColumn extends TestCase {


    public static void testDummy(){

       // System.out.println(getDesiredTablesColumns("c:\\prg\\gsp_sqlfiles\\TestCases\\java\\mssql\\dbobject\\update3.outj"));
        assertTrue(true);
    }


    public static String getDesiredTablesColumns(String sqlfile){
        String line;
       StringBuffer sb = new StringBuffer();
        String newline  = "\n";

        boolean isTable = false,isColumn = false;
        try{
            BufferedReader br = new BufferedReader( new FileReader(sqlfile) );

            try{
                while( (line = br.readLine()) != null){

                    if (line.trim().length() == 0) continue;
                    if (line.toLowerCase().indexOf("tables:") >= 0 ) {
                        isTable = true;
                        isColumn =false;
                        sb.append(line+newline);
                        continue;
                    }

                    if (line.toLowerCase().indexOf("fields:") >= 0 ) {
                       isTable = false;
                        isColumn =true;
                        sb.append(newline+line+newline);
                       continue;
                    }

                    if (line.toLowerCase().indexOf("functions:") >= 0 ) {
                        isTable = false;
                        isColumn =false;
                       break;
                    }

                    if (line.toLowerCase().indexOf("database:") >= 0 ) {
                        isTable = false;
                        isColumn =false;
                       break;
                    }

                    if (line.toLowerCase().indexOf("schema:") >= 0 ) {
                       isTable = false;
                        isColumn =false;
                       break;
                    }


                  if (isTable){
                      sb.append(line+newline);
                  }

                    if (isColumn){
                        sb.append(line+newline);
                    }
                }
                br.close();
                }catch(IOException e){
                  System.out.println(e.toString());
                }

        }catch(FileNotFoundException e){
            System.out.println(e.toString());
        }

       return  sb.toString();
    }

   static void doTest(EDbVendor pdbvendor, String pDir){
        TGetTableColumn getTableColumn = new TGetTableColumn(pdbvendor);
        getTableColumn.isConsole = false;
       getTableColumn.showDetail = false;
        //getTableColumn.runFile(sqlfile);
       SqlFileList sqlfiles = new SqlFileList(pDir,true);
       String strDesired,strActual;
       for(int k=0;k < sqlfiles.sqlfiles.size();k++){
          // System.out.println(sqlfiles.sqlfiles.get(k).toString());
           getTableColumn.runFile(sqlfiles.sqlfiles.get(k).toString());
           File f = new File(sqlfiles.sqlfiles.get(k).toString().replace(".sql",".outj"));
           if(f.exists() && !f.isDirectory()) {
               strDesired =  getDesiredTablesColumns(sqlfiles.sqlfiles.get(k).toString().replace(".sql",".outj"));
           }else {
               strDesired =  getDesiredTablesColumns(sqlfiles.sqlfiles.get(k).toString().replace(".sql",".out"));
           }

           strActual = getTableColumn.outList.toString();
           assertTrue("\nfile:"+sqlfiles.sqlfiles.get(k).toString()+"\n\ndesired:\n"+strDesired+"\nActual:\n"+strActual
                   ,strDesired.equalsIgnoreCase(strActual));
           //System.out.println(getTableColumn.outList.toString());
           //System.out.println(sqlfiles.sqlfiles.get(k).toString());
           //System.out.println(sqlfiles.sqlfiles.get(k).toString().replace(".sql",".out"));
    }
   }

  public static void testPlsqlVar(){
      TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
      getTableColumn.isConsole = false;
      getTableColumn.showDetail = false;
      //getTableColumn.setMetaDatabase(new myMetaDB());
      getTableColumn.setSqlEnv(new TOracleServerEnv());

      getTableColumn.runText("<<main>>\n" +
              "DECLARE\n" +
              "ename VARCHAR2(10) := 'KING';\n" +
              "BEGIN\n" +
              "DELETE FROM emp WHERE ename = main.ename;\n" +
              "end;");
       String strActual = getTableColumn.outList.toString();
//       System.out.println("<<main>>\n" +
//               "DECLARE\n" +
//               "ename VARCHAR2(10) := 'KING';\n" +
//               "BEGIN\n" +
//               "DELETE FROM emp WHERE ename = main.ename;\n" +
//               "end;");
     // System.out.println(strActual);
      assertTrue(strActual.trim().equalsIgnoreCase("Tables:\n" +
              "emp\n" +
              "\nFields:\n" +
              "emp.ename"));

  }

   public static void testOracle(){
       doTest(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PRIVATE + "java/oracle/dbobject/");
       doTest(EDbVendor.dbvoracle,gspCommon.BASE_SQL_DIR_PRIVATE + "fetchdbobject\\oracle\\");
   }

    public static void testSqlServer(){
        doTest(EDbVendor.dbvmssql,gspCommon.BASE_SQL_DIR_PRIVATE + "java/mssql/dbobject/");
        doTest(EDbVendor.dbvmssql,gspCommon.BASE_SQL_DIR_PRIVATE + "fetchdbobject\\mssql\\bydbobject\\");
    }

    public static void testTableEffectDelete(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("delete from employee\n" +
                "where department_id = \n" +
                "(select department_id\n" +
                "from departments\n" +
                "where department_name like '%Public%');");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstdelete\n" +
                " employee(tetDelete)\n" +
                "   department_id\n" +
                " sstselect\n" +
                "  departments(tetSelect)\n" +
                "    department_id\n" +
                "    department_name"));
    }

    public static void testColumnLocationDelete(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("delete from employee\n" +
                "where department_id = \n" +
                "(select department_id\n" +
                "from departments\n" +
                "where department_name like '%Public%');");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstdelete\n" +
                " employee(tetDelete)\n" +
                "   department_id(where)\n" +
                " sstselect\n" +
                "  departments(tetSelect)\n" +
                "    department_id(selectList)\n" +
                "    department_name(where)"));
    }

    public static void testTableEffectInsert1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("insert into departments(department_id,department_name,manager_id,location_id)\n" +
                "values(70,'Public Relations',100,1900);");

        String strActual = getTableColumn.getInfos().toString();
       // System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstinsert\n" +
                " departments(tetInsert)\n" +
                "   department_id\n" +
                "   department_name\n" +
                "   manager_id\n" +
                "   location_id"));
    }

    public static void testColumnLocationInsert1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("insert into departments(department_id,department_name,manager_id,location_id)\n" +
                "values(70,'Public Relations',100,1900);");

        String strActual = getTableColumn.getInfos().toString();
 //        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstinsert\n" +
                " departments(tetInsert)\n" +
                "   department_id(insertColumn)\n" +
                "   department_name(insertColumn)\n" +
                "   manager_id(insertColumn)\n" +
                "   location_id(insertColumn)"));
    }

    public static void testTableEffectInsert2(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("insert into sales_reps(id,name,salary,commission_pct)\n" +
                "select employee_id,last_name,salary,commission_pc\n" +
                "from employees\n" +
                "where job_id like '%REP%';");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstinsert\n" +
                " sales_reps(tetInsert)\n" +
                "   id\n" +
                "   name\n" +
                "   salary\n" +
                "   commission_pct\n" +
                " sstselect\n" +
                "  employees(tetSelect)\n" +
                "    employee_id\n" +
                "    last_name\n" +
                "    salary\n" +
                "    commission_pc\n" +
                "    job_id"));
    }

    public static void testColumnLocationInsert2(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("insert into sales_reps(id,name,salary,commission_pct)\n" +
                "select employee_id,last_name,salary,commission_pc\n" +
                "from employees\n" +
                "where job_id like '%REP%';");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstinsert\n" +
                " sales_reps(tetInsert)\n" +
                "   id(insertColumn)\n" +
                "   name(insertColumn)\n" +
                "   salary(insertColumn)\n" +
                "   commission_pct(insertColumn)\n" +
                " sstselect\n" +
                "  employees(tetSelect)\n" +
                "    employee_id(selectList)\n" +
                "    last_name(selectList)\n" +
                "    salary(selectList)\n" +
                "    commission_pc(selectList)\n" +
                "    job_id(where)"));
    }

    public static void testTableEffectUpdate(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("update employees e\n" +
                "set department_name =\n" +
                "(select department_name from deparments d\n" +
                "where e.department_id = d.department_id);");

        String strActual = getTableColumn.getInfos().toString();
//         System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstupdate\n" +
                " employees(tetUpdate)\n" +
                "   department_name\n" +
                "   department_id\n" +
                " sstselect\n" +
                "  deparments(tetSelect)\n" +
                "    department_name\n" +
                "    department_id"));
    }

    public static void testColumnLocationUpdate(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("update employees e\n" +
                "set department_name =\n" +
                "(select department_name from deparments d\n" +
                "where e.department_id = d.department_id);");

        String strActual = getTableColumn.getInfos().toString();
//         System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstupdate\n" +
                " employees(tetUpdate)\n" +
                "   department_name(set)\n" +
                "   department_id(where)\n" +
                " sstselect\n" +
                "  deparments(tetSelect)\n" +
                "    department_name(selectList)\n" +
                "    department_id(where)"));
    }

    public static void testTableEffectMerge(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("MERGE Production.UnitMeasure AS target\n" +
                "    USING (SELECT @UnitMeasureCode, @Name) AS source (UnitMeasureCode, Name)\n" +
                "    ON (target.UnitMeasureCode = source.UnitMeasureCode)\n" +
                "    WHEN MATCHED THEN \n" +
                "        UPDATE SET Name = source.Name\n" +
                "\tWHEN NOT MATCHED THEN\t\n" +
                "\t    INSERT (UnitMeasureCode, Name)\n" +
                "\t    VALUES (source.UnitMeasureCode, source.Name)\n" +
                "\t    OUTPUT deleted.*, $action, inserted.* INTO #MyTempTable;");

        String strActual = getTableColumn.getInfos().toString();
        // System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstmerge\n" +
                " Production.UnitMeasure(tetMerge)\n" +
                "   UnitMeasureCode\n" +
                "   Name\n" +
                "   UnitMeasureCode\n" +
                "   Name\n" +
                "   *\n" +
                "   $action\n" +
                "   *\n" +
                " (subquery, alias:source)\n" +
                "   UnitMeasureCode\n" +
                "   Name\n" +
                "   UnitMeasureCode\n" +
                "   Name\n" +
                " #MyTempTable(tetOutput)\n" +
                " sstselect"));
    }

    public static void testColumnLocationMerge(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("MERGE Production.UnitMeasure AS target\n" +
                "    USING (SELECT @UnitMeasureCode, @Name) AS source (UnitMeasureCode, Name)\n" +
                "    ON (target.UnitMeasureCode = source.UnitMeasureCode)\n" +
                "    WHEN MATCHED THEN \n" +
                "        UPDATE SET Name = source.Name\n" +
                "\tWHEN NOT MATCHED THEN\t\n" +
                "\t    INSERT (UnitMeasureCode, Name)\n" +
                "\t    VALUES (source.UnitMeasureCode, source.Name)\n" +
                "\t    OUTPUT deleted.*, $action, inserted.* INTO #MyTempTable;");

        String strActual = getTableColumn.getInfos().toString();
        //System.out.println(strActual);
        String requiredStr = "sstmerge\n" +
                " Production.UnitMeasure(tetMerge)\n" +
                "   UnitMeasureCode(joinCondition)\n" +
                "   Name(set)\n" +
                "   UnitMeasureCode(insertColumn)\n" +
                "   Name(insertColumn)\n" +
                "   *(output)\n" +
                "   $action(output)\n" +
                "   *(output)\n" +
                " (subquery, alias:source)\n" +
                "   UnitMeasureCode(joinCondition)\n" +
                "   Name(setValue)\n" +
                "   UnitMeasureCode(insertValues)\n" +
                "   Name(insertValues)\n" +
                " #MyTempTable(tetOutput)\n" +
                " sstselect";
        //System.out.println("Required:\n"+requiredStr+"\n\nActual:\n"+strActual);
        assertTrue(strActual.trim().equalsIgnoreCase(requiredStr));
    }

    public static void testTableEffectCreateTable1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("CREATE TABLE employees_demo\n" +
                "    ( employee_id    NUMBER(6)\n" +
                "    , first_name     VARCHAR2(20)\n" +
                "\t, SalesPersonID int NULL REFERENCES SalesPerson(SalesPersonID)\n" +
                "\t)");

        String strActual = getTableColumn.getInfos().toString();
//         System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstcreatetable\n" +
                " employees_demo(tetCreate)\n" +
                "   employee_id\n" +
                "   first_name\n" +
                "   SalesPersonID\n" +
                " SalesPerson(tetConstraintReference)\n" +
                "   SalesPersonID"));
    }

    public static void testColumnLocationCreateTable1(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("CREATE TABLE employees_demo\n" +
                "    ( employee_id    NUMBER(6)\n" +
                "    , first_name     VARCHAR2(20)\n" +
                "\t, SalesPersonID int NULL REFERENCES SalesPerson(SalesPersonID)\n" +
                "\t)");

        String strActual = getTableColumn.getInfos().toString();
//         System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstcreatetable\n" +
                " employees_demo(tetCreate)\n" +
                "   employee_id(createTable)\n" +
                "   first_name(createTable)\n" +
                "   SalesPersonID(createTable)\n" +
                " SalesPerson(tetConstraintReference)\n" +
                "   SalesPersonID(constraintRef)"));
    }


    public static void testTableEffectCreateTable2(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.runText("CREATE TABLE employees_demo\n" +
                "    ( ProductID    NUMBER(6)\n" +
                "    , SpecialOfferID     VARCHAR2(20)\n" +
                "\t ,CONSTRAINT FK_SpecialOfferProduct_SalesOrderDetail FOREIGN KEY\n" +
                " (ProductID, SpecialOfferID)\n" +
                "REFERENCES SpecialOfferProduct (ProductID, SpecialOfferID)\n" +
                "\t)");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstcreatetable\n" +
                " employees_demo(tetCreate)\n" +
                "   ProductID\n" +
                "   SpecialOfferID\n" +
                " SpecialOfferProduct(tetConstraintReference)\n" +
                "   ProductID\n" +
                "   SpecialOfferID"));
    }

    public static void testColumnLocationCreateTable2(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("CREATE TABLE employees_demo\n" +
                "    ( ProductID    NUMBER(6)\n" +
                "    , SpecialOfferID     VARCHAR2(20)\n" +
                "\t ,CONSTRAINT FK_SpecialOfferProduct_SalesOrderDetail FOREIGN KEY\n" +
                " (ProductID, SpecialOfferID)\n" +
                "REFERENCES SpecialOfferProduct (ProductID, SpecialOfferID)\n" +
                "\t)");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstcreatetable\n" +
                " employees_demo(tetCreate)\n" +
                "   ProductID(createTable)\n" +
                "   SpecialOfferID(createTable)\n" +
                " SpecialOfferProduct(tetConstraintReference)\n" +
                "   ProductID(constraintRef)\n" +
                "   SpecialOfferID(constraintRef)"));
    }


    public static void testColumnLocationJoin(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("select e.employee_id,l.city,d.department_name\n" +
                "from employee e\n" +
                "join department d\n" +
                "on d.department_id = e.department_id\n" +
                "join locations l\n" +
                "on d.location_id = l.location_id;");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstselect\n" +
                " employee(tetSelect)\n" +
                "   department_id(joinCondition)\n" +
                "   employee_id(selectList)\n" +
                " department(tetSelect)\n" +
                "   department_id(joinCondition)\n" +
                "   location_id(joinCondition)\n" +
                "   department_name(selectList)\n" +
                " locations(tetSelect)\n" +
                "   location_id(joinCondition)\n" +
                "   city(selectList)"));
    }

    public static void testColumnLocationSelect(){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showDetail = false;
        getTableColumn.showTableEffect = true;
        getTableColumn.showColumnLocation = true;
        getTableColumn.runText("select department_id,avg(salary)\n" +
                "from employees\n" +
                "where location_id  = 1000\n" +
                "group by department_id\n" +
                "having avg(salary) > 8000\n" +
                "order by sum(salary);");

        String strActual = getTableColumn.getInfos().toString();
//        System.out.println(strActual);
        assertTrue(strActual.trim().equalsIgnoreCase("sstselect\n" +
                " employees(tetSelect)\n" +
                "   department_id(selectList)\n" +
                "   salary(selectList)\n" +
                "   location_id(where)\n" +
                "   department_id(groupby)\n" +
                "   salary(having)\n" +
                "   salary(orderby)"));
    }


}
