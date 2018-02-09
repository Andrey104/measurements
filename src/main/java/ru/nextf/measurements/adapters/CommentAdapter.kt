package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import ru.nextf.measurements.formatDateTime
import ru.nextf.measurements.modelAPI.Comment
import ru.nextf.measurements.modelAPI.User


/**
 * Created by addd on 13.12.2017.
 */
class CommentAdapter(notesList: List<Comment>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mCommentsList: List<Comment> = notesList
    private val ITEM = 0
    private val LOADING = 1
    private var isLoadingAdded = false

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val viewHolder = holder as ViewHolder
                viewHolder.name.text = mCommentsList[position].user.firstName + " " + mCommentsList[position].user.lastName
                when (mCommentsList[position].user.type) {
                    5, 4, 3 -> viewHolder.imageView.setImageResource(ru.nextf.measurements.R.drawable.admin)
                    2 -> viewHolder.imageView.setImageResource(ru.nextf.measurements.R.drawable.manager)
                    1 -> viewHolder.imageView.setImageResource(ru.nextf.measurements.R.drawable.worker_comment)
                }
                var fullDate = formatDateTime(mCommentsList[position].date)
                viewHolder.time.text = fullDate.substring(0, 5)
                viewHolder.date.text = fullDate.substring(6, fullDate.length - 1)
                viewHolder.text.text = mCommentsList[position].text
            }
            LOADING -> {
            }
        }

    }

    fun isEmpty() = mCommentsList.isEmpty()

    override fun getItemCount(): Int {
        return mCommentsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == ITEM) {
            v = LayoutInflater.from(parent?.context).inflate(ru.nextf.measurements.R.layout.list_item_comment_problem, parent, false)
            return CommentAdapter.ViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(ru.nextf.measurements.R.layout.progressbar_item, parent, false)
            LoadingVH(v)
        }
    }

    fun add(mc: Comment) {
        (mCommentsList as ArrayList<Comment>).add(mc)
        notifyItemInserted(mCommentsList.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Comment(0, User(), "", "", false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == mCommentsList.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = mCommentsList.size - 1
        val item = getItem(position)

        if (item != null) {
            (mCommentsList as ArrayList<Comment>).removeAt(position)
            notifyItemRemoved(position)
        }
    }

    protected inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val proressBar: ProgressBar

        init {
            proressBar = itemView.findViewById(ru.nextf.measurements.R.id.progressBar1)
        }
    }

    fun getItem(position: Int): Comment? {
        return mCommentsList[position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView
        var date: TextView
        var time: TextView
        var text: TextView
        var imageView: ImageView
        var constraintLayout: ConstraintLayout

        init {
            date = itemView.findViewById(ru.nextf.measurements.R.id.dateComment)
            time = itemView.findViewById(ru.nextf.measurements.R.id.timeComment)
            name = itemView.findViewById(ru.nextf.measurements.R.id.nameComment)
            text = itemView.findViewById(ru.nextf.measurements.R.id.textComment)
            imageView = itemView.findViewById(ru.nextf.measurements.R.id.imageView12)
            constraintLayout = itemView.findViewById(ru.nextf.measurements.R.id.constraintLayout)
        }


    }
}