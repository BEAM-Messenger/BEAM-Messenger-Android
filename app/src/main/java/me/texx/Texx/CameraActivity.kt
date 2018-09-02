package me.texx.Texx

import android.graphics.Color.RED
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
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
import me.texx.Texx.util.ThemeUtil.getThemeName
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Activity to either record a video or take a photo
 * Output will be saved and redirected to corresponding preview activity
 */
class CameraActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_camera)

        initCameraLayout()
    }

    private fun initCameraLayout() {
        camera.sessionType = SessionType.PICTURE
        setGestures()
        setListeners()
    }

    private fun setGestures() {
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
        camera.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE)
        camera.mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.EXPOSURE_CORRECTION)
    }

    private val takingPicture = camera.sessionType == SessionType.PICTURE

    private fun setListeners() {
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                val file: File? = createFile()
                SaveFileTask(file).execute(jpeg)
                startActivity(intentFor<PhotoEditorActivity>("filepath" to file.toString()))
            }
        })

        camera_button.setOnClickListener {
            if (takingPicture)
                camera.capturePicture()
            else
                camera.startCapturingVideo()
        }

        camera_button.setOnLongClickListener {
            if (takingPicture) {
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

    /**
     * Saves [ByteArray] in [file]
     */
    internal inner class SaveFileTask(private val file: File?) : AsyncTask<ByteArray, String, String>() {
        override fun doInBackground(vararg jpeg: ByteArray): String? {
            try {
                val out = FileOutputStream(file!!.path)

                out.write(jpeg[0])
                out.close()
            } catch (e: java.io.IOException) {
                longToast("Exception in photoCallback $e")
            }

            return null
        }
    }

    /**
     * Creates empty [File] to write the file on
     */
    private fun createFile(): File? {
        val mediaStorageDir = File(Environment.getExternalStorageDirectory().toString()
                + "/Android/media/"
                + applicationContext.packageName
                + "/Images")

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val mediaFile: File
        mediaFile = File(mediaStorageDir.path + File.separator + generateFilename())
        return mediaFile
    }

    /**
     * Generates filename [String] by using current timestamp
     */
    private fun generateFilename(): String {
        val timestamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        return "Texx_$timestamp.jpg"
    }

    /**
     * Start components on activity resume (called at start)
     */
    override fun onResume() {
        super.onResume()
        camera.start()
    }

    /**
     * Stop components on activity pause
     */
    override fun onPause() {
        super.onPause()
        camera.stop()
    }

    /**
     * Destroy components on activity close
     */
    override fun onDestroy() {
        super.onDestroy()
        camera.destroy() // doesn't really destroys your camera lol
    }
}
