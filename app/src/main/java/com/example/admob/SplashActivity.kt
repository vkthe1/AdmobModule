package com.example.admob

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.vk.adslib.AdMobUtils

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Handler(Looper.getMainLooper()).postDelayed({
//            (application as AppDelegate).getAdmob()
//                .checkConsent(this, object : AdMobUtils.AdShowCallBack {
//                    override fun bannerAdLoaded() {
//                    }
//
//                    override fun rectangleBannerAdLoaded() {
//                    }
//
//                    override fun nativeAdLoaded() {
//                    }
//
//                    override fun consentGiven() {
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                            finish()
                        }, 2000)
//                    }
//
//                })},1000)
    }

    }