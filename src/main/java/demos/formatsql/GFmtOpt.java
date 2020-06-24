
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
	 * @todo add sample1
	 */
	public TAlignStyle selectColumnlistStyle = TAlignStyle.AsStacked;

	/**
	 * the comma style in select statement
	 * @todo add sample2
	 */
	public TLinefeedsCommaOption selectColumnlistComma = TLinefeedsCommaOption.LfAfterComma;

	/**
	 * each column in the select statement should append a new line.
	 * @todo add sample3
	 */
	public boolean selectItemInNewLine = false;

	/**
	 * @todo add sample4
	 */
	public boolean alignAliasInSelectList = true;

	/**
	 * @todo add sample5
	 */
	public boolean treatDistinctAsVirtualColumn = false;

	// select from clause / Join clause
	/**
	 * the table name style in the from clause
	 * @todo add sample6
	 */
	public TAlignStyle selectFromclauseStyle = TAlignStyle.AsStacked;

	/**
	 * the comma style in the from clause
	 * @todo add sample7
	 */
	public TLinefeedsCommaOption selectFromclauseComma = TLinefeedsCommaOption.LfAfterComma;

	/**
	 * each column in the table names in the form clause should start with a new
	 * line.
	 * @todo add sample8
	 */
	public boolean fromClauseInNewLine = false;

	/**
	 * the join clause should start with a new line.
	 * @todo add sample9
	 */
	public boolean selectFromclauseJoinOnInNewline = true;

	/**
	 * align 'join' keyword with 'from' keyword
	 * @todo add sample10
	 */
	public boolean alignJoinWithFromKeyword = false;

	// And/Or keyword under where
	/**
	 * in the where clause, the 'and' and 'or' keyword should under the 'where'
	 * keyword
	 * @todo add sample11
	 */
	public boolean andOrUnderWhere = false;

	// Insert statement

	/**
	 * the align style for the insert column
	 * @todo add sample12
	 */
	public TAlignStyle insertColumnlistStyle = TAlignStyle.AsStacked;

	/**
	 * the value style in insert statement
	 * @todo add sample13
	 */
	public TAlignStyle insertValuelistStyle = TAlignStyle.AsStacked;

	// create table
	/**
	 * @todo add sample14
	 */
	public boolean beStyleCreatetableLeftBEOnNewline = false;
	/**
	 * @todo add sample15
	 */
	public boolean beStyleCreatetableRightBEOnNewline = false;
	/**
	 * @todo add sample16
	 */
	public boolean createtableListitemInNewLine = false;
	/**
	 * @todo add sample17
	 */
	public TAlignOption createtableFieldlistAlignOption = TAlignOption.AloLeft;

	// default options
	/**
	 * @todo add sample18
	 */
	public TLinefeedsCommaOption defaultCommaOption = TLinefeedsCommaOption.LfAfterComma;
	/**
	 * @todo add sample19
	 */
	public TAlignStyle defaultAligntype = TAlignStyle.AsStacked;

	// Indent
	/**
	 * @todo add sample20
	 */
	public Integer indentLen = 2;
	/**
	 * @todo add sample21
	 */
	public Boolean useTab = false;
	/**
	 * @todo add sample22
	 */
	public Integer tabSize = 2;
	public Integer beStyleFunctionBodyIndent = 2;
	/**
	 * @todo add sample23
	 */
	public Boolean beStyleBlockLeftBEOnNewline = true;
	/**
	 * @todo add sample24
	 */
	public Integer beStyleBlockLeftBEIndentSize = 2;
	/**
	 * @todo add sample25
	 */
	public Integer beStyleBlockRightBEIndentSize = 2;
	/**
	 * @todo add sample26
	 */
	public Integer beStyleBlockIndentSize = 2;
	/**
	 * @todo add sample27
	 */
	public Integer beStyleIfElseSingleStmtIndentSize = 2;

	// case when
	/**
	 * @todo add sample28
	 */
	public Boolean caseWhenThenInSameLine = false;
	/**
	 * @todo add sample29
	 */
	public Integer indentCaseFromSwitch = 2;
	/**
	 * @todo add sample30
	 */
	public Integer indentCaseThen = 0;

	// keyword align option
	/**
	 * @todo add sample31
	 */
	public TAlignOption selectKeywordsAlignOption = TAlignOption.AloLeft;

	// case option
	/**
	 * @todo add sample32
	 */
	public TCaseOption caseKeywords = TCaseOption.CoUppercase;
	/**
	 * @todo add sample33
	 */
	public TCaseOption caseIdentifier = TCaseOption.CoNoChange;
	/**
	 * @todo add sample34
	 */
	public TCaseOption caseQuotedIdentifier = TCaseOption.CoNoChange;
	/**
	 * @todo add sample35
	 */
	public TCaseOption caseFuncname = TCaseOption.CoInitCap;
	/**
	 * @todo add sample36
	 */
	public TCaseOption caseDatatype = TCaseOption.CoUppercase;

	// WSPadding
	/**
	 * @todo add sample37
	 */
	public Boolean wsPaddingOperatorArithmetic = true;
	/**
	 * @todo add sample38
	 */
	public Boolean wsPaddingParenthesesInFunction = false;
	/**
	 * @todo add sample39
	 */
	public Boolean wsPaddingParenthesesInExpression = true;
	/**
	 * @todo add sample40
	 */
	public Boolean wsPaddingParenthesesOfSubQuery = false;
	/**
	 * @todo add sample41
	 */
	public Boolean wsPaddingParenthesesInFunctionCall = false;
	/**
	 * @todo add sample42
	 */
	public Boolean wsPaddingParenthesesOfTypename = false;

	// CTE
	/**
	 * @todo add sample43
	 */
	public Boolean cteNewlineBeforeAs = true;

	/**
	 * declare statement
	 * @todo add sample44
	 */
	public Boolean linebreakAfterDeclare = false;

	// create function
	/**
	 * @todo add sample45
	 */
	public TAlignStyle parametersStyle = TAlignStyle.AsStacked;

	/**
	 * @todo add sample46
	 */
	public TLinefeedsCommaOption parametersComma = TLinefeedsCommaOption.LfAfterComma;

	/**
	 * @todo add sample47
	 */
	public Boolean beStyleFunctionLeftBEOnNewline = false;

	/**
	 * @todo add sample48
	 */
	public Integer beStyleFunctionLeftBEIndentSize = 0;

	/**
	 * @todo add sample49
	 */
	public Boolean beStyleFunctionRightBEOnNewline = true;

	/**
	 * @todo add sample50
	 */
	public Integer beStyleFunctionRightBEIndentSize = 0;

	/**
	 * @todo add sample51
	 */
	public Boolean beStyleFunctionFirstParamInNewline = false;

	/**
	 * used for execute statement
	 * @todo add sample52
	 */
	public Boolean linebreakBeforeParamInExec = true;

	// the empty lines
	/**
	 * @todo add sample53
	 */
	public TEmptyLinesOption emptyLines = TEmptyLinesOption.EloMergeIntoOne;
	/**
	 * @todo add sample54
	 */
	public Boolean insertBlankLineInBatchSqls = false;
	/**
	 * @todo add sample55
	 */
	public Boolean noEmptyLinesBetweenMultiSetStmts = false;

	// line number
	/**
	 * @todo add sample56
	 */
	public Boolean linenumberEnabled = false;
	/**
	 * @todo add sample57
	 */
	public Boolean linenumberZeroBased = false;
	/**
	 * @todo add sample58
	 */
	public Integer linenumberLeftMargin = 0;
	/**
	 * @todo add sample59
	 */
	public Integer linenumberRightMargin = 2;

	/**
	 * @todo add sample60
	 */
	public TAlignStyle functionCallParametersStyle = TAlignStyle.AsWrapped;
	/**
	 * @todo add sample61
	 */
	public TLinefeedsCommaOption functionCallParametersComma = TLinefeedsCommaOption.LfAfterComma;

	/**
	 * @todo add sample62
	 */
	public Boolean removeComment = false;

	// used for compact mode
	/**
	 * @todo add sample63
	 */
	public TCompactMode compactMode = TCompactMode.CpmNone;
	/**
	 * @todo add sample64
	 */
	public Integer lineWidth = 99;

	private final static GOutputFmt defaultOutputFmt = GOutputFmt.ofSql;

	/**
	 * used for sql output format.
	 * @todo add sample65
	 * @see GOutputFmt
	 */
	public GOutputFmt outputFmt = defaultOutputFmt;

	public String tabHtmlString = "&nbsp;&nbsp;&nbsp;&nbsp;";

	public GFmtOpt( String sessionId )
	{
		this.sessionId = sessionId;
	}

}
