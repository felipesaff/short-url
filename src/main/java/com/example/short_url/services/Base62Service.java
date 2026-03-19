package com.example.short_url.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class Base62Service {
    private final FeistelCipherService feistelCipherService;
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    private final char[] shuffledAlphabet;
    private final int[] reverseMap;

    public Base62Service(
            FeistelCipherService feistelCipherService,
            @Value("${app.base62.salt}") String salt
    ) {
        this.feistelCipherService = feistelCipherService;
        this.shuffledAlphabet = fisherYatesShuffle(salt);
        this.reverseMap = buildReverseMap(this.shuffledAlphabet);
    }

    private char[] fisherYatesShuffle(String salt) {
        char[] alphabet = BASE62_ALPHABET.toCharArray();
        int[] seedSequence = generateSeedFromSalt(salt);

        for (int i = alphabet.length - 1; i > 0; i--) {
            int j = Math.abs(seedSequence[i % seedSequence.length] + i) % (i + 1);

            char temp = alphabet[i];
            alphabet[i] = alphabet[j];
            alphabet[j] = temp;
        }

        return alphabet;
    }

    private int[] generateSeedFromSalt(String salt) {
        int[] seeds = new int[salt.length()];
        for (int i = 0; i < salt.length(); i++) {
            seeds[i] = salt.charAt(i) * (i + 1);
        }
        return seeds;
    }

    private int[] buildReverseMap(char[] alphabet) {
        int[] map = new int[128];
        Arrays.fill(map, -1);
        for (int i = 0; i < alphabet.length; i++) {
            map[alphabet[i]] = i;
        }
        return map;
    }

    public String encode(long linkId) {
        if (linkId < 0) {
            throw new IllegalArgumentException("O número deve ser não-negativo: " + linkId);
        }
        if (linkId == 0) {
            return String.valueOf(shuffledAlphabet[0]);
        }

        long obfuscated = feistelCipherService.encrypt(linkId);

        if (obfuscated == 0) {
            return String.valueOf(shuffledAlphabet[0]);
        }

        StringBuilder sb = new StringBuilder();
        long n = obfuscated;

        while (n > 0) {
            int remainder = (int) (n % BASE);
            sb.append(shuffledAlphabet[remainder]);
            n /= BASE;
        }

        System.out.println("linkId: " + linkId);
        System.out.println("obfuscated: " + obfuscated);
        System.out.println("encoded: " + sb);

        return sb.reverse().toString();
    }

    public long decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            throw new IllegalArgumentException("String codificada não pode ser nula ou vazia");
        }

        long result = 0;

        for (char c : encoded.toCharArray()) {
            if (c >= 128 || reverseMap[c] == -1) {
                throw new IllegalArgumentException("Caractere inválido na string codificada: '" + c + "'");
            }
            result = result * BASE + reverseMap[c];
        }

        return feistelCipherService.decrypt(result);
    }
}