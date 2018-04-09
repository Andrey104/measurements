package ru.nextf.measurements.network

import android.content.Context
import android.preference.PreferenceManager
import ru.nextf.measurements.modelAPI.*
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by addd on 28.12.2017.
 */
object NetworkControllerDeals {
    private lateinit var date: String
    private lateinit var status: String
    private lateinit var listDeals: List<Deal>
    private var isAll = false

    var callbackListDeals: CallbackListDeals? = null
    var callbackPaginationListDeals: PaginationCallback? = null
    var callbackOneDeal: OneDealCallback? = null
    var addDiscountCallback: DiscountCallback? = null
    var callbackMountsDeal: MountsDealCallback? = null
    var callbackMount: MountCallback? = null
    var callbackOneMount: OneMountCallback? = null


    private val BASE_URL = "http://188.225.46.31/api/"
    private val api: MeasurementsAPI by lazy { init(ru.nextf.measurements.MyApp.instance) }
    private fun init(context: Context): MeasurementsAPI {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val okHttpClient = OkHttpClient.Builder()
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().addHeader("Authorization", "Token " + sp.getString("token", ""))?.build()
            chain.proceed(request)
        }

        okHttpClient.networkInterceptors().add(interceptor)
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())).client(okHttpClient.build()).build()
        return retrofit.create(MeasurementsAPI::class.java)

    }

    //----------------------------запросы---------------------------------
    fun getAllCurrentDeals() {
        isAll = true
        status = "current"
        val call = api.getCurrentDeals(1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackListDeals?.resultList(listDeals, 0, "all", it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1, "all", 0)
            }

        })
    }

    fun getAllRejectedDeals() {
        isAll = true
        status = "rejected"
        val call = api.getRejectedDeals(1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackListDeals?.resultList(listDeals, 0, "all", it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1, "all", 0)
            }

        })
    }

    fun getAllClosedDeals() {
        isAll = true
        status = "closed"
        val call = api.getClosedDeals(1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackListDeals?.resultList(listDeals, 0, "all", it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1, "all", 0)
            }

        })
    }

    fun getCurrentDeals(date: String) {
        isAll = false
        this.date = date
        status = "current"
        val call = api.getCurrentDeals(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackListDeals?.resultList(listDeals, 0, date, it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1, date, 0)
            }

        })
    }

    fun getMount(id: String) {
        val call = api.getMount(id)
        call.enqueue(object : retrofit2.Callback<Mount> {
            override fun onResponse(call: Call<Mount>?, response: Response<Mount>?) {
                response?.body()?.let {
                    callbackMount?.resultMount(it, true)
                }
            }

            override fun onFailure(call: Call<Mount>?, t: Throwable?) {
                callbackMount?.resultMount(Mount(), false)
            }

        })
    }

    fun getRejectedDeals(date: String) {
        isAll = false
        this.date = date
        status = "rejected"
        val call = api.getCurrentDeals(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackListDeals?.resultList(listDeals, 0, date, it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1, date, 0)
            }

        })
    }

    fun getClosedDeals(date: String) {
        isAll = false
        status = "closed"
        this.date = date
        val call = api.getCurrentDeals(date, 1)
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackListDeals?.resultList(listDeals, 0, date, it?.count ?: 1)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackListDeals?.resultList(emptyList(), 1, date, 0)
            }

        })
    }


    fun updateListInFragment() {
        when (status) {
            "current" -> {
                if (isAll) {
                    getAllCurrentDeals()
                } else {
                    getCurrentDeals(date)
                }
            }
            "rejected" ->
                if (isAll) {
                    getAllRejectedDeals()
                } else {
                    getRejectedDeals(date)
                }
            "closed" ->
                if (isAll) {
                    getAllClosedDeals()
                } else {
                    getClosedDeals(date)
                }
        }
    }


    fun pagination(page: Int) {
        when (status) {
            "current" -> paginationCurrentRequest(page)

            "rejected" -> paginationRejectRequest(page)

            "closed" -> paginationCloseRequest(page)
        }
    }

    private fun paginationCurrentRequest(page: Int) {
        var call: Call<MyResultDeals> = if (isAll) {
            api.getCurrentDeals(page)
        } else {
            api.getCurrentDeals(date, page)
        }
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackPaginationListDeals?.resultPagination(listDeals, 0)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackPaginationListDeals?.resultPagination(emptyList(), 1)
            }

        })

    }

    private fun paginationRejectRequest(page: Int) {
        var call: Call<MyResultDeals> = if (isAll) {
            api.getRejectedDeals(page)
        } else {
            api.getRejectedDeals(date, page)
        }
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackPaginationListDeals?.resultPagination(listDeals, 0)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackPaginationListDeals?.resultPagination(emptyList(), 1)
            }

        })

    }

    private fun paginationCloseRequest(page: Int) {
        var call: Call<MyResultDeals> = if (isAll) {
            api.getClosedDeals(page)
        } else {
            api.getClosedDeals(date, page)
        }
        call.enqueue(object : retrofit2.Callback<MyResultDeals> {
            override fun onResponse(call: Call<MyResultDeals>?, response: Response<MyResultDeals>?) {
                response?.body()?.let {
                    listDeals = it.results ?: emptyList()
                    callbackPaginationListDeals?.resultPagination(listDeals, 0)
                }
            }

            override fun onFailure(call: Call<MyResultDeals>?, t: Throwable?) {
                callbackPaginationListDeals?.resultPagination(emptyList(), 1)
            }

        })
    }


    fun getOneDeal(id: String) {
        val call = api.getOneDeal(id)
        call.enqueue(object : retrofit2.Callback<Deal> {
            override fun onResponse(call: Call<Deal>?, response: Response<Deal>?) {
                var deal: Deal? = null
                response?.body().let {
                    if (response?.code() == 200) {
                        deal = response.body()
                        callbackOneDeal?.resultOneDeal(deal, true)
                    } else {
                        callbackOneDeal?.resultOneDeal(deal, false)
                    }
                }
            }

            override fun onFailure(call: Call<Deal>?, t: Throwable?) {
                callbackOneDeal?.resultOneDeal(null, false)
            }

        })
    }

    fun addDiscount(recalculation: RecalculationRequest, id: String) {
        val call = api.addRecalculation(recalculation, id)
        call.enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                if (response?.code() == 200) {
                    addDiscountCallback?.resultAddDiscount(true)
                }
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                addDiscountCallback?.resultAddDiscount(false)
            }
        })
    }

    fun getMountsDeal(id: String) {
        val call = api.getMountsDeal(id)
        call.enqueue(object : retrofit2.Callback<List<Mount>> {
            override fun onResponse(call: Call<List<Mount>>?, response: Response<List<Mount>>?) {
                var mounts: List<Mount>? = null
                response?.body().let {
                    if (response?.code() == 200) {
                        mounts = response.body()
                        callbackMountsDeal?.resultMountsDeal(mounts, true)
                    } else {
                        callbackMountsDeal?.resultMountsDeal(null, false)
                    }
                }
            }

            override fun onFailure(call: Call<List<Mount>>?, t: Throwable?) {
                callbackMountsDeal?.resultMountsDeal(null, false)
            }

        })
    }

    fun getOneMount(id: String) {
        val call = api.getOneMount(id)
        call.enqueue(object : retrofit2.Callback<Mount> {
            override fun onResponse(call: Call<Mount>?, response: Response<Mount>?) {
                var mount: Mount? = null
                response?.body().let {
                    if (response?.code() == 200) {
                        mount = response.body()
                        callbackOneMount?.resultOneMount(mount, true)
                    } else {
                        callbackOneMount?.resultOneMount(null, false)
                    }
                }
            }

            override fun onFailure(call: Call<Mount>?, t: Throwable?) {
                callbackOneMount?.resultOneMount(null, false)
            }

        })
    }

    //----------------------------------callbacks-----------------------------------

    interface CallbackListDeals {
        fun resultList(listDeals: List<Deal>, result: Int, date: String, count: Int)
    }

    fun registerCallBack(callbackListDeals: CallbackListDeals?) {
        NetworkControllerDeals.callbackListDeals = callbackListDeals
    }

    interface PaginationCallback {
        fun resultPagination(listDeals: List<Deal>, result: Int)
    }

    fun registerPaginationCallback(callback: PaginationCallback?) {
        callbackPaginationListDeals = callback
    }

    interface OneDealCallback {
        fun resultOneDeal(deal: Deal?, boolean: Boolean)
    }

    fun registerOneDealCallback(callback: OneDealCallback?) {
        callbackOneDeal = callback
    }

    interface DiscountCallback {
        fun resultAddDiscount(boolean: Boolean)
    }

    fun registerDiscountCallback(callback: DiscountCallback?) {
        addDiscountCallback = callback
    }

    interface MountsDealCallback {
        fun resultMountsDeal(listMounts: List<Mount>?, boolean: Boolean)
    }

    fun registerMountsDealCallback(callback: MountsDealCallback?) {
        callbackMountsDeal = callback
    }

    interface MountCallback {
        fun resultMount(mount: Mount, boolean: Boolean)
    }

    fun registerMountCallback(callback: MountCallback?) {
        callbackMount = callback
    }

    interface OneMountCallback {
        fun resultOneMount(mount: Mount?, boolean: Boolean)
    }

    fun registerOneMountCallback(callback: OneMountCallback?) {
        callbackOneMount = callback
    }

}