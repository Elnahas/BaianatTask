package elnahas.com.baianattask.data.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import elnahas.com.baianattask.data.models.NoteModel
import elnahas.com.baianattask.other.Constants
import elnahas.com.baianattask.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NoteRepository @Inject constructor() {

    private val mNotesCollection =
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_NOTES)

    @ExperimentalCoroutinesApi
    fun getAllNotes(): Flow<State<ArrayList<NoteModel>>> = callbackFlow {


        val listener = mNotesCollection.addSnapshotListener { snapshot, exception ->

            offer(State.loading())


            val tempList = ArrayList<NoteModel>()
            snapshot!!.forEach {

                val data = it.toObject<NoteModel>()
                data.id = it.id
                tempList.add(data)


            }

            offer(State.success(tempList))

            exception?.let {
                offer(State.failed(it.message.toString()))
                cancel(it.message.toString())
            }
        }

        awaitClose {
            listener.remove()
            cancel()
        }
    }

    fun getNote(noteId: String) = flow {

        emit(State.loading())

        val snapshot = mNotesCollection.document(noteId).get().await()

        val note = snapshot.toObject(NoteModel::class.java)

        emit(State.success(note!!))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun addNote(noteModel: NoteModel) = flow<State<DocumentReference>> {

        emit(State.loading())

        val noteRef = mNotesCollection.add(noteModel).await()

        emit(State.success(noteRef))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    fun updateNote(noteModel: NoteModel) = flow<State<Any>> {

        emit(State.loading())

        val updateData: HashMap<String, Any> = HashMap()
        updateData["title"] = noteModel.title!!
        updateData["description"] = noteModel.description!!

        mNotesCollection.document(noteModel.id!!).update(updateData).await()
        emit(State.success("success"))

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


}