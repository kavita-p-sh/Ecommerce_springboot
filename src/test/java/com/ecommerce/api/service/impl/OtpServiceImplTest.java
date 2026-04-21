package com.ecommerce.api.service.impl;


import com.ecommerce.api.exception.*;
import com.ecommerce.api.service.OtpGenerator;
import com.ecommerce.api.util.CacheConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test class for  OtpServiceImpl.
 * This class tests OTP generation, verification, and security-related scenarios
 * such as IP blocking, rate limiting, and OTP expiration using Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class OtpServiceImplTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private OtpGenerator otpGenerator;

    @InjectMocks
    private OtpServiceImpl otpService;

    private final String uniqueKey = "test135@gmail.com";
    private final String ipAddress = "192.168.1.10";


    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * Test case: OTP should be generated successfully.
     */
    @Test
    void shouldGenerateOtp(){
        String otpKey = CacheConstant.OTP_PREFIX + uniqueKey;
        String ipCountKey = CacheConstant.OTP_COUNT_PREFIX + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(ipCountKey)).thenReturn(1L);
        when(otpGenerator.generateOtp()).thenReturn("123456");
        when(valueOperations.setIfAbsent(
                eq(otpKey),
                eq("123456"),
                eq(CacheConstant.OTP_TTL_MINUTES),
                eq(TimeUnit.MINUTES)
        )).thenReturn(true);

        String result = otpService.generateOtp(uniqueKey, ipAddress);

        assertEquals(CacheConstant.OTP_SENT, result);
        verify(otpGenerator).generateOtp();
        verify(valueOperations).setIfAbsent(
                otpKey,
                "123456",
                CacheConstant.OTP_TTL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    /**
     * Test case: Exception should be thrown if OTP already exists.
     *
     */
    @Test
    void shouldThrowOtpAlreadySentExceptionWhenOtpAlreadyExists() {
        String otpKey = CacheConstant.OTP_PREFIX + uniqueKey;
        String ipCountKey = CacheConstant.OTP_COUNT_PREFIX + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(ipCountKey)).thenReturn(1L);
        when(otpGenerator.generateOtp()).thenReturn("123456");
        when(valueOperations.setIfAbsent(
                eq(otpKey),
                eq("123456"),
                eq(CacheConstant.OTP_TTL_MINUTES),
                eq(TimeUnit.MINUTES)
        )).thenReturn(false);
        when(redisTemplate.getExpire(otpKey, TimeUnit.SECONDS)).thenReturn(100L);

        OtpAlreadySentException ex = assertThrows(
                OtpAlreadySentException.class,
                () -> otpService.generateOtp(uniqueKey, ipAddress)
        );

        assertEquals(CacheConstant.OTP_ALREADY_SENT, ex.getMessage());
    }

    /**
     * Test case: IP should be blocked from generating OTP.
     */
    @Test
    void shouldThrowIpBlockedExceptionWhenIpIsBlockedDuringGenerate() {
        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress))
                .thenReturn("BLOCKED");

        assertThrows(IpBlockedException.class,
                () -> otpService.generateOtp(uniqueKey, ipAddress));
    }

    /**
     * Test case: OTP request limit exceeded for an IP.
     */
    @Test
    void shouldThrowTooManyRequestsExceptionWhenOtpRequestLimitExceeded() {
        String ipCountKey = CacheConstant.OTP_COUNT_PREFIX + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(ipCountKey))
                .thenReturn((long) CacheConstant.MAX_OTP_REQUEST_PER_IP + 1);

        assertThrows(TooManyRequestsException.class,
                () -> otpService.generateOtp(uniqueKey, ipAddress));

        verify(valueOperations).set(
                eq(CacheConstant.IP_BLOCK_PREFIX + ipAddress),
                eq(CacheConstant.BLOCKED_VALUE),
                eq(CacheConstant.IP_BLOCK_TTL_MINUTES),
                eq(TimeUnit.MINUTES)
        );
    }

    /**
     * Test case: OTP verification should succeed when OTP is correct.
     */
    @Test
    void shouldReturnTrueWhenOtpIsValid() {
        String otpKey = CacheConstant.OTP_PREFIX + uniqueKey;
        String attemptKey = CacheConstant.OTP_ATTEMPT_PREFIX + uniqueKey;
        String verifyCountKey = "VERIFY_COUNT:" + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(verifyCountKey)).thenReturn(1L);
        when(valueOperations.get(otpKey)).thenReturn("123456");
        when(valueOperations.increment(attemptKey)).thenReturn(1L);

        boolean result = otpService.verifyOtp(uniqueKey, "123456", ipAddress);

        assertTrue(result);
        verify(redisTemplate).delete(otpKey);
        verify(redisTemplate).delete(attemptKey);
    }

    /**
     * Test case: OTP verification should fail when OTP is incorrect.
     */
    @Test
    void shouldReturnFalseWhenOtpIsInvalid() {
        String otpKey = CacheConstant.OTP_PREFIX + uniqueKey;
        String attemptKey = CacheConstant.OTP_ATTEMPT_PREFIX + uniqueKey;
        String verifyCountKey = "VERIFY_COUNT:" + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(verifyCountKey)).thenReturn(1L);
        when(valueOperations.get(otpKey)).thenReturn("123456");
        when(valueOperations.increment(attemptKey)).thenReturn(1L);

        boolean result = otpService.verifyOtp(uniqueKey, "999999", ipAddress);

        assertFalse(result);
        verify(redisTemplate, never()).delete(otpKey);
    }

    /**
     * Test case: Exception should be thrown when OTP is expired.
     */
    @Test
    void shouldThrowOtpExpiredExceptionWhenCachedOtpNotFound() {
        String otpKey = CacheConstant.OTP_PREFIX + uniqueKey;
        String verifyCountKey = "VERIFY_COUNT:" + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(verifyCountKey)).thenReturn(1L);
        when(valueOperations.get(otpKey)).thenReturn(null);

        assertThrows(OtpExpiredException.class,
                () -> otpService.verifyOtp(uniqueKey, "123456", ipAddress));
    }
    /**
     * Test case: Verification attempts exceed allowed limit.
     */
    @Test
    void WhenAttemptsExceedLimit() {
        String otpKey = CacheConstant.OTP_PREFIX + uniqueKey;
        String attemptKey = CacheConstant.OTP_ATTEMPT_PREFIX + uniqueKey;
        String verifyCountKey = "VERIFY_COUNT:" + ipAddress;

        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress)).thenReturn(null);
        when(valueOperations.increment(verifyCountKey)).thenReturn(1L);
        when(valueOperations.get(otpKey)).thenReturn("123456");
        when(valueOperations.increment(attemptKey))
                .thenReturn((long) CacheConstant.MAX_VERIFY_ATTEMPTS + 1);

        assertThrows(VerificationLimitExceededException.class,
                () -> otpService.verifyOtp(uniqueKey, "123456", ipAddress));

        verify(redisTemplate).delete(otpKey);
    }


    /**
     * Test case: IP is blocked during OTP verification.
     */
    @Test
    void shouldThrowIpBlockedExceptionVerify() {
        when(valueOperations.get(CacheConstant.IP_BLOCK_PREFIX + ipAddress))
                .thenReturn("BLOCKED");

        assertThrows(IpBlockedException.class,
                () -> otpService.verifyOtp(uniqueKey, "123456", ipAddress));
    }


    /**
     * Test case: Should return cached OTP if present.
     */
    @Test
    void shouldReturnCachedOtpWhenOtpExists() {
        when(valueOperations.get(CacheConstant.OTP_PREFIX + uniqueKey)).thenReturn("123456");

        String result = otpService.getCachedOtp(uniqueKey);

        assertEquals("123456", result);
    }

    /**
     * Test case: Should return null if no OTP is present in cache.
     */
    @Test
    void shouldReturnNullWhenCachedOtpDoesNotExist() {
        when(valueOperations.get(CacheConstant.OTP_PREFIX + uniqueKey)).thenReturn(null);

        String result = otpService.getCachedOtp(uniqueKey);

        assertNull(result);
    }




}







