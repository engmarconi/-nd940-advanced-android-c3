package com.udacity.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.udacity.R
import com.udacity.utils.IS_SUCCESS_KEY
import com.udacity.utils.REPO_NAME_KEY
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val motionLayout = findViewById<MotionLayout>(R.id.motion_layout)
        val bundle = intent.extras
        if (bundle != null) {
            filenameText.text = bundle.getString(REPO_NAME_KEY)
            statusText.text = if (bundle.getBoolean(IS_SUCCESS_KEY)) resources.getString(R.string.success)           else resources.getString(R.string.fail)
            statusText.setTextColor(if (bundle.getBoolean(IS_SUCCESS_KEY)) resources.getColor(R.color.colorPrimaryDark, resources.newTheme())
            else  resources.getColor(R.color.red, resources.newTheme()))
        }
    }

}
