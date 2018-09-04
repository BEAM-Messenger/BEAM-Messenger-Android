package me.texx.Texx

import android.content.Context
import android.graphics.Color.RED
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import daio.io.dresscode.dressCodeName
import daio.io.dresscode.matchDressCode
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.ViewType
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

    private var currentlyDrawing = false
    private var currentlyTyping = false
    private var typingColor = RED

    private fun initPhotoEditor() {
        val filepath = intent.getStringExtra("filepath")
        val imageEditorView = findViewById<PhotoEditorView>(R.id.image_editor)
        imageEditorView.source.setImageURI(Uri.parse(filepath))

        val photoEditor = PhotoEditor.Builder(this, imageEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(Typeface.DEFAULT)
                .build()

        setButtonListeners(photoEditor)
    }

    private fun setButtonListeners(photoEditor: PhotoEditor) {

        // undo button
        button_undo.setOnClickListener { photoEditor.undo() }

        // redo button
        button_redo.setOnClickListener { photoEditor.redo() }

        // draw button
        button_draw.setOnClickListener {
            currentlyDrawing = !currentlyDrawing
            photoEditor.setBrushDrawingMode(currentlyDrawing)

            if (currentlyDrawing) seekbar_color.visibility = View.VISIBLE
            else {
                seekbar_color.visibility = View.GONE
                button_draw.background = ContextCompat.getDrawable(this, R.drawable.ic_mode_edit_white_24dp)
            }
        }

        // type button
        button_type.setOnClickListener {
            currentlyTyping = !currentlyTyping
            if (currentlyTyping) showTextEditor("")
            else hideTextEditor()
        }

        // text editing "view" for on photo typing
        text_edit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                photoEditor.addText(text_edit.text.toString(), typingColor)
                text_edit.visibility = View.GONE
            }
            return@setOnEditorActionListener true
        }

        // text editing box on photo (long click)
        photoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {
            override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {
                currentlyTyping = !currentlyTyping
                if (!currentlyTyping) showTextEditor("") // TODO: Fix editing of text
                else hideTextEditor()
            }

            override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(numberOfAddedViews: Int) {}
            override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {}
            override fun onStartViewChangeListener(viewType: ViewType) {}
            override fun onStopViewChangeListener(viewType: ViewType) {}
        })

        // color seekbar
        seekbar_color.setOnColorChangeListener { _, _, color ->
            if (currentlyDrawing) {
                photoEditor.brushColor = color
                button_draw.setBackgroundColor(color)
            } else if (currentlyTyping) {
                typingColor = color
                button_type.setBackgroundColor(color)
            }
        }
    }

    private fun showTextEditor(text: String) {
        seekbar_color.visibility = View.VISIBLE
        text_edit.visibility = View.VISIBLE
        text_edit.imeOptions = EditorInfo.IME_ACTION_DONE
        text_edit.setText(text)
        text_edit.requestFocusFromTouch() // set focus
        val inputManager = this@PhotoEditorActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(text_edit, 0)
    }

    private fun hideTextEditor() {
        button_type.background = ContextCompat.getDrawable(this, R.drawable.ic_text_fields_white_24dp)
        seekbar_color.visibility = View.GONE
        text_edit.visibility = View.GONE
    }
}
