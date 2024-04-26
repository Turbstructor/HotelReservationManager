package spartacodingclub.nbcamp.kotlinspring.assignment.core.data

import kotlin.random.Random

class Customer(val name: String) {
    private var balance: Int = 0
    private val expenseLog: ArrayList<String> = arrayListOf()

    init {
        balance = Random.nextInt(100000, 1000001)
        expenseLog.add("Initial balance set: $balance")
    }

    fun printExpenseLog() {
        var logCount = 1

        println("Querying expenses log of $name:")
        for (eachLog in expenseLog) println("${logCount++}. $eachLog")
        println()
        println("Current balance: $balance")
    }

    fun addUsage(money: Int, isExpense: Boolean, detail: String) {
        expenseLog.add("$money ${(if (isExpense) "extracted" else "deposited")} for $detail")
        balance += (money * (if (isExpense) -1 else 1))
    }
}
