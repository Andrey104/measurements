package ru.nextf.measurements.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.nextf.measurements.R
import ru.nextf.measurements.formatDateTime
import ru.nextf.measurements.modelAPI.Comment


/**
 * Created by addd on 13.12.2017.
 */
class CommentAdapter(notesList: List<Comment>, activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mCommentsList: List<Comment> = notesList
    private val ITEM = 0
    private val MUSIC = 1
    val mHandler = Handler()
    private val arr = ArrayList<Runnable>()
    private val arrayViewHolder = ArrayList<MusicComment>()
    private var oldSelectedPleer: MediaPlayer? = null
    private var isLoadingAdded = false
    private var activity = activity

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
                viewHolder.name.text = mCommentsList[position].user.firstName + " " + mCommentsList[position].user.lastName
                when (mCommentsList[position].user.type) {
                    5, 4, 3 -> viewHolder.imageView.setImageResource(ru.nextf.measurements.R.drawable.admin)
                    2 -> viewHolder.imageView.setImageResource(ru.nextf.measurements.R.drawable.manager)
                    1 -> viewHolder.imageView.setImageResource(ru.nextf.measurements.R.drawable.worker_comment)
                }
                var fullDate = formatDateTime(mCommentsList[position].date)
                viewHolder.time.text = fullDate.substring(0, 5)
                viewHolder.date.text = fullDate.substring(6, fullDate.length)
                var prep = false
                var isPlaying = false
                var mp = MediaPlayer()
                val daytonPolice = mCommentsList[position].file

                mp.setOnPreparedListener { mp ->
                    prep = true
                    viewHolder.seekBar.visibility = View.VISIBLE
                    viewHolder.buttonStart.visibility = View.VISIBLE
                    viewHolder.buttonStop.visibility = View.VISIBLE
                    viewHolder.progressBar.visibility = View.GONE
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val attributes = AudioAttributes.Builder()
                    attributes.setContentType(CONTENT_TYPE_MUSIC)
                    mp.setAudioAttributes(attributes.build())
                } else {
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
                }
                mp.setDataSource(daytonPolice)
                val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        val mUpdateTimeTask = object : Runnable {
                            override fun run() {
                                println("${mp.currentPosition} / ${mp.duration} ")
                                val mCurrentPosition = (mp.currentPosition * 100 / mp.duration * 100)
                                println("${mCurrentPosition} ")
                                viewHolder.seekBar.progress = mCurrentPosition / 100
                                if (isPlaying) {
                                    mHandler.postDelayed(this, 1000)
                                }
                            }
                        }
                        val onePercent = mp.duration / 100
                        mp.seekTo(seekBar.progress * onePercent)
                        mHandler.postDelayed(mUpdateTimeTask, 2000)
                    }
                }
                viewHolder.seekBar.setOnSeekBarChangeListener(seekBarChangeListener)
                mp.prepareAsync()


                val mUpdateTimeTask = object : Runnable {
                    override fun run() {
                        println("${mp.currentPosition} / ${mp.duration} ")
                        val mCurrentPosition = (mp.currentPosition * 100 / mp.duration * 100)
                        println("${mCurrentPosition} ")
                        viewHolder.seekBar.progress = mCurrentPosition / 100
                        if (isPlaying) {
                            mHandler.postDelayed(this, 1000)
                        }
                    }
                }

                arr.add(mUpdateTimeTask)

                arrayViewHolder.add(viewHolder)
                viewHolder.buttonStart.setOnClickListener {
                    if (oldSelectedPleer != null) {
                        if (oldSelectedPleer?.isPlaying == true) {
                            oldSelectedPleer?.pause()
                        }
                    }
                    if (prep) {
                        oldSelectedPleer = mp
                        mp.start()
                        for (holder in arrayViewHolder) {
                            if (holder != viewHolder) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.white, activity.theme))
                                } else {
                                    holder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.white))
                                }
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    viewHolder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.backgroundAdmin, activity.theme))
                                } else {
                                    viewHolder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.backgroundAdmin))
                                }
                            }

                        }

                        isPlaying = true
                        mHandler.postDelayed(mUpdateTimeTask, 2000)
                    }
                }
                viewHolder.buttonStop.setOnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        viewHolder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.white, activity.theme))
                    } else {
                        viewHolder.constraintLayout.setBackgroundColor(activity.resources.getColor(R.color.white))
                    }
                    isPlaying = false
                    mp.pause()
                }
                mp.setOnSeekCompleteListener { mHandler.removeCallbacks(mUpdateTimeTask) }
            }
        }

    }


    fun closePlayer() {
        for (run in arr) {
            mHandler.removeCallbacks(run)
        }
        if (oldSelectedPleer != null) {
            if (oldSelectedPleer?.isPlaying == true) {
                oldSelectedPleer?.pause()
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
        val buttonStart: ImageButton
        val buttonStop: ImageButton
        val seekBar: SeekBar
        var name: TextView
        var date: TextView
        var time: TextView
        var imageView: ImageView
        var progressBar: ProgressBar
        var constraintLayout: ConstraintLayout

        init {
            buttonStart = itemView.findViewById(ru.nextf.measurements.R.id.buttonPlay)
            buttonStop = itemView.findViewById(ru.nextf.measurements.R.id.buttonStop)
            seekBar = itemView.findViewById(ru.nextf.measurements.R.id.seekBar2)
            date = itemView.findViewById(ru.nextf.measurements.R.id.dateComment)
            time = itemView.findViewById(ru.nextf.measurements.R.id.timeComment)
            name = itemView.findViewById(ru.nextf.measurements.R.id.nameComment)
            imageView = itemView.findViewById(ru.nextf.measurements.R.id.imageView12)
            progressBar = itemView.findViewById(ru.nextf.measurements.R.id.progressBar10)
            constraintLayout = itemView.findViewById(ru.nextf.measurements.R.id.constraintLayout)
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