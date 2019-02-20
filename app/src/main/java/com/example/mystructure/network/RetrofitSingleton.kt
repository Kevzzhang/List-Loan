import android.R
import android.os.Build
import com.example.mybase.network.RestApi
import com.example.mystructure.BuildConfig
import com.example.mystructure.network.CustomSslSocketFactory
import com.example.mystructure.network.UnsafeOkHttpClient
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
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



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            client = UnsafeOkHttpClient.getUnsafeOkHttpClient()
//            client = OkHttpClient.Builder().addInterceptor(logging)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .retryOnConnectionFailure(false).build()
        } else {
            var cipherSuites: MutableList<CipherSuite>? = ConnectionSpec.MODERN_TLS.cipherSuites()
            if (!cipherSuites!!.contains(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)) {
                cipherSuites = ArrayList(cipherSuites)
                cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
            }
            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .cipherSuites(*cipherSuites.toTypedArray())
                    .build()
            client = OkHttpClient.Builder().addInterceptor(logging)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .connectionSpecs(listOf(spec))
                    .sslSocketFactory(getCustomSSLSocket(), getCustomTrustManager()).build()
        }


        retrofit = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()


        restApi = retrofit.create<RestApi>(RestApi::class.java)
    }

    fun getRestApi(): RestApi {
        return restApi
    }

    private fun getCustomSSLSocket(): SSLSocketFactory? {
        if (customSSL == null) {
            try {
                customSSL = CustomSslSocketFactory()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

        }
        return customSSL
    }

    private fun getCustomTrustManager(): X509TrustManager {
        if (customTrustManager == null) {
            customTrustManager = CustomTrustManager()
        }
        return customTrustManager as X509TrustManager
    }


}