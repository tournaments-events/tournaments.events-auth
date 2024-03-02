package com.sympauthy.view

import com.sympauthy.config.model.UrlsConfig
import com.sympauthy.config.model.getOrNull
import io.micronaut.context.condition.Condition
import io.micronaut.context.condition.ConditionContext

/**
 * Condition checking if at least one of the urls configured in the flow is served by this server.
 */
class UserFlowEnabled : Condition {

    override fun matches(context: ConditionContext<*>): Boolean {
        val urlsConfig = context.getBean(UrlsConfig::class.java).getOrNull() ?: return false
        val root = urlsConfig.root.toString()
        return urlsConfig.flow.all.any { it.toString().startsWith(root) }
    }
}
