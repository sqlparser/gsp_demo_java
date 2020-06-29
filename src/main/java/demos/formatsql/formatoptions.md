## GENERAL SQL PARSER AND SQL PRETTY PRINTER FORMATTER OPTIONS
#### General
* [opearateSourceToken](#1opearatesourcetoken)
* [defaultAligntype](#20defaultaligntype)
* [removeComment](#65removecomment)
* [outputFmt](#68outputfmt)
#### Select list
* [selectColumnlistStyle](#2selectcolumnliststyle)
* [selectColumnlistComma](#3selectcolumnlistcomma)
* [selectItemInNewLine](#4selectiteminnewline)
* [alignAliasInSelectList](#5alignaliasinselectlist)
* [treatDistinctAsVirtualColumn](#6treatdistinctasvirtualcolumn)
#### Select from clause / Join clause
* [selectFromclauseStyle](#7selectfromclausestyle)
* [selectFromclauseComma](#8selectfromclausecomma)
* [fromClauseInNewLine](#9fromclauseinnewline)
* [selectFromclauseJoinOnInNewline](#10selectfromclausejoinoninnewline)
* [alignJoinWithFromKeyword](#11alignjoinwithfromkeyword)
#### And/Or keyword under where
* [andOrUnderWhere](#12andorunderwhere)
#### Insert list
* [insertColumnlistStyle](#13insertcolumnliststyle)
* [insertValuelistStyle](#14insertvalueliststyle)
* [defaultCommaOption](#19defaultcommaoption)
#### Create table
* [beStyleCreatetableLeftBEOnNewline](#15bestylecreatetableleftbeonnewline)
* [beStyleCreatetableRightBEOnNewline](#16bestylecreatetablerightbeonnewline)
* [createtableListitemInNewLine](#17createtablelistiteminnewline)
* [createtableFieldlistAlignOption](#18createtablefieldlistalignoption)
#### Indent
* [indentLen](#21indentlen)
* [useTab](#22usetab)
* [tabSize](#23tabsize)
* [beStyleFunctionBodyIndent](#24bestylefunctionbodyindent)
* [beStyleBlockLeftBEOnNewline](#25bestyleblockleftbeonnewline)
* [beStyleBlockLeftBEIndentSize](#26bestyleblockleftbeindentsize)
* [beStyleBlockRightBEIndentSize](#27bestyleblockrightbeindentsize)
* [beStyleBlockIndentSize](#28bestyleblockindentsize)
* [beStyleIfElseSingleStmtIndentSize](#29bestyleifelsesinglestmtindentsize)
#### When Then clause
* [caseWhenThenInSameLine](#30casewhentheninsameline)
* [indentCaseFromSwitch](#31indentcasefromswitch)
* [indentCaseThen](#32indentcasethen)
#### Keyword align in select/delete/insert/update
* [selectKeywordsAlignOption](#33selectkeywordsalignoption)
#### Case options for various token
* [caseKeywords](#34casekeywords)
* [caseIdentifier](#35caseidentifier)
* [caseQuotedIdentifier](#36casequotedidentifier)
* [caseFuncname](#37casefuncname)
* [caseDatatype](#38casedatatype)
#### Padding
* [wsPaddingOperatorArithmetic](#39wspaddingoperatorarithmetic)
* [wsPaddingParenthesesInFunction](#40wspaddingparenthesesinfunction)
* [wsPaddingParenthesesInExpression](#41wspaddingparenthesesinexpression)
* [wsPaddingParenthesesOfSubQuery](#42wspaddingparenthesesofsubquery)
* [wsPaddingParenthesesInFunctionCall](#43wspaddingparenthesesinfunctioncall)
* [wsPaddingParenthesesInFunctionCall](#44wspaddingparenthesesinfunctioncall)
* [wsPaddingParenthesesOfTypename](#45wspaddingparenthesesoftypename)
#### Common Table Expression
* [cteNewlineBeforeAs](#46ctenewlinebeforeas)
#### Declare statement
* [linebreakAfterDeclare](#47linebreakafterdeclare)
#### Parameters in create procedure/function
* [parametersStyle](#48parametersstyle)
* [parametersComma](#49parameterscomma)
* [beStyleFunctionLeftBEOnNewline](#50bestylefunctionleftbeonnewline)
* [beStyleFunctionLeftBEIndentSize](#51bestylefunctionleftbeindentsize)
* [beStyleFunctionRightBEOnNewline](#52bestylefunctionrightbeonnewline)
* [beStyleFunctionRightBEIndentSize](#53bestylefunctionrightbeindentsize)
* [beStyleFunctionFirstParamInNewline](#54bestylefunctionfirstparaminnewline)
#### Execute statement
* [linebreakBeforeParamInExec](#55linebreakbeforeparaminexec)
#### Blank lines
* [emptyLines](#56emptylines)
* [insertBlankLineInBatchSqls](#57insertblanklineinbatchsqls)
* [noEmptyLinesBetweenMultiSetStmts](#58noemptylinesbetweenmultisetstmts)
#### Line number
* [linenumberEnabled](#59linenumberenabled)
* [linenumberZeroBased](#60linenumberzerobased)
* [linenumberLeftMargin](#61linenumberleftmargin)
* [linenumberRightMargin](#62linenumberrightmargin)
#### Parameters in function Call
* [functionCallParametersStyle](#63functioncallparametersstyle)
* [functionCallParametersComma](#64functioncallparameterscomma)
#### Used for compact mode
* [lineWidth](#67linewidth)
* [compactMode](#66compactmode)


#### 1.opearateSourceToken

**usage**: If it is true, the GSP operate the source tokens. If not, the GSP will ignore the operation request.

Type: boolean

Optional values: [true, false]

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID ,Customers.CustomerName, Orders.OrderDate FROM   Orders INNER JOIN Customers ON Orders. CustomerID=Customers.CustomerID;

```

#### 2.selectColumnlistStyle

**usage**: If it is AsStacked,every select column will start a new line. If it is AsWrapped, select column will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `AsWrapped`, SQL format:

```sql
SELECT Orders.OrderID ,Customers.CustomerName, Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

#### 3.selectColumnlistComma

**usage**: This option will change select statement comma position and white space.

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma]

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
SELECT Orders.OrderID
       , Customers.CustomerName
       , Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `LfBeforeComma`, SQL format:

```sql
SELECT Orders.OrderID
       ,Customers.CustomerName
       ,Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

#### 4.selectItemInNewLine

**usage**: If it is true, select token will create a new line.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `true`, SQL format:

```sql
SELECT
  Orders.OrderID,
  Customers.CustomerName,
  Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

#### 5.alignAliasInSelectList

**usage**: If it is true, select alias will be vertical alignment.

Type: boolean

Optional values: [true, false]

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT home_address AS h,
       age          AS a
FROM   person;
```

- When the value is `false`, SQL format:

```sql
SELECT home_address AS h,
       age AS a
FROM   person;
```

#### 6.treatDistinctAsVirtualColumn

**usage**: If it is true, `Distinct` will be treated a virtual column.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT DISTINCT Company
FROM   Orders;
```

- When the value is `true`, SQL format:

```sql
SELECT DISTINCT 
       Company
FROM   Orders;
```

#### 7.selectFromclauseStyle

**usage**: If it is AsStacked,from clause will start a new line. If it is AsWrapped, from clause will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
SELECT name,
       salary
FROM   person,
       company,
       employee;
```

- When the value is `AsWrapped`, SQL format:

```sql
SELECT name,
       salary
FROM   person, company, employee;
```

#### 8.selectFromclauseComma

**usage**: This option will change from clause comma position and white space.

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma]

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
SELECT name,
       salary
FROM   person,
       company,
       employee;
```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
SELECT name,
       salary
FROM   person
       , company
       , employee;
```

- When the value is `LfBeforeComma`, SQL format:

```sql
SELECT name,
       salary
FROM   person
       ,company
       ,employee;
```

#### 9.fromClauseInNewLine

**usage**: If it is true, from token will create a new line.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT name,
       salary
FROM   person,
       company,
       employee;
```

- When the value is `true`, SQL format:

```sql
SELECT name,
       salary
FROM  
  person,
  company,
  employee;
```

#### 10.selectFromclauseJoinOnInNewline

**usage**: If it is true, `join` and `on` will be different line.

Type: boolean

Optional values: [true, false]

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers ON Orders. CustomerID = Customers.CustomerID;

```

#### 11.alignJoinWithFromKeyword

**usage**: If it is true, join will vertical align with from token.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT Orders.OrderID,
       Customers.CustomerName,
       Orders.OrderDate
FROM   Orders
       INNER JOIN Customers
       ON Orders. CustomerID = Customers.CustomerID;

```

- When the value is `true`, SQL format:

```sql
SELECT     Orders.OrderID,
           Customers.CustomerName,
           Orders.OrderDate
FROM       Orders
INNER JOIN Customers
           ON Orders. CustomerID = Customers.CustomerID;

```

#### 12.andOrUnderWhere

**usage**: if it is true,in the where clause, the 'and' and 'or' keyword will under the 'where'

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT name,
       age
FROM   person
WHERE  age > 30
       AND sex = 'male'
       OR grade = '1'
```

- When the value is `true`, SQL format:

```sql
SELECT name,
       age
FROM   person
WHERE  age > 30
   AND sex = 'male'
    OR grade = '1'
```

#### 13.insertColumnlistStyle

**usage**: If it is AsStacked, every insert column will start a new line. If it is AsWrapped, insert column will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
INSERT INTO company
            (name,
             user_id,
             office)
VALUES        ('John',
               123,
               'Lloyds Office'), ('Billy',
                                  125,
                                  'London Office'), ('Miranda',
                                                     126,
                                                     'Bristol Office');
```

- When the value is `AsWrapped`, SQL format:

```sql
INSERT INTO company
            (name, user_id, office)
VALUES        ('John',
               123,
               'Lloyds Office'), ('Billy',
                                  125,
                                  'London Office'), ('Miranda',
                                                     126,
                                                     'Bristol Office');
```

#### 14.insertValuelistStyle

**usage**: If it is AsStacked, every insert value will start a new line. If it is AsWrapped, insert value will be same line.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
INSERT INTO company
            (name,
             user_id,
             office)
VALUES        ('John',
               123,
               'Lloyds Office'), ('Billy',
                                  125,
                                  'London Office'), ('Miranda',
                                                     126,
                                                     'Bristol Office');
```

- When the value is `AsWrapped`, SQL format:

```sql
INSERT INTO company
            (name,
             user_id,
             office)
VALUES        ('John', 123, 'Lloyds Office'), ('Billy', 125, 'London Office'), ('Miranda', 126, 'Bristol Office');
```

#### 15.beStyleCreatetableLeftBEOnNewline

**usage**: If it is true, each row of the SCHEMA in THE DDL will start new line from the left. If it is false, The default formatting rules will be used.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
CREATE TABLE `province`(`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `create_time` DATETIME DEFAULT NULL,
                        `update_time` DATETIME DEFAULT NULL,
                        `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

- When the value is `true`, SQL format:

```sql
CREATE TABLE `province`
  (`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
   `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
   `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
   `create_time` DATETIME DEFAULT NULL,
   `update_time` DATETIME DEFAULT NULL,
   `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
   `flag`        INT DEFAULT '0',
   PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

#### 16.beStyleCreatetableRightBEOnNewline

```diff
- Not implemented yet
```

#### 17.createtableListitemInNewLine

**usage**: If it is true, The fields for each row in the DDL will start new line from the left. If it is false, The default formatting rules will be used.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
CREATE TABLE `province`(`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `create_time` DATETIME DEFAULT NULL,
                        `update_time` DATETIME DEFAULT NULL,
                        `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

- When the value is `true`, SQL format:

```sql
CREATE TABLE `province`(
  `id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

#### 18.createtableFieldlistAlignOption

**usage**: If it is AloRight, All fields in the DDL are right-aligned. If it is false, The default formatting rules will be used.

Type: TAlignStyle

Optional values: [AloRight, AloLeft]

Default value: AloLeft

- When the value is `AloLeft`, SQL format:

```sql
CREATE TABLE `province`(`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `create_time` DATETIME DEFAULT NULL,
                        `update_time` DATETIME DEFAULT NULL,
                        `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

- When the value is `AloRight`, SQL format:

```sql
CREATE TABLE `province`(         `id` VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                               `name` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                            `creator` VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `create_time` DATETIME DEFAULT NULL,
                        `update_time` DATETIME DEFAULT NULL,
                             `remark` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                               `flag` INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

#### 19.defaultCommaOption

**usage**: If it is LfbeforeCommaWithSpace, Commas are placed in front of each field and separated by Spaces. If it is LfBeforeComma, Commas are placed before each field.If it is LfAfterComma,The default formatting rules will be used.

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma]

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
CREATE TABLE `province`(`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `create_time` DATETIME DEFAULT NULL,
                        `update_time` DATETIME DEFAULT NULL,
                        `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
CREATE TABLE `province`(`id`            VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
                        , `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
                        , `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
                        , `create_time` DATETIME DEFAULT NULL
                        , `update_time` DATETIME DEFAULT NULL
                        , `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
                        , `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

- When the value is `LfBeforeComma`, SQL format:

```sql
CREATE TABLE `province`(`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
                        ,`name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
                        ,`creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
                        ,`create_time` DATETIME DEFAULT NULL
                        ,`update_time` DATETIME DEFAULT NULL
                        ,`remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
                        ,`flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

#### 20.defaultAligntype

**usage**: If it is AsWrapped, List them in order. If it is AsStacked,The default formatting rules will be used.

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped]

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
CREATE TABLE `province`(`id`          VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                        `name`        VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `creator`     VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `create_time` DATETIME DEFAULT NULL,
                        `update_time` DATETIME DEFAULT NULL,
                        `remark`      VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
                        `flag`        INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

- When the value is `AsWrapped`, SQL format:

```sql
CREATE TABLE `province`(`id` VARCHAR(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL ,`name` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_genera
l_ci DEFAULT NULL , `creator` VARCHAR(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL , `create_time` DATETIME DEFAULT NULL , `update_time` DATET
IME DEFAULT NULL , `remark` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL , `flag` INT DEFAULT '0',
                        PRIMARY KEY (`id`) ) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ;
```

#### 21.indentLen

```diff
- Not implemented yet
```

#### 22.useTab

**usage**: If it is true, The second argument of each executed statement is preceded by a TAB. If it is false,The default formatting rules will be used.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT name,
       age
FROM   person
WHERE  age > 30
       AND sex = 'male'
       OR grade = '1'
```

- When the value is `true`, SQL format:

```sql
SELECT name,
        age
FROM   person
WHERE  age > 30
        AND sex = 'male'
        OR grade = '1'
```

#### 23.tabSize

**usage**: Change the size of the TAB indent. 

Type: Integer

Default value: 2

``Note: It must be used with the parameter `useTab` to take effect``

- When the value is `2`, SQL format:

```sql
SELECT name,
        age
FROM   person
WHERE  age > 30
        AND sex = 'male'
        OR grade = '1'
```

- When the value is `10`, SQL format:

```sql
SELECT name,
          age
FROM   person
WHERE  age > 30
          AND sex = 'male'
          OR grade = '1'
```

#### 24.beStyleFunctionBodyIndent

```diff
- Not implemented yet
```

#### 25.beStyleBlockLeftBEOnNewline

**usage**: If it is true, The BEGIN keyword will start new line. If it is false,The default formatting rules will be used.

Type: boolean

Optional values: [true, false]

Default value: false

``Note: It must be used with the parameter `beStyleBlockLeftBEIndentSize` or `beStyleBlockRightBEIndentSize` and `beStyleBlockIndentSize` and `beStyleIfElseSingleStmtIndentSize` to take effect``

- When the value is `false`, beStyleBlockLeftBEIndentSize value is `2`,beStyleBlockIndentSize value is `2`,beStyleIfElseSingleStmtIndentSize value is `2` SQL format:

```sql
IF @cost <= @compareprice BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

- When the value is `true`, beStyleBlockLeftBEIndentSize value is `2`,beStyleBlockIndentSize value is `2`,beStyleIfElseSingleStmtIndentSize value is `2` SQL format:

```sql
IF @cost <= @compareprice
  BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

#### 26.beStyleBlockLeftBEIndentSize

**usage**: If it is true, The BEGIN and END keyword the indent size from the left. If it is false,The default formatting rules will be used.

Type: Integer

Default value: 2

``Note: It must be used with the parameter `beStyleBlockIndentSize` to take effect``

- When the value is `2`, beStyleBlockIndentSize value is `2` SQL format:

```sql
IF @cost <= @compareprice
  BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

- When the value is `0`, beStyleBlockIndentSize value is `2` SQL format:

```sql
IF @cost <= @compareprice
BEGIN
  PRINT 'These products can be purchased for less than '
END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

#### 27.beStyleBlockRightBEIndentSize

**usage**: If it is true, The statement between BEGIN and END and the indent size from the END keyword to the right. If it is false,The default formatting rules will be used.

Type: Integer

Default value: 2

``Note: It must be used with the parameter `beStyleBlockIndentSize` and `beStyleBlockLeftBEOnNewline` and `beStyleBlockIndentSize` and `beStyleIfElseSingleStmtIndentSize` to take effect``

- When the value is `2`, beStyleBlockIndentSize value is `2` and beStyleBlockLeftBEOnNewline value is false and beStyleIfElseSingleStmtIndentSize value is 2 SQL format:

```sql
IF @cost <= @compareprice BEGIN
  PRINT 'These products can be purchased for less than '
END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

- When the value is `0`, beStyleBlockIndentSize value is `2` and beStyleBlockLeftBEOnNewline value is false and beStyleIfElseSingleStmtIndentSize value is 2 SQL format:

```sql
IF @cost <= @compareprice BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

#### 28.beStyleBlockIndentSize

**usage**: If it is true, The indenting size between BEGIN and END. If it is false,The default formatting rules will be used.

Type: Integer

Default value: 2

``Note: It must be used with the parameter `beStyleBlockLeftBEIndentSize` to take effect``

- When the value is `2`, beStyleBlockLeftBEIndentSize value is `2` is 2 SQL format:

```sql
IF @cost <= @compareprice
  BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

- When the value is `0`, beStyleBlockLeftBEIndentSize value is `2` SQL format:

```sql
IF @cost <= @compareprice
  BEGIN
  PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

#### 29.beStyleIfElseSingleStmtIndentSize

**usage**: If it is true, The indent size of the statement after IF ELSE. If it is false,The default formatting rules will be used.

Type: Integer

Default value: 2

- When the value is `2`, SQL format:

```sql
IF @cost <= @compareprice
  BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
  PRINT 'The prices for all products in this category exceed '
```

- When the value is `0`,SQL format:

```sql
IF @cost <= @compareprice
  BEGIN
    PRINT 'These products can be purchased for less than '
  END
ELSE
PRINT 'The prices for all products in this category exceed '
```

#### 30.caseWhenThenInSameLine

**usage**: If it is true, When and Then in same line. If it is false,The default formatting rules will be used.

Type: boolean

Optional values: [true, false]

Default value: false

- When the value is `false`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `true`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue" THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen" THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

#### 31.indentCaseFromSwitch

**usage**: Change the scaling distance between the Case and the statement it executes.

Type: Integer

Default value: 2

- When the value is `2`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `10`, SQL format:

```sql
SELECT sid,
       CASE course
                 WHEN "shuxue"
                 THEN score
                 ELSE 0
       END AS shuxue,
       CASE course
                 WHEN "yuwen"
                 THEN score
                 ELSE 0
       END AS yuwen
FROM   course;
```

#### 32.indentCaseThen

**usage**: Change the zoom distance between Case and Then.

Type: Integer

Default value: 0

- When the value is `0`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `10`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
                   THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
                   THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

#### 33.selectKeywordsAlignOption

**usage**: If it is AloRight, Align keywords to the right. If it is AloLeft,The default formatting rules will be used. 

Type: TAlignOption

Optional values: [AloLeft, AloRight] 

Default value: AloLeft

- When the value is `AloLeft`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `AloRight`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
  FROM course;
```

#### 34.caseKeywords

**usage**: If it is CoUppercase, Keywords are capitalized. If it is CoLowercase,Keywords are all lowercase. If it is CoNoChange,Don't change. If it is CoInitCap,Keywords are Capital letter. 

Type: TCaseOption

Optional values: [CoUppercase, CoLowercase, CoNoChange, CoInitCap] 

Default value: CoUppercase

- When the value is `CoUppercase`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoLowercase`, SQL format:

```sql
select sid,
       case course
         when "shuxue"
         then score
         else 0
       end as shuxue,
       case course
         when "yuwen"
         then score
         else 0
       end as yuwen
from   course;
```

- When the value is `CoNoChange`, SQL format:

```sql
SELECT sid,
       case COURSE
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE COURSE
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoInitCap`, SQL format:

```sql
Select sid,
       Case COURSE
         When "shuxue"
         Then score
         Else 0
       End As shuxue,
       Case COURSE
         When "yuwen"
         Then score
         Else 0
       End As yuwen
From   course;
```

#### 35.caseIdentifier

**usage**: If it is CoUppercase, Identifier are capitalized. If it is CoLowercase,Identifier are all lowercase. If it is CoNoChange,Don't change. If it is CoInitCap,Identifier are Capital letter. 

Type: TCaseOption

Optional values: [CoUppercase, CoLowercase, CoNoChange, CoInitCap] 

Default value: CoNoChange

- When the value is `CoUppercase`, SQL format:

```sql
SELECT SID,
       CASE COURSE
         WHEN "shuxue"
         THEN SCORE
         ELSE 0
       END AS SHUXUE,
       CASE COURSE
         WHEN "yuwen"
         THEN SCORE
         ELSE 0
       END AS YUWEN
FROM   COURSE;
```

- When the value is `CoLowercase`, SQL format:

```sql
SELECT sid,
       CASE course
         WHEN "shuxue"
         THEN score
         ELSE 0
       END AS shuxue,
       CASE course
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoNoChange`, SQL format:

```sql
SELECT sid,
       case COURSE
         WHEN "shuxue"
         THEN SCORE
         ELSE 0
       END AS SHUXUE,
       CASE COURSE
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoInitCap`, SQL format:

```sql
Select sid,
       Case COURSE
         When "shuxue"
         Then SCORE
         Else 0
       End As SHUXUE,
       Case COURSE
         When "yuwen"
         Then score
         Else 0
       End As yuwen
From   course;
```

#### 36.caseQuotedIdentifier

**usage**: If it is CoUppercase,Quoted Identifier are capitalized. If it is CoLowercase,Quoted Identifier are all lowercase. If it is CoNoChange,Don't change. If it is CoInitCap,Quoted Identifier are Capital letter. 

Type: TCaseOption

Optional values: [CoUppercase, CoLowercase, CoNoChange, CoInitCap] 

Default value: CoNoChange

- When the value is `CoUppercase`, SQL format:

```sql
SELECT sid,
       CASE COURSE
         WHEN "SHUXUE"
         THEN SCORE
         ELSE 0
       END AS SHUXUE,
       CASE COURSE
         WHEN "YUWEN"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoLowercase`, SQL format:

```sql
SELECT sid,
       CASE COURSE
         WHEN "shuxue"
         THEN SCORE
         ELSE 0
       END AS SHUXUE,
       CASE COURSE
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoNoChange`, SQL format:

```sql
SELECT sid,
       case COURSE
         WHEN "shuxue"
         THEN SCORE
         ELSE 0
       END AS SHUXUE,
       CASE COURSE
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

- When the value is `CoInitCap`, SQL format:

```sql
SELECT sid,
       CASE COURSE
         WHEN "shuxue"
         THEN SCORE
         ELSE 0
       END AS SHUXUE,
       CASE COURSE
         WHEN "yuwen"
         THEN score
         ELSE 0
       END AS yuwen
FROM   course;
```

#### 37.caseFuncname

**usage**: If it is CoUppercase,Funcname are capitalized. If it is CoLowercase,Funcname are all lowercase. If it is CoNoChange,Don't change. If it is CoInitCap,Funcname are Capital letter. 

Type: TCaseOption

Optional values: [CoUppercase, CoLowercase, CoNoChange, CoInitCap] 

Default value: CoInitCap

- When the value is `CoUppercase`, SQL format:

```sql
SELECT   department_id,
         MIN(salary)
FROM     employees
GROUP BY department_id
```

- When the value is `CoLowercase`, SQL format:

```sql
SELECT   department_id,
         min(salary)
FROM     employees
GROUP BY department_id
```

- When the value is `CoNoChange`, SQL format:

```sql
SELECT   department_id,
         min(salary)
FROM     employees
GROUP BY department_id
```

- When the value is `CoInitCap`, SQL format:

```sql
SELECT   department_id,
         Min(salary)
FROM     employees
GROUP BY department_id
```

#### 38.caseDatatype

```diff
- Not implemented yet
```

#### 39.wsPaddingOperatorArithmetic

**usage**: If it is false,Conditional statement letter spacing is no Padding,If it is true,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT *
FROM   information_schema.COLUMNS
WHERE  TABLE_NAME = 'crm_user'
```

- When the value is `false`, SQL format:

```sql
SELECT *
FROM   information_schema.COLUMNS
WHERE  TABLE_NAME='crm_user'
```

#### 40.wsPaddingParenthesesInFunction

**usage**: If it is true,Padding in the parentheses and between the content on either side of the Function,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `true`, SQL format:

```sql
CREATE FUNCTION sales.Fn_salesbystore ( @storeid INT
 )
RETURNS TABLE
AS
  RETURN 0;
```

- When the value is `false`, SQL format:

```sql
CREATE FUNCTION sales.Fn_salesbystore (@storeid INT
)
RETURNS TABLE
AS
  RETURN 0;
```

#### 41.wsPaddingParenthesesInExpression

**usage**: If it is false,Parentheses are not filled on either side of the expression,If it is true,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: true

- When the value is `true`, SQL format:

```sql
SELECT ( ( ( a - b ) - c ) )
FROM   t
```

- When the value is `false`, SQL format:

```sql
SELECT (((a - b) - c))
FROM   t
```

#### 42.wsPaddingParenthesesOfSubQuery

**usage**: If it is true,Padding in the parentheses on both sides of the SubQuery,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `true`, SQL format:

```sql
SELECT last_name
FROM   employees
WHERE  salary > (SELECT salary
                 FROM   employees
                 WHERE  last_name = 'Abel');
```

- When the value is `false`, SQL format:

```sql
SELECT last_name
FROM   employees
WHERE  salary > ( SELECT salary
                  FROM   employees
                  WHERE  last_name = 'Abel' );
```

#### 43.wsPaddingParenthesesInFunctionCall

**usage**: If it is true,Padding the parenthesis on both sides of the calling function,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `true`, SQL format:

```sql
SELECT   department_id,
         Min( salary )
FROM     employees
GROUP BY department_id
```

- When the value is `false`, SQL format:

```sql
SELECT   department_id,
         Min(salary)
FROM     employees
GROUP BY department_id
```

#### 44.wsPaddingParenthesesInFunctionCall

**usage**: If it is true,Padding the parenthesis on both sides of the calling function,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `true`, SQL format:

```sql
SELECT   department_id,
         Min( salary )
FROM     employees
GROUP BY department_id
```

- When the value is `false`, SQL format:

```sql
SELECT   department_id,
         Min(salary)
FROM     employees
GROUP BY department_id
```

#### 45.wsPaddingParenthesesOfTypename

**usage**: If it is true,Padding the parenthesis on both sides of the typeName,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `true`, SQL format:

```sql
CREATE TABLE datatype(fld0 GENERICTYPE,
                      fld1 CHAR( 2 ),
                      fld3 NCHAR( 1 ));
```

- When the value is `false`, SQL format:

```sql
CREATE TABLE datatype(fld0 GENERICTYPE,
                      fld1 CHAR(2),
                      fld3 NCHAR(1));
```

#### 46.cteNewlineBeforeAs

**usage**: If it is true,AS after WITH CTE will start new line,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: true

- When the value is `false`, SQL format:

```sql
WITH CTE AS (SELECT   Year(from_date) AS YEAR,
                      Sum(salary)     AS SUM
             FROM     salaries
             GROUP BY YEAR)
  SELECT q1.YEAR,
         q2.YEAR                            AS next_year,
         q1.SUM,
         q2.SUM                             AS next_sum,
         100 * ( q2.SUM - q1.SUM ) / q1.SUM AS pct
  FROM   CTE AS q1,
         CTE AS q2
  WHERE  q1.YEAR = q2.YEAR - 1;
```

- When the value is `true`, SQL format:

```sql
WITH CTE
     AS (SELECT   Year(from_date) AS YEAR,
                  Sum(salary)     AS SUM
         FROM     salaries
         GROUP BY YEAR)
  SELECT q1.YEAR,
         q2.YEAR                            AS next_year,
         q1.SUM,
         q2.SUM                             AS next_sum,
         100 * ( q2.SUM - q1.SUM ) / q1.SUM AS pct
  FROM   CTE AS q1,
         CTE AS q2
  WHERE  q1.YEAR = q2.YEAR - 1;
```

#### 47.linebreakAfterDeclare

**usage**: If it is false,after DECLARE will no start new line,If it is true,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: true

- When the value is `false`, SQL format:

```sql
DECLARE @s  VARCHAR(1000),
        @s2 VARCHAR(10)
```

- When the value is `true`, SQL format:

```sql
DECLARE
  @s  VARCHAR(1000),
  @s2 VARCHAR(10)
```

#### 48.parametersStyle

**usage**: If it is AsWrapped,the parameters will be wrapped,If it is AsStacked,The default formatting rules will be used. 

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped] 

Default value: AsStacked

- When the value is `AsStacked`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `AsWrapped`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT, @n AS INT, @test AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 49.parametersComma

**usage**: If it is LfbeforeCommaWithSpace,Each parameter will be preceded by a comma and a space,If it is LfBeforeComma,Each parameter will be preceded by a comma.If it is LfAfterComma,The default formatting rules will be used. 

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma] 

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT
                                    , @n    AS INT
                                    , @test AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `LfBeforeComma`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT
                                    ,@n     AS INT
                                    ,@test  AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 50.beStyleFunctionLeftBEOnNewline

**usage**: If it is true,All parameters are from the left will start new line,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `false`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `true`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders
(@custid AS INT,
 @n      AS INT,
 @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 51.beStyleFunctionLeftBEIndentSize

**usage**: If it is 2,All parameters are from the left will start new line and indent size is 2,If it is 0,The default formatting rules will be used. 

Type: Integer

Default value: 0

``Note: It must be used with the parameter `beStyleFunctionLeftBEOnNewline` to take effect``

- When the value is `0`, beStyleFunctionLeftBEOnNewline value is true,SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `2`, beStyleFunctionLeftBEOnNewline value is true, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders
  (@custid AS INT,
   @n      AS INT,
   @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 52.beStyleFunctionRightBEOnNewline

**usage**: If it is false,The right bracket of the argument is wrapped,If it is true,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: true

``Note: It must be used with the parameter `beStyleFunctionLeftBEOnNewline` to take effect``

- When the value is `true`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `false`,  SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 53.beStyleFunctionRightBEIndentSize

**usage**: If it is 2,The right bracket of the argument is wrapped and indent is 2,If it is 0,The default formatting rules will be used. 

Type: Integer

Default value: 0

``Note: It must be used with the parameter `beStyleFunctionLeftBEOnNewline` to take effect``

- When the value is `0`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `2`,  SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
  )
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 54.beStyleFunctionFirstParamInNewline

**usage**: If it is true,Start with the first argument in the function will start new line,If it is false,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `false`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(@custid AS INT,
                                    @n      AS INT,
                                    @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

- When the value is `true`, SQL format:

```sql
CREATE FUNCTION dbo.Fn_gettoporders(
  @custid AS INT,
  @n      AS INT,
  @test   AS CHAR
)
RETURNS TABLE
AS
  RETURN
    SELECT   TOP(@n) *
    FROM     sales.salesorderheader
    WHERE    customerid = @custid
    ORDER BY totaldue DESC
GO
```

#### 55.linebreakBeforeParamInExec

**usage**: If it is false,Each line of arguments in Exec will start new line,If it is true,The default formatting rules will be used. 

Type: boolean

Optional values: [true, false] 

Default value: true

- When the value is `false`, SQL format:

```sql
EXEC Sptrackmember @p_member_id, '2.2', @p_weeknum
```

- When the value is `true`, SQL format:

```sql
EXEC Sptrackmember
  @p_member_id,
  '2.2',
  @p_weeknum
```

#### 56.emptyLines

**usage**: If it is EloRemove,remove empty lines,If it is EloPreserve,Preserve empty lines,If it is EloMergeIntoOne,merger empty lines to one line. 

Type: TEmptyLinesOption

Optional values: [EloMergeIntoOne, EloRemove, EloPreserve] 

Default value: EloMergeIntoOne

- When the value is `EloMergeIntoOne`, SQL format:

```sql
CREATE FUNCTION dbo.Isoweek (@DATE DATETIME
)
RETURNS INT WITH EXECUTE AS caller
AS
  BEGIN
    DECLARE @ISOweek INT

    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4)) + '0104')

--Special cases: Jan 1-3 may belong to the previous year
    IF ( @ISOweek = 0 )  SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE) - 1  AS CHAR(4)) + '12' + Cast(24 + Datepart(DAY,@DATE) AS CHAR(2))) + 1

--Special case: Dec 29-31 may belong to the next year
    IF ( ( Datepart(mm,@DATE) = 12 )
         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) )  SET @ISOweek=1

    RETURN(@ISOweek)
  END;
GO
```

- When the value is `EloRemove`, SQL format:

```sql
CREATE FUNCTION dbo.Isoweek (@DATE DATETIME
)
RETURNS INT WITH EXECUTE AS caller
AS
  BEGIN
    DECLARE @ISOweek INT

    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4)) + '0104')
--Special cases: Jan 1-3 may belong to the previous year
    IF ( @ISOweek = 0 )  SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE) - 1  AS CHAR(4)) + '12' + Cast(24 + Datepart(DAY,@DATE) AS CHAR(2))) + 1
--Special case: Dec 29-31 may belong to the next year
    IF ( ( Datepart(mm,@DATE) = 12 )
         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) )  SET @ISOweek=1
    RETURN(@ISOweek)
  END;
GO
```

- When the value is `EloPreserve`, SQL format:

```sql
CREATE FUNCTION dbo.Isoweek (@DATE DATETIME
)
RETURNS INT WITH EXECUTE AS caller
AS
  BEGIN
    DECLARE @ISOweek INT

    SET @ISOweek= Datepart(wk,@DATE) + 1 - Datepart(wk,Cast(Datepart(yy,@DATE) AS CHAR(4)) + '0104')

--Special cases: Jan 1-3 may belong to the previous year
    IF ( @ISOweek = 0 )  SET @ISOweek=dbo.Isoweek(Cast(Datepart(yy,@DATE) - 1  AS CHAR(4)) + '12' + Cast(24 + Datepart(DAY,@DATE) AS CHAR(2))) + 1
--Special case: Dec 29-31 may belong to the next year
    IF ( ( Datepart(mm,@DATE) = 12 )
         AND ( ( Datepart(dd,@DATE) - Datepart(dw,@DATE) ) >= 28 ) )  SET @ISOweek=1




    RETURN(@ISOweek)
  END;
GO
```

#### 57.insertBlankLineInBatchSqls

```diff
- Not implemented yet,Setting true and false is the same
```

#### 58.noEmptyLinesBetweenMultiSetStmts

**usage**: If it is true,There are no empty rows between each row,If it is false,There are empty rows between each rowZ. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `false`, SQL format:

```sql
SET @A = @B

SET @C = @D

SET @E = @F

SET @G = @H
```

- When the value is `true`, SQL format:

```sql
SET @A = @B
SET @C = @D
SET @E = @F
SET @G = @H
```

#### 59.linenumberEnabled

**usage**: If it is true,Add the number of rows per row,If it is false,There are empty rows between each rowZ. 

Type: boolean

Optional values: [true, false] 

Default value: false

- When the value is `false`, SQL format:

```sql
SET @A = @B

SET @C = @D

SET @E = @F

SET @G = @H
```

- When the value is `true`, SQL format:

```sql
1  SET @A = @B
2
3  SET @C = @D
4
5  SET @E = @F
6
7  SET @G = @H
```

#### 60.linenumberZeroBased

**usage**: If it is true,Add the number of rows per row and count start from 0,If it is false,There are empty rows between each rowZ. 

Type: boolean

Optional values: [true, false] 

Default value: false

``Note: It must be used with the parameter `linenumberEnabled` to take effect``

- When the value is `false`, linenumberEnabled value is true, SQL format:

```sql
SET @A = @B

SET @C = @D

SET @E = @F

SET @G = @H
```

- When the value is `true`, linenumberEnabled value is true,SQL format:

```sql
0  SET @A = @B
1
2  SET @C = @D
3
4  SET @E = @F
5
6  SET @G = @H
```

#### 61.linenumberLeftMargin

**usage**: If it is 2,The number of rows to the left of each row margin is 2,If it is 0,There are empty rows between each rowZ. 

Type: Integer

Default value: 0

``Note: It must be used with the parameter `linenumberEnabled` to take effect``

- When the value is `0`, linenumberEnabled value is true, SQL format:

```sql
SET @A = @B

SET @C = @D

SET @E = @F

SET @G = @H
```

- When the value is `2`, linenumberEnabled value is true,SQL format:

```sql
  1  SET @A = @B
  2
  3  SET @C = @D
  4
  5  SET @E = @F
  6
  7  SET @G = @H
```

#### 62.linenumberRightMargin

**usage**: If it is 0,The number of rows to the right of each row margin is 0,If it is 0,There are empty rows between each rowZ. 

Type: Integer

Default value: 2

``Note: It must be used with the parameter `linenumberEnabled` to take effect``

- When the value is `2`, linenumberEnabled value is true, SQL format:

```sql
1  SET @A = @B
2
3  SET @C = @D
4
5  SET @E = @F
6
7  SET @G = @H
```

- When the value is `0`, linenumberEnabled value is true,SQL format:

```sql
1SET @A = @B
2
3SET @C = @D
4
5SET @E = @F
6
7SET @G = @H
```

#### 63.functionCallParametersStyle

**usage**: If it is AsStacked,call function parameters are stacked,If it is AsWrapped,The default formatting rules will be used. 

Type: TAlignStyle

Optional values: [AsStacked, AsWrapped] 

Default value: AsWrapped

- When the value is `AsStacked`, SQL format:

```sql
SET @a = dbo.Func1(@param1,
                   @param2,
                   @param3 + 1,
                   @param4)
```

- When the value is `AsWrapped`, SQL format:

```sql
SET @a = dbo.Func1(@param1, @param2, @param3 + 1, @param4)
```

#### 64.functionCallParametersComma

**usage**: If it is LfbeforeCommaWithSpace,the comma is left of the parameters and has a space,If it is LfBeforeComma,the comma is left of the parameters.If it is LfAfterComma,The default formatting rules will be used. 

Type: TLinefeedsCommaOption

Optional values: [LfAfterComma, LfbeforeCommaWithSpace, LfBeforeComma] 

Default value: LfAfterComma

- When the value is `LfAfterComma`, SQL format:

```sql
SET @a = dbo.Func1(@param1,
                   @param2,
                   @param3 + 1,
                   @param4)
```

- When the value is `LfbeforeCommaWithSpace`, SQL format:

```sql
SET @a = dbo.Func1(@param1
                   , @param2
                   , @param3 + 1
                   , @param4)
```

- When the value is `LfBeforeComma`, SQL format:

```sql
SET @a = dbo.Func1(@param1
                   ,@param2
                   ,@param3 + 1
                   ,@param4)
```

#### 65.removeComment

```diff
- Not implemented yet
```

#### 66.compactMode

**usage**: If it is Cpmugly,start compact mode ,If it is CpmNone,The default formatting rules will be used. 

Type: TCompactMode

Optional values: [CpmNone, Cpmugly] 

Default value: CpmNone

- When the value is `Cpmugly`, SQL format:

```sql
CREATE TABLE test( c1 VARCHAR(10) COMMENT '1', c2 VARCHAR(5) COMMENT '2', c3 VARCHAR(20) COMMENT
'3', c4 CHAR(1) COMMENT '4', c5 CHAR(1) COMMENT '5' ) ENGINE=INNODB;
```

- When the value is `CpmNone`, SQL format:

```sql
CREATE TABLE test(c1 VARCHAR(10) COMMENT '1',
                  c2 VARCHAR(5) COMMENT '2',
                  c3 VARCHAR(20) COMMENT '3',
                  c4 CHAR(1) COMMENT '4',
                  c5 CHAR(1) COMMENT '5')ENGINE=INNODB;
```

#### 67.lineWidth

**usage**: If it is 90,line width is 90,If it is 99,The default formatting rules will be used. 

Type: Integer

Default value: 99

``Note: It must be used with the parameter `compactMode` to take effect``

- When the value is `99`, compactMode value is Cpmugly,SQL format:

```sql
CREATE TABLE test( c1 VARCHAR(10) COMMENT '1', c2 VARCHAR(5) COMMENT '2', c3 VARCHAR(20) COMMENT
'3', c4 CHAR(1) COMMENT '4', c5 CHAR(1) COMMENT '5' ) ENGINE=INNODB;
```

- When the value is `90`, compactMode value is Cpmugly,SQL format:

```sql
CREATE TABLE test( c1 VARCHAR(10) COMMENT '1', c2 VARCHAR(5) COMMENT '2', c3 VARCHAR(20)
COMMENT '3', c4 CHAR(1) COMMENT '4', c5 CHAR(1) COMMENT '5' ) ENGINE=INNODB;
```

#### 68.outputFmt

**usage**: Input file format. 

Type: GOutputFmt

Optional values: [ofSql, ofCsharp, ofCsharpsbd, ofvb, ofvbsbd, ofjava, ofjavasbf, ofvc, ofpascal, ofphp, ofproc, ofprocobol, ofhtml, ofhtml2, ofhtmlkeeplayout, ofhtmlkeeplayout2, ofhtmlkeeplayoutmodifycase, ofhtmlkeeplayout2modifycase, oftxtmodifycase, ofxml, ofdbobject, ofrtf, ofrtfkeeplayout, ofrtfkeeplayoutmodifycase, ofFromCSharp, ofFromVB, ofFromVC, ofFromJava, ofFromPhp, ofFromPascal, ofUnknown] 

Default value: ofSql

