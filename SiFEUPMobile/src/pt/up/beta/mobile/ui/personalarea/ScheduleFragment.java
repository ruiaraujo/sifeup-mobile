package pt.up.beta.mobile.ui.personalarea;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import pt.up.beta.mobile.datatypes.Block;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.sifeup.ScheduleUtils;
import pt.up.beta.mobile.sifeup.SessionManager;
import pt.up.beta.mobile.tracker.AnalyticsUtils;
import pt.up.beta.mobile.ui.BaseActivity;
import pt.up.beta.mobile.ui.BaseFragment;
import pt.up.beta.mobile.utils.DateUtils;
import pt.up.beta.mobile.utils.FileUtils;
import pt.up.beta.mobile.utils.calendar.CalendarHelper;
import pt.up.beta.mobile.utils.calendar.Event;
import pt.up.beta.mobile.R;

import external.com.google.android.apps.iosched.ui.widget.BlockView;
import external.com.google.android.apps.iosched.ui.widget.BlocksLayout;
import external.com.google.android.apps.iosched.ui.widget.ObservableScrollView;
import external.com.google.android.apps.iosched.util.MotionEventUtils;
import external.com.google.android.apps.iosched.util.UIUtils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This fragment is responsible of fetching a schedule of the student, which
 * number is passed as argument. Also it can export the schedule for Google
 * Calendar.
 * 
 * @author Ângela Igreja
 * 
 */
