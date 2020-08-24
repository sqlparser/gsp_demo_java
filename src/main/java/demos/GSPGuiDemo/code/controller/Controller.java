package com.gudusoft.format.controller;

import com.gudusoft.format.constant.DbConstant;
import com.gudusoft.format.formatsql.GFmtOptFactory;
import com.gudusoft.format.util.BouncedUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.styleenums.*;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<Object> type1122;

    @FXML
    private ComboBox<Object> type1121;

    @FXML
    private ToggleGroup SampleModeGroup1253;

    @FXML
    private ToggleGroup SampleModeGroup7;

    @FXML
    private ToggleGroup SampleModeGroup6;

    @FXML
    private ToggleGroup SampleModeGroup3;

    @FXML
    private ComboBox<Object> type;

    @FXML
    private ToggleGroup SampleModeGroup2;

    @FXML
    private ToggleGroup SampleModeGroup5;

    @FXML
    private ToggleGroup SampleModeGroup11121;

    @FXML
    private RadioButton rd332;

    @FXML
    private ComboBox<Object> type11;

    @FXML
    private RadioButton rd333;

    @FXML
    private ToggleGroup SampleModeGroup51;

    @FXML
    private RadioButton rd36;

    @FXML
    private RadioButton rd35;

    @FXML
    private RadioButton rd38;

    @FXML
    private ComboBox<Object> selectKeywordsAlignOptionType;

    @FXML
    private ToggleGroup SampleModeGroup65;

    @FXML
    private RadioButton rd37;

    @FXML
    private RadioButton rd32;

    @FXML
    private RadioButton rd31;

    @FXML
    private RadioButton rd34;

    @FXML
    private ToggleGroup SampleModeGroup1241;

    @FXML
    private RadioButton rd33;

    @FXML
    private ToggleGroup SampleModeGroup1121;

    @FXML
    private ComboBox<Object> type1131;

    @FXML
    private RadioButton rd3181;

    @FXML
    private RadioButton rd291;

    @FXML
    private ToggleGroup SampleModeGroup12341;

    @FXML
    private TextField indentCaseThenText;

    @FXML
    private ToggleGroup SampleModeGroup;

    @FXML
    private ComboBox<Object> caseQuotedIdentifierType;

    @FXML
    private RadioButton rd321;

    @FXML
    private RadioButton rd322;

    @FXML
    private RadioButton rd323;

    @FXML
    private RadioButton rd324;

    @FXML
    private TextField beStyleIfElseSingleStmtIndentSizeText11;

    @FXML
    private RadioButton rd30;

    @FXML
    private ComboBox<Object> caseIdentifierType;

    @FXML
    private ToggleGroup SampleModeGroup1234;

    @FXML
    private ToggleGroup SampleModeGroup1232;

    @FXML
    private RadioButton rd65;

    @FXML
    private ComboBox<Object> type2112;

    @FXML
    private ToggleGroup SampleModeGroup1233;

    @FXML
    private ComboBox<Object> type2111;

    @FXML
    private ToggleGroup SampleModeGroup1231;

    @FXML
    private RadioButton rd66;

    @FXML
    private Tab exportMenu;

    @FXML
    private TextField beStyleIfElseSingleStmtIndentSizeText;

    @FXML
    private ToggleGroup SampleModeGroup132;

    @FXML
    private RadioButton rd314;

    @FXML
    private TextField tabSize;

    @FXML
    private RadioButton rd311;

    @FXML
    private ToggleGroup SampleModeGroup123;

    @FXML
    private RadioButton rd312;

    @FXML
    private RadioButton rd313;

    @FXML
    private TextField beStyleBlockIndentSizeText;

    @FXML
    private ComboBox<Object> type11121;

    @FXML
    private ToggleGroup SampleModeGroup141;

    @FXML
    private Label typeTitle;

    @FXML
    private RadioButton rd271;

    @FXML
    private RadioButton rd3241;

    @FXML
    private AnchorPane AnchorPane1;

    @FXML
    private RadioButton rd301;

    @FXML
    private TextField beStyleIfElseSingleStmtIndentSizeText1;

    @FXML
    private RadioButton rd423;

    @FXML
    private ComboBox<Object> type211;

    @FXML
    private TextField beStyleBlockRightBEIndentSizeText;

    @FXML
    private ToggleGroup SampleModeGroup12;

    @FXML
    private ToggleGroup SampleModeGroup11;

    @FXML
    private ToggleGroup SampleModeGroup10;

    @FXML
    private ToggleGroup SampleModeGroup14;

    @FXML
    private TextField indentCaseFromSwitchText;

    @FXML
    private RadioButton rd381;

    @FXML
    private RadioButton rd261;

    @FXML
    private RadioButton rd413;

    @FXML
    private TabPane tab;

    @FXML
    private ComboBox<Object> caseKeywordsType;

    @FXML
    private ComboBox<Object> type1212;

    @FXML
    private Button copyButton;

    @FXML
    private ToggleGroup SampleModeGroup121;

    @FXML
    private RadioButton rd132;

    @FXML
    private RadioButton rd133;

    @FXML
    private RadioButton rd3141;

    @FXML
    private ToggleGroup SampleModeGroup12541;

    @FXML
    private RadioButton rd371;

    @FXML
    private ComboBox<Object> type111;

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private ToggleGroup SampleModeGroup112;

    @FXML
    private Button formatButton;

    @FXML
    private ComboBox<Object> type112;

    @FXML
    private Tab formatMenu;

    @FXML
    private ComboBox<Object> type113;

    @FXML
    private RadioButton rd25;

    @FXML
    private RadioButton rd24;

    @FXML
    private RadioButton rd27;

    @FXML
    private RadioButton rd26;

    @FXML
    private ToggleGroup SampleModeGroup1433;

    @FXML
    private RadioButton rd20;

    @FXML
    private RadioButton rd23;

    @FXML
    private AnchorPane parametric;

    @FXML
    private RadioButton rd22;

    @FXML
    private TextArea sqlData;

    @FXML
    private RadioButton rd121;

    @FXML
    private ComboBox<Object> caseFuncnameType;

    @FXML
    private RadioButton rd3171;

    @FXML
    private RadioButton rd29;

    @FXML
    private TextField beStyleBlockLeftBEIndentSizeText;

    @FXML
    private TextField beStyleBlockLeftBEIndentSizeText5;

    @FXML
    private TextArea formatResult;

    @FXML
    private AnchorPane root;

    @FXML
    private RadioButton rd14;

    @FXML
    private RadioButton rd13;

    @FXML
    private RadioButton rd16;

    @FXML
    private RadioButton rd15;

    @FXML
    private ComboBox<Object> type1112;

    @FXML
    private ComboBox<Object> type1111;

    @FXML
    private RadioButton rd12;

    @FXML
    private RadioButton rd11;

    @FXML
    private RadioButton rd351;

    @FXML
    private RadioButton rd352;

    @FXML
    private RadioButton rd111;

    @FXML
    private RadioButton rd18;

    @FXML
    private RadioButton rd17;

    @FXML
    private RadioButton rd19;

    @FXML
    private ComboBox<Object> type133;

    @FXML
    private ToggleGroup SampleModeGroup1;

    @FXML
    private RadioButton rd345;

    @FXML
    private RadioButton rd346;

    @FXML
    private TextField tabSize1;

    @FXML
    private TextField tabSize5;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copyButton.setDisable(true);
        scrollpane.setPannable(true);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        //初始化databases下拉框数据
        initSelects(type, DbConstant.dbs);

        //初始化General
        rd11.setSelected(true);
        initSelects(type11, Arrays.asList("AsStacked", "AsWrapped"));

        //初始化Select list
        initSelects(type211, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type111, Arrays.asList("LfAfterComma", "LfbeforeCommaWithSpace", "LfBeforeComma"));
        rd14.setSelected(true);
        rd15.setSelected(true);
        rd18.setSelected(true);

        //初始化Select from clause / Join clause
        initSelects(type2111, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type1111, Arrays.asList("LfAfterComma", "LfbeforeCommaWithSpace", "LfBeforeComma"));
        rd20.setSelected(true);
        rd22.setSelected(true);
        rd25.setSelected(true);

        //初始化And/Or keyword under where
        rd27.setSelected(true);

        //初始化Insert list
        initSelects(type2112, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type1112, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type11121, Arrays.asList("LfAfterComma", "LfbeforeCommaWithSpace", "LfBeforeComma"));

        //初始化Create table
        initSelects(type133, Arrays.asList("AloRight", " AloLeft"));

        //初始化Create table的RadioButton属性
        rd30.setSelected(true);
        rd32.setSelected(true);

        //初始化Indent的RadioButton属性
        rd34.setSelected(true);
        rd36.setSelected(true);

        //初始化Keyword align in select/delete/insert/update
        initSelects(selectKeywordsAlignOptionType, Arrays.asList("AloLeft", "AloRight"));

        //初始化Case options for various token
        initSelects(caseKeywordsType, Arrays.asList("CoLowercase", "CoUppercase", "CoNoChange", "CoInitCap"));
        initSelects(caseIdentifierType, Arrays.asList("CoLowercase", "CoUppercase", "CoNoChange", "CoInitCap"));
        initSelects(caseQuotedIdentifierType, Arrays.asList("CoLowercase", "CoUppercase", "CoNoChange", "CoInitCap"));
        initSelects(caseFuncnameType, Arrays.asList("CoLowercase", "CoUppercase", "CoNoChange", "CoInitCap"));

        //初始化Padding
        rd291.setSelected(true);
        rd352.setSelected(true);
        rd321.setSelected(true);
        rd322.setSelected(true);
        rd323.setSelected(true);
        rd324.setSelected(true);
        rd3241.setSelected(true);

        //初始化Common Table Expression
        rd3181.setSelected(true);

        //初始化Declare statement
        rd381.setSelected(true);

        //初始化Parameters in create procedure/function
        initSelects(type113, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type1131, Arrays.asList("LfAfterComma", "LfbeforeCommaWithSpace", "LfBeforeComma"));
        rd66.setSelected(true);
        rd313.setSelected(true);
        rd3241.setSelected(true);

        //初始化Execute statement
        rd271.setSelected(true);

        //初始化Blank lines
        initSelects(type1212, Arrays.asList("EloMergeIntoOne", "EloRemove", "EloPreserve"));
        rd121.setSelected(true);

        //初始化Line number
        rd333.setSelected(true);
        rd346.setSelected(true);

        //初始化Parameters in function Call
        initSelects(type1122, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type112, Arrays.asList("LfAfterComma", "LfbeforeCommaWithSpace", "LfBeforeComma"));

        //初始化Used for compact mode
        initSelects(type1121, Arrays.asList("CpmNone", "Cpmugly"));
    }

    //初始化下拉框数据
    private void initSelects(ComboBox<Object> comboBoxs, List items) {
        ObservableList options =
                FXCollections.observableArrayList(
                        items
                );
        comboBoxs.setItems(options);
        comboBoxs.getSelectionModel().selectFirst();
    }


    @FXML
    void format(ActionEvent event) {
        try {
            String database = "dbv" + type.getValue();
            EDbVendor dbVendor = EDbVendor.valueOf(database);
            TGSqlParser sqlparser = new TGSqlParser(dbVendor);
            sqlparser.sqltext = sqlData.getText();
            int ret = sqlparser.parse();
            if (ret == 0) {
                GFmtOpt option = GFmtOptFactory.newInstance();
                setGFmtOpts(option);
                String result = FormatterFactory.pp(sqlparser, option);
                formatResult.setText(result);
                if ("".equals(formatResult.getText())) {
                    copyButton.setDisable(true);
                } else {
                    copyButton.setDisable(false);
                }
            } else {
                System.err.println("sql parse error is " + sqlparser.getErrormessage());
                BouncedUtil.failBounced("format");
            }
        } catch (Exception e) {
            e.printStackTrace();
            BouncedUtil.failBounced("format");
        }
    }


    //设置参数值
    private void setGFmtOpts(GFmtOpt gFmtOpts) {
        //General
        if (rd11.isSelected()) {
            gFmtOpts.opearateSourceToken = true;
        } else if (rd12.isSelected()) {
            gFmtOpts.opearateSourceToken = false;
        }


        //Select list
        String defaultAligntype = (String) type11.getValue();
        if ("AsStacked".equals(defaultAligntype)) {
            gFmtOpts.defaultAligntype = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(defaultAligntype)) {
            gFmtOpts.defaultAligntype = TAlignStyle.AsWrapped;
        }
        String selectColumnlistStyle = (String) type211.getValue();
        if ("AsStacked".equals(selectColumnlistStyle)) {
            gFmtOpts.selectColumnlistStyle = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(selectColumnlistStyle)) {
            gFmtOpts.selectColumnlistStyle = TAlignStyle.AsWrapped;
        }
        String selectColumnlistComma = (String) type111.getValue();
        if ("LfAfterComma".equals(selectColumnlistComma)) {
            gFmtOpts.selectColumnlistComma = TLinefeedsCommaOption.LfAfterComma;
        } else if ("LfbeforeCommaWithSpace".equals(selectColumnlistComma)) {
            gFmtOpts.selectColumnlistComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        } else if ("LfBeforeComma".equals(selectColumnlistComma)) {
            gFmtOpts.selectColumnlistComma = TLinefeedsCommaOption.LfBeforeComma;
        }
        if (rd13.isSelected()) {
            gFmtOpts.selectItemInNewLine = true;
        } else if (rd14.isSelected()) {
            gFmtOpts.selectItemInNewLine = false;
        }
        if (rd15.isSelected()) {
            gFmtOpts.alignAliasInSelectList = true;
        } else if (rd16.isSelected()) {
            gFmtOpts.alignAliasInSelectList = false;
        }
        if (rd17.isSelected()) {
            gFmtOpts.treatDistinctAsVirtualColumn = true;
        } else if (rd18.isSelected()) {
            gFmtOpts.treatDistinctAsVirtualColumn = false;
        }


        //Select from clause / Join clause
        String selectFromclauseStyle = (String) type2111.getValue();
        if ("AsStacked".equals(selectFromclauseStyle)) {
            gFmtOpts.selectFromclauseStyle = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(selectFromclauseStyle)) {
            gFmtOpts.selectFromclauseStyle = TAlignStyle.AsWrapped;
        }
        String selectFromclauseComma = (String) type1111.getValue();
        if ("LfAfterComma".equals(selectFromclauseComma)) {
            gFmtOpts.selectFromclauseComma = TLinefeedsCommaOption.LfAfterComma;
        } else if ("LfbeforeCommaWithSpace".equals(selectFromclauseComma)) {
            gFmtOpts.selectFromclauseComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        } else if ("LfBeforeComma".equals(selectFromclauseComma)) {
            gFmtOpts.selectFromclauseComma = TLinefeedsCommaOption.LfBeforeComma;
        }
        if (rd19.isSelected()) {
            gFmtOpts.fromClauseInNewLine = true;
        } else if (rd20.isSelected()) {
            gFmtOpts.fromClauseInNewLine = false;
        }
        if (rd22.isSelected()) {
            gFmtOpts.selectFromclauseJoinOnInNewline = true;
        } else if (rd23.isSelected()) {
            gFmtOpts.selectFromclauseJoinOnInNewline = false;
        }
        if (rd24.isSelected()) {
            gFmtOpts.alignJoinWithFromKeyword = true;
        } else if (rd25.isSelected()) {
            gFmtOpts.alignJoinWithFromKeyword = false;
        }

        //And/Or keyword under where
        if (rd26.isSelected()) {
            gFmtOpts.andOrUnderWhere = true;
        } else if (rd27.isSelected()) {
            gFmtOpts.andOrUnderWhere = false;
        }

        //Insert list
        String insertColumnlistStyle = (String) type2112.getValue();
        if ("AsStacked".equals(insertColumnlistStyle)) {
            gFmtOpts.insertColumnlistStyle = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(insertColumnlistStyle)) {
            gFmtOpts.insertColumnlistStyle = TAlignStyle.AsWrapped;
        }
        String insertValuelistStyle = (String) type1112.getValue();
        if ("AsStacked".equals(insertValuelistStyle)) {
            gFmtOpts.insertValuelistStyle = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(insertValuelistStyle)) {
            gFmtOpts.insertValuelistStyle = TAlignStyle.AsWrapped;
        }
        String defaultCommaOption = (String) type11121.getValue();
        if ("LfAfterComma".equals(defaultCommaOption)) {
            gFmtOpts.defaultCommaOption = TLinefeedsCommaOption.LfAfterComma;
        } else if ("LfbeforeCommaWithSpace".equals(defaultCommaOption)) {
            gFmtOpts.defaultCommaOption = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        } else if ("LfBeforeComma".equals(defaultCommaOption)) {
            gFmtOpts.defaultCommaOption = TLinefeedsCommaOption.LfBeforeComma;
        }

        //Create table
        if (rd29.isSelected()) {
            gFmtOpts.beStyleCreatetableLeftBEOnNewline = true;
        } else if (rd30.isSelected()) {
            gFmtOpts.beStyleCreatetableLeftBEOnNewline = false;
        }
        if (rd31.isSelected()) {
            gFmtOpts.createtableListitemInNewLine = true;
        } else if (rd32.isSelected()) {
            gFmtOpts.createtableListitemInNewLine = false;
        }
        String createtableFieldlistAlignOption = (String) type133.getValue();
        if ("AloLeft".equals(createtableFieldlistAlignOption)) {
            gFmtOpts.createtableFieldlistAlignOption = TAlignOption.AloLeft;
        } else if ("AloRight".equals(createtableFieldlistAlignOption)) {
            gFmtOpts.createtableFieldlistAlignOption = TAlignOption.AloRight;
        }

        //Indent
        if (rd33.isSelected()) {
            gFmtOpts.useTab = true;
        } else if (rd34.isSelected()) {
            gFmtOpts.useTab = false;
        }
        if (rd35.isSelected()) {
            gFmtOpts.beStyleBlockLeftBEOnNewline = true;
        } else if (rd36.isSelected()) {
            gFmtOpts.beStyleBlockLeftBEOnNewline = false;
        }
        gFmtOpts.tabSize = Integer.valueOf(tabSize.getText());
        gFmtOpts.beStyleIfElseSingleStmtIndentSize = Integer.valueOf(beStyleIfElseSingleStmtIndentSizeText.getText());
        gFmtOpts.beStyleBlockLeftBEIndentSize = Integer.valueOf(beStyleBlockLeftBEIndentSizeText.getText());
        gFmtOpts.beStyleBlockIndentSize = Integer.valueOf(beStyleBlockIndentSizeText.getText());
        gFmtOpts.beStyleBlockRightBEIndentSize = Integer.valueOf(beStyleBlockRightBEIndentSizeText.getText());

        //When Then clause
        if (rd35.isSelected()) {
            gFmtOpts.caseWhenThenInSameLine = true;
        } else if (rd36.isSelected()) {
            gFmtOpts.caseWhenThenInSameLine = false;
        }
        gFmtOpts.indentCaseFromSwitch = Integer.valueOf(indentCaseFromSwitchText.getText());
        gFmtOpts.indentCaseThen = Integer.valueOf(indentCaseThenText.getText());

        //Keyword align in select/delete/insert/update
        String selectKeyword = (String) selectKeywordsAlignOptionType.getValue();
        if ("AloLeft".equals(selectKeyword)) {
            gFmtOpts.selectKeywordsAlignOption = TAlignOption.AloLeft;
        } else if ("AloRight".equals(selectKeyword)) {
            gFmtOpts.selectKeywordsAlignOption = TAlignOption.AloRight;
        }

        //Case options for various token
        String caseKeywords = (String) caseKeywordsType.getValue();
        if ("CoUppercase".equals(caseKeywords)) {
            gFmtOpts.caseKeywords = TCaseOption.CoUppercase;
        } else if ("CoLowercase".equals(caseKeywords)) {
            gFmtOpts.caseKeywords = TCaseOption.CoLowercase;
        } else if ("CoNoChange".equals(caseKeywords)) {
            gFmtOpts.caseKeywords = TCaseOption.CoNoChange;
        } else if ("CoInitCap".equals(caseKeywords)) {
            gFmtOpts.caseKeywords = TCaseOption.CoInitCap;
        }


        String caseIdentifier = (String) caseIdentifierType.getValue();
        if ("CoUppercase".equals(caseIdentifier)) {
            gFmtOpts.caseIdentifier = TCaseOption.CoUppercase;
        } else if ("CoLowercase".equals(caseIdentifier)) {
            gFmtOpts.caseIdentifier = TCaseOption.CoLowercase;
        } else if ("CoNoChange".equals(caseIdentifier)) {
            gFmtOpts.caseIdentifier = TCaseOption.CoNoChange;
        } else if ("CoInitCap".equals(caseIdentifier)) {
            gFmtOpts.caseIdentifier = TCaseOption.CoInitCap;
        }


        String caseQuotedIdentifier = (String) caseQuotedIdentifierType.getValue();
        if ("CoUppercase".equals(caseQuotedIdentifier)) {
            gFmtOpts.caseQuotedIdentifier = TCaseOption.CoUppercase;
        } else if ("CoLowercase".equals(caseQuotedIdentifier)) {
            gFmtOpts.caseQuotedIdentifier = TCaseOption.CoLowercase;
        } else if ("CoNoChange".equals(caseQuotedIdentifier)) {
            gFmtOpts.caseQuotedIdentifier = TCaseOption.CoNoChange;
        } else if ("CoInitCap".equals(caseQuotedIdentifier)) {
            gFmtOpts.caseQuotedIdentifier = TCaseOption.CoInitCap;
        }


        String caseFuncname = (String) caseFuncnameType.getValue();
        if ("CoUppercase".equals(caseFuncname)) {
            gFmtOpts.caseFuncname = TCaseOption.CoUppercase;
        } else if ("CoLowercase".equals(caseFuncname)) {
            gFmtOpts.caseFuncname = TCaseOption.CoLowercase;
        } else if ("CoNoChange".equals(caseFuncname)) {
            gFmtOpts.caseFuncname = TCaseOption.CoNoChange;
        } else if ("CoInitCap".equals(caseFuncname)) {
            gFmtOpts.caseFuncname = TCaseOption.CoInitCap;
        }


        //Padding
        if (rd291.isSelected()) {
            gFmtOpts.wsPaddingOperatorArithmetic = true;
        } else if (rd301.isSelected()) {
            gFmtOpts.wsPaddingOperatorArithmetic = false;
        }
        if (rd351.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInFunction = true;
        } else if (rd352.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInFunction = false;
        }
        if (rd321.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInExpression = true;
        } else if (rd311.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInExpression = false;
        }
        if (rd312.isSelected()) {
            gFmtOpts.wsPaddingParenthesesOfSubQuery = true;
        } else if (rd322.isSelected()) {
            gFmtOpts.wsPaddingParenthesesOfSubQuery = false;
        }
        if (rd413.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInFunctionCall = true;
        } else if (rd423.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInFunctionCall = false;
        }
        if (rd314.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInFunctionCall = true;
        } else if (rd324.isSelected()) {
            gFmtOpts.wsPaddingParenthesesInFunctionCall = false;
        }
        if (rd312.isSelected()) {
            gFmtOpts.wsPaddingParenthesesOfTypename = true;
        } else if (rd133.isSelected()) {
            gFmtOpts.wsPaddingParenthesesOfTypename = false;
        }

        //Common Table Expression
        if (rd3181.isSelected()) {
            gFmtOpts.cteNewlineBeforeAs = true;
        } else if (rd3171.isSelected()) {
            gFmtOpts.cteNewlineBeforeAs = false;
        }

        //Declare statement
        if (rd381.isSelected()) {
            gFmtOpts.linebreakAfterDeclare = true;
        } else if (rd371.isSelected()) {
            gFmtOpts.linebreakAfterDeclare = false;
        }

        //Parameters in create procedure/function
        String parametersStyle = (String) type113.getValue();
        if ("AsStacked".equals(parametersStyle)) {
            gFmtOpts.parametersStyle = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(parametersStyle)) {
            gFmtOpts.parametersStyle = TAlignStyle.AsWrapped;
        }
        String parametersComma = (String) type1131.getValue();
        if ("LfAfterComma".equals(parametersComma)) {
            gFmtOpts.parametersComma = TLinefeedsCommaOption.LfAfterComma;
        } else if ("LfbeforeCommaWithSpace".equals(parametersComma)) {
            gFmtOpts.parametersComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        } else if ("LfBeforeComma".equals(parametersComma)) {
            gFmtOpts.parametersComma = TLinefeedsCommaOption.LfBeforeComma;
        }
        if (rd65.isSelected()) {
            gFmtOpts.beStyleFunctionLeftBEOnNewline
                    = true;
        } else if (rd66.isSelected()) {
            gFmtOpts.beStyleFunctionLeftBEOnNewline
                    = false;
        }
        gFmtOpts.beStyleFunctionLeftBEIndentSize
                = Integer.valueOf(beStyleIfElseSingleStmtIndentSizeText1.getText());
        if (rd313.isSelected()) {
            gFmtOpts.beStyleFunctionRightBEOnNewline
                    = true;
        } else if (rd323.isSelected()) {
            gFmtOpts.beStyleFunctionRightBEOnNewline
                    = false;
        }
        gFmtOpts.beStyleFunctionRightBEIndentSize
                = Integer.valueOf(beStyleIfElseSingleStmtIndentSizeText11.getText());
        if (rd3141.isSelected()) {
            gFmtOpts.beStyleFunctionFirstParamInNewline
                    = true;
        } else if (rd3241.isSelected()) {
            gFmtOpts.beStyleFunctionFirstParamInNewline
                    = false;
        }

        //Execute statement
        if (rd261.isSelected()) {
            gFmtOpts.linebreakBeforeParamInExec
                    = true;
        } else if (rd271.isSelected()) {
            gFmtOpts.linebreakBeforeParamInExec
                    = false;
        }

        //Blank lines
        String emptyLines = (String) type1212.getValue();
        if ("EloMergeIntoOne".equals(emptyLines)) {
            gFmtOpts.emptyLines = TEmptyLinesOption.EloMergeIntoOne;
        } else if ("EloRemove".equals(parametersComma)) {
            gFmtOpts.emptyLines = TEmptyLinesOption.EloRemove;
        } else if ("EloPreserve".equals(parametersComma)) {
            gFmtOpts.emptyLines = TEmptyLinesOption.EloPreserve;
        }
        if (rd111.isSelected()) {
            gFmtOpts.noEmptyLinesBetweenMultiSetStmts
                    = true;
        } else if (rd121.isSelected()) {
            gFmtOpts.noEmptyLinesBetweenMultiSetStmts
                    = false;
        }

        //Line number
        if (rd332.isSelected()) {
            gFmtOpts.linenumberEnabled
                    = true;
        } else if (rd333.isSelected()) {
            gFmtOpts.linenumberEnabled
                    = false;
        }
        if (rd345.isSelected()) {
            gFmtOpts.linenumberZeroBased
                    = true;
        } else if (rd346.isSelected()) {
            gFmtOpts.linenumberZeroBased
                    = false;
        }
        gFmtOpts.linenumberLeftMargin
                = Integer.valueOf(tabSize5.getText());
        gFmtOpts.linenumberRightMargin
                = Integer.valueOf(beStyleBlockLeftBEIndentSizeText5.getText());

        //Parameters in function Call
        String functionCallParametersStyle = (String) type1122.getValue();
        if ("AsStacked".equals(functionCallParametersStyle)) {
            gFmtOpts.functionCallParametersStyle = TAlignStyle.AsStacked;
        } else if ("AsWrapped".equals(functionCallParametersStyle)) {
            gFmtOpts.functionCallParametersStyle = TAlignStyle.AsWrapped;
        }
        String functionCallParametersComma = (String) type112.getValue();
        if ("LfAfterComma".equals(functionCallParametersComma)) {
            gFmtOpts.functionCallParametersComma = TLinefeedsCommaOption.LfAfterComma;
        } else if ("LfbeforeCommaWithSpace".equals(functionCallParametersComma)) {
            gFmtOpts.functionCallParametersComma = TLinefeedsCommaOption.LfbeforeCommaWithSpace;
        } else if ("LfBeforeComma".equals(functionCallParametersComma)) {
            gFmtOpts.functionCallParametersComma = TLinefeedsCommaOption.LfBeforeComma;
        }

        //Used for compact mode
        gFmtOpts.lineWidth
                = Integer.valueOf(tabSize1.getText());
        String compactMode = (String) type1121.getValue();
        if ("CpmNone".equals(compactMode)) {
            gFmtOpts.compactMode = TCompactMode.CpmNone;
        } else if ("Cpmugly".equals(compactMode)) {
            gFmtOpts.compactMode = TCompactMode.Cpmugly;
        }
    }


    @FXML
    void copy(ActionEvent event) {
        try {
            if ("".equals(formatResult.getText())) {
                BouncedUtil.failBounced("copy");
                copyButton.setDisable(true);
            } else {
                copyButton.setDisable(false);
                formatResult.selectAll();
                formatResult.copy();
                // 两次避免第一次复制不上
                formatResult.selectAll();
                formatResult.copy();
                BouncedUtil.successBounced("copy");
            }
        } catch (Exception e) {
            e.printStackTrace();
            BouncedUtil.failBounced("copy");
        }
    }

}

