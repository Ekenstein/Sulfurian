package com.github.ekenstein.sulfurian

import com.github.ekenstein.sulfurian.database.ReadOnlyUserRepository
import com.github.ekenstein.sulfurian.database.UserRepository
import com.github.ekenstein.sulfurian.database.WritableUserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

private const val TRANSACTION_ISOLATION = Connection.TRANSACTION_READ_COMMITTED

class ServiceContext(private val database: Database) {
    fun <T> readOnlyTransaction(statement: ServiceContextInReadOnlyTransaction.() -> T): T =
        transaction(TRANSACTION_ISOLATION, readOnly = true) {
            val context = ServiceContextInReadOnlyTransaction()
            statement(context)
        }
}

open class ServiceContextInReadOnlyTransaction {
    open val userRepository: ReadOnlyUserRepository by lazy {
        UserRepository()
    }
}

class ServiceContextInWritableTransaction : ServiceContextInReadOnlyTransaction() {
    override val userRepository: WritableUserRepository by lazy {
        UserRepository()
    }
}
