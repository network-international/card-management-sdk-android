# Consumer ProGuard rules for card-management-sdk-android
# These rules ensure that classes referenced via reflection are preserved
# during minification by consuming applications (including R8 and DexGuard).

# Keep all BouncyCastle classes
# BouncyCastleProvider registers algorithms REFLECTIVELY by literal class name
# (e.g. org.bouncycastle.jcajce.provider.asymmetric.RSA$Mappings).
# If these classes are renamed by R8/ProGuard/DexGuard, the reflection fails.
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Keep Provider constructor (required for reflection-based instantiation)
-keepclassmembers class * extends java.security.Provider {
    <init>(...);
}

# Keep all SDK classes
-keep class ae.network.nicardmanagementsdk.** { *; }
-keepclassmembers class ae.network.nicardmanagementsdk.** { *; }

# Preserve attributes needed for reflection and debugging
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
