package demos.sqlenv.operation;

import demos.sqlenv.constant.DbOperation;
import demos.sqlenv.operation.strategy.DbOperationStrategyFactory;

public class DbOperationFactory {

    public static <T> DbOperationService<T> getDbOperationService(String dbType, DbOperation operation) {
        return DbOperationStrategyFactory.getInstance().getDbOperationStrategy(dbType, operation);
    }

}
