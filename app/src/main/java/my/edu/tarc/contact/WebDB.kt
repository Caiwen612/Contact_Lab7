package my.edu.tarc.mycontact

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

class WebDB constructor(context: Context) {
    //This also use singleton design pattern
    companion object{
        @Volatile
        private var INSTANCE: WebDB? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: WebDB(context).also {
                    INSTANCE = it
                }
            }
    }

    //Load image
    //by lazy = only create the varible when it is needed, very last minute
    //When u need this variable, then it only created , if not it will not created
    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    //
    fun <T> addToRequestQueue(request: Request<T>){
        requestQueue.add(request)
    }
}