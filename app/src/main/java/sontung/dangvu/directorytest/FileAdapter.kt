package sontung.dangvu.directorytest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import sontung.dangvu.directorytest.databinding.DirectoryListItemBinding
import java.io.File

class FileAdapter(
    private var files : List<File>,
    private val listener : OnItemSelectedListener?
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<DirectoryListItemBinding>(
            inflater,
            R.layout.directory_list_item,
            parent,
            false
        )
        return FileViewHolder(view)
    }

    fun updateDir(list: List<File>) {
        files = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bindData(files[position])
    }

    inner class FileViewHolder(private val binding : DirectoryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(bindingFile : File) {
            binding.file = bindingFile
            binding.root.setOnClickListener {
                let { listener?.onItemSelectedListener(bindingFile) }
            }
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelectedListener(file: File)
    }
}
