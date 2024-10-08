package com.example.e_commerce_app.rating

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.model.rateing.RatingItem
import com.example.e_commerce_app.product_details.adapter.RatingAdapter


class RatingFragment : Fragment() {
    private lateinit var ratingRecyclerView: RecyclerView
    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var ratingList: List<RatingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ratingRecyclerView = view.findViewById(R.id.ratingRecyclerView)
        ratingRecyclerView.layoutManager = LinearLayoutManager(requireContext())




        val initialRatingList = listOf(
            RatingItem(R.drawable.p1, "Emma", "Absolutely love this product! Highly recommend.", 5.0f),
            RatingItem(R.drawable.p2, "Liam", "Decent quality, but a bit pricey.", 4.0f),
            RatingItem(R.drawable.p3, "Olivia", "Meets my expectations. Good value for money.", 4.2f),
            RatingItem(R.drawable.p4, "Noah", "Not what I expected, but still okay.", 3.0f),
            RatingItem(R.drawable.p5, "Ava", "Fantastic! Will buy again.", 4.8f),
            RatingItem(R.drawable.p6, "Sophia", "Average experience, nothing special.", 3.5f),
            RatingItem(R.drawable.p7, "Mason", "Poor quality. I wouldn't recommend it.", 2.5f)
        )

        ratingList = initialRatingList.shuffled()

        ratingAdapter = RatingAdapter(ratingList)
        ratingRecyclerView.adapter = ratingAdapter

    }

}