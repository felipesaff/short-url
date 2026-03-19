package com.example.short_url.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeistelCipherService {
    private final Long xorMask;

    public FeistelCipherService(@Value("${app.feistel.xorMask}") String xorMask) {
        this.xorMask = Long.parseUnsignedLong(xorMask, 16);
    }

    public long encrypt(long n) {
        long left  = (n >> 16) & 0xFFFF;
        long right = n & 0xFFFF;

        for (int i = 0; i < 3; i++) {
            long newLeft = right;
            long newRight = left ^ ((right * right + xorMask + i) % 65536);
            left  = newLeft;
            right = newRight;
        }

        return (left << 16) | right;
    }

    public long decrypt(long n) {
        long left  = (n >> 16) & 0xFFFF;
        long right = n & 0xFFFF;


        for (int i = 2; i >= 0; i--) {
            long newRight = left;
            left  = right ^ ((left * left + xorMask + i) % 65536);
            right = newRight;
        }

        return (left << 16) | right;
    }
}
