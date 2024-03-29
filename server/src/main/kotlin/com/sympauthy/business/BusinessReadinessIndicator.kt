package com.sympauthy.business

import com.sympauthy.business.manager.ClaimManager
import com.sympauthy.business.manager.ClientManager
import com.sympauthy.business.manager.ScopeManager
import com.sympauthy.util.loggerForClass
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.discovery.event.ServiceReadyEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking

@Singleton
class BusinessReadinessIndicator(
    @Inject private val claimManager: ClaimManager,
    @Inject private val clientManager: ClientManager,
    @Inject private val scopeManager: ScopeManager
) : ApplicationEventListener<ServiceReadyEvent> {

    private val logger = loggerForClass()

    override fun onApplicationEvent(event: ServiceReadyEvent) = runBlocking {
        try {
            val claims = claimManager.listStandardClaims()
            val clients = clientManager.listClients()
            val scopes = scopeManager.listScopes()

            logger.info("${claims.count()} claims, ${scopes.count()} scopes and ${clients.count()} client(s) are enabled.")
        } catch (t: Throwable) {
            // Do not log since the errors will likely be reported by another components
        }
    }
}
