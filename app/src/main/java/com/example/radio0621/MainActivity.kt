package com.example.radio0621

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var commentsLayout: LinearLayout
    private lateinit var sortByPopularityBtn: Button
    private lateinit var sortByNewestBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        commentsLayout = findViewById(R.id.commentsLayout)
        sortByPopularityBtn = findViewById(R.id.popularityBtn)
        sortByNewestBtn = findViewById(R.id.newestBtn)

        // 仮のコメントを作成して表示
        createDummyComments()

        // 「人気順」ボタンのクリックリスナーを設定
        sortByPopularityBtn.setOnClickListener {
            sortByPopularity()
        }

        // 「新しい順」ボタンのクリックリスナーを設定
        sortByNewestBtn.setOnClickListener {
            sortByNewest()
        }
    }

    private fun createDummyComments() {
        // 仮のコメントを作成し、表示する
        for (i in 1..5) {
            val comment = Comment("ユーザー$i", "コメント$i", 10 * i, 2 * i, getPastDate(i))
            addCommentView(comment)
        }
    }

    private fun addCommentView(comment: Comment) {
        val commentView = layoutInflater.inflate(R.layout.comment_item, null)
        val authorTextView = commentView.findViewById<TextView>(R.id.authorTextView)
        val commentTextView = commentView.findViewById<TextView>(R.id.commentTextView)
        val upvoteBtn = commentView.findViewById<Button>(R.id.upvoteBtn)
        val upvoteCountTextView = commentView.findViewById<TextView>(R.id.upvoteCountTextView)
        val downvoteBtn = commentView.findViewById<Button>(R.id.downvoteBtn)
        val downvoteCountTextView = commentView.findViewById<TextView>(R.id.downvoteCountTextView)
        val dateTextView = commentView.findViewById<TextView>(R.id.dateTextView)

        authorTextView.text = comment.user
        commentTextView.text = comment.commentText
        upvoteCountTextView.text = comment.likes.toString()
        downvoteCountTextView.text = comment.dislikes.toString()
        dateTextView.text = comment.date

        // 👍ボタンのクリックリスナーを設定
        upvoteBtn.setOnClickListener {
            if (upvoteBtn.isSelected) {
                comment.likes--
                upvoteCountTextView.text = comment.likes.toString()
                upvoteBtn.isSelected = false
            } else {
                comment.likes++
                upvoteCountTextView.text = comment.likes.toString()
                upvoteBtn.isSelected = true
                // 👎ボタンが押されていた場合、取り消す
                if (downvoteBtn.isSelected) {
                    comment.dislikes--
                    downvoteCountTextView.text = comment.dislikes.toString()
                    downvoteBtn.isSelected = false
                }
            }
        }

        // 👎ボタンのクリックリスナーを設定
        downvoteBtn.setOnClickListener {
            if (downvoteBtn.isSelected) {
                comment.dislikes--
                downvoteCountTextView.text = comment.dislikes.toString()
                downvoteBtn.isSelected = false
            } else {
                comment.dislikes++
                downvoteCountTextView.text = comment.dislikes.toString()
                downvoteBtn.isSelected = true
                // 👍ボタンが押されていた場合、取り消す
                if (upvoteBtn.isSelected) {
                    comment.likes--
                    upvoteCountTextView.text = comment.likes.toString()
                    upvoteBtn.isSelected = false
                }
            }
        }

        commentsLayout.addView(commentView)
    }

    private fun sortByPopularity() {
        val comments = getCommentsFromLayout()
        comments.sortByDescending { it.likes }
        updateCommentsLayout(comments)
    }

    private fun sortByNewest() {
        val comments = getCommentsFromLayout()
        comments.sortByDescending { convertToDate(it.date) }
        updateCommentsLayout(comments)
    }

    private fun getCommentsFromLayout(): MutableList<Comment> {
        val comments = mutableListOf<Comment>()
        for (i in 0 until commentsLayout.childCount) {
            val commentView = commentsLayout.getChildAt(i)
            val authorTextView = commentView.findViewById<TextView>(R.id.authorTextView)
            val commentTextView = commentView.findViewById<TextView>(R.id.commentTextView)
            val upvoteCountTextView = commentView.findViewById<TextView>(R.id.upvoteCountTextView)
            val downvoteCountTextView = commentView.findViewById<TextView>(R.id.downvoteCountTextView)
            val dateTextView = commentView.findViewById<TextView>(R.id.dateTextView)

            val user = authorTextView.text.toString()
            val commentText = commentTextView.text.toString()
            val likes = upvoteCountTextView.text.toString().toInt()
            val dislikes = downvoteCountTextView.text.toString().toInt()
            val date = dateTextView.text.toString()

            val comment = Comment(user, commentText, likes, dislikes, date)
            comments.add(comment)
        }
        return comments
    }

    private fun updateCommentsLayout(comments: List<Comment>) {
        commentsLayout.removeAllViews()
        for (comment in comments) {
            addCommentView(comment)
        }
    }

    private fun getPastDate(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun convertToDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.parse(dateString) ?: Date()
    }
}

data class Comment(
    val user: String,
    val commentText: String,
    var likes: Int,
    var dislikes: Int,
    val date: String
)

