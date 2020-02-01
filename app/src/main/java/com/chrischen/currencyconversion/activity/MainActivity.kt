package com.chrischen.currencyconversion.activity

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.dagger.DaggerMainComponent
import com.chrischen.currencyconversion.databinding.ActivityMainBinding
import com.chrischen.currencyconversion.dialog.CurrencySelectionDialog
import com.chrischen.currencyconversion.storage.CurrencyPreference
import com.chrischen.currencyconversion.utility.BiometricPromptSecretKeyHelper
import com.chrischen.currencyconversion.utility.BiometricUtils
import com.chrischen.currencyconversion.viewholder.TopHolder
import com.chrischen.currencyconversion.viewmodel.MainViewModel
import com.chrischen.currencyconversion.widget.GridItemDecoration
import java.io.IOException
import java.nio.charset.Charset
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject


class MainActivity : AppCompatActivity(), TopHolder.Listener {

    companion object {
        private const val SPAN_TOP = 3
        private const val SPAN_ITEM = 1
        private const val SPAN_COUNT = SPAN_TOP
        private const val GRID_ITEM_MARGIN = 12
        private const val KEY_NAME = "demo_key"
        private const val KEY_TOKEN = "demo_token"
    }

    private val TAG = MainActivity::class.java.simpleName

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter(this)

    private lateinit var biometricPrompt: BiometricPrompt

    private var encryptedBytes: ByteArray? = null

    private var shouldDecrypt = false

    private var encryptingCipher: Cipher? = null

    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = DaggerMainComponent.builder()
            .currencyPreference(CurrencyPreference)
            .build()
        component.inject(this)
        initViews()
    }

    private fun initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel
            recyclerView.let {
                it.layoutManager =
                    GridLayoutManager(this@MainActivity, SPAN_TOP).also { layoutManager ->
                        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return when (adapter.getItemViewType(position)) {
                                    MainAdapter.Item.TYPE_TOP -> SPAN_TOP
                                    else -> SPAN_ITEM
                                }
                            }
                        }
                    }
                it.addItemDecoration(GridItemDecoration(SPAN_COUNT, GRID_ITEM_MARGIN))
                it.adapter = adapter
            }
        }

        viewModel.currencyItems.observe(this, Observer {
            adapter.setItems(it)
        })

        viewModel.logMessage.observe(this, Observer {
            Log.d(it.first, it.second)
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.dialog_biometric_title))
            .setSubtitle(getString(R.string.dialog_biometric_subtitle))
            .setNegativeButtonText(getString(R.string.dialog_biometric_negative_button))
            .build()

        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, authenticationCallback)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && BiometricUtils.isAvailable(this)) {
            BiometricPromptSecretKeyHelper.generateBiometricBoundKey(
                KEY_NAME,
                true
            )
        }
    }

    private val currencySelectionListener = object : CurrencySelectionDialog.Listener {
        override fun onCurrencyClick(currency: String?) {
            viewModel.changeCurrency(currency)
        }
    }

    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(
            errorCode: Int,
            errString: CharSequence
        ) {
            super.onAuthenticationError(errorCode, errString)
            Toast.makeText(
                applicationContext,
                "Authentication error: $errString", Toast.LENGTH_SHORT
            )
                .show()
        }

        override fun onAuthenticationSucceeded(
            result: BiometricPrompt.AuthenticationResult
        ) {
            super.onAuthenticationSucceeded(result)
            val cryptoObject = result.cryptoObject ?: return

            try {
                if (shouldDecrypt) {
                    val byteArray = cryptoObject.cipher?.doFinal(encryptedBytes)
                    Log.d(TAG, byteArray?.toString(Charset.defaultCharset()))
                } else {
                    // Save the cipher to use for decryption.
                    encryptingCipher = cryptoObject.cipher
                    encryptedBytes = encryptingCipher?.doFinal(
                        KEY_TOKEN.toByteArray(Charset.defaultCharset())
                    )
                    Log.d(TAG, KEY_TOKEN)
                    Log.d(TAG, Arrays.toString(encryptedBytes))
                }
            } catch (e: BadPaddingException) {
                Log.e(TAG, e.toString())
            } catch (e: IllegalBlockSizeException) {
                Log.e(TAG, e.toString())
            }

            Toast.makeText(
                applicationContext,
                R.string.authentication_succeeded, Toast.LENGTH_SHORT
            )
                .show()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Toast.makeText(
                applicationContext, R.string.authentication_failed,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onEncryption() {
        if (BiometricUtils.isAvailable(this)) {
            authenticateWithEncryption()
        }
    }

    override fun onDecryption() {
        if (BiometricUtils.isAvailable(this)) {
            authenticateWithDecryption()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onRefresh()
    }

    override fun onPause() {
        biometricPrompt.cancelAuthentication()
        super.onPause()
    }

    override fun onAmountChanged(amountText: CharSequence?) {
        viewModel.changeAmount(amountText)
    }

    override fun showCurrencyListDialog(currencyList: List<String>?) {
        currencyList?.let {
            CurrencySelectionDialog(this, currencySelectionListener, it)
                .setOnClickOutsideDismiss(true)
                .show()
        }
    }

    private fun authenticateWithEncryption() {
        val cipher = getCryptoCipher()
        val secretKey = getSecretKey()
        if (cipher == null || secretKey == null) {
            return
        }
        try {
            cipher!!.init(Cipher.ENCRYPT_MODE, secretKey)
            biometricPrompt.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(cipher)
            )
            Log.d(TAG, "Started authentication with a crypto object")
            authenticateWithCrypto(cipher)
            shouldDecrypt = false
        } catch (e: InvalidKeyException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun authenticateWithDecryption() {
        val cipher = getCryptoCipher()
        val secretKey = getSecretKey()
        if (cipher == null || secretKey == null) {
            return
        }

        if (encryptingCipher == null) {
            Log.d(TAG, "User must first encrypt a message");
            return
        }

        try {
            cipher.init(
                Cipher.DECRYPT_MODE, secretKey,
                IvParameterSpec(encryptingCipher!!.iv)
            )
            biometricPrompt.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(cipher)
            )
            Log.d(TAG, "Started authentication with a crypto object");
            authenticateWithCrypto(cipher)
            shouldDecrypt = true
        } catch (e: InvalidKeyException) {
            Log.e(TAG, e.toString())
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(TAG, e.toString())
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getCryptoCipher(): Cipher? {
        var cipher: Cipher? = null
        try {
            cipher = BiometricPromptSecretKeyHelper.getCipher()
        } catch (e: NoSuchPaddingException) {
            Log.e("Failed to get cipher", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Failed to get cipher", e.toString())
        }

        return cipher
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun getSecretKey(): SecretKey? {
        var secretKey: SecretKey? = null
        try {
            secretKey = BiometricPromptSecretKeyHelper.getSecretKey(KEY_NAME)
        } catch (e: KeyStoreException) {
            Log.e(TAG, e.toString())
        } catch (e: CertificateException) {
            Log.e(TAG, e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, e.toString())
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        } catch (e: UnrecoverableKeyException) {
            Log.e(TAG, e.toString())
        }

        return secretKey
    }

    private fun authenticateWithCrypto(cipher: Cipher) {
        biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }
}
