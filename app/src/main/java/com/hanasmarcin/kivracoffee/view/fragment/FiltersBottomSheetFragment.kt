package com.hanasmarcin.kivracoffee.view.fragment

import android.animation.LayoutTransition
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.hanasmarcin.kivracoffee.R
import com.hanasmarcin.kivracoffee.databinding.FragmentFiltersBottomSheetBinding
import com.hanasmarcin.kivracoffee.model.FilterItem
import com.hanasmarcin.kivracoffee.model.FilterType
import com.hanasmarcin.kivracoffee.model.SortType
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeFiltersViewModel

class FiltersBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: CoffeeFiltersViewModel by hiltNavGraphViewModels(R.id.coffeeListGraph)

    private var binding: FragmentFiltersBottomSheetBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_KivraCoffee_BottomSheet)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentFiltersBottomSheetBinding.inflate(inflater, container, false)
            .apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initView() = binding?.apply {
        root.layoutTransition =
            LayoutTransition().apply { enableTransitionType(LayoutTransition.CHANGING) }
        chCountry.setOnClickListener {
            viewModel.coffeeFilters.value?.countries?.let { updateChips(it, FilterType.COUNTRY) }
        }
        chIntensifiers.setOnClickListener {
            viewModel.coffeeFilters.value?.intensifiers?.let { updateChips(it, FilterType.INTENSIFIER) }
        }
        chVarieties.setOnClickListener {
            viewModel.coffeeFilters.value?.varieties?.let { updateChips(it, FilterType.VARIETY) }
        }
        chClear.setOnClickListener {
            viewModel.clearAllFilters()
            cgFilterItems.children.forEach { (it as? Chip)?.isChecked = false }
        }
        chDefaultSort.setOnClickListener {
            viewModel.selectSort(SortType.DEFAULT)
        }
        chBlendSort.setOnClickListener {
            viewModel.selectSort(SortType.BLEND_NAME)
        }
        chCountrySort.setOnClickListener {
            viewModel.selectSort(SortType.COUNTRY)
        }
        ivHide.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateChips(data: List<FilterItem>, filterType: FilterType) = binding?.apply {
        // after changing selected filtering category, change filterItems that can be selected
        svFilterItemsChips.scrollTo(0, 0)
        cgFilterItems.removeAllViews()
        TransitionManager.beginDelayedTransition(root)
        data.forEach { item ->
            val chip = createNewChip("${item.name} (${item.count})", item.isSelected).apply {
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.changeFilterItemState(item, filterType, isChecked)
                }
            }
            cgFilterItems.addView(chip)
        }
    }

    private fun createNewChip(title: String, isSelected: Boolean) = Chip(context).apply {
        id = View.generateViewId()
        text = title
        isClickable = true
        isCheckable = true
        isCheckedIconVisible = false
        isFocusable = true
        isChecked = isSelected
        rippleColor = ContextCompat.getColorStateList(context, R.color.kivra_green)
        chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.color_chip_on_primary_surface)
        setTextAppearance(R.style.Theme_KivraCoffee_ChipText)
        setTextColor(ContextCompat.getColorStateList(context, R.color.color_chip_text))
    }

    private fun initViewModel() = with(viewModel) {
        coffeeFilters.observe(viewLifecycleOwner, { filters ->
            binding?.apply {
                if (cgFilterItems.childCount == 0) {
                    when (cgFilterCategories.checkedChipId) {
                        chCountry.id -> updateChips(filters.countries, FilterType.COUNTRY)
                        chIntensifiers.id -> updateChips(filters.intensifiers, FilterType.INTENSIFIER)
                        chVarieties.id -> updateChips(filters.varieties, FilterType.VARIETY)
                    }
                }
                filters.getCountriesCount().let {
                    chCountry.text = resources.getString(R.string.country_label, it.first, it.second)
                }
                filters.getIntensifiersCount().let {
                    chIntensifiers.text = resources.getString(R.string.intensifier_label, it.first, it.second)
                }
                filters.getVarietiesCount().let {
                    chVarieties.text = resources.getString(R.string.variety_label, it.first, it.second)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}