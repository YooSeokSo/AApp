package com.example.aapp.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.aapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*Navigation을 세팅합니다. */
        NavigationUI.setupActionBarWithNavController(
            this, findNavController(R.id.navigation_host)
        )
    }

    /* AppBar(툴바)에서 뒤로가기 버튼(<-) 눌렀을 때,
      뒤로 이동하려면 onSupportNavigateUp를 오버라이드하여 구현  */
    override fun onSupportNavigateUp() = findNavController(R.id.navigation_host).navigateUp()
}

