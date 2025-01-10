package com.example.eightwork


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {
    private val DATA_KEY = "DATA_KEY"
    private lateinit var dataResponce: DataResponce
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setTitle("Shklov")

        val recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.r_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        val daysApi = RetrofitHelper.getInstance().create(DayGetter::class.java)

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        if(savedInstanceState == null){

            GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler){
                val days = daysApi.check()

                withContext(Dispatchers.Main){
                    if(days.body() != null){
                        dataResponce = days.body()!!
                        Log.i("Запрос", days.body().toString())
                        val adapter = DayListAdapter()
                        adapter.submitList(dataResponce.list.toMutableList())
                        recyclerView.adapter = adapter
                    }
                }
            }
        }
        else {
            val jsonText = savedInstanceState.getString(DATA_KEY)
            var gson = Gson()
            dataResponce = gson.fromJson(jsonText, DataResponce::class.java)
            val adapter = DayListAdapter()
            adapter.submitList(dataResponce.list.toMutableList())
            recyclerView.adapter = adapter
            Log.i("savedInstanceState", dataResponce.toString())
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val gson = Gson()
        val jsonText = gson.toJson(dataResponce)
        outState.putString(DATA_KEY, jsonText)

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