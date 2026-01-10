package com.example.my_project_1_aviv.utilities

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_project_1_aviv.R

class ScoreAdapter(
    private val scores: ArrayList<Score>,
    private val onMapClicked: (Double, Double) -> Unit
) : RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.score_item, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position]

        holder.nameLabel.text = score.name
        holder.scoreLabel.text = "${score.score}"
        holder.mapIcon.setOnClickListener { onMapClicked(score.lat, score.lon) }

        when (position) {
            0 -> { // מקום ראשון - זהב
                holder.medalImage.visibility = View.VISIBLE
                holder.rankLabel.visibility = View.GONE
                holder.medalImage.setImageResource(android.R.drawable.btn_star_big_on)
                holder.medalImage.setColorFilter(Color.parseColor("#FFD700")) // Gold
            }
            1 -> { // מקום שני - כסף
                holder.medalImage.visibility = View.VISIBLE
                holder.rankLabel.visibility = View.GONE
                holder.medalImage.setImageResource(android.R.drawable.btn_star_big_on)
                holder.medalImage.setColorFilter(Color.parseColor("#C0C0C0")) // Silver
            }
            2 -> { // מקום שלישי - ארד (ברונזה)
                holder.medalImage.visibility = View.VISIBLE
                holder.rankLabel.visibility = View.GONE
                holder.medalImage.setImageResource(android.R.drawable.btn_star_big_on)
                holder.medalImage.setColorFilter(Color.parseColor("#CD7F32")) // Bronze
            }
            else -> { // שאר המקומות - מספר רגיל
                holder.medalImage.visibility = View.GONE
                holder.rankLabel.visibility = View.VISIBLE
                holder.rankLabel.text = "#${position + 1}"
            }
        }
    }

    override fun getItemCount(): Int {
        return scores.size
    }

    class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameLabel: TextView = itemView.findViewById(R.id.score_LBL_name)
        val scoreLabel: TextView = itemView.findViewById(R.id.score_LBL_score)
        val rankLabel: TextView = itemView.findViewById(R.id.score_LBL_rank)
        val mapIcon: ImageView = itemView.findViewById(R.id.score_IMG_map)
        val medalImage: ImageView = itemView.findViewById(R.id.score_IMG_medal)
    }
}