package com.github.diabetesassistant.auth.domain

import com.auth0.jwt.JWTVerifier
import com.github.diabetesassistant.auth.data.AuthClient
import com.github.diabetesassistant.auth.data.CredentialsDTO
import com.github.diabetesassistant.auth.data.UserCreationDTO
import com.github.diabetesassistant.auth.data.UserDTO
import java.util.UUID

class AuthService(private val authClient: AuthClient, private val verifier: JWTVerifier) {
    companion object {
        private const val PATIENT_ROLE = "patient"
    }

    suspend fun login(credentials: Credentials): Result<Token> {
        val dto = CredentialsDTO(credentials.email, credentials.password)
        val tokenCreationResult = authClient.createToken(dto)
        return tokenCreationResult.mapCatching {
            Token(it.accessToken, it.idToken, verifier.verify(it.idToken))
        }
    }

    suspend fun register(credentials: Credentials): Result<User> {
        val dto = UserCreationDTO(credentials.email, credentials.password, PATIENT_ROLE)
        val createdUserDTO: Result<UserDTO> = authClient.createUser(dto)
        return createdUserDTO.mapCatching {
            User(UUID.fromString(it.id), it.email)
        }
    }
}
