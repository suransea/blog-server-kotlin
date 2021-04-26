package top.suransea.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

open class TimedIntEntity(id: EntityID<Int>, table: TimedIntIdTable) : IntEntity(id) {
    var ctime by table.ctime
    var utime by table.utime
}

class ArticleEntity(id: EntityID<Int>) : TimedIntEntity(id, ArticleTable) {
    companion object : IntEntityClass<ArticleEntity>(ArticleTable)

    var title by ArticleTable.title
    var summery by ArticleTable.summary
    var contentId by ArticleTable.contentId
}

class ContentEntity(id: EntityID<Int>) : TimedIntEntity(id, ContentTable) {
    companion object : IntEntityClass<ContentEntity>(ContentTable)

    var content by ContentTable.content
}

fun TimedIntEntity.setupTime() {
    val now = Date().time
    ctime = now
    utime = now
}

fun TimedIntEntity.updateTime() {
    utime = Date().time
}
