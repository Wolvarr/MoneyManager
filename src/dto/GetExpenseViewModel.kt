package hu.bme.dto

import org.joda.time.DateTime
import java.util.*

data class GetExpenseViewModel(
    val name: String,
    val amount: Int,
    val date: DateTime
)