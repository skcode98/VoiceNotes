# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepattributes Signature
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * extends com.google.gson.TypeAdapter

# Firebase
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep interface dagger.hilt.** { *; }

# Room
-keepattributes SourceFile,LineNumberTable
-keep public class * extends androidx.room.RoomDatabase

# Media3
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
