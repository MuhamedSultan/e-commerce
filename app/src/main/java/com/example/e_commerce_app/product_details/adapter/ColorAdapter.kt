package com.example.e_commerce_app.product_details.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R

class ColorAdapter(private val colors: List<String>) :
    RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    private var selectedPosition = -1

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorImageView: ImageView = itemView.findViewById(R.id.color_item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]
        when (color) {
            "white" -> holder.colorImageView.setImageResource(R.drawable.white)
            "black" -> holder.colorImageView.setImageResource(R.drawable.black)
            "blue" -> holder.colorImageView.setImageResource(R.drawable.blue)
            "red" -> holder.colorImageView.setImageResource(R.drawable.red)
            "gray" -> holder.colorImageView.setImageResource(R.drawable.gray)
            "yellow" -> holder.colorImageView.setImageResource(R.drawable.yellow)
            "beige" -> holder.colorImageView.setImageResource(R.drawable.beige)
            "light_brown" -> holder.colorImageView.setImageResource(R.drawable.light_brown)
            "burgandy" -> holder.colorImageView.setImageResource(R.drawable.burgandy)
            else -> holder.colorImageView.setImageResource(R.drawable.light_brown)
        }
        // Handle selection and deselection
        if (selectedPosition == position) {
            holder.colorImageView.setBackgroundResource(R.color.gray_dark_color)  // Highlight selected
        } else {
            holder.colorImageView.setBackgroundResource(R.color.light_gray)
        }

        holder.colorImageView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousPosition)
            notifyItemChanged(position)

            Toast.makeText(holder.itemView.context, "Selected: $color", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getItemCount() = colors.size
}
