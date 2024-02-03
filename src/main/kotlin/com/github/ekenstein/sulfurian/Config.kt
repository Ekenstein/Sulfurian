package com.github.ekenstein.sulfurian

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required

class Config private constructor(private val argParser: ArgParser) {
    val discordClientId by argParser.option(
        ArgType.String,
        "client_id"
    ).required()

    val discordClientSecret by argParser.option(
        ArgType.String,
        "client_secret"
    ).required()

    companion object {
        fun parse(args: Array<String>): Config {
            val parser = ArgParser("Sulfurian")
            val config = Config(parser)
            parser.parse(args)
            return config
        }
    }
}
