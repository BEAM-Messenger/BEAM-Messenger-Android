package me.texx.Texx

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import me.texx.Texx.util.ThemeUtil.getThemeName

/**
 * Activity which will be shown after you've taken a picture
 * Previews the taken picture and posts it if you want
 */
class MediaPreviewActivity : AppCompatActivity() {
    /**
     * Set initial configuration
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        matchDressCode()
        super.onCreate(savedInstanceState)
        dressCodeName = getThemeName(this)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_media_preview)

        val filepath = intent.getStringExtra("filepath")
        val imageView = findViewById<ImageView>(R.id.imagePreview)
        imageView.setImageURI(Uri.parse(filepath))
    }
}
