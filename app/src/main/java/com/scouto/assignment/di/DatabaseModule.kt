package com.scouto.assignment.di

import android.content.Context
import androidx.room.Room
import com.scouto.assignment.data.database.AppDatabase
import com.scouto.assignment.network.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun getDatatbase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "My Database"
        ).build()
    }

    @Provides
    fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://vpic.nhtsa.dot.gov/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun getService(retrofit: Retrofit) : NetworkService {
        return retrofit.create(NetworkService::class.java)
    }
}