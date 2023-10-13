## Description
Pull out every column name from the SQL script and shows whether a column 
appears in the projections(select list), restrictions(where condition), 
joins (taking into account that the JOIN might be done in the WHERE clause), group, order, etc.

The result of each input SQL file will be saved in a text file in the format like this:

The header line:

TABLE_NAME,COLUMN_NAME,PROJECTION_FLAG,RESTRICTION_FLAG,JOIN_FLAG,GROUP_BY_FLAG,ORDER_BY_FLAG

and found column:

VW_MMA_BAY_QTR_DIM,QTR_STRT_DT,0,1,1,0,0


## Usage
`java ColumnAnalyze <input files directory> <output files directory>`

A text file included the generated result will be saved under output files directory with .txt extension and the same name.

input files directory: The directory includes the input SQL script files.

output files directory: The directory includes the text file with the generated result.


## Related demo
This tool will call the [columnImpact\ColumnImpact.java](../antiSQLInjection/columnImpact) to do further analysis.

## Changes
* [First version(2013-11-25)](https://github.com/sqlparser/wings/issues/255)