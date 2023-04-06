package com.scouto.assignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.scouto.assignment.R
import com.scouto.assignment.data.model.singlemake.SingleMakeResult

class ModelAdapter(val onClick: onItemClick): RecyclerView.Adapter<ModelAdapter.MyViewHolder>() {
    val data : ArrayList<SingleMakeResult> = arrayListOf()
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView : TextView = itemView.findViewById(R.id.modelTv)
        val mainlayout : ConstraintLayout = itemView.findViewById(R.id.modelupparlayout)
        init {
            mainlayout.setOnClickListener {
                onClick.onClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dialog_model_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = data[position].Model_Name
    }

    fun datasetChanged(newList: List<SingleMakeResult>){
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface onItemClick{
        fun onClick(position: Int)
    }
}