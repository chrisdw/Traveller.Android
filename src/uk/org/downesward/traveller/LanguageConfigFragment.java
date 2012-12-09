package uk.org.downesward.traveller;

import java.io.IOException;
import java.io.InputStream;

import uk.org.downesward.traveller.language.Language;
import uk.org.downesward.traveller.language.Languages;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class LanguageConfigFragment extends Fragment implements OnClickListener {
	private String language;
	private boolean mDualPane;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public static LanguageConfigFragment newInstance(String language) {
		LanguageConfigFragment f = new LanguageConfigFragment();

		Bundle args = new Bundle();
		args.putString("LANGUAGE", language);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle args = getArguments();
		if (args != null) {
			language = args.getString("LANGUAGE");
		}
		// Inflate the layout for this fragment
		
		View view = inflater
				.inflate(R.layout.langauageconfig, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		TextView lblSyl = (TextView) getActivity().findViewById(R.id.lblSyll);
		lblSyl.setText(R.string.lblSyl);
		TextView lblWords = (TextView) getActivity()
				.findViewById(R.id.lblWords);
		lblWords.setText(R.string.lblWords);

		NumberPicker numPick = (NumberPicker) getActivity().findViewById(
				R.id.numSyll);
		numPick.setMaxValue(6);
		numPick.setMinValue(1);

		numPick = (NumberPicker) getActivity().findViewById(R.id.numWords);
		numPick.setMaxValue(10);
		numPick.setMinValue(1);

		Button butGen = (Button) getActivity().findViewById(R.id.butGenerate);
		butGen.setText(R.string.butGen);
		butGen.setOnClickListener(this);

		View detailsFrame = getActivity().findViewById(R.id.wordlistfragment);
		mDualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;

	}

	public void onClick(View arg0) {
		NumberPicker numPick = (NumberPicker) getActivity().findViewById(
				R.id.numSyll);
		int numSyl = numPick.getValue();
		numPick = (NumberPicker) getActivity().findViewById(R.id.numWords);
		int numWords = numPick.getValue();
		Bundle b = new Bundle();
		b.putInt("SYLLABLES", numSyl);
		b.putInt("WORDS", numWords);
		AssetManager am = getActivity().getAssets();
		Resources res = getResources();
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

			Language langItem = langs.get(language);
			String[] words = new String[numWords];
			if (langItem != null) {
				for (int i = 0; i < numWords; i++) {
					words[i] = langItem.generateWord(numSyl);
				}
				b.putStringArray("WORDLIST", words);

				if (mDualPane) {
					WordListFragment details = (WordListFragment) getFragmentManager()
							.findFragmentById(R.id.wordlistfragment);
					if (details == null) {
						// Make new fragment to show this selection.
						details = WordListFragment.newInstance(words);
						// Execute a transaction, replacing any existing
						// fragment
						// with this one inside the frame.
						FragmentTransaction ft = getFragmentManager()
								.beginTransaction();
						ft.replace(R.id.wordlistfragment, details);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();
					} else {
						details.setWords(words);
					}
				} else {
					Intent intent = new Intent(getActivity(),
							WordDisplayActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.putExtras(b);
					startActivity(intent);
				}
			}

		} catch (IOException io) {
			Toast.makeText(
					getActivity(),
					String.format(
							res.getString(R.string.language_io_exception),
							io.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

	void showDetails(String[] words) {
		setLanguage(language);
		if (mDualPane) {
			// We can display everything in-place with fragments, so update
			// the list to highlight the selected item and show the data.
			// getListView().setItemChecked(index, true);
			// Check what fragment is currently shown, replace if needed.
			WordListFragment details = (WordListFragment) getFragmentManager()
					.findFragmentById(R.id.wordlistfragment);
			if (details == null) {
				// Make new fragment to show this selection.
				details = WordListFragment.newInstance(words);
				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				ft.replace(R.id.wordlistfragment, details);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
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
}
