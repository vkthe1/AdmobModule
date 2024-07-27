package com.example.admob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.admob.databinding.ActivityMainBinding
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.vk.adslib.AdMobUtils


class MainActivity : AppCompatActivity(), AdMobUtils.AdCallBack {

    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        (application as AppDelegate).getAdmob().checkConsent(this,object :AdMobUtils.AdShowCallBack{
            override fun bannerAdLoaded() {

            }

            override fun rectangleBannerAdLoaded() {
            }

            override fun nativeAdLoaded() {

            }

            override fun consentGiven() {
                Toast.makeText(this@MainActivity,"Consent Given",Toast.LENGTH_LONG).show()
            }

        })

        mBinding.openAds.setOnClickListener {
            (application as AppDelegate).getAdmob().showOpenAds(this, this, true)
        }
        mBinding.bannerAds.setOnClickListener {
            (application as AppDelegate).getAdmob().showBannerAds(mBinding.llContainer, this, true)
        }
        mBinding.interstitial.setOnClickListener {
            (application as AppDelegate).getAdmob().showInterstitialAds(this, this, true)
        }
        mBinding.reward.setOnClickListener {
            (application as AppDelegate).getAdmob().showRewardAd(this, this, true)
        }
        mBinding.rewardInterstitial.setOnClickListener {
            (application as AppDelegate).getAdmob().showRewardInterstitialAds(this, this, true)
        }
        mBinding.nativeAdLarge.setOnClickListener {
            (application as AppDelegate).getAdmob()
                .showNativeAds(mBinding.templateLarge, this, true)
        }
        mBinding.nativeAdSmall.setOnClickListener {
            (application as AppDelegate).getAdmob()
                .showNativeAds(mBinding.templateSmall, this, true)
        }


    }

    override fun onAdsCallBack(isSuccess: Boolean) {
        Toast.makeText(this, "onAdsCallBack", Toast.LENGTH_LONG).show()
    }

    override fun adRewarded() {
        Toast.makeText(this, "adRewarded", Toast.LENGTH_LONG).show()
    }
}