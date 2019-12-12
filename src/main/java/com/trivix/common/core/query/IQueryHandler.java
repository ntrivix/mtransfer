package com.trivix.common.core.query;

public interface IQueryHandler<TResultType, TQuery extends IQuery<TResultType>> {
    TResultType executeQuery(TQuery query);
}
