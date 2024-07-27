package com.vk.adslib

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.vk.adslib.template.TemplateView
import com.vk.adslib.template.TemplateViewSmall


class AdMobUtils(
    val mContext: Context,
    val admobIds: HashMap<String, ArrayList<String>>,
    val testIds: ArrayList<String>? = null,
    val isDesignedForFamily: Boolean = false,
    val needToShowAds: Boolean = true,
    val isPortrait: Boolean = true,
    val testAdsEnable: Boolean = true
) {
    val TAG = "vk"
    private val isConcent = false;

    interface AdShowCallBack {
        fun bannerAdLoaded()
        fun rectangleBannerAdLoaded()
        fun nativeAdLoaded()
        fun consentGiven()
    }

    interface AdsNextRequestCallBack {
        fun bannerAdRequested(id: String)
        fun interstitialAdRequested(id: String)
        fun openAdRequested(id: String)
        fun rewardAdRequested(id: String)
        fun rewardInterstitialAdRequested(id: String)
        fun nativeAdRequested(id: String)
        fun rectangleAdRequested(id: String)
    }

    interface AdsFailCallBack {
        fun BannerAdFailed()
        fun InterstitialAdFailed()
        fun OpenAdFailed()
        fun RewardAdFailed()
        fun RewardInterstitialAdFailed()
        fun NativeAdFailed()
        fun RectangleAdFailed()
    }

    private lateinit var consentInformation: ConsentInformation
    private lateinit var consentForm: ConsentForm
    private var interstitialDialog: Dialog? = null
    private var rewardInterstitialDialog: Dialog? = null
    private var rewardAdDialog: Dialog? = null
    private var adsRequestCallback: AdsNextRequestCallBack? = null
    private var adsShowCallback: AdShowCallBack? = null
    private var adsFailedCallback: AdsFailCallBack? = null
    private var appOpenAd: AppOpenAd? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var mAdView: AdView? = null
    private var mAdRectangleView: AdView? = null
    private var mNativeAd: NativeAd? = null
    private var bannerAdLoaded = false
    private var rectangleAdLoaded = false

    private var isAppPurchased = false
    private var isShowingAd = false
    private var bannerAdSize = AdSize.FULL_BANNER
    var bannerIds = ArrayList<String>()
    var rectangleAdIds = ArrayList<String>()
    var interstitialAdIds = ArrayList<String>()
    var rewardInterstitialAdIds = ArrayList<String>()
    var rewardAdIds = ArrayList<String>()
    var nativeAdIds = ArrayList<String>()
    var openAdIds = ArrayList<String>()
    var consentStatus = ConsentInformation.ConsentStatus.UNKNOWN
    var openAdIndex = 0
    var bannerIndex = 0
    var rectangleIndex = 0
    var interstitialIndex = 0
    var rewardedInterstitialIndex = 0
    var rewardIndex = 0
    var nativeIndex = 0
    fun setBannerAdSize(size: AdSize) {
        bannerAdSize = size
    }

    init {
        callAllAdsRequest()
    }

    fun checkConsent(activity: Activity, listener: AdShowCallBack) {
        val debugSettings = if (BuildConfig.DEBUG) ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(AdRequest.DEVICE_ID_EMULATOR)
            .addTestDeviceHashedId("9DD45FD0EBCEC4A149AB3409D8065CF2")
            .addTestDeviceHashedId("682AA2E1A96803EDDF5462A6660AC976")
            .addTestDeviceHashedId("6FC9DD857568713E0433E9EB514D6653")
            .addTestDeviceHashedId("1DEFC50DE26ACD8E2883FF561A7CF508")
            .addTestDeviceHashedId("51743A5BD822D669ED5F76955964F5A4")
            .build()
        else ConsentDebugSettings.Builder(activity).build()


        val params = if (testAdsEnable) {
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(isDesignedForFamily)
                .setConsentDebugSettings(debugSettings)
                .build()
        } else {
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(isDesignedForFamily)
                .build()
        }

        consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                // The consent information state was updated.
                // You are now ready to check if a form is available.
                consentStatus = consentInformation.consentStatus
                if (consentInformation.isConsentFormAvailable) {
                    loadForm(activity, listener)
                }

                if (consentStatus != ConsentInformation.ConsentStatus.REQUIRED) {
                    listener.consentGiven()
                    callAllAdsRequest()
                }
            },
            {
                // Handle the error.
                listener.consentGiven()
                logText(it.message)
            })
    }

    private fun loadForm(activity: Activity, listener: AdShowCallBack) {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(
            activity,
            {
                this.consentForm = it
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        activity,
                        ConsentForm.OnConsentFormDismissedListener {
                            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
                                // App can start requesting ads.
                                consentStatus = consentInformation.consentStatus
                                logText("Consent $consentStatus")
                                listener.consentGiven()
                                callAllAdsRequest()
                                return@OnConsentFormDismissedListener
                            }

                            // Handle dismissal by reloading form.
                            loadForm(activity, listener)

                        }
                    )
                }
            },
            {
                // Handle the error.
            }
        )
    }

    fun setIsAppPurchase(showAds: Boolean) {
        isAppPurchased = showAds
    }


    init {
//        callAllAdsRequest()
    }

    fun setAdRequestCallBack(listener: AdsNextRequestCallBack) {
        adsRequestCallback = listener
    }

    fun setAdsLoadedCallBack(listener: AdShowCallBack) {
        adsShowCallback = listener
    }

    fun setAdsCallBackListener(lister: AdsFailCallBack) {
        adsFailedCallback = lister
    }

    /*** This Method call for ads call request which you are implemented ***/
    fun callAllAdsRequest() {
        if (needToShowAds && !isAppPurchased) {
            MobileAds.initialize(
                mContext
            ) {
                val requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                if (isDesignedForFamily) {
                    requestConfiguration.setTagForChildDirectedTreatment(
                        RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
                    )
                    requestConfiguration.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                }
                testIds?.let {
                    requestConfiguration.setTestDeviceIds(it)
                }
                MobileAds.setRequestConfiguration(requestConfiguration.build())

                if (admobIds.containsKey(OPEN_AD_ID)) {
                    openAdIds = admobIds[OPEN_AD_ID]!!
//                    loadOpenAds(openAdIds[openAdIndex])
                }

                if (admobIds.containsKey(BANNER_AD_ID)) {
                    bannerIds = admobIds[BANNER_AD_ID]!!
//                        loadBannerAds(bannerIds[bannerIndex])
                }


                if (admobIds.containsKey(INTERSTITIAL_AD_ID)) {
                    interstitialAdIds = admobIds[INTERSTITIAL_AD_ID]!!
//                    loadInterstitialAds(interstitialAdIds[interstitialIndex])
                }

                if (admobIds.containsKey(NATIVE_ADVANCE_AD_ID)) {
                    nativeAdIds = admobIds[NATIVE_ADVANCE_AD_ID]!!
//                    loadNativeAdvancedAds(nativeAdIds[nativeIndex])
                }

                if (admobIds.containsKey(RECTANGLE_AD_ID)) {
                    rectangleAdIds = admobIds[RECTANGLE_AD_ID]!!
//                    loadRectangleAds(rectangleAdIds[rectangleIndex])
                }

                if (admobIds.containsKey(REWARD_INTERSTITIAL_AD_ID)) {
                    rewardInterstitialAdIds = admobIds[REWARD_INTERSTITIAL_AD_ID]!!
//                    loadRewardInterstitialAds(rewardInterstitialAdIds[rewardedInterstitialIndex])
                }

                if (admobIds.containsKey(REWARD_AD_ID)) {
                    rewardAdIds = admobIds[REWARD_AD_ID]!!
//                    loadRewardAds(rewardAdIds[rewardIndex])
                }


            }
        }
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }


    interface AdCallBack {
        fun onAdsCallBack(isSuccess: Boolean)
        fun adRewarded()
    }


    /*** Open Ads Module ***/

    fun showOpenAds(activity: Activity, listener: AdCallBack, isPreRequest: Boolean = true) {
        if (!admobIds.containsKey(OPEN_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }
        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (openAdIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }

//        Handler(Looper.getMainLooper()).postDelayed({
//            if (openAdDialog == null) {
//                openAdDialog = Dialog(activity)
//                openAdDialog?.let {
//                    it.setContentView(R.layout.loading_dialog)
//                    it.setCancelable(false)
//                    it.setCanceledOnTouchOutside(false)
//                    it.show()
//                }
//            }
//        },500)
        Handler(Looper.getMainLooper()).postDelayed({
            AppOpenAd.load(
                mContext, openAdIds[openAdIndex], getAdRequest(),
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        logText("$openAdIndex Open Ad Failed")
                        appOpenAd = null
                        if (openAdIndex == openAdIds.size - 1) {
                            adsFailedCallback?.OpenAdFailed()
//                            if (openAdDialog?.isShowing == true) {
//                                openAdDialog?.dismiss()
//                                openAdDialog = null
//                            }
                            listener.onAdsCallBack(false)
                            return
                        }
                        openAdIndex++
                        logText("$openAdIndex Open Ad Next Index")
                        showOpenAds(activity, listener, false)
                        adsRequestCallback?.openAdRequested(openAdIndex.toString())
                    }

                    override fun onAdLoaded(p0: AppOpenAd) {
                        super.onAdLoaded(p0)
//                        if (openAdDialog?.isShowing == true) {
//                            openAdDialog?.dismiss()
//                            openAdDialog = null
//                        }
                        appOpenAd = p0
                        appOpenAd?.let {
                            if (isShowingAd)
                                return@let
                            val fullScreenContentCallback: FullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        // Set the reference to null so isAdAvailable() returns false.
                                        appOpenAd = null
                                        isShowingAd = false
                                        listener.onAdsCallBack(true)

                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        isShowingAd = false
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        isShowingAd = true
                                    }
                                }

                            appOpenAd?.let {
                                it.fullScreenContentCallback = fullScreenContentCallback
                                it.show(activity)
                            }
                        }
                    }
                })

        }, 1000)

    }


    /*** Interstitial Ads ***/

    fun getAdsHeight(): Int {
        return mAdView?.let {
            mAdView!!.height
        } ?: kotlin.run { 0 }
    }

    /*** Interstitial AD ***/
    fun showInterstitialAds(
        activity: Activity,
        listener: AdCallBack,
        isPreRequest: Boolean = true
    ) {
        if (!admobIds.containsKey(INTERSTITIAL_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }

        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (interstitialAdIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }
        if (interstitialDialog == null) {
            interstitialDialog = Dialog(activity)
            interstitialDialog?.let {
                it.setContentView(R.layout.loading_dialog)
                it.setCancelable(false)
                it.setCanceledOnTouchOutside(false)
                it.show()
            }
        }

        logText("Interstitial Ad $interstitialIndex")
        Handler(Looper.getMainLooper()).postDelayed({
            InterstitialAd.load(
                activity,
                interstitialAdIds[interstitialIndex],
                getAdRequest(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        logText("$interstitialIndex Interstitial Ad Failed")
                        mInterstitialAd = null
                        if (interstitialIndex == interstitialAdIds.size - 1) {
                            adsFailedCallback?.InterstitialAdFailed()
                            if (interstitialDialog?.isShowing == true)
                                interstitialDialog?.dismiss()
                            interstitialDialog = null
                            listener.onAdsCallBack(false)
                            return
                        }
                        interstitialIndex++
                        logText("$interstitialIndex Interstitial Next Index")
                        if (interstitialAdIds.isNotEmpty())
                            showInterstitialAds(activity, listener)
//                    loadInterstitialAds(interstitialAdIds[interstitialIndex])
                        adsRequestCallback?.interstitialAdRequested(interstitialAdIds.toString())
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                        if (interstitialDialog?.isShowing == true)
                            interstitialDialog?.dismiss()
                        interstitialDialog = null
                        mInterstitialAd?.let {
                            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    isShowingAd = false
                                    listener.onAdsCallBack(true)
//                            if (isPreRequest && admobIds.containsKey(INTERSTITIAL_AD_ID))
//                                if (interstitialAdIds.isNotEmpty())
//                                    loadInterstitialAds(interstitialAdIds[interstitialIndex])
                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    super.onAdFailedToShowFullScreenContent(p0)
                                    isShowingAd = false
                                }

                                override fun onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent()
                                    isShowingAd = true
                                }
                            }
                            it.show(activity)
                        }
                    }
                })
        }, 500)
    }

    fun logText(str: String) {
        if (BuildConfig.DEBUG)
            Log.e("VK", str)
    }

    /*** BANNER AD ***/

    fun showBannerAds(
        viewContainer: ViewGroup,
        listener: AdCallBack,
        isPreRequest: Boolean = true
    ) {
        if (!admobIds.containsKey(BANNER_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }

        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (bannerIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }


        if (mAdView == null) {
            mAdView = AdView(mContext)
            mAdView?.let {
                it.setAdSize(bannerAdSize)
                it.adUnitId = bannerIds[bannerIndex]
                it.loadAd(getAdRequest())
                it.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        bannerAdLoaded = true
                        adsShowCallback?.bannerAdLoaded()
                        if (mAdView != null && bannerAdLoaded) {
                            mAdView?.let { parentView ->
                                if (parentView.parent != null) {
                                    (parentView.parent as ViewGroup).removeView(mAdView)
                                }
                                viewContainer.addView(mAdView)
                                listener.onAdsCallBack(true)
                            }
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                        logText("$bannerIndex Banner Ad Failed")
                        bannerAdLoaded = false
                        if (bannerIndex == bannerIds.size - 1) {
                            adsFailedCallback?.BannerAdFailed()
                            return
                        }
                        bannerIndex++
                        mAdView = null
                        logText("$bannerIndex Banner Next Index")
                        showBannerAds(viewContainer, listener, isPreRequest)
                        adsRequestCallback?.bannerAdRequested(bannerIndex.toString())
                    }

                    override fun onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    override fun onAdClosed() {
                        bannerAdLoaded = false
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                    }
                }
            }
        } else if (bannerAdLoaded) {
            mAdView?.let { parentView ->
                if (parentView.parent != null) {
                    (parentView.parent as ViewGroup).removeView(mAdView)
                }
                viewContainer.addView(mAdView)
                listener.onAdsCallBack(true)
            }
        }

    }


    /*** Native Ad ***/
    fun showNativeAds(view: FrameLayout, listener: AdCallBack, isPreRequest: Boolean = true) {
        if (!admobIds.containsKey(NATIVE_ADVANCE_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }
        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (nativeAdIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }


        if (mNativeAd == null) {
            val adLoader = AdLoader.Builder(mContext, nativeAdIds[nativeIndex])
                .forNativeAd { ad: NativeAd ->
                    // Show the ad.
                    mNativeAd = ad
                    adsShowCallback?.nativeAdLoaded()
                    mNativeAd?.let { ad ->
                        if (view is TemplateView) {
                            view.visibility = View.VISIBLE
                            view.setNativeAd(ad)
                        } else if (view is TemplateViewSmall) {
                            view.visibility = View.VISIBLE
                            view.setNativeAd(ad)
                        }
                        listener.onAdsCallBack(true)
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        mNativeAd = null
                        logText("$nativeIndex Native Ad Failed")
                        if (nativeIndex == nativeAdIds.size - 1) {
                            adsFailedCallback?.NativeAdFailed()
                            return
                        }
                        nativeIndex++
                        logText("$nativeIndex Native Ad Next Index")
                        showNativeAds(view, listener)
                        adsRequestCallback?.nativeAdRequested(nativeIndex.toString())
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder().build()
                )
                .build()
            adLoader.loadAd(getAdRequest())
        } else {
            mNativeAd?.let { ad ->
                if (view is TemplateView) {
                    view.visibility = View.VISIBLE
                    view.setNativeAd(ad)
                } else if (view is TemplateViewSmall) {
                    view.visibility = View.VISIBLE
                    view.setNativeAd(ad)
                }
                listener.onAdsCallBack(true)
            }
        }

    }


    /*** RECTANGLE_AD_ID ****/

    fun showRectangleAds(
        viewContainer: ViewGroup,
        listener: AdCallBack,
        isPreRequest: Boolean = true
    ) {
        if (!admobIds.containsKey(RECTANGLE_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }

        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (rectangleAdIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }

        if (mAdRectangleView == null) {
            mAdRectangleView = AdView(mContext)
            mAdRectangleView?.let {
                it.setAdSize(AdSize.MEDIUM_RECTANGLE)
                it.adUnitId = rectangleAdIds[rectangleIndex]
                it.loadAd(getAdRequest())
                it.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        // Code to be executed when an ad finishes loading.
                        rectangleAdLoaded = true
                        adsShowCallback?.rectangleBannerAdLoaded()
                        mAdRectangleView?.let { parentView ->
                            try {
                                if (parentView.parent != null) {
                                    (parentView.parent as ViewGroup).removeView(mAdRectangleView)
                                }
                                viewContainer.addView(mAdRectangleView)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            listener.onAdsCallBack(true)
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Code to be executed when an ad request fails.
                        rectangleAdLoaded = false
                        logText("$rectangleIndex Rectangle Ad Failed")
                        if (rectangleIndex == rectangleAdIds.size - 1) {
                            adsFailedCallback?.RectangleAdFailed()
                            return
                        }
                        rectangleIndex++
                        mAdRectangleView = null
                        logText("$rectangleIndex Rectangle Ad Next Index")
                        showRectangleAds(viewContainer, listener)
                        adsRequestCallback?.rectangleAdRequested(rectangleIndex.toString())
                    }

                    override fun onAdOpened() {
                        // Code to be executed when an ad opens an overlay that
                        // covers the screen.
                    }

                    override fun onAdClicked() {
                        // Code to be executed when the user clicks on an ad.
                    }

                    override fun onAdClosed() {
                        rectangleAdLoaded = false
                        // Code to be executed when the user is about to return
                        // to the app after tapping on an ad.
                    }
                }
            }
        } else if (rectangleAdLoaded) {
            mAdRectangleView?.let {
                try {
                    if (it.parent != null) {
                        (it.parent as ViewGroup).removeView(mAdRectangleView)
                    }
                    viewContainer.addView(mAdRectangleView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                listener.onAdsCallBack(true)
            }
        }

    }


    /*** REWARD_INTERSTITIAL_AD_ID ***/
    fun showRewardInterstitialAds(
        activity: Activity,
        listener: AdCallBack,
        isPreRequest: Boolean = true
    ) {
        if (!admobIds.containsKey(REWARD_INTERSTITIAL_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }

        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (rewardInterstitialAdIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }
        if (rewardInterstitialDialog == null) {
            rewardInterstitialDialog = Dialog(activity)
            rewardInterstitialDialog?.let {
                it.setContentView(R.layout.loading_dialog)
                it.setCancelable(false)
                it.setCanceledOnTouchOutside(false)
                it.show()
            }
        }
        logText("Reward Interstitial $rewardedInterstitialIndex")

        Handler(Looper.getMainLooper()).postDelayed({
            RewardedInterstitialAd.load(mContext,
                rewardInterstitialAdIds[rewardedInterstitialIndex],
                AdRequest.Builder().build(),
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedInterstitialAd) {
                        rewardedInterstitialAd = ad
                        if (rewardInterstitialDialog?.isShowing == true)
                            rewardInterstitialDialog?.dismiss()
                        rewardInterstitialDialog = null
                        rewardedInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdClicked() {
                                    // Called when a click is recorded for an ad.
                                    logText("Ad was clicked.")
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    logText("Ad dismissed fullscreen content.")
                                    rewardedInterstitialAd = null
                                    isShowingAd = false
                                    listener.onAdsCallBack(true)
//                                if (isPreRequest && admobIds.containsKey(INTERSTITIAL_AD_ID))
//                                    if (rewardInterstitialAdIds.isNotEmpty())
//                                        loadInterstitialAds(rewardInterstitialAdIds[rewardedInterstitialIndex])

                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                    // Called when ad fails to show.
                                    logText("Ad failed to show fullscreen content.")
                                    rewardedInterstitialAd = null
                                }

                                override fun onAdImpression() {
                                    // Called when an impression is recorded for an ad.
                                    logText("Ad recorded an impression.")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    isShowingAd = true
                                    logText("Ad showed fullscreen content.")
                                }
                            }
                        rewardedInterstitialAd?.let {
                            it.show(activity) {
                                listener.adRewarded()
                            }
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        logText("$rewardedInterstitialIndex Interstitial Reward Failed")
                        rewardedInterstitialAd = null
                        if (rewardedInterstitialIndex == rewardInterstitialAdIds.size - 1) {
                            adsFailedCallback?.RewardInterstitialAdFailed()
                            if (rewardInterstitialDialog?.isShowing == true)
                                rewardInterstitialDialog?.dismiss()
                            rewardInterstitialDialog = null
                            listener.onAdsCallBack(false)
                            return
                        }
                        rewardedInterstitialIndex++
                        logText("$rewardedInterstitialIndex Interstitial Reward Next Index")
                        if (rewardInterstitialAdIds.isNotEmpty())
                            showRewardInterstitialAds(activity, listener)
                        adsRequestCallback?.rewardInterstitialAdRequested(rewardedInterstitialIndex.toString())
                    }
                })
        }, 500)
    }


    /*** REWARD_AD_ID ***/
    fun showRewardAd(activity: Activity, listener: AdCallBack, isPreRequest: Boolean = true) {
        if (!admobIds.containsKey(REWARD_AD_ID)) {
            listener.onAdsCallBack(false)
            return
        }

        if (consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            listener.onAdsCallBack(false)
            return
        }

        if (rewardAdIds.isEmpty()) {
            listener.onAdsCallBack(false)
            return
        }

        if (isAppPurchased) {
            listener.onAdsCallBack(false)
            return
        }
        if (rewardAdDialog == null) {
            rewardAdDialog = Dialog(activity)
            rewardAdDialog?.let {
                it.setContentView(R.layout.loading_dialog)
                it.setCancelable(false)
                it.setCanceledOnTouchOutside(false)
                it.show()
            }
        }

        logText("Reward Ad $rewardIndex")
//        Toast.makeText(mContext, "Reward Ad $rewardIndex", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            RewardedAd.load(
                mContext,
                rewardAdIds[rewardIndex],
                getAdRequest(),
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mRewardedAd = null
                        logText("$rewardIndex Reward Ad Failed")
                        if (rewardIndex == rewardAdIds.size - 1) {
                            adsFailedCallback?.RewardAdFailed()
                            if (rewardAdDialog?.isShowing == true)
                                rewardAdDialog?.dismiss()
                            rewardAdDialog = null
                            listener.onAdsCallBack(false)
                            return
                        }
                        rewardIndex++
                        logText("$rewardIndex Reward Ad Next Index")
                        if (rewardAdIds.isNotEmpty())
                            showRewardAd(activity, listener)
                        adsRequestCallback?.rewardAdRequested(rewardIndex.toString())
                    }

                    override fun onAdLoaded(rewardedAd: RewardedAd) {
                        if (rewardAdDialog?.isShowing == true)
                            rewardAdDialog?.dismiss()
                        rewardAdDialog = null
                        mRewardedAd = rewardedAd
                        mRewardedAd?.let {
                            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                                override fun onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    isShowingAd = true
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                    // Called when ad fails to show.
                                }

                                override fun onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    mRewardedAd = null
                                    isShowingAd = false
                                    listener.onAdsCallBack(true)
                                }
                            }
                            mRewardedAd?.show(activity) {
                                listener.adRewarded()
                            }
                        }
                    }
                })

        }, 500)

    }


    companion object {
        const val OPEN_AD_ID = "OpenAdID"
        const val BANNER_AD_ID = "BannerOpenAdID"
        const val RECTANGLE_AD_ID = "RectangleOpenAdID"
        const val INTERSTITIAL_AD_ID = "InterstitialAdID"
        const val REWARD_INTERSTITIAL_AD_ID = "RewardInterstitialAdID"
        const val REWARD_AD_ID = "RewardAdID"
        const val NATIVE_ADVANCE_AD_ID = "NativeAdvanceID"

    }
}