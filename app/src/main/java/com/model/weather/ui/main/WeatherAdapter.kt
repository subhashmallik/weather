package com.model.weather.ui.main

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.model.weather.R
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.math.roundToInt

class WeatherAdapter(private val mContext: Context, val map: LinkedHashMap<String, ArrayList<Double>>) : RecyclerView.Adapter<WeatherAdapter.ItemHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val indexes: List<String> = ArrayList<String>(map.keys)
        holder.dayText.text = indexes[position]
        holder.tempText.text =  mContext.resources.getString(R.string.deg, map[indexes[position]]!!.average().roundToInt().toString())
    }


    override fun getItemCount(): Int {
        val indexes: List<String> = ArrayList<String>(map.keys)
       return indexes.size
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val dayText: TextView = itemView.findViewById(R.id.day_text)
        val  tempText: TextView = itemView.findViewById(R.id.temp_text)
    }
}