package com.tiagohs.cinema_history.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.tiagohs.cinema_history.BuildConfig
import com.tiagohs.cinema_history.databinding.ViewNativeAdBinding
import timber.log.Timber

/** Blocos de anúncio nativo do app (cada um é um bloco no AdMob). */
enum class AdPlacement(val unitId: String) {
    LISTS(BuildConfig.ADMOB_NATIVE_LISTS),
    CHAPTER(BuildConfig.ADMOB_NATIVE_CHAPTER),
    MOVIE(BuildConfig.ADMOB_NATIVE_MOVIE)
}

/**
 * Adapter de um único item com um anúncio nativo. Tem 0 itens até o anúncio carregar, então
 * nada "pula" na tela se não houver anúncio (sem consentimento, offline ou sem preenchimento).
 * Usado dentro de um [ConcatAdapter]: as posições dos outros adapters não mudam.
 */
class NativeAdAdapter(
    private val placement: AdPlacement,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<NativeAdAdapter.NativeAdViewHolder>(), DefaultLifecycleObserver {

    private var nativeAd: NativeAd? = null
    private var loading = false
    private var destroyed = false

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        load(recyclerView.context)
    }

    private fun load(context: Context) {
        if (loading || nativeAd != null || destroyed) return
        if (!AdsConfig.nativeEnabled || !AdsManager.canRequestAds(context)) return

        loading = true

        AdLoader.Builder(context, placement.unitId)
            .forNativeAd { ad ->
                if (destroyed) {
                    ad.destroy()
                    return@forNativeAd
                }
                nativeAd = ad
                notifyItemInserted(0)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    loading = false
                    Timber.d("Native ad %s: %s", placement, error.message)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    override fun getItemCount(): Int = if (nativeAd != null) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NativeAdViewHolder =
        NativeAdViewHolder(ViewNativeAdBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: NativeAdViewHolder, position: Int) {
        nativeAd?.let { holder.bind(it) }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        destroyed = true
        nativeAd?.destroy()
        nativeAd = null
        owner.lifecycle.removeObserver(this)
    }

    class NativeAdViewHolder(private val binding: ViewNativeAdBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: NativeAd) {
            val adView = binding.nativeAdView

            binding.adHeadline.text = ad.headline
            adView.headlineView = binding.adHeadline

            binding.adBody.text = ad.body
            binding.adBody.visibility = if (ad.body.isNullOrBlank()) View.GONE else View.VISIBLE
            adView.bodyView = binding.adBody

            binding.adAdvertiser.text = ad.advertiser
            binding.adAdvertiser.visibility = if (ad.advertiser.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
            adView.advertiserView = binding.adAdvertiser

            val icon = ad.icon?.drawable
            binding.adIcon.setImageDrawable(icon)
            binding.adIcon.visibility = if (icon == null) View.GONE else View.VISIBLE
            adView.iconView = binding.adIcon

            binding.adCallToAction.text = ad.callToAction
            binding.adCallToAction.visibility = if (ad.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE
            adView.callToActionView = binding.adCallToAction

            val media = ad.mediaContent
            if (media != null) {
                binding.adMedia.mediaContent = media
                // Altura fixa (180dp): o anúncio não ocupa a tela inteira nem gera cliques acidentais.
                binding.adMedia.setImageScaleType(android.widget.ImageView.ScaleType.CENTER_CROP)
                binding.adMedia.visibility = View.VISIBLE
            } else {
                binding.adMedia.visibility = View.GONE
            }
            adView.mediaView = binding.adMedia

            adView.setNativeAd(ad)
        }
    }
}

/**
 * Insere um anúncio nativo depois do item [after] de uma lista, criando dois adapters do mesmo tipo
 * (antes/depois) ligados por um [ConcatAdapter]. Se a lista for curta, só o adapter original é usado.
 * Só serve para adapters que não dependem da posição absoluta do item.
 */
fun <T> adapterWithNativeAd(
    list: List<T>,
    after: Int,
    placement: AdPlacement,
    lifecycleOwner: LifecycleOwner,
    create: (List<T>) -> RecyclerView.Adapter<out RecyclerView.ViewHolder>
): RecyclerView.Adapter<out RecyclerView.ViewHolder> {
    if (list.size <= after) return create(list)

    return ConcatAdapter(
        create(list.take(after)),
        NativeAdAdapter(placement, lifecycleOwner),
        create(list.drop(after))
    )
}
