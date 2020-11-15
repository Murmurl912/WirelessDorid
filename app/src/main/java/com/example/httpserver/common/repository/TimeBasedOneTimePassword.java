package com.example.httpserver.common.repository;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class TimeBasedOneTimePassword {

    private final Key key;
    private final TimeBasedOneTimePasswordGenerator generator;
    private final Duration duration;

    private TimeBasedOneTimePassword(TimeBasedOneTimePasswordGenerator generator,
                                     Key key,
                                     Duration duration) {
        this.key = key;
        this.generator = generator;
        this.duration = duration;
    }

    public static TimeBasedOneTimePassword from(Key key, Duration duration, int length) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(duration);
        if (length < 6 || length > 8) {
            throw new IllegalArgumentException("length must larger than 5 and less than 9");
        }
        try {
            TimeBasedOneTimePasswordGenerator generator =
                    new TimeBasedOneTimePasswordGenerator(duration, length, key.getAlgorithm());
            return new TimeBasedOneTimePassword(generator, key, duration);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static TimeBasedOneTimePassword create(Duration duration,
                                                  int length,
                                                  String algorithm,
                                                  int size) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(size);
        Key key = keyGenerator.generateKey();
        return from(key, duration, length);
    }

    public static TimeBasedOneTimePassword from(TimeBasedOneTimePasswordStore store) {
        return from(store.key, store.duration, store.length);
    }

    public int pin() {
        try {
            return generator.generateOneTimePassword(key, Instant.now());
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public Key key() {
        return key;
    }

    public TimeBasedOneTimePasswordStore store() {
        return new TimeBasedOneTimePasswordStore(key, generator.getPasswordLength(), duration);
    }

    public static class TimeBasedOneTimePasswordStore implements Externalizable {
        private Key key;
        private int length;
        private Duration duration;

        public TimeBasedOneTimePasswordStore(Key key, int length, Duration duration) {
            this.key = key;
            this.length = length;
            this.duration = duration;
        }

        public TimeBasedOneTimePasswordStore() {

        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(key);
            out.write(length);
            out.writeObject(duration);
        }

        @Override
        public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException {
            this.key = (Key) in.readObject();
            this.length = in.readInt();
            this.duration = (Duration) in.readObject();
        }
    }

}
