package com.example.auto_attentatino

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.auto_attentatino.api.service.TestApi
import com.example.auto_attentatino.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        print("실행!")

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofitForString: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
//            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val retrofitForJson: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        binding.ButtonKakaoLogin.setOnClickListener {
            Log.d("Okane", "로그인 버튼이 눌렸어요")
            Log.d("Okane", "아이디 : " + binding.editTextKakaoId.text.toString())
            Log.d("Okane", "비밀번호 : " + binding.editTextKakaoPassword.text.toString())
            Log.d("Okane", "api_url : " + BuildConfig.SERVER_URL);

            val testApiService: TestApi = retrofitForString.create(TestApi::class.java)
            testApiService.testing().enqueue(object: Callback<String> {

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val res:String? = response.body()
                    Log.d("Okane", "testRes : " + res);
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("Okane", "실패" + t.toString());
                }

            })
        }
    }
}