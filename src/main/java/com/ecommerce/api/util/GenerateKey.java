package com.ecommerce.api.util;

import java.util.Base64;
import java.security.SecureRandom;

public class GenerateKey {
    public static void main(String[] args) {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        System.out.println(Base64.getEncoder().encodeToString(key));
    }
}