package elnahas.com.baianattask.ui.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import elnahas.com.baianattask.R
import elnahas.com.baianattask.adapters.ListAdapter
import elnahas.com.baianattask.data.viewmodel.NoteViewModel
import elnahas.com.baianattask.databinding.FragmentListBinding
import elnahas.com.baianattask.ui.fragments.SharedViewModel
import elnahas.com.baianattask.utils.State
import elnahas.com.baianattask.utils.hideKeyboard
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ListFragment : Fragment() {

    @ExperimentalCoroutinesApi
    private val noteViewModel : NoteViewModel by viewModels()
    private val sharedViewModel : SharedViewModel by viewModels()

    lateinit var listAdapter : ListAdapter

    private var _binding : FragmentListBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater , container  , false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = sharedViewModel

        setupRecyclerView()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setHasOptionsMenu(true)

        hideKeyboard(requireActivity())

        setObservers()

        return binding?.root
    }

    private fun setupRecyclerView() {
        listAdapter = ListAdapter()
        binding.recyclerView.adapter = listAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun setObservers() {

        lifecycleScope.launchWhenStarted {
            noteViewModel.noteListState.collect {
                when (it) {
                    is State.Success -> {

                        binding.progress.isVisible = false
                        sharedViewModel.checkIfListEmpty(it.data)
                        listAdapter.differ.submitList(it.data)

                        listAdapter.setOnItemClickListener { noteModel, view ->
                            val bundle = Bundle()
                            bundle.putString("noteId", noteModel.id)
                            findNavController().navigate(
                                R.id.action_listFragment_to_updateFragment,
                                bundle
                            )
                        }
                    }
                    is State.Failed -> {
                        binding.progress.isVisible = false
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT ).show()
                    }
                    is State.Loading -> {
                        binding.progress.isVisible = true
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}