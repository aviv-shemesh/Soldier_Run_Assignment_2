package com.example.my_project_1_aviv.utilities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_project_1_aviv.R

class ScoreListFragment : Fragment() {

    private lateinit var main_LST_scores: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        findViews(view)
        initViews()
        return view
    }

    private fun findViews(view: View) {
        main_LST_scores = view.findViewById(R.id.main_LST_scores)
    }

    private fun initViews() {
        val scoreManager = ScoreManager(requireContext())
        val scoreList = scoreManager.getAllScores()

        val adapter = ScoreAdapter(scoreList) { lat, lon ->
            if (activity is ArmyScoreActivity) {
                (activity as ArmyScoreActivity).showMapLocation(lat, lon)
            }
        }

        main_LST_scores.layoutManager = LinearLayoutManager(requireContext())
        main_LST_scores.adapter = adapter
    }
}