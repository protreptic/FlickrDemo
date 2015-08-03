package name.peterbukhal.android.skbkonturdemo.fragment;

import name.peterbukhal.android.skbkonturdemo.fragment.base.BaseEntityListFragment;
import name.peterbukhal.android.skbkonturdemo.storage.model.BrowsingHistoryItem;
import android.support.v7.widget.RecyclerView.Adapter;

public class BrowsingHistoryFragment extends BaseEntityListFragment<BrowsingHistoryItem> {

	@Override
	protected Adapter<?> createAdapter() {
		return null;
	}

	@Override
	protected void refreshData() {
		
	}

	@Override
	protected Class<BrowsingHistoryItem> getType() {
		return BrowsingHistoryItem.class;
	}

}
