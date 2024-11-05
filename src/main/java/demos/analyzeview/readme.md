## Description
Parse Teradata SQL that creates Teradata views and identify the name of the target view,
all input tables/views, the target column names of the view and all the source table/view 
and columns for the target column.

## Usage
`java Analyze_View scriptfile [/o <output file path>]`

## Related demo

## Changes
-  [2019-02-26, Can't work under the latest GSP java core library](https://github.com/sqlparser/gsp_demo/issues/3)
-  [2012-11-14, First version](https://github.com/sqlparser/wings/issues/166)
