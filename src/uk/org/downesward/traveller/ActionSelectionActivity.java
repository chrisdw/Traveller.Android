package uk.org.downesward.traveller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActionSelectionActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionselection);
		Button butGen = (Button) this.findViewById(R.id.butLangauge);

		butGen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActionSelectionActivity.this,
						LanguageActivity.class);
				startActivity(intent);
			}         
		});

		butGen = (Button) this.findViewById(R.id.butEncounters);

		butGen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ActionSelectionActivity.this,
						EncounterConfigurationActivity.class);
				startActivity(intent);
			}         
		});
	}
}
