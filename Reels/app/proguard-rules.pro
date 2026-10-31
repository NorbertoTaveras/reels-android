# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Gson reads TMDB DTO fields through reflection. DTO fields use explicit
# @SerializedName values, so field names may still be obfuscated.
-keepclassmembers,allowobfuscation class com.norbertotaveras.reels.data.remote.dto.** {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations

# Glance instantiates widget actions through their public no-argument constructor.
-keep class com.norbertotaveras.reels.presentation.widget.** implements androidx.glance.appwidget.action.ActionCallback {
    public <init>();
}

# WorkManager creates input mergers reflectively when running Glance updates.
-keep class androidx.work.OverwritingInputMerger {
    public <init>();
}

# Glance persists widget class names and recreates each widget reflectively.
-keep class com.norbertotaveras.reels.presentation.widget.* extends androidx.glance.appwidget.GlanceAppWidget {
    public <init>();
}
