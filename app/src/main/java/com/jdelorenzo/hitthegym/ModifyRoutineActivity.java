package com.jdelorenzo.hitthegym;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jdelorenzo.hitthegym.data.WorkoutContract;
import com.jdelorenzo.hitthegym.dialogs.CreateExerciseDialogFragment;
import com.jdelorenzo.hitthegym.dialogs.SelectDaysDialogFragment;
import com.jdelorenzo.hitthegym.service.DatabaseIntentService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class ModifyRoutineActivity extends AppCompatActivity implements
        SelectDaysDialogFragment.SelectDaysListener, EditDayFragment.SelectDayListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    private String mRoutineName;
    private long mRoutineId;
    private boolean [] mCheckedDays;
    private boolean mTwoPane;

    private static final String EXTRA_CHECKED_DAYS = "checkedDays";
    private static final String EXTRA_WORKOUT_ID = "workoutId";
    private static final String EXTRA_TWO_PANE = "twoPane";

    public static final String ARG_WORKOUT_NAME = "workout";
    public static final String ARG_WORKOUT_ID = "workoutId";
    private static final String FTAG_EDIT_DAY = "editDayFragment";
    private static final String FTAG_EDIT_WORKOUT = "editWorkoutFragment";
    private static final String FTAG_SELECT_DAYS = "selectDaysDialogFragment";
    private static final String FTAG_ADD_EXERCISE = "addExerciseDialogFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_workout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if (b!= null) {
            mRoutineName = b.getString(ARG_WORKOUT_NAME);
            mRoutineId = b.getLong(ARG_WORKOUT_ID);
        }
        mTwoPane = findViewById(R.id.fragment_detail_container) != null;
        if (savedInstanceState == null) {
            EditDayFragment selectDayFragment = EditDayFragment.newInstance(mRoutineId, null);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_master_container, selectDayFragment, FTAG_EDIT_DAY)
                    .commit();
        }
        //if we moved from single pane to two pane, the fragments have to be re-arranged.
        else if ((savedInstanceState.getBoolean(EXTRA_TWO_PANE) != mTwoPane) && mTwoPane) {
            FragmentManager fm = getFragmentManager();
            if (fm.findFragmentByTag(FTAG_EDIT_WORKOUT) != null) {
                EditWorkoutFragment editWorkoutFragment = (EditWorkoutFragment)
                        getFragmentManager().findFragmentByTag(FTAG_EDIT_WORKOUT);
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().remove(editWorkoutFragment).commit();
                fm.executePendingTransactions();
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_detail_container, editWorkoutFragment,
                                FTAG_EDIT_WORKOUT)
                        .commit();
                EditDayFragment selectDayFragment = EditDayFragment.newInstance(mRoutineId, null);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_master_container, selectDayFragment, FTAG_EDIT_DAY)
                        .commit();
            }
        }
    }

    @Override
    public void onDaySelected(long dayId) {
        EditWorkoutFragment editWorkoutFragment = EditWorkoutFragment.newInstance(mRoutineId, dayId);
        int layoutId = mTwoPane ? R.id.fragment_detail_container : R.id.fragment_master_container;
        getFragmentManager()
                .beginTransaction()
                .replace(layoutId, editWorkoutFragment, FTAG_EDIT_WORKOUT)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDaysSelected(ArrayList<Integer> indices) {
        DatabaseIntentService.startActionEditDays(this, indices, mRoutineId);
    }

    //In single pane mode, there is one FAB button for this activity, and it handles callbacks
    //for its child fragments
    @Optional @OnClick(R.id.fab)
    public void onFabClick() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag(FTAG_EDIT_WORKOUT) != null) {
            onExerciseFab();
        }
        else if (fragmentManager.findFragmentByTag(FTAG_EDIT_DAY) != null) {
            onDayFab();
        }
    }

    //in multi pane mode, this is one of the FAB menu options
    @Optional @OnClick(R.id.fab_day)
    public void onDayFab() {
        EditDayFragment fragment = (EditDayFragment) getFragmentManager().findFragmentByTag(FTAG_EDIT_DAY);
        SelectDaysDialogFragment dialogFragment = SelectDaysDialogFragment.newInstance(fragment.getChecked());
        dialogFragment.show(getFragmentManager(), FTAG_SELECT_DAYS);
    }

    //in multi pane mode, this is one of the FAB menu options
    @Optional @OnClick(R.id.fab_exercise)
    public void onExerciseFab() {
        EditWorkoutFragment fragment = (EditWorkoutFragment) getFragmentManager()
                .findFragmentByTag(FTAG_EDIT_WORKOUT);
        if (fragment == null) {
            Toast.makeText(this, getString(R.string.toast_select_day), Toast.LENGTH_SHORT).show();
            return;
        }
        final long dayId = fragment.getDayId();
        CreateExerciseDialogFragment dialogFragment = CreateExerciseDialogFragment
                .newInstance(new CreateExerciseDialogFragment.CreateExerciseDialogFragmentListener() {
            @Override
            public void onCreateExercise(int sets, int reps, String description, double weight) {
                //always store weight in metric
                weight = Utility.convertWeightToMetric(getApplicationContext(), weight);
                DatabaseIntentService.startActionAddExercise(getApplicationContext(), dayId, description, sets, reps, weight);
                getContentResolver().notifyChange(WorkoutContract.ExerciseEntry.buildDayId(dayId), null);
            }
        });
        dialogFragment.show(getFragmentManager(), FTAG_ADD_EXERCISE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(EXTRA_CHECKED_DAYS, mCheckedDays);
        outState.putLong(EXTRA_WORKOUT_ID, mRoutineId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRoutineId = savedInstanceState.getLong(EXTRA_WORKOUT_ID);
        mCheckedDays = savedInstanceState.getBooleanArray(EXTRA_CHECKED_DAYS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modify_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_delete_routine) {
            DatabaseIntentService.startActionDeleteRoutine(this, mRoutineId);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
