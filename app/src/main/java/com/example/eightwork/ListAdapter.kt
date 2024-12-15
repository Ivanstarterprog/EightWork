package com.example.eightwork

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide

const val VIEW_TYPE_HOT = 0;
const val VIEW_TYPE_COLD = 1;

class DayListAdapter() : ListAdapter<DayPrognosis, ViewHolder>(DayDiffCallback()) {

    inner class DayViewHolderHot(view: View) : RecyclerView.ViewHolder(view) {
        val datetime: TextView = view.findViewById<TextView>(R.id.datetime);
        val plusTxt: TextView = view.findViewById<TextView>(R.id.txt_plus_temperature)
        val icon: ImageView = view.findViewById<ImageView>(R.id.icon)

        fun bind(position: Int) {
            val hotDay: DayPrognosis = currentList[position]
            datetime.setText(hotDay.dt_txt)
            plusTxt.setText(hotDay.main.getTempAsString());
            val address = "https://openweathermap.org/img/wn/${hotDay.weather[0].icon}@2x.png"
            Glide.with(icon).load(address).into(icon)
        }
    }

    inner class DayViewHolderCold(view: View) : RecyclerView.ViewHolder(view) {
        val datetime: TextView = view.findViewById<TextView>(R.id.datetime);
        val minusTxt: TextView = view.findViewById<TextView>(R.id.txt_minus_temperature)
        val icon: ImageView = view.findViewById<ImageView>(R.id.icon)

        fun bind(position: Int) {
            val coldDay: DayPrognosis = currentList[position]
            datetime.setText(coldDay.dt_txt)
            minusTxt.setText(coldDay.main.getTempAsString());
            val address = "https://openweathermap.org/img/wn/${coldDay.weather[0].icon}@2x.png"
            Glide.with(icon).load(address).into(icon)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val day: DayPrognosis = currentList[position]
        if (day.main.temp > 0) {
            return VIEW_TYPE_HOT
        }
        return VIEW_TYPE_COLD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == VIEW_TYPE_HOT) {
            return DayViewHolderHot(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.r_item_hot,
                    parent, false
                )
            )
        }
        return DayViewHolderCold(
            LayoutInflater.from(parent.context).inflate(
                R.layout.r_item_cold,
                parent, false
            )
        )
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = currentList[position]
        if(holder.itemViewType == VIEW_TYPE_HOT){
            (holder as DayViewHolderHot).bind(position)
        }
        else{
            (holder as DayViewHolderCold).bind(position)
        }
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