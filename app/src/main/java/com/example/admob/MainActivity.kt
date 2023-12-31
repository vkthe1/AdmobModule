package com.example.admob

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.admob.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.vk.adslib.AdMobUtils


class MainActivity : AppCompatActivity(), AdMobUtils.AdCallBack {

    private val mBinding:ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this,R.layout.activity_main)
    }
    var consentStatus = ConsentInformation.ConsentStatus.REQUIRED

    private lateinit var consentInformation: ConsentInformation
    private lateinit var consentForm: ConsentForm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.openAds.setOnClickListener {
            (application as AppDelegate).getAdmob().showOpenAds(this,this,true)
        }
        mBinding.bannerAds.setOnClickListener {
            (application as AppDelegate).getAdmob().showBannerAds(mBinding.llContainer,this,true)
        }
        mBinding.interstitial.setOnClickListener {
            (application as AppDelegate).getAdmob().showInterstitialAds(this,this,true)
        }
        mBinding.reward.setOnClickListener {
            (application as AppDelegate).getAdmob().showRewardAd(this,this,true)
        }
        mBinding.rewardInterstitial.setOnClickListener {
            (application as AppDelegate).getAdmob().showRewardInterstitialAds(this,this,true)
        }
        mBinding.nativeAdLarge.setOnClickListener {
            (application as AppDelegate).getAdmob().showNativeAds(mBinding.templateLarge,this,true)
        }
        mBinding.nativeAdSmall.setOnClickListener {
            (application as AppDelegate).getAdmob().showNativeAds(mBinding.templateSmall,this,true)
        }



    }

    override fun onAdsCallBack(isSuccess: Boolean) {
        Toast.makeText(this,"onAdsCallBack",Toast.LENGTH_LONG).show()
    }

    override fun adRewarded() {
        Toast.makeText(this,"adRewarded",Toast.LENGTH_LONG).show()
    }
}