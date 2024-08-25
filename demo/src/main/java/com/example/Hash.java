package com.example;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Hash {
    private String hash;
    private byte[] salt;

    public void genSalt(byte[] salt){
        if(salt != null)
            this.salt = salt;
        else
            this.salt = generateSalt();
    }
    public void doHash(String pass){
        this.hash = Base64.getEncoder().encodeToString(generateArgon2id(pass));
    }
    public String getHash(){
        return hash;
    }
    public byte[] getSalt(){
        return salt;
    }
    private static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }
    private byte[] generateArgon2id(String password) {
        int opsLimit = 3;
        int memLimit = 262144;
        int outputLength = 32;
        int parallelism = 1;
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13) // 19
                .withIterations(opsLimit)
                .withMemoryAsKB(memLimit)
                .withParallelism(parallelism)
                .withSalt(this.salt);
        Argon2BytesGenerator gen = new Argon2BytesGenerator();
        gen.init(builder.build());
        byte[] result = new byte[outputLength];
        gen.generateBytes(password.getBytes(StandardCharsets.UTF_8), result, 0, result.length);
        return result;
    }
}
