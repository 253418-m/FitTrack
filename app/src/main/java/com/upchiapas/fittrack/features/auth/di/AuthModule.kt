package com.upchiapas.fittrack.features.auth.di

import android.content.Context
import com.upchiapas.fittrack.BuildConfig
import com.upchiapas.fittrack.core.network.*
import com.upchiapas.fittrack.core.session.*
import com.upchiapas.fittrack.features.auth.data.*
import com.upchiapas.fittrack.features.auth.domain.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

@Module @InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides @Singleton fun json() = Json { ignoreUnknownKeys = true }
    @Provides @Singleton fun snapshot() = SessionSnapshot()
    @Provides @Singleton fun api(json: Json, snapshot: SessionSnapshot): AuthApi {
        val configured = BuildConfig.API_BASE_URL
        val enabled = configured.isNotBlank() && (BuildConfig.DEBUG || configured.startsWith("https://"))
        val origin = (if (enabled) configured else "https://localhost/").toHttpUrl()
        val client = OkHttpClient.Builder()
            .followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false)
            .connectTimeout(15, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).callTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(origin, snapshot, enabled)).build()
        return Retrofit.Builder().baseUrl(origin).client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build().create(AuthApi::class.java)
    }
    @Provides @Singleton fun store(@ApplicationContext context: Context): SessionStore = ProtectedSessionStore(context, SessionCipher())
    @Provides @Singleton fun repository(api: AuthApi, store: SessionStore, snapshot: SessionSnapshot, json: Json) =
        AuthRepositoryImpl(api, store, snapshot, json, CoroutineScope(SupervisorJob() + Dispatchers.IO))
    @Provides fun auth(repository: AuthRepositoryImpl): AuthRepository = repository
    @Provides fun session(repository: AuthRepositoryImpl): SessionRepository = repository
    @Provides fun login(repository: AuthRepository) = LoginUseCase(repository)
    @Provides fun register(repository: AuthRepository) = RegisterUseCase(repository)
    @Provides fun observe(repository: SessionRepository) = ObserveSession(repository)
    @Provides fun restore(repository: SessionRepository) = RestoreSession(repository)
    @Provides fun logout(repository: SessionRepository) = Logout(repository)
}
