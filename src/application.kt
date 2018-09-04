package jp.kght6123.ktor.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
//import kotlinx.html.js.*
import kotlinx.css.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.http.content.resources
import io.ktor.http.content.static

fun main(args: Array<String>): Unit = io.ktor.server.netty.DevelopmentEngine.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        gson {
        }
    }

    val client = HttpClient(Jetty) {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!!!!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                head {
                    styleCss {
                        // 埋め込みCSS
                    }
                    link { rel = "stylesheet"; type = "text/css"; href = "/styles.css" }
                    script (type = ScriptType.textJavaScript) {
                        unsafe {
                            raw("alert('World!!')")
                        }
                    }
                }
                body {
                    h1 { +"HTML" }
                    p { classes = setOf("myclass"); text("HTML-DSL Hello World!!!") }
                    ul {
                        for (n in 1..10) {
                            li { style { color = Color.blue }; text("$n") }
                        }
                        //onClick = "message('clicked the image')"
                    }
                    button (classes = "btn") { text("Test")
                        //onClickFunction = {}
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.green
                }
            }
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
