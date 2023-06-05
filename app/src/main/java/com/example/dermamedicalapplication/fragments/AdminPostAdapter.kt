
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.dermamedicalapplication.fragments.ApprovedFragment
import com.example.dermamedicalapplication.fragments.DeletedFragment
import com.example.dermamedicalapplication.fragments.NewPostFragment

class AdminPostAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :  FragmentStateAdapter(
    fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            NewPostFragment()
        else if (position == 1)
            DeletedFragment()
        else
             ApprovedFragment()

        }
    }
