package com.github.ekenstein.sulfurian.models

import java.util.UUID

typealias IdValue = UUID

interface Id {
    val id: IdValue
}