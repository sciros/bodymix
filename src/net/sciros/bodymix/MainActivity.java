package net.sciros.bodymix;

import net.sciros.bodymix.adapter.PlaylistTypeSpinnerAdapter;
import net.sciros.bodymix.adapter.SectionsPagerAdapter;
import net.sciros.bodymix.domain.AlbumType;
import net.sciros.bodymix.domain.InternalConstants;
import net.sciros.bodymix.domain.Playlist;
import net.sciros.bodymix.io.PlaylistRefresher;
import net.sciros.bodymix.userstate.RunningSession;
import net.sciros.bodymix.userstate.UserStateConstants;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, PlaylistRefresher {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (actionBar.getSelectedTab().getPosition() != position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            Tab tab = actionBar.newTab();
            tab.setText(sectionsPagerAdapter.getPageTitle(i));
            tab.setTabListener(this);
            actionBar.addTab(tab);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        int position = tab.getPosition();
        if (position != viewPager.getCurrentItem()) {
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (InternalConstants.PLAYLIST_TAB_POSITION.equals(tab.getPosition())) {
            Fragment displayedFragment = sectionsPagerAdapter.getDisplayedFragment(InternalConstants.PLAYLIST_TAB_POSITION);
            if (displayedFragment instanceof PlaylistFragment) {
                sectionsPagerAdapter.handlePlaylistsSectionReselected();
            }
        } else if (InternalConstants.TRACKS_TAB_POSITION.equals(tab.getPosition())) {
            Fragment displayedFragment = sectionsPagerAdapter.getDisplayedFragment(InternalConstants.TRACKS_TAB_POSITION);
            if (displayedFragment instanceof TracksFragment) {
                sectionsPagerAdapter.handleTracksSectionReselected();
            }
        }
    }
    
    protected void handleTrackEditing () {
        //RunningSession should contain track number, playlist, and albums
        Fragment displayedFragment = sectionsPagerAdapter.getDisplayedFragment(InternalConstants.TRACKS_TAB_POSITION);
        if (this.sectionsPagerAdapter.getDisplayingAlbumFragment()) {
            AlbumsFragment albumsFragment = (AlbumsFragment) displayedFragment;
            albumsFragment.getSwapFragmentsListener().onSwapFragments();
        } else {
            TracksFragment tracksFragment = (TracksFragment) displayedFragment;
            tracksFragment.findTracksAndBuildViewAdapterAndListener();
        }
        getActionBar().setSelectedNavigationItem(InternalConstants.TRACKS_TAB_POSITION);
    }
    
    protected void handleTrackSelection () {
        Fragment displayedFragment = sectionsPagerAdapter.getDisplayedFragment(InternalConstants.TRACKS_TAB_POSITION);
        if (displayedFragment instanceof TracksFragment) {
            TracksFragment tracksFragment = (TracksFragment) displayedFragment;
            tracksFragment.getSwapFragmentsListnener().onSwapFragments();
        }
        
        PlaylistFragment playlistFragment = (PlaylistFragment) sectionsPagerAdapter.getDisplayedFragment(InternalConstants.PLAYLIST_TAB_POSITION);
        playlistFragment.addViewAdapterAndListener();
        
        getActionBar().setSelectedNavigationItem(InternalConstants.PLAYLIST_TAB_POSITION);
    }
    
    protected void handlePlaylistSelection () {
        Fragment displayedFragment = sectionsPagerAdapter.getDisplayedFragment(InternalConstants.PLAYLIST_TAB_POSITION);
        if (displayedFragment instanceof PlaylistsFragment) {
            PlaylistsFragment playlistsFragment = (PlaylistsFragment) displayedFragment;
            playlistsFragment.getSwapFragmentsListener().onSwapFragments();
        }
        
        PlaylistFragment playlistFragment = (PlaylistFragment) sectionsPagerAdapter.getDisplayedFragment(InternalConstants.PLAYLIST_TAB_POSITION);
        playlistFragment.addViewAdapterAndListener();
    }
    
    protected void handleNewPlaylist () {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.create_new_playlist);
        alertDialogBuilder.setView(generateNewPlaylistFormView());
        final PlaylistRefresher playlistRefresher = this;
        final Context context = this;
        alertDialogBuilder.setPositiveButton(R.string.create, null);
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                TextView errorText = (TextView) ((AlertDialog) dialog).findViewById(R.id.errorText);
                errorText.setText("");
                
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new OnShowListener() {
           @Override
           public void onShow (DialogInterface dialog) {
               Button createButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
               createButton.setOnClickListener(new OnClickListener() {
                   @Override
                   public void onClick (View view) {
                       TextView errorText = (TextView) alertDialog.findViewById(R.id.errorText);
                       errorText.setText("");
                       
                       EditText playlistNameTextField = (EditText) alertDialog.findViewById(R.id.playlist_name_text_field);
                       Spinner playlistTypeSpinner = (Spinner) alertDialog.findViewById(R.id.playlist_type_spinner);
                       NumberPicker playlistLengthNumberPicker = (NumberPicker) alertDialog.findViewById(R.id.playlist_length_number_picker);
                       String playlistName = playlistNameTextField.getText().toString();
                       AlbumType playlistType = (AlbumType) playlistTypeSpinner.getSelectedItem();
                       Integer playlistLength = playlistLengthNumberPicker.getValue();
                       
                       Playlist playlist = new Playlist();
                       if (!playlist.validateFields(playlistName, playlistType, playlistLength)) {
                           errorText.setText(R.string.name_required);
                       } else {
                           playlist.setName(playlistName);
                           playlist.setType(playlistType);
                           playlist.setLength(playlistLength);
                           playlist.fillInPlaylistBlanksWithBlankTracks();
                           playlist.fillInFilePathUsingName();
                           playlist.save(context, playlistRefresher, true);
                           RunningSession.getInstance().getAttributes().put(UserStateConstants.CURRENT_PLAYLIST, playlist);
                           handlePlaylistSelection();
                           alertDialog.dismiss();
                       }
                   }
               });
            }
        });
        alertDialog.show();
    }
    
    private View generateNewPlaylistFormView () {
        LayoutInflater inflater = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newPlaylistFormView = inflater.inflate(R.layout.new_playlist, null, false);
        Spinner playlistTypeSpinner = (Spinner) newPlaylistFormView.findViewById(R.id.playlist_type_spinner);
        playlistTypeSpinner.setAdapter(new PlaylistTypeSpinnerAdapter(this, AlbumType.values()));
        NumberPicker playlistLengthNumberPicker = (NumberPicker) newPlaylistFormView.findViewById(R.id.playlist_length_number_picker);
        playlistLengthNumberPicker.setMaxValue(16);
        playlistLengthNumberPicker.setMinValue(5);
        playlistLengthNumberPicker.setValue(10);
        return newPlaylistFormView;
    }
    
    protected static String makeFragmentName (Integer viewId, Integer position) {
        return "android:switcher:"+viewId.toString()+":"+position.toString();
    }
    
    @Override
    public void refreshPlaylists () {
        PlaylistsFragment playlistsFragment = (PlaylistsFragment) sectionsPagerAdapter.getRegisteredFragment(PlaylistsFragment.class.getSimpleName());
        playlistsFragment.getAdapter().notifyDataSetChanged();
    }
}
