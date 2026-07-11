# Proguard rules untuk Dani Modder
-keep class com.dani.modder.** { *; }
-keepclassmembers class com.dani.modder.** { *; }
-dontwarn com.dani.modder.**

# Keep Android framework
-keep public class android.** { public *; }
-keep interface android.** { *; }
