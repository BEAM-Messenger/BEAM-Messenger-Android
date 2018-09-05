package me.texx.Texx

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.WindowManager
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntro2Fragment
import org.jetbrains.anko.startActivity


/**
 * Activity to show basic information and ask for permissions
 */
class IntroActivity : AppIntro2() {
    /**
     * Set initial configuration
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        createSlides()
        setPermissionRequests()
        showSkipButton(false)
        setColorTransitionsEnabled(true)
    }

    private fun setPermissionRequests() {
        askForPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        setSwipeLock(false)
    }

    private fun createSlides() {
        addSlide(AppIntro2Fragment.newInstance("Texx", "For the people", R.drawable.ic_logo_placeholder, Color.parseColor("#304ffe")))
        addSlide(AppIntro2Fragment.newInstance("Camera", "If you want to take and upload pictures, please allow us to do so.",
                R.drawable.ic_logo_placeholder, Color.parseColor("#1976d2")))
        addSlide(AppIntro2Fragment.newInstance("Log in", "Please register or login now.", R.drawable.ic_logo_placeholder, Color.parseColor("#29b6f6")))
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        startActivity<RoutingActivity>()
    }

}
