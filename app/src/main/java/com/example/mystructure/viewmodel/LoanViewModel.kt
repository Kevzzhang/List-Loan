package com.example.mystructure.viewmodel

import RetrofitSingleton
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mystructure.R
import com.example.mystructure.model.Loan
import com.example.mystructure.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoanViewModel(application: Application) : AndroidViewModel(application) {

    var mErrorMessage = MutableLiveData<String>()
    private var mCall: Call<List<Loan>>? = null
    var mLoanLiveData: MutableLiveData<List<Loan>> = MutableLiveData()
    private var mLoans: MutableList<Loan> = ArrayList()

    fun fetchLoan() {
        if (isNetworkConnected()) {
            mCall = RetrofitSingleton.getInstance().getRestApi().getLoan()
            mCall?.enqueue(object : Callback<List<Loan>> {
                override fun onResponse(call: Call<List<Loan>>, response: Response<List<Loan>>) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.size > 0) {
                            mLoans.clear()
                            for (item in response.body()!!) {
                                val loan = Loan()
                                loan.date = item.date
                                loan.due_date = item.due_date
                                loan.status = item.status
                                loan.loan_total = item.loan_total
                                mLoans.add(loan)
                            }
                            mLoanLiveData.value = mLoans
                        } else {
                            mErrorMessage.value = getApplication<Application>().applicationContext.getString(R.string.no_data)
                        }

                    } else {
                        mErrorMessage.value = getApplication<Application>().applicationContext.getString(R.string.server_error)
                    }
                }

                override fun onFailure(call: Call<List<Loan>>, t: Throwable) {
                    mErrorMessage.value = t.message
                }
            })
        } else {
            mErrorMessage.value = getApplication<Application>().applicationContext.getString(R.string.no_internet)
        }
    }

    fun cancelCall(){
        if(mCall != null){
            mCall!!.cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelCall()
    }

    private fun isNetworkConnected(): Boolean {
        return NetworkUtils.checkConnection(getApplication())
    }
}