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

-dontobfuscate
-optimizationpasses 5
-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontnote bsh.ClassGenerator
-dontnote bsh.BshClassManager
-keep class org.duangsuse.** { *; }
#keep beanshell engine
-keep class bsh.** { *; }
-keepclassmembers class android.os.Build { int SDK_INT; }
#keep animated textview
-keep class com.hanks.htextview.** { *; }
#keep cricle anim
-keep class top.wefor.circularanim.** { *; }
#floating action button
-keep class com.melnykov.fab.** { *; }
#extra anim
-keep class com.easyandroidanimations.library.** { *; }
#v4
-keep class android.support.v4.widget.CircleImageView { *; }
-keep class android.support.v4.widget.SwipeRefreshLayout { *; }
-keep class android.support.v4.view.ViewPager { *; }
-keep class android.support.v4.widget.DrawerLayout.** { *; }
-keep class android.support.v4.widget.MaterialProgressDrawable { *; }
#alua support
-keep class android.widget.PageAdapter { *; }
-keep class android.widget.ArrayPageAdapter { *; }
-keep class android.widget.ArrayListAdapter { *; }
-keep class android.widget.CardView { *; }
-keep class android.widget.ExListView { *; }
-keep class android.widget.PageLayout.** { *; }
-keep class android.widget.PageView.** { *; }
-keep class android.widget.PullingLayout.** { *; }
-keep class android.widget.RoundRectDrawable { *; }
-keep class android.app.FloatWindow { *; }

-keep class android.widget.DrawerLayout$DrawerListener { *; }
-keep class android.widget.PageView$OnAdapterChangeListener { *; }
-keep class android.widget.PageLayout$OnPageChangeListener { *; }
-keep class android.widget.PageView$OnPageChangeListener { *; }
-keep class android.widget.PullingLayout$OnLoadMoreListener { *; }
-keep class android.widget.PullingLayout$OnPullDownListener { *; }
-keep class android.widget.PullingLayout$OnPullUpListener { *; }
-keep class android.widget.PullingLayout$OnRefreshListener { *; }
-keep class android.widget.SlidingLayout$OnMenuClosedListener { *; }
-keep class android.widget.SlidingLayout$OnMenuOpenedListener { *; }
-keep class android.widget.SlidingLayout$OnMenuStateChangeListener { *; }
-keep class android.widget.PullingLayout { *; }
