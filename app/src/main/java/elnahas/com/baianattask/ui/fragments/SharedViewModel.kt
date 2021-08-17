package elnahas.com.baianattask.ui.fragments

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import elnahas.com.baianattask.data.models.NoteModel

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    val emptyList : MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfListEmpty(listNote:List<NoteModel>){
        emptyList.value = listNote.isEmpty()
    }


    fun verifyDataFromUser(title : String , description:String) : Boolean{

        return !(TextUtils.isEmpty(title) || TextUtils.isEmpty(description))
    }



}