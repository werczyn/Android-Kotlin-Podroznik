package com.example.podroznik.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.podroznik.R
import com.example.podroznik.models.Note
import com.example.podroznik.views.NoteActivity
import kotlinx.android.synthetic.main.item.view.*
import java.io.Serializable

class PhotoViewHolder(view: View): RecyclerView.ViewHolder(view)

class PhotosAdapter : RecyclerView.Adapter<PhotoViewHolder>() {

    private var notes = mutableListOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
        PhotoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false)
        )


    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if(notes[position].photo!=null){
            notes[position].photo?.let { holder.itemView.itemImageView.setImageBitmap(BitmapFactory.decodeFile(it)) }
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context,NoteActivity::class.java)
            intent.putExtra("note", notes[position])
            context.startActivity(intent)
        }
    }

    fun setNotes(notes: List<Note>) {
        val oldSize = this.notes.size
        this.notes = notes.toMutableList()
        notifyItemRangeInserted(oldSize, notes.size)
    }

}