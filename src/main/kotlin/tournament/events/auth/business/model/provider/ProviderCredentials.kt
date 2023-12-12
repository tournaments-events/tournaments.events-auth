package tournament.events.auth.business.model.provider

import io.micronaut.http.MutableHttpRequest

interface ProviderCredentials {

    fun <T> authenticate(httpRequest: MutableHttpRequest<T>)
}
