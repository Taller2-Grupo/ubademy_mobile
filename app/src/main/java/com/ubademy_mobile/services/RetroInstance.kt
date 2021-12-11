package com.ubademy_mobile.services
import android.util.Log
import com.ubademy_mobile.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {

    companion object{

        fun getRetroInstance(baseUrl:String):Retrofit{
            val logging = HttpLoggingInterceptor()
            logging.level = (HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
            client.addInterceptor(logging)

            if (Constants.TOKEN != null) {
                Log.e("token not null", Constants.TOKEN.toString())
                client.addInterceptor(
                Interceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("Authorization", "Bearer " + Constants.TOKEN)
                        .method(original.method, original.body)
                        .build()
                    chain.proceed(request)
            })}

           return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}