package io.github.feelfreelinux.wykopmobilny.ui.mikroblog.entry

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import io.github.feelfreelinux.wykopmobilny.R
import io.github.feelfreelinux.wykopmobilny.base.BaseFragment
import io.github.feelfreelinux.wykopmobilny.callbacks.FeedClickCallbacks
import io.github.feelfreelinux.wykopmobilny.decorators.EntryCommentItemDecoration
import io.github.feelfreelinux.wykopmobilny.objects.Entry
import io.github.feelfreelinux.wykopmobilny.ui.mainnavigation.NavigationActivity
import io.github.feelfreelinux.wykopmobilny.api.WykopApi
import io.github.feelfreelinux.wykopmobilny.utils.prepare

val EXTRA_ENTRY_ID = "ENTRY_ID"

class EntryFragment : BaseFragment(), EntryContract.View, SwipeRefreshLayout.OnRefreshListener {
    private val kodein = LazyKodein(appKodein)
    lateinit var recyclerView: RecyclerView

    private val apiManager: WykopApi by kodein.instance()
    private val entryId by lazy { arguments.getInt(EXTRA_ENTRY_ID) }
    private val navActivity by lazy { activity as NavigationActivity }
    val callbacks by lazy { FeedClickCallbacks(navActivity, apiManager) }
    val presenter by lazy { EntryPresenter(apiManager, entryId) }
    val adapter by lazy { EntryAdapter(callbacks) }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.recycler_view_layout, container, false)
        presenter.subscribe(this)
        // Prepare RecyclerView
        recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)!!
        recyclerView.prepare()

        // Set margin, adapter
        recyclerView.addItemDecoration(EntryCommentItemDecoration(resources.getDimensionPixelOffset(R.dimen.comment_section_left_margin)))
        recyclerView.adapter = adapter

        // Set needed flags
        navActivity.isLoading = true
        navActivity.setSwipeRefreshListener(this)

        // Trigger data loading
        presenter.loadData()
        return view
    }

    override fun onRefresh() {presenter.loadData()}


    override fun showEntry(entry: Entry) {
        adapter.entry = entry
        navActivity.isLoading = false
        navActivity.isRefreshing = false
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(id: Int): Fragment {
            val fragmentData = Bundle()
            val fragment = EntryFragment()
            fragmentData.putInt(EXTRA_ENTRY_ID, id)
            fragment.arguments = fragmentData
            return fragment
        }
    }
}