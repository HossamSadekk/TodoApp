package com.example.todoapp.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import com.example.todoapp.adapters.TasksAdapter
import com.example.todoapp.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {
    private val viewModel: TaskViewModel by viewModels()
    private var binding: FragmentTasksBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksBinding.bind(view)
        val tasksAdapter = TasksAdapter()
        binding?.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                setHasFixedSize(true)
            }
        }
        viewModel.getTasks().observe(viewLifecycleOwner){
            tasksAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}