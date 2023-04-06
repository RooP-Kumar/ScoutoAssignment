package com.scouto.assignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scouto.assignment.R
import com.scouto.assignment.data.model.makes.AllMakesResult

class MakesAdapter(val onClick: onItemClick): RecyclerView.Adapter<MakesAdapter.MyViewHolder>() {
    val data : ArrayList<AllMakesResult> = arrayListOf()
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.findViewById(R.id.item_text_view)
        val mainlayout : RelativeLayout = itemView.findViewById(R.id.upparlayout)
        init {
            mainlayout.setOnClickListener {
                onClick.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dialog_makes_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = data[position].Make_Name
    }

    fun datasetChanged(newList: List<AllMakesResult>){
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface onItemClick{
        fun onClick(position: Int)
    }
}