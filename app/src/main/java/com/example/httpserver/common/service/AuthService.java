package com.example.httpserver.common.service;

import com.example.httpserver.app.repository.TimeBasedOneTimePassword;
import com.example.httpserver.app.repository.TotpRepository;
import com.example.httpserver.common.model.LoginModel;
import com.example.httpserver.common.repository.ServiceConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import io.jsonwebtoken.*;

import javax.crypto.KeyGenerator;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

public class AuthService {
    private final ObjectMapper mapper = new ObjectMapper();
    private ServiceConfigRepository repository;
    private TimeBasedOneTimePassword password;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String algorithm = "RSA";
    private JwtParser parser;

    public AuthService(ServiceConfigRepository repository, TotpRepository totpRepository) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.repository = repository;
        this.password = totpRepository.getDefault();
        load();
    }

    public String login(String data) throws JsonProcessingException {
        LoginModel model = mapper.readValue(data, LoginModel.class);
        String name = repository.get("username");
        String pass = repository.get("password");
        if(Objects.equals(model.password, pass) && Objects.equals(model.username, name)) {
            return mapper.writeValueAsString(new String[]{token(model), refresh(model)});
        }
        if(password.pin() == Integer.parseInt(model.pin)) {
            return mapper.writeValueAsString(new String[]{token(model), refresh(model)});
        }
        throw new SecurityException("Authorization failed!");
    }

    public boolean verify(NanoHTTPD.IHTTPSession session) {
        String header = session.getHeaders().get("authorization");
        if(header == null || header.isEmpty()) {
            return false;
        }
        String token = header.replace("Bearer ", "");
        return verify(token);
    }

    public boolean verify(String token) {

        Jws<Claims> claims = parser.parseClaimsJws(token);
        String name = claims.getBody().get("name", String.class);
        String scope = claims.getBody().get("session", String.class);
        String id = claims.getBody().getId();
        return true;
    }

    public String token(LoginModel model) {
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

    public String refresh(LoginModel model) {
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
        repository.put("private-key", Base64.getEncoder().encodeToString(publicKey.getEncoded()));

         parser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
    }

    private void load() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyStr = repository.get("public-key");
        String privateKeyStr = repository.get("private-key");

        if(publicKeyStr == null || publicKeyStr.isEmpty()) {
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
