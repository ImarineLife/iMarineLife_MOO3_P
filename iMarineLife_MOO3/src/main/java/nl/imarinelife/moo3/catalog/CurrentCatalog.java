package nl.imarinelife.moo3.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.imarinelife.lib.LibApp;
import nl.imarinelife.lib.Preferences;
import nl.imarinelife.lib.catalog.Catalog;
import nl.imarinelife.lib.divinglog.db.dive.DiveDbHelper;
import nl.imarinelife.lib.divinglog.db.res.LocationDbHelper;
import nl.imarinelife.lib.divinglog.db.res.ProfilePartDbHelper;
import nl.imarinelife.lib.fieldguide.db.FieldGuideAndSightingsEntryDbHelper;
import nl.imarinelife.moo3.R;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CurrentCatalog extends Catalog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int DATAVERSION_FIELDGUIDEANDSIGHTINGS = 6;
	public static final int DATAVERSION_LOCATIONS = 2;
	public static final int DATAVERSION_PROFILEPARTS = 2;

	public static final String TAG = "CurrentCatalog";

	private static CurrentCatalog me = null;

	public static Catalog getInstance(Context ctx) {
		Log.d(TAG, "getInstance");
		if (me == null) {
			me = new CurrentCatalog(ctx);
			LibApp.getInstance().setCatalog(me);

			me.initializeLocations(ctx);
			me.initializeProfileParts(ctx);
			me.initializeFieldGuide(ctx);
			me.cleanup();

		}
		return me;
	}

	private CurrentCatalog(Context ctx) {
		super(ctx);
		// first thing to do is decide on the possible language
		possibleLanguage = ctx.getResources().getString(R.string.language);
		String currentLanguage = Preferences.getString(
				Preferences.CURRENT_LANGUAGE, null);
		String ignoredLanguage = Preferences.getString(
				Preferences.IGNORED_LANGUAGE, null);
		if (currentLanguage == null) {
			Log.d(TAG, "setting language [" + possibleLanguage
					+ "] in CURRENT_LANGUAGE");
			Preferences.setString(Preferences.CURRENT_LANGUAGE,
					possibleLanguage);
			currentLanguage = possibleLanguage;
		}
		if (ignoredLanguage==null || !ignoredLanguage.equals(possibleLanguage)) {
			ignoredLanguage = currentLanguage;
			Preferences
					.setString(Preferences.IGNORED_LANGUAGE, ignoredLanguage);
		}
		Log.d(TAG, "Language possible[" + possibleLanguage + "] current["
				+ currentLanguage + "] ignored(if not current)["
				+ ignoredLanguage + "]");
		

		acknowledgements = LibApp.getCurrentResources().getString(R.string.ack);

		try {
			version = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			version = 1;
		}

		try {
			versionName = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "1.1";
		}

		name = LibApp.getCurrentResources().getString(R.string.catalog_name);
		project_name = LibApp.getCurrentResources().getString(R.string.project_name);
		expansionFileMainVersion = Integer.parseInt(LibApp.getCurrentResources()
				.getString(R.string.expansionfile_main_version));
		expansionFilePatchVersion = Integer.parseInt(LibApp.getCurrentResources()
				.getString(R.string.expansionfile_patch_version));
		appName = LibApp.getCurrentResources().getString(R.string.app_name);
		introduction = LibApp.getCurrentResources().getString(R.string.introduction);
		help = LibApp.getCurrentResources().getString(R.string.help_for_observations);
		thanks = LibApp.getCurrentResources().getString(R.string.thanks);
		mailBody = LibApp.getCurrentResources().getString(R.string.mail_body);
		mailFrom = LibApp.getCurrentResources().getString(R.string.mail_from);
		mailFromPwd = LibApp.getCurrentResources().getString(R.string.mail_from_password);
		mailTo = LibApp.getCurrentResources().getString(R.string.mail_to);
		if (Preferences.getString(Preferences.CURRENT_LANGUAGE, null) == null) {
			Preferences.setString(Preferences.CURRENT_LANGUAGE,
					possibleLanguage);
		}

		ids = new Integer[] { 132233, 132251, 170782, 134296, 134121, 168589,
				165853, 165801, 132663, 132833, 132883, 135304, 135302, 135306,
				135263, 135299, 117768, 157933, 117994, 117491, 117644, 231751,
				117273, 125333, 283798, 100803, 100834, 100872, 395099, 100982,
				100994, 100991, 101002, 106361, 106386, 129686, 131495, 130967,
				334510, 555935, 140143, 146905, 139718, 138963, 140403, 138878,
				138235, 139686, 140092, 140032, 150457, 140627, 139580, 140840,
				139523, 140855, 140857, 138709, 138711, 156714, 139908, 139768,
				139765, 141627, 140830, 141615, 153380, 141641, 140480, 140687,
				140658, 140656, 140732, 141444, 141454, 140271, 107253, 107232,
				107154, 107188, 107322, 107323, 107333, 205077, 107345, 107276,
				107398, 107392, 107387, 107388, 107381, 107418, 389288, 158417,
				111355, 111159, 111367, 120026, 120177, 107613, 107614, 107616,
				181372, 107486, 106986, 107518, 107651, 107552, 146768, 128548,
				123776, 124929, 125131, 124319, 103732, 103718, 103929, 103788,
				103647, 250126, 103579, 148715, 103862, 126281, 126442, 126448,
				126436, 126438, 126440, 126445, 126977, 127430, 126505, 127387,
				127203, 127204, 127214, 127219, 126975, 127459, 127123, 126996,
				126768, 126770, 126792, 126892, 126928, 127143, 127141, 127160 };

		latinIds = new Integer[] { R.string.latin132233, R.string.latin132251,
				R.string.latin170782, R.string.latin134296,
				R.string.latin134121, R.string.latin168589,
				R.string.latin165853, R.string.latin165801,
				R.string.latin132663, R.string.latin132833,
				R.string.latin132883, R.string.latin135304,
				R.string.latin135302, R.string.latin135306,
				R.string.latin135263, R.string.latin135299,
				R.string.latin117768, R.string.latin157933,
				R.string.latin117994, R.string.latin117491,
				R.string.latin117644, R.string.latin231751,
				R.string.latin117273, R.string.latin125333,
				R.string.latin283798, R.string.latin100803,
				R.string.latin100834, R.string.latin100872,
				R.string.latin395099, R.string.latin100982,
				R.string.latin100994, R.string.latin100991,
				R.string.latin101002, R.string.latin106361,
				R.string.latin106386, R.string.latin129686,
				R.string.latin131495, R.string.latin130967,
				R.string.latin334510, R.string.latin555935,
				R.string.latin140143, R.string.latin146905,
				R.string.latin139718, R.string.latin138963,
				R.string.latin140403, R.string.latin138878,
				R.string.latin138235, R.string.latin139686,
				R.string.latin140092, R.string.latin140032,
				R.string.latin150457, R.string.latin140627,
				R.string.latin139580, R.string.latin140840,
				R.string.latin139523, R.string.latin140855,
				R.string.latin140857, R.string.latin138709,
				R.string.latin138711, R.string.latin156714,
				R.string.latin139908, R.string.latin139768,
				R.string.latin139765, R.string.latin141627,
				R.string.latin140830, R.string.latin141615,
				R.string.latin153380, R.string.latin141641,
				R.string.latin140480, R.string.latin140687,
				R.string.latin140658, R.string.latin140656,
				R.string.latin140732, R.string.latin141444,
				R.string.latin141454, R.string.latin140271,
				R.string.latin107253, R.string.latin107232,
				R.string.latin107154, R.string.latin107188,
				R.string.latin107322, R.string.latin107323,
				R.string.latin107333, R.string.latin205077,
				R.string.latin107345, R.string.latin107276,
				R.string.latin107398, R.string.latin107392,
				R.string.latin107387, R.string.latin107388,
				R.string.latin107381, R.string.latin107418,
				R.string.latin389288, R.string.latin158417,
				R.string.latin111355, R.string.latin111159,
				R.string.latin111367, R.string.latin120026,
				R.string.latin120177, R.string.latin107613,
				R.string.latin107614, R.string.latin107616,
				R.string.latin181372, R.string.latin107486,
				R.string.latin106986, R.string.latin107518,
				R.string.latin107651, R.string.latin107552,
				R.string.latin146768, R.string.latin128548,
				R.string.latin123776, R.string.latin124929,
				R.string.latin125131, R.string.latin124319,
				R.string.latin103732, R.string.latin103718,
				R.string.latin103929, R.string.latin103788,
				R.string.latin103647, R.string.latin250126,
				R.string.latin103579, R.string.latin148715,
				R.string.latin103862, R.string.latin126281,
				R.string.latin126442, R.string.latin126448,
				R.string.latin126436, R.string.latin126438,
				R.string.latin126440, R.string.latin126445,
				R.string.latin126977, R.string.latin127430,
				R.string.latin126505, R.string.latin127387,
				R.string.latin127203, R.string.latin127204,
				R.string.latin127214, R.string.latin127219,
				R.string.latin126975, R.string.latin127459,
				R.string.latin127123, R.string.latin126996,
				R.string.latin126768, R.string.latin126770,
				R.string.latin126792, R.string.latin126892,
				R.string.latin126928, R.string.latin127143,
				R.string.latin127141, R.string.latin127160 };

		commonIds = new HashMap<String, Integer>();
		commonIds.put("common132233", R.string.common132233);
		commonIds.put("common132251", R.string.common132251);
		commonIds.put("common170782", R.string.common170782);
		commonIds.put("common134296", R.string.common134296);
		commonIds.put("common134121", R.string.common134121);
		commonIds.put("common168589", R.string.common168589);
		commonIds.put("common165853", R.string.common165853);
		commonIds.put("common165801", R.string.common165801);
		commonIds.put("common132663", R.string.common132663);
		commonIds.put("common132833", R.string.common132833);
		commonIds.put("common132883", R.string.common132883);
		commonIds.put("common135304", R.string.common135304);
		commonIds.put("common135302", R.string.common135302);
		commonIds.put("common135306", R.string.common135306);
		commonIds.put("common135263", R.string.common135263);
		commonIds.put("common135299", R.string.common135299);
		commonIds.put("common117768", R.string.common117768);
		commonIds.put("common157933", R.string.common157933);
		commonIds.put("common117994", R.string.common117994);
		commonIds.put("common117491", R.string.common117491);
		commonIds.put("common117644", R.string.common117644);
		commonIds.put("common231751", R.string.common231751);
		commonIds.put("common117273", R.string.common117273);
		commonIds.put("common125333", R.string.common125333);
		commonIds.put("common283798", R.string.common283798);
		commonIds.put("common100803", R.string.common100803);
		commonIds.put("common100834", R.string.common100834);
		commonIds.put("common100872", R.string.common100872);
		commonIds.put("common395099", R.string.common395099);
		commonIds.put("common100982", R.string.common100982);
		commonIds.put("common100994", R.string.common100994);
		commonIds.put("common100991", R.string.common100991);
		commonIds.put("common101002", R.string.common101002);
		commonIds.put("common106361", R.string.common106361);
		commonIds.put("common106386", R.string.common106386);
		commonIds.put("common129686", R.string.common129686);
		commonIds.put("common131495", R.string.common131495);
		commonIds.put("common130967", R.string.common130967);
		commonIds.put("common334510", R.string.common334510);
		commonIds.put("common555935", R.string.common555935);
		commonIds.put("common140143", R.string.common140143);
		commonIds.put("common146905", R.string.common146905);
		commonIds.put("common139718", R.string.common139718);
		commonIds.put("common138963", R.string.common138963);
		commonIds.put("common140403", R.string.common140403);
		commonIds.put("common138878", R.string.common138878);
		commonIds.put("common138235", R.string.common138235);
		commonIds.put("common139686", R.string.common139686);
		commonIds.put("common140092", R.string.common140092);
		commonIds.put("common140032", R.string.common140032);
		commonIds.put("common150457", R.string.common150457);
		commonIds.put("common140627", R.string.common140627);
		commonIds.put("common139580", R.string.common139580);
		commonIds.put("common140840", R.string.common140840);
		commonIds.put("common139523", R.string.common139523);
		commonIds.put("common140855", R.string.common140855);
		commonIds.put("common140857", R.string.common140857);
		commonIds.put("common138709", R.string.common138709);
		commonIds.put("common138711", R.string.common138711);
		commonIds.put("common156714", R.string.common156714);
		commonIds.put("common139908", R.string.common139908);
		commonIds.put("common139768", R.string.common139768);
		commonIds.put("common139765", R.string.common139765);
		commonIds.put("common141627", R.string.common141627);
		commonIds.put("common140830", R.string.common140830);
		commonIds.put("common141615", R.string.common141615);
		commonIds.put("common153380", R.string.common153380);
		commonIds.put("common141641", R.string.common141641);
		commonIds.put("common140480", R.string.common140480);
		commonIds.put("common140687", R.string.common140687);
		commonIds.put("common140658", R.string.common140658);
		commonIds.put("common140656", R.string.common140656);
		commonIds.put("common140732", R.string.common140732);
		commonIds.put("common141444", R.string.common141444);
		commonIds.put("common141454", R.string.common141454);
		commonIds.put("common140271", R.string.common140271);
		commonIds.put("common107253", R.string.common107253);
		commonIds.put("common107232", R.string.common107232);
		commonIds.put("common107154", R.string.common107154);
		commonIds.put("common107188", R.string.common107188);
		commonIds.put("common107322", R.string.common107322);
		commonIds.put("common107323", R.string.common107323);
		commonIds.put("common107333", R.string.common107333);
		commonIds.put("common205077", R.string.common205077);
		commonIds.put("common107345", R.string.common107345);
		commonIds.put("common107276", R.string.common107276);
		commonIds.put("common107398", R.string.common107398);
		commonIds.put("common107392", R.string.common107392);
		commonIds.put("common107387", R.string.common107387);
		commonIds.put("common107388", R.string.common107388);
		commonIds.put("common107381", R.string.common107381);
		commonIds.put("common107418", R.string.common107418);
		commonIds.put("common389288", R.string.common389288);
		commonIds.put("common158417", R.string.common158417);
		commonIds.put("common111355", R.string.common111355);
		commonIds.put("common111159", R.string.common111159);
		commonIds.put("common111367", R.string.common111367);
		commonIds.put("common120026", R.string.common120026);
		commonIds.put("common120177", R.string.common120177);
		commonIds.put("common107613", R.string.common107613);
		commonIds.put("common107614", R.string.common107614);
		commonIds.put("common107616", R.string.common107616);
		commonIds.put("common181372", R.string.common181372);
		commonIds.put("common107486", R.string.common107486);
		commonIds.put("common106986", R.string.common106986);
		commonIds.put("common107518", R.string.common107518);
		commonIds.put("common107651", R.string.common107651);
		commonIds.put("common107552", R.string.common107552);
		commonIds.put("common146768", R.string.common146768);
		commonIds.put("common128548", R.string.common128548);
		commonIds.put("common123776", R.string.common123776);
		commonIds.put("common124929", R.string.common124929);
		commonIds.put("common125131", R.string.common125131);
		commonIds.put("common124319", R.string.common124319);
		commonIds.put("common103732", R.string.common103732);
		commonIds.put("common103718", R.string.common103718);
		commonIds.put("common103929", R.string.common103929);
		commonIds.put("common103788", R.string.common103788);
		commonIds.put("common103647", R.string.common103647);
		commonIds.put("common250126", R.string.common250126);
		commonIds.put("common103579", R.string.common103579);
		commonIds.put("common148715", R.string.common148715);
		commonIds.put("common103862", R.string.common103862);
		commonIds.put("common126281", R.string.common126281);
		commonIds.put("common126442", R.string.common126442);
		commonIds.put("common126448", R.string.common126448);
		commonIds.put("common126436", R.string.common126436);
		commonIds.put("common126438", R.string.common126438);
		commonIds.put("common126440", R.string.common126440);
		commonIds.put("common126445", R.string.common126445);
		commonIds.put("common126977", R.string.common126977);
		commonIds.put("common127430", R.string.common127430);
		commonIds.put("common126505", R.string.common126505);
		commonIds.put("common127387", R.string.common127387);
		commonIds.put("common127203", R.string.common127203);
		commonIds.put("common127204", R.string.common127204);
		commonIds.put("common127214", R.string.common127214);
		commonIds.put("common127219", R.string.common127219);
		commonIds.put("common126975", R.string.common126975);
		commonIds.put("common127459", R.string.common127459);
		commonIds.put("common127123", R.string.common127123);
		commonIds.put("common126996", R.string.common126996);
		commonIds.put("common126768", R.string.common126768);
		commonIds.put("common126770", R.string.common126770);
		commonIds.put("common126792", R.string.common126792);
		commonIds.put("common126892", R.string.common126892);
		commonIds.put("common126928", R.string.common126928);
		commonIds.put("common127143", R.string.common127143);
		commonIds.put("common127141", R.string.common127141);
		commonIds.put("common127160", R.string.common127160);

		commonToGroup = new HashMap<String, String>();
		commonToGroup.put("common132233", "porifera");
		commonToGroup.put("common132251", "porifera");
		commonToGroup.put("common170782", "porifera");
		commonToGroup.put("common134296", "porifera");
		commonToGroup.put("common134121", "porifera");
		commonToGroup.put("common168589", "porifera");
		commonToGroup.put("common165853", "porifera");
		commonToGroup.put("common165801", "porifera");
		commonToGroup.put("common132663", "porifera");
		commonToGroup.put("common132833", "porifera");
		commonToGroup.put("common132883", "porifera");
		commonToGroup.put("common135304", "cnidaria");
		commonToGroup.put("common135302", "cnidaria");
		commonToGroup.put("common135306", "cnidaria");
		commonToGroup.put("common135263", "cnidaria");
		commonToGroup.put("common135299", "cnidaria");
		commonToGroup.put("common117768", "cnidaria");
		commonToGroup.put("common157933", "cnidaria");
		commonToGroup.put("common117994", "cnidaria");
		commonToGroup.put("common117491", "cnidaria");
		commonToGroup.put("common117644", "cnidaria");
		commonToGroup.put("common231751", "cnidaria");
		commonToGroup.put("common117273", "cnidaria");
		commonToGroup.put("common125333", "cnidaria");
		commonToGroup.put("common283798", "actiniaria");
		commonToGroup.put("common100803", "actiniaria");
		commonToGroup.put("common100834", "actiniaria");
		commonToGroup.put("common100872", "actiniaria");
		commonToGroup.put("common395099", "actiniaria");
		commonToGroup.put("common100982", "actiniaria");
		commonToGroup.put("common100994", "actiniaria");
		commonToGroup.put("common100991", "actiniaria");
		commonToGroup.put("common101002", "actiniaria");
		commonToGroup.put("common106361", "ctenophora");
		commonToGroup.put("common106386", "ctenophora");
		commonToGroup.put("common129686", "vermes");
		commonToGroup.put("common131495", "vermes");
		commonToGroup.put("common130967", "vermes");
		commonToGroup.put("common334510", "vermes");
		commonToGroup.put("common555935", "vermes");
		commonToGroup.put("common140143", "gastropoda");
		commonToGroup.put("common146905", "gastropoda");
		commonToGroup.put("common139718", "gastropoda");
		commonToGroup.put("common138963", "gastropoda");
		commonToGroup.put("common140403", "gastropoda");
		commonToGroup.put("common138878", "gastropoda");
		commonToGroup.put("common138235", "gastropoda");
		commonToGroup.put("common139686", "nudibranchia");
		commonToGroup.put("common140092", "nudibranchia");
		commonToGroup.put("common140032", "nudibranchia");
		commonToGroup.put("common150457", "nudibranchia");
		commonToGroup.put("common140627", "nudibranchia");
		commonToGroup.put("common139580", "nudibranchia");
		commonToGroup.put("common140840", "nudibranchia");
		commonToGroup.put("common139523", "nudibranchia");
		commonToGroup.put("common140855", "nudibranchia");
		commonToGroup.put("common140857", "nudibranchia");
		commonToGroup.put("common138709", "nudibranchia");
		commonToGroup.put("common138711", "nudibranchia");
		commonToGroup.put("common156714", "nudibranchia");
		commonToGroup.put("common139908", "nudibranchia");
		commonToGroup.put("common139768", "nudibranchia");
		commonToGroup.put("common139765", "nudibranchia");
		commonToGroup.put("common141627", "nudibranchia");
		commonToGroup.put("common140830", "nudibranchia");
		commonToGroup.put("common141615", "nudibranchia");
		commonToGroup.put("common153380", "nudibranchia");
		commonToGroup.put("common141641", "nudibranchia");
		commonToGroup.put("common140480", "bivalvia");
		commonToGroup.put("common140687", "bivalvia");
		commonToGroup.put("common140658", "bivalvia");
		commonToGroup.put("common140656", "bivalvia");
		commonToGroup.put("common140732", "bivalvia");
		commonToGroup.put("common141444", "cephalopoda");
		commonToGroup.put("common141454", "cephalopoda");
		commonToGroup.put("common140271", "cephalopoda");
		commonToGroup.put("common107253", "crustacea_major");
		commonToGroup.put("common107232", "crustacea_major");
		commonToGroup.put("common107154", "crustacea_major");
		commonToGroup.put("common107188", "crustacea_major");
		commonToGroup.put("common107322", "crustacea_major");
		commonToGroup.put("common107323", "crustacea_major");
		commonToGroup.put("common107333", "crustacea_major");
		commonToGroup.put("common205077", "crustacea_major");
		commonToGroup.put("common107345", "crustacea_major");
		commonToGroup.put("common107276", "crustacea_major");
		commonToGroup.put("common107398", "crustacea_major");
		commonToGroup.put("common107392", "crustacea_major");
		commonToGroup.put("common107387", "crustacea_major");
		commonToGroup.put("common107388", "crustacea_major");
		commonToGroup.put("common107381", "crustacea_major");
		commonToGroup.put("common107418", "crustacea_major");
		commonToGroup.put("common389288", "crustacea_major");
		commonToGroup.put("common158417", "crustacea_major");
		commonToGroup.put("common111355", "bryozoa");
		commonToGroup.put("common111159", "bryozoa");
		commonToGroup.put("common111367", "bryozoa");
		commonToGroup.put("common120026", "caridea");
		commonToGroup.put("common120177", "caridea");
		commonToGroup.put("common107613", "caridea");
		commonToGroup.put("common107614", "caridea");
		commonToGroup.put("common107616", "caridea");
		commonToGroup.put("common181372", "caridea");
		commonToGroup.put("common107486", "caridea");
		commonToGroup.put("common106986", "caridea");
		commonToGroup.put("common107518", "caridea");
		commonToGroup.put("common107651", "caridea");
		commonToGroup.put("common107552", "caridea");
		commonToGroup.put("common146768", "caridea");
		commonToGroup.put("common128548", "phoronida");
		commonToGroup.put("common123776", "echinodermata");
		commonToGroup.put("common124929", "echinodermata");
		commonToGroup.put("common125131", "echinodermata");
		commonToGroup.put("common124319", "echinodermata");
		commonToGroup.put("common103732", "ascidiacea");
		commonToGroup.put("common103718", "ascidiacea");
		commonToGroup.put("common103929", "ascidiacea");
		commonToGroup.put("common103788", "ascidiacea");
		commonToGroup.put("common103647", "ascidiacea");
		commonToGroup.put("common250126", "ascidiacea");
		commonToGroup.put("common103579", "ascidiacea");
		commonToGroup.put("common148715", "ascidiacea");
		commonToGroup.put("common103862", "ascidiacea");
		commonToGroup.put("common126281", "pisces");
		commonToGroup.put("common126442", "pisces");
		commonToGroup.put("common126448", "pisces");
		commonToGroup.put("common126436", "pisces");
		commonToGroup.put("common126438", "pisces");
		commonToGroup.put("common126440", "pisces");
		commonToGroup.put("common126445", "pisces");
		commonToGroup.put("common126977", "pisces");
		commonToGroup.put("common127430", "pisces");
		commonToGroup.put("common126505", "pisces");
		commonToGroup.put("common127387", "pisces");
		commonToGroup.put("common127203", "pisces");
		commonToGroup.put("common127204", "pisces");
		commonToGroup.put("common127214", "pisces");
		commonToGroup.put("common127219", "pisces");
		commonToGroup.put("common126975", "pisces");
		commonToGroup.put("common127459", "pisces");
		commonToGroup.put("common127123", "pisces");
		commonToGroup.put("common126996", "pisces");
		commonToGroup.put("common126768", "pisces");
		commonToGroup.put("common126770", "pisces");
		commonToGroup.put("common126792", "pisces");
		commonToGroup.put("common126892", "pisces");
		commonToGroup.put("common126928", "pisces");
		commonToGroup.put("common127143", "pisces");
		commonToGroup.put("common127141", "pisces");
		commonToGroup.put("common127160", "pisces");

		groupIds = new HashMap<String, Integer>();
		groupIds.put("porifera", R.string.porifera);
		groupIds.put("cnidaria", R.string.cnidaria);
		groupIds.put("actiniaria", R.string.actiniaria);
		groupIds.put("ctenophora", R.string.ctenophora);
		groupIds.put("vermes", R.string.vermes);
		groupIds.put("gastropoda", R.string.gastropoda);
		groupIds.put("nudibranchia", R.string.nudibranchia);
		groupIds.put("bivalvia", R.string.bivalvia);
		groupIds.put("cephalopoda", R.string.cephalopoda);
		groupIds.put("crustacea_major", R.string.crustacea_major);
		groupIds.put("bryozoa", R.string.bryozoa);
		groupIds.put("caridea", R.string.caridea);
		groupIds.put("phoronida", R.string.phoronida);
		groupIds.put("echinodermata", R.string.echinodermata);
		groupIds.put("ascidiacea", R.string.ascidiacea);
		groupIds.put("pisces", R.string.pisces);

		descrIds = new HashMap<String, Integer>();
		descrIds.put("descr132233", R.string.descr132233);
		descrIds.put("descr132251", R.string.descr132251);
		descrIds.put("descr170782", R.string.descr170782);
		descrIds.put("descr134296", R.string.descr134296);
		descrIds.put("descr134121", R.string.descr134121);
		descrIds.put("descr168589", R.string.descr168589);
		descrIds.put("descr165853", R.string.descr165853);
		descrIds.put("descr165801", R.string.descr165801);
		descrIds.put("descr132663", R.string.descr132663);
		descrIds.put("descr132833", R.string.descr132833);
		descrIds.put("descr132883", R.string.descr132883);
		descrIds.put("descr135304", R.string.descr135304);
		descrIds.put("descr135302", R.string.descr135302);
		descrIds.put("descr135306", R.string.descr135306);
		descrIds.put("descr135263", R.string.descr135263);
		descrIds.put("descr135299", R.string.descr135299);
		descrIds.put("descr117768", R.string.descr117768);
		descrIds.put("descr157933", R.string.descr157933);
		descrIds.put("descr117994", R.string.descr117994);
		descrIds.put("descr117491", R.string.descr117491);
		descrIds.put("descr117644", R.string.descr117644);
		descrIds.put("descr231751", R.string.descr231751);
		descrIds.put("descr117273", R.string.descr117273);
		descrIds.put("descr125333", R.string.descr125333);
		descrIds.put("descr283798", R.string.descr283798);
		descrIds.put("descr100803", R.string.descr100803);
		descrIds.put("descr100834", R.string.descr100834);
		descrIds.put("descr100872", R.string.descr100872);
		descrIds.put("descr395099", R.string.descr395099);
		descrIds.put("descr100982", R.string.descr100982);
		descrIds.put("descr100994", R.string.descr100994);
		descrIds.put("descr100991", R.string.descr100991);
		descrIds.put("descr101002", R.string.descr101002);
		descrIds.put("descr106361", R.string.descr106361);
		descrIds.put("descr106386", R.string.descr106386);
		descrIds.put("descr129686", R.string.descr129686);
		descrIds.put("descr131495", R.string.descr131495);
		descrIds.put("descr130967", R.string.descr130967);
		descrIds.put("descr334510", R.string.descr334510);
		descrIds.put("descr555935", R.string.descr555935);
		descrIds.put("descr140143", R.string.descr140143);
		descrIds.put("descr146905", R.string.descr146905);
		descrIds.put("descr139718", R.string.descr139718);
		descrIds.put("descr138963", R.string.descr138963);
		descrIds.put("descr140403", R.string.descr140403);
		descrIds.put("descr138878", R.string.descr138878);
		descrIds.put("descr138235", R.string.descr138235);
		descrIds.put("descr139686", R.string.descr139686);
		descrIds.put("descr140092", R.string.descr140092);
		descrIds.put("descr140032", R.string.descr140032);
		descrIds.put("descr150457", R.string.descr150457);
		descrIds.put("descr140627", R.string.descr140627);
		descrIds.put("descr139580", R.string.descr139580);
		descrIds.put("descr140840", R.string.descr140840);
		descrIds.put("descr139523", R.string.descr139523);
		descrIds.put("descr140855", R.string.descr140855);
		descrIds.put("descr140857", R.string.descr140857);
		descrIds.put("descr138709", R.string.descr138709);
		descrIds.put("descr138711", R.string.descr138711);
		descrIds.put("descr156714", R.string.descr156714);
		descrIds.put("descr139908", R.string.descr139908);
		descrIds.put("descr139768", R.string.descr139768);
		descrIds.put("descr139765", R.string.descr139765);
		descrIds.put("descr141627", R.string.descr141627);
		descrIds.put("descr140830", R.string.descr140830);
		descrIds.put("descr141615", R.string.descr141615);
		descrIds.put("descr153380", R.string.descr153380);
		descrIds.put("descr141641", R.string.descr141641);
		descrIds.put("descr140480", R.string.descr140480);
		descrIds.put("descr140687", R.string.descr140687);
		descrIds.put("descr140658", R.string.descr140658);
		descrIds.put("descr140656", R.string.descr140656);
		descrIds.put("descr140732", R.string.descr140732);
		descrIds.put("descr141444", R.string.descr141444);
		descrIds.put("descr141454", R.string.descr141454);
		descrIds.put("descr140271", R.string.descr140271);
		descrIds.put("descr107253", R.string.descr107253);
		descrIds.put("descr107232", R.string.descr107232);
		descrIds.put("descr107154", R.string.descr107154);
		descrIds.put("descr107188", R.string.descr107188);
		descrIds.put("descr107322", R.string.descr107322);
		descrIds.put("descr107323", R.string.descr107323);
		descrIds.put("descr107333", R.string.descr107333);
		descrIds.put("descr205077", R.string.descr205077);
		descrIds.put("descr107345", R.string.descr107345);
		descrIds.put("descr107276", R.string.descr107276);
		descrIds.put("descr107398", R.string.descr107398);
		descrIds.put("descr107392", R.string.descr107392);
		descrIds.put("descr107387", R.string.descr107387);
		descrIds.put("descr107388", R.string.descr107388);
		descrIds.put("descr107381", R.string.descr107381);
		descrIds.put("descr107418", R.string.descr107418);
		descrIds.put("descr389288", R.string.descr389288);
		descrIds.put("descr158417", R.string.descr158417);
		descrIds.put("descr111355", R.string.descr111355);
		descrIds.put("descr111159", R.string.descr111159);
		descrIds.put("descr111367", R.string.descr111367);
		descrIds.put("descr120026", R.string.descr120026);
		descrIds.put("descr120177", R.string.descr120177);
		descrIds.put("descr107613", R.string.descr107613);
		descrIds.put("descr107614", R.string.descr107614);
		descrIds.put("descr107616", R.string.descr107616);
		descrIds.put("descr181372", R.string.descr181372);
		descrIds.put("descr107486", R.string.descr107486);
		descrIds.put("descr106986", R.string.descr106986);
		descrIds.put("descr107518", R.string.descr107518);
		descrIds.put("descr107651", R.string.descr107651);
		descrIds.put("descr107552", R.string.descr107552);
		descrIds.put("descr146768", R.string.descr146768);
		descrIds.put("descr128548", R.string.descr128548);
		descrIds.put("descr123776", R.string.descr123776);
		descrIds.put("descr124929", R.string.descr124929);
		descrIds.put("descr125131", R.string.descr125131);
		descrIds.put("descr124319", R.string.descr124319);
		descrIds.put("descr103732", R.string.descr103732);
		descrIds.put("descr103718", R.string.descr103718);
		descrIds.put("descr103929", R.string.descr103929);
		descrIds.put("descr103788", R.string.descr103788);
		descrIds.put("descr103647", R.string.descr103647);
		descrIds.put("descr250126", R.string.descr250126);
		descrIds.put("descr103579", R.string.descr103579);
		descrIds.put("descr148715", R.string.descr148715);
		descrIds.put("descr103862", R.string.descr103862);
		descrIds.put("descr126281", R.string.descr126281);
		descrIds.put("descr126442", R.string.descr126442);
		descrIds.put("descr126448", R.string.descr126448);
		descrIds.put("descr126436", R.string.descr126436);
		descrIds.put("descr126438", R.string.descr126438);
		descrIds.put("descr126440", R.string.descr126440);
		descrIds.put("descr126445", R.string.descr126445);
		descrIds.put("descr126977", R.string.descr126977);
		descrIds.put("descr127430", R.string.descr127430);
		descrIds.put("descr126505", R.string.descr126505);
		descrIds.put("descr127387", R.string.descr127387);
		descrIds.put("descr127203", R.string.descr127203);
		descrIds.put("descr127204", R.string.descr127204);
		descrIds.put("descr127214", R.string.descr127214);
		descrIds.put("descr127219", R.string.descr127219);
		descrIds.put("descr126975", R.string.descr126975);
		descrIds.put("descr127459", R.string.descr127459);
		descrIds.put("descr127123", R.string.descr127123);
		descrIds.put("descr126996", R.string.descr126996);
		descrIds.put("descr126768", R.string.descr126768);
		descrIds.put("descr126770", R.string.descr126770);
		descrIds.put("descr126792", R.string.descr126792);
		descrIds.put("descr126892", R.string.descr126892);
		descrIds.put("descr126928", R.string.descr126928);
		descrIds.put("descr127143", R.string.descr127143);
		descrIds.put("descr127141", R.string.descr127141);
		descrIds.put("descr127160", R.string.descr127160);
		/*
		 * 
		 * locationNames = new HashMap<String, Integer>();locationsMap.put(
		 * "loc101", R.string.loc101);locationsMap.put( "loc102",
		 * R.string.loc102);locationsMap.put( "loc103",
		 * R.string.loc103);locationsMap.put( "loc104",
		 * R.string.loc104);locationsMap.put( "loc106",
		 * R.string.loc106);locationsMap.put( "loc107",
		 * R.string.loc107);locationsMap.put( "loc108",
		 * R.string.loc108);locationsMap.put( "loc109",
		 * R.string.loc109);locationsMap.put( "loc110",
		 * R.string.loc110);locationsMap.put( "loc111",
		 * R.string.loc111);locationsMap.put( "loc112",
		 * R.string.loc112);locationsMap.put( "loc113",
		 * R.string.loc113);locationsMap.put( "loc114",
		 * R.string.loc114);locationsMap.put( "loc115",
		 * R.string.loc115);locationsMap.put( "loc116",
		 * R.string.loc116);locationsMap.put( "loc117",
		 * R.string.loc117);locationsMap.put( "loc118",
		 * R.string.loc118);locationsMap.put( "loc119",
		 * R.string.loc119);locationsMap.put( "loc120",
		 * R.string.loc120);locationsMap.put( "loc121",
		 * R.string.loc121);locationsMap.put( "loc122",
		 * R.string.loc122);locationsMap.put( "loc123",
		 * R.string.loc123);locationsMap.put( "loc124",
		 * R.string.loc124);locationsMap.put( "loc125",
		 * R.string.loc125);locationsMap.put( "loc126",
		 * R.string.loc126);locationsMap.put( "loc127",
		 * R.string.loc127);locationsMap.put( "loc128",
		 * R.string.loc128);locationsMap.put( "loc129",
		 * R.string.loc129);locationsMap.put( "loc130",
		 * R.string.loc130);locationsMap.put( "loc131",
		 * R.string.loc131);locationsMap.put( "loc132",
		 * R.string.loc132);locationsMap.put( "loc133",
		 * R.string.loc133);locationsMap.put( "loc134",
		 * R.string.loc134);locationsMap.put( "loc135",
		 * R.string.loc135);locationsMap.put( "loc136",
		 * R.string.loc136);locationsMap.put( "loc137",
		 * R.string.loc137);locationsMap.put( "loc138",
		 * R.string.loc138);locationsMap.put( "loc139",
		 * R.string.loc139);locationsMap.put( "loc140",
		 * R.string.loc140);locationsMap.put( "loc141",
		 * R.string.loc141);locationsMap.put( "loc142",
		 * R.string.loc142);locationsMap.put( "loc143",
		 * R.string.loc143);locationsMap.put( "loc144",
		 * R.string.loc144);locationsMap.put( "loc145",
		 * R.string.loc145);locationsMap.put( "loc146",
		 * R.string.loc146);locationsMap.put( "loc147",
		 * R.string.loc147);locationsMap.put( "loc148",
		 * R.string.loc148);locationsMap.put( "loc149",
		 * R.string.loc149);locationsMap.put( "loc150",
		 * R.string.loc150);locationsMap.put( "loc151",
		 * R.string.loc151);locationsMap.put( "loc152",
		 * R.string.loc152);locationsMap.put( "loc153",
		 * R.string.loc153);locationsMap.put( "loc154",
		 * R.string.loc154);locationsMap.put( "loc155",
		 * R.string.loc155);locationsMap.put( "loc156",
		 * R.string.loc156);locationsMap.put( "loc157",
		 * R.string.loc157);locationsMap.put( "loc158",
		 * R.string.loc158);locationsMap.put( "loc159",
		 * R.string.loc159);locationsMap.put( "loc160",
		 * R.string.loc160);locationsMap.put( "loc161",
		 * R.string.loc161);locationsMap.put( "loc162",
		 * R.string.loc162);locationsMap.put( "loc163",
		 * R.string.loc163);locationsMap.put( "loc164",
		 * R.string.loc164);locationsMap.put( "loc165",
		 * R.string.loc165);locationsMap.put( "loc166",
		 * R.string.loc166);locationsMap.put( "loc167",
		 * R.string.loc167);locationsMap.put( "loc168",
		 * R.string.loc168);locationsMap.put( "loc169",
		 * R.string.loc169);locationsMap.put( "loc170",
		 * R.string.loc170);locationsMap.put( "loc171",
		 * R.string.loc171);locationsMap.put( "loc173",
		 * R.string.loc173);locationsMap.put( "loc174",
		 * R.string.loc174);locationsMap.put( "loc175",
		 * R.string.loc175);locationsMap.put( "loc176",
		 * R.string.loc176);locationsMap.put( "loc177",
		 * R.string.loc177);locationsMap.put( "loc178",
		 * R.string.loc178);locationsMap.put( "loc179",
		 * R.string.loc179);locationsMap.put( "loc180",
		 * R.string.loc180);locationsMap.put( "loc181",
		 * R.string.loc181);locationsMap.put( "loc182",
		 * R.string.loc182);locationsMap.put( "loc183",
		 * R.string.loc183);locationsMap.put( "loc184",
		 * R.string.loc184);locationsMap.put( "loc185",
		 * R.string.loc185);locationsMap.put( "loc186",
		 * R.string.loc186);locationsMap.put( "loc189",
		 * R.string.loc189);locationsMap.put( "loc190",
		 * R.string.loc190);locationsMap.put( "loc191",
		 * R.string.loc191);locationsMap.put( "loc192",
		 * R.string.loc192);locationsMap.put( "loc193",
		 * R.string.loc193);locationsMap.put( "loc194",
		 * R.string.loc194);locationsMap.put( "loc195",
		 * R.string.loc195);locationsMap.put( "loc196",
		 * R.string.loc196);locationsMap.put( "loc199",
		 * R.string.loc199);locationsMap.put( "loc200",
		 * R.string.loc200);locationsMap.put( "loc201",
		 * R.string.loc201);locationsMap.put( "loc202",
		 * R.string.loc202);locationsMap.put( "loc203",
		 * R.string.loc203);locationsMap.put( "loc204",
		 * R.string.loc204);locationsMap.put( "loc205",
		 * R.string.loc205);locationsMap.put( "loc206",
		 * R.string.loc206);locationsMap.put( "loc207",
		 * R.string.loc207);locationsMap.put( "loc208",
		 * R.string.loc208);locationsMap.put( "loc209",
		 * R.string.loc209);locationsMap.put( "loc210",
		 * R.string.loc210);locationsMap.put( "loc211",
		 * R.string.loc211);locationsMap.put( "loc212",
		 * R.string.loc212);locationsMap.put( "loc213",
		 * R.string.loc213);locationsMap.put( "loc214",
		 * R.string.loc214);locationsMap.put( "loc215",
		 * R.string.loc215);locationsMap.put( "loc218",
		 * R.string.loc218);locationsMap.put( "loc222",
		 * R.string.loc222);locationsMap.put( "loc226",
		 * R.string.loc226);locationsMap.put( "loc233",
		 * R.string.loc233);locationsMap.put( "loc237",
		 * R.string.loc237);locationsMap.put( "loc256",
		 * R.string.loc256);locationsMap.put( "loc257",
		 * R.string.loc257);locationsMap.put( "loc258",
		 * R.string.loc258);locationsMap.put( "loc259",
		 * R.string.loc259);locationsMap.put( "loc260",
		 * R.string.loc260);locationsMap.put( "loc261",
		 * R.string.loc261);locationsMap.put( "loc262",
		 * R.string.loc262);locationsMap.put( "loc263",
		 * R.string.loc263);locationsMap.put( "loc266",
		 * R.string.loc266);locationsMap.put( "loc267",
		 * R.string.loc267);locationsMap.put( "loc272",
		 * R.string.loc272);locationsMap.put( "loc278",
		 * R.string.loc278);locationsMap.put( "loc300",
		 * R.string.loc300);locationsMap.put( "loc302",
		 * R.string.loc302);locationsMap.put( "loc303",
		 * R.string.loc303);locationsMap.put( "loc304",
		 * R.string.loc304);locationsMap.put( "loc305",
		 * R.string.loc305);locationsMap.put( "loc306",
		 * R.string.loc306);locationsMap.put( "loc307",
		 * R.string.loc307);locationsMap.put( "loc308",
		 * R.string.loc308);locationsMap.put( "loc309",
		 * R.string.loc309);locationsMap.put( "loc311",
		 * R.string.loc311);locationsMap.put( "loc315",
		 * R.string.loc315);locationsMap.put( "loc317",
		 * R.string.loc317);locationsMap.put( "loc318",
		 * R.string.loc318);locationsMap.put( "loc319",
		 * R.string.loc319);locationsMap.put( "loc320", R.string.loc320);
		 * 
		 * profileParts = new String[][] {{ "pp_0to3", "true","1" }, {
		 * "pp_3to6", "true","2" }, { "pp_6to12", "true","3" }, { "pp_12to18",
		 * "true","4" }, { "pp_18", "true","5" }, { "pp_wreak", "false","6" }, {
		 * "pp_ponton", "false","7" }}; profilePartsMap = new HashMap<String,
		 * Integer>();profilepartsMap.put( "pp_0to3",
		 * R.string.pp_0to3);profilepartsMap.put( "pp_3to6",
		 * R.string.pp_3to6);profilepartsMap.put( "pp_6to12",
		 * R.string.pp_6to12);profilepartsMap.put( "pp_12to18",
		 * R.string.pp_12to18);profilepartsMap.put( "pp_18",
		 * R.string.pp_18);profilepartsMap.put( "pp_wreak",
		 * R.string.pp_wreak);profilepartsMap.put( "pp_ponton",
		 * R.string.pp_ponton);
		 */

		checkValues = new String[] { null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, "Ei", null, "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei", null, "Ei", "Ei", "Ei", "Ei", "Ei", "Ei",
				"Ei", null, null, null, null, null, "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei", "Ei",
				"Ei", "Ei", "Ei" };

		valuesMap = new HashMap<String, Integer>();
		valuesMap.put("Poliep", R.string.Poliep);
		valuesMap.put("Ei", R.string.Ei);
		valuesMap.put("Z", R.string.Z);
		valuesMap.put("A", R.string.A);
		valuesMap.put("M", R.string.M);

		locationNames = new HashMap<String, Integer>();
		locationNames.put("loc101", R.string.loc101);
		locationNames.put("loc102", R.string.loc102);
		locationNames.put("loc103", R.string.loc103);
		locationNames.put("loc104", R.string.loc104);
		locationNames.put("loc106", R.string.loc106);
		locationNames.put("loc107", R.string.loc107);
		locationNames.put("loc108", R.string.loc108);
		locationNames.put("loc109", R.string.loc109);
		locationNames.put("loc110", R.string.loc110);
		locationNames.put("loc111", R.string.loc111);
		locationNames.put("loc112", R.string.loc112);
		locationNames.put("loc113", R.string.loc113);
		locationNames.put("loc114", R.string.loc114);
		locationNames.put("loc115", R.string.loc115);
		locationNames.put("loc116", R.string.loc116);
		locationNames.put("loc117", R.string.loc117);
		locationNames.put("loc118", R.string.loc118);
		locationNames.put("loc119", R.string.loc119);
		locationNames.put("loc120", R.string.loc120);
		locationNames.put("loc121", R.string.loc121);
		locationNames.put("loc122", R.string.loc122);
		locationNames.put("loc123", R.string.loc123);
		locationNames.put("loc124", R.string.loc124);
		locationNames.put("loc125", R.string.loc125);
		locationNames.put("loc126", R.string.loc126);
		locationNames.put("loc127", R.string.loc127);
		locationNames.put("loc128", R.string.loc128);
		locationNames.put("loc129", R.string.loc129);
		locationNames.put("loc130", R.string.loc130);
		locationNames.put("loc131", R.string.loc131);
		locationNames.put("loc132", R.string.loc132);
		locationNames.put("loc133", R.string.loc133);
		locationNames.put("loc134", R.string.loc134);
		locationNames.put("loc135", R.string.loc135);
		locationNames.put("loc136", R.string.loc136);
		locationNames.put("loc137", R.string.loc137);
		locationNames.put("loc138", R.string.loc138);
		locationNames.put("loc139", R.string.loc139);
		locationNames.put("loc140", R.string.loc140);
		locationNames.put("loc141", R.string.loc141);
		locationNames.put("loc142", R.string.loc142);
		locationNames.put("loc143", R.string.loc143);
		locationNames.put("loc144", R.string.loc144);
		locationNames.put("loc145", R.string.loc145);
		locationNames.put("loc146", R.string.loc146);
		locationNames.put("loc147", R.string.loc147);
		locationNames.put("loc148", R.string.loc148);
		locationNames.put("loc149", R.string.loc149);
		locationNames.put("loc150", R.string.loc150);
		locationNames.put("loc151", R.string.loc151);
		locationNames.put("loc152", R.string.loc152);
		locationNames.put("loc153", R.string.loc153);
		locationNames.put("loc154", R.string.loc154);
		locationNames.put("loc155", R.string.loc155);
		locationNames.put("loc156", R.string.loc156);
		locationNames.put("loc157", R.string.loc157);
		locationNames.put("loc158", R.string.loc158);
		locationNames.put("loc159", R.string.loc159);
		locationNames.put("loc160", R.string.loc160);
		locationNames.put("loc161", R.string.loc161);
		locationNames.put("loc162", R.string.loc162);
		locationNames.put("loc163", R.string.loc163);
		locationNames.put("loc164", R.string.loc164);
		locationNames.put("loc165", R.string.loc165);
		locationNames.put("loc166", R.string.loc166);
		locationNames.put("loc167", R.string.loc167);
		locationNames.put("loc168", R.string.loc168);
		locationNames.put("loc169", R.string.loc169);
		locationNames.put("loc170", R.string.loc170);
		locationNames.put("loc171", R.string.loc171);
		locationNames.put("loc173", R.string.loc173);
		locationNames.put("loc174", R.string.loc174);
		locationNames.put("loc175", R.string.loc175);
		locationNames.put("loc176", R.string.loc176);
		locationNames.put("loc177", R.string.loc177);
		locationNames.put("loc178", R.string.loc178);
		locationNames.put("loc179", R.string.loc179);
		locationNames.put("loc180", R.string.loc180);
		locationNames.put("loc181", R.string.loc181);
		locationNames.put("loc182", R.string.loc182);
		locationNames.put("loc183", R.string.loc183);
		locationNames.put("loc184", R.string.loc184);
		locationNames.put("loc185", R.string.loc185);
		locationNames.put("loc186", R.string.loc186);
		locationNames.put("loc189", R.string.loc189);
		locationNames.put("loc190", R.string.loc190);
		locationNames.put("loc191", R.string.loc191);
		locationNames.put("loc192", R.string.loc192);
		locationNames.put("loc193", R.string.loc193);
		locationNames.put("loc194", R.string.loc194);
		locationNames.put("loc195", R.string.loc195);
		locationNames.put("loc196", R.string.loc196);
		locationNames.put("loc199", R.string.loc199);
		locationNames.put("loc200", R.string.loc200);
		locationNames.put("loc201", R.string.loc201);
		locationNames.put("loc202", R.string.loc202);
		locationNames.put("loc203", R.string.loc203);
		locationNames.put("loc204", R.string.loc204);
		locationNames.put("loc205", R.string.loc205);
		locationNames.put("loc206", R.string.loc206);
		locationNames.put("loc207", R.string.loc207);
		locationNames.put("loc208", R.string.loc208);
		locationNames.put("loc209", R.string.loc209);
		locationNames.put("loc210", R.string.loc210);
		locationNames.put("loc211", R.string.loc211);
		locationNames.put("loc212", R.string.loc212);
		locationNames.put("loc213", R.string.loc213);
		locationNames.put("loc214", R.string.loc214);
		locationNames.put("loc215", R.string.loc215);
		locationNames.put("loc218", R.string.loc218);
		locationNames.put("loc222", R.string.loc222);
		locationNames.put("loc226", R.string.loc226);
		locationNames.put("loc233", R.string.loc233);
		locationNames.put("loc237", R.string.loc237);
		locationNames.put("loc256", R.string.loc256);
		locationNames.put("loc257", R.string.loc257);
		locationNames.put("loc258", R.string.loc258);
		locationNames.put("loc259", R.string.loc259);
		locationNames.put("loc260", R.string.loc260);
		locationNames.put("loc261", R.string.loc261);
		locationNames.put("loc262", R.string.loc262);
		locationNames.put("loc263", R.string.loc263);
		locationNames.put("loc266", R.string.loc266);
		locationNames.put("loc267", R.string.loc267);
		locationNames.put("loc272", R.string.loc272);
		locationNames.put("loc278", R.string.loc278);
		locationNames.put("loc300", R.string.loc300);
		locationNames.put("loc302", R.string.loc302);
		locationNames.put("loc303", R.string.loc303);
		locationNames.put("loc304", R.string.loc304);
		locationNames.put("loc305", R.string.loc305);
		locationNames.put("loc306", R.string.loc306);
		locationNames.put("loc307", R.string.loc307);
		locationNames.put("loc308", R.string.loc308);
		locationNames.put("loc309", R.string.loc309);
		locationNames.put("loc311", R.string.loc311);
		locationNames.put("loc315", R.string.loc315);
		locationNames.put("loc317", R.string.loc317);
		locationNames.put("loc318", R.string.loc318);
		locationNames.put("loc319", R.string.loc319);
		locationNames.put("loc320", R.string.loc320);

		profileParts = new String[][] { { "pp_0to3", "true", "1" },
				{ "pp_3to6", "true", "2" }, { "pp_6to12", "true", "3" },
				{ "pp_12to18", "true", "4" }, { "pp_18", "true", "5" },
				{ "pp_wreak", "false", "6" }, { "pp_ponton", "false", "7" } };
		profilePartsMap = new HashMap<String, Integer>();
		profilePartsMap.put("pp_0to3", R.string.pp_0to3);
		profilePartsMap.put("pp_3to6", R.string.pp_3to6);
		profilePartsMap.put("pp_6to12", R.string.pp_6to12);
		profilePartsMap.put("pp_12to18", R.string.pp_12to18);
		profilePartsMap.put("pp_18", R.string.pp_18);
		profilePartsMap.put("pp_wreak", R.string.pp_wreak);
		profilePartsMap.put("pp_ponton", R.string.pp_ponton);

		allgroups = LibApp.getCurrentResources().getString(R.string.allgroups);

		if (sightingChoices.isEmpty()) {
			sightingChoices.add("?");
			sightingChoices.add("0");
			sightingChoices.add("Z");
			sightingChoices.add("A");
			sightingChoices.add("M");

			checkboxChoices.add("Ei");
			defaultChoice = "?";
		}
		if (defaultableSightingChoices.isEmpty()) {
			defaultableSightingChoices.add("?");
			defaultableSightingChoices.add("0");
		}

		// necessary byte array for getting expansion file
		// null if no expansionfile is available
		salt = new byte[] { 12, 67, -15, 6, -55, -87, 99, -12, 41, 65, 25, 88,
				-19, -66, 102, -10, -31, -92, -61, 3 };

		// necessary base64 public key for getting expansion file
		// null if no expansionfile is available
		base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj8z8sSvkS1om9oJptKRXOVVWZRMejhA5O4Ij2Hp2fN2qfY6jpQdlBrkrsoOxoYniwy8zmK3aPcTiey2hpe3/ZYxJBiP38KZLSg33sRPCDAHWVM0V0rADONnVu7iy9cF/w2v19GcI/srdOJNAdIy7woDpRtRLR7fGfwvn4EYiImmErHH65O468UnOSfy+U9wLgCmHQRDsSaRG3ii5mTwOU5PIxOxK206a2gk5e28osZbI9vspcgXy79PqDQIzHuv//sZqGlEA80jznQ7rLf6kn6QSASe3R8ZflTNRSPNZHWV03umvK8JVYPbXE2G7UJFS0lpsODMn5JKje2k6ZL1drwIDAQAB";
	}

	public String getPossibleLanguage() {
		return possibleLanguage;
	}

	public void setPossibleLanguage(String language) {
		this.possibleLanguage = language;
	}

	public Integer[] getIds() {
		return ids;
	}

	public Integer[] getLatinIds() {
		return latinIds;
	}

	public Map<String, Integer> getCommonIdMapping() {
		return commonIds;
	}

	public Map<String, Integer> getGroupIdMapping() {
		return groupIds;
	}

	public Map<String, Integer> getDescriptionIdMapping() {
		return descrIds;
	}

	public String[] getCheckValues() {
		return checkValues;
	}

	public Map<String, Integer> getProfilePartsMapping() {
		return profilePartsMap;
	}

	public Map<String, Integer> getLocationNamesMapping() {
		return locationNames;
	}

	public Map<String, Integer> getValuesMapping() {
		return valuesMap;
	}

	public String getAppName() {
		return appName;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public String getAllGroups() {
		return allgroups;
	}

	public byte[] getSalt() {
		return salt;
	}

	public String getBase64PublicKey() {
		return base64PublicKey;
	}

	public String getMailBody() {
		return mailBody;
	}

	public String getMailTo() {
		return mailTo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public String getMailFromPwd() {
		return mailFromPwd;
	}

	public ArrayList<String> getSightingChoices() {
		return sightingChoices;
	}

	public ArrayList<String> getDefaultableSightingChoices() {
		return defaultableSightingChoices;
	}

	public ArrayList<String> getCheckBoxChoices() {
		return checkboxChoices;
	}

	public String getIntroduction() {
		return introduction;
	}

	public String getHelp() {
		return help;
	}

	public String getThanks() {
		return thanks;
	}

	public String getProject() {
		return project_name;
	}

	@Override
	public int getExpansionFileMainVersion() {
		return expansionFileMainVersion;
	}

	@Override
	public int getExpansionFilePatchVersion() {
		return expansionFilePatchVersion;
	}

	@Override
	public Map<String, String> getCommonToGroupMapping() {
		return commonToGroup;
	}

	public int getDataVersion_FieldGuideAndSightings(){
		return DATAVERSION_FIELDGUIDEANDSIGHTINGS;
	}
	
	public int getDataVersion_Locations(){
		return DATAVERSION_LOCATIONS;
		
	}
	public int getDataVersion_ProfileParts(){
		return DATAVERSION_PROFILEPARTS;
	}
	
	public void onDataVersionUpgrade_FieldGuideAndSightings(
			int oldVersion, int newVersion) {
		boolean doFieldGuideOnlyUpdate = true;
		FieldGuideAndSightingsEntryDbHelper helper = FieldGuideAndSightingsEntryDbHelper.getInstance(LibApp.getContext());
		switch (oldVersion) {
			case 0:// do what needs to be done. like onUpgrade, to be implemented as fallthrough
			case 1: 
			case 2:
			case 3:
				helper.fillFieldsForVersion004(); //refill FieldGuide and Sightings
				doFieldGuideOnlyUpdate=false; //already done
			case 4:
			case 5:	
				if(doFieldGuideOnlyUpdate){
					helper.resetLocaleDependentContent(true);
					doFieldGuideOnlyUpdate = false; //already done
				}
		}
	}
	
	public void onDataVersionUpgrade_Locations(
			int oldVersion, int newVersion){
		switch (oldVersion) {
		case 0: // do what needs to be done. like onUpgrade, to be
				// implemented as fallthrough
		case 1: 
			Catalog catalog = LibApp.getInstance().getCurrentCatalog();
			if (catalog != null) {
				DiveDbHelper diveDbHelper = DiveDbHelper.getInstance(LibApp
						.getContext());
				LocationDbHelper locationsHelper = LocationDbHelper.getInstance(LibApp.getContext());
				// will fill locations anew
				catalog.fillLocations(locationsHelper, diveDbHelper);
			}
		}
	}
	
	public void onDataVersionUpgrade_ProfileParts(
			int oldVersion, int newVersion){
		switch (oldVersion) {
		case 0: // do what needs to be done. like onUpgrade, to be
				// implemented as fallthrough
		case 1: Catalog catalog = LibApp.getInstance().getCurrentCatalog();
			if (catalog != null) {
				// will fill profileparts anew
				ProfilePartDbHelper helper = ProfilePartDbHelper.getInstance(LibApp.getInstance());
				catalog.fillProfileParts(helper);
				Preferences.setInt(Preferences.DATAVERSION_PROFILEPARTS, newVersion);
			}
		}
	}

	
	
}
