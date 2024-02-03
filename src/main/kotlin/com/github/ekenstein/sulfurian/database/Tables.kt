package com.github.ekenstein.sulfurian.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val id = uuid("id")
    val name = varchar("user_name", 255)
    val email = varchar("email", length = 255)

    override val primaryKey = PrimaryKey(id)
}

object Characters : Table() {
    val id = uuid("id")
    val name = varchar("name", 255)
    val server = varchar("server", 255)
    val race = varchar("race", 255)
    val characterClass = varchar("class", 255)
    val userId = reference("user_id", Users.id)

    override val primaryKey = PrimaryKey(id)
}

object Wallets : Table() {
    val id = uuid("id")
    val characterId = reference("character_id", Characters.id)

    override val primaryKey = PrimaryKey(id)
}

object Tokens : Table() {
    val id = uuid("id")
    val walletId = reference("wallet_id", Wallets.id)

    override val primaryKey = PrimaryKey(id)
}

fun createSchema(database: Database) {
    transaction(database) {
        SchemaUtils.create(Users, Characters, Wallets, Tokens)
    }
}
