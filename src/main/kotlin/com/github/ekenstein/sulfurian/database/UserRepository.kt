package com.github.ekenstein.sulfurian.database

import com.github.ekenstein.sulfurian.models.User
import com.github.ekenstein.sulfurian.models.UserId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface ReadOnlyUserRepository {
    fun selectUsers(): List<User>
    fun selectUserById(id: UserId): User?
}

interface WritableUserRepository : ReadOnlyUserRepository {
    fun insertUser(user: User)
    fun updateUser(user: User)
    fun deleteUser(userId: UserId)
}

class UserRepository : WritableUserRepository {
    private fun rowMapper(row: ResultRow) = User(
        id = UserId(row[Users.id]),
        userName = row[Users.name],
        email = row[Users.email],
    )

    override fun insertUser(user: User) {
        Users.insert {
            it[id] = user.id.id
            it[name] = user.userName
            it[email] = user.email
        }
    }

    override fun updateUser(user: User) {
        Users.update({ Users.id eq user.id.id }) {
            it[name] = user.userName
            it[email] = user.email
        }
    }

    override fun deleteUser(userId: UserId) {
        Users.deleteWhere { id.eq(userId.id) }
    }

    override fun selectUsers(): List<User> = Users.selectAll().map(::rowMapper)

    override fun selectUserById(id: UserId): User? = Users.selectAll()
        .where { Users.id eq id.id }
        .map(::rowMapper)
        .singleOrNull()

}