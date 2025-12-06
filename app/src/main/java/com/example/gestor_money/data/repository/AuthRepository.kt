package com.example.gestor_money.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Firebase Authentication
 * Handles user authentication (email/password and anonymous) and provides auth state observation.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val _authState = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val authState: Flow<FirebaseUser?> = _authState.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authState.value = firebaseAuth.currentUser
        }
    }

    /**
     * Observes the authentication state of the user.
     * Emits the current FirebaseUser or null if no user is signed in.
     */
    fun observeAuthState(): Flow<FirebaseUser?> {
        return authState
    }

    /**
     * Get current user ID, or null if not authenticated
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Sign in a user with email and password.
     */
    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error desconocido al iniciar sesión")
        }
    }

    /**
     * Sign up a new user with email and password.
     */
    suspend fun signUp(email: String, password: String): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error desconocido al registrarse")
        }
    }

    /**
     * Sign in anonymously and return user ID.
     */
    suspend fun signInAnonymously(): AuthResult {
        return try {
            auth.signInAnonymously().await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error desconocido al iniciar sesión anónimamente")
        }
    }

    /**
     * Check if user is currently signed in.
     */
    fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        auth.signOut()
    }
}

/**
 * Sealed class to represent the result of authentication operations.
 */
sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
