package com.example.todoapp.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.ItemTaskBinding

class TasksAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Task, TasksAdapter.TaskViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        listener.onItemClicked(task)
                    }
                }
                checkbox.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val task = getItem(position)
                        listener.onCheckBoxClick(task,checkbox.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkbox.isChecked = task.completed
                taskText.text = task.name
                taskText.paint.isStrikeThruText = task.completed
                priority.isVisible = task.important
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(task: Task)
        fun onCheckBoxClick(task: Task, checkedState: Boolean)
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }
}