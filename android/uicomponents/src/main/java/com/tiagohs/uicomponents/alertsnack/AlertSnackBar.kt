package com.tiagohs.uicomponents.alertsnack

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.helpers.R
import com.tiagohs.helpers.extensions.getResourceDrawable
import com.tiagohs.helpers.extensions.getResourceString
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.layout_snackbar_base_alert.view.*

class AlertSnackBar(
    parent: ViewGroup,
    content: AlertSnackBarView
) : BaseTransientBottomBar<AlertSnackBar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(
            view: View?,
            type: MessageViewType = MessageViewType.ERROR,
            title: Int? = null,
            description: Int? = null,
            duration: Int
        ): AlertSnackBar? =
            make(
                view,
                type,
                view?.context.getResourceString(title),
                view?.context.getResourceString(description),
                duration
            )

        fun make(
            view: View?,
            type: MessageViewType = MessageViewType.ERROR,
            title: String? = null,
            description: String? = null,
            duration: Int
        ): AlertSnackBar? {

            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )
            val context = view?.context ?: return null

            try {
                val customView = AlertSnackBarView(context).apply {
                    layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )

                    setResourceBackgroundColor(R.color.transparent)
                }

                when (type) {
                    MessageViewType.ERROR -> {
                        customView.messageContainer.setResourceBackgroundColor(R.color.md_red_500)
                        customView.icon.setImageDrawable(context.getResourceDrawable(com.tiagohs.uicomponents.R.drawable.ic_error))
                    }
                    else -> {
                        customView.messageContainer.setResourceBackgroundColor(R.color.md_green_500)
                        customView.icon.setImageDrawable(context.getResourceDrawable(com.tiagohs.uicomponents.R.drawable.ic_check))
                    }
                }

                title?.let { customView.title.setResourceText(it) }
                description?.let { customView.description.setResourceText(it) }

                return AlertSnackBar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
                return null
            }

        }


    }
}

internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null

    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                fallback = view
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback
}