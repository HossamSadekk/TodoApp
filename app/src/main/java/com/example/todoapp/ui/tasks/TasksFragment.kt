package com.example.todoapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.adapters.TasksAdapter
import com.example.todoapp.data.Sort
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.FragmentTasksBinding
import com.example.todoapp.utils.exhaustive
import com.example.todoapp.utils.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnItemClickListener {
    private val viewModel: TaskViewModel by viewModels()
    private var binding: FragmentTasksBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTasksBinding.bind(view)
        val tasksAdapter = TasksAdapter(this)
        binding?.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                setHasFixedSize(true)
            }
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tasksAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)

            fabAddTasks.setOnClickListener {
                viewModel.onAddItemClick()
            }
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.taskEvent.collect{
                event ->
                when(event){
                    is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(),"Task Deleted",Snackbar.LENGTH_LONG)
                            .setAction("Undo"){
                                viewModel.onUndoDeleteClicked(event.task)
                            }.show()
                    }
                    is TaskViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment(null,"Add Task")
                        findNavController().navigate(action)
                    }
                    is TaskViewModel.TaskEvent.NavigateToEditTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment(event.task,"Edit Task")
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            // Search Query
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
            R.id.action_hide_completed_tasks -> {
                viewModel.onHideCompletedSelected(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClicked(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, checkedState: Boolean) {
        viewModel.onTaskStateChanged(task, checkedState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}