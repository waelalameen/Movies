package tech.wa.moviessample.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import android.content.ComponentName
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StyleRes
import androidx.core.content.getSystemService
import androidx.core.util.Preconditions
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import tech.wa.moviessample.R
import tech.wa.moviessample.ui.activities.HiltTestActivity

fun View.visible(predicate: Boolean) {
    visibility = if (predicate) View.VISIBLE else View.GONE
}

fun Fragment.pop() {
    Navigation.findNavController(view!!).popBackStack()
}

fun Activity.setStatusBarColor(colorResource: Int, lightText: Boolean) {
    window.statusBarColor = getColor(colorResource)
    if (lightText) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    else window.decorView.systemUiVisibility =
        window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
}

fun Activity.hideKeyboard(view: View) {
    try {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    } catch (ignore: Exception) { }
}

/**
 * launchFragmentInContainer from the androidx.fragment:fragment-testing library
 * is NOT possible to use right now as it uses a hardcoded Activity under the hood
 * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
 *
 * As a workaround, use this function that is equivalent. It requires you to add
 * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
 * as can be found in this project.
 */
@SuppressLint("RestrictedApi")
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    navGraphId: Int? = null,
    @StyleRes themeResId: Int = R.style.Theme_MoviesSample,
    crossinline action: Fragment.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        navGraphId?.let {
            val inflater = navController.navInflater
            val graph = inflater.inflate(it)
            navController.graph = graph
        }

        fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
            if (viewLifecycleOwner != null) {
                Navigation.setViewNavController(fragment.requireView(), navController)
            }
        }

        fragment.action()
    }
}