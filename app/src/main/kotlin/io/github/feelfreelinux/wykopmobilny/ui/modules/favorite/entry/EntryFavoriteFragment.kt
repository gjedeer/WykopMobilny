package io.github.feelfreelinux.wykopmobilny.ui.modules.favorite.entry

import android.os.Bundle
import io.github.feelfreelinux.wykopmobilny.base.BaseFeedFragment
import io.github.feelfreelinux.wykopmobilny.models.dataclass.Entry
import io.github.feelfreelinux.wykopmobilny.models.fragments.DataFragment
import io.github.feelfreelinux.wykopmobilny.models.fragments.PagedDataModel
import io.github.feelfreelinux.wykopmobilny.models.fragments.getDataFragmentInstance
import io.github.feelfreelinux.wykopmobilny.models.fragments.removeDataFragment
import io.github.feelfreelinux.wykopmobilny.ui.adapters.FeedAdapter
import io.github.feelfreelinux.wykopmobilny.ui.modules.favorite.FavoriteFragmentNotifier
import io.github.feelfreelinux.wykopmobilny.utils.printout
import javax.inject.Inject

class EntryFavoriteFragment : BaseFeedFragment<Entry>(), EntryFavoriteView, FavoriteFragmentNotifier {
    @Inject override lateinit var feedAdapter : FeedAdapter
    @Inject lateinit var presenter : EntryFavoritePresenter
    lateinit var dataFragment : DataFragment<PagedDataModel<List<Entry>>>

    companion object {
        val DATA_FRAGMENT_TAG = "ENTRY_FAVORITE_FRAGMENT_TAG"

        fun newInstance() : EntryFavoriteFragment {
            return EntryFavoriteFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.subscribe(this)
        dataFragment = supportFragmentManager.getDataFragmentInstance(DATA_FRAGMENT_TAG)
        dataFragment.data?.apply {
            presenter.page = page
        }
        presenter.subscribe(this)
        initAdapter(dataFragment.data?.model)
    }

    override fun loadData(shouldRefresh: Boolean) {
        presenter.loadData(shouldRefresh)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        dataFragment.data = PagedDataModel(presenter.page , data)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.unsubscribe()
    }

    override fun removeDataFragment() {
        supportFragmentManager?.removeDataFragment(dataFragment)
    }
}