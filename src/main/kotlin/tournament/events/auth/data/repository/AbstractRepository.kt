package tournament.events.auth.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.metamodel.PropertyMetamodel
import org.komapper.core.dsl.query.FlowQuery
import org.komapper.core.dsl.query.QueryScope
import org.komapper.r2dbc.R2dbcDatabase
import tournament.events.auth.data.model.IdentifiableEntity
import java.util.*

abstract class AbstractRepository<Entity : Any, Id : Any, Meta : EntityMetamodel<Entity, Id, Meta>>(
    private val database: R2dbcDatabase,
    private val meta: Meta
) {

    suspend fun insert(entity: Entity): Entity {
        if (entity is IdentifiableEntity) {
            entity.id = UUID.randomUUID()
        }
        return database.runQuery {
            QueryDsl.insert(meta).single(entity)
        }
    }

    suspend fun insertOrUpdate(
        entity: Entity,
        vararg keys: PropertyMetamodel<Entity, *, *>
    ): Entity {
        if (entity is IdentifiableEntity) {
            entity.id = UUID.randomUUID()
        }
        database.runQuery {
            val query = QueryDsl.insert(meta)
                .onDuplicateKeyUpdate(*keys)
                .single(entity)
            query
        }
        return entity
    }

    fun find(
        block: QueryScope.(meta: Meta) -> FlowQuery<Entity>
    ): Flow<Entity> {
        return database.flowQuery {
            block(this, meta)
        }
    }

    suspend fun findOne(
        block: QueryScope.(meta: Meta) -> FlowQuery<Entity>
    ): Entity? {
        return find(block).firstOrNull()
    }
}
