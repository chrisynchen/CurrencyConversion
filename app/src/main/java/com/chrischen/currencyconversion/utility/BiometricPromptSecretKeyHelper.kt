/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrischen.currencyconversion.utility

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * source from https://android.googlesource.com/platform/frameworks/support/+/androidx-master-dev/samples/BiometricDemos/src/main/java/com/example/android/biometric/BiometricPromptSecretKeyHelper.java
 */
@RequiresApi(api = Build.VERSION_CODES.M)
internal object BiometricPromptSecretKeyHelper {

    /**
     * Generates a key that requires the user to authenticate with a biometric before each use.
     */
    @Throws(
        InvalidAlgorithmParameterException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class
    )
    fun generateBiometricBoundKey(
        keyName: String,
        invalidatedByBiometricEnrollment: Boolean
    ) {
        generateKey(keyName, true, invalidatedByBiometricEnrollment, -1)
    }

    /**
     * Generates a key that can only be used if the user is authenticated via secure lock screen or
     * {@link androidx.biometric.BiometricPrompt.PromptInfo.Builder#setDeviceCredentialAllowed(
     *boolean)}
     */
    @Throws(
        InvalidAlgorithmParameterException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class
    )
    fun generateCredentialBoundKey(keyName: String, validityDuration: Int) {
        generateKey(keyName, false, false, validityDuration)
    }

    @Throws(
        NoSuchProviderException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun generateKey(
        keyName: String, biometricBound: Boolean,
        invalidatedByBiometricEnrollment: Boolean, validityDuration: Int
    ) {
        val builder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true)
        if (biometricBound) {
            // Require the user to authenticate for every use of the key. This is the default, i.e.
            // userAuthenticationValidityDurationSeconds is -1 unless specified otherwise.
            // Explicitly setting it to -1 here for the sake of example.
            // For this to work, at least one biometric must be enrolled.
            builder.setUserAuthenticationValidityDurationSeconds(-1)
            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level 24+.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // it isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Invalidate the keys if a new biometric has been enrolled.
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment)
            }
        } else {
            // Sets the duration for which the key can be used after the last user authentication.
            // For this to work, authentication must happen either via secure lock screen or the
            // ConfirmDeviceCredential flow, which can be done by creating BiometricPrompt with
            // BiometricPrompt.PromptInfo.Builder#setDeviceCredentialAllowed(true)
            builder.setUserAuthenticationValidityDurationSeconds(validityDuration)
        }
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(builder.build())
        // Generates and stores the key in Android KeyStore under the keystoreAlias (keyName)
        // specified in the builder.
        keyGenerator.generateKey()
    }

    @Throws(
        KeyStoreException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        IOException::class,
        UnrecoverableKeyException::class
    )
    fun getSecretKey(keyName: String): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(keyName, null) as SecretKey
    }

    @Throws(
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class
    )
    fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}