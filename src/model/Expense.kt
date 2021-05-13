package hu.bme.model

import org.joda.time.DateTime

data class Expense(
    val id: Int,
    val name: String,
    val amount: Int,
    val date: DateTime
    )