package com.example.eightwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rView: RecyclerView = findViewById<RecyclerView>(R.id.r_view)
        rView.layoutManager = LinearLayoutManager(this)
        val daysApi = RetrofitHelper.getInstance().create(DayGetter::class.java)

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler){
            val days = daysApi.check()

            withContext(Dispatchers.Main){
                if(days.body() != null){
                    Log.d("Days go by", days.body().toString())
                    val adapter : DayListAdapter = DayListAdapter()
                    adapter.submitList(days.body()?.list?.toMutableList())
                    rView.adapter = adapter
                }
            }
        }

    }
}

interface DayGetter {
    @GET("forecast?q=Shklov,by&appid=${BuildConfig.API_KEY_OPEN_WEATHER_MAP}&units=metric")
    suspend fun check() : Response<DataResponce>
}

object RetrofitHelper {
    val baseUrl = "http://api.openweathermap.org/data/2.5/"
    fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

data class Main(
    @SerializedName("temp") val temp: Double
){
    fun getTempAsString() : String{
        return "${temp}° C"
    }
}
data class Weather(
    @SerializedName("main") val main: String,
    @SerializedName("icon") val icon: String
)
data class DayPrognosis (
    @SerializedName("dt_txt") val dt_txt: String,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: ArrayList<Weather>
)

data class DataResponce(
    @SerializedName("list") val list: ArrayList<DayPrognosis>
)