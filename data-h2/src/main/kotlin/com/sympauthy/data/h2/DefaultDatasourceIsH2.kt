package com.sympauthy.data.h2

import io.micronaut.context.condition.Condition
import io.micronaut.context.condition.ConditionContext
import io.r2dbc.h2.H2ConnectionFactory
import io.r2dbc.spi.ConnectionFactory

/**
 * Condition checking if the default datasource is an H2 database.
 */
class DefaultDatasourceIsH2 : Condition {

    override fun matches(context: ConditionContext<*>): Boolean {
        val connectionFactory = context.getBean(ConnectionFactory::class.java)
        return connectionFactory is H2ConnectionFactory
    }
}
