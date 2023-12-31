package com.example.admob

import android.app.Application
import com.vk.adslib.AdMobUtils

class AppDelegate : Application(){

    private var admobUtils : AdMobUtils?=null

    fun getAdmob(): AdMobUtils {
        return admobUtils!!
    }

    private var isFromBackground:Boolean=false

    override fun onCreate() {
        super.onCreate()
        val map=HashMap<String,ArrayList<String>>()
        map[AdMobUtils.OPEN_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/3419835294",)
        map[AdMobUtils.BANNER_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/6300978111",)
        map[AdMobUtils.RECTANGLE_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/6300978111",)
        map[AdMobUtils.INTERSTITIAL_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/1033173712",)
        map[AdMobUtils.NATIVE_ADVANCE_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/2247696110")
        map[AdMobUtils.REWARD_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/5224354917")
        map[AdMobUtils.REWARD_INTERSTITIAL_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/5354046379")
        val ids=ArrayList<String>()
//        ids.add("682AA2E1A96803EDDF5462A6660AC976")
        admobUtils= AdMobUtils(this,map, needToShowAds = true, testIds = ids, isDesignedForFamily = false, isPortrait = true, testAdsEnable = BuildConfig.DEBUG)
//        admobUtils?.setBannerAdSize(AdSize.SMART_BANNER)
//      admobUtils?.setIsAppPurchase(true)
    }
}