package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.configs.limitContentWidth
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.tiagohs.cinema_history.databinding.FragmentAwardsContentBinding
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardActions
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardContentAdapter
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardItem
import com.tiagohs.cinema_history.presentation.adapters.awards.AwardItemAnimator
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.cinema_history.presentation.configs.Motion
import com.tiagohs.entities.enums.AwardsPageType
import com.tiagohs.entities.main_topics.AwardMainTopic

/**
 * Aba "Sobre": ficha do prêmio e o histórico em versão compacta (primeiros parágrafos);
 * o restante (vídeos, imagens, curiosidades) aparece em "Ler mais".
 */
class AwardsFragment : BaseFragment<FragmentAwardsContentBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAwardsContentBinding.inflate(inflater, container, false)

    private var awardMainTopic: AwardMainTopic? = null
    private var expanded = false
    private var adapter: AwardContentAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expanded = savedInstanceState?.getBoolean(STATE_EXPANDED) ?: false
        // Tablets: conteúdo da aba numa coluna centralizada (rola na tela toda).
        binding.pageContentList.limitContentWidth(R.dimen.ls_details_max_width)
        setupArguments()
        setupList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_EXPANDED, expanded)
    }

    override fun onDestroyView() {
        binding.pageContentList.adapter = null
        adapter = null
        super.onDestroyView()
    }

    @Suppress("DEPRECATION")
    private fun setupArguments() {
        awardMainTopic = arguments?.getSerializable(AWARD_MAIN_TOPIC) as? AwardMainTopic
    }

    private fun setupList() {
        val hostActions = (activity as? AwardScreenHost)?.awardActions ?: return
        val actions = object : AwardActions by hostActions {
            override fun onReadMoreClicked() = toggleExpanded()
        }
        val adapter = AwardContentAdapter(Glide.with(this), actions, Motion.enabled(context))
        this.adapter = adapter

        binding.pageContentList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = AwardItemAnimator()
            this.adapter = adapter
        }
        submit(animate = false)
    }

    private fun toggleExpanded() {
        expanded = !expanded
        submit(animate = expanded)
    }

    private fun submit(animate: Boolean) {
        val award = awardMainTopic ?: return
        val adapter = adapter ?: return
        if (animate) adapter.playEntrance()
        adapter.submitList(AwardItem.forHistory(award, award.history.orEmpty(), expanded))
    }

    override fun onErrorAction() {}

    companion object {

        const val AWARD_MAIN_TOPIC = "AWARD_MAIN_TOPIC"
        const val AWARD_PAGE_TYPE = "AWARD_PAGE_TYPE"
        private const val STATE_EXPANDED = "STATE_EXPANDED"

        fun newInstance(
            awardMainTopic: AwardMainTopic,
            awardsPageType: AwardsPageType
        ): AwardsFragment {
            val awardsFragment = AwardsFragment()
            awardsFragment.arguments = Bundle().apply {
                putSerializable(AWARD_MAIN_TOPIC, awardMainTopic)
                putSerializable(AWARD_PAGE_TYPE, awardsPageType)
            }

            return awardsFragment
        }
    }
}
