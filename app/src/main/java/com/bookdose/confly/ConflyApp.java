package com.bookdose.confly;

import android.app.Application;

import com.skytree.epub.BookInformation;

import java.util.ArrayList;

import epubtest.CustomFont;
import epubtest.SkyDatabase;
import epubtest.SkySetting;

/**
 * Created by Teebio on 11/10/15 AD.
 */
public class ConflyApp extends Application {
    public ArrayList<BookInformation> bis;
    public ArrayList<CustomFont> customFonts = new ArrayList<CustomFont>();
    public SkySetting setting;
    public SkyDatabase sd = null;
    public int sortType=0;

    @Override
    public void onCreate() {
        super.onCreate();
        sd = new SkyDatabase(this);
        //reloadBookInformations();
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
