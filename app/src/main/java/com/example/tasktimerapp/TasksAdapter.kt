package com.example.tasktimerapp

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tasks_row.view.*

class TasksAdapter(var activity: HomePageActivity, private var tasksList: ArrayList<Tasks>):
    RecyclerView.Adapter<TasksAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.tasks_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val taskList = tasksList[position]

        holder.itemView.apply {
            tvTaskTitle.text = taskList.title
            tvTaskDetails.text = taskList.description
            tvTimer.setBase(SystemClock.elapsedRealtime() - taskList.timer)

            /////////////////////////////////
            ////////CHECK TIMER STATE///////
            if (taskList.running) {
                tvTimer.start()
                tvTimer.setTextColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
            }else{
                tvTimer.stop()
                tvTimer.setTextColor(ContextCompat.getColor(context,R.color.blue))
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

    override fun getItemCount() = tasksList.size

    fun rvUpdate(tasksListUpdate: ArrayList<Tasks>){
        this.tasksList = tasksListUpdate
        notifyDataSetChanged()
    }


}