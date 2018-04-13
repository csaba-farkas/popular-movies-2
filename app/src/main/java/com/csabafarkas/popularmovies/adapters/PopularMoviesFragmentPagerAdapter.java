package com.csabafarkas.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.csabafarkas.popularmovies.MovieDetailsFragment;
import com.csabafarkas.popularmovies.R;

public class PopularMoviesFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context context;

    public PopularMoviesFragmentPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
        tabTitles = new String[] {
                context.getResources().getString(R.string.plot_tab_title),
                context.getResources().getString(R.string.trailers_tab_title),
                context.getResources().getString(R.string.reviews_tab_title)
        };
    }
    @Override
    public Fragment getItem(int position) {
        return MovieDetailsFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
