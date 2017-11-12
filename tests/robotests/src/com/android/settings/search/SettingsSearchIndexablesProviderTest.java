package com.android.settings.search;

import static com.google.common.truth.Truth.assertThat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.SearchIndexablesContract;

import com.android.settings.R;
import com.android.settings.TestConfig;
import com.android.settings.search.indexing.FakeSettingsFragment;
import com.android.settings.testutils.SettingsRobolectricTestRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.Set;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION_O)
public class SettingsSearchIndexablesProviderTest {

    private final String BASE_AUTHORITY = "com.android.settings";

    private SettingsSearchIndexablesProvider mProvider;

    private Set<Class> mProviderClasses;
    private Context mContext;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;

        mProvider = new SettingsSearchIndexablesProvider();
        ProviderInfo info = new ProviderInfo();
        info.exported = true;
        info.grantUriPermissions = true;
        info.authority = BASE_AUTHORITY;
        info.readPermission = Manifest.permission.READ_SEARCH_INDEXABLES;
        mProvider.attachInfo(mContext, info);

        mProviderClasses = new HashSet<>(SettingsSearchIndexablesProvider.INDEXABLES);
        SettingsSearchIndexablesProvider.INDEXABLES.clear();
        SettingsSearchIndexablesProvider.INDEXABLES.add(FakeSettingsFragment.class);
    }

    @After
    public void cleanUp() {
        SettingsSearchIndexablesProvider.INDEXABLES.clear();
        SettingsSearchIndexablesProvider.INDEXABLES.addAll(mProviderClasses);
    }

    @Test
    public void testRawColumnFetched() {
        Uri rawUri = Uri.parse("content://" + BASE_AUTHORITY + "/" +
                SearchIndexablesContract.INDEXABLES_RAW_PATH);

        final Cursor cursor = mProvider.query(rawUri,
                SearchIndexablesContract.INDEXABLES_RAW_COLUMNS, null, null, null);

        cursor.moveToFirst();
        assertThat(cursor.getString(1)).isEqualTo(FakeSettingsFragment.TITLE);
        assertThat(cursor.getString(2)).isEqualTo(FakeSettingsFragment.SUMMARY_ON);
        assertThat(cursor.getString(3)).isEqualTo(FakeSettingsFragment.SUMMARY_OFF);
        assertThat(cursor.getString(4)).isEqualTo(FakeSettingsFragment.ENTRIES);
        assertThat(cursor.getString(5)).isEqualTo(FakeSettingsFragment.KEYWORDS);
        assertThat(cursor.getString(6)).isEqualTo(FakeSettingsFragment.SCREEN_TITLE);
        assertThat(cursor.getString(7)).isEqualTo(FakeSettingsFragment.CLASS_NAME);
        assertThat(cursor.getInt(8)).isEqualTo(FakeSettingsFragment.ICON);
        assertThat(cursor.getString(9)).isEqualTo(FakeSettingsFragment.INTENT_ACTION);
        assertThat(cursor.getString(10)).isEqualTo(FakeSettingsFragment.TARGET_PACKAGE);
        assertThat(cursor.getString(11)).isEqualTo(FakeSettingsFragment.TARGET_CLASS);
        assertThat(cursor.getString(12)).isEqualTo(FakeSettingsFragment.KEY);
    }

    @Test
    public void testResourcesColumnFetched() {
        Uri rawUri = Uri.parse("content://" + BASE_AUTHORITY + "/" +
                SearchIndexablesContract.INDEXABLES_XML_RES_PATH);

        final Cursor cursor = mProvider.query(rawUri,
                SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS, null, null, null);

        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(1);
        assertThat(cursor.getInt(1)).isEqualTo(R.xml.display_settings);
        assertThat(cursor.getString(2)).isEqualTo(FakeSettingsFragment.CLASS_NAME);
        assertThat(cursor.getInt(3)).isEqualTo(0);
        assertThat(cursor.getString(4)).isNull();
        assertThat(cursor.getString(5)).isNull();
        assertThat(cursor.getString(6)).isNull();
    }

    @Test
    public void testNonIndexablesColumnFetched() {
        Uri rawUri = Uri.parse("content://" + BASE_AUTHORITY + "/" +
                SearchIndexablesContract.NON_INDEXABLES_KEYS_PATH);
        //final ContentResolver resolver = mContext.getContentResolver();

        final Cursor cursor = mProvider.query(rawUri,
                SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS, null, null, null);

        cursor.moveToFirst();
        assertThat(cursor.getCount()).isEqualTo(2);
        assertThat(cursor.getString(0)).isEqualTo("pref_key_1");
        cursor.moveToNext();
        assertThat(cursor.getString(0)).isEqualTo("pref_key_3");
    }
}
