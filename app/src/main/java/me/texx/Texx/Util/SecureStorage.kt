package me.texx.Texx.Util

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import com.kazakago.cryptore.CipherAlgorithm
import com.kazakago.cryptore.Cryptore
import com.madapps.prefrences.EasyPrefrences

/**
 * Class for saving data securely in SharedPreferences
 */
class SecureStorage(private val context: Context) {
    /**
     * Encrypts and saves the [value] with [key] as index
     */
    fun set(key: String, value: String) {
        sharedPrefs.putString(key, encryptAES(value))
    }

    /**
     * Finds the encrypted value by [key], decrypts it and returns the value as string
     */
    fun get(key: String): String? {
        return try {
            decryptAES(sharedPrefs.getString(key))
        } catch (e: Exception) {
            null
        }
    }

    private val sharedPrefs = EasyPrefrences(context)

    private enum class Alias(val value: String) {
        RSA("CIPHER_RSA"),
        AES("CIPHER_AES")
    }

    private val cryptoreAES: Cryptore by lazy {
        val builder = Cryptore.Builder(alias = Alias.AES.value, type = CipherAlgorithm.AES)
        builder.build()
    }

    private fun encryptAES(plainStr: String): String {
        val plainByte = plainStr.toByteArray()
        val result = cryptoreAES.encrypt(plainByte = plainByte)
        cipherIV = result.cipherIV
        return Base64.encodeToString(result.bytes, Base64.DEFAULT)
    }

    private fun decryptAES(encryptedStr: String): String {
        val encryptedByte = Base64.decode(encryptedStr, Base64.DEFAULT)
        val result = cryptoreAES.decrypt(encryptedByte = encryptedByte, cipherIV = cipherIV)
        return String(result.bytes)
    }

    private var cipherIV: ByteArray?
        get() {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
            preferences.getString("cipher_iv", null)?.let {
                return Base64.decode(it, Base64.DEFAULT)
            }
            return null
        }
        set(value) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(this.context)
            val editor = preferences.edit()
            editor.putString("cipher_iv", Base64.encodeToString(value, Base64.DEFAULT))
            editor.apply()
        }
}