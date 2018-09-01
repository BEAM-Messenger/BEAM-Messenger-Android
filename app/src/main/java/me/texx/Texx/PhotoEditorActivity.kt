package me.texx.Texx

import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import kotlinx.android.synthetic.main.activity_photo_editor.*
import me.texx.Texx.util.ThemeUtil.getThemeName

/**
 * Activity which will be shown after you've taken a picture
 * Previews the taken picture and posts it if you want
 */
class PhotoEditorActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_photo_editor)

        initPhotoEditor()
    }

    private fun initPhotoEditor() {
        val filepath = intent.getStringExtra("filepath")
        val imageEditorView = findViewById<PhotoEditorView>(R.id.imageEditor)
        imageEditorView.source.setImageURI(Uri.parse(filepath))

        val photoEditor = PhotoEditor.Builder(this, imageEditorView)
                .setPinchTextScalable(true)
                .build()

        setButtonListeners(photoEditor)
    }

    private fun setButtonListeners(photoEditor: PhotoEditor) {
        var currentlyDrawing = false

        undoButton.setOnClickListener { photoEditor.undo() }
        redoButton.setOnClickListener { photoEditor.redo() }

        photoDrawButton.setOnClickListener {
            currentlyDrawing = !currentlyDrawing
            photoEditor.setBrushDrawingMode(currentlyDrawing)

            if (currentlyDrawing) drawColorSeekbar.visibility = View.VISIBLE
            else {
                drawColorSeekbar.visibility = View.GONE
                photoDrawButton.background = ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_white_24dp)
            }
        }

        drawColorSeekbar.setOnColorChangeListener { _, _, color ->
            if (currentlyDrawing) {
                photoEditor.brushColor = color
                photoDrawButton.setBackgroundColor(color)
            }
        }
    }
}
