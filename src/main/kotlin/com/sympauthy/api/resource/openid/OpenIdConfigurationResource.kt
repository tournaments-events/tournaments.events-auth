package com.sympauthy.api.resource.openid

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    externalDocs = ExternalDocumentation(
        description = "OpenId Connect Discovery specification",
        url = "https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata"
    )
)
@Serdeable
data class OpenIdConfigurationResource(
    @JsonProperty("issuer")
    val issuer: String,
    @JsonProperty("authorization_endpoint")
    val authorizationEndpoint: String,
    @JsonProperty("token_endpoint")
    val tokenEndpoint: String,
    @JsonProperty("userinfo_endpoint")
    val userInfoEndpoint: String? = null,
    @JsonProperty("jwks_uri")
    val jwksUri: String,
    @JsonProperty("registration_endpoint")
    val registrationEndpoint: String? = null,
    @JsonProperty("scopes_supported")
    val scopesSupported: List<String>? = null,
    @JsonProperty("response_types_supported")
    val responseTypesSupported: List<String>,
    @JsonProperty("response_modes_supported")
    val responseModesSupported: List<String>? = null,
    @JsonProperty("grant_types_supported")
    val grantTypesSupported: List<String>? = null,
    @JsonProperty("acr_values_supported")
    val acrValuesSupported: List<String>? = null,
    @JsonProperty("subject_types_supported")
    val subjectTypesSupported: List<String>,
    @JsonProperty("id_token_signing_alg_values_supported")
    val idTokenSigningAlgValuesSupported: List<String>,
    @JsonProperty("id_token_encryption_alg_values_supported")
    val idTokenEncryptionAlgValuesSupported: List<String>? = null,
    @JsonProperty("id_token_encryption_enc_values_supported")
    val idTokenEncryptionEncValuesSupported: List<String>? = null,
    @JsonProperty("userinfo_signing_alg_values_supported")
    val userInfoSigningAlgValuesSupported: List<String>? = null,
    @JsonProperty("userinfo_encryption_alg_values_supported")
    val userInfoEncryptionAlgValuesSupported: List<String>? = null,
    @JsonProperty("userinfo_encryption_enc_values_supported")
    val userInfoEncryptionEncValuesSupported: List<String>? = null,
    @JsonProperty("request_object_signing_alg_values_supported")
    val requestObjectSigningAlgValuesSupported: List<String>? = null,
    @JsonProperty("request_object_encryption_alg_values_supported")
    val requestObjectEncryptionAlgValuesSupported: List<String>? = null,
    @JsonProperty("token_endpoint_auth_methods_supported")
    val tokenEndpointAuthMethodsSupported: List<String>? = null,
    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    val tokenEndpointAuthSigningAlgValuesSupported: List<String>? = null,
    @JsonProperty("display_values_supported")
    val displayValuesSupported: List<String>? = null,
    @JsonProperty("claim_types_supported")
    val claimTypesSupported: List<String>? = null,
    @JsonProperty("claims_supported")
    val claimsSupported: List<String>? = null,
    @JsonProperty("service_documentation")
    val serviceDocumentation: String? = null,
    @JsonProperty("claims_locales_supported")
    val claimsLocalesSupported: List<String>? = null,
    @JsonProperty("ui_locales_supported")
    val uiLocalesSupported: List<String>? = null,
    @JsonProperty("claims_parameter_supported")
    val claimsParameterSupported: Boolean? = null,
    @JsonProperty("request_parameter_supported")
    val requestParameterSupported: Boolean? = null,
    @JsonProperty("request_uri_parameter_supported")
    val requestUriParameterSupported: Boolean? = null,
    @JsonProperty("require_request_uri_registration")
    val requireRequestUriRegistration: Boolean? = null,
    @JsonProperty("op_policy_uri")
    val opPolicyUri: String? = null,
    @JsonProperty("op_tos_uri")
    val opTosUri: String? = null
)
