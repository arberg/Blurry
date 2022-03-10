package jp.wasabeef.example.blurry

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import jp.wasabeef.blurry.Blurry

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    
    Toast.makeText(this, "Touch 'BLURRY' to blur the dogs", Toast.LENGTH_LONG).show()

    var performanceTestDone = false
    val fullscreenImageView = findViewById<ImageView>(R.id.fullscreen_invisible)
    fullscreenImageView.doOnNextLayout { // Prevent crash when phone is locked/screen off (or random ?)
      fullscreenImageView.doOnPreDraw {
        Blurry.with(this)
          .radius(25)
          .sampling(1)
          .capture(fullscreenImageView)
          .getAsync {
            fullscreenImageView.setImageDrawable(BitmapDrawable(resources, it))
            performanceTestDone=true
          }
      }
    }

    findViewById<View>(R.id.button).setOnClickListener {
      if (!performanceTestDone) {
        Toast.makeText(this, "Please wait for performance test to finish (see logs)", Toast.LENGTH_LONG).show()
        return@setOnClickListener
      }
      val startMs = System.currentTimeMillis()
      Blurry.with(this)
        .radius(25)
        .sampling(1)
        .color(Color.argb(66, 0, 255, 255))
        .async()
        .capture(findViewById(R.id.right_top))
        .into(findViewById(R.id.right_top))

      val bitmap = Blurry.with(this)
        .radius(10)
        .sampling(8)
        .capture(findViewById(R.id.right_bottom)).get()
      findViewById<ImageView>(R.id.right_bottom).setImageDrawable(BitmapDrawable(resources, bitmap))

      Blurry.with(this)
        .radius(25)
        .sampling(4)
        .color(Color.argb(66, 255, 255, 0))
        .capture(findViewById(R.id.left_bottom))
        .getAsync {
          findViewById<ImageView>(R.id.left_bottom).setImageDrawable(BitmapDrawable(resources, it))
        }

      Log.d(getString(R.string.app_name),
        "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
    }

    findViewById<View>(R.id.button).setOnLongClickListener(object : View.OnLongClickListener {

      private var blurred = false

      override fun onLongClick(v: View): Boolean {
        if (blurred) {
          Blurry.delete(findViewById(R.id.content))
        } else {
          val startMs = System.currentTimeMillis()
          Blurry.with(this@MainActivity)
            .radius(25)
            .sampling(2)
            .async()
            .animate(500)
            .onto(findViewById<View>(R.id.content) as ViewGroup)
          Log.d(getString(R.string.app_name),
            "TIME " + (System.currentTimeMillis() - startMs).toString() + "ms")
        }

        blurred = !blurred
        return true
      }
    })
  }
}
