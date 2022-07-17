package jp.mydns.lilium.booklist

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PreferencesModel(application: Application) : AndroidViewModel(application) {
    private val _awsAccessKey = MutableLiveData<String?>()
    private val _awsS3BucketKey = MutableLiveData<String?>()
    private val _awsS3ObjectKey = MutableLiveData<String?>()
    private val _awsSecretAccessKey = MutableLiveData<String?>()

    private val awsAccessKeyKey = application.getString(R.string.pref_aws_access_key)
    private val awsS3BucketKeyKey = application.getString(R.string.pref_aws_s3_bucket_key)
    private val awsS3ObjectKeyKey = application.getString(R.string.pref_aws_s3_object_key)
    private val awsSecretAccessKeyKey = application.getString(R.string.pref_aws_secret_access_key)
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
        val prefsName = application.getString(R.string.preferences_name)
        preferences = application.getSharedPreferences(prefsName, Application.MODE_PRIVATE)
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        _awsAccessKey.value = preferences.getString(awsAccessKeyKey, null)
        _awsS3BucketKey.value = preferences.getString(awsS3BucketKeyKey, null)
        _awsS3ObjectKey.value = preferences.getString(awsS3ObjectKeyKey, null)
        _awsSecretAccessKey.value = preferences.getString(awsSecretAccessKeyKey, null)
    }

    override fun onCleared() {
        super.onCleared()
        preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }
}