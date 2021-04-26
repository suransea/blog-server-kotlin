package top.suransea

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.mariadb.jdbc.MariaDbDataSource
import top.suransea.route.all
import java.text.DateFormat

fun main() {
    databaseInit()
    server().start(wait = true)
}

fun databaseInit() {
    MariaDbDataSource("localhost", 3306, "blog").apply {
        user = "sea"
        setPassword("")
        Database.connectPool(this)
    }
}

fun server() = embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
        }
    }
    routing(Routing::all)
}
