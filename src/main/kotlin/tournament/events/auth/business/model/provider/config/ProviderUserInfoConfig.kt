package tournament.events.auth.business.model.provider.config

import com.jayway.jsonpath.internal.Path
import tournament.events.auth.business.model.provider.ProviderUserInfoPathKey
import java.net.URI

data class ProviderUserInfoConfig(
    val uri: URI,
    val paths: Map<ProviderUserInfoPathKey, Path>
)
