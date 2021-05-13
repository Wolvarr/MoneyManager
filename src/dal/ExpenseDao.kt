package hu.bme.dal
import hu.bme.dto.ExpensesFilter
import hu.bme.dto.GetExpenseViewModel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Closeable
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import java.lang.Exception

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
        if(expense1 == null || expense2 == null)
        {
            throw Exception("The expenseses with the given ids were not found!")
        }
        updateExpense(id1, expense1?.name as String, (expense1.amount + expense2?.amount as Int), expense1.date)
        deleteExpense(id2)
    }

    override fun getAllExpenses(filter: ExpensesFilter) = transaction(db) {
       val expenseList =  Expenses.selectAll().map {
            GetExpenseViewModel(
                it[Expenses.name], it[Expenses.amount], it[Expenses.date]
            )
        }.filter { it.name.contains(filter.name) }

        //I know this would be bad in performance, but I didn't find (an easy and nice) solution to add the filtering to the SQL query
        //and decide if the filter properties are not null at the same time.
        //I think this way at least the name filter would be in the query (I'm not sure tho),
        // but I had to make it auto initialized to "" make this work

        if(filter.minAmount != null)
        {
            expenseList.filter { it.amount > filter.minAmount }
        }

        if(filter.maxAmount != null)
        {
            expenseList.filter { it.amount < filter.maxAmount }
        }

        if(filter.beforeDate != null)
        {
            expenseList.filter { it.date < filter.beforeDate }
        }

        if(filter.afterDate != null)
        {
            expenseList.filter { it.date > filter.afterDate }
        }

        expenseList
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
    fun getAllExpenses(filter: ExpensesFilter): List<GetExpenseViewModel>
}