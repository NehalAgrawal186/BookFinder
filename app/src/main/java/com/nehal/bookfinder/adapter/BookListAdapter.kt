package com.nehal.bookfinder.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nehal.bookfinder.R
import com.nehal.bookfinder.activity.BookDescriptionActivity
import com.nehal.bookfinder.model.DashboardChildItem
import com.squareup.picasso.Picasso

class BookListAdapter(val context: Context, val itemList: ArrayList<DashboardChildItem>) :
    RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

    class BookListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgBook: ImageView = view.findViewById(R.id.imgBook)
        var txtBookName: TextView = view.findViewById(R.id.txtBookName)
        var txtBookAuthor: TextView = view.findViewById(R.id.txtBookAuthor)
        var rlBook: RelativeLayout = view.findViewById(R.id.rlBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.book_item, parent
            , false
        )
        return BookListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        val book = itemList[position]
        holder.txtBookName.text = book.title
        holder.txtBookAuthor.text = book.author
        if (!book.thumbnail.equals("NOT AVAILABLE")) {
            Picasso.get().load(book.thumbnail).error(R.drawable.book_app_logo).into(holder.imgBook)
        }
        holder.rlBook.setOnClickListener {
            val intent = Intent(context, BookDescriptionActivity::class.java)
            intent.putExtra("selfLink", book.selfLink)
            context.startActivity(intent)
        }

    }


}