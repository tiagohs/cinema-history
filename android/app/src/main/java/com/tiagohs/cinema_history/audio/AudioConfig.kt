package com.tiagohs.cinema_history.audio

import android.content.Context
import android.content.SharedPreferences
import com.tiagohs.cinema_history.BuildConfig
import java.net.URI

/**
 * Configuração da narração em áudio dos capítulos (ver content-src/audio/LEIAME.md, seção "No app").
 *
 * Os arquivos ficam em `<BASE_URL><idioma>/main_<era>/page_<n>/manifest.json` + `<faixa>.ogg`,
 * exatamente como o `generate.py` grava em `out/`. O app só mostra o botão "Ouvir" quando o
 * manifest do capítulo existe.
 */
object AudioConfig {

    /**
     * Único lugar para trocar o endereço do áudio. Precisa terminar com "/".
     *
     * TODO(R2): trocar pelo domínio próprio do bucket R2, por exemplo
     *  "https://audio.<seu-domínio>/v1/". Enquanto for ".invalid", nenhum manifest é encontrado e o
     *  recurso fica escondido no app.
     */
    const val BASE_URL = "https://audio.example.invalid/cinema-history/"

    /** Fonte de teste (só em debug): app/src/debug/assets/audio-test/ (gerada por make_test_assets.py). */
    const val TEST_BASE_URL = "asset:///audio-test/"

    /** Faixa liberada para quem ainda não apoia (a "Abertura"). */
    const val FREE_TRACK_ID = "00"

    const val SEEK_INCREMENT_MS = 15_000L
    val SPEEDS = floatArrayOf(0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
    val SLEEP_MINUTES = intArrayOf(15, 30, 45, 60)

    private const val PREFS = "audio_prefs"
    private const val KEY_TEST_SOURCE = "audio_test_source"
    private const val KEY_ACCESS_OVERRIDE = "audio_access_override"

    fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Em DEBUG, a flag "audio_test_source" troca a origem pelos assets de teste. Em release é sempre falso. */
    fun isTestSource(context: Context): Boolean =
        BuildConfig.DEBUG && prefs(context).getBoolean(KEY_TEST_SOURCE, false)

    fun setTestSource(context: Context, enabled: Boolean) {
        if (!BuildConfig.DEBUG) return
        prefs(context).edit().putBoolean(KEY_TEST_SOURCE, enabled).apply()
        AudioRepository.clearMemory()
    }

    /** Só debug: simula o estado do apoio sem mexer no Supporter ("", hidden, locked, unlocked). */
    fun accessOverride(context: Context): String =
        if (BuildConfig.DEBUG) prefs(context).getString(KEY_ACCESS_OVERRIDE, "") ?: "" else ""

    fun setAccessOverride(context: Context, value: String) {
        if (!BuildConfig.DEBUG) return
        prefs(context).edit().putString(KEY_ACCESS_OVERRIDE, value).apply()
    }

    fun baseUrl(context: Context): String = if (isTestSource(context)) TEST_BASE_URL else BASE_URL

    fun chapterDir(context: Context, key: ChapterKey): String =
        "${baseUrl(context)}${key.lang}/main_${key.era}/page_${key.page}/"

    fun manifestUrl(context: Context, key: ChapterKey): String = chapterDir(context, key) + "manifest.json"

    /** Resolve o "file" do manifest em relação à pasta do capítulo (aceita "01.ogg" ou caminhos relativos). */
    fun resolve(dir: String, file: String): String = try {
        URI(dir).resolve(file).toString()
    } catch (e: Exception) {
        dir + file
    }
}

/** Identifica um capítulo: idioma do conteúdo, era (main topic) e página (sumário). */
data class ChapterKey(val lang: String, val era: Int, val page: Int) {

    val id: String get() = "$lang/$era/$page"

    companion object {
        fun parse(id: String?): ChapterKey? {
            val parts = id?.split("/") ?: return null
            if (parts.size != 3) return null
            return ChapterKey(parts[0], parts[1].toIntOrNull() ?: return null, parts[2].toIntOrNull() ?: return null)
        }
    }
}

/** mediaId de cada faixa no player: "<idioma>/<era>/<página>#<faixa>". */
object AudioIds {
    fun mediaId(key: ChapterKey, trackId: String) = "${key.id}#$trackId"
    fun chapterOf(mediaId: String?): ChapterKey? = ChapterKey.parse(mediaId?.substringBefore('#'))
    fun trackOf(mediaId: String?): String? = mediaId?.substringAfter('#', "")?.takeIf { it.isNotEmpty() }
}
