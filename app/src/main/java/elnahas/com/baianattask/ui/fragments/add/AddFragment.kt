package elnahas.com.baianattask.ui.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import elnahas.com.baianattask.R
import elnahas.com.baianattask.data.models.NoteModel
import elnahas.com.baianattask.data.viewmodel.NoteViewModel
import elnahas.com.baianattask.databinding.FragmentAddBinding
import elnahas.com.baianattask.ui.fragments.SharedViewModel
import elnahas.com.baianattask.utils.State
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddFragment : Fragment() {


    val sharedViewModel : SharedViewModel by viewModels()

    private var _binding : FragmentAddBinding?=null
    private val binding get() = _binding!!

    val noteViewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Data Binding
        _binding = FragmentAddBinding.inflate(inflater , container  , false)
        binding.lifecycleOwner = this


        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu , menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_save)
        {

            val mTitle = edt_title.text.toString()
            val mDescription = edt_description.text.toString()

            val validation = sharedViewModel.verifyDataFromUser(mTitle , mDescription)

            if (validation)
            {
                val note = NoteModel(title = mTitle  , description = mDescription)

                lifecycleScope.launch {
                    addNote(
                        note
                    )
                }


            }
            else
            {
                Toast.makeText(requireContext() , "Please Fill All Fields" , Toast.LENGTH_LONG).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun addNote(noteModel: NoteModel) {
        noteViewModel.addNote(noteModel).collect { state ->
            when (state) {
                is State.Loading -> {

                }

                is State.Success -> {
                    Toast.makeText(requireContext() , "Successfully Added" , Toast.LENGTH_LONG).show()

                    findNavController().navigate(R.id.action_addFragment_to_listFragment)
                }

                is State.Failed -> {
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}