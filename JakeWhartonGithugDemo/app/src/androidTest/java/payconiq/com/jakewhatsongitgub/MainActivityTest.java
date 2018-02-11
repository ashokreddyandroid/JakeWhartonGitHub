package payconiq.com.jakewhatsongitgub;


import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;

import payconiq.com.jakewhartongitgub.R;
import payconiq.com.jakewhartongitgub.activities.MainActivity;

/**
 * Created by Ashok on 11/02/18.
 */

public class MainActivityTest   extends ActivityInstrumentationTestCase2<MainActivity> {
    /**
     * Creates an {@link ActivityInstrumentationTestCase2}.
     *
     * @param activityClass The activity to test. This must be a class in the instrumentation
     *                      targetPackage specified in the AndroidManifest.xml
     */
    public MainActivityTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    public MainActivityTest() {
        super(MainActivity.class);
    }


    private MainActivity mainActivity;
    private RecyclerView githubList;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mainActivity = getActivity();
        githubList = (RecyclerView) mainActivity.findViewById(R.id.github_recycler);
    }

    public void testPreconditions() {
        assertNotNull("Activity is not null", mainActivity);
        assertNotNull("Recycleview is not null", githubList);
    }
}
