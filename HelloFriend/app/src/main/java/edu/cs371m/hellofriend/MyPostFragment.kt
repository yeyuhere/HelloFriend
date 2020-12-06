package edu.cs371m.hellofriend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_post.*

class MyPostFragment: Fragment() {
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var adapter: PostAdapter
    private var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getMySchedule(currentUser)
        initAdapter(requireView())
        helloUser.text = "Hello, " + currentUser?.displayName + "!"
        viewModel.observeMySchedules().observe(viewLifecycleOwner, Observer {
            adapter.setPostList(it.toMutableList())
        })
        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            (activity as MainActivity).finishAffinity()
        }
    }
}