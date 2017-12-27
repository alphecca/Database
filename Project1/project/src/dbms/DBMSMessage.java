package dbms;

public class DBMSMessage {

	//1-2 DDL Error Message
	static final String CreateTableSuccess	= "'%s' table is created";
	static final String DuplicateColumnDefError = "Create table has failed: column definition is duplicated";
	static final String DuplicatePrimaryKeyDefError	= "Create table has failed: primary key definition is duplicated";
	static final String ReferenceTypeError = "Create table has failed: foreign key references wrong type";
	static final String ReferenceNonPrimaryKeyError	= "Create table has failed: foreign key references non primary key column";
	static final String ReferenceColumnExistenceError = "Create table has failed: foreign key references non existing column";
	static final String ReferenceTableExistenceError = "Create table has failed: foreign key references non existing table";
	static final String NonExistingColumnDefError = "Create table has failed: '%s' does not exists in column definition";
	static final String TableExistenceError	 = "Create table has failed: table with the same name already exists";
		
	static final String DropSuccess = "'%s' table is dropped";
	static final String DropReferencedTableError = "Drop table has failed: '%s' is referenced by other table";
		
	static final String ShowTablesNoTable = "There is no table";
		
	static final String NoSuchTable = "No such table";
		
	static final String CharLengthError	 = "Char length should be over 0";
	//myError(1-2)
	static final String MultipleReference = "Create table has failed: this column already references other column";
	static final String NoSuchColumn = "no such column exists";
	static final String ReferenceColumnItself = "cannot refer to its own table";
	
	//1-3 DML Error Message
	static final String InsertResult = "The row is inserted";
	static final String InsertDuplicatePrimaryKeyError = "Insertion has failed: Primary key duplication";
	static final String InsertReferentialIntegrityError = "Insertion has failed: Referential integrity violation";
	static final String InsertTypeMismatchError = "Insertion has failed: Types are not matched";
	static final String InsertColumnExistenceError = "Insertion has failed: %s does not exist";//#colName
	static final String InsertColumnNonNullableError = "Insertion has failed: %s is not nullable";//#colName
	static final String DeleteResult ="%d row(s) are deleted";//[#count]
	static final String DeleteReferentialIntegrityPassed = "%d row(s) are not deleted due to referential integrity";//[#count]
	static final String SelectTableExistenceError = "Selection has failed: %s does not exist";//[#tableName]
	static final String SelectColumnResolveError = "Selection has failed: fail to resolve %s";//[#colName]
	static final String WhereIncomparableError ="Where clause try to compare incomparable values";
	static final String WhereTableNotSpecified = "Where clause try to reference tables which are not specified";
	static final String WhereColumnNotExist = "Where clause try to reference non existing column";
	static final String WhereAmbiguousReference = "Where clause contains ambiguous reference";

	//myError(1-3)
	
}
