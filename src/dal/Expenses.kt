package hu.bme.dal

import org.jetbrains.exposed.sql.Table

object Expenses : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name", 100)
    val amount = integer("amount")
    val date = date("date")
}