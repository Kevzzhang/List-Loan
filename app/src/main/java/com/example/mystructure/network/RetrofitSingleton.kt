import com.example.mybase.network.RestApi
import com.example.mystructure.BuildConfig
import com.example.mystructure.network.UnsafeOkHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RetrofitSingleton {

    private var retrofit: Retrofit
    private var logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private var client: OkHttpClient
    private var restApi: RestApi
    private var customSSL: SSLSocketFactory? = null
    private var customTrustManager: X509TrustManager? = null

    companion object {
        private var INSTANCE: RetrofitSingleton? = null
        fun getInstance(): RetrofitSingleton {
            if (INSTANCE == null)
                INSTANCE = RetrofitSingleton()
            return INSTANCE as RetrofitSingleton
        }
    }

    init {
        if (BuildConfig.DEBUG)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        else
            logging.setLevel(HttpLoggingInterceptor.Level.NONE)



        client = UnsafeOkHttpClient.getUnsafeOkHttpClient()

        retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()


        restApi = retrofit.create<RestApi>(RestApi::class.java)
    }

    fun getRestApi(): RestApi {
        return restApi
    }
}