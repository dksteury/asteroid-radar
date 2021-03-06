package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModel.Factory(activity.application)).get(MainViewModel::class.java)
//        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = AsteroidAdapter(AsteroidListener { asteroid ->
            viewModel.onAsteroidClicked(asteroid)
        })
        binding.asteroidRecycler.adapter = adapter

        viewModel.navigateToDetail.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onAsteroidNavigated()
            }
        })

        viewModel.asteroids.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.networkError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        it,
                        Snackbar.LENGTH_INDEFINITE
                        )
                        .setAction(R.string.retry) {
                            viewModel.retry()
                        }
                        .show()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

//    private fun displayNetworkErrorDialog() {
//        val builder = AlertDialog.Builder(requireActivity())
//                .setMessage(getString(R.string.network_error_explanation))
//                .setPositiveButton(R.string.retry, viewModel.retry())
//                .setMessage(getString(R.string.astronomica_unit_explanation))
//                .setPositiveButton(android.R.string.ok, null)
//        builder.create().show()
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
                when (item.itemId) {
                    R.id.show_today_menu -> AsteroidFilter.TODAY
                    R.id.show_week_menu -> AsteroidFilter.WEEK
                    else -> AsteroidFilter.ALL
                }
        )
        return true
    }
}
