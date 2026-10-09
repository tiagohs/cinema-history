package com.tiagohs.cinema_history.presentation.activities

import com.tiagohs.cinema_history.presentation.configs.forScreen
import android.animation.Animator
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ActivityMilMoviesPresentationBinding
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieListAdapter
import com.tiagohs.cinema_history.presentation.adapters.decorators.ScaleMovieImageTransformer
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.LargeScreen
import com.tiagohs.domain.presenter.MilMoviesPresentationPresenter
import com.tiagohs.domain.views.MilMoviesPresentationView
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import javax.inject.Inject


class MilMoviesPresentationActivity : BaseActivity<ActivityMilMoviesPresentationBinding>(), MilMoviesPresentationView {

    override fun inflateBinding(inflater: LayoutInflater) = ActivityMilMoviesPresentationBinding.inflate(inflater)
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MilMoviesPresentationPresenter

    lateinit var mainTopic: MilMoviesMainTopic

    var page = 1
    var isSearching = false

    var adapter: MovieListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        presenter.onBindView(this)

        setupToolbar(binding.toolbar)
        setupArguments()
        presenter.fetchMoviesByListId(mainTopic.list_id)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun setupArguments() {
        mainTopic = intent?.extras?.getSerializable(MAIN_TOPIC) as? MilMoviesMainTopic ?: return
    }

    override fun bindMovieList(list: List<Movie>) {
        adapter = MovieListAdapter(ArrayList(list), this, mainTopic).apply {
            onMovieSelected = { movie, _ -> onMovieSelected(movie) }
        }

        binding.moviesViewPager.apply {
            adapter = this@MilMoviesPresentationActivity.adapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val movieList = this@MilMoviesPresentationActivity.adapter?.list ?: return

                    movieList.getOrNull(position)?.let { loadBackdrop(it) }

                    if (position == movieList.size - 3 && !isSearching && presenter.hasMorePages()) {
                        binding.loadingProgress.show()

                        presenter.fetchMoreMovies(mainTopic.list_id, ++page)

                        isSearching = true
                    }
                }
            })
        }

        if (setupLargeScreenPager()) {
            bindHeader()
            return
        }

        val horizontalSpace = 42.convertIntToDp(this)
        val spaceBetweenItems = 32.convertIntToDp(this)

        binding.moviesViewPager.setPageTransformer(
            ScaleMovieImageTransformer(
                horizontalSpace,
                spaceBetweenItems
            )
        )

        binding.moviesViewPager.addItemDecoration(
            ScaleMovieImageTransformer.HorizontalMarginItemDecoration(
                horizontalSpace
            )
        )

        bindHeader()
    }

    private fun bindHeader() {
        val titleColorRes = resources.getIdentifier(mainTopic.titleColor, "color", packageName)
        val titleColor = getResourceColor(titleColorRes)

        binding.toolbar.navigationIcon?.setColorFilter(titleColor, PorterDuff.Mode.SRC_ATOP)

        binding.presentationTitle.setResourceText(mainTopic.title)
        binding.presentationTitle.setTextColor(titleColor)
        binding.presentationSubtitle.setTextColor(titleColor)

        binding.presentationTitle.startAnimation(AnimationUtils.createFadeInAnimation(200, 350))
        binding.presentationSubtitle.startAnimation(AnimationUtils.createFadeInAnimation(200, 350))
    }

    /**
     * Tablets: em vez de um pôster esticado na largura toda (que viraria uma faixa horizontal em
     * paisagem), cada página tem largura de pôster (proporcional à altura da janela) e os vizinhos
     * aparecem dos dois lados, mostrando mais filmes ao mesmo tempo. No celular devolve false e o
     * carrossel continua exatamente como antes.
     */
    private fun setupLargeScreenPager(): Boolean {
        if (!LargeScreen.isLarge(this)) return false

        val density = resources.displayMetrics.density
        val windowWidth = (resources.configuration.screenWidthDp * density).toInt()
        val windowHeight = (resources.configuration.screenHeightDp * density).toInt()
        val minSide = (42 * density).toInt()
        val pageWidth = (windowHeight * 0.55f).toInt().coerceIn((320 * density).toInt(), windowWidth - 2 * minSide)
        val sidePadding = ((windowWidth - pageWidth) / 2).coerceAtLeast(minSide)

        binding.moviesViewPager.offscreenPageLimit = 2
        (binding.moviesViewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView)?.apply {
            setPadding(sidePadding, paddingTop, sidePadding, paddingBottom)
            clipToPadding = false
        }
        binding.moviesViewPager.setPageTransformer { page, position ->
            val pageBinding = com.tiagohs.cinema_history.databinding.AdapterMovieListBinding.bind(page)
            val distance = kotlin.math.abs(position).coerceAtMost(1f)
            pageBinding.imageCard.scaleY = 1 - ScaleMovieImageTransformer.MIN_SCALE_Y * distance
            pageBinding.imageCard.scaleX = 1 - ScaleMovieImageTransformer.MIN_SCALE_X * distance
            // títulos só no pôster central
            pageBinding.title.alpha = 1 - distance
            pageBinding.originalTitle.alpha = 1 - distance
        }
        return true
    }

    private fun loadBackdrop(movie: Movie) {
        val url = movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500.forScreen(this)) ?: return

        binding.backdropImage.loadImage(url, placeholder = null, transform = BlurTransformation(35, 3))
    }

    override fun bindMoreMovies(movies: List<Movie>) {
        binding.loadingProgress.hide()

        adapter?.addMoreMovies(movies)

        isSearching = false
    }

    override fun startLoading() {
        binding.contentContainer.alpha = 0f

        binding.loadView.showShimmer(true)
        binding.loadView.show()
        binding.loadView.alpha = 1f
    }

    override fun hideLoading() {
        binding.contentContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        binding.loadView
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    binding.loadView.stopShimmer()
                    binding.loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) {}

            })
            .start()
    }

    private fun onMovieSelected(movie: Movie) {
        val id = movie.id ?: return

        startActivityWithSlideRightToLeftAnimation(MovieDetailsActivity.newIntent(this, id))
    }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"

        fun newIntent(mainTopic: MilMoviesMainTopic, context: Context): Intent {
            val intent = Intent(context, MilMoviesPresentationActivity::class.java)

            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}
