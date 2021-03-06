package com.jdelorenzo.hitthegym.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jdelorenzo.hitthegym.R;
import com.jdelorenzo.hitthegym.data.WorkoutContract;
import com.jdelorenzo.hitthegym.data.WorkoutContract.*;
import com.jdelorenzo.hitthegym.model.Weight;

import org.joda.time.LocalDate;

import java.util.ArrayList;


/**
 * An {@link IntentService} subclass for handling asynchronous database call requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class DatabaseIntentService extends IntentService {
    private static final String ACTION_ADD_ROUTINE = "com.jdelorenzo.hitthegym.service.action.ADD_WORKOUT";
    private static final String ACTION_DELETE_ROUTINE = "com.jdelorenzo.hitthegym.service.action.DELETE_WORKOUT";
    private static final String ACTION_RENAME_ROUTINE = "com.jdelorenzo.hitthegym.service.action.RENAME_WORKOUT";
    private static final String ACTION_ADD_DAY = "com.jdelorenzo.hitthegym.service.action.ADD_DAY";
    private static final String ACTION_EDIT_DAYS = "com.jdelorenzo.hitthegym.service.action_EDIT_DAYS";
    private static final String ACTION_DELETE_DAY = "com.jdelorenzo.hitthegym.service.action.DELETE_DAY";
    private static final String ACTION_ADD_EXERCISE = "com.jdelorenzo.hitthegym.service.action.ADD_EXERCISE";
    private static final String ACTION_EDIT_EXERCISE = "com.jdelorenzo.hitthegym.service.action.EDIT_EXERCISE";
    private static final String ACTION_DELETE_EXERCISE = "com.jdelorenzo.hitthegym.service.action.DELETE_EXERCISE";
    private static final String ACTION_EDIT_EXERCISE_WEIGHT = "com.jdelorenzo.capstone.service.action_EDIT_EXERCISE_WEIGHT";
    private static final String ACTION_COMPLETE_WORKOUT = "com.jdelorenzo.capstone.service.action_COMPLETE_WORKOUT";
    private static final String ACTION_RECORD_WEIGHTS = "com.jdelorenzo.capstone.service.action_RECORD_WEIGHTS";

    private static final String EXTRA_DAY_ID = "com.jdelorenzo.hitthegym.service.extra.DAY_ID";
    private static final String EXTRA_DAY_OF_WEEK = "com.jdelorenzo.hitthegym.service.extra.DAY_OF_WEEK";
    private static final String EXTRA_WORKOUT_ID = "com.jdelorenzo.hitthegym.service.extra.WORKOUT_ID";
    private static final String EXTRA_NAME = "com.jdelorenzo.hitthegym.service.extra.NAME";
    private static final String EXTRA_REPS = "com.jdelorenzo.hitthegym.service.extra.REPS";
    private static final String EXTRA_SETS = "com.jdelorenzo.hitthegym.service.extra.SETS";
    private static final String EXTRA_WEIGHT = "com.jdelorenzo.hitthegym.service.extra.WEIGHT";
    private static final String EXTRA_DATE = "com.jdelorenzo.hitthegym.service.extra.DATE";
    private static final String EXTRA_WEIGHTS = "com.jdelorenzo.hitthegym.service.extra.WEIGHTS";

    private static final String LOG_TAG = DatabaseIntentService.class.getSimpleName();

    public DatabaseIntentService() {
        super("DatabaseIntentService");
    }

    private static String getCurrentDateString(Context context) {
        LocalDate date = new LocalDate();
        return date.toString(context.getString(R.string.date_format));
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionAddRoutine(Context context, String name) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_ADD_ROUTINE);
        intent.setData(RoutineEntry.CONTENT_URI);
        intent.putExtra(EXTRA_NAME, name);
        context.startService(intent);
    }

    public static void startActionDeleteRoutine(Context context, long id) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_DELETE_ROUTINE);
        intent.setData(RoutineEntry.buildRoutineId(id));
        context.startService(intent);
    }

    public static void startActionRenameRoutine(Context context, long id, String name) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_RENAME_ROUTINE);
        intent.setData(RoutineEntry.buildRoutineId(id));
        intent.putExtra(EXTRA_NAME, name);
        context.startService(intent);
    }

    public static void startActionAddDay(Context context, long workoutId, int dayOfWeek) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_ADD_DAY);
        intent.setData(DayEntry.CONTENT_URI);
        intent.putExtra(EXTRA_WORKOUT_ID, workoutId);
        intent.putExtra(EXTRA_DAY_OF_WEEK, dayOfWeek);
        context.startService(intent);
    }

    public static void startActionDeleteDay(Context context, long dayId) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_DELETE_DAY);
        intent.setData(DayEntry.buildDayId(dayId));
        context.startService(intent);
    }

    public static void startActionAddExercise(Context context, long dayId, String name, int sets,
                                              int reps, double weight) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_ADD_EXERCISE);
        intent.setData(ExerciseEntry.CONTENT_URI);
        intent.putExtra(EXTRA_DAY_ID, dayId);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_SETS, sets);
        intent.putExtra(EXTRA_REPS, reps);
        intent.putExtra(EXTRA_WEIGHT, weight);
        context.startService(intent);
    }

    public static void startActionDeleteExercise(Context context, long id) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_DELETE_EXERCISE);
        intent.setData(ExerciseEntry.buildExerciseId(id));
        context.startService(intent);
    }

    public static void startActionEditExercise(Context context, long id, String name, int sets,
                                              int reps, double weight) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_EDIT_EXERCISE);
        intent.setData(ExerciseEntry.buildExerciseId(id));
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_SETS, sets);
        intent.putExtra(EXTRA_REPS, reps);
        intent.putExtra(EXTRA_WEIGHT, weight);
        context.startService(intent);
    }

    public static void startActionEditExerciseWeight(Context context, long id, double weight) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_EDIT_EXERCISE_WEIGHT);
        intent.setData(ExerciseEntry.buildExerciseId(id));
        intent.putExtra(EXTRA_WEIGHT, weight);
        context.startService(intent);
    }

    public static void startActionEditDays(Context context, ArrayList<Integer> days, long workoutId) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_EDIT_DAYS);
        intent.setData(DayEntry.buildRoutineId(workoutId));
        intent.putExtra(EXTRA_WORKOUT_ID, workoutId);
        intent.putExtra(EXTRA_DAY_OF_WEEK, days);
        context.startService(intent);
    }

    public static void startActionCompleteWorkout(Context context, long dayId) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_COMPLETE_WORKOUT);
        Uri exerciseUri = WorkoutContract.DayEntry.buildDayId(dayId);
        intent.setData(exerciseUri);
        intent.putExtra(EXTRA_DATE, getCurrentDateString(context));
        context.startService(intent);
    }

    public static void startActionRecordWeights(Context context, ArrayList<Weight> weights) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_RECORD_WEIGHTS);
        Uri weightUri = WeightEntry.CONTENT_URI;
        intent.setData(weightUri);
        intent.putExtra(EXTRA_WEIGHTS, weights);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ContentValues contentValues;
        if (intent != null) {
            final String action = intent.getAction();
            switch(action) {

                case ACTION_ADD_ROUTINE:
                    contentValues = new ContentValues();
                    contentValues.put(RoutineEntry.COLUMN_NAME,
                            intent.getStringExtra(EXTRA_NAME));
                    getApplicationContext().getContentResolver().insert(
                            intent.getData(),
                            contentValues);
                    break;

                case ACTION_DELETE_ROUTINE:
                    getContentResolver().delete(
                            intent.getData(),
                            null,
                            null
                    );
                    break;

                case ACTION_RENAME_ROUTINE:
                    contentValues = new ContentValues();
                    contentValues.put(RoutineEntry.COLUMN_NAME,
                            intent.getStringExtra(EXTRA_NAME));
                    getContentResolver().update(
                            intent.getData(),
                            contentValues,
                            null,
                            null
                    );
                    break;

                case ACTION_ADD_DAY:
                    contentValues = new ContentValues();
                    contentValues.put(DayEntry.COLUMN_ROUTINE_KEY,
                            intent.getLongExtra(EXTRA_WORKOUT_ID, 0));
                    contentValues.put(DayEntry.COLUMN_DAY_OF_WEEK,
                            intent.getIntExtra(EXTRA_DAY_OF_WEEK, 0));
                    getContentResolver().insert(
                            intent.getData(),
                            contentValues
                    );
                    break;

                case ACTION_DELETE_DAY:
                    getContentResolver().delete(
                            intent.getData(),
                            null,
                            null
                    );
                    break;

                case ACTION_EDIT_DAYS:
                    ArrayList<Integer> days = intent.getIntegerArrayListExtra(EXTRA_DAY_OF_WEEK);
                    long workoutKey = intent.getLongExtra(EXTRA_WORKOUT_ID, 0);
                    int deleted = getContentResolver().delete(intent.getData(), null, null);
                    ContentValues[] values = new ContentValues[days.size()];
                    for (int i = 0; i < days.size(); i++) {
                        ContentValues v = new ContentValues();
                        v.put(DayEntry.COLUMN_ROUTINE_KEY, workoutKey);
                        v.put(DayEntry.COLUMN_DAY_OF_WEEK, days.get(i));
                        values[i] = v;
                    }
                    int inserted = getContentResolver().bulkInsert(intent.getData(), values);
                    Log.d(LOG_TAG, "Inserted " + inserted + " days and removed " + deleted + " days");
                    break;

                case ACTION_ADD_EXERCISE:
                    contentValues = new ContentValues();
                    contentValues.put(ExerciseEntry.COLUMN_DAY_KEY,
                            intent.getLongExtra(EXTRA_DAY_ID, 0));
                    contentValues.put(ExerciseEntry.COLUMN_DESCRIPTION,
                            intent.getStringExtra(EXTRA_NAME));
                    contentValues.put(ExerciseEntry.COLUMN_REPS,
                            intent.getIntExtra(EXTRA_REPS, 0));
                    contentValues.put(ExerciseEntry.COLUMN_SETS,
                            intent.getIntExtra(EXTRA_SETS, 0));
                    contentValues.put(ExerciseEntry.COLUMN_WEIGHT,
                            intent.getDoubleExtra(EXTRA_WEIGHT, 0));
                    getContentResolver().insert(
                            intent.getData(),
                            contentValues
                    );
                    break;

                case ACTION_EDIT_EXERCISE:
                    contentValues = new ContentValues();
                    contentValues.put(ExerciseEntry.COLUMN_DESCRIPTION,
                            intent.getStringExtra(EXTRA_NAME));
                    contentValues.put(ExerciseEntry.COLUMN_REPS,
                            intent.getIntExtra(EXTRA_REPS, 0));
                    contentValues.put(ExerciseEntry.COLUMN_SETS,
                            intent.getIntExtra(EXTRA_SETS, 0));
                    contentValues.put(ExerciseEntry.COLUMN_WEIGHT,
                            intent.getDoubleExtra(EXTRA_WEIGHT, 0));
                    getContentResolver().update(
                            intent.getData(),
                            contentValues,
                            null,
                            null
                    );
                    break;

                case ACTION_EDIT_EXERCISE_WEIGHT:
                    contentValues = new ContentValues();
                    contentValues.put(ExerciseEntry.COLUMN_WEIGHT,
                            intent.getDoubleExtra(EXTRA_WEIGHT, 0));
                    getContentResolver().update(
                            intent.getData(),
                            contentValues,
                            null,
                            null
                    );
                    break;

                case ACTION_DELETE_EXERCISE:
                    getContentResolver().delete(
                            intent.getData(),
                            null,
                            null
                    );
                    break;

                case ACTION_COMPLETE_WORKOUT:
                    //update day with date
                    contentValues = new ContentValues();
                    contentValues.put(DayEntry.COLUMN_LAST_DATE,
                            intent.getStringExtra(EXTRA_DATE));
                    getContentResolver().update(
                            intent.getData(),
                            contentValues,
                            null,
                            null
                    );
                    break;

                case ACTION_RECORD_WEIGHTS:
                    ArrayList<Weight> weights = intent.getParcelableArrayListExtra(EXTRA_WEIGHTS);
                    ContentValues[] contentValueList = new ContentValues[weights.size()];
                    String date = getCurrentDateString(getApplicationContext());
                    for (int i = 0; i < weights.size(); i++) {
                        Weight w = weights.get(i);
                        ContentValues value = new ContentValues();
                        value.put(WeightEntry.COLUMN_EXERCISE_KEY, w.getExerciseId());
                        value.put(WeightEntry.COLUMN_WEIGHT, w.getWeight());
                        value.put(WeightEntry.COLUMN_DATE, date);
                        contentValueList[i] = value;
                    }
                    getContentResolver().bulkInsert(
                            intent.getData(),
                            contentValueList
                    );
                    break;

                default:
                    Log.e(LOG_TAG, "Service called with unknown action " + action);
            }
        }
    }


}
