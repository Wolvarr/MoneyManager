package hu.bme.dto

import org.joda.time.DateTime
import java.util.*

data class ExpensesFilter(
    val name: String = "",
    val maxAmount: Double?,
    val minAmount: Double?,
    val beforeDate: DateTime?,
    val afterDate: DateTime?
    val asdasd : DateTime?
)