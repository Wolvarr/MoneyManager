package hu.bme.dto

data class CreateExpenseDto(
    val name: String,
    val amount: Int,
)