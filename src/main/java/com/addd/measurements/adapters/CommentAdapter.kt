package com.addd.measurements.adapters

import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.addd.measurements.MyApp
import com.addd.measurements.R
import com.addd.measurements.modelAPI.Comment


/**
 * Created by addd on 13.12.2017.
 */
class CommentAdapter(notesList: List<Comment>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var mCommentsList: List<Comment> = notesList

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.name.text = mCommentsList[position].user.toString()
        if (mCommentsList[position].user == 5) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.constraintLayout.setBackgroundColor(MyApp.instance.resources.getColor(R.color.backgroundAdmin, MyApp.instance.theme))
            } else {
                holder.constraintLayout.setBackgroundColor(MyApp.instance.resources.getColor(R.color.backgroundAdmin))
            }
        }
        holder.date.text = mCommentsList[position].date
        holder.text.text = mCommentsList[position].text

    }

    override fun getItemCount(): Int {
       return mCommentsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommentAdapter.ViewHolder {
        var v = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_comment_problem, parent, false)
        return CommentAdapter.ViewHolder(v)
    }





    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var date: TextView
        var text: TextView
        var constraintLayout: ConstraintLayout

        init {
            date =itemView.findViewById(R.id.dateComment)
            name = itemView.findViewById(R.id.nameComment)
            text = itemView.findViewById(R.id.textComment)
            constraintLayout = itemView.findViewById(R.id.constraintLayout)
        }


    }
}