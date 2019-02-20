package com.example.mystructure.ui.activity

import android.os.Bundle
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.example.mystructure.R
import com.example.mystructure.ui.adapter.LoanAdapter
import com.example.mystructure.utils.BaseActivity
import com.example.mystructure.viewmodel.LoanViewModel

class MainActivity : BaseActivity() {

    @BindView(R.id.swipe_refresh)
    protected lateinit var mSwipeRefreshLayout: SwipeRefreshLayout;
    @BindView(R.id.rv_loan)
    protected lateinit var mRvLoan: RecyclerView
    @BindView(R.id.parent)
    protected lateinit var mParent: CoordinatorLayout
    @BindView(R.id.tv_item_size)
    protected lateinit var mItemSize: TextView

    private lateinit var mAdapter: LoanAdapter
    private lateinit var mViewModel: LoanViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        mViewModel = ViewModelProviders.of(this).get(LoanViewModel::class.java)

        mAdapter = LoanAdapter()
        mRvLoan.layoutManager = LinearLayoutManager(this)
        mRvLoan.itemAnimator = DefaultItemAnimator()
        mRvLoan.adapter = mAdapter

        mViewModel.fetchLoan()
        mSwipeRefreshLayout.isRefreshing = true

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorBlue),
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorGreen),
                ContextCompat.getColor(this, R.color.colorRed))

        mSwipeRefreshLayout.setOnRefreshListener {
            mViewModel.fetchLoan()
        }

        mViewModel.mLoanLiveData.observe(this, Observer { loans ->
            mAdapter.clearItems()

            if (mSwipeRefreshLayout.isRefreshing) {
                mSwipeRefreshLayout.isRefreshing = false
            }
            mAdapter.addItems(loans)
            setItemSize(loans.size)

        })

        mViewModel.mErrorMessage.observe(this, Observer { message ->
            if (mSwipeRefreshLayout.isRefreshing) {
                mSwipeRefreshLayout.isRefreshing = false
            }
            showErrorMessage(message, mParent)
        })
    }

    private fun setItemSize(size : Int){
        val text = size.toString()+"/"+size.toString()
        mItemSize.text = text
    }

}
