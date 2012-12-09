package uk.org.downesward.traveller;

import java.io.IOException;
import java.io.InputStream;

import uk.org.downesward.traveller.language.Language;
import uk.org.downesward.traveller.language.Languages;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class LanguageListFragment extends ListFragment {
	boolean mDualPane;
	private String mCurLanguage = "";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Resources res = getResources();
		AssetManager am = getActivity().getAssets();
		try {
			Languages langs = new Languages();
			Language oLang;
			InputStream lang;

			String[] files = am.list("languages");
			for (int i = 0; i < files.length; i++) {
				lang = am.open("languages/" + files[i]);
				oLang = new Language(lang);
				oLang.setLanguage(files[i].substring(0, files[i].length() - 4));
				langs.put(oLang.getLanguage(), oLang);
			}
			String[] langList = new String[langs.size()];
			int i = 0;
			for (Language langItem : langs.values()) {
				langList[i++] = langItem.getLanguage();
			}

			ArrayAdapter<String> list = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_list_item_activated_1,
					langList);
			list.sort(String.CASE_INSENSITIVE_ORDER);
			this.setListAdapter(list);

		} catch (IOException io) {
			Toast.makeText(
					this.getActivity(),
					String.format(
							res.getString(R.string.language_io_exception),
							io.getMessage()), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(
					this.getActivity(),
					String.format(
							res.getString(R.string.language_io_exception),
							e.getMessage()), Toast.LENGTH_LONG).show();
		}
		// Check to see if we have a frame in which to embed the details
		// fragment directly in the containing UI.
		View detailsFrame = getActivity().findViewById(R.id.languageconfigfragment);
		mDualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;
		if (savedInstanceState != null) {
			// Restore last state for checked position.
			setLanguage(savedInstanceState.getString("LANGUAGE"));
		}
		if (mDualPane) {
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Bundle b = new Bundle();
		String item = (String) getListAdapter().getItem(position);
		b.putString("LANGUAGE", item);

		// Need to get the configuration fragment and set it's language
		showDetails(item);
	}

	void showDetails(String language) {
		setLanguage(language);
		if (mDualPane) {
			// We can display everything in-place with fragments, so update
			// the list to highlight the selected item and show the data.
			//getListView().setItemChecked(index, true);
			// Check what fragment is currently shown, replace if needed.
			LanguageConfigFragment details = (LanguageConfigFragment) getFragmentManager()
					.findFragmentById(R.id.languageconfigfragment);
			if (details == null || details.getLanguage() != language) {
				// Make new fragment to show this selection.
				if (details == null) {
					details = LanguageConfigFragment.newInstance(language);
					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();
					ft.replace(R.id.languageconfigfragment, details);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
				}
				else {
					details.setLanguage(language);
				}
			}
		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			Intent intent = new Intent();
			intent.setClass(getActivity(), LanguageConfigurationActvity.class);
			intent.putExtra("LANGUAGE", language);
			startActivity(intent);
		}
	}
	
	public static LanguageListFragment newInstance(String language) {
		LanguageListFragment f = new LanguageListFragment();
		
		Bundle args = new Bundle();
		args.putString("LANGUAGE", language);
		f.setArguments(args);
		return f;
	}

	void setLanguage(String mCurLanguage) {
		this.mCurLanguage = mCurLanguage;
	}

	String getLanguage() {
		return mCurLanguage;
	}

}
