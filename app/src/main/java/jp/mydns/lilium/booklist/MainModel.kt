package jp.mydns.lilium.booklist

import android.app.Application
import androidx.lifecycle.*
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.coroutines.launch

class MainModel(application: Application) : AndroidViewModel(application) {
    private val app = application as MyApplication
    private val s3Client = MediatorLiveData<AmazonS3Client?>()
    private val _sourceItems = MediatorLiveData<List<Book>?>()
    private val _items = MediatorLiveData<List<Book>?>()
    val items: LiveData<List<Book>?> = _items
    val query = MutableLiveData("")

    init {
        val s3ClientObserver = Observer<Any?> {
            val ak = app.myPreferences.awsAccessKey.value ?: return@Observer
            val sk = app.myPreferences.awsSecretAccessKey.value ?: return@Observer

            val c = BasicAWSCredentials(ak, sk)
            val r = Region.getRegion("ap-northeast-1")
            val cc = ClientConfiguration()
            s3Client.value = AmazonS3Client(c, r, cc)
        }
        s3Client.addSource(app.myPreferences.awsAccessKey, s3ClientObserver)
        s3Client.addSource(app.myPreferences.awsSecretAccessKey, s3ClientObserver)

        val sourceListObserver = Observer<Any?> {
            val bk = app.myPreferences.awsS3BucketKey.value ?: return@Observer
            val ok = app.myPreferences.awsS3ObjectKey.value ?: return@Observer
            val c = s3Client.value ?: return@Observer

            viewModelScope.launch {
                val tmp = AwsRepository.fetchFileList(app, c, bk, ok)
                if (tmp != null) {
                    _sourceItems.value = tmp
                }
            }
        }
        _sourceItems.addSource(app.myPreferences.awsS3BucketKey, sourceListObserver)
        _sourceItems.addSource(app.myPreferences.awsS3ObjectKey, sourceListObserver)
        _sourceItems.addSource(s3Client, sourceListObserver)
        viewModelScope.launch {
            _sourceItems.value = AwsRepository.loadFileList(app)
        }

        val queriedListObserver = Observer<Any?> {
            val q = query.value
            if (q.isNullOrBlank()) {
                _items.value = emptyList()
                return@Observer
            }
            val sl = _sourceItems.value
            if (sl.isNullOrEmpty()) {
                _items.value = emptyList()
                return@Observer
            }
            _items.value = sl.filter { it.name.contains(q) }
        }
        _items.addSource(_sourceItems, queriedListObserver)
        _items.addSource(query, queriedListObserver)
    }

    override fun onCleared() {
        super.onCleared()
        _items.removeSource(_sourceItems)
        _items.removeSource(query)
        _sourceItems.removeSource(app.myPreferences.awsS3BucketKey)
        _sourceItems.removeSource(app.myPreferences.awsS3ObjectKey)
        _sourceItems.removeSource(s3Client)
        s3Client.removeSource(app.myPreferences.awsAccessKey)
        s3Client.removeSource(app.myPreferences.awsSecretAccessKey)
    }
}