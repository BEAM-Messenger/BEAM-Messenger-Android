package me.texx.Texx

import android.graphics.BitmapFactory
import android.graphics.Color.RED
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import com.otaliastudios.cameraview.SessionType
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_media_preview.*
import me.texx.Texx.util.ThemeUtil.getThemeName
import org.jetbrains.anko.startActivity

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        matchDressCode()
        super.onCreate(savedInstanceState)
        dressCodeName = getThemeName(this)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_camera)

        initCameraLayout()
    }

    private fun initCameraLayout() {
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
        camera.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE)
        camera.mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.EXPOSURE_CORRECTION)

        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray) {
                imagePreview.setImageBitmap(BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size))
                startActivity<MediaPreviewActivity>()
            }
        })

        camera_button.setOnClickListener {
            camera.capturePicture()
        }

        camera_button.setOnLongClickListener {
            if (camera.sessionType == SessionType.PICTURE) {
                camera.sessionType = SessionType.VIDEO
                val videoButtonDrawable: Drawable = this.resources.getDrawable(R.drawable.focus_marker_outline)
                videoButtonDrawable.colorFilter = PorterDuffColorFilter(RED, PorterDuff.Mode.SRC_IN)
                camera_button.setBackgroundDrawable(videoButtonDrawable)
            } else {
                camera.sessionType = SessionType.PICTURE
                camera_button.setBackgroundDrawable(this.resources.getDrawable(R.drawable.focus_marker_outline))
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        camera.start()
    }

    override fun onPause() {
        super.onPause()
        camera.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.destroy()
    }
}
