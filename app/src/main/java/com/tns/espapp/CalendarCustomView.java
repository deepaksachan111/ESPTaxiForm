package com.tns.espapp;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by TNS on 1/11/2017.
 */

public class CalendarCustomView extends LinearLayout {
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private GridView calendarGridView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter mAdapter;
    private static String  sDate;
  //  private DatabaseQuery mQuery;

    public CalendarCustomView(Context context) {
        super(context);
    }

    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
        setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
    }

    public CalendarCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initializeUILayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        previousButton = (ImageView) view.findViewById(R.id.previous_month);
        nextButton = (ImageView) view.findViewById(R.id.next_month);
        currentDate = (TextView) view.findViewById(R.id.display_current_date);
        addEventButton = (Button) view.findViewById(R.id.add_calendar_event);
        calendarGridView = (GridView) view.findViewById(R.id.calendar_grid);


    }

    private void setPreviousButtonClickEvent() {
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setNextButtonClickEvent() {
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setGridCellClickEvents() {
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent , View view, int position, long id) {
                Toast.makeText(context, "Clicked " + ++position, Toast.LENGTH_LONG).show();
                view.setBackgroundColor(Color.GREEN);
                currentDate.setText(position+" " + sDate);
            }
        });
    }

    private void setUpCalendarAdapter() {
        List<Date> dayValueInCells = new ArrayList<Date>();
     //   mQuery = new DatabaseQuery(context);
    //   List<EventObjects> mEvents = mQuery.getAllFutureEvents();

        List<EventObjects> mEvents = new ArrayList<>();
        mEvents.add(new EventObjects("one", new Date()));
        mEvents.add(new EventObjects("two", new Date()));
        mEvents.add(new EventObjects("three", new Date()));
        mEvents.add(new EventObjects("four", new Date()));
        mEvents.add(new EventObjects("five", new Date()));

        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.d(TAG, "Number of date " + dayValueInCells.size());
         sDate = formatter.format(cal.getTime());
        currentDate.setText(sDate);
        mAdapter = new GridAdapter(context, dayValueInCells, cal, mEvents);
        calendarGridView.setAdapter(mAdapter);
    }

    public class GridAdapter extends ArrayAdapter {
        private final String TAG = GridAdapter.class.getSimpleName();
        private LayoutInflater mInflater;
        private List<Date> monthlyDates;
        private Calendar currentDate;
        private List<EventObjects> allEvents;

        public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<EventObjects> allEvents) {
            super(context, R.layout.single_cell_layout);
            this.monthlyDates = monthlyDates;
            this.currentDate = currentDate;
            this.allEvents = allEvents;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date mDate = monthlyDates.get(position);
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(mDate);
            int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
            int displayMonth = dateCal.get(Calendar.MONTH) + 1;
            int displayYear = dateCal.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH) + 1;
            int currentYear = currentDate.get(Calendar.YEAR);
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(R.layout.single_cell_layout, parent, false);
            }
            if (displayMonth == currentMonth && displayYear == currentYear) {
                view.setBackgroundColor(Color.parseColor("#FF5733"));
            } else {
                view.setBackgroundColor(Color.parseColor("#cccccc"));
            }
            //Add day to calendar
            TextView cellNumber = (TextView) view.findViewById(R.id.calendar_date_id);
            cellNumber.setText(String.valueOf(dayValue));
            //Add events to the calendar
            TextView eventIndicator = (TextView) view.findViewById(R.id.event_id);
            Calendar eventCalendar = Calendar.getInstance();
            for (int i = 0; i < allEvents.size(); i++) {
                eventCalendar.setTime(allEvents.get(i).getDate());
                if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                        && displayYear == eventCalendar.get(Calendar.YEAR)) {
                    eventIndicator.setBackgroundColor(Color.parseColor("#FF4081"));
                }
            }
            return view;
        }

        @Override
        public int getCount() {
            return monthlyDates.size();
        }

        @Nullable
        @Override
        public Object getItem(int position) {
            return monthlyDates.get(position);
        }

        @Override
        public int getPosition(Object item) {
            return monthlyDates.indexOf(item);
        }

    }
}