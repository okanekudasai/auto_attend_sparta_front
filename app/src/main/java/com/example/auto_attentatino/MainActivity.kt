package com.example.auto_attentatino

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.auto_attentatino.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ButtonKakaoLogin.setOnClickListener {
            Log.d("Okane", "로그인 버튼이 눌렸어요")
            Log.d("Okane", "아이디 : " + binding.editTextKakaoId.text.toString())
            Log.d("Okane", "비밀번호 : " + binding.editTextKakaoPassword.text.toString())
            Log.d("Okane", "api_url : " + BuildConfig.SERVER_URL);

        }

    }
}