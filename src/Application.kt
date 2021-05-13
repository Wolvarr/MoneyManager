package hu.bme

import hu.bme.Model.Expense
import hu.bme.dal.ExpenseDao
import hu.bme.dto.CreateExpenseDto
import hu.bme.dto.ExpensesFilter
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database

val dao = ExpenseDao(
    Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;",
    driver = "org.h2.Driver"))

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 23567
    embeddedServer(Netty, port) {
        dao.init()
        install(CallLogging)
        install (ContentNegotiation) {
            gson {
                setPrettyPrinting ()
            }
        }
        routing {
            route("/expenses") {
                get {
                    call.respond(mapOf("products" to dao.getAllExpenses()))
                }
                post {
                    val expense = call.receive<CreateExpenseDto>()
                    dao.createExpense(expense.name, expense.amount)
                    call.respond(HttpStatusCode.OK)
                }
                put {
                    val expense = call.receive<Expense>()
                    dao.updateExpense(expense.id, expense.name, expense.amount, expense.date)
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]
                    if (id != null)
                        dao.deleteExpense(id.toInt())
                    call.respond(HttpStatusCode.OK)
                }

                get("/{id}") {
                    val id = call.parameters["id"]
                    if (id != null) {
                        val response = dao.getExpense(id.toInt())
                        if (response != null)
                            call.respond(response)
                        else call.respond("No such expense found!")
                    }
                }
            }
        }
    }.start(wait = true)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
}

