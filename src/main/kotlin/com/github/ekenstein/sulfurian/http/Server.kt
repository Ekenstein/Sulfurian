package com.github.ekenstein.sulfurian.http

import com.github.ekenstein.sulfurian.Config
import com.github.ekenstein.sulfurian.http.ktor.modules.publicApi
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class Server private constructor(engine: ApplicationEngine){
    companion object {
        fun start(config: Config): Server {
            val engine = embeddedServer(Netty) {
                publicApi(config)
            }

            return Server(engine)
        }
    }
}