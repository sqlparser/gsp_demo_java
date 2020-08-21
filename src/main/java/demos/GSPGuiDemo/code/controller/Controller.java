package demos.GSPGuiDemo.code.controller;

import demos.GSPGuiDemo.code.constant.DbConstant;
import demos.GSPGuiDemo.code.util.BouncedUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<Object> type1211;

    @FXML
    private RadioButton rd12;

    @FXML
    private Tab exportMenu;

    @FXML
    private RadioButton rd11;
    @FXML
    private RadioButton rd13;
    @FXML
    private RadioButton rd14;
    @FXML
    private RadioButton rd131;
    @FXML
    private RadioButton rd141;
    @FXML
    private RadioButton rd132;
    @FXML
    private RadioButton rd142;

    @FXML
    private Button copyButton;

    @FXML
    private TextArea sqlData;

    @FXML
    private Label typeTitle;

    @FXML
    private ComboBox<Object> type;

    @FXML
    private ToggleGroup SampleModeGroup2;

    @FXML
    private ToggleGroup SampleModeGroup;

    @FXML
    private ComboBox<Object> type111;

    @FXML
    private TextArea formatResult;

    @FXML
    private ComboBox<Object> type11;

    @FXML
    private ComboBox<Object> type12111;

    @FXML
    private Button formatButton;

    @FXML
    private ComboBox<Object> type211;

    @FXML
    private Tab formatMenu;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copyButton.setDisable(true);

        //初始化databases下拉框数据
        initSelects(type, DbConstant.dbs);

        //初始化General
        rd11.setSelected(true);
        initSelects(type11, Arrays.asList("AsStacked", "AsWrapped"));

        //初始化Select list
        initSelects(type211, Arrays.asList("AsStacked", "AsWrapped"));
        initSelects(type111, Arrays.asList("LfAfterComma", "LfbeforeCommaWithSpace", "LfBeforeComma"));
        rd14.setSelected(true);
        rd141.setSelected(true);
        rd132.setSelected(true);
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
                System.out.println("sql parse error is:" + sqlparser.getErrormessage());
                BouncedUtil.failBounced("format");
            }
        } catch (Exception e) {
            e.printStackTrace();
            BouncedUtil.failBounced("format");
        }
    }


    //设置参数值
    private void setGFmtOpts(GFmtOpt gFmtOpts) {
        if (rd11.isSelected()) {
            gFmtOpts.opearateSourceToken = true;
        } else if (rd12.isSelected()) {
            gFmtOpts.opearateSourceToken = false;
        }

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

        if (rd141.isSelected()) {
            gFmtOpts.alignAliasInSelectList = true;
        } else if (rd131.isSelected()) {
            gFmtOpts.alignAliasInSelectList = false;
        }

        if (rd142.isSelected()) {
            gFmtOpts.treatDistinctAsVirtualColumn = true;
        } else if (rd132.isSelected()) {
            gFmtOpts.treatDistinctAsVirtualColumn = false;
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

