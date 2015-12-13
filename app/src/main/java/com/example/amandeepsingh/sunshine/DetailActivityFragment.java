package com.example.amandeepsingh.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.amandeepsingh.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static int DETAIL_LOADER = 0;
    private static final String Log_Tag =DetailActivityFragment.class.getSimpleName();
    private static final String Forecast_Share_HashTag=" #SunShine";
    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;

    private static final String[] FORECAST_COLUMNS = {
                           WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                           WeatherContract.WeatherEntry.COLUMN_DATE,
                           WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                           WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                           WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                   };
                // these constants correspond to the projection defined above, and must change if the
                   // projection changes
                private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent=getActivity().getIntent();
        if (intent != null) {
            mForecastStr = intent.getDataString();
        }
        if (null != mForecastStr) {
                            ((TextView) rootView.findViewById(R.id.detail_text))
                                           .setText(mForecastStr);
                       }
        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem item=menu.findItem(R.id.menu_item_share);
        ShareActionProvider mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if(mShareActionProvider!=null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
        {
            Log.d(Log_Tag,"Share Action provider is null");
        }
    }

    private Intent createShareForecastIntent()
    {
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr+Forecast_Share_HashTag);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings)
        {
            Intent settingsIntent=new Intent(getActivity(),SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(Intent shareIntent)
    {
        if(mShareActionProvider!=null)
        {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


    @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                    getLoaderManager().initLoader(DETAIL_LOADER, null, this);
                    super.onActivityCreated(savedInstanceState);
                }

    @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    Intent intent = getActivity().getIntent();
                    if (intent == null) {
                            return null;
                        }

                            // Now create and return a CursorLoader that will take care of
                                    // creating a Cursor for the data being displayed.
                                            return new CursorLoader(
                                   getActivity(), intent.getData(),
                                    FORECAST_COLUMNS,
                                    null,
                                    null,
                                    null
                                    );
                }

                    @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    if (!data.moveToFirst()) { return; }

                            String dateString = Utility.formatDate(
                                    data.getLong(COL_WEATHER_DATE));

                            String weatherDescription =
                                    data.getString(COL_WEATHER_DESC);

                            boolean isMetric = Utility.isMetric(getActivity());

                            String high = Utility.formatTemperature(
                                    data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

                            String low = Utility.formatTemperature(
                                   data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

                        mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

                            TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
                    detailTextView.setText(mForecastStr);

                           // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                                    if (mShareActionProvider != null) {
                            mShareActionProvider.setShareIntent(createShareForecastIntent());
                        }
                }

                   @Override
            public void onLoaderReset(Loader<Cursor> loader) { }
}

