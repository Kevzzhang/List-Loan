package com.example.mystructure.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mystructure.R
import com.example.mystructure.model.Loan
import com.example.mystructure.ui.holder.LoanViewHolder

class LoanAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var mItems : MutableList<Loan> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LoanViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_loan,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val loanHolder = holder as LoanViewHolder
        loanHolder.bindView(mItems.get(position))
    }

    fun addItems(item : List<Loan>){
        mItems.addAll(item)
        notifyItemRangeInserted(mItems.size, itemCount)
    }

    fun clearItems(){
        mItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

}
