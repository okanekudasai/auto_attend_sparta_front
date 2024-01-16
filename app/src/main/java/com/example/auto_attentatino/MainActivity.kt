package com.example.auto_attentatino

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auto_attentatino.api.dto.LoginDto
import com.example.auto_attentatino.api.service.Api
import com.example.auto_attentatino.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    fun log(msg: String) {
        Log.d("Okane", msg)
    }


    private lateinit var binding: ActivityMainBinding

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(66666, TimeUnit.SECONDS) // 읽기 Timeout 설정 (초 단위)
        .connectTimeout(66666, TimeUnit.SECONDS) // 연결 Timeout 설정 (초 단위)
        .build()
    val api = Retrofit.Builder()
        .baseUrl(BuildConfig.SERVER_URL)
//        .baseUrl("http://10.0.2.2:5000/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build().create(Api::class.java)

    private lateinit var sharedPreference: SharedPreferences
    private fun setSharedPreference(key: String, value: String) {
        val editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.apply()
    }
    private fun loadSharedPreference(key: String, defaultValue: String): String {
        return sharedPreference.getString(key, defaultValue) ?: defaultValue
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        GlobalScope.launch(Dispatchers.IO) {
            var response = api.isChromeOpen()
            if(response.isSuccessful) {
                var isChromeOpen = response.body()!!
                log("크롬이 켜져 있나요? : "  + isChromeOpen)
                if (isChromeOpen == "True") {
                    withContext(Dispatchers.Main) {
                        binding.logoutLayout.visibility = View.VISIBLE
                        showToast(this@MainActivity, "로그인 되어 있네요!")
                    }
                    api.makeSchedule()
                } else {
                    log("안켜져있어요")
                    withContext(Dispatchers.Main) {
                        binding.loginLayout.visibility = View.VISIBLE
                        showToast(this@MainActivity, "로그인 되어 있지 않아요!")
                    }
                }
            }
        }

        setListener();
    }

    fun setListener() {

        binding.ButtonKakaoLogin.setOnClickListener {
            hideKeyboard()
            binding.loginLayout.visibility = View.GONE
            binding.pendingLayout.visibility = View.VISIBLE

            log("로그인 버튼이 눌렸어요")
            log("아이디 : " + binding.editTextKakaoId.text.toString())
            log("비밀번호 : " + binding.editTextKakaoPassword.text.toString())
            val loginDto = LoginDto(binding.editTextKakaoId.text.toString(), binding.editTextKakaoPassword.text.toString())
            GlobalScope.launch(Dispatchers.IO) {
                val loginResponse = api.logining(loginDto)
                log("로그인을 시도할게요")
                if(loginResponse.isSuccessful) {
                    log("로그인 시도 결과를 알려드릴게요")
                    var loginResult: String = loginResponse.body()!!
                    if (loginResult == "성공") {
                        log ("로그인 성공!")
                        withContext(Dispatchers.Main) {
                            binding.pendingLayout.visibility = View.GONE
                            binding.logoutLayout.visibility = View.VISIBLE
                            showToast(this@MainActivity, "로그인에 성공했어요")
                        }
                        api.makeSchedule().enqueue(object: Callback<String> {
                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {
                            }
                        })

                    } else {
                        log("로그인 실패 : " + loginResult)
                        withContext(Dispatchers.Main) {
                            binding.pendingLayout.visibility = View.GONE
                            binding.loginLayout.visibility = View.VISIBLE
                            showToast(this@MainActivity, loginResult)
                        }
                    }
                }
            }

        }
        binding.logoutButton.setOnClickListener {
            binding.logoutLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
            showToast(this@MainActivity, "로그아웃 되었어요")
            GlobalScope.launch(Dispatchers.IO) {
                var response = api.isChromeOpen()
                if(response.isSuccessful) {
                    var isChromeOpen = response.body()!!
                    if (isChromeOpen == "True") {
                        api.logouting()
                    }
                }
            }
        }
    }
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun hideKeyboard() {
        var view = this.currentFocus
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}