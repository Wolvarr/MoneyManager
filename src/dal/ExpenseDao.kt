package hu.bme.dal
import hu.bme.dto.ExpensesFilter
import hu.bme.dto.GetExpenseViewModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Closeable
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime

class ExpenseDao(val db: Database) : DAOInterface {

    override fun init() = transaction(db) {
        SchemaUtils.create(Expenses)
    }

    override fun createExpense(name: String, amount: Int) = transaction(db) {
        Expenses.insert {
            it[Expenses.name] = name
            it[Expenses.amount] = amount
            it[Expenses.date]  = DateTime.now()
        }
        Unit
    }

    override fun updateExpense(id: Int, name: String, amount: Int, date: DateTime) = transaction(db) {
        Expenses.update({ Expenses.id eq id}) {
            it[Expenses.name] = name
            it[Expenses.amount] = amount
            it[Expenses.date]  = date
        }
        Unit
    }

    override fun deleteExpense(id: Int) = transaction(db) {
        Expenses.deleteWhere { Expenses.id eq id }
        Unit
    }

    override fun getExpense(id: Int) = transaction(db) {
        Expenses.select{ Expenses.id eq id}.map{
            GetExpenseViewModel(
                it[Expenses.name],
                it[Expenses.amount],
                it[Expenses.date]
            )}.singleOrNull()
    }

    override fun sumExpenses(id1: Int, id2: Int) = transaction(db){
        val expense1 = getExpense(id1)
        val expense2 = getExpense(id2)
        updateExpense(id1, expense1?.name as String, (expense1.amount + expense2?.amount as Int), expense1.date)
        deleteExpense(id2)
    }

    override fun getAllExpenses() = transaction(db) {
      TODO("Not yet implemented")
    }

    override fun close() {}
}

interface DAOInterface : Closeable {
    fun init()
    fun createExpense(name: String, amount: Int)
    fun updateExpense(id: Int, name: String, amount: Int, date: DateTime)
    fun deleteExpense(id: Int)
    fun getExpense(id: Int): GetExpenseViewModel?
    fun sumExpenses(id1: Int, id2: Int)
    //TODO filterek
    fun getAllExpenses(): List<GetExpenseViewModel>
}