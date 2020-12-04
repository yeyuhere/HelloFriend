package edu.cs371m.hellofriend

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction


class MainActivity
    : AppCompatActivity()
{
//    val viewModel: MainViewModel by viewModels()
    private lateinit var homeFragment: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        val authInitIntent = Intent(this, AuthInitActivity::class.java)
        startActivity(authInitIntent)

        homeFragment = HomeFragment.newInstance()
        initHomeFragment()
    }


    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0);
    }

    private fun initHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_fragment, homeFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

    }
}

