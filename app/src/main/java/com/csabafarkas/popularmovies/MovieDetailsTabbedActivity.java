package com.csabafarkas.popularmovies;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.csabafarkas.popularmovies.adapters.PopularMoviesFragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsTabbedActivity extends AppCompatActivity {

    @BindView(R.id.movie_details_viewpager)
    ViewPager viewPager;
    @BindView(R.id.movie_details_tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details_tabbed);
        ButterKnife.bind(this);
        viewPager.setAdapter(new PopularMoviesFragmentPagerAdapter(getSupportFragmentManager(),
                MovieDetailsTabbedActivity.this));
        tabLayout.setupWithViewPager(viewPager);

    }
}
