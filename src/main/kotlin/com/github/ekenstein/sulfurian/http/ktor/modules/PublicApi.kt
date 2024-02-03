package com.github.ekenstein.sulfurian.http.ktor.modules

import com.github.ekenstein.sulfurian.Config
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.oauth
import io.ktor.server.routing.routing

fun Application.publicApi(config: Config) {
    install(Authentication) {
        oauth("auth-oauth-discord") {
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "discord",
                    authorizeUrl = "https://discord.com/oauth2/authorize",
                    accessTokenUrl = "https://discord.com/api/oauth2/token",
                    clientId = config.discordClientId,
                    clientSecret = config.discordClientSecret,
                )
            }
        }
    }

    routing {

    }
}
