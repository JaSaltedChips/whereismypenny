# Keep Room entity and DAO class names (needed for reflection-based migration checks)
-keep class app.harsha.wheresmypenny.data.db.entity.** { *; }

# Keep Kotlin metadata so coroutines and reflection work in release builds
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
