package noetix;
/*
 * Date: 11-4-6
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TViewAliasClause;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import junit.framework.TestCase;

public class testViewAlias extends TestCase {

    public static void test1(){

          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
          sqlparser.sqltext = "CREATE OR REPLACE FORCE VIEW \"DEV_602_MEDIUM_TC\".\"AP20_INVOICE_DISTRIBUTIONS\" (\"A$ACCRUAL_POSTED_FLAG\", \"A$DISTR$ACCOUNT\", \"A$DISTR$COMPANY\", \"A$DISTR$DEPARTMENT\", \"A$DISTR$PRODUCT\", \"A$DISTR$SUB_ACCOUNT\", \"A$EXPENDITURE_ORGANIZATION_NAM\", \"A$GL_DATE\", \"A$INVOICE_DATE\", \"A$INVOICE_NUMBER\", \"A$POSTED_FLAG\", \"A$PROJECT_NAME\", \"A$PROJECT_NUMBER\", \"A$VENDOR_NAME\", \"A$VENDOR_SITE_CODE\", \"A$ZZ__________________________\", \"APLIAB$OPERATIONS_ACCOUNTING_F\", \"ACCRUAL_POSTED_FLAG\", \"AMOUNT\", \"AMOUNT_BASE\", \"ASSETS_ADDITION_CODE\", \"ASSETS_TRACKING_FLAG\", \"BASE_AMOUNT\", \"BASE_CURRENCY_CODE\", \"BASE_INVOICE_PRICE_VARIANCE\", \"BASE_QUANTITY_VARIANCE\", \"DESCRIPTION\", \"DISTR$ACCOUNT\", \"DISTR$COMPANY\", \"DISTR$DEPARTMENT\", \"DISTR$OPERATIONS_ACCOUNTING_FL\", \"DISTR$PRODUCT\", \"DISTR$SUB_ACCOUNT\", \"DISTRIBUTION_CREATION_DATE\", \"DISTRIBUTION_LINE_NUMBER\", \"EARLIEST_SETTLEMENT_DATE\", \"EXCHANGE_DATE\", \"EXCHANGE_RATE\", \"EXCHANGE_RATE_TYPE\", \"EXCHANGE_RATE_VARIANCE\", \"EXPENDITURE_ITEM_DATE\", \"EXPENDITURE_ORGANIZATION_NAME\", \"EXPENDITURE_TYPE\", \"EXPENSE_JUSTIFICATION\", \"FINAL_MATCH_FLAG\", \"GL_DATE\", \"HOLD_MATCH_STATUS\", \"INV$ATTRIBUTE_CATEGORY\", \"INV$MISC_VENDOR_ADDRESS\", \"INV$MISC_VENDOR_CITY\", \"INV$MISC_VENDOR_NAME\", \"INV$MISC_VENDOR_STATE\", \"INV$MISC_VENDOR_ZIP\", \"INCOME_TAX_REGION\", \"INCOME_TAX_TYPE\", \"INVOICE_CURRENCY_CODE\", \"INVOICE_DATE\", \"INVOICE_NUMBER\", \"INVOICE_PAYMENT_STATUS\", \"INVOICE_PRICE_VARIANCE\", \"INVOICE_PRICE_VARIANCE_BASE\", \"INVOICE_SOURCE\", \"INVOICE_TYPE_LOOKUP_CODE\", \"JOURNAL_BATCH_DESCRIPTION\", \"JOURNAL_BATCH_NAME\", \"JOURNAL_BATCH_POSTED_DATE\", \"JOURNAL_BATCH_STATUS\", \"LINE_TYPE_LOOKUP_CODE\", \"PA_ADDITION_FLAG\", \"PA_QUANTITY\", \"PERIOD_NAME\", \"POSTED_AMOUNT\", \"POSTED_AMOUNT_BASE\", \"POSTED_BASE_AMOUNT\", \"POSTED_FLAG\", \"PREPAY_AMOUNT_REMAINING\", \"PREPAY_AMOUNT_REMAINING_BASE\", \"PRICE_VAR$OPERATIONS_ACCOUNTIN\", \"PROJECT_NAME\", \"PROJECT_NUMBER\", \"QUANTITY_INVOICED\", \"QUANTITY_VARIANCE\", \"REVERSAL_FLAG\", \"STAT_AMOUNT\", \"TASK_NAME\", \"TASK_NUMBER\", \"UNIT_PRICE\", \"UNIT_PRICE_BASE\", \"VAT_CODE\", \"VENDOR_EXCHANGE_DATE\", \"VENDOR_EXCHANGE_RATE\", \"VENDOR_EXCHANGE_RATE_TYPE\", \"VENDOR_NAME\", \"VENDOR_PREPAY_AMOUNT\", \"VENDOR_PREPAY_AMOUNT_BASE\", \"VENDOR_SITE_CODE\", \"Z$$_________________________\", \"Z$AP20_INVOICE_DISTRIBUTIONS\", \"Z$AP20_INVOICES\", \"Z$GL_CHART_OF_ACCOUNTS\") AS\n" +
                  "  SELECT\n" +
                  "/*+ RULE */\n" +
                  "   IDSTR.ACCRUAL_POSTED_FLAG A$Accrual_Posted_Flag,\n" +
                  "   Distr.SEGMENT3 A$Distr$Account,\n" +
                  "   Distr.SEGMENT1 A$Distr$Company,\n" +
                  "   Distr.SEGMENT2 A$Distr$Department,\n" +
                  "   Distr.SEGMENT5 A$Distr$Product,\n" +
                  "   Distr.SEGMENT4 A$Distr$Sub_Account,\n" +
                  "   Expenditure_Organization_Name.NAME A$Expenditure_Organization_Nam,\n" +
                  "   IDSTR.ACCOUNTING_DATE A$Gl_Date,\n" +
                  "   INV.INVOICE_DATE A$Invoice_Date,\n" +
                  "   INV.INVOICE_NUM A$Invoice_Number,\n" +
                  "   IDSTR.POSTED_FLAG A$Posted_Flag,\n" +
                  "   PROJ.NAME A$Project_Name,\n" +
                  "   PROJ.SEGMENT1 A$Project_Number,\n" +
                  "   VEND.VENDOR_NAME A$Vendor_Name,\n" +
                  "   VCODE.VENDOR_SITE_CODE A$Vendor_Site_Code,\n" +
                  "   'A$ZZ__________________________Copyright Noetix Corporation 1992-2009'\n" +
                  "A$ZZ__________________________,\n" +
                  "   APLiab.SEGMENT1 ||'-'||APLiab.SEGMENT2 ||'-'||APLiab.SEGMENT3\n" +
                  "||'-'||APLiab.SEGMENT4 ||'-'||APLiab.SEGMENT5 APLiab$Operations_Accounting_F,\n" +
                  "   IDSTR.ACCRUAL_POSTED_FLAG Accrual_Posted_Flag,\n" +
                  "   NVL(IDSTR.AMOUNT,0) Amount,\n" +
                  "   NVL(IDSTR.BASE_AMOUNT,DECODE(INV.INVOICE_CURRENCY_CODE,\n" +
                  "BOOK.CURRENCY_CODE,NVL(IDSTR.AMOUNT,0),NULL)) Amount_Base,\n" +
                  "   NVL(IDSTR.ASSETS_ADDITION_FLAG,'N') Assets_Addition_Code,\n" +
                  "   IDSTR.ASSETS_TRACKING_FLAG Assets_Tracking_Flag,\n" +
                  "   NVL(IDSTR.BASE_AMOUNT,DECODE(INV.INVOICE_CURRENCY_CODE,\n" +
                  "BOOK.CURRENCY_CODE,NVL(IDSTR.AMOUNT,0),NULL)) Base_Amount,\n" +
                  "   BOOK.CURRENCY_CODE Base_Currency_Code,\n" +
                  "   IDSTR.BASE_INVOICE_PRICE_VARIANCE Base_Invoice_Price_Variance,\n" +
                  "   IDSTR.BASE_QUANTITY_VARIANCE Base_Quantity_Variance,\n" +
                  "   IDSTR.DESCRIPTION Description,\n" +
                  "   Distr.SEGMENT3 Distr$Account,\n" +
                  "   Distr.SEGMENT1 Distr$Company,\n" +
                  "   Distr.SEGMENT2 Distr$Department,\n" +
                  "   Distr.SEGMENT1 ||'-'||Distr.SEGMENT2 ||'-'||Distr.SEGMENT3\n" +
                  "||'-'||Distr.SEGMENT4 ||'-'||Distr.SEGMENT5 Distr$Operations_Accounting_Fl,\n" +
                  "   Distr.SEGMENT5 Distr$Product,\n" +
                  "   Distr.SEGMENT4 Distr$Sub_Account,\n" +
                  "   IDSTR.CREATION_DATE Distribution_Creation_Date,\n" +
                  "   IDSTR.DISTRIBUTION_LINE_NUMBER Distribution_Line_Number,\n" +
                  "   INV.EARLIEST_SETTLEMENT_DATE Earliest_Settlement_Date,\n" +
                  "   IDSTR.EXCHANGE_DATE Exchange_Date,\n" +
                  "   IDSTR.EXCHANGE_RATE Exchange_Rate,\n" +
                  "   IDSTR.EXCHANGE_RATE_TYPE Exchange_Rate_Type,\n" +
                  "   IDSTR.EXCHANGE_RATE_VARIANCE Exchange_Rate_Variance,\n" +
                  "   IDSTR.EXPENDITURE_ITEM_DATE Expenditure_Item_Date,\n" +
                  "   Expenditure_Organization_Name.NAME Expenditure_Organization_Name,\n" +
                  "   IDSTR.EXPENDITURE_TYPE Expenditure_Type,\n" +
                  "   IDSTR.JUSTIFICATION Expense_Justification,\n" +
                  "   NVL(IDSTR.FINAL_MATCH_FLAG,'N') Final_Match_Flag,\n" +
                  "   IDSTR.ACCOUNTING_DATE Gl_Date,\n" +
                  "   SUBSTR (DECODE (IDSTR.MATCH_STATUS_FLAG, 'N', 'Not Tested for Approval',\n" +
                  "'T', 'Failed Approval Testing', 'A', 'Approved', NULL,\n" +
                  "'Not Tested for Approval'\n" +
                  ", 'Undefined Value: ' || IDSTR.MATCH_STATUS_FLAG), 1, 23) Hold_Match_Status,\n" +
                  "   INV.ATTRIBUTE_CATEGORY INV$ATTRIBUTE_CATEGORY,\n" +
                  "   decode(INV.ATTRIBUTE_CATEGORY,\n" +
                  "'One-Time',INV.ATTRIBUTE4,null) INV$Misc_Vendor_Address,\n" +
                  "   decode(\n" +
                  "INV.ATTRIBUTE_CATEGORY,'One-Time',INV.ATTRIBUTE5,null) INV$Misc_Vendor_City,\n" +
                  "   decode(\n" +
                  "INV.ATTRIBUTE_CATEGORY,'One-Time',INV.ATTRIBUTE3,null) INV$Misc_Vendor_Name,\n" +
                  "   decode(\n" +
                  "INV.ATTRIBUTE_CATEGORY,'One-Time',INV.ATTRIBUTE6,null) INV$Misc_Vendor_State,\n" +
                  "   decode(\n" +
                  "INV.ATTRIBUTE_CATEGORY,'One-Time',INV.ATTRIBUTE7,null) INV$Misc_Vendor_Zip,\n" +
                  "   IDSTR.INCOME_TAX_REGION Income_Tax_Region,\n" +
                  "   IDSTR.TYPE_1099 Income_Tax_Type,\n" +
                  "   INV.INVOICE_CURRENCY_CODE Invoice_Currency_Code,\n" +
                  "   INV.INVOICE_DATE Invoice_Date,\n" +
                  "   INV.INVOICE_NUM Invoice_Number,\n" +
                  "   DECODE(INV.PAYMENT_STATUS_FLAG,'N'\n" +
                  ",'No','Y','Yes','P','Partial',INV.PAYMENT_STATUS_FLAG) Invoice_Payment_Status,\n" +
                  "   IDSTR.INVOICE_PRICE_VARIANCE Invoice_Price_Variance,\n" +
                  "   NVL(IDSTR.BASE_INVOICE_PRICE_VARIANCE,DECODE(INV.INVOICE_CURRENCY_CODE,\n" +
                  "BOOK.CURRENCY_CODE,\n" +
                  "IDSTR.INVOICE_PRICE_VARIANCE,NULL)) Invoice_Price_Variance_Base,\n" +
                  "   INV.SOURCE Invoice_Source,\n" +
                  "   INV.INVOICE_TYPE_LOOKUP_CODE Invoice_Type_Lookup_Code,\n" +
                  "   GLBTC.DESCRIPTION Journal_Batch_Description,\n" +
                  "   GLBTC.NAME Journal_Batch_Name,\n" +
                  "   GLBTC.POSTED_DATE Journal_Batch_Posted_Date,\n" +
                  "   GLBTC.STATUS Journal_Batch_Status,\n" +
                  "   IDSTR.LINE_TYPE_LOOKUP_CODE Line_Type_Lookup_Code,\n" +
                  "   NVL(IDSTR.PA_ADDITION_FLAG,'Y') Pa_Addition_Flag,\n" +
                  "   NVL(IDSTR.PA_QUANTITY,0) Pa_Quantity,\n" +
                  "   IDSTR.PERIOD_NAME Period_Name,\n" +
                  "   NVL(IDSTR.POSTED_AMOUNT,0) Posted_Amount,\n" +
                  "   NVL(IDSTR.POSTED_BASE_AMOUNT,DECODE(INV.INVOICE_CURRENCY_CODE,\n" +
                  "BOOK.CURRENCY_CODE,NVL(IDSTR.POSTED_AMOUNT,0),NULL)) Posted_Amount_Base,\n" +
                  "   NVL(IDSTR.POSTED_BASE_AMOUNT,DECODE(INV.INVOICE_CURRENCY_CODE,\n" +
                  "BOOK.CURRENCY_CODE,NVL(IDSTR.POSTED_AMOUNT,0),NULL)) Posted_Base_Amount,\n" +
                  "   IDSTR.POSTED_FLAG Posted_Flag,\n" +
                  "   NVL (IDSTR.PREPAY_AMOUNT_REMAINING, DECODE (INV.INVOICE_TYPE_LOOKUP_CODE,\n" +
                  "'PREPAYMENT',  DECODE(INV.PAYMENT_STATUS_FLAG,'Y',  DECODE(\n" +
                  "IDSTR.LINE_TYPE_LOOKUP_CODE,\n" +
                  "'ITEM', IDSTR.AMOUNT,0), 0), 0)) Prepay_Amount_Remaining,\n" +
                  "   (NVL (IDSTR.PREPAY_AMOUNT_REMAINING, DECODE(INV.INVOICE_TYPE_LOOKUP_CODE,\n" +
                  "'PREPAYMENT',  DECODE(INV.PAYMENT_STATUS_FLAG,'Y',  DECODE(\n" +
                  "IDSTR.LINE_TYPE_LOOKUP_CODE,'ITEM', IDSTR.AMOUNT,0), 0), 0)) * NVL (\n" +
                  "IDSTR.EXCHANGE_RATE, DECODE (INV.INVOICE_CURRENCY_CODE,\n" +
                  "BOOK.CURRENCY_CODE, 1,NULL))) Prepay_Amount_Remaining_Base,\n" +
                  "   Price_Var.SEGMENT1 ||'-'||Price_Var.SEGMENT2 ||'-'||Price_Var.SEGMENT3 ||\n" +
                  "'-'\n" +
                  "||Price_Var.SEGMENT4 ||'-'||Price_Var.SEGMENT5 Price_Var$Operations_Accountin,\n" +
                  "   PROJ.NAME Project_Name,\n" +
                  "   PROJ.SEGMENT1 Project_Number,\n" +
                  "   NVL(IDSTR.QUANTITY_INVOICED,0) Quantity_Invoiced,\n" +
                  "   IDSTR.QUANTITY_VARIANCE Quantity_Variance,\n" +
                  "   NVL(IDSTR.REVERSAL_FLAG,'N') Reversal_Flag,\n" +
                  "   IDSTR.STAT_AMOUNT Stat_Amount,\n" +
                  "   TASK.TASK_NAME Task_Name,\n" +
                  "   TASK.TASK_NUMBER Task_Number,\n" +
                  "   IDSTR.UNIT_PRICE Unit_Price,\n" +
                  "   (IDSTR.UNIT_PRICE)*NVL(IDSTR.EXCHANGE_RATE,\n" +
                  "DECODE(INV.INVOICE_CURRENCY_CODE,BOOK.CURRENCY_CODE,1,NULL)) Unit_Price_Base,\n" +
                  "   APTXC.NAME Vat_Code,\n" +
                  "   INV.EXCHANGE_DATE Vendor_Exchange_Date,\n" +
                  "   INV.EXCHANGE_RATE Vendor_Exchange_Rate,\n" +
                  "   INV.EXCHANGE_RATE_TYPE Vendor_Exchange_Rate_Type,\n" +
                  "   VEND.VENDOR_NAME Vendor_Name,\n" +
                  "   CASE WHEN IDSTR.LINE_TYPE_LOOKUP_CODE ='PREPAY'          OR (\n" +
                  "IDSTR.LINE_TYPE_LOOKUP_CODE = 'TAX'              AND\n" +
                  "IDSTR.PREPAY_TAX_PARENT_ID\n" +
                  "IS NOT NULL)      THEN IDSTR.AMOUNT      ELSE NULL END Vendor_Prepay_Amount,\n" +
                  "   CASE WHEN IDSTR.LINE_TYPE_LOOKUP_CODE ='PREPAY'          OR (\n" +
                  "IDSTR.LINE_TYPE_LOOKUP_CODE = 'TAX'              AND\n" +
                  "IDSTR.PREPAY_TAX_PARENT_ID IS NOT NULL)      THEN NVL(IDSTR.BASE_AMOUNT,\n" +
                  "DECODE(INV.INVOICE_CURRENCY_CODE,BOOK.CURRENCY_CODE,\n" +
                  "NVL(IDSTR.AMOUNT,0),NULL))      ELSE NULL END Vendor_Prepay_Amount_Base,\n" +
                  "   VCODE.VENDOR_SITE_CODE Vendor_Site_Code,\n" +
                  "   'Z$$_________________________' Z$$_________________________,\n" +
                  "   IDSTR.rowid Z$AP20_Invoice_Distributions,\n" +
                  "   INV.rowid Z$AP20_Invoices,\n" +
                  "   Distr.rowid Z$GL_Chart_Of_Accounts\n" +
                  "  FROM\n" +
                  "       HR.HR_ALL_ORGANIZATION_UNITS_TL Expenditure_Organization_Name,\n" +
                  "       GL.GL_CODE_COMBINATIONS Price_Var,\n" +
                  "       GL.GL_CODE_COMBINATIONS Distr,\n" +
                  "       GL.GL_CODE_COMBINATIONS APLiab,\n" +
                  "       PO.PO_VENDORS VEND,\n" +
                  "       PO.PO_VENDOR_SITES_ALL VCODE,\n" +
                  "       PA.PA_TASKS TASK,\n" +
                  "       PA.PA_PROJECTS_ALL PROJ,\n" +
                  "       AP.AP_TAX_CODES_ALL APTXC,\n" +
                  "       GL.GL_SETS_OF_BOOKS BOOK,\n" +
                  "       GL.GL_JE_BATCHES GLBTC,\n" +
                  "       GL.GL_JE_HEADERS GLHDR,\n" +
                  "       GL.GL_JE_LINES GLLIN,\n" +
                  "       GL.GL_IMPORT_REFERENCES GLREF,\n" +
                  "       AP.AP_ACCOUNTING_EVENTS_ALL AE,\n" +
                  "       AP.AP_AE_HEADERS_ALL AEH,\n" +
                  "       AP.AP_AE_LINES_ALL AEL,\n" +
                  "       AP.AP_INVOICES_ALL INV,\n" +
                  "       DEV_586_SMALL_DE.AP20_OU_ACL_Map_Base XMAP,\n" +
                  "       AP.AP_INVOICE_DISTRIBUTIONS_ALL IDSTR\n" +
                  "WHERE 'Copyright Noetix Corporation 1992-2009' is not null\n" +
                  "   AND IDSTR.INVOICE_ID                    = INV.INVOICE_ID\n" +
                  "   AND NVL (IDSTR.ORG_ID, -9999) = XMAP.ORG_ID\n" +
                  "   AND XMAP.APPLICATION_INSTANCE = '45'\n" +
                  "   /*** SET_OF_BOOKS_ID      => 1 ***/\n" +
                  "   /*** CHART_OF_ACCOUNTS_ID => 101 ***/\n" +
                  "   /*** ORG_ID      => 204 ***/\n" +
                  "   AND IDSTR.SET_OF_BOOKS_ID = XMAP.SET_OF_BOOKS_ID\n" +
                  "   AND IDSTR.ACCOUNTING_EVENT_ID = AE.ACCOUNTING_EVENT_ID (+)\n" +
                  "   AND AE.SOURCE_TABLE (+)= 'AP_INVOICES'\n" +
                  "   AND (AE.SOURCE_ID IS NULL OR\n" +
                  "        AE.SOURCE_ID = INV.INVOICE_ID)\n" +
                  "   AND AE.ACCOUNTING_EVENT_ID = AEH.ACCOUNTING_EVENT_ID (+)\n" +
                  "   AND IDSTR.INVOICE_DISTRIBUTION_ID = AEL.SOURCE_ID (+)\n" +
                  "   AND IDSTR.DIST_CODE_COMBINATION_ID = AEL.CODE_COMBINATION_ID (+)\n" +
                  "   AND AEL.SOURCE_TABLE (+) = 'AP_INVOICE_DISTRIBUTIONS'\n" +
                  "   AND ( ( AEL.SOURCE_ID IS NULL) OR\n" +
                  "         ( AEL.AE_LINE_ID = ( SELECT MIN(AEL1.AE_LINE_ID)\n" +
                  "                                FROM AP.AP_AE_LINES_ALL AEL1\n" +
                  "                               WHERE AEL1.AE_HEADER_ID = AEL.AE_HEADER_ID\n" +
                  "                            AND AEL1.SOURCE_TABLE = 'AP_INVOICE_DISTRIBUTIONS'\n" +
                  "                               AND AEL1.SOURCE_ID = AEL.SOURCE_ID\n" +
                  "                    AND AEL1.CODE_COMBINATION_ID = AEL.CODE_COMBINATION_ID)) )\n" +
                  "   AND ((AEL.AE_HEADER_ID IS NULL OR AEH.AE_HEADER_ID IS NULL) OR\n" +
                  "         AEL.AE_HEADER_ID = AEH.AE_HEADER_ID)\n" +
                  "   AND (AEH.AE_HEADER_ID IS NULL OR\n" +
                  "        AEH.SET_OF_BOOKS_ID = IDSTR.SET_OF_BOOKS_ID)\n" +
                  "   AND AEL.GL_SL_LINK_ID = GLREF.GL_SL_LINK_ID (+)\n" +
                  "   AND GLREF.GL_SL_LINK_TABLE (+) = 'APECL'\n" +
                  "   AND GLREF.JE_HEADER_ID  =  GLLIN.JE_HEADER_ID (+)\n" +
                  "   AND GLREF.JE_LINE_NUM   =  GLLIN.JE_LINE_NUM (+)\n" +
                  "   AND GLLIN.JE_HEADER_ID = GLHDR.JE_HEADER_ID (+)\n" +
                  "   AND GLHDR.JE_BATCH_ID  = GLBTC.JE_BATCH_ID (+)\n" +
                  "   AND IDSTR.TAX_CODE_ID = APTXC.TAX_ID  (+)\n" +
                  "   AND BOOK.SET_OF_BOOKS_ID = XMAP.SET_OF_BOOKS_ID\n" +
                  "   AND BOOK.SET_OF_BOOKS_ID +0  = IDSTR.SET_OF_BOOKS_ID\n" +
                  "   AND NVL (AE.ORG_ID(+), IDSTR.ORG_ID) = IDSTR.ORG_ID\n" +
                  "   AND NVL (AEH.ORG_ID, XMAP.ORG_ID) =XMAP.ORG_ID\n" +
                  "   AND NVL (AEL.ORG_ID, XMAP.ORG_ID) =XMAP.ORG_ID\n" +
                  "   AND NVL (APTXC.ORG_ID, XMAP.ORG_ID) =XMAP.ORG_ID\n" +
                  "   AND NVL (INV.ORG_ID, -9999) =XMAP.ORG_ID\n" +
                  "   AND PROJ.PROJECT_ID(+) = IDSTR.PROJECT_ID\n" +
                  "   AND TASK.TASK_ID(+) = IDSTR.TASK_ID\n" +
                  "   AND INV.VENDOR_SITE_ID = VCODE.VENDOR_SITE_ID(+)\n" +
                  "   AND NVL (VCODE.ORG_ID, XMAP.ORG_ID ) = XMAP.ORG_ID\n" +
                  "   AND VEND.VENDOR_ID +0 = INV.VENDOR_ID\n" +
                  "   AND IDSTR.ACCTS_PAY_CODE_COMBINATION_ID = APLiab.CODE_COMBINATION_ID(+)\n" +
                  "   AND IDSTR.DIST_CODE_COMBINATION_ID = Distr.CODE_COMBINATION_ID\n" +
                  "   AND IDSTR.PRICE_VAR_CODE_COMBINATION_ID = Price_Var.CODE_COMBINATION_ID(+)\n" +
                  "   AND IDSTR.EXPENDITURE_ORGANIZATION_ID\n" +
                  "= Expenditure_Organization_Name.ORGANIZATION_ID(+)\n" +
                  "   AND Expenditure_Organization_Name.LANGUAGE (+) = NOETIX_ENV_PKG.GET_LANGUAGE";

          assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TViewAliasClause va = createView.getViewAliasClause();
        TObjectName o =    va.getViewAliasItemList().getViewAliasItem(0).getAlias();
        assertTrue(o.toString().equalsIgnoreCase("\"A$ACCRUAL_POSTED_FLAG\""));
      }

}