public class ScheduleFragment extends BaseFragment implements
        ObservableScrollView.OnScrollListener, OnPageChangeListener,
        OnClickListener, ResponseCommand {

    private final static String SCHEDULE_KEY = "pt.up.fe.mobile.ui.studentarea.SCHEDULE";
    private final static String MILLISECONDS_KEY = "pt.up.fe.mobile.ui.studentarea.MILLISECONDS";

    private ViewPager mPager;
    private TextView mTitle;
    private int mTitleCurrentDayIndex = -1;
    private View mLeftIndicator;
    private View mRightIndicator;
    private ArrayList<Block> schedule = new ArrayList<Block>();
    private List<Day> mDays = new ArrayList<Day>();
    private long mondayMillis;
    private LayoutInflater mInflater;
    private boolean fetchingPreviousWeek = false;
    private boolean fetchingNextWeek = false;
    private boolean setToNow = false;
    private String scheduleCode;
    private int scheduleType;
    /**
     * The key for the schedule code in the intent.
     */
    final public static String SCHEDULE_CODE = "pt.up.fe.mobile.ui.studentarea.SCHEDULE_CODE";

    /**
     * The key for the schedule type in the intent.
     */
    final public static String SCHEDULE_TYPE = "pt.up.fe.mobile.ui.studentarea.SCHEDULE_TYPE";

    final public static int SCHEDULE_STUDENT = 0;
    final public static int SCHEDULE_EMPLOYEE = 1;
    final public static int SCHEDULE_ROOM = 2;
    final public static int SCHEDULE_UC = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bundle args = getArguments();
        setHasOptionsMenu(true);
        scheduleCode = getArguments().getString(SCHEDULE_CODE);
        if (scheduleCode == null)
            scheduleCode = SessionManager.getInstance(getActivity())
                    .getLoginCode();
        scheduleType = getArguments().getInt(SCHEDULE_TYPE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mInflater = inflater;
        ViewGroup root = (ViewGroup) inflater.inflate(
                R.layout.fragment_schedule, getParentContainer(), true);
        mPager = (ViewPager) root.findViewById(R.id.pager);
        mPager.setAdapter(new DayAdapter());// Workaround a android bug
        mTitle = (TextView) root.findViewById(R.id.block_title);

        mPager.setOnPageChangeListener(this);

        mLeftIndicator = root.findViewById(R.id.indicator_left);
        mLeftIndicator.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    decreaseDay();
                    return true;
                }
                return false;
            }
        });
        mLeftIndicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                decreaseDay();
            }
        });

        mRightIndicator = root.findViewById(R.id.indicator_right);
        mRightIndicator.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ((motionEvent.getAction() & MotionEventUtils.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    increaseDay();
                    return true;
                }
                return false;
            }
        });
        mRightIndicator.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                increaseDay();
            }
        });
        mondayMillis = DateUtils.firstDayofWeek();
        setupDay(DateUtils.moveDayofWeek(mondayMillis, -3), 0);
        setupDay(mondayMillis, 1);
        setupDay(DateUtils.moveDayofWeek(mondayMillis, 1), 2);
        setupDay(DateUtils.moveDayofWeek(mondayMillis, 2), 3);
        setupDay(DateUtils.moveDayofWeek(mondayMillis, 3), 4);
        setupDay(DateUtils.moveDayofWeek(mondayMillis, 4), 5);
        setupDay(DateUtils.moveDayofWeek(mondayMillis, 7), 6);
        onPageSelected(1);

        return getParentContainer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (schedule != null)
            outState.putParcelableArrayList(SCHEDULE_KEY, schedule);
        outState.putLong(MILLISECONDS_KEY, mondayMillis);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            long millis = savedInstanceState.getLong(MILLISECONDS_KEY);
            if (millis != 0)
                mondayMillis = millis;
            schedule = savedInstanceState.getParcelableArrayList(SCHEDULE_KEY);
            if (schedule == null)
                updateSchedule();
            else {
                setToNow = true;
                displaySchedule();
                showFastMainScreen();
            }
        } else {
            updateSchedule();
        }
    }

    private void increaseDay() {
        mTitleCurrentDayIndex++;
        mPager.setCurrentItem(mTitleCurrentDayIndex);
        if (mTitleCurrentDayIndex >= mDays.size())
            mTitleCurrentDayIndex = 1;
    }

    private void decreaseDay() {
        mTitleCurrentDayIndex--;
        mPager.setCurrentItem(mTitleCurrentDayIndex);
        if (mTitleCurrentDayIndex <= 0)
            mTitleCurrentDayIndex = mDays.size() - 2;
    }

    public void onScrollChanged(ObservableScrollView view) {
        // Keep each day view at the same vertical scroll offset.
        final int scrollY = view.getScrollY();
        for (Day day : mDays) {
            if (day.scrollView != view) {
                day.scrollView.scrollTo(0, scrollY);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_now) {
            if (!updateNowView()) {
                AnalyticsUtils.getInstance(getActivity()).trackEvent(
                        AnalyticsUtils.MENU_CAT, "Click", "Go to Now", 1);
                Toast.makeText(getActivity(), R.string.toast_now_not_visible,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_export_calendar) {
            // export to Calendar (create event)
            AnalyticsUtils.getInstance(getActivity()).trackEvent(
                    AnalyticsUtils.MENU_CAT, "Click", "Export Calendar", 1);
            calendarExport();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void movetoNextWeek() {
        fetchingNextWeek = true;
        mondayMillis = mDays.get(mDays.size() - 1).timeStart;
        updateSchedule();
    }

    private void movetoPreviousWeek() {
        fetchingPreviousWeek = true;
        mondayMillis = mDays.get(1).timeStart;
        mondayMillis = DateUtils.moveDayofWeek(mondayMillis, -7);
        updateSchedule();
    }

    /**
     * Update position and visibility of "now" view.
     */
    private boolean updateNowView() {

        final Time nowTime = new Time(UIUtils.TIME_REFERENCE);
        nowTime.setToNow();
        if (nowTime.weekDay == 0 || nowTime.weekDay == 6)
            return false;
        final long now = nowTime.toMillis(true);

        Day nowDay = null; // effectively Day corresponding to today
        for (Day day : mDays) {
            if (now >= day.timeStart && now <= day.timeEnd) {
                nowDay = day;
                day.nowView.setVisibility(View.VISIBLE);
            } else {
                day.nowView.setVisibility(View.GONE);
            }
        }
        if (nowDay != null) {
            long hours = (now - nowDay.timeStart) / 3600000;
            double timeOffset = (double) (hours - nowDay.blocksView
                    .getTimeRulerStartHour())
                    / (double) nowDay.blocksView.getTimeRulerHours();
            // Scroll to show "now" in center
            mPager.setCurrentItem(nowDay.index);
            nowDay.nowView.invalidate();
            final int offset = (int) (nowDay.scrollView.getHeight() * timeOffset);
            nowDay.scrollView.scrollTo(0, offset);
            nowDay.blocksView.requestLayout();
        } else {
            mondayMillis = DateUtils.firstDayofWeek();
            for (int i = 0; i < mDays.size(); ++i)
                updateDay(i, DateUtils.moveDayofWeek(mondayMillis, i));
            setToNow = true;
            updateSchedule();
        }
        return true;
    }

    private void setupDay(long startMillis, int i) {
        Day day = new Day();

        // Setup data
        day.index = mDays.size();
        day.timeStart = startMillis;
        day.timeEnd = startMillis + android.text.format.DateUtils.DAY_IN_MILLIS;
        // Setup views
        day.rootView = (ViewGroup) mInflater.inflate(R.layout.blocks_content,
                null);

        day.scrollView = (ObservableScrollView) day.rootView
                .findViewById(R.id.blocks_scroll);
        day.scrollView.setOnScrollListener(this);

        day.blocksView = (BlocksLayout) day.rootView.findViewById(R.id.blocks);
        day.nowView = day.rootView.findViewById(R.id.blocks_now);

        day.blocksView.setDrawingCacheEnabled(true);
        day.blocksView.setAlwaysDrawnWithCacheEnabled(true);
        // Clear out any existing sessions before inserting again
        day.blocksView.removeAllBlocks();
        Time date = new Time(UIUtils.TIME_REFERENCE);
        date.set(startMillis);
        date.normalize(false);
        day.label = android.text.format.DateUtils.getDayOfWeekString(date.weekDay + 1,
                android.text.format.DateUtils.LENGTH_LONG) + ", " + date.format("%d-%m");
        mDays.add(i, day);
    }

    private void updateDay(int index, long millis) {

        Time date = new Time(UIUtils.TIME_REFERENCE);
        date.set(millis);
        date.normalize(false);
        mDays.get(index).label = android.text.format.DateUtils.getDayOfWeekString(date.weekDay + 1,
                android.text.format.DateUtils.LENGTH_LONG) + ", " + date.format("%d-%m");
        mDays.get(index).timeStart = millis;
        mDays.get(index).timeEnd = millis + android.text.format.DateUtils.DAY_IN_MILLIS;
    }

    private void cleanBlocks() {
        for (int i = 0; i < mDays.size(); ++i)
            mDays.get(i).blocksView.removeAllBlocks();
    }

    /**
     * A helper class containing object references related to a particular day
     * in the schedule.
     */
    @SuppressWarnings("serial")
    private class Day implements Serializable {
        private ViewGroup rootView;
        private ObservableScrollView scrollView;
        private View nowView;
        private BlocksLayout blocksView;

        private int index = -1;
        private String label = null;
        private long timeStart = -1;
        private long timeEnd = -1;
    }

    private void addBlock(int dayIndex, Block block) {
        if (getActivity() == null) {
            return;
        }
        // Plus one because mDays.at(0) is a day of the previous week
        Day day = mDays.get(dayIndex + 1);

        final String blockId = block.getLectureAcronym()
                + block.getLectureType()
                + (block.getBuildingCode() == null ? "" : block
                        .getBuildingCode()) + block.getRoomCode()
                + block.getStartTime();
        final String title = block.getLectureAcronym()
                + " ("
                + block.getLectureType()
                + ")"
                + "\n"
                + (block.getBuildingCode() == null ? "" : block
                        .getBuildingCode()) + block.getRoomCode();
        final long start = block.getStartTime() * 1000 + day.timeStart;
        final long end = start + (long) (block.getLectureDuration() * 3600000);
        final boolean containsStarred = false;

        int column = 0;
        if (block.getLectureType().equals("T"))
            column = 1;
        final BlockView blockView = new BlockView(getActivity(), blockId,
                title, start, end, containsStarred, column);
        blockView.setOnClickListener(this);
        day.blocksView.addBlock(blockView);

    }


    /**
     * 
     * This function return the milliseconds of the the first day of the week,
     * in our case Monday, though if it is past Friday, it will return the next
     * Monday.
     * 
     * @return mondayMillis milliseconds of the the first day of the week
     */



    /**
     * Exports the schedule to Google Calendar TODO: Produce an ICAL file which
     * can be imported by most calendars.
     */
    private boolean calendarExport() {

        final ContentResolver cr = getActivity().getContentResolver();
        final CalendarHelper calendarHelper = CalendarHelper.getInstance(cr);
        final Cursor cursor = calendarHelper.getCalendars();

        if (cursor == null) {
            if (getActivity() != null)
                Toast.makeText(getActivity(),
                        R.string.toast_export_calendar_error, Toast.LENGTH_LONG)
                        .show();
            return false;
        }
        // Iterate over calendars to store names and ids
        if (cursor.moveToFirst()) {
            final String[] calNames = new String[cursor.getCount()];
            final int[] calIds = new int[cursor.getCount()];
            for (int i = 0; i < calNames.length; i++) {
                calIds[i] = cursor.getInt(0);
                calNames[i] = cursor.getString(1);
                cursor.moveToNext();
            }

            // Throw a Calendar chooser menu
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setSingleChoiceItems(calNames, -1,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // iterate over schedule and add them to schedule
                            for (Block b : schedule) {
                                // new event
                                String title = b.getLectureAcronym() + " ("
                                        + b.getLectureType() + ")";
                                String eventLocation = (b.getBuildingCode() != null ? b
                                        .getBuildingCode() : "")
                                        + b.getRoomCode();
                                String description = "Professor: "
                                        + b.getTeacherAcronym();
                                long date = pt.up.beta.mobile.utils.DateUtils.moveDayofWeek(mondayMillis,
                                        b.getWeekDay())
                                        + b.getStartTime() * 1000;

                                Event event = new Event(
                                        title,
                                        eventLocation,
                                        description,
                                        date,
                                        (long) (date + b.getLectureDuration() * 3600000));
                                final Uri newEvent = calendarHelper
                                        .insertEvent(calIds[which], event);
                                // TODO: event recursive - this will not be
                                // done.
                                // check event error
                                if (newEvent == null)
                                    Log.e("ScheduleExport", "error on event");

                            }

                            dialog.dismiss();
                            if (getActivity() != null)
                                Toast.makeText(
                                        getActivity(),
                                        R.string.toast_export_calendar_finished,
                                        Toast.LENGTH_LONG).show();

                        }

                    });

            builder.create().show();
        }
        cursor.close();

        return true;
    }

    class DayAdapter extends PagerAdapter {
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);

        }

        public void finishUpdate(View arg0) {
        }

        public int getCount() {
            return mDays.size();
        }

        public Object instantiateItem(View collection, int position) {
            ((ViewPager) collection).addView(mDays.get(position).rootView, 0);
            return mDays.get(position).rootView;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View arg0) {
        }

    }

    public void onPageScrollStateChanged(int state) {

    }

    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {

    }

    public void onPageSelected(int position) {
        if (position >= mDays.size())
            return;
        mTitleCurrentDayIndex = position;
        Day day = mDays.get(position);
        mTitle.setText(day.label);
        if (position == 0)
            movetoPreviousWeek();
        else if (position == mDays.size() - 1)
            movetoNextWeek();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof BlockView) {
            Block block = findBlock(((BlockView) view).getBlockId());
            if (block == null)
                Toast.makeText(getActivity(), "Something stupid happened",
                        Toast.LENGTH_SHORT).show();
            if (getActivity() == null)
                return;
            Intent i = new Intent(getActivity(), ClassDescriptionActivity.class);
            i.putExtra(ClassDescriptionFragment.BLOCK, block);
            startActivity(i);
        }
    }

    private Block findBlock(String blockId) {
        for (Block block : schedule) {
            String id = block.getLectureAcronym()
                    + block.getLectureType()
                    + (block.getBuildingCode() == null ? "" : block
                            .getBuildingCode()) + block.getRoomCode()
                    + block.getStartTime();
            if (id.equals(blockId))
                return block;
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public void onError(ERROR_TYPE error) {
        if (getActivity() == null)
            return;
        getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        switch (error) {
        case AUTHENTICATION:
            Toast.makeText(getActivity(), getString(R.string.toast_auth_error),
                    Toast.LENGTH_LONG).show();
            goLogin();
            return;
		case NETWORK:
			showRepeatTaskScreen(getString(R.string.toast_server_error));
			break;
		default:
			showEmptyScreen(getString(R.string.general_error));
			break;
		}
	}

    @SuppressWarnings({ "unchecked", "deprecation" })
    public void onResultReceived(Object... results) {
        if (getActivity() == null)
            return;
        getActivity().removeDialog(BaseActivity.DIALOG_FETCHING);
        schedule = (ArrayList<Block>) results[0];
        displaySchedule();
        showMainScreen();
    }

    private void displaySchedule() {
        if (fetchingNextWeek || fetchingPreviousWeek || setToNow) {
            updateDay(0, DateUtils.moveDayofWeek(mondayMillis, -3)); // previous
            // friday
            for (int i = 1; i < mDays.size() - 1; ++i)
                updateDay(i, DateUtils.moveDayofWeek(mondayMillis, (i - 1)));
            updateDay(mDays.size() - 1, DateUtils.moveDayofWeek(mondayMillis, 7)); // next
                                                                         // monday
            cleanBlocks();
        }

        Log.e("Schedule", "success");
        for (Block block : schedule)
            addBlock(block.getWeekDay(), block);
        mPager.setAdapter(new DayAdapter());
        if (fetchingPreviousWeek) {
            mPager.setCurrentItem(mDays.size() - 2, false);
        } else if (fetchingNextWeek) {
            mPager.setCurrentItem(1, false);
        } else if (setToNow) {
            updateNowView();
        } else {
            mPager.setCurrentItem(1);
            updateNowView();
        }
        if (fetchingNextWeek || fetchingPreviousWeek || setToNow) {
            fetchingNextWeek = false;
            fetchingPreviousWeek = false;
            setToNow = false;
        }
    }

    @SuppressWarnings("deprecation")
    private void updateSchedule() {
        if (fetchingNextWeek || fetchingPreviousWeek || setToNow) {
            getActivity().showDialog(BaseActivity.DIALOG_FETCHING);
        }
        final File cache = FileUtils.getFile(getActivity(), ScheduleFragment.class.getSimpleName()  + scheduleCode + mondayMillis);
        switch (scheduleType) {
        case SCHEDULE_STUDENT:
            task = ScheduleUtils.getStudentScheduleReply(scheduleCode, mondayMillis,
                    this, cache);
            break;
        case SCHEDULE_EMPLOYEE:
            task = ScheduleUtils.getEmployeeScheduleReply(scheduleCode,
                    mondayMillis, this, cache);
            break;
        case SCHEDULE_ROOM:
            task = ScheduleUtils.getRoomScheduleReply(scheduleCode,
                    mondayMillis, this, cache);
            break;
        case SCHEDULE_UC:
            task = ScheduleUtils.getUcScheduleReply(scheduleCode, mondayMillis,
                    this, cache);
            break;
        }
    }
    
    @SuppressWarnings("deprecation")
	protected void onRepeat(){
        if (fetchingNextWeek || fetchingPreviousWeek || setToNow) {
            getActivity().showDialog(BaseActivity.DIALOG_FETCHING);
        }
        else
        	showLoadingScreen();
        updateSchedule();
    }
    

}