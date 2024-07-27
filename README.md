# AdmobModule

## Add Dependency
```
dependencies 
{
        implementation 'com.github.vkthe1:AdmobModule:V1.0.0'
}
```

## Initialize AdmobModule

Create Application class and init module from onCreate of it.

```
    //Create a map containing ads ids you need to use in your app
    val map=HashMap<String,ArrayList<String>>()
        map[AdMobUtils.OPEN_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/3419835294",)
        map[AdMobUtils.BANNER_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/6300978111",)
        map[AdMobUtils.RECTANGLE_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/6300978111",)
        map[AdMobUtils.INTERSTITIAL_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/1033173712",)
        map[AdMobUtils.NATIVE_ADVANCE_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/2247696110")
        map[AdMobUtils.REWARD_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/5224354917")
        map[AdMobUtils.REWARD_INTERSTITIAL_AD_ID]= arrayListOf("ca-app-pub-3940256099942544/5354046379")
        

    //Set Device Test Id to set test ad request
    val ids=ArrayList<String>()
        ids.add("57596A06DC12378E6265FDCEFB5809D8")
        ids.add("B315B985E4776F85B49A8D8E50896357")

    //You can enable test ad by passing testAdsEnable = true

    //You can enable test ad by passing isDesignedForFamily = true if your app/game is targeting child.

    //You can enable test ad by passing isDesignedForFamily = false if your app/game is targeting adult.

    //Initialize Module
        admobUtils= AdMobUtils(this,map, needToShowAds = true, testIds = ids, isDesignedForFamily = true/false, testAdsEnable = !BuildConfig.DEBUG)

    // Set Banner Ad Size
        admobUtils?.setBannerAdSize(AdSize.BANNER)

    // You can directly stop initialization of Admob by passing needToShowAds = false while initializing module
    // Set Purchase boolean if App has purchasing option for removing ads runtime also you need to set true false while initializing module
        admobUtils?.setIsAppPurchase(true/false) -> True - If Purchase/ False - If Not Purchased
```

## Method call for different ads from Activity/Fragment

```
    //Show Open Ad
    (application as ApplicationClass).getAdmob().showOpenAds(activity/fragment,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }
 
        override fun onAdsCallBack(isSuccess: Boolean) {
        }
 
        })

    //Show Banner Ad LinearLayout -> llContainer where you need to add banner
    //llContainer is the layout in which you need to show Banner ad.
    (application as ApplicationClass).getAdmob().showBannerAds(llContainer,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }
    
        override fun onAdsCallBack(isSuccess: Boolean) {
        }
    
        })

    //Show Rectangle Ad LinearLayout -> llContainer where you need to add banner 
    //llContainer is the layout in which you need to show Rectangle ad.
    (application as ApplicationClass).getAdmob().showRectangleAds(llContainer,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }
    
        override fun onAdsCallBack(isSuccess: Boolean) {
        }
    
        })

    //Show Interstitial Ad
    (application as ApplicationClass).getAdmob().showInterstitialAds(activity/fragment,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }
       
        override fun onAdsCallBack(isSuccess: Boolean) {
        }
      
        })

    //Show Reward Ad
    (application as ApplicationClass).getAdmob().showRewardAd(activity/fragment,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }
    
        override fun onAdsCallBack(isSuccess: Boolean) {
        }

    })
    //Show Native Ad Large Template Layout 
    (application as ApplicationClass).getAdmob().showNativeAds(templateLarge,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }
            
        override fun onAdsCallBack(isSuccess: Boolean) {
        }
            
        })
    //Show Banner Ad Small in TemplateSmall Layout 
    (application as ApplicationClass).getAdmob().showNativeAds(templateSmall,object:AdMobUtils.AdCallBack{
        override fun adRewarded() {
        }

        override fun onAdsCallBack(isSuccess: Boolean) {
        }

        })
```

## XML Layout Used for Native Ads

```
    //Used this layout to show Native Ads Large UI
        <com.vk.adslib.template.TemplateView
            android:id="@+id/templateLarge"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:gnt_template_type="@layout/gnt_medium_template_view"
            />

    //Used this layout to show Native Ads Small UI
        <com.vk.adslib.template.TemplateViewSmall
            android:id="@+id/templateSmall"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:gnt_template_type="@layout/gnt_small_template_view"
            />

```
