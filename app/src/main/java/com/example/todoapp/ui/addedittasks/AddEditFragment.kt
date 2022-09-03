package com.example.todoapp.ui.addedittasks

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddEditTaskBinding
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
            time.text = "Created : ${viewModel.task?.created}"
        }
    }
}