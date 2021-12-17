package com.hanasmarcin.kivracoffee.view.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ArcMotion
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis
import com.hanasmarcin.kivracoffee.R
import com.hanasmarcin.kivracoffee.databinding.FragmentCoffeeDetailsBinding
import com.hanasmarcin.kivracoffee.model.CoffeeModel
import com.hanasmarcin.kivracoffee.utils.MAP_URL
import com.hanasmarcin.kivracoffee.utils.SEARCH_URL
import com.hanasmarcin.kivracoffee.utils.TRANSITION_NAME_PATTERN
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class CoffeeDetailsFragment : Fragment() {
    val args: CoffeeDetailsFragmentArgs by navArgs()
    var binding: FragmentCoffeeDetailsBinding? = null

    val viewModel: CoffeeDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // different enter transitions for coffee bag view (with shared transition from list view) and the rest
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            removeTarget(R.id.coffee_bag_content)
        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            setPathMotion(ArcMotion())
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentCoffeeDetailsBinding.inflate(inflater, container, false)
            .apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // fragment entering animation is postponed to just before the layout is being drawn
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding?.apply {
            // animation for the rotating blob
            ivBlobBackground.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.rotate)
                    .apply { interpolator = LinearInterpolator() })
            ivBackBtn.setOnClickListener { findNavController().popBackStack() }
        }
        populateWithData(args.coffee)
    }

    private fun populateWithData(coffee: CoffeeModel) = binding?.apply {
        coffeeBagContent.apply {
            cvFlag.rotation = coffee.flagRotation
            root.transitionName = TRANSITION_NAME_PATTERN.format(coffee.uid)
            ivFlag.setImageDrawable(viewModel.getFlagForCountryName(coffee.country))
            tvTitle.text = coffee.blendName
            tvOrigin.text = coffee.origin
            tvVariety.text = coffee.variety
            tvIntensifier.text = coffee.intensifier
            coffee.notes.forEach {
                cgNotes.addView(createChip(it))
            }
            chOriginBtn.setOnClickListener {
                // open external map app to find location
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(MAP_URL.format(coffee.origin)))
                )
            }
            chVarietyBtn.setOnClickListener {
                // open external browser to search variety
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(SEARCH_URL.format(coffee.variety)))
                )
            }
        }
    }

    private fun createChip(label: String) = Chip(context).apply {
        text = label
        isClickable = false
        rippleColor = ContextCompat.getColorStateList(context, R.color.kivra_green)
        chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.color_chip_on_surface)
        setTextAppearance(R.style.Theme_KivraCoffee_ChipText)
        setTextColor(ContextCompat.getColorStateList(context, R.color.color_chip_text))
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}