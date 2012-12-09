package uk.org.downesward.traveller;

import java.io.IOException;
import java.io.InputStream;

import uk.org.downesward.traveller.language.Language;
import uk.org.downesward.traveller.language.Languages;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class LanguageConfigurationActvity extends Activity implements
		OnClickListener {
	static final int PICK_LANGUAGE = 0;
	String language;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.langauageconfig);

		Intent intent = new Intent(LanguageConfigurationActvity.this,
				LanguageSelectionActivity.class);
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(intent, PICK_LANGUAGE);

		TextView lblSyl = (TextView) this.findViewById(R.id.lblSyll);
		lblSyl.setText(R.string.lblSyl);
		TextView lblWords = (TextView) this.findViewById(R.id.lblWords);
		lblWords.setText(R.string.lblWords);

		NumberPicker numPick = (NumberPicker) this.findViewById(R.id.numSyll);
		numPick.setMaxValue(6);
		numPick.setMinValue(1);

		numPick = (NumberPicker) this.findViewById(R.id.numWords);
		numPick.setMaxValue(10);
		numPick.setMinValue(1);

		Button butGen = (Button) this.findViewById(R.id.butGenerate);
		butGen.setText(R.string.butGen);
		butGen.setOnClickListener(this);
	}
	
    protected void onActivityResult(int requestCode, int resultCode,
    		Intent data) {
    			
    	if (requestCode == PICK_LANGUAGE) {
    		if (resultCode == RESULT_OK) {
    			Bundle b = data.getExtras();
    			if (b != null) {
    				language = b.getString("LANGUAGE");
    				this.setTitle(language);
    			}
    		}
    		else {
    			this.finish();
    		}
    	}
    }
    
	public void onClick(View v) {
		NumberPicker numPick = (NumberPicker) this.findViewById(R.id.numSyll);
		int numSyl = numPick.getValue();
		numPick = (NumberPicker) this.findViewById(R.id.numWords);
		int numWords = numPick.getValue();
		Bundle b = new Bundle();
		b.putInt("SYLLABLES", numSyl);
		b.putInt("WORDS", numWords);
		AssetManager am = getAssets();
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
			for (int i = 0; i < numWords; i++) {
				words[i] = langItem.generateWord(numSyl);
			}
			b.putStringArray("WORDLIST", words);
			
			Intent intent = new Intent(LanguageConfigurationActvity.this,
					WordDisplayActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.putExtras(b);
			startActivity(intent);
			
		} catch (IOException io) {
			Toast.makeText(
					this,
					String.format(
							res.getString(R.string.language_io_exception),
							io.getMessage()), Toast.LENGTH_LONG).show();
		}
	}

}
