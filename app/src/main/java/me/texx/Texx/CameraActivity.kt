package me.texx.Texx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.RED
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
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
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


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
        setGestures()
        setListeners()
    }

    private fun setGestures() {
        camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER)
        camera.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE)
        camera.mapGesture(Gesture.SCROLL_HORIZONTAL, GestureAction.EXPOSURE_CORRECTION)
    }

    private fun setListeners() {
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                val file: File? = createFile()
                writeFile(BitmapFactory.decodeByteArray(jpeg, 0, jpeg!!.size), file)
                startActivity(intentFor<MediaPreviewActivity>("filepath" to file.toString()))
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

    /**
     * Saves [bitmap] in local storage and returns the absolute path [String] of the file
     */
    private fun writeFile(bitmap: Bitmap, file: File?) {
        if (file == null) {
            longToast("Error creating file - please check the storage permission.)")
            return
        }
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
        } catch (e: FileNotFoundException) {
            longToast("File not found - please try again.\n(${e.message})")
        } catch (e: IOException) {
            longToast("Error accessing file - please try again.\n(${e.message})")
        }
    }

    /**
     * Creates empty file to write the file on
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

    private fun generateFilename(): String {
        val timestamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        return "Texx_$timestamp.jpg"
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
