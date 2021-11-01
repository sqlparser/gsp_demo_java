package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;

import org.xml.sax.SAXException;

import org.jdom.Element;

/**
 * @deprecated
 * */
public class testXmlXSD extends TestCase {

    boolean  validXmlfile(String pxmlfile, String pxsdfile){
        boolean  v = false;

// build an XSD-aware SchemaFactory
        SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );

// hook up org.xml.sax.ErrorHandler implementation.
        org.xml.sax.ErrorHandler myErrorHandler = null ;
        schemaFactory.setErrorHandler( myErrorHandler );

// get the custom xsd schema describing the required format for my XML files.
        Schema schemaXSD = null;
        try {
            schemaXSD = schemaFactory.newSchema( new File( pxsdfile ) );
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

// Create a Validator capable of validating XML files according to my custom schema.
        Validator validator = schemaXSD.newValidator();

// Get a parser capable of parsing vanilla XML into a DOM tree
        DocumentBuilder parser = null;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

// parse the XML purely as XML and get a DOM tree represenation.
        org.w3c.dom.Document document = null;
        try {
            document = parser.parse( new File( pxmlfile ) );
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

// parse the XML DOM tree againts the stricter XSD schema

        try {
            validator.validate( new DOMSource( document ) );
            v = true;
        } catch (IOException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return  v;

    }

    //static String xsdfile = "c:\\prg\\gsqlparser\\xml\\define\\gsp.xsd";
   // static String xmlfile =  "c:\\prg\\gsqlparser\\xml\\define\\test1.xml";
   // static  String create_table_sqlfile = "c:\\prg\\gsqlparser\\xml\\define\\createtable.sql";
    static  String alter_table_sqlfile = gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"mssql\\xml\\define\\altertable.sql";

    public static String tag_sql_script = "sql_script";
    public static String tag_create_table_statement = "create_table_statement";
    public static String tag_table_name = "table_name";
    public static String tag_column_definition_list = "column_definition_list";
    public static String tag_table_constraint_list = "table_constraint_list";
    public static String tag_column_definition = "column_definition";
    public static String tag_name = "name";
    public static String tag_type = "type";
    public static String tag_object_name = "object_name";
    public static String tag_column_name = "column_name";
    public static String tag_column_name_list = "column_name_list";
    public static String tag_datatype = "datatype";
    public static String tag_default_value = "default_value";
    public static String tag_expression = "expression";
    public static String tag_check_condition = "check_condition";
    public static String tag_parsed_expression = "parsed_expression";
    public static String tag_column_constraint_list = "column_constraint_list";
    public static String tag_constraint = "constraint";
    public static String tag_referenced_object_name = "referenced_object_name";
    public static String tag_referenced_column_name_list = "referenced_column_name_list";

    public static String attribute_isnull = "isnull";
    public static String attribute_dbvendor = "dbvendor";

    public static String tag_cdata_begin = "<![CDATA[";
    public static String tag_cdata_end = "]]>";


    public static String tag_alter_table_statement = "alter_table_statement";
    public static String tag_alter_table_option_list = "alter_table_option_list";
    public static String tag_alter_table_option = "alter_table_option";
    public static String tag_option_type = "option_type";

    public static String constraint_type_not_null = "not_null";
    public static String constraint_type_unique = "unique";
    public static String constraint_type_check = "check";
    public static String constraint_type_primary_key = "primary_key";
    public static String constraint_type_foreign_key = "foreign_key";
    public static String constraint_type_reference_clause = "reference_clause";


   Element expressionToXml(TExpression pe,boolean isParsed){
       Element e_expression = new Element(tag_expression);
       e_expression.setText(tag_cdata_begin + pe.toString() + tag_cdata_end);
       return e_expression;
   }

   Element constraintToXml(TConstraint pConstraint){

       Element e_constraint = new Element(tag_constraint);

       if (pConstraint.getConstraintName() != null){
           Element e_name = new Element(tag_name);
           e_name.setText(pConstraint.getConstraintName().toString());
           e_constraint.addContent(e_name);
       }

       Element e_type = new Element(tag_type);
       e_constraint.addContent(e_type);


       switch (pConstraint.getConstraint_type()){
           case primary_key:
               e_type.setText(constraint_type_primary_key);
               Element e_column_name_list = new Element(tag_column_name_list);
               e_constraint.addContent(e_column_name_list);
               for(int i=0;i<pConstraint.getColumnList().size();i++){
                   Element e_column_name = new Element(tag_column_name);
                   e_column_name.setText(pConstraint.getColumnList().getElement(i).getColumnName().toString());
                   e_column_name_list.addContent(e_column_name);
               }
               break;
           case notnull:
               e_type.setText(constraint_type_not_null);
               break;
           case unique:
               e_type.setText(constraint_type_unique);
               if (pConstraint.getColumnList() !=null){
                   e_column_name_list = new Element(tag_column_name_list);
                   e_constraint.addContent(e_column_name_list);
                   for(int i=0;i<pConstraint.getColumnList().size();i++){
                       Element e_column_name = new Element(tag_column_name);
                       e_column_name.setText(pConstraint.getColumnList().getElement(i).getColumnName().toString());
                       e_column_name_list.addContent(e_column_name);
                   }
               }
               break;
           case check:
               e_type.setText(constraint_type_check);
               Element e_check_condition = new Element(tag_check_condition);
               e_check_condition.addContent(expressionToXml(pConstraint.getCheckCondition(),false));
               e_constraint.addContent(e_check_condition);
               break;
           case foreign_key:
               e_type.setText(constraint_type_foreign_key);
               e_column_name_list = new Element(tag_column_name_list);
               e_constraint.addContent(e_column_name_list);
               for(int i=0;i<pConstraint.getColumnList().size();i++){
                   Element e_column_name = new Element(tag_column_name);
                   e_column_name.setText(pConstraint.getColumnList().getElement(i).getColumnName().toString());
                   e_column_name_list.addContent(e_column_name);
               }
               if (pConstraint.getReferencedObject() != null){
                   Element e_reference_object = new Element(tag_referenced_object_name);
                   e_constraint.addContent(e_reference_object);
                   e_reference_object.setText(pConstraint.getReferencedObject().toString());
                   e_column_name_list = new Element(tag_referenced_column_name_list);
                   e_constraint.addContent(e_column_name_list);
                   for(int i=0;i<pConstraint.getReferencedColumnList().size();i++){
                       Element e_column_name = new Element(tag_column_name);
                       e_column_name.setText(pConstraint.getReferencedColumnList().getObjectName(i).toString());
                       e_column_name_list.addContent(e_column_name);
                   }
               }
               break;
           case reference:
               e_type.setText(constraint_type_reference_clause);
               if (pConstraint.getReferencedObject() != null){
                   Element e_reference_object = new Element(tag_referenced_object_name);
                   e_constraint.addContent(e_reference_object);
                   e_reference_object.setText(pConstraint.getReferencedObject().toString());
                   e_column_name_list = new Element(tag_referenced_column_name_list);
                   e_constraint.addContent(e_column_name_list);
                   for(int i=0;i<pConstraint.getReferencedColumnList().size();i++){
                       Element e_column_name = new Element(tag_column_name);
                       e_column_name.setText(pConstraint.getReferencedColumnList().getObjectName(i).toString());
                       e_column_name_list.addContent(e_column_name);
                   }
               }
               break;
           default:
               e_type.setText("not_found");
               break;
       }


       return e_constraint;
   }


    public void testXml() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqlfilename = alter_table_sqlfile;
        int ret = sqlparser.parse();
        assertTrue(ret == 0);


//        TAlterTableStatement alterTable = (TAlterTableStatement)sqlparser.sqlstatements.get(0);
//        Element e_alter_table_statement = alterTableStatementXml(alterTable);
//
//        Element root = new Element(tag_sql_script);
//        Document Doc = new Document(root);
//        root.setAttribute(attribute_dbvendor,sqlparser.getDbVendor().toString());
//        root.addContent(e_alter_table_statement);
//
//        XMLOutputter XMLOut = new XMLOutputter();
//        XMLOut.setFormat(Format.getPrettyFormat());
//
//        try {
//            XMLOut.output(Doc, new FileOutputStream(xmlfile));
//           } catch (Exception e) {
//            e.printStackTrace();
//           }
//
//         assertTrue(validXmlfile(xmlfile,xsdfile));

    }

    public Element alterTableStatementXml(TAlterTableStatement pAlterTable)  {
        Element e_alter_table_statement = new Element(tag_alter_table_statement);
        Element table_name = new Element(tag_table_name);
        table_name.setText(pAlterTable.getTableName().toString());
        e_alter_table_statement.addContent(table_name);

        Element e_alter_table_option_list = new Element(tag_alter_table_option_list);
        e_alter_table_statement.addContent(e_alter_table_option_list);
        for(int i=0;i<pAlterTable.getAlterTableOptionList().size();i++){
            TAlterTableOption al = pAlterTable.getAlterTableOptionList().getAlterTableOption(i);
            Element e_alter_table_option = new Element(tag_alter_table_option);
            e_alter_table_option_list.addContent(e_alter_table_option);
            Element e_option_type = new Element(tag_type);
            e_option_type.setText( al.getOptionType().toString());
            e_alter_table_option.addContent(e_option_type);
            switch (al.getOptionType()){
                case AddColumn:
                    break;
                case AddConstraint:
                    Element e_table_constraint_list = new Element(tag_table_constraint_list);
                    e_alter_table_option.addContent(e_table_constraint_list);
                    for(int j=0; i<al.getConstraintList().size();i++){
                        TConstraint tableConstraint = al.getConstraintList().getConstraint(j);
                        e_table_constraint_list.addContent(constraintToXml(tableConstraint));
                    }
                    break;
                case AddConstraintFK:
                    break;
                case AddConstraintIndex:
                    break;
                case AddConstraintPK:
                    break;
                case AddConstraintUnique:
                    break;
                case AlterColumn:
                    break;
                case AlterConstraintCheck:
                    break;
                case AlterConstraintFK:
                    break;
                case ChangeColumn:
                    break;
                case CheckConstraint:
                    break;
                case DropColumn:
                    break;
                case DropColumnsContinue:
                    break;
                case DropConstraint:
                    break;
                case DropConstraintCheck:
                    break;
                case DropConstraintFK:
                    break;
                case DropConstraintRestrict:
                    break;
                case DropConstraintUnique:
                    break;
                case DropUnUsedColumn:
                    break;
                case DropConstraintPartitioningKey:
                    break;
                case ModifyColumn:
                    break;
                case ModifyConstraint:
                    break;
                case RenameColumn:
                    break;
                case RenameConstraint:
                    break;
                case RenameTable:
                    break;
                case SetUnUsedColumn:
                    break;
                case Unknown:
                    break;
                default:;

            }
        }


        return  e_alter_table_statement;
    }

    public Element createTableStatementXml(TCreateTableSqlStatement pCreateTable)  {

        TCreateTableSqlStatement createTable = pCreateTable;

        Element e_create_table_statement = new Element(tag_create_table_statement);

        Element table_name = new Element(tag_table_name);
        table_name.setText(createTable.getTableName().toString());
        e_create_table_statement.addContent(table_name);

        Element column_definition_list = new Element(tag_column_definition_list);
        e_create_table_statement.addContent(column_definition_list);

        for(int i=0;i<createTable.getColumnList().size();i++){
            TColumnDefinition column = createTable.getColumnList().getColumn(i);

            Element e_column_definition =  new Element(tag_column_definition);
            e_column_definition.setAttribute(attribute_isnull,column.isNull()?"true":"false");
            column_definition_list.addContent(e_column_definition);
            Element name = new Element(tag_name);
            name.setText(column.getColumnName().toString());
            e_column_definition.addContent(name);
            Element datatype = new Element(tag_datatype);
            datatype.setText(column.getDatatype().toString());
            e_column_definition.addContent(datatype);
            if (column.getDefaultExpression() != null){
                Element e_default_value = new Element(tag_default_value);
                e_column_definition.addContent(e_default_value);
                e_default_value.addContent(expressionToXml(column.getDefaultExpression(),false));
            }

            if (column.getConstraints() != null){
                Element e_column_constraint_list = new Element(tag_column_constraint_list);
                e_column_definition.addContent(e_column_constraint_list);
                for(int j=0;j<column.getConstraints().size();j++){
                    TConstraint columnConstraint = column.getConstraints().getConstraint(j);
                    e_column_constraint_list.addContent(constraintToXml(columnConstraint));
                }
            }
        }

        if (createTable.getTableConstraints().size() > 0){
            Element e_table_constraint_list = new Element(tag_table_constraint_list);
            e_create_table_statement.addContent(e_table_constraint_list);
            for(int i=0; i<createTable.getTableConstraints().size();i++){
                TConstraint tableConstraint = createTable.getTableConstraints().getConstraint(i);
                e_table_constraint_list.addContent(constraintToXml(tableConstraint));
            }
        }

        return e_create_table_statement;
    }

}
