package com.example.todoapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.todoapp.R
import com.example.todoapp.adapters.TasksAdapter
import com.example.todoapp.data.Sort
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        viewModel.tasks.observe(viewLifecycleOwner){
            tasksAdapter.submitList(it)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks,menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged{
            // Search Query
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked = viewModel.preferencesFlow.first().hideCompleted
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId){
            R.id.action_search -> {
                 true
            }
            R.id.sort_by_date -> {
                viewModel.onSortOrderSelected(Sort.BY_DATE)
                true
            }
           R.id.sort_by_name -> {
               viewModel.onSortOrderSelected(Sort.BY_NAME)
               true
           }
           R.id.action_hide_completed_tasks ->{
               viewModel.onHideCompletedSelected(item.isChecked)
               true
           }
           R.id.action_delete_all_completed_tasks -> {
               true
           }
           else -> super.onOptionsItemSelected(item)
       }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}