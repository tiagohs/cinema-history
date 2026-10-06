# Modelos (JSON locais em assets/local, TMDB, OMDB, Firebase) são lidos via Gson/Jackson
# por reflexão: o R8 não pode renomear nem remover classes, campos ou construtores.
-keep class com.tiagohs.entities.** { *; }
-keepclassmembers enum com.tiagohs.entities.** { *; }

# Classes passadas entre telas via Intent (Serializable/Parcelable)
-keepclassmembers class com.tiagohs.entities.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
