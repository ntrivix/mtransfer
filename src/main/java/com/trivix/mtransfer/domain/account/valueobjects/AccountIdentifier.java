package com.trivix.mtransfer.domain.account.valueobjects;

import java.util.UUID;

public class AccountIdentifier implements IAccountIdentifier {
    private UUID accountId;
    
    public AccountIdentifier(String uuid) {
        if (uuid == null || uuid.isEmpty())
            throw new IllegalArgumentException("Account identifier must be defined");
        accountId = UUID.fromString(uuid);
    }

    public AccountIdentifier(UUID accountId) {
        this.accountId = accountId;
    }

    public AccountIdentifier() {
        this.accountId = UUID.randomUUID();
    }

    @Override
    public UUID getUUID() {
        return accountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IAccountIdentifier accountId1 = (IAccountIdentifier) o;
        return getUUID().equals(accountId1.getUUID());
    }

    @Override
    public int hashCode() {
        return getUUID().hashCode();
    }

    @Override
    public String toString() {
        return "acc-" + accountId;
    }
}
