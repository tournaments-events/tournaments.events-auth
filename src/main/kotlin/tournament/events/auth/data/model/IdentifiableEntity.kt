package tournament.events.auth.data.model

import java.util.UUID

/**
 * An entity that can be identified by a UUID string.
 *
 * When the entity is created into the database, the id will automatically be generated by the
 * repository.
 */
interface IdentifiableEntity {
    var id: UUID?
}
