package net.sciros.bodymix.adapter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sciros.bodymix.AlbumsFragment;
import net.sciros.bodymix.PlaylistFragment;
import net.sciros.bodymix.PlaylistsFragment;
import net.sciros.bodymix.R;
import net.sciros.bodymix.TracksFragment;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.listener.SwapFragmentsListener;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private FragmentManager fragmentManager;
    private Fragment leftFragmentToSwapOut;
    private Fragment rightFragmentToSwapOut;
    
    private boolean displayingAlbumFragment;
    public boolean getDisplayingAlbumFragment () { return this.displayingAlbumFragment; }
    
    private SparseArray<Fragment> displayedFragments = new SparseArray<Fragment>();
    private Map<String,Fragment> registeredFragments = new HashMap<String,Fragment>();
    
    public Fragment getRegisteredFragment (String key) {
        return registeredFragments.get(key);
    }
    
    public Fragment getDisplayedFragment (int position) {
        return displayedFragments.get(position);
    }
    
    public SectionsPagerAdapter(FragmentManager fm, Context cx) {
        super(fm);
        fragmentManager = fm;
        context = cx;
    }

    private final class SwapTrackAndAlbumFragmentsListener implements SwapFragmentsListener {
        @Override
        public void onSwapFragments () {
            if (rightFragmentToSwapOut != null) {
              fragmentManager.beginTransaction().remove(rightFragmentToSwapOut).commit();
            }
            if (rightFragmentToSwapOut instanceof AlbumsFragment) {
                rightFragmentToSwapOut = TracksFragment.newInstance(swapTrackAndAlbumFragmentsListener);
                displayingAlbumFragment = false;
            } else {
                rightFragmentToSwapOut = AlbumsFragment.newInstance(swapTrackAndAlbumFragmentsListener);
                displayingAlbumFragment = true;
            }
            notifyDataSetChanged();
        }
    }
    
    private final class SwapPlaylistAndAllPlaylistsFragmentsListener implements SwapFragmentsListener {
        @Override
        public void onSwapFragments () {
            if (leftFragmentToSwapOut != null) {
                fragmentManager.beginTransaction().remove(leftFragmentToSwapOut).commit();
            }
            if (leftFragmentToSwapOut instanceof PlaylistsFragment) {
                leftFragmentToSwapOut = PlaylistFragment.newInstance(swapPlaylistAndAllPlaylistsFragmentsListener);
            } else {
                leftFragmentToSwapOut = PlaylistsFragment.newInstance(swapPlaylistAndAllPlaylistsFragmentsListener);
            }
            notifyDataSetChanged();
        }
    }
    
    private SwapFragmentsListener swapTrackAndAlbumFragmentsListener = new SwapTrackAndAlbumFragmentsListener();
    private SwapFragmentsListener swapPlaylistAndAllPlaylistsFragmentsListener = new SwapPlaylistAndAllPlaylistsFragmentsListener();
    
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        if (position == InternalConstants.PLAYLIST_TAB_POSITION) {
            if (leftFragmentToSwapOut == null) { //first time -- show all playlists
                leftFragmentToSwapOut = PlaylistsFragment.newInstance(swapPlaylistAndAllPlaylistsFragmentsListener);
            }
            return leftFragmentToSwapOut;
        } else if (position == InternalConstants.TRACKS_TAB_POSITION) {
            if (rightFragmentToSwapOut == null) { //first time -- show all albums
                rightFragmentToSwapOut = AlbumsFragment.newInstance(swapTrackAndAlbumFragmentsListener);
                displayingAlbumFragment = true;
            }
            return rightFragmentToSwapOut;
        } else {
            return null;
        }
    }
    
    @Override
    public int getItemPosition (Object object) {
        if ((object instanceof AlbumsFragment && rightFragmentToSwapOut instanceof TracksFragment) ||
            (object instanceof TracksFragment && rightFragmentToSwapOut instanceof AlbumsFragment) ||
            (object instanceof PlaylistFragment && rightFragmentToSwapOut instanceof AlbumsFragment) ||
            (object instanceof PlaylistFragment && leftFragmentToSwapOut instanceof PlaylistsFragment) ||
            (object instanceof PlaylistsFragment && leftFragmentToSwapOut instanceof PlaylistFragment)) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public int getCount() {
        return 2; // total pages.
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        CharSequence title = null;
        if (position == InternalConstants.PLAYLIST_TAB_POSITION) {
            title = context.getString(R.string.playlists).toUpperCase(l);
        } else if (position == InternalConstants.TRACKS_TAB_POSITION) {
            title = context.getString(R.string.albums).toUpperCase(l) + "/" + context.getString(R.string.tracks).toUpperCase(l);
        } else {
            title = context.getString(R.string.tracks).toUpperCase(l);
        }
        return title;
    }
    
    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        displayedFragments.put(position, fragment);
        registeredFragments.put(fragment.getClass().getSimpleName(), fragment);
        return fragment;
    }
    
    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        displayedFragments.remove(position);
        super.destroyItem(container,  position, object);
    }
    
    public void handlePlaylistsSectionReselected () {
        if (leftFragmentToSwapOut instanceof PlaylistFragment)
            swapPlaylistAndAllPlaylistsFragmentsListener.onSwapFragments();
    }
    
    public void handleTracksSectionReselected () {
        if (rightFragmentToSwapOut instanceof TracksFragment)
            swapTrackAndAlbumFragmentsListener.onSwapFragments();
    }
}
