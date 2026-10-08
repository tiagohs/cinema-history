# ----------------------------------------------------------------------------
# Cinema History - regras do R8
# As regras dos modelos estão em entities/consumer-rules.pro.
# Bibliotecas como Retrofit, OkHttp, Gson, Glide, Dagger e Firebase já trazem
# as próprias regras (consumer rules); aqui ficam só os complementos.
# ----------------------------------------------------------------------------

# Stack traces legíveis no Crashlytics (o mapping.txt é enviado pelo plugin).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Genéricos e anotações usados por Retrofit/Gson (List<T>, Observable<T>, @SerializedName).
-keepattributes Signature,InnerClasses,EnclosingMethod,*Annotation*

# Retrofit + RxJava2: com R8 em full mode, os tipos de retorno das interfaces
# precisam manter a assinatura genérica.
-keep,allowobfuscation,allowshrinking class io.reactivex.Observable
-keep,allowobfuscation,allowshrinking class io.reactivex.Single
-keep,allowobfuscation,allowshrinking class io.reactivex.Flowable
-keep,allowobfuscation,allowshrinking class io.reactivex.Maybe
-keep,allowobfuscation,allowshrinking class io.reactivex.Completable
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Deserializers customizados do Gson (domain/services/deserializers).
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.JsonSerializer
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory

# Glide: módulo registrado por anotação (FirebaseAppStorageModule).
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }

# Room (usado pelo WorkManager do SDK de anúncios): o banco gerado (*_Impl) é criado por reflexão.
# Sem isto, o R8 full mode remove o construtor e o app fecha ao abrir
# ("Failed to create an instance of androidx.work.impl.WorkDatabase").
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class androidx.work.impl.WorkDatabase_Impl { <init>(); }
