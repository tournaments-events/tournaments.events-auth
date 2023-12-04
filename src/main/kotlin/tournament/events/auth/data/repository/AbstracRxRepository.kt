package tournament.events.auth.data.repository

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.rx3.rxSingle
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.metamodel.PropertyMetamodel
import org.komapper.core.dsl.query.FlowQuery
import org.komapper.core.dsl.query.QueryScope
import org.komapper.r2dbc.R2dbcDatabase
import tournament.events.auth.data.model.IdentifiableEntity
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

typealias Transaction = CoroutineContext.Element

abstract class AbstracRxRepository<Entity : Any, Id : Any, Meta : EntityMetamodel<Entity, Id, Meta>>(
    private val database: R2dbcDatabase,
    private val meta: Meta
) {
    internal fun getCoroutineContext(transaction: Transaction?): CoroutineContext {
        return when {
            transaction != null -> EmptyCoroutineContext + transaction
            else -> EmptyCoroutineContext
        }
    }

    fun insert(entity: Entity): Single<Entity> {
        return insert(entity, null)
    }

    fun insert(
        entity: Entity,
        transaction: Transaction?
    ): Single<Entity> {
        return rxSingle(getCoroutineContext(transaction)) {
            if (entity is IdentifiableEntity) {
                entity.id = UUID.randomUUID()
            }
            database.runQuery {
                QueryDsl.insert(meta).single(entity)
            }
        }
    }

    protected fun insertOrUpdate(
        entity: Entity,
        vararg keys: PropertyMetamodel<Entity, *, *>,
        transaction: Transaction? = null
    ): Single<Entity> {
        return rxSingle(getCoroutineContext(transaction)) {
            if (entity is IdentifiableEntity) {
                entity.id = UUID.randomUUID()
            }
            database.runQuery {
                val query = QueryDsl.insert(meta)
                    .onDuplicateKeyUpdate(*keys)
                    .single(entity)
                query
            }
        }.map { entity }
    }

    fun update(
        entity: Entity,
        transaction: Transaction? = null
    ): Single<Entity> {
        return rxSingle(getCoroutineContext(transaction)) {
            database.runQuery {
                QueryDsl.update(meta).single(entity)
            }
        }
    }

    fun find(
        transaction: Transaction? = null,
        block: QueryScope.(meta: Meta) -> FlowQuery<Entity>
    ): Observable<Entity> {
        val flow = database.flowQuery {
            block(this, meta)
        }
        return flow.asObservable(getCoroutineContext(transaction))
    }

    fun findOne(
        transaction: Transaction? = null,
        block: QueryScope.(meta: Meta) -> FlowQuery<Entity>
    ): Maybe<Entity> {
        return find(transaction, block).singleElement()
    }
}
