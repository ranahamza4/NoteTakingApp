package com.example.notetaking.repository

import com.example.notetaking.api.NotesAPI
import com.example.notetaking.models.NoteRequest
import com.example.notetaking.models.NoteResponse
import com.example.notetaking.utills.NetworkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NotesRepository @Inject constructor(private val notesAPI: NotesAPI) {

private var _allNotesAPIFlow = MutableSharedFlow<NetworkResult<List<NoteResponse>>>()
    var allNotesAPIFLow= _allNotesAPIFlow
    private var _singleNotesAPIFlow = MutableSharedFlow<NetworkResult<NoteResponse>>()
    var singleNotesAPIFLow= _singleNotesAPIFlow


    suspend fun getNotes(){
        _allNotesAPIFlow.emit(NetworkResult.Loading())

        var response=  notesAPI.getNotes()

        if (response.isSuccessful && response != null) {
            _allNotesAPIFlow.emit(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _allNotesAPIFlow.emit(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _allNotesAPIFlow.emit(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun deleteNote(noteId:String){
        _singleNotesAPIFlow.emit(NetworkResult.Loading())
        var response = notesAPI.deleteNote(noteId)
        handleResponse(response)
    }

    suspend fun createNote(noteRequest: NoteRequest){
        _singleNotesAPIFlow.emit(NetworkResult.Loading())
        var response = notesAPI.createNote(noteRequest)
        handleResponse(response)
    }

    suspend fun updateNote(noteId: String,noteRequest: NoteRequest){
        _singleNotesAPIFlow.emit(NetworkResult.Loading())
        var response = notesAPI.updateNote(noteId,noteRequest)
        handleResponse(response)
    }

  private suspend fun handleResponse(response: Response<NoteResponse>) {

      if (response.isSuccessful && response != null) {
          _singleNotesAPIFlow.emit(NetworkResult.Success(response.body()!!))
      } else if (response.errorBody() != null) {
          val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
          _singleNotesAPIFlow.emit(NetworkResult.Error(errorObj.getString("message")))
      } else {
          _singleNotesAPIFlow.emit(NetworkResult.Error("Something Went Wrong"))
      }


    }


}