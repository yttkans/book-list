package jp.mydns.lilium.booklist.repository

import jp.mydns.lilium.booklist.Book

class QueryRepository {
    companion object {
        private val space = Regex("\\s")

        fun query(value: String?, items: List<Book>?): List<Book>? {
            if (items == null) return null
            if (value == null) return items

            val vs = value.split(space).filter { it.isNotBlank() }
            return items.filter { item ->
                vs.all { item.name.contains(it, true) }
            }
        }
    }
}