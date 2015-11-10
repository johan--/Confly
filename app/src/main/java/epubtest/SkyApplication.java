package epubtest;

import android.app.Application;

import com.skytree.epub.BookInformation;

import java.util.ArrayList;

public class SkyApplication extends Application {
    public String message = "We are the world.";
	public ArrayList<BookInformation> bis;
	public ArrayList<CustomFont> customFonts = new ArrayList<CustomFont>();
	public SkySetting setting;
	public SkyDatabase sd = null;
	public int sortType=0;
	
	@Override
    public void onCreate() {
        super.onCreate();
        sd = new SkyDatabase(this);
        reloadBookInformations();
        loadSetting();
    }
	
	public void reloadBookInformations() {
		this.bis = sd.fetchBookInformations(sortType,"");
	}
	
	public void reloadBookInformations(String key) {
		this.bis = sd.fetchBookInformations(sortType,key);
	}
	
	public void loadSetting() {
		this.setting = sd.fetchSetting();
	}
	
	public void saveSetting() {
		sd.updateSetting(this.setting);
	}
}
