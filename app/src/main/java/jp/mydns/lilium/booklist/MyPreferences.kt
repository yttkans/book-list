package jp.mydns.lilium.booklist

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MyPreferences(context: Context) {
    private val _awsAccessKey = MutableLiveData<String?>()
    private val _awsS3BucketKey = MutableLiveData<String?>()
    private val _awsS3ObjectKey = MutableLiveData<String?>()
    private val _awsSecretAccessKey = MutableLiveData<String?>()

    private val awsAccessKeyKey = context.getString(R.string.pref_aws_access_key)
    private val awsS3BucketKeyKey = context.getString(R.string.pref_aws_s3_bucket_key)
    private val awsS3ObjectKeyKey = context.getString(R.string.pref_aws_s3_object_key)
    private val awsSecretAccessKeyKey = context.getString(R.string.pref_aws_secret_access_key)
    private val preferences: SharedPreferences

    val awsAccessKey: LiveData<String?> = _awsAccessKey
    val awsS3BucketKey: LiveData<String?> = _awsS3BucketKey
    val awsS3ObjectKey: LiveData<String?> = _awsS3ObjectKey
    val awsSecretAccessKey: LiveData<String?> = _awsSecretAccessKey

    private val onSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            when (key) {
                awsAccessKeyKey -> _awsAccessKey
                awsSecretAccessKeyKey -> _awsSecretAccessKey
                awsS3BucketKeyKey -> _awsS3BucketKey
                awsS3ObjectKeyKey -> _awsS3ObjectKey
                else -> null
            }?.let { it.value = prefs.getString(key, null) }
        }

    init {
        val prefsName = context.getString(R.string.preferences_name)
        preferences = context.getSharedPreferences(prefsName, Application.MODE_PRIVATE)
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        _awsAccessKey.value = preferences.getString(awsAccessKeyKey, null)
        _awsS3BucketKey.value = preferences.getString(awsS3BucketKeyKey, null)
        _awsS3ObjectKey.value = preferences.getString(awsS3ObjectKeyKey, null)
        _awsSecretAccessKey.value = preferences.getString(awsSecretAccessKeyKey, null)
    }
}