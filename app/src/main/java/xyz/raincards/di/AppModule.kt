package xyz.raincards.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.raincards.BuildConfig
import xyz.raincards.api.Api
import xyz.raincards.api.HeadersInterceptor
import xyz.raincards.api.NetworkConnectionInterceptor
import xyz.raincards.utils.Setup

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext appContext: Context): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        val TIMEOUT_IN_SECONDS = 60

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_IN_SECONDS.toLong(), TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_IN_SECONDS.toLong(), TimeUnit.SECONDS)
            .addInterceptor(HeadersInterceptor(appContext))
            .addInterceptor(loggingInterceptor)
            .addInterceptor(NetworkConnectionInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(Setup.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiOnboarding(retrofit: Retrofit): Api = retrofit.create(Api::class.java)

//    @Provides
//    @Singleton
//    fun provideDatabase(app: Application): AppDatabase = Room
//        .databaseBuilder(app, AppDatabase::class.java, "zenna_db")
//        .fallbackToDestructiveMigration()
//        .build()
}
