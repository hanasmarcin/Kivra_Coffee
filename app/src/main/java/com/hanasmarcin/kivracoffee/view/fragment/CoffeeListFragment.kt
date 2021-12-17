package com.hanasmarcin.kivracoffee.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialFadeThrough
import com.hanasmarcin.kivracoffee.R
import com.hanasmarcin.kivracoffee.databinding.FragmentCoffeeListBinding
import com.hanasmarcin.kivracoffee.view.adapter.CoffeeListAdapter
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeFiltersViewModel
import com.hanasmarcin.kivracoffee.viewmodel.CoffeeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CoffeeListFragment : Fragment() {

    private val viewModel: CoffeeListViewModel by viewModels()
    private val coffeeFiltersViewModel: CoffeeFiltersViewModel by hiltNavGraphViewModels(R.id.coffeeListGraph)
    private var binding: FragmentCoffeeListBinding? = null

    private val coffeeListAdapter by lazy {
        CoffeeListAdapter({
            viewModel.getFlagForCountryName(it)
        }) { coffeeModel, coffeeBagView ->
            // onClickListener for rv item - navigate to details page
            val extras = FragmentNavigatorExtras(coffeeBagView to coffeeBagView.transitionName)
            findNavController().navigate(
                CoffeeListFragmentDirections.actionCoffeeListFragmentToCoffeeDetailsFragment(coffeeModel), extras)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentCoffeeListBinding.inflate(inflater, container, false)
            .apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            rvCoffeeList.apply {
                adapter = coffeeListAdapter
                itemAnimator = object : DefaultItemAnimator() {
                    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
                        super.onAnimationFinished(viewHolder)
                        smoothScrollToPosition(0)
                    }
                }
            }
            ivFabBlob.setOnClickListener {
                findNavController().navigate(CoffeeListFragmentDirections.actionCoffeeListFragmentToFiltersBottomSheetFragment())
            }
        }
        // fragment entering animation is postponed to just before the layout is being drawn
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        initViewModel()
        initCoffeeFiltersViewModel()
    }

    private fun initViewModel() = with(viewModel) {
        coffeeList.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                setupErrorLayout(resources.getString(R.string.no_data_found))
            } else {
                // there are new items from db/api
                binding?.apply {
                    rvCoffeeList.visibility = VISIBLE
                    shimmerCoffeeList.root.visibility = INVISIBLE
                    emptyLayout.root.visibility = INVISIBLE
                }
                coffeeListAdapter.submitList(it)
            }
        })
        coffeeFilters.observe(viewLifecycleOwner, {
            coffeeFiltersViewModel.initFilters(it)
        })
        isError.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                setupErrorLayout(resources.getString(R.string.error_fetching_data))
            }
        })
        isLoading.observe(viewLifecycleOwner, {
            setupLoadingLayout(it)
        })
    }

    private fun setupErrorLayout(errorMsg: String) {
        binding?.emptyLayout?.apply {
            root.visibility = VISIBLE
            tvError.text = errorMsg
        }
        binding?.apply {
            rvCoffeeList.visibility = INVISIBLE
            shimmerCoffeeList.root.visibility = INVISIBLE
        }
    }

    private fun setupLoadingLayout(isLoading: Boolean?) {
        if (isLoading != false) binding?.apply {
            shimmerCoffeeList.root.visibility = VISIBLE
        } else binding?.apply {
            shimmerCoffeeList.root.visibility = INVISIBLE
        }
    }

    private fun initCoffeeFiltersViewModel() = with(coffeeFiltersViewModel) {
        coffeeFilters.observe(viewLifecycleOwner, { filters ->
            viewModel.filterCoffees(filters)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
