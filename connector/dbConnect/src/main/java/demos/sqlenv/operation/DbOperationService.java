package demos.sqlenv.operation;

import demos.sqlenv.TSQLDataSource;

public interface DbOperationService<T> {

    T operate(TSQLDataSource datasource);

}