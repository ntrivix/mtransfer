package com.trivix.mtransfer.domain.account.commands.changeBalance;

public class ChangeAccountBalanceCommandResult {
    private ChangeAccountBalanceStatus status;
    private String error;

    public ChangeAccountBalanceCommandResult(ChangeAccountBalanceStatus status, String error) {
        this.status = status;
        this.error = error;
    }

    public ChangeAccountBalanceCommandResult(ChangeAccountBalanceStatus status) {
        this.status = status;
    }

    public boolean isSuccessful() {
        return status == ChangeAccountBalanceStatus.SUCCESSFUL;
    }

    public ChangeAccountBalanceStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
