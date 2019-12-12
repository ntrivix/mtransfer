package com.trivix.common.core.command;

public interface ICommandHandler<TResultType, TCommand extends ICommand<TResultType>> {
    TResultType executeCommand(TCommand inputType);
}
