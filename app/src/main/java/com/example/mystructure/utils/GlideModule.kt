package com.example.mystructure.utils

import android.content.Context
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.KeyStore
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@GlideModule
class GlideModule : AppGlideModule(){
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val clientBuilder = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)

        val factory = OkHttpUrlLoader.Factory(enableTls12OnPreLollipop(clientBuilder).build())

        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }

    private fun enableTls12OnPreLollipop(client: OkHttpClient.Builder): OkHttpClient.Builder {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT <= 19) {
            try {
                val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(null as KeyStore?)
                val trustManagers = trustManagerFactory.trustManagers
                if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
                    throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
                }
                val trustManager = trustManagers[0]

                val sc = SSLContext.getInstance("TLSv1.2")
                sc.init(null, null, null)
                client.sslSocketFactory(GlideSSLSocketFactory(sc.socketFactory), trustManager as X509TrustManager)

                var cipherSuites: MutableList<CipherSuite>? = ConnectionSpec.MODERN_TLS.cipherSuites()
                if (!cipherSuites!!.contains(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)) {
                    cipherSuites = ArrayList(cipherSuites)
                    cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                }
                val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .cipherSuites(*cipherSuites.toTypedArray())
                        .build()

                client.connectionSpecs(listOf(spec))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return client
    }
}
