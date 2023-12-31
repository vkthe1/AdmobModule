
-dontwarn sun.misc.Unsafe
-dontwarn org.reactivestreams.FlowAdapters
-dontwarn org.reactivestreams.**
-dontwarn java.util.concurrent.flow.**
-dontwarn java.util.concurrent.**

### Glide, Glide Okttp Module, Glide Transformations
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# -keepresourcexmlelements manifest/application/meta-data@value=GlideModule 3 For dexguard
-dontwarn jp.co.cyberagent.android.gpuimage.**

### Reactive Network
-dontwarn com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
-dontwarn io.reactivex.functions.Function
-dontwarn rx.internal.util.**
-dontwarn sun.misc.Unsafe

### Retrolambda
# as per official recommendation: https://github.com/evant/gradle-retrolambda#proguard
-dontwarn java.lang.invoke.*

### Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { <fields>; }
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.ads.** # Don't proguard AdMob classes
-dontwarn com.google.ads.** # Temporary workaround for v6.2.1. It gives a warning that you can ignore
### Retrofit 2
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform


# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

#-dontwarn retrofit2.adapter.rxjava.CompletableHelper$** # https://github.com/square/retrofit/issues/2034
##To use Single instead of Observable in Retrofit interface
#-keepnames class rx.Single

#Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit
# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

### OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase




# Findbugs Annotation
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings

# Findbugs jsr305
-dontwarn javax.annotation.**



### MoPub
#-keepclassmembers class com.mopub.** { public *; }
#-keep public class com.mopub.**
#-keep public class android.webkit.JavascriptInterface {}
#
#-keep class * extends com.mopub.mobileads.CustomEventBanner {}
#-keepclassmembers class com.mopub.mobileads.CustomEventBannerAdapter {!private !public !protected *;}
#-keep class * extends com.mopub.mobileads.CustomEventInterstitial {}
#-keep class * extends com.mopub.mobileads.CustomEventNative {}


## Android Advertiser ID
#-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
#-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
#-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

### Crashlytics
# In order to provide the most meaningful crash reports
-keepattributes SourceFile,LineNumberTable
-dontwarn com.crashlytics.**
-keep class com.google.firebase** { *; }
-dontwarn com.google.firebase.crashlytics.**

### Crash report
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

### Other
-dontwarn com.google.errorprone.annotations.*

### Exoplayer2
#-dontwarn com.google.android.exoplayer2.**

### Android Architecture Components
# Ref: https://issuetracker.google.com/issues/62113696
# LifecycleObserver's empty constructor is considered to be unused by proguard
#-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}
#-keep class * implements android.arch.lifecycle.LifecycleObserver {
#    <init>(...);
#}

# ViewModel's empty constructor is considered to be unused by proguard
#-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
#    <init>(...);
#}

# keep Lifecycle State and Event enums values
#-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
#-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
## keep methods annotated with @OnLifecycleEvent even if they seem to be unused
## (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
#-keepclassmembers class * {
#    @android.arch.lifecycle.OnLifecycleEvent *;
#}



### Kotlin Coroutine
# ServiceLoader support
-optimizationpasses 5
-overloadaggressively
-repackageclasses 'x.y.zc.sdv.sd.sv.sdv.sv.rb.wseg.b.t.sv.x.y.z'
-allowaccessmodification
#-keepattributes *Annotation*
#-obfuscationdictionary keywords.txt
#-assumenosideeffects class android.util.Log {
#    public static boolean isLoggable(java.lang.String, int);
#    public static int v(...);
#    public static int i(...);
#    public static int w(...);
#    public static int d(...);
#    public static int e(...);
#}
-keep public class * {
    public protected *;
}