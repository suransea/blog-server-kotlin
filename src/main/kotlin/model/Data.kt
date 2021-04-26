package top.suransea.model

open class Timed(
    var ctime: Long = 0,
    var utime: Long = 0
)

data class Article(
    var id: Int? = null,
    var title: String? = "",
    var summary: String? = "",
    var content: String? = "",
) : Timed()

fun Article(entity: ArticleEntity) = Article(
    entity.id.value, entity.title, entity.summery, ContentEntity[entity.contentId].content
).withTimeFrom(entity)

fun <T : Timed> T.withTimeFrom(entity: TimedIntEntity): T {
    ctime = entity.ctime
    utime = entity.utime
    return this
}
