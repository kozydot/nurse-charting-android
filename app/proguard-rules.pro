# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Kozy\AppData\Local\Android\Sdk\tools\proguard\proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# If you use reflection or JNI write specific rules rules here.
# Room specific rules
-keep class androidx.room.RoomDatabase { *; }
-keep class androidx.room.RoomOpenHelper { *; }
-keep class androidx.room.migration.Migration { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** create(...);
}
-keepclassmembers class * extends androidx.room.TypeConverter {
    public <init>();
}
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn androidx.room.**