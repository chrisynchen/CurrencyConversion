package com.chrischen.currencyconversion.utility

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat


object BiometricUtils {

    private val TAG = BiometricUtils::class.java.simpleName

    /**
     * if not apply the dependency androidx.biometric:biometric:1.0.1
     * we will need the below methods.
     * Reference:
     * https://proandroiddev.com/5-steps-to-implement-biometric-authentication-in-android-dbeb825aeee8
     */
    private fun isBiometricPromptEnabled(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
    }

    /*
     * fingerprint authentication is only supported
     * above Android 6.0.
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private fun isPermissionGranted(context: Context): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.USE_FINGERPRINT
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isHardwareSupported(context: Context): Boolean {
        return FingerprintManagerCompat.from(context).isHardwareDetected
    }

    private fun isFingerprintAvailable(context: Context): Boolean {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
    }

    fun isAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticateType = biometricManager.canAuthenticate()
        when (authenticateType) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d(TAG, "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e(TAG, "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e(TAG, "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Log.e(
                    TAG, "The user hasn't associated " +
                            "any biometric credentials with their account."
                )
        }

        return authenticateType == BiometricManager.BIOMETRIC_SUCCESS
    }
}