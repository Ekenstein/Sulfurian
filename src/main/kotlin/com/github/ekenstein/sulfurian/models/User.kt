package com.github.ekenstein.sulfurian.models

data class UserId(override val id: IdValue) : Id

data class User(val id: UserId, val userName: String, val email: String)