package com.example.mystructure.utils
import android.annotation.SuppressLint
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mystructure.R
import com.google.android.material.snackbar.Snackbar

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private var mSnackbar: Snackbar? = null


    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showErrorMessage(message: String, parent: CoordinatorLayout) {
        mSnackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG)
        val v = mSnackbar!!.getView()
        v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed))
        mSnackbar!!.show()
    }

    fun showSwipeRefresh(mSwipeRefresh: SwipeRefreshLayout) {
        if (true)
            mSwipeRefresh.isRefreshing = true
    }

    fun hideSwipeRefresh(mSwipeRefresh: SwipeRefreshLayout) {
        if (true)
            mSwipeRefresh.isRefreshing = false
    }

}