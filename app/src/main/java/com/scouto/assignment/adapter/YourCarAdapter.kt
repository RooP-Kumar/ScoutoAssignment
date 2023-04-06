package com.scouto.assignment.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.scouto.assignment.R
import com.scouto.assignment.data.model.YourCar
import java.io.File

class YourCarAdapter(private val context : Context, private val onImage: OnImage, private val onDelete: OnDelete): RecyclerView.Adapter<YourCarAdapter.MyViewHolder>() {

    private val data : ArrayList<YourCar> = arrayListOf()

    inner class MyViewHolder(item : View) : RecyclerView.ViewHolder(item) {
        val makeTV : TextView = item.findViewById(R.id.carMakeTextView)
        val modelTV : TextView = item.findViewById(R.id.carModelTextView)
        val image : ImageView = item.findViewById(R.id.carImage)
        private val addImage : MaterialButton = item.findViewById(R.id.add_image)
        private val deleteCar : MaterialButton = item.findViewById(R.id.deleteCar)
        init {
            addImage.setOnClickListener {
                onImage.onClick(adapterPosition, data)
            }

            deleteCar.setOnClickListener {
                onDelete.onClick(adapterPosition, data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.car_item, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.apply {
            val item = data[position]
            makeTV.text = item.make
            modelTV.text = item.model
            if(item.image.isNotEmpty()) {
                val file = File(item.image)
                if (file.exists()){
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    image.setImageBitmap(bitmap)
                } else {
                    image.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_car))
                }

            } else {
                image.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_car))
            }
        }
    }

    fun datasetChange(newList : List<YourCar>){
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    interface OnDelete{
        fun onClick(position: Int, data: List<YourCar>)
    }

    interface OnImage{
        fun onClick(position: Int, data: List<YourCar>)
    }
}