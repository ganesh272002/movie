package com.example.movieseries.ui

//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.movieseries.data.Category
//import com.example.movieseries.data.Movie
//import com.example.movieseries.databinding.ItemCategoryBinding

//class CategoryAdapter(
//    //private val fragment: Fragment,
//    //private val onFavoriteClick: (Movie, Boolean) -> Unit
//) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
//
//    private val categories = mutableListOf<Category>()
//
//    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(category: Category) {
//            binding.categoryTitle.text = category.title
//
//            // Pass the fragment + callback into MoviesAdapter
//            val movieAdapter = MoviesAdapter(fragment, onFavoriteClick)
//            binding.horizontalRecyclerView.apply {
//                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//                adapter = movieAdapter
//            }
//            movieAdapter.submitList(category.movies)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
//        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return CategoryViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
//        holder.bind(categories[position])
//    }
//
//    override fun getItemCount(): Int = categories.size
//
//    fun submitList(newCategories: List<Category>) {
//        categories.clear()
//        categories.addAll(newCategories)
//        notifyDataSetChanged()
//    }
//}
