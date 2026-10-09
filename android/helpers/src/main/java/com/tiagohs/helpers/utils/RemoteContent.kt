package com.tiagohs.helpers.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import timber.log.Timber
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

/**
 * Conteúdo atualizável sem nova versão do app, servido pelo site no Cloudflare Pages:
 *
 *   https://website-cb5.pages.dev/cinema-history/content/manifest.json
 *   https://website-cb5.pages.dev/cinema-history/content/<idioma>/<arquivo>.json
 *
 * O manifest lista os arquivos publicados e o sha256 de cada um. [sync] baixa o que mudou para
 * filesDir/remote_content/ (conferindo hash e JSON), e o FakeInterceptor lê dali antes dos assets.
 * Ao atualizar o app, o cache é apagado: o conteúdo embutido na nova versão volta a valer até o
 * próximo sync. Para publicar: content-src/remote.py (ver content-src/REMOTO.md).
 */
object RemoteContent {

    const val BASE_URL = "https://website-cb5.pages.dev/cinema-history/content/"
    private const val DIR = "remote_content"
    private const val PREFS = "remote_content"
    private const val KEY_APP_VERSION = "app_version"
    private const val KEY_LAST_SYNC = "last_sync"
    private const val SYNC_INTERVAL_MS = 6 * 60 * 60 * 1000L
    /** Versão do formato do manifest que este app entende. */
    private const val FORMAT = 1

    @Volatile private var root: File? = null

    /** Chamar no Application.onCreate. Não faz rede: só prepara a pasta e limpa o cache se o app mudou de versão. */
    fun init(context: Context) {
        val dir = File(context.filesDir, DIR)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val version = appVersion(context)
        if (prefs.getLong(KEY_APP_VERSION, -1) != version) {
            dir.deleteRecursively()
            prefs.edit().clear().putLong(KEY_APP_VERSION, version).apply()
        }
        root = dir
    }

    /** Arquivo baixado para "local/<idioma>/x.json", ou null se não houver versão remota. */
    fun cachedFile(assetPath: String): File? {
        val dir = root ?: return null
        val file = File(dir, assetPath.removePrefix("local/"))
        return if (file.isFile) file else null
    }

    /** Baixa o que mudou. Rode fora da main thread. Falhas de rede são ignoradas (fica o que já havia). */
    fun sync(context: Context, force: Boolean = false) {
        val dir = root ?: return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!force && System.currentTimeMillis() - prefs.getLong(KEY_LAST_SYNC, 0) < SYNC_INTERVAL_MS) return

        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            val manifest = JSONObject(get(client, BASE_URL + "manifest.json") ?: return)
            if (manifest.optInt("format", 1) > FORMAT) return
            if (manifest.optLong("min_app_version", 0) > appVersion(context)) return

            val files = manifest.getJSONObject("files")
            val editor = prefs.edit()
            val keep = HashSet<String>()
            val bundled = bundledHashes(context)
            val language = ContentLanguage.current()
            for (path in files.keys()) {
                if (path.contains("..")) continue
                // só o idioma em uso (os outros baixam se o usuário trocar de idioma)
                if (!path.startsWith("$language/")) continue
                val hash = files.getString(path)
                // igual ao que já veio dentro do app: não precisa baixar
                if (bundled[path] == hash) continue
                keep += path
                val target = File(dir, path)
                if (prefs.getString("h:$path", null) == hash && target.isFile) continue

                val body = get(client, BASE_URL + path) ?: continue
                if (sha256(body) != hash || !isJson(body)) {
                    Timber.w("RemoteContent: arquivo inválido %s", path)
                    continue
                }
                target.parentFile?.mkdirs()
                val tmp = File(target.path + ".tmp")
                tmp.writeText(body)
                if (!tmp.renameTo(target)) { target.delete(); tmp.renameTo(target) }
                editor.putString("h:$path", hash)
            }
            // remove o que saiu do manifest
            dir.walkTopDown().filter { it.isFile }.forEach { f ->
                val rel = f.relativeTo(dir).path.replace(File.separatorChar, '/')
                if (rel !in keep) { f.delete(); editor.remove("h:$rel") }
            }
            editor.putLong(KEY_LAST_SYNC, System.currentTimeMillis()).apply()
        } catch (e: Exception) {
            Timber.w(e, "RemoteContent: sync falhou")
        }
    }

    /** Hashes do conteúdo embutido no app (assets/local/remote_manifest.json, gerado por content-src/remote.py). */
    private fun bundledHashes(context: Context): Map<String, String> = try {
        val files = JSONObject(context.assets.open("local/remote_manifest.json").bufferedReader().use { it.readText() })
            .getJSONObject("files")
        files.keys().asSequence().associateWith { files.getString(it) }
    } catch (e: Exception) { emptyMap() }

    private fun get(client: OkHttpClient, url: String): String? =
        client.newCall(Request.Builder().url(url).build()).execute().use { r ->
            if (r.isSuccessful) r.body?.string() else null
        }

    private fun isJson(text: String): Boolean = try {
        JSONTokener(text).nextValue().let { it is JSONObject || it is JSONArray }
    } catch (e: Exception) { false }

    private fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    @Suppress("DEPRECATION")
    private fun appVersion(context: Context): Long = try {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= 28) info.longVersionCode else info.versionCode.toLong()
    } catch (e: PackageManager.NameNotFoundException) { 0 }
}
