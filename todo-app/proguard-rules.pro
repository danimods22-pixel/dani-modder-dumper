# Keep app classes
-keep class com.dani.todo.** { *; }
-keepclassmembers class com.dani.todo.** { *; }
-dontwarn com.dani.todo.**

# Keep Android framework
-keep public class android.** { public *; }
-keep interface android.** { *; }
