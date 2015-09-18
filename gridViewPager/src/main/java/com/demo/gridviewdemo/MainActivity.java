package com.demo.gridviewdemo;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends FragmentActivity implements OnClickListener
{

	FragmentPageAdapter mAdapter;
	ViewPager mPager;
	int mNumFragments = 0; // total number of fragments
	int mNumItems = 0;

	ArrayList<GridItem> list = new ArrayList<GridItem>();
	
	String imagePath;
	LinearLayout llDots;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Foldername/";
		File fl = new File(imagePath);
		File images[] = null;
		if(fl.exists())
		{
			if(fl.isDirectory())
			{
				images = fl.listFiles();
			}
		}
		if(images != null && images.length > 0)
		{
			for (int i = 0; i < images.length ; i++)
			{
				if(images[i] != null)
				{
					GridItem item = new GridItem();
					item.setImagePath("file://" + images[i].getPath());
					item.setTitle("file://" + images[i].getPath());
					list.add(item);
				}
			}
		}
		
		mAdapter = new FragmentPageAdapter(getSupportFragmentManager(), list, getResources());
		mPager = (ViewPager) findViewById(R.id.viewPagger);
		mPager.setOffscreenPageLimit(4);
		mPager.setAdapter(mAdapter);
		llDots = (LinearLayout) findViewById(R.id.llDots);
		
		for (int i = 0; i < mAdapter.getCount(); i++)
		{
			ImageButton imgDot = new ImageButton(this);
			imgDot.setTag(i);
			imgDot.setImageResource(R.drawable.dot_selector);
			imgDot.setBackgroundResource(0);
			imgDot.setPadding(5, 5, 5, 5);
			LayoutParams params = new LayoutParams(20, 20);
			imgDot.setLayoutParams(params);
			if(i == 0)
				imgDot.setSelected(true);
			imgDot.setOnClickListener(this);
			llDots.addView(imgDot);
		}
		
		mPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int pos)
			{
				Log.e("", "Page Selected is ===> " + pos);
				for (int i = 0; i < mAdapter.getCount(); i++)
				{
					if(i != pos)
					{
						((ImageView)llDots.findViewWithTag(i)).setSelected(false);
					}
				}
				((ImageView)llDots.findViewWithTag(pos)).setSelected(true);
			}
			
			@Override
			public void onPageScrolled(int pos, float arg1, int arg2)
			{
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				
			}
		});
	}
	@Override
	public void onClick(View v)
	{
		if(v != null)
		{
			if(v.getTag() != null)
			{
				int pos = (Integer) v.getTag();
				for (int i = 0; i < mAdapter.getCount(); i++)
				{
					if(i != pos)
					{
						((ImageView)llDots.findViewWithTag(i)).setSelected(false);
					}
				}
				mPager.setCurrentItem(pos, true);
				((ImageView)llDots.findViewWithTag(pos)).setSelected(true);
			}
		}
	}
}
