## Description
Pull out every column name from the SQL script and shows whether a column 
appears in the projections(select list), restrictions(where condition), 
joins (taking into account that the JOIN might be done in the WHERE clause), group, order, etc.

The result of each input SQL file will be saved in a text file in the format like this:

The header line:

>TABLE_NAME,COLUMN_NAME,PROJECTION_FLAG,RESTRICTION_FLAG,JOIN_FLAG,GROUP_BY_FLAG,ORDER_BY_FLAG

and found column:

>VW_MMA_BAY_QTR_DIM,QTR_STRT_DT,0,1,1,0,0

**This tool is created as requested by one of our customers with many specific requirements.**


## Usage
`java -jar PlannerAnalyze.jar <input files directory> [-log] [-debug]`

* input files directory, the directory includes the input SQL files which has .pln extention.
* There will be 2 output files generated for each input file, `parsed_<timestamp>.dat` and  `mv_source_<timestamp>.dat`,
these 2 files will be saved under the directory `peak_techniques_output/puredata_optimisation/` where the tool is running.
    * parsed_<timestamp>.dat
		>TABLE_NAME,COLUMN_NAME,PROJECTION_FLAG,RESTRICTION_FLAG,JOIN_FLAG,GROUP_BY_FLAG,ORDER_BY_FLAG
		>VW_MMA_BAY_QTR_DIM,QTR_STRT_DT,0,1,1,0,0

    * mv_source_<timestamp>.dat
		> PLAN_ID1,DATABASE..TABLE_NAME1,COLUMN_1 COLUMN_2 COLUMN_N
		> PLAN_ID1,DATABASE..TABLE_NAME2,COLUMN_5 COLUMN_9 COLUMN_Y
		> PLAN_ID2,DATABASE..TABLE_NAME3,COLUMN_1 COLUMN_2 COLUMN_N
		> PLAN_ID2,DATABASE..TABLE_NAME4,COLUMN_5 COLUMN_9 COLUMN_Y

		Each “plan ID” (file name) should generate as many lines as tables are used in the
		query. So if PLAN_ID1 has two tables in it, then it should have 2 lines in the file
		(one table’s columns per line). The ID (name of the *.pln file), then a comma, then
		the database..table followed by a comma, and a list of the columns for that table
		that appears anywhere in each query (space delimiter between the columns).

		**The data should be ordered by the second column (the database..table_name).**
  
* -log, if specified, then generate a pa.log file including:
    * How many pln file was processed, how many was failed.
    * If there are failed files, list file name and related sql.
	

* -debug, which will generate a file with name: debug.log that including all file names that doesn't generate any output.  

## Related demo
This tool will call the [columnImpact\ColumnImpact.java](../antiSQLInjection/columnImpact) to do further analysis.

## Changes
* [First version(2014-02-14)](https://github.com/sqlparser/wings/issues/255) 