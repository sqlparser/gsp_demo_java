package demos.gettablecolumns;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import demos.joinRelationAnalyze.joinRelationAnalyze;

class myMetaDB implements IMetaDatabase {

    String columns[][] = {
        {"server","db","DW","AcctInfo_PT","ACCT_ID"},
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

class TInfoRecord {

    public String getSPString(){
        if (getSPName() == null) return "N/A";
        else return getSPName().toString();
    }

    String getTableStr(TTable table){
        String tableName ="";
        if (table.getTableType() == ETableSource.subquery) {
            tableName = "(subquery, alias:" + table.getAliasName() + ")";
        }else{
            tableName = table.getTableName().toString();
            if (table.isLinkTable()){
                tableName = tableName+"("+table.getLinkTable().getTableName().toString()+")";
            }else if (table.isCTEName()){
                tableName = tableName+"(CTE)";
            }
        }

        return tableName;
    }

    public String getFullColumnName(){
        if (dbObjectType != EDbObjectType.column) return "";
        String columnName = getColumn().getColumnNameOnly();

        if (getTable() != null 
        		&& getTable( ).getObjectNameReferences( ).searchColumnReference( getColumn() )!=-1){
            if (getTableStr(getTable()).length() > 0){
                columnName = getTableStr(getTable())+"."+columnName;
            }
            schemaName = getTable().getPrefixSchema();
            if (schemaName.length() > 0) {
                columnName = schemaName+"."+columnName;
            }
            return columnName;
        }
        
       return getColumn().toString( );
    }

    public String printMe(boolean includingTitle){
        String spTitle="\nfilename|spname|object type\n";
        String tableTitle="\nfilename|spname|object type|schema|table|table effect\n";
        String columnTitle="\nfilename|spname|object type|schema|table|column|location|coordinate|datatype\n";
        String indexTitle="\nfilename|spname|object type|schema|index|table|column|location|coordinate\n";

        String schemaName = "N/A";
        String tableName = "unknownTable";
        String indexName = "unknownIndex";

        StringBuffer  sb = new StringBuffer (1024);
        switch (dbObjectType){
            case procedure:
                if (includingTitle) sb.append(spTitle);
                sb.append(getFileName()+"|"+getSPName().toString()+"|"+dbObjectType);
                break;
            case table:
                if (includingTitle) sb.append(tableTitle);

                tableName = getTableStr(getTable());
                schemaName = getTable().getPrefixSchema();
                if (schemaName.length() == 0) schemaName ="N/A";

                sb.append(getFileName()+"|"+getSPString()+"|"+dbObjectType+"|"+schemaName+"|"+tableName+"|"+getTable().getEffectType());
                break;
            case column:
                if (includingTitle) sb.append(columnTitle);
                if (getTable() != null){
                    //it's an orphan column
                    tableName = getTableStr(getTable());
                    schemaName = getTable().getPrefixSchema();
                    if (schemaName.length() == 0) schemaName ="N/A";
                }else{
                    tableName = "missed";
                }

                String datatypeStr = "";
                if ((getColumn().getLinkedColumnDef() != null)){
                    //column in create table, add datatype information as well
                    TTypeName datatype = getColumn().getLinkedColumnDef().getDatatype();
                    datatypeStr = datatype.getDataTypeName();
                    if (datatype.getLength() != null){
                        datatypeStr = datatypeStr+":"+datatype.getLength().toString();
                    } else if (datatype.getPrecision() != null){
                        datatypeStr = datatypeStr+":"+datatype.getPrecision().toString();
                        if (datatype.getScale() != null){
                            datatypeStr = datatypeStr+":"+datatype.getScale().toString();
                        }
                    }
                }
                sb.append(getFileName()+"|"+getSPString()+"|"+dbObjectType+"|"+schemaName+"|"+tableName+"|"+getColumn().toString()+"|"+getColumn().getLocation()+"|("+getColumn().coordinate()+")|"+datatypeStr);
                break;
            case index:
                if (includingTitle) sb.append(indexTitle);
                if (getTable() != null){
                    schemaName = getTable().getPrefixSchema();
                    if (schemaName.length() == 0) schemaName ="N/A";
                    tableName = getTable().getTableName().toString();
                }
                if (getIndex() != null){
                    indexName = getIndex().toString();
                }
                sb.append(getFileName()+"|"+getSPString()+"|"+dbObjectType+"|"+schemaName+"|"+indexName+"|"+tableName+"|"+getColumn().toString()+"|"+getColumn().getLocation()+"|("+getColumn().coordinate()+")");
                break;
        }

        return sb.toString();
    }

    private TObjectName index;

    public void setIndex(TObjectName index) {
        this.index = index;
    }

    public TObjectName getIndex() {

        return index;
    }

    private EDbObjectType dbObjectType;

    public void setDbObjectType(EDbObjectType dbObjectType) {
        this.dbObjectType = dbObjectType;
    }

    public EDbObjectType getDbObjectType() {

        return dbObjectType;
    }

    public TInfoRecord(){

    }

    public TInfoRecord(EDbObjectType dbObjectType){
        this.dbObjectType = dbObjectType;
    }

    public TInfoRecord(TTable table){
      this.table = table;
        this.dbObjectType = EDbObjectType.table;
    }

    public TInfoRecord(TInfoRecord clone, EDbObjectType dbObjectType){
        this.fileName = clone.fileName;
        this.SPName = clone.SPName;
        this.table = clone.table;
        this.column = clone.column;
        this.dbObjectType = dbObjectType;
    }

    private String fileName = "N/A";
    private TObjectName SPName ; //stored procedure name

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSPName(TObjectName SPName) {
        this.SPName = SPName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getFileName() {

        return fileName;
    }

    public TObjectName getSPName() {
        return SPName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    private String schemaName;

//    public String tableName;
//    public String columnName;

    private TTable table;

    public void setTable(TTable table) {
        this.table = table;
    }

    public void setColumn(TObjectName column) {
        this.column = column;
    }

    public TTable getTable() {

        return table;
    }

    public TObjectName getColumn() {
        return column;
    }

    private TObjectName column;
}

public class TGetTableColumn{


    private EDbVendor dbVendor;
    private String queryStr;
    private TGSqlParser sqlParser;
    private IMetaDatabase metaDatabase = null;

    public void setMetaDatabase(IMetaDatabase metaDatabase) {
        this.metaDatabase = metaDatabase;
        sqlParser.setMetaDatabase(metaDatabase);
    }

    private TSQLEnv sqlEnv = null;

    public void setSqlEnv(TSQLEnv sqlEnv) {
        this.sqlEnv = sqlEnv;
        sqlParser.setSqlEnv(sqlEnv);
    }

    private StringBuffer functionlist,schemalist,
    triggerlist,sequencelist,databaselist;

    public StringBuffer infos;

    public StringBuffer outList;

    private ArrayList<TInfoRecord> infoList;

    private ArrayList<String> fieldlist,tablelist,indexList,cteList;

    private StringBuffer tableColumnList;

    private String newline  = "\n";

    private String sqlFileName ="N/A";

    public boolean isConsole;
    public boolean listStarColumn;

    public boolean showTableEffect;
    public boolean showColumnLocation;
    public boolean showDatatype;
    public boolean showIndex;
    public boolean showColumnsOfCTE;
        public boolean linkOrphanColumnToFirstTable;
    public boolean showDetail = false;
    public boolean showSummary = true;
    public boolean showTreeStructure = false;
    public boolean showBySQLClause = false;
    public boolean showJoin = false;
    public boolean showCTE = false;

    private Stack<TStoredProcedureSqlStatement> spList;


    class myTokenListHandle implements ITokenListHandle {
        // 把 ${tx_date_yyyymm} 合并为一个token，token code为 TBasetype.ident
        public boolean processTokenList(TSourceTokenList sourceTokenList){
            int startIndex = -1;
            int endIndex = -1;

            for(int i=0;i< sourceTokenList.size();i++) {
                TSourceToken token = sourceTokenList.get(i);

                // Check for '$' followed immediately by '{'
                if (token.tokencode == 36) { // Check for '$'
                    if (i + 1 < sourceTokenList.size() && sourceTokenList.get(i + 1).tokencode == 123) { // Check for '{' immediately after '$'
                        startIndex = i;
                    }
                } else if (token.tokencode == 125 && startIndex != -1) { // Check for '}'
                    endIndex = i;

                }


                if (startIndex != -1 && endIndex != -1) {
                    TSourceToken firstToken = sourceTokenList.get(startIndex);
                    firstToken.tokencode = TBaseType.ident;
                    for (int j = startIndex + 1; j <= endIndex; j++) {
                        TSourceToken st = sourceTokenList.get(j);
                        st.tokenstatus = ETokenStatus.tsdeleted;
                        firstToken.setString(firstToken.astext + st.astext);
                    }

                    //System.out.println("Found variable token: " + firstToken.toStringDebug());

                    startIndex = -1;
                    endIndex = -1;
                }
            }
            return true;
        }
    }

    String dotChar = ".";
    public  TGetTableColumn(EDbVendor pDBVendor){
       dbVendor = pDBVendor;

        sqlParser = new TGSqlParser(dbVendor);
        // sqlParser.setTokenListHandle(new myTokenListHandle());
        //sqlParser.setMetaDatabase(new myMetaDB());

        if (dbVendor == EDbVendor.dbvdax){
            dotChar = "";
        }
        tablelist = new ArrayList<String>();
        fieldlist = new ArrayList<String>();
        indexList = new ArrayList<String>();
        cteList = new ArrayList<String>();
        infoList = new ArrayList<TInfoRecord>();

        spList = new Stack<TStoredProcedureSqlStatement>();

         infos = new StringBuffer();
         functionlist = new StringBuffer();
         schemalist = new StringBuffer();
         triggerlist = new StringBuffer();
         sequencelist = new StringBuffer();
         databaselist  = new StringBuffer();
        tableColumnList  = new StringBuffer();
        outList  = new StringBuffer();

        isConsole = true;
        listStarColumn = false;
        showTreeStructure = false;
        showTableEffect = false;
        showColumnLocation = false;
        linkOrphanColumnToFirstTable = true;
        showDatatype = false;
        showIndex = false;
        showCTE = false;
        showColumnsOfCTE = false;
    }

    public void runText(String pQuery){
        run(pQuery,false);
    }

    public void runFile(String pFileName){
        sqlFileName = pFileName;
        run(pFileName,true);
    }

    String numberOfSpace(int pNum){
        String ret="";
        for(int i=0;i<pNum;i++){
            ret = ret+" ";
        }
        return ret;
    }

    public StringBuffer getInfos() {
        return infos;
    }


    protected void run(String pQuery, boolean isFile){
        queryStr = pQuery;
        if (isFile) sqlParser.sqlfilename = pQuery;
        else sqlParser.sqltext = pQuery;
        int iRet = sqlParser.parse();
        if (iRet != 0){
        	if(isConsole)
        		System.out.println(sqlParser.getErrormessage());
        	else 
        		throw new RuntimeException(sqlParser.getErrormessage());
            return;
        }

        outList.setLength(0);
        tablelist.clear();
        fieldlist.clear();
        indexList.clear();
        cteList.clear();

        for(int i=0;i<sqlParser.sqlstatements.size();i++){
            analyzeStmt(sqlParser.sqlstatements.get(i),0);
        }


       // print detailed info
        if (showDetail){
            boolean includingTitle = true;
            for(int i=0;i<infoList.size();i++){
                if (i>0){
                    includingTitle = !(infoList.get(i).getDbObjectType() == infoList.get(i-1).getDbObjectType());
                }
				outputResult( infoList.get( i ).printMe( includingTitle ) );
            }
        }

        // print summary info
        if (showSummary){
            removeDuplicateAndSort(tablelist);
            removeDuplicateAndSort(fieldlist);
            removeDuplicateAndSort(indexList);
            removeDuplicateAndSort(cteList);

            printArray("Tables:", tablelist);
            outputResult("");
            printArray("Fields:",fieldlist);
            if(showIndex && (indexList.size() > 0)){
                printArray("Indexs:",indexList);
            }

            if(showColumnsOfCTE && (cteList.size() > 0)){
                outputResult("");
                printArray("Ctes:",cteList);
            }
        }

        // print tree structure
        if (showTreeStructure){
			outputResult( infos.toString( ) );
        }

        if (showBySQLClause){
            ArrayList<ETableEffectType> tableEffectTypes = new ArrayList<ETableEffectType>();
            ArrayList<ESqlClause> columnClauses = new ArrayList<ESqlClause>();

            for(int i=0;i<infoList.size();i++){
                if (infoList.get(i).getDbObjectType() == EDbObjectType.table){
                    if (!tableEffectTypes.contains(infoList.get(i).getTable().getEffectType())){
                        tableEffectTypes.add(infoList.get(i).getTable().getEffectType());
                    }
                }
            }
            outputResult("Tables:");
            for(int j=0;j<tableEffectTypes.size();j++){
            	outputResult("\t"+tableEffectTypes.get(j).toString());

                for(int i=0;i<infoList.size();i++){
                    if (infoList.get(i).getDbObjectType() == EDbObjectType.table){
                        TTable lcTable = infoList.get(i).getTable();
                        if (lcTable.getEffectType() == tableEffectTypes.get(j) && lcTable.getSubquery()==null && lcTable.getTableName()!=null){
                        	outputResult("\t\t" + lcTable.toString()+ "("+lcTable.getTableName().coordinate()+")");
                        }
                    }
                }
            }

            // column
            for(int i=0;i<infoList.size();i++){
                if (infoList.get(i).getDbObjectType() == EDbObjectType.column){
                    if (!columnClauses.contains(infoList.get(i).getColumn().getLocation())){
                        columnClauses.add(infoList.get(i).getColumn().getLocation());
                    }
                }
            }
            outputResult("");
            outputResult("Columns:");
            for(int j=0;j<columnClauses.size();j++){
            	outputResult("\t"+columnClauses.get(j).toString());

                for(int i=0;i<infoList.size();i++){
                    if (infoList.get(i).getDbObjectType() == EDbObjectType.column){
                        TObjectName lcColumn = infoList.get(i).getColumn();
                        if (lcColumn.getLocation() == columnClauses.get(j)){
                        	outputResult("\t\t" +  infoList.get(i).getFullColumnName()+ "("+lcColumn.coordinate()+")");
                        }
                    }
                }
            }


        }
        
        if(showJoin){
        	joinRelationAnalyze analysis = new joinRelationAnalyze( sqlParser, showColumnLocation );
			outputResult( analysis.getAnalysisResult( ) );
        }

      //  System.out.println("Fields:"+newline+fieldlist.toString());
    }


	private void outputResult( String result)
	{
		if (isConsole){
		     System.out.println(result);
             //System.out.println(TBaseType.toHex(result,"UTF-8"));
		 }else {
		 	//if(outList.length()>0)
		 	//	outList.append(newline);
		     outList.append(result).append( newline);
		 }
	}


    void printArray(String pTitle,ArrayList<String> pList){
		outputResult( pTitle );
		Object str[] = pList.toArray( );
		for ( int i = 0; i < str.length; i++ )
		{
			outputResult( str[i].toString( ) );
		}
    }


    void removeDuplicateAndSort(ArrayList <String>  pList){
        Collections.sort(pList, new SortIgnoreCase() );

        for ( int i = 0 ; i < pList.size() - 1 ; i ++ ) {
             for ( int j = pList.size() - 1 ; j > i; j -- ) {
               if (pList.get(j).equalsIgnoreCase((pList.get(i)))) {
                 pList.remove(j);
               }
              }
            }
    }

    protected void analyzeStmt(TCustomSqlStatement stmt, int pNest){
        TTable lcTable = null;
        TObjectName lcColumn = null;
        String tn = "",cn="";

        if (stmt instanceof  TStoredProcedureSqlStatement){
            spList.push((TStoredProcedureSqlStatement)stmt);
            TInfoRecord spRecord = new TInfoRecord(EDbObjectType.procedure);
            spRecord.setSPName(spList.peek().getStoredProcedureName());
        }
        //System.out.println( numberOfSpace(pNest)+ stmt.sqlstatementtype);
        infos.append(numberOfSpace(pNest) + stmt.sqlstatementtype+newline);

        for(int i=0;i<stmt.tables.size();i++){
            //if  (stmt.tables.getTable(i).isBaseTable())
            //{
             lcTable = stmt.tables.getTable(i);
             if (showColumnsOfCTE && lcTable.isCTEName()){
                 for(TAttributeNode node:lcTable.getAttributes()){
                     cteList.add(node.getName());
                 }
             }
             TInfoRecord tableRecord = new TInfoRecord(lcTable);
            tableRecord.setFileName(this.sqlFileName);
            if (spList.size() > 0){
                tableRecord.setSPName(spList.peek().getStoredProcedureName());
            }
            infoList.add(tableRecord);

			if ( lcTable.getTableType( ) == ETableSource.subquery )
			{
				tn = "(subquery, alias:" + lcTable.getAliasName( ) + ")";
			}
			else if ( lcTable.getTableType( ) == ETableSource.tableExpr )
			{
				tn = "(table expression, alias:"
						+ lcTable.getAliasName( )
						+ ")";
			}
            else if ( lcTable.getTableType( ) == ETableSource.openquery )
            {
                tn = "(table openquery, alias:"
                        + lcTable.getAliasName( )
                        + ")";

                if (lcTable.getSubquery() != null){
                    analyzeStmt(lcTable.getSubquery(),pNest++);
                }
            }else if (lcTable.getTableType() == ETableSource.function){
                tn = "(table-valued function:"
                        + lcTable.getTableName( )
                        + ")";
            }
            else if (lcTable.getTableType() == ETableSource.pivoted_table){
                tn = "(pivot-table:"
                        + lcTable.getTableName( )
                        + ")";
            }
            else if (lcTable.getTableType() == ETableSource.unnest){
                tn = "(unnest-table:"
                        + lcTable.getAliasName( )
                        + ")";
            }
			else if ( lcTable.getTableName( ) != null )
			{
				tn = lcTable.getTableName( ).toString( );
				if ( lcTable.isLinkTable( ) )
				{
					tn = tn
							+ "("
							+ lcTable.getLinkTable( )
									.getTableName( )
									.toString( )
							+ ")";
				}
				else if ( lcTable.isCTEName( ) )
				{
					tn = tn + "(CTE)";
				}
			}
                //System.out.println(numberOfSpace(pNest+1)+tn.getName());
                if ((showTableEffect) &&(lcTable.isBaseTable())){
                    infos.append(numberOfSpace(pNest+1)+ tn+"("+lcTable.getEffectType()+")"+newline);
                }else{
                    infos.append(numberOfSpace(pNest+1)+ tn+newline);
                }

                tableColumnList.append(","+tn);

                if (!((lcTable.getTableType() == ETableSource.subquery)
                        || (lcTable.isCTEName()&&(!showCTE))
                        ||(lcTable.getTableType() == ETableSource.openquery)
                        ||(lcTable.getTableType() == ETableSource.function)) && lcTable.getTableName()!=null) {
                   if (lcTable.isLinkTable()){
                      // tablelist.append(lcTable.getLinkTable().toString()+newline);
                       tablelist.add(lcTable.getLinkTable().toString());
                   }else{
                      // tablelist.append(lcTable.toString()+newline);
                       tablelist.add(lcTable.getTableName().toString());
                   }
                }


                for (int j=0;j<stmt.tables.getTable(i).getLinkedColumns().size();j++){
                    lcColumn = stmt.tables.getTable(i).getLinkedColumns().getObjectName(j);
                    if (lcColumn.getValidate_column_status() == TBaseType.MARKED_NOT_A_COLUMN_IN_COLUMN_RESOLVER) continue;
                    TInfoRecord columnRecord = new TInfoRecord(tableRecord,EDbObjectType.column);
                    columnRecord.setColumn(lcColumn);
                    infoList.add(columnRecord);
                    cn = lcColumn.getColumnNameOnly();
                    if ((showDatatype)&&(lcColumn.getLinkedColumnDef() != null)){
                        //column in create table, add datatype information as well
                        TTypeName datatype = lcColumn.getLinkedColumnDef().getDatatype();
                        cn = cn + ":"+datatype.getDataTypeName();
                        if (datatype.getLength() != null){
                            cn = cn+":"+datatype.getLength().toString();
                        } else if (datatype.getPrecision() != null){
                            cn = cn+":"+datatype.getPrecision().toString();
                            if (datatype.getScale() != null){
                                cn = cn+":"+datatype.getScale().toString();
                            }
                        } else if (datatype.getDisplayLength() != null){
                            cn = cn+":"+datatype.getDisplayLength().toString();
                        }
                    }
                    //System.out.println(numberOfSpace(pNest+2)+cn.getColumnNameOnly());
                    if (showColumnLocation){
                        String  posStr = "";
//                        if ( lcColumn.getColumnToken() != null) {
//                            TSourceToken lcStartToken = lcColumn.getColumnToken();
//                            posStr ="("+ lcStartToken.lineNo+","+lcStartToken.columnNo+ ")";
//                        }
                        infos.append(numberOfSpace(pNest+3)+ lcColumn.getColumnNameOnly()+posStr+"("+lcColumn.getLocation()+")"+newline);
                    }else{
                        infos.append(numberOfSpace(pNest+3)+ lcColumn.getColumnNameOnly()+newline);
                    }

                    if (!((lcTable.getTableType() == ETableSource.subquery)||(lcTable.isCTEName()&&(!showCTE)))){
                         if ((listStarColumn) || (!(lcColumn.getColumnNameOnly().equals("*")))){
                             if (lcTable.isLinkTable()){
                                 fieldlist.add(lcTable.getLinkTable().getTableName() + dotChar + cn );
                             }else{
                                 fieldlist.add(tn + dotChar + cn );
                             }
                         }
                    }
                    tableColumnList.append(","+tn+dotChar+ cn);
                }
                //add by grq 2023.07.09 issue=I7ITBQ

//                if(stmt.sqlstatementtype.equals(ESqlStatementType.sstinsert)){
//                    TInsertSqlStatement insertStmt = (TInsertSqlStatement) stmt;
//                    if(insertStmt.getColumnList() == null || insertStmt.getColumnList().size()<=0){
//                        TSelectSqlStatement selectStmt = insertStmt.getSubQuery();
//                        if(selectStmt != null){
//                            for (int j=0;j<selectStmt.getResultColumnList().size();j++){
//                                TResultColumn rsColumn = selectStmt.getResultColumnList().getResultColumn(j);
//                                if(rsColumn.getLocation().equals(ESqlClause.unknown)){
//                                    rsColumn.setLocation(ESqlClause.selectList);
//                                }
//                                TInfoRecord columnRecord = new TInfoRecord(tableRecord,EDbObjectType.column);
//                                TObjectName objectCloumn = rsColumn.getAliasClause()==null? null: rsColumn.getAliasClause().getAliasName();
//                                if(objectCloumn == null){
//                                    objectCloumn = rsColumn.getColumnFullname();
//                                }
//                                columnRecord.setColumn(objectCloumn);
//                                infoList.add(columnRecord);
//                                cn = rsColumn.getColumnAlias();
//                                if(StringUtils.isEmpty(cn)){
//                                    cn = rsColumn.getColumnNameOnly();
//                                }
//                                if (showColumnLocation){
//                                    String  posStr = "";
//                                    infos.append(numberOfSpace(pNest+3)+ cn + posStr+"("+rsColumn.getLocation()+")"+newline);
//                                }else{
//                                    infos.append(numberOfSpace(pNest+3)+ cn + newline);
//                                }
//
//                                if (!((lcTable.getTableType() == ETableSource.subquery)||(lcTable.isCTEName()&&(!showCTE)))){
//                                    if ((listStarColumn) || (!(rsColumn.getColumnNameOnly().equals("*")))){
//                                        if (lcTable.isLinkTable()){
//                                            fieldlist.add(lcTable.getLinkTable().getTableName() + dotChar + cn );
//                                        }else{
//                                            fieldlist.add(tn + dotChar + cn );
//                                        }
//                                    }
//                                }
//                                tableColumnList.append(","+tn+dotChar+ cn);
//                            }
//                        }
//                    }
//                }
                //end by grq
            //}
        }

        if (stmt.getOrphanColumns().size() > 0){
           // infos.append(numberOfSpace(pNest+1)+" orphan columns:"+newline);
            String oc = "";
            for (int k=0;k<stmt.getOrphanColumns().size();k++){
                if ((stmt.getOrphanColumns().getObjectName(k).getResolveStatus() == TBaseType.RESOLVED_AND_FOUND)
                    ||(stmt.getOrphanColumns().getObjectName(k).getResolveStatus() == TBaseType.RESOLVED_BUT_AMBIGUOUS))
                continue;
                TInfoRecord columnRecord = new TInfoRecord(EDbObjectType.column);
                columnRecord.setColumn(stmt.getOrphanColumns().getObjectName(k));
                columnRecord.setFileName(this.sqlFileName);
                infoList.add(columnRecord);

                oc =  stmt.getOrphanColumns().getObjectName(k).getColumnNameOnly();// stmt.getOrphanColumns().getObjectName(k).toString();
                if (showColumnLocation){
                    infos.append(numberOfSpace(pNest+3)+oc+"("+stmt.getOrphanColumns().getObjectName(k).getLocation()+")"+newline);
                }else{
                    infos.append(numberOfSpace(pNest+3)+oc+newline);
                }

                if ((linkOrphanColumnToFirstTable)&&(stmt.getFirstPhysicalTable() != null)){
                    if ((listStarColumn) ||(!(oc.equalsIgnoreCase("*"))))
                    fieldlist.add(stmt.getFirstPhysicalTable().toString() + dotChar + oc );
                    columnRecord.setTable(stmt.getFirstPhysicalTable());
                }else {
                    fieldlist.add("missed"+dotChar + oc+"("+stmt.getOrphanColumns().getObjectName(k).coordinate()+")" );
                }
                tableColumnList.append(",missed"+dotChar+oc+newline);
            }
        }

        for(int i=0;i<stmt.getIndexColumns().size();i++){
            TColumnWithSortOrder indexColumn = stmt.getIndexColumns().getElement(i);
            TInfoRecord indexRecord = new TInfoRecord(EDbObjectType.index);
            indexRecord.setColumn(indexColumn.getColumnName());
            indexRecord.setFileName(this.sqlFileName);
            infoList.add(indexRecord);


            String tableName = "unknownTable";
            String indexName = "unknownIndex";
            if (indexColumn.getOwnerTable() != null){
                tableName = indexColumn.getOwnerTable().getTableName().toString();
                indexRecord.setTable(indexColumn.getOwnerTable());
            }
            if (indexColumn.getOwnerConstraint() != null){
                if (indexColumn.getOwnerConstraint().getConstraintName() != null){
                    indexName = indexColumn.getOwnerConstraint().getConstraintName().toString();
                    indexRecord.setIndex(indexColumn.getOwnerConstraint().getConstraintName());
                }
            }
            indexList.add(tableName + ":" + indexColumn.getColumnName().toString()+":"+indexName);
        }

        for (int i=0;i<stmt.getStatements().size();i++){
            analyzeStmt(stmt.getStatements().get(i),pNest+1);
        }

        if (stmt instanceof  TStoredProcedureSqlStatement){
            TStoredProcedureSqlStatement p = (TStoredProcedureSqlStatement)stmt;
            for(int i=0;i<p.getBodyStatements().size();i++){
                analyzeStmt(p.getBodyStatements().get(i),pNest+1);
            }
            spList.pop();
        }

    }
}

 class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }


