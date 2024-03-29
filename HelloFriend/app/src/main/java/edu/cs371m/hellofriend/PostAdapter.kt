package edu.cs371m.hellofriend

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var viewModel: MainViewModel)
    : ListAdapter<Schedule, PostAdapter.VH>(Diff())
{
    private var postList = mutableListOf<Schedule>()

    class Diff : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.scheduleID == newItem.scheduleID
        }
        override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
            return oldItem.fromM == newItem.fromM
                    && oldItem.fromH == newItem.fromH
                    && oldItem.toH == newItem.toM
                    && oldItem.toM == newItem.toM
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.age == newItem.age
                    && oldItem.latitude == newItem.latitude
                    && oldItem.longitude == newItem.longitude
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var infoTV = itemView.findViewById<TextView>(R.id.infoText)
        private var deleteBut = itemView.findViewById<Button>(R.id.deleteImage)

        init{
            deleteBut.setOnClickListener {
                viewModel.deleteSchedule(postList[adapterPosition])
            }
        }

        fun bind(item: Schedule) {
            infoTV.text = item.age + "YO: " + item.fromH + ":" + item.fromM + " - " + item.toH + ":" + item.toM
        }
    }

    fun setPostList(postList: MutableList<Schedule>) {
        this.postList = postList
        Log.d("xxx", "postlist size: ${postList.size}")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_post, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun getItem(position: Int): Schedule {
        return postList[position]
    }
}