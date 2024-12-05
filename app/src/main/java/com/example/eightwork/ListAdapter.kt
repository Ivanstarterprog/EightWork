package com.example.eightwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val datetime: TextView = view.findViewById<TextView>(R.id.datetime);
    val minusTxt: TextView = view.findViewById<TextView>(R.id.txt_minus_temperature)
    val plusTxt: TextView = view.findViewById<TextView>(R.id.txt_plus_temperature)
    val icon: ImageView = view.findViewById<ImageView>(R.id.icon)
}

class DayListAdapter() : ListAdapter<DayPrognosis, DayViewHolder>(DayDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.r_item,
            parent, false);
        return DayViewHolder(view);
    }
    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = currentList[position]

        holder.datetime.setText(day.dt_txt)

        if(day.main.temp < 0){
            holder.minusTxt.setText(day.main.getTempAsString())
            holder.plusTxt.setText("")
        }
        else{
            holder.plusTxt.setText(day.main.getTempAsString())
            holder.minusTxt.setText("")
        }
        val address = "https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png"
        Glide.with(holder.icon).load(address).into(holder.icon)

    }
}

class DayDiffCallback : DiffUtil.ItemCallback<DayPrognosis>() {
    override fun areItemsTheSame(oldItem: DayPrognosis, newItem: DayPrognosis): Boolean {
        return oldItem.dt_txt == newItem.dt_txt;
    }

    override fun areContentsTheSame(oldItem: DayPrognosis, newItem: DayPrognosis): Boolean {
        return oldItem == newItem;
    }
}