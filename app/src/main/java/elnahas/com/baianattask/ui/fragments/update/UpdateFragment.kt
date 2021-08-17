package elnahas.com.baianattask.ui.fragments.update

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import elnahas.com.baianattask.R
import elnahas.com.baianattask.data.models.NoteModel
import elnahas.com.baianattask.data.viewmodel.NoteViewModel
import elnahas.com.baianattask.databinding.FragmentUpdateBinding
import elnahas.com.baianattask.ui.fragments.SharedViewModel
import elnahas.com.baianattask.utils.State
import kotlinx.android.synthetic.main.fragment_update.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateFragment : Fragment() {

    val args : UpdateFragmentArgs by navArgs()
    val sharedViewModel : SharedViewModel by viewModels()
    val noteViewModel : NoteViewModel by viewModels()

    private var _binding : FragmentUpdateBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val noteId = args.noteId

        lifecycleScope.launch {
            noteViewModel.getNote(noteId!!).collect { state ->
                when (state) {
                    is State.Loading -> {
                    }

                    is State.Success -> {

                        binding.edtTitle.setText(state.data.title!!.toString())
                        binding.edtDescription.setText(state.data.description!!.toString())

                    }

                    is State.Failed -> {
                    }
                }
            }
        }


        setHasOptionsMenu(true)

        _binding = FragmentUpdateBinding.inflate(inflater , container , false)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu , menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                lifecycleScope.launch {
                    updateNote()
                }
            }
            R.id.menu_share -> {
                shareApp()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun updateNote() {

        val title = edt_title.text.toString()
        val description = edt_description.text.toString()

        val validation = sharedViewModel.verifyDataFromUser(title , description)

        if (validation)
        {
            val updateItems = NoteModel(title = title ,description =  description)
            updateItems.id = args.noteId

            noteViewModel.updateNote(updateItems).collect{ state ->
                when (state) {
                    is State.Loading -> {
                    }

                    is State.Success -> {

                        Toast.makeText(requireContext() , "Successfully Updated" , Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                    }

                    is State.Failed -> {

                    }
                }
            }

        }
        else
            Toast.makeText(requireContext() , "Please fill Fields" , Toast.LENGTH_LONG).show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun shareApp() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://baianattask.com/notes/${args.noteId}"
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }



}