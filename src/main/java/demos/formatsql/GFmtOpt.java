
package gudusoft.gsqlparser.pp.para;

import gudusoft.gsqlparser.pp.para.styleenums.TAlignOption;
import gudusoft.gsqlparser.pp.para.styleenums.TAlignStyle;
import gudusoft.gsqlparser.pp.para.styleenums.TCaseOption;
import gudusoft.gsqlparser.pp.para.styleenums.TCompactMode;
import gudusoft.gsqlparser.pp.para.styleenums.TEmptyLinesOption;
import gudusoft.gsqlparser.pp.para.styleenums.TLinefeedsCommaOption;

/**
 * the format options
 * 
 * @author zhoujun
 */
public class GFmtOpt
{

	/**
	 * the session id. each thread can own only one id.
	 */
	public final String sessionId;

	/**
	 * if it is true, the helper class will operate the source tokens. if not,
	 * the helper class will ignore the operation request
	 */
	public boolean opearateSourceToken = true;

	// select list, group by clause, order by Clause
	/**
	 * the align style for the select column
	 */
	public TAlignStyle selectColumnlistStyle = TAlignStyle.AsStacked;

	/**
	 * the comma style in select statement
	 */
	public TLinefeedsCommaOption selectColumnlistComma = TLinefeedsCommaOption.LfAfterComma;

	/**
	 * each column in the select statement should append a new line.
	 */
	public boolean selectItemInNewLine = false;

	/**
	 * 
	 */
	public boolean alignAliasInSelectList = true;

	/**
	 * 
	 */
	public boolean treatDistinctAsVirtualColumn = false;

	// select from clause / Join clause
	/**
	 * the table name style in the from clause
	 */
	public TAlignStyle selectFromclauseStyle = TAlignStyle.AsStacked;

	/**
	 * the comma style in the from clause
	 */
	public TLinefeedsCommaOption selectFromclauseComma = TLinefeedsCommaOption.LfAfterComma;

	/**
	 * each column in the table names in the form clause should start with a new
	 * line.
	 */
	public boolean fromClauseInNewLine = false;

	/**
	 * the join clause should start with a new line.
	 */
	public boolean selectFromclauseJoinOnInNewline = true;

	/**
	 * align 'join' keyword with 'from' keyword
	 */
	public boolean alignJoinWithFromKeyword = false;

	// And/Or keyword under where
	/**
	 * in the where clause, the 'and' and 'or' keyword should under the 'where'
	 * keyword
	 */
	public boolean andOrUnderWhere = false;

	// Insert statement

	/**
	 * the align style for the insert column
	 */
	public TAlignStyle insertColumnlistStyle = TAlignStyle.AsStacked;

	/**
	 * the value style in insert statement
	 */
	public TAlignStyle insertValuelistStyle = TAlignStyle.AsStacked;

	// create table
	public boolean beStyleCreatetableLeftBEOnNewline = false;
	public boolean beStyleCreatetableRightBEOnNewline = false;
	public boolean createtableListitemInNewLine = false;
	public TAlignOption createtableFieldlistAlignOption = TAlignOption.AloLeft;

	// default options
	public TLinefeedsCommaOption defaultCommaOption = TLinefeedsCommaOption.LfAfterComma;
	public TAlignStyle defaultAligntype = TAlignStyle.AsStacked;

	// Indent
	public Integer indentLen = 2;
	public Boolean useTab = false;
	public Integer tabSize = 2;
	public Integer beStyleFunctionBodyIndent = 2;
	public Boolean beStyleBlockLeftBEOnNewline = true;
	public Integer beStyleBlockLeftBEIndentSize = 2;
	public Integer beStyleBlockRightBEIndentSize = 2;
	public Integer beStyleBlockIndentSize = 2;
	public Integer beStyleIfElseSingleStmtIndentSize = 2;

	// case when
	public Boolean caseWhenThenInSameLine = false;
	public Integer indentCaseFromSwitch = 2;
	public Integer indentCaseThen = 0;

	// keyword align option
	public TAlignOption selectKeywordsAlignOption = TAlignOption.AloLeft;

	// case option
	public TCaseOption caseKeywords = TCaseOption.CoUppercase;
	public TCaseOption caseIdentifier = TCaseOption.CoNoChange;
	public TCaseOption caseQuotedIdentifier = TCaseOption.CoNoChange;
	public TCaseOption caseFuncname = TCaseOption.CoInitCap;
	public TCaseOption caseDatatype = TCaseOption.CoUppercase;

	// WSPadding
	public Boolean wsPaddingOperatorArithmetic = true;
	public Boolean wsPaddingParenthesesInFunction = false;
	public Boolean wsPaddingParenthesesInExpression = true;
	public Boolean wsPaddingParenthesesOfSubQuery = false;
	public Boolean wsPaddingParenthesesInFunctionCall = false;
	public Boolean wsPaddingParenthesesOfTypename = false;

	// CTE
	public Boolean cteNewlineBeforeAs = true;

	/**
	 * declare statement
	 */
	public Boolean linebreakAfterDeclare = false;

	// create function
	public TAlignStyle parametersStyle = TAlignStyle.AsStacked;

	public TLinefeedsCommaOption parametersComma = TLinefeedsCommaOption.LfAfterComma;

	public Boolean beStyleFunctionLeftBEOnNewline = false;

	public Integer beStyleFunctionLeftBEIndentSize = 0;

	public Boolean beStyleFunctionRightBEOnNewline = true;

	public Integer beStyleFunctionRightBEIndentSize = 0;

	public Boolean beStyleFunctionFirstParamInNewline = false;

	/**
	 * used for execute statement
	 */
	public Boolean linebreakBeforeParamInExec = true;

	// the empty lines
	public TEmptyLinesOption emptyLines = TEmptyLinesOption.EloMergeIntoOne;
	public Boolean insertBlankLineInBatchSqls = false;
	public Boolean noEmptyLinesBetweenMultiSetStmts = false;

	// line number
	public Boolean linenumberEnabled = false;
	public Boolean linenumberZeroBased = false;
	public Integer linenumberLeftMargin = 0;
	public Integer linenumberRightMargin = 2;

	public TAlignStyle functionCallParametersStyle = TAlignStyle.AsWrapped;
	public TLinefeedsCommaOption functionCallParametersComma = TLinefeedsCommaOption.LfAfterComma;

	public Boolean removeComment = false;

	// used for compact mode
	public TCompactMode compactMode = TCompactMode.CpmNone;
	public Integer lineWidth = 99;

	private final static GOutputFmt defaultOutputFmt = GOutputFmt.ofSql;

	/**
	 * used for sql output format.
	 * 
	 * @see GOutputFmt
	 */
	public GOutputFmt outputFmt = defaultOutputFmt;

	public String tabHtmlString = "&nbsp;&nbsp;&nbsp;&nbsp;";

	public GFmtOpt( String sessionId )
	{
		this.sessionId = sessionId;
	}

}
