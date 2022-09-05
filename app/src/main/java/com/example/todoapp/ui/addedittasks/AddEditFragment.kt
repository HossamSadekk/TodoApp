package com.example.todoapp.ui.addedittasks

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddEditTaskBinding
import com.example.todoapp.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            editTextTask.setText(viewModel.taskName)
            checkboxImportant.isChecked = viewModel.important
            checkboxImportant.jumpDrawablesToCurrentState()
            time.isVisible = viewModel.task != null
            time.text = "Created : ${viewModel.task?.dateFormat}"

            editTextTask.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkboxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.important = isChecked
            }

            fabAddTasks.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTask.clearFocus()
                        setFragmentResult(
                            requestKey = "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
}