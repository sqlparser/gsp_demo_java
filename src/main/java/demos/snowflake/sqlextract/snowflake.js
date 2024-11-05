function snowflake() {
};

function Statement() {
};

function ResultSet() {
	this.offset = 0;
};

function SfDate() {
};

snowflake.createStatement = function(sql_command_object) {
	var extractor = Java.type('demos.snowflake.sqlextract.SnowflakeSQLExtractor');
	var result =extractor.printSQL(sql_command_object["sqlText"]);
	return new Statement();
};

snowflake.execute = function(command) {
	var extractor = Java.type('demos.snowflake.sqlextract.SnowflakeSQLExtractor');
	var result =extractor.printSQL(command["sqlText"]);
	return new ResultSet();
}

Statement.prototype = {
	execute : function() {
		return new ResultSet();
	},

	getQueryId : function() {
		return "pseudo";
	},

	getSqlText : function() {
		return "pseudo";
	},

	getColumnCount : function() {
		return 100;
	},

	getRowCount : function() {
		return 100;
	},

	getColumnSqlType : function(colIdxOrColName) {
		return new SfDate();
	},

	getColumnName : function(colIdx) {
		return "pseudo";
	},

	getColumnScale : function(colIdx) {
		return 0;
	},

	isColumnNullable : function(colIdx) {
		return true;
	},

	isColumnText : function(colIdx) {
		return true;
	},

	isColumnArray : function(colIdx) {
		return true;
	},

	isColumnBinary : function(colIdx) {
		return true;
	},

	isColumnBoolean : function(colIdx) {
		return true;
	},

	isColumnDate : function(colIdx) {
		return true;
	},

	isColumnNumber : function(colIdx) {
		return true;
	},

	isColumnObject : function(colIdx) {
		return true;
	},

	isColumnTime : function(colIdx) {
		return true;
	},

	isColumnTimestamp : function(colIdx) {
		return true;
	},

	isColumnVariant : function(colIdx) {
		return true;
	}
}

ResultSet.prototype = {
	next : function(colIdx) {
		this.offset++;
		if (this.offset > 100)
			return false;
		return true;
	},

	getColumnSqlType : function(colIdxOrColName) {
		return "pseudo";
	},

	getColumnValue : function(colIdxOrColName) {
		return new SfDate();
	},

	getColumnValueAsString : function(colIdxOrColName) {
		return "pseudo";
	},

	getQueryId : function(colIdxOrColName) {
		return "pseudo";
	}
}

SfDate.prototype = {
	getEpochSeconds : function() {
		return 0;
	},

	getNanoSeconds : function() {
		return 0;
	},

	getScale : function() {
		return 0;
	},

	getTimezone : function() {
		return 0;
	},

	toString : function() {
		return "pseudo";
	}
}
