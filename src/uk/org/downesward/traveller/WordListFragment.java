package uk.org.downesward.traveller;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class WordListFragment extends ListFragment {
	private String[] words;
	ArrayAdapter<String> list;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle b = getArguments();
		if (b != null) {
			words = b.getStringArray("WORDLIST");
		}
		else {
			words = new String[0];
		}
		
		// Inflate the layout for this fragment

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		Bundle b = getArguments();
		if (b != null) {
			words = b.getStringArray("WORDLIST");
		}
		else {
			words = new String[0];
		}
		list = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_list_item_1, getWords());
		list.sort(String.CASE_INSENSITIVE_ORDER);
		this.setListAdapter(list);
	}
	
	public static WordListFragment newInstance(String[] words) {
		WordListFragment f = new WordListFragment();
		
		Bundle args = new Bundle();
		args.putStringArray("WORDLIST", words);
		f.setArguments(args);
		return f;
	}

	void setWords(String[] words) {
		this.words = words;
		list = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_list_item_1, words);
		list.sort(String.CASE_INSENSITIVE_ORDER);
		this.setListAdapter(list);
	}

	String[] getWords() {
		return words;
	}
}
