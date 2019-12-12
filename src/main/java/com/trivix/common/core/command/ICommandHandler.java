package com.trivix.common.core.command;

/**
 * Interface responsible for execution commands.
 * @param <TResultType>
 * @param <TCommand>
 */
public interface ICommandHandler<TResultType, TCommand extends ICommand<TResultType>> {
    TResultType executeCommand(TCommand inputType);
}
