package com.trivix.common.core.command;

public class CommandExecutionFailedException extends RuntimeException {
    private ICommand command;

    public CommandExecutionFailedException(ICommand command, String message) {
        super(message);
        this.command = command;
    }

    public CommandExecutionFailedException(ICommand command, String message, Throwable cause) {
        super(message, cause);
        this.command = command;
    }

    public CommandExecutionFailedException(ICommand command, Throwable cause) {
        super(cause);
        this.command = command;
    }

    public ICommand getCommand() {
        return command;
    }
}
