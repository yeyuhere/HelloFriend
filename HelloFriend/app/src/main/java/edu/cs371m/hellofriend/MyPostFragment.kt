package edu.cs371m.hellofriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_post.*

class MyPostFragment: Fragment() {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var adapter: PostAdapter

    companion object {
        fun newInstance(): MyPostFragment {
            return MyPostFragment()
        }
    }

    private fun initAdapter(root: View) {
        adapter = PostAdapter(viewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

}