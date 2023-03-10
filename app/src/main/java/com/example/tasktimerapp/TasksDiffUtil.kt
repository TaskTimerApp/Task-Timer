package com.example.tasktimerapp

import androidx.recyclerview.widget.DiffUtil

class TasksDiffUtil(): DiffUtil.ItemCallback<Tasks>() {
    override fun areItemsTheSame(oldItem: Tasks, newItem: Tasks): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Tasks, newItem: Tasks): Boolean {
        return oldItem == newItem
    }
}