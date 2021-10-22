package com.isotech.goolad.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.isotech.goolad.R
import com.isotech.goolad.data.model.Parameter
import com.isotech.goolad.utils.toString
import kotlinx.android.synthetic.main.item_prediction_result.view.*
import java.util.*


class PredictionResultListAdapter(val onClick: (Parameter) -> Unit) :
    ListAdapter<Parameter, PredictionResultListAdapter.ViewHolder>(TaskDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_prediction_result, parent, false
        )
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: Parameter) = with(itemView) {
            text_prediction_result.text = data.diagnosis
            text_prediction_date.text = data.timestamp.toString("dd MMMM yyyy HH:mm", Locale("id", "ID"))
            if (data.diagnosis!!.contains("tidak", true)) {
                text_prediction_result.setTextColor((resources.getColor(R.color.green)))
            } else {
                text_prediction_result.setTextColor((resources.getColor(R.color.red)))
            }
            setOnClickListener { onClick(data) }
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Parameter>() {
        override fun areItemsTheSame(
            oldItem: Parameter,
            newItem: Parameter
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Parameter,
            newItem: Parameter
        ): Boolean {
            return oldItem == newItem
        }
    }
}