/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.plantdetail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ShareCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.databinding.FragmentPlantDetailBinding
import com.google.samples.apps.sunflower.utilities.InjectorUtils
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel

/**
 * A fragment representing a single Plant detail screen.
 */
class PlantDetailFragment : Fragment() {

    private val args: PlantDetailFragmentArgs by navArgs()

    private val plantDetailViewModel: PlantDetailViewModel by viewModels {
        InjectorUtils.providePlantDetailViewModelFactory(requireActivity(), args.plantId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentPlantDetailBinding>(
            inflater, R.layout.fragment_plant_detail, container, false
        ).apply {
            // ...
            composeView.apply {
                // Dispose the Composition when the view's LifecycleOwner
                // is destroyed
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                setContent {
                    // You're in Compose world!
                    MdcTheme {
                        PlantDetailDescription(plantDetailViewModel)
                    }
                }
            }
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    // Helper function for calling a share functionality.
    // Should be used when user presses a share button/menu item.
    @Suppress("DEPRECATION")
    private fun createShareIntent() {
        val shareText = plantDetailViewModel.plant.value.let { plant ->
            if (plant == null) {
                ""
            } else {
                getString(R.string.share_text_plant, plant.name)
            }
        }
        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity()).setText(shareText)
            .setType("text/plain").createChooserIntent()
            .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(shareIntent)
    }

    // FloatingActionButtons anchored to AppBarLayouts have their visibility controlled by the scroll position.
    // We want to turn this behavior off to hide the FAB when it is clicked.
    //
    // This is adapted from Chris Banes' Stack Overflow answer: https://stackoverflow.com/a/41442923
    private fun hideAppBarFab(fab: FloatingActionButton) {
        val params = fab.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as FloatingActionButton.Behavior
        behavior.isAutoHideEnabled = false
        fab.hide()
    }

    interface Callback {
        fun add(plant: Plant?)
    }
}

