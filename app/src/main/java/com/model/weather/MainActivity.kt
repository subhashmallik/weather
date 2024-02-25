package com.model.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.model.weather.ui.main.MainFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val x = 10382
        val str1 = x.toString().split("").toTypedArray()
        println(str1.asList());
        for (i in str1.indices) {
            println(str1[i])
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments: List<Fragment> = supportFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}