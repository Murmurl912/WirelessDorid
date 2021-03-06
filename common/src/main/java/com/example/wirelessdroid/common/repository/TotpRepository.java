package com.example.wirelessdroid.common.repository;


import java.security.NoSuchAlgorithmException;
import java.time.Duration;

public class TotpRepository {
    private final static TotpRepository repository;

    static {
        repository = new TotpRepository();
    }

    private TimeBasedOneTimePassword defaultPassword;

    private TotpRepository() {
        if (repository != null) {
            throw new IllegalStateException();
        }
    }

    public static TotpRepository instance() {
        return repository;
    }

    public synchronized TimeBasedOneTimePassword getDefault() throws NoSuchAlgorithmException {
        if (defaultPassword == null) {
            defaultPassword = TimeBasedOneTimePassword.create(Duration.ofSeconds(30), 6, "HmacSHA256", 512);
        }
        return defaultPassword;
    }

}
