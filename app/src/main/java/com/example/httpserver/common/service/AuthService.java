package com.example.httpserver.common.service;

import com.example.httpserver.app.service.config.ServiceConfigurationRepository;
import com.example.httpserver.common.exception.BadTokenException;
import com.example.httpserver.common.exception.TokenExpiredException;
import com.example.httpserver.common.model.LoginModel;
import com.example.httpserver.common.repository.TimeBasedOneTimePassword;
import com.example.httpserver.common.repository.TotpRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import io.jsonwebtoken.*;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class AuthService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ServiceConfigurationRepository repository;
    private final TimeBasedOneTimePassword password;
    private final String algorithm = "RSA";
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private JwtParser parser;

    public AuthService(ServiceConfigurationRepository repository, TotpRepository totpRepository)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.repository = repository;
        this.password = totpRepository.getDefault();
        load();
    }

    public String[] login(LoginModel model) {
        boolean basic = Objects.equals(repository.get("basic"), "true");
        boolean totp = Objects.equals(repository.get("totp"), "true");

        if (basic) {
            String name = repository.get("username");
            String pass = repository.get("password");

            if (Objects.equals(model.password, pass) && Objects.equals(model.username, name)) {
                return new String[]{token(model), refresh(model)};
            }
        }

        if (totp) {
            if (password.pin() == Integer.parseInt(model.pin)) {
                return new String[]{token(model), refresh(model)};
            }
        }

        if (!totp && !basic) {
            return new String[]{"", ""};
        }
        throw new SecurityException("Authorization failed!");
    }

    public void verify(NanoHTTPD.IHTTPSession session) {
        boolean basic = Objects.equals(repository.get("basic"), "true");
        boolean totp = Objects.equals(repository.get("totp"), "true");
        if (!basic && !totp) {
            return;
        }

        String header = session.getHeaders().get("authorization");
        if (header == null || header.isEmpty()) {
            throw new BadTokenException();
        }
        String token = header.replace("Bearer ", "");
        verify(token);
    }

    public void verify(String token) {
        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);
            String name = claims.getBody().get("name", String.class);
            String scope = claims.getBody().get("scope", String.class);
            String id = claims.getBody().getId();
            if (!Objects.equals(scope, "session")) {
                throw new BadTokenException();
            }
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new BadTokenException();
        }
    }

    public String refresh(String token) {
        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);
            String name = claims.getBody().get("name", String.class);
            String scope = claims.getBody().get("scope", String.class);
            String id = claims.getBody().getId();
            if (!Objects.equals(scope, "refresh")) {
                throw new BadTokenException();
            }
            LoginModel model = new LoginModel();
            model.username = name;
            return refresh(model);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        } catch (Exception e) {
            throw new BadTokenException();
        }
    }

    private String token(LoginModel model) {
        return Jwts.builder()
                .setSubject(model.username)
                .claim("name", model.username)
                .claim("scope", "session")
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .setIssuedAt(new Date())
                .setIssuer("auth")
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS512)
                .compact();
    }

    private String refresh(LoginModel model) {
        return Jwts.builder()
                .setSubject(model.username)
                .claim("name", model.username)
                .claim("scope", "refresh")
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 7 * 24 * 60 * 60 * 1000))
                .setIssuedAt(new Date())
                .setIssuer("auth")
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, SignatureAlgorithm.RS512)
                .compact();
    }

    private void generate() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();

        repository.put("public-key", Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        repository.put("private-key", Base64.getEncoder().encodeToString(privateKey.getEncoded()));

        parser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
    }

    private void load() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyStr = repository.get("public-key");
        String privateKeyStr = repository.get("private-key");

        if (publicKeyStr == null || publicKeyStr.isEmpty()) {
            generate();
            return;
        }

        if (privateKeyStr == null || privateKeyStr.isEmpty()) {
            generate();
            return;
        }

        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
        KeyFactory factory = KeyFactory.getInstance(algorithm);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicBytes);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateBytes);

        privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
        publicKey = factory.generatePublic(x509EncodedKeySpec);

        parser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
    }

}
