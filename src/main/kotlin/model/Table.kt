package top.suransea.model

import org.jetbrains.exposed.dao.id.IntIdTable

open class TimedIntIdTable(name: String = "", columnName: String = "id") : IntIdTable(name, columnName) {
    val ctime = long("ctime")
    val utime = long("utime")
}

object ArticleTable : TimedIntIdTable("article") {
    val title = varchar("title", 100).index()
    val summary = varchar("summary", 300)
    val contentId = integer("content_id")
}

object ContentTable : TimedIntIdTable("content") {
    val content = text("content")
}
