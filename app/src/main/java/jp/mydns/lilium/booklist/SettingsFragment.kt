package jp.mydns.lilium.booklist

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import jp.mydns.lilium.booklist.repository.AwsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingsFragment : PreferenceFragmentCompat(), CoroutineScope {
    private val app get() = requireContext().applicationContext as MyApplication

    private lateinit var awsAccessKey: String
    private lateinit var awsAccessKeyPref: Preference
    private lateinit var awsSecretAccessKey: String
    private lateinit var awsSecretAccessKeyPref: Preference
    private lateinit var awsS3BucketKey: String
    private lateinit var awsS3BucketKeyPref: Preference
    private lateinit var awsS3ObjectKey: String
    private lateinit var awsS3ObjectKeyPref: Preference

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val preferencesName = getString(R.string.preferences_name)
        preferenceManager.sharedPreferencesName = preferencesName
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        app.myPreferences.awsAccessKey.observe(viewLifecycleOwner) {
            awsAccessKeyPref.summary = it ?: "(null)"
        }

        awsAccessKey = getString(R.string.pref_aws_access_key)
        awsAccessKeyPref = findPreference(awsAccessKey)!!
        app.myPreferences.awsAccessKey.observe(viewLifecycleOwner) {
            awsAccessKeyPref.summary = it ?: "(null)"
        }

        awsSecretAccessKey = getString(R.string.pref_aws_secret_access_key)
        awsSecretAccessKeyPref = findPreference(awsSecretAccessKey)!!
        app.myPreferences.awsSecretAccessKey.observe(viewLifecycleOwner) {
            awsSecretAccessKeyPref.summary = it ?: "(null)"
        }

        awsS3BucketKey = getString(R.string.pref_aws_s3_bucket_key)
        awsS3BucketKeyPref = findPreference(awsS3BucketKey)!!
        app.myPreferences.awsS3BucketKey.observe(viewLifecycleOwner) {
            awsS3BucketKeyPref.summary = it ?: "(null)"
        }

        awsS3ObjectKey = getString(R.string.pref_aws_s3_object_key)
        awsS3ObjectKeyPref = findPreference(awsS3ObjectKey)!!
        app.myPreferences.awsS3ObjectKey.observe(viewLifecycleOwner) {
            awsS3ObjectKeyPref.summary = it ?: "(null)"
        }

        val fileListDateKey = getString(R.string.pref_file_list_date_key)
        val fileListDatePref = findPreference<Preference>(fileListDateKey)!!
        fileListDatePref.summary = "(loading)"
        launch {
            fileListDatePref.summary = AwsRepository.loadFileListDate(requireContext())
        }

        val versionCodeKey = getString(R.string.pref_version_code_key)
        findPreference<Preference>(versionCodeKey)!!.summary = BuildConfig.VERSION_CODE.toString()

        val versionNameKey = getString(R.string.pref_version_name_key)
        findPreference<Preference>(versionNameKey)!!.summary = BuildConfig.VERSION_NAME
    }
}