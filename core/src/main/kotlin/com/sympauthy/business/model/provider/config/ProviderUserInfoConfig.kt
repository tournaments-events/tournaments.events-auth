package com.sympauthy.business.model.provider.config

import com.jayway.jsonpath.JsonPath
import com.sympauthy.business.model.provider.ProviderUserInfoPathKey
import java.net.URI

data class ProviderUserInfoConfig(
    val uri: URI,
    val paths: Map<ProviderUserInfoPathKey, JsonPath>
)
