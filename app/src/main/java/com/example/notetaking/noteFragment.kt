package com.example.notetaking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.notetaking.databinding.FragmentNoteBinding
import com.example.notetaking.models.NoteRequest
import com.example.notetaking.models.NoteResponse
import com.example.notetaking.utills.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class noteFragment : Fragment() {


    private var _bindig: FragmentNoteBinding? = null
    private val binding get() = _bindig!!
    private var note: NoteResponse? = null
    private val noteViewModel by viewModels<NoteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _bindig = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitialData()
        bindHandlers()
        bindObservers()


    }

    private fun bindObservers() {
        lifecycleScope.launchWhenStarted {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){

                noteViewModel.singleNoteFlow.collect{ result ->
                 hideProgressBar()
                    when(result){
                        is NetworkResult.Loading -> {
                            showProgressBar()
                        }
                        is NetworkResult.Error -> {
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        }
                        is NetworkResult.Success -> {
                            findNavController().popBackStack()
                        }
                    }

                }
            }
        }
    }

    private fun showProgressBar(){
        binding.progressBar.visibility= View.VISIBLE
    }
    private fun hideProgressBar(){
        binding.progressBar.visibility=View.GONE
    }

    private fun bindHandlers() {
        binding.btnDelete.setOnClickListener {
            showProgressBar()
            if (note == null) {
                findNavController().popBackStack()
            } else {
               noteViewModel.deleteNote(note!!._id)
            }
        }

        binding.btnSubmit.setOnClickListener {
            showProgressBar()
            var newNote = NoteRequest(binding.txtDescription.text.toString(),
                binding.txtTitle.text.toString())
            if (note == null) {
                noteViewModel.createNote(newNote)
            } else {
                noteViewModel.updateNote(note!!._id, newNote)
            }
        }


    }


    private fun setInitialData() {
        val jsonNote = Gson().fromJson(arguments?.getString("note"), NoteResponse::class.java)

        if (jsonNote != null) {
            note = jsonNote

            note?.let {
                binding.txtTitle.setText(it.title)
                binding.txtDescription.setText(it.description)

            }
        } else {
            binding.addEditText.text = "Add Note"


        }
    }


        override fun onDestroy() {
            super.onDestroy()
            _bindig = null
        }

    }
