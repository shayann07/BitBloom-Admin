package com.example.bitbloomadmin.ui

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.example.bitbloomadmin.MainActivity
import com.example.bitbloomadmin.R

open class BaseFragment : Fragment() {

    private var loadingOverlay: View? = null

    /** Setup the drawer icon click to open the nav drawer */
    fun setupDrawerTrigger(view: View) {
        view.findViewById<ImageView>(R.id.menuIcon)
            ?.setOnClickListener { (activity as? MainActivity)?.openDrawer() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Choose a ViewGroup to host the overlay
        val container: ViewGroup = when {
            view is ScrollView && view.childCount == 1 -> {
                // Wrap the ScrollView’s single child so we can overlay on it
                val child = view.getChildAt(0)
                view.removeView(child)
                FrameLayout(requireContext()).apply {
                    layoutParams = child.layoutParams
                    addView(child)
                    view.addView(this)
                }
            }
            view is ViewGroup -> view
            else -> return
        }

        // 2) Inflate the overlay once and add it
        if (loadingOverlay == null) {
            loadingOverlay = layoutInflater
                .inflate(R.layout.dialog_loading_overlay, container, false)
                .apply { isClickable = true }
            container.addView(loadingOverlay)
        }
    }

    /** Show the Lottie loading overlay */
    fun showLoading() = loadingOverlay?.let {
        it.visibility = View.VISIBLE
        it.bringToFront()
    }

    /** Hide the Lottie loading overlay */
    fun hideLoading() = loadingOverlay?.let {
        it.visibility = View.GONE
    }
}
