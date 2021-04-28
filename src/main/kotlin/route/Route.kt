package top.suransea.route

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import top.suransea.model.Article
import top.suransea.model.ArticleAccess

fun Routing.all() {
    article()
}

fun Routing.article() = route("/articles") {
    get {
        call.respond(ArticleAccess.all())
    }
    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "invalid id.")
            return@get
        }
        ArticleAccess.findById(id)?.let {
            call.respond(it)
        } ?: call.respond(HttpStatusCode.NotFound, "no result.")
    }
    post {
        call.respond(ArticleAccess.add(call.receive()))
    }
    put("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "invalid id.")
            return@put
        }
        val article: Article = call.receive()
        article.id = id
        ArticleAccess.addOrUpdate(article)
        call.respond(HttpStatusCode.OK, "")
    }
    delete("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: run {
            call.respond(HttpStatusCode.BadRequest, "invalid id.")
            return@delete
        }
        ArticleAccess.remove(id)
        call.respond(HttpStatusCode.OK, "")
    }
}
