package com.example.auto_attentatino

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.auto_attentatino.api.dto.LoginDto
import com.example.auto_attentatino.api.service.LoginApi
import com.example.auto_attentatino.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS) // 읽기 Timeout 설정 (초 단위)
        .connectTimeout(60, TimeUnit.SECONDS) // 연결 Timeout 설정 (초 단위)
        .build()

    val retrofitForString: Retrofit = Retrofit.Builder()
//        .baseUrl(BuildConfig.SERVER_URL)
        .baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val retrofitForJson: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private lateinit var sharedPreference: SharedPreferences
    private fun setSharedPreference(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // 데이터 불러오기 함수
    private fun getSharedPreference(key: String, defaultValue: String): String {
        return sharedPreference.getString(key, defaultValue) ?: defaultValue
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreference = getSharedPreferences("sharedPreference", MODE_PRIVATE)
        val chromeIndex: String = getSharedPreference("chromeIndex", "-1")
        if (chromeIndex.toInt() == -1) {
            Log.d("Okane", "저장된 크롬인덱스가 없음")
            binding.loginLayout.visibility = View.VISIBLE
        } else {
            Log.d("Okane", "저장된 크롬인덱스 : ${chromeIndex}")
        }

        setListener();
    }

    fun setListener() {
        binding.ButtonKakaoLogin.setOnClickListener {
            Log.d("Okane", "로그인 버튼이 눌렸어요")
            Log.d("Okane", "아이디 : " + binding.editTextKakaoId.text.toString())
            Log.d("Okane", "비밀번호 : " + binding.editTextKakaoPassword.text.toString())
            val loginApi = retrofitForString.create(LoginApi::class.java)
//            val loginDto = LoginDto(binding.editTextKakaoId.text.toString(), binding.editTextKakaoPassword.text.toString())
            val loginDto = LoginDto("eeee", "fff")

            GlobalScope.launch(Dispatchers.IO) {
                val response = loginApi.logining(loginDto)
                if(response.isSuccessful) {
                    Log.d("Okane", response.body()!!)
                }
                Log.d("Okane", "2")
            }
            Log.d("Okane", "1")
        }

    }

}