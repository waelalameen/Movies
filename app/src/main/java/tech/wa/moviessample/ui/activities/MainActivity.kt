package tech.wa.moviessample.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import tech.wa.moviessample.R
import tech.wa.moviessample.databinding.ActivityMainBinding
import tech.wa.moviessample.extensions.setStatusBarColor
import tech.wa.moviessample.extensions.setupWithNavController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentNavigationController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(R.color.white, lightText = true)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navGraphList = setOf(
            R.navigation.search_navigation,
            R.navigation.favorites_navigation
        )

        currentNavigationController = binding.bottomNavigationView.setupWithNavController(
            navGraphList.toList(), supportFragmentManager, R.id.fragment_container_view, intent
        )
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavigationController()
    }

    override fun onSupportNavigateUp(): Boolean {
        currentNavigationController?.let {
            return it.value?.navigateUp() ?: super.onSupportNavigateUp()
        }
        return super.onSupportNavigateUp()
    }
}