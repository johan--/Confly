package com.bookdose.confly.adapter;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.bookdose.confly.ImageReaderFragment;
import com.bookdose.confly.object.Issue;

import java.util.ArrayList;

/**
 * Created by Teebio on 10/2/15 AD.
 */
public class ImageReaderAdapter extends FragmentStatePagerAdapter implements ImageReaderFragment.ImageReaderListener{
    private ArrayList<String> mTopicList;
    private String folder;
    FragmentManager fragmentManager;

    private Resources resources;

    static final int DEFAULT_NUM_FRAGMENTS = 1;
    static final int DEFAULT_NUM_ITEMS = 1;

    @Override
    public void didDoubleTapImageReader() {
        if (pagerAdapterListener != null)
            pagerAdapterListener.didDoubleTap();
    }

    public interface ImageReaderAdapterListener{
        void didSelectedBook(Issue issue);
        void didDoubleTap();
    }

    private ImageReaderAdapterListener pagerAdapterListener;

    public void setPagerAdapterListener(ImageReaderAdapterListener pagerAdapterListener) {
        this.pagerAdapterListener = pagerAdapterListener;
    }

    public ImageReaderAdapter(FragmentManager fm, ArrayList<String> list,
                               Resources re, String folder)
    {
        super(fm);
        fragmentManager = fm;
        mTopicList = list;
        resources = re;
        this.folder = folder;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        ImageReaderFragment imageReaderFragment = (ImageReaderFragment)object;
        if (imageReaderFragment.imageReaderListener == null)
            imageReaderFragment.setImageReaderListener(this);
        return super.isViewFromObject(view, object);
    }

//    @Override
//    public Object instantiateItem(View container, int position) {
////        if (mCurTransaction == null) {
////            mCurTransaction = mFragmentManager.beginTransaction();
////        }
//
//        // Do we already have this fragment?
//        String name = makeFragmentName(container.getId(), position);
//        Fragment fragment = fragmentManager.findFragmentByTag(name);
//        if (fragment != null) {
//            fragmentManager.beginTransaction().attach(fragment);
//        } else {
//            fragment = getItem(position);
//            fragmentManager.beginTransaction().add(container.getId(), fragment,
//                    makeFragmentName(container.getId(), position));
//        }
//
//        return fragment;
//    }

    @Override
    public Fragment getItem(int position)
    {
//        Bundle args = new Bundle();
//        args.putInt("firstImage", position * mNumItems);
//
//        // The last page might not have the full number of items.
//        int imageCount = mNumItems;
//        if (position == (mNumFragments - 1))
//        {
//            int numTopics = mTopicList.size();
//            int rem = numTopics % mNumItems;
//            if (rem > 0)
//                imageCount = rem;
//        }
//        args.putInt("imageCount", imageCount);
//        args.putSerializable("topicList", mTopicList);
        String pathFile = mTopicList.get(position);

        ImageReaderFragment image = ImageReaderFragment.newInstance(pathFile, folder);
        image.setImageReaderListener(this);
//        f.setGridFragmentListener(this);
//        f.setArguments(args);
        return image;
    }


    @Override
    public int getCount()
    {
        return mTopicList.size();
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }
}
