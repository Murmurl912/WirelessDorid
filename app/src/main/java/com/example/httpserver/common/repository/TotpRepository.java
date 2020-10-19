package com.example.httpserver.common.repository;


import java.security.NoSuchAlgorithmException;
import java.time.Duration;

public class TotpRepository {
    private final static TotpRepository repository;
    private TimeBasedOneTimePassword defaultPassword;

    static {
        repository = new TotpRepository();
    }

    private TotpRepository() {
        if(repository != null) {
            throw new IllegalStateException();
        }
    }

    public static TotpRepository instance() {
        return repository;
    }

    public synchronized TimeBasedOneTimePassword getDefault() throws NoSuchAlgorithmException {
        if(defaultPassword == null) {
            defaultPassword = TimeBasedOneTimePassword.create(Duration.ofSeconds(30), 6, "HmacSHA256", 512);
        }
        return defaultPassword;
    }

}
