package jp.mydns.lilium.booklist

import org.json.JSONObject

data class Book(
    val name: String,
    val path: String,
    val size: Long,
) : DiffCallback.Item {
    companion object {
        fun of(obj: JSONObject): Book {
            val path = obj.getString("path")
            val lastSlash = path.lastIndexOf('\\')
            val name = path.substring(lastSlash + 1)
            val size = obj.getLong("size")
            return Book(
                name = name,
                path = path,
                size = size,
            )
        }
    }

    override val id get() = path
}