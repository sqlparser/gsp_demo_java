
package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TObjectName;
import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;

public class ViewColumn {

    private View view;

    private int id;
    private String name;

    private Pair<Long, Long> startPosition;
    private Pair<Long, Long> endPosition;

    private TObjectName columnObject;
    private List<TObjectName> starLinkColumns = new ArrayList<TObjectName>();

    private int columnIndex;
    private boolean showStar;

    public ViewColumn(View view, TObjectName columnObject, int index) {
        if (view == null || columnObject == null)
            throw new IllegalArgumentException("TableColumn arguments can't be null.");

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        this.columnObject = columnObject;

        TSourceToken startToken = columnObject.getStartToken();
        TSourceToken endToken = columnObject.getEndToken();
        this.startPosition = new Pair<Long, Long>(startToken.lineNo,
                startToken.columnNo);
        this.endPosition = new Pair<Long, Long>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length());

        if (!"".equals(columnObject.getColumnNameOnly()))
            this.name = columnObject.getColumnNameOnly();
        else
            this.name = columnObject.toString();

        this.name = SQLUtil.trimColumnStringQuote(name);
        
        this.view = view;
        this.columnIndex = index;
        view.addColumn(this);
    }

    public View getView() {
        return view;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Pair<Long, Long> getStartPosition() {
        return startPosition;
    }

    public Pair<Long, Long> getEndPosition() {
        return endPosition;
    }

    public TObjectName getColumnObject() {
        return columnObject;
    }

    public void bindStarLinkColumns(List<TObjectName> starLinkColumns) {
        if (starLinkColumns != null && !starLinkColumns.isEmpty()) {
            this.starLinkColumns = starLinkColumns;
        }
    }

    public List<TObjectName> getStarLinkColumns() {
        return starLinkColumns;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

	public boolean isShowStar() {
		return showStar;
	}

	public void setShowStar(boolean showStar) {
		this.showStar = showStar;
	}

}
