package com.example.mystructure.ui.holder

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.mystructure.R
import com.example.mystructure.model.Loan
import kotlinx.android.synthetic.main.item_loan.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LoanViewHolder(view : View) : RecyclerView.ViewHolder(view),bindLoanHolder{

    private var locale = Locale("in","ID")
    private var formatter = SimpleDateFormat("dd MMM yyyy", locale)
    private var inputFormat = SimpleDateFormat("yyyy-MM-dd",locale)

    var mJatuhTempo = view.findViewById<TextView>(R.id.tv_date)
    var mAmount = view.findViewById<TextView>(R.id.tv_total_amount)
    var mStatus = view.findViewById<TextView>(R.id.tv_loan_status)
    var mLoanDate = view.findViewById<TextView>(R.id.tv_loan_date)

    override fun bindView(item: Loan) {
        val dueDate : Date = inputFormat.parse(item.due_date)
        val date : Date = inputFormat.parse(item.date)

        mJatuhTempo.text = formatter.format(dueDate)
        mAmount.text = item.loan_total
        mStatus.text = item.status
        mLoanDate.text = formatter.format(date)

        itemView.setOnClickListener {
            Toast.makeText(itemView.context,item.loan_total,Toast.LENGTH_LONG).show()
        }
    }

}

interface bindLoanHolder{
    fun bindView(item : Loan)
}