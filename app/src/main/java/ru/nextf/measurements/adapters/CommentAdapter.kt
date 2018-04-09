package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.nextf.measurements.formatDateTime
import ru.nextf.measurements.modelAPI.Comment
import android.media.AudioManager
import android.media.MediaPlayer
import android.widget.Button


/**
 * Created by addd on 13.12.2017.
 */
class CommentAdapter(notesList: List<Comment>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mCommentsList: List<Comment> = notesList
    private val ITEM = 0
    private val MUSIC = 1
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
                viewHolder.date.text = fullDate.substring(6, fullDate.length)
                viewHolder.text.text = mCommentsList[position].text
            }
            MUSIC -> {
                val viewHolder = holder as MusicComment
                viewHolder.button.setOnClickListener {
                    val daytonPolice = mCommentsList[position].file
                    val mp = MediaPlayer()

                    mp.setOnPreparedListener { mp -> mp.start() }

                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mp.setDataSource(daytonPolice) // It will not take the string of my url
                    mp.prepareAsync()
                }
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
            v = LayoutInflater.from(parent.context).inflate(ru.nextf.measurements.R.layout.music_comment, parent, false)
            MusicComment(v)
        }
    }

    fun add(mc: Comment) {
        (mCommentsList as ArrayList<Comment>).add(mc)
        notifyItemInserted(mCommentsList.size - 1)
    }


    override fun getItemViewType(position: Int): Int {
        return if (mCommentsList[position].commentType == 1) {
            ITEM
        } else {
            MUSIC
        }
    }


    protected inner class MusicComment(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button

        init {
            button = itemView.findViewById(ru.nextf.measurements.R.id.buttonPlay)
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