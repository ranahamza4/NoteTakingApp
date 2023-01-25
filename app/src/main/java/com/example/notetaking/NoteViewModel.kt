package com.example.notetaking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notetaking.models.NoteRequest
import com.example.notetaking.repository.NotesRepository
import com.example.notetaking.repository.UserRepository
import com.example.notetaking.utills.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private var noteRepository: NotesRepository) : ViewModel() {

    var allNotesFlow = noteRepository.allNotesAPIFLow
    var singleNoteFlow= noteRepository.singleNotesAPIFLow


    fun getAllNotes(){
        viewModelScope.launch{

            noteRepository.getNotes()
        }
    }
    fun createNote(noteRequest: NoteRequest){
        viewModelScope.launch {
            noteRepository.createNote(noteRequest)
        }
    }


    fun deleteNote(noteId:String){
        viewModelScope.launch {
            noteRepository.deleteNote(noteId)
        }}

        fun updateNote(noteId:String, noteRequest: NoteRequest){
            viewModelScope.launch {
                noteRepository.updateNote(noteId,noteRequest)
            }
        }

    }



