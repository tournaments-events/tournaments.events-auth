package com.sympauthy.api.controller.flow

import com.sympauthy.api.resource.flow.SignInInputResource
import com.sympauthy.api.resource.flow.SignInResultResource
import com.sympauthy.api.util.AuthorizationFlowRedirectUriBuilder
import com.sympauthy.business.manager.password.PasswordFlowManager
import com.sympauthy.security.authorizeAttempt
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject

// http://localhost:8092/api/oauth2/authorize?response_type=code&client_id=example&redirect_uri=http://example.com&state=whatever
// http://localhost:5173/sign-in?state=eyJraWQiOiIiLCJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwOTIiLCJzdWIiOiI5ODlkNmRjYS0yYmFkLTRjMzgtYWVjZi05YjgwYTgyYzMzZTcifQ.WJeRIo0Wx8ZLy8VhacKakidA-wOhb5jnDKaEq0ltheU_HRtqoHgFptFaaN4ugyCJRQvAwHvf197RCw4662mjgZ9FDEtNJ_IeoUZPWzwLFSw7CF7s918sq7ZtvzmEKKShxrqYoMPiN8LHALFOt9JMiVGNzNn47y-SLxgx91b0-CmC6hEGjusNVM7cVud7OQAfofvthU81i_YBKuOXOk9Cdfx9F8XaQHJwhyXp7SXvCw9JDJYDVeiGZK2dv_4zfVNr8d0yQUhv_KoIHlOs_APQpTQcfs3fs65i3l-n6vOthZj6tWb95d2huX1tgAMvLEaVyyaK411u7dSjJ8iYd7tJVg

@Controller("/api/v1/flow/sign-in")
@Secured(IS_ANONYMOUS)
class SignInController(
    @Inject private val passwordFlowManager: PasswordFlowManager,
    @Inject private val authorizationFlowRedirectUriBuilder: AuthorizationFlowRedirectUriBuilder,
) {

    @Operation(
        description = "Sign-in using a login and a password.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "The credentials provided where valid. The authentication flow will continue.",
                useReturnTypeSchema = true
            )
        ],
        tags = ["flow"]
    )
    @Post
    suspend fun signIn(
        authentication: Authentication,
        @Body inputResource: SignInInputResource
    ): SignInResultResource {
        val attempt = authentication.authorizeAttempt

        val result = passwordFlowManager.signInWithPassword(
            authorizeAttempt = attempt,
            login = inputResource.login,
            password = inputResource.password
        )
        return authorizationFlowRedirectUriBuilder.getRedirectUri(attempt, result)
            .toString()
            .let(::SignInResultResource)
    }
}
