package elnahas.com.baianattask.data.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import elnahas.com.baianattask.data.models.NoteModel
import elnahas.com.baianattask.data.repository.NoteRepository
import elnahas.com.baianattask.utils.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class NoteViewModel @ViewModelInject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _noteListState = MutableStateFlow<State<ArrayList<NoteModel>>>(State.Empty())
    val noteListState: StateFlow<State<ArrayList<NoteModel>>> = _noteListState

    init {
        getAllNotes()
    }

    @ExperimentalCoroutinesApi
    fun getAllNotes() {

        viewModelScope.launch {

            repository.getAllNotes().onStart {
                _noteListState.value = State.Loading()
            }
                .catch {
                    _noteListState.value = State.failed(it.message.toString())
                }
                .collect { data->
                    _noteListState.value = data
                }
        }
    }

    fun addNote(note: NoteModel) = repository.addNote(note)
    fun getNote(noteId: String) = repository.getNote(noteId)
    fun updateNote(note: NoteModel) = repository.updateNote(note)
}