package jp.mydns.lilium.booklist.repository

import android.content.Context
import com.amazonaws.services.s3.AmazonS3Client
import jp.mydns.lilium.booklist.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.zip.GZIPInputStream

class AwsRepository {
    companion object {
        @Suppress("BlockingMethodInNonBlockingContext")
        suspend fun fetchFileList(
            context: Context,
            client: AmazonS3Client,
            bucketKey: String,
            objectKey: String,
        ): List<Book>? = withContext(Dispatchers.IO) {
            try {
                val metaFile = File(context.cacheDir, "meta.json")
                val listFile = File(context.cacheDir, "list.json")

                if (metaFile.exists()) {
                    val remoteMeta = client.getObjectMetadata(bucketKey, objectKey)
                    val localMeta = JSONObject(metaFile.readText())
                    if (remoteMeta.lastModified.time == localMeta.getLong("lastModified")) {
                        val arr = JSONArray(listFile.readText())
                        return@withContext (0 until arr.length()).map {
                            Book.of(arr.getJSONObject(it))
                        }
                    }
                }

                val remoteObj = client.getObject(bucketKey, objectKey)
                val remoteText = remoteObj.objectContent.use {
                    GZIPInputStream(it).bufferedReader().readText()
                }
                listFile.writeText(remoteText)

                val localMeta = JSONObject()
                localMeta.put("lastModified", remoteObj.objectMetadata.lastModified.time)
                metaFile.writeText(localMeta.toString())

                val arr = JSONArray(remoteText)
                return@withContext (0 until arr.length()).map {
                    Book.of(arr.getJSONObject(it))
                }
            } catch (e: Exception) {
                return@withContext null
            }
        }

        suspend fun loadFileList(context: Context): List<Book> = withContext(Dispatchers.IO) {
            return@withContext try {
                val listFile = File(context.cacheDir, "list.json")
                if (listFile.exists()) {
                    val arr = JSONArray(listFile.readText())
                    (0 until arr.length()).map {
                        Book.of(arr.getJSONObject(it))
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}