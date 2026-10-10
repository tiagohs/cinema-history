package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.FragmentAwardsNomineesContentBinding
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardContentAdapter
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardItem
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardItemAnimator
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardYearAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.cinema_history.presentation.configs.Motion
import com.tiagohs.entities.awards.AwardYearSummary
import com.tiagohs.entities.awards.NomineeResult
import com.tiagohs.entities.enums.AwardsPageType
import com.tiagohs.entities.main_topics.AwardMainTopic

/**
 * Aba "Indicados e vencedores": seletor de anos (com salto por década) e, para o ano aberto,
 * uma fileira de pôsteres por categoria. Só o ano selecionado é carregado (pelo host).
 */
class AwardsNomineeFragment : BaseFragment<FragmentAwardsNomineesContentBinding>(), AwardYearListener {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAwardsNomineesContentBinding.inflate(inflater, container, false)

    private var awardMainTopic: AwardMainTopic? = null
    private var years: List<AwardYearSummary> = emptyList()
    private var decades: List<Int> = emptyList()
    private var shownDecade = Int.MIN_VALUE
    private var shownYear: String? = null
    private var motionEnabled = true

    private var yearAdapter: AwardYearAdapter? = null
    private var contentAdapter: AwardContentAdapter? = null

    private val host: AwardScreenHost? get() = activity as? AwardScreenHost

    /** O indicador só aparece se a leitura demorar (ano em cache troca sem piscar). */
    private val showLoadingRunnable = Runnable {
        bindingOrNull?.yearLoading?.visibility = View.VISIBLE
    }

    private val yearScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val lm = recyclerView.layoutManager as? LinearLayoutManager ?: return
            val first = lm.findFirstVisibleItemPosition()
            if (first == RecyclerView.NO_POSITION) return
            updateDecadeLabel(decadeOf(years.getOrNull(first)?.year))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionEnabled = Motion.enabled(context)
        // Tablets: conteúdo da aba numa coluna centralizada (rola na tela toda).
        binding.pageContentList.limitContentWidth(R.dimen.ls_details_max_width)
        setupArguments()
        setupYearBar()
        setupList()

