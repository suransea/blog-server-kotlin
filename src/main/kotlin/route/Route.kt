package top.suransea.route

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import top.suransea.model.*

fun Routing.all() {
    article()
}

fun Routing.article() = route("/articles") {
    get {
        transaction {
            ArticleEntity.all().map(::Article)
        }.let {
            call.respond(it)
        }
    }
    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        id ?: run {
            call.respond(HttpStatusCode.BadRequest, "invalid id.")
            return@get
        }
        transaction {
            ArticleEntity.findById(id)?.run(::Article)
        }?.let {
            call.respond(it)
        } ?: run {
            call.respond(HttpStatusCode.NotFound, "")
        }
    }
    post {
        val article: Article = call.receive()
        transaction {
            ArticleEntity.new {
                title = article.title.orEmpty()
                summery = article.summary.orEmpty()
                contentId = ContentEntity.new {
                    content = article.content.orEmpty()
                    setupTime()
                }.id.value
                setupTime()
            }
        }.let {
            call.respond(it.id.value)
        }
    }
    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        id ?: run {
            call.respond(HttpStatusCode.BadRequest, "invalid id.")
            return@put
        }
        val article: Article = call.receive()
        var found = false
        transaction {
            ArticleEntity.findById(id)?.apply {
                found = true
                title = article.title.orEmpty()
                summery = article.summary.orEmpty()
                updateTime()
                ContentEntity[contentId].content = article.content.orEmpty()
            } ?: run {
                ArticleEntity.new(id) {
                    title = article.title.orEmpty()
                    summery = article.summary.orEmpty()
                    contentId = ContentEntity.new {
                        content = article.content.orEmpty()
                        setupTime()
                    }.id.value
                    setupTime()
                }
            }
        }
        call.respond(if (found) HttpStatusCode.OK else HttpStatusCode.Created, "")
    }
    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
        id ?: run {
            call.respond(HttpStatusCode.BadRequest, "invalid id.")
            return@delete
        }
        transaction {
            val article = ArticleEntity.findById(id) ?: return@transaction
            ContentEntity[article.id.value].delete()
            article.delete()
        }
        call.respond(HttpStatusCode.OK, "")
    }
}
