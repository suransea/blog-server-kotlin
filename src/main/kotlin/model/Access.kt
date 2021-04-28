package top.suransea.model

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object ArticleAccess {
    fun all(): List<Article> = transaction {
        ArticleTable.selectAll().map(ResultRow::toArticle)
    }

    fun findById(id: Int): Article? = transaction {
        ArticleTable.select { ArticleTable.id eq id }
            .map(ResultRow::toArticle).firstOrNull()
    }

    fun add(article: Article): Int = transaction {
        ArticleTable.insertAndGetId {
            article.id?.let { articleId ->
                it[id] = articleId
            }
            it[title] = article.title.orEmpty()
            it[summary] = article.summary.orEmpty()
            val now = Date().time
            it[ctime] = now
            it[utime] = now
            it[contentId] = ContentTable.insertAndGetId { table ->
                table[content] = article.content.orEmpty()
                table[ctime] = now
                table[utime] = now
            }.value
        }.value
    }

    fun addOrUpdate(article: Article): Unit = transaction {
        ArticleTable
            .slice(ArticleTable.contentId)
            .select { ArticleTable.id eq article.id }
            .map { it[ArticleTable.contentId] }
            .firstOrNull()
            ?.let { contentId ->
                val now = Date().time
                ArticleTable.update({ ArticleTable.id eq article.id }) {
                    it[title] = article.title.orEmpty()
                    it[summary] = article.summary.orEmpty()
                    it[utime] = now
                }
                ContentTable.update({ ContentTable.id eq contentId }) {
                    it[content] = article.content.orEmpty()
                    it[utime] = now
                }
            }
            ?: run {
                add(article)
            }
    }

    fun remove(articleId: Int): Unit = transaction {
        ArticleTable
            .slice(ArticleTable.contentId)
            .select { ArticleTable.id eq articleId }
            .map { it[ArticleTable.contentId] }
            .firstOrNull()
            ?.let {
                ArticleTable.deleteWhere { ArticleTable.id eq articleId }
                ContentTable.deleteWhere { ContentTable.id eq it }
            }
    }
}

private fun ResultRow.toArticle() = let {
    with(ArticleTable) {
        Article(
            it[id].value,
            it[title],
            it[summary],
            ContentTable.slice(ContentTable.content).select {
                ContentTable.id eq it[contentId]
            }.firstOrNull()?.getOrNull(ContentTable.content),
            it[ctime],
            it[utime]
        )
    }
}