        host?.setYearListener(this)
    }

    override fun onDestroyView() {
        host?.setYearListener(null)
        binding.yearList.removeOnScrollListener(yearScrollListener)
        binding.yearLoading.removeCallbacks(showLoadingRunnable)
        binding.pageContentList.adapter = null
        binding.yearList.adapter = null
        contentAdapter = null
        yearAdapter = null
        shownYear = null
        shownDecade = Int.MIN_VALUE

        super.onDestroyView()
    }

    @Suppress("DEPRECATION")
    private fun setupArguments() {
        awardMainTopic = arguments?.getSerializable(AWARD_MAIN_TOPIC) as? AwardMainTopic
        years = awardMainTopic?.yearIndex.orEmpty()
        decades = years.mapNotNull { decadeOf(it.year) }.distinct()
    }

    private fun setupYearBar() {
        val adapter = AwardYearAdapter { position, summary ->
            centerYear(position, smooth = true)
            host?.selectYear(summary)
        }
        yearAdapter = adapter
        adapter.submitList(years)

        binding.yearList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            itemAnimator = null
            this.adapter = adapter
            addOnScrollListener(yearScrollListener)
        }

        if (decades.size > 1) {
            binding.decadeButton.visibility = View.VISIBLE
            binding.decadeButton.setOnClickListener { showDecadeMenu(it) }
        } else {
            binding.decadeButton.visibility = View.GONE
        }
        updateDecadeLabel(decadeOf(years.firstOrNull()?.year))
    }

    private fun setupList() {
        val context = context ?: return
        val adapter = AwardContentAdapter(Glide.with(this), host?.awardActions ?: return, motionEnabled)
        contentAdapter = adapter

        binding.pageContentList.apply {
            layoutManager = LinearLayoutManager(context).apply { initialPrefetchItemCount = 3 }
            setHasFixedSize(true)
            setItemViewCacheSize(4)
            itemAnimator = AwardItemAnimator()
            this.adapter = adapter
        }
        binding.yearRetry.setOnClickListener { host?.retrySelectedYear() }
    }

    private fun showDecadeMenu(anchor: View) {
        val popup = PopupMenu(anchor.context, anchor)
        decades.forEachIndexed { index, decade -> popup.menu.add(0, index, index, decadeLabel(decade)) }
        popup.setOnMenuItemClickListener { menuItem ->
            val decade = decades.getOrNull(menuItem.itemId) ?: return@setOnMenuItemClickListener false
            val position = years.indexOfFirst { decadeOf(it.year) == decade }
            if (position >= 0) {
                // Vai para a década e abre o ano mais recente dela.
                (binding.yearList.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
                host?.selectYear(years[position])
            }
            true
        }
        popup.show()
    }

    private fun updateDecadeLabel(decade: Int?) {
        decade ?: return
        if (decade == shownDecade) return
        shownDecade = decade
        binding.decadeButton.text = decadeLabel(decade)
    }

    private fun decadeLabel(decade: Int) = getString(R.string.award_decade_label, decade.toString())

    private fun decadeOf(year: String?): Int? = year?.take(4)?.toIntOrNull()?.let { it / 10 * 10 }

    private fun centerYear(position: Int, smooth: Boolean) {
        val lm = binding.yearList.layoutManager as? LinearLayoutManager ?: return
        if (smooth && motionEnabled) {
            lm.startSmoothScroll(AwardYearAdapter.CenterSmoothScroller(binding.yearList.context).apply {
                targetPosition = position
            })
        } else {
            val offset = (binding.yearList.width - resources.getDimensionPixelSize(R.dimen.awards_year_pill_min_width)) / 2
            lm.scrollToPositionWithOffset(position, offset.coerceAtLeast(0))
        }
    }

    // ------------------------------------------------------------ AwardYearListener

    override fun onYearLoading(year: String) {
        val binding = bindingOrNull ?: return
        val adapter = yearAdapter ?: return

        val firstSelection = adapter.selectedYear == null
        adapter.select(year)
        val position = adapter.indexOf(year)
        if (position >= 0) {
            if (firstSelection) binding.yearList.post { bindingOrNull?.let { centerYear(position, smooth = false) } }
            else centerYear(position, smooth = true)
        }

        binding.yearError.visibility = View.GONE
        if (shownYear != year) {
            binding.pageContentList.animate().alpha(0.35f).setDuration(if (motionEnabled) 150 else 0).start()
            binding.yearLoading.removeCallbacks(showLoadingRunnable)
            binding.yearLoading.postDelayed(showLoadingRunnable, LOADING_DELAY_MS)
        }
    }

    override fun onYearLoaded(year: String, result: NomineeResult) {
        val binding = bindingOrNull ?: return
        val adapter = contentAdapter ?: return

        yearAdapter?.select(year)
        binding.yearLoading.removeCallbacks(showLoadingRunnable)
        binding.yearLoading.visibility = View.GONE
        binding.yearError.visibility = View.GONE
        binding.pageContentList.animate().cancel()
        binding.pageContentList.alpha = 1f

        if (shownYear == year && adapter.itemCount > 0) return
        shownYear = year

        val items = AwardItem.forYear(
            year,
            result.content.orEmpty(),
            getString(R.string.award_ceremony_title),
            getString(R.string.award_videos_title),
            getString(R.string.award_highlights_title)
        )
        adapter.submitYear(items) {
            bindingOrNull?.pageContentList?.scrollToPosition(0)
        }
    }

    override fun onYearFailed(year: String) {
        val binding = bindingOrNull ?: return
        binding.yearLoading.removeCallbacks(showLoadingRunnable)
        binding.yearLoading.visibility = View.GONE
        binding.pageContentList.animate().cancel()
        binding.pageContentList.alpha = 1f
        if (shownYear != year) {
            contentAdapter?.submitYear(emptyList())
            shownYear = null
        }
        binding.yearError.visibility = View.VISIBLE
    }

    override fun onErrorAction() {}

    companion object {

        const val AWARD_MAIN_TOPIC = "AWARD_MAIN_TOPIC"
        const val AWARD_PAGE_TYPE = "AWARD_PAGE_TYPE"
        private const val LOADING_DELAY_MS = 180L

        fun newInstance(
            awardMainTopic: AwardMainTopic,
            awardsPageType: AwardsPageType
        ): AwardsNomineeFragment {
            val awardsFragment = AwardsNomineeFragment()
            awardsFragment.arguments = Bundle().apply {
                putSerializable(AWARD_MAIN_TOPIC, awardMainTopic)
                putSerializable(AWARD_PAGE_TYPE, awardsPageType)
            }

            return awardsFragment
        }
    }
}
