package elnahas.com.baianattask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import elnahas.com.baianattask.data.models.NoteModel
import elnahas.com.baianattask.databinding.ItemNoteBinding

class ListAdapter :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(){

    val diffCallback = object : DiffUtil.ItemCallback<NoteModel>() {

        override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
            return oldItem == newItem

        }

    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = differ.currentList[position]
        holder.bind(note)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(note, holder.itemView) }
        }
    }

    class ViewHolder(var binding:ItemNoteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(noteModel: NoteModel){
            binding.noteModel = noteModel
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent : ViewGroup):ViewHolder{

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNoteBinding.inflate(layoutInflater  , parent , false)
                return ViewHolder(binding)

            }
        }
    }

    private var onItemClickListener: ((NoteModel, View) -> Unit)? = null

    fun setOnItemClickListener(listener: (NoteModel, View) -> Unit) {
        onItemClickListener = listener
    }

}