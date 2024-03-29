package com.sympauthy.business.manager

import com.sympauthy.business.model.oauth2.Scope
import com.sympauthy.business.model.user.StandardScope
import com.sympauthy.config.model.ScopesConfig
import com.sympauthy.config.model.StandardScopeConfig
import com.sympauthy.config.model.orThrow
import io.reactivex.rxjava3.core.Observable
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.rx3.await

@Singleton
class ScopeManager(
    @Inject private val uncheckedScopesConfig: ScopesConfig
) {
    /**
     * List of scopes defined in the OAuth 2 & OpenId specifications.
     *
     * They can be enabled or disabled by the configuration.
     */
    private val enabledStandardScopes: Observable<Scope> = Observable.fromIterable(StandardScope.entries)
        .flatMap { standardScope ->
            val config = uncheckedScopesConfig.orThrow().scopes.asSequence()
                .filterIsInstance<StandardScopeConfig>()
                .firstOrNull { it.scope == standardScope.scope }
            if (config == null || config.enabled) {
                val scope = Scope(
                    scope = standardScope.scope
                )
                Observable.just(scope)
            } else {
                Observable.empty()
            }
        }
        .cache()

    /**
     * List of [Scope] enabled on this authorization server.
     *
     * The list contains both standard claims defined in the OpenID specification and custom scopes defined by
     * the operator of this authorization server.
     */
    suspend fun listScopes(): List<Scope> {
        return enabledStandardScopes.toList().await()
    }

    /**
     * Return the [Scope], otherwise null, if:
     * - [scope] is a standard scope and its has not been explicitly disabled by configuration.
     * - [scope] is a custom scope and have been properly defined in the configuration.
     */
    suspend fun find(scope: String): Scope? {
        return listScopes().firstOrNull { it.scope == scope }
    }
}
