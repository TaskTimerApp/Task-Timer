package com.example.tasktimerapp

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tasktimerapp.databinding.TasksRowBinding

class TasksAdapter(private val activity: HomePageActivity):
    ListAdapter<Tasks, TasksAdapter.ItemViewHolder>(TasksDiffUtil()) {
    class ItemViewHolder(val binding: TasksRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            TasksRowBinding.inflate(
            LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val taskList = getItem(position)

        holder.binding.apply {
            tvTaskTitle.text = taskList.title
            tvTaskDetails.text = taskList.description
            tvTimer.setBase(SystemClock.elapsedRealtime() - taskList.timer)

            /////////////////////////////////
            ////////CHECK TIMER STATE///////
            if (taskList.running) {
                tvTimer.start()
                tvTimer.setTextColor(ContextCompat.getColor(activity, android.R.color.holo_orange_dark))
            }else{
                tvTimer.stop()
                tvTimer.setTextColor(ContextCompat.getColor(activity,R.color.blue))
            }
            ///////////////////////////////
            //////////////////////////////

            taskCard.setOnClickListener{
                if (taskList.running) {
                    taskList.timer = SystemClock.elapsedRealtime() - tvTimer.getBase()
                }
                activity.taskDetailsActivity(taskList)
            }

            deleteTaskIcon.setOnClickListener { activity.deleteTask(taskList) }
        }

    }



//    fun rvUpdate(tasksListUpdate: ArrayList<Tasks>){
//        this.tasksList = tasksListUpdate
//        notifyDataSetChanged()
//    }


}