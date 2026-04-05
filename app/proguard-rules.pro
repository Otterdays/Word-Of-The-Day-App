# Default ProGuard rules for Word of the Day
# Keep Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.example.wordofday.**$$serializer { *; }
-keepclassmembers class com.example.wordofday.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.wordofday.** {
    kotlinx.serialization.KSerializer serializer(...);
}
