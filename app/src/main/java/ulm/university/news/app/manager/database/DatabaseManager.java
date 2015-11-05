package ulm.university.news.app.manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The DatabaseManager class provides basic functionality to open and retrieve a connection to the SQLite database.
 * If database and tables are not existing yet, they will be created. This class implements the Singleton pattern to
 * ensure proper database access for concurrent threads.
 *
 * @author Matthias Mak
 */
public class DatabaseManager extends SQLiteOpenHelper {
    /** This classes tag for logging. */
    private static final String LOG_TAG = "DatabaseManager";
    /** The single instance of DatabaseManager. */
    private static DatabaseManager _instance;
    /** The name of the database. */
    private static final String DATABASE_NAME = "ulm_university_news.db";
    /** The version of the database. */
    private static final int DATABASE_VERSION = 1;

    /** SQL statement to enable foreign key support. */
    private static final String FOREIGN_KEYS_ON = "PRAGMA foreign_keys=ON;";

    // Columns of the LocalUser table.
    public static final String LOCAL_USER_TABLE = "LocalUser";
    public static final String LOCAL_USER_ID = "_id";
    public static final String LOCAL_USER_NAME = "Name";
    public static final String LOCAL_USER_SERVER_ACCESS_TOKEN = "ServerAccessToken";
    public static final String LOCAL_USER_PUSH_ACCESS_TOKEN = "PushAccessToken";
    public static final String LOCAL_USER_PLATFORM = "Platform";

    /** SQL statement to create the LocalUser table. */
    private static final String CREATE_TABLE_LOCAL_USER = "CREATE TABLE " + LOCAL_USER_TABLE + "("
            + LOCAL_USER_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + LOCAL_USER_NAME + " TEXT NOT NULL, "
            + LOCAL_USER_SERVER_ACCESS_TOKEN + " TEXT NOT NULL, "
            + LOCAL_USER_PUSH_ACCESS_TOKEN + " TEXT NOT NULL, "
            + LOCAL_USER_PLATFORM + " INTEGER NOT NULL);";

    // Columns of the User table.
    public static final String USER_TABLE = "User";
    public static final String USER_ID = "_id";
    public static final String USER_ID_FOREIGN = "User_Id";
    public static final String USER_NAME = "Name";
    public static final String USER_OLD_NAME = "OldName";

    /** SQL statement to create the User table. */
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + USER_TABLE + "("
            + USER_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + USER_NAME + " TEXT NOT NULL, "
            + USER_OLD_NAME + " TEXT);";

    // Columns of the Channel table.
    public static final String CHANNEL_TABLE = "Channel";
    public static final String CHANNEL_ID = "_id";
    public static final String CHANNEL_ID_FOREIGN = "Channel_Id";
    public static final String CHANNEL_NAME = "Name";
    public static final String CHANNEL_DESCRIPTION = "Description";
    public static final String CHANNEL_TYPE = "Type";
    public static final String CHANNEL_TERM = "Term";
    public static final String CHANNEL_LOCATIONS = "Locations";
    public static final String CHANNEL_CONTACTS = "Contacts";
    public static final String CHANNEL_CREATION_DATE = "CreationDate";
    public static final String CHANNEL_MODIFICATION_DATE = "ModificationDate";
    public static final String CHANNEL_DATES = "Dates";
    public static final String CHANNEL_WEBSITE = "Website";

    /** SQL statement to create the Channel table. */
    private static final String CREATE_TABLE_CHANNEL = "CREATE TABLE " + CHANNEL_TABLE + "("
            + CHANNEL_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + CHANNEL_NAME + " TEXT NOT NULL, "
            + CHANNEL_DESCRIPTION + " TEXT, "
            + CHANNEL_TYPE + " INTEGER NOT NULL, "
            + CHANNEL_TERM + " TEXT, "
            + CHANNEL_LOCATIONS + " TEXT, "
            + CHANNEL_CONTACTS + " TEXT NOT NULL, "
            + CHANNEL_CREATION_DATE + " TEXT NOT NULL, "
            + CHANNEL_MODIFICATION_DATE + " TEXT NOT NULL, "
            + CHANNEL_DATES + " TEXT, "
            + CHANNEL_WEBSITE + " TEXT);";

    // Column of the SubscribedChannels table.
    public static final String SUBSCRIBED_CHANNELS_TABLE = "SubscribedChannels";

    /** SQL statement to create the SubscribedChannels table. */
    private static final String CREATE_TABLE_SUBSCRIBED_CHANNELS = "CREATE TABLE " + SUBSCRIBED_CHANNELS_TABLE + "("
            + CHANNEL_ID_FOREIGN + " INTEGER PRIMARY KEY NOT NULL, "
            + "FOREIGN KEY(" + CHANNEL_ID_FOREIGN + ") REFERENCES " + CHANNEL_TABLE + "(" + CHANNEL_ID + "));";

    // Columns of the Lecture table.
    public static final String LECTURE_TABLE = "Lecture";
    public static final String LECTURE_FACULTY = "Faculty";
    public static final String LECTURE_START_DATE = "StartDate";
    public static final String LECTURE_END_DATE = "EndDate";
    public static final String LECTURE_LECTURER = "Lecturer";
    public static final String LECTURE_ASSISTANT = "Assistant";

    /** SQL statement to create the Lecture table. */
    private static final String CREATE_TABLE_LECTURE = "CREATE TABLE " + LECTURE_TABLE + "("
            + CHANNEL_ID_FOREIGN + " INTEGER PRIMARY KEY NOT NULL, "
            + LECTURE_FACULTY + " INTEGER NOT NULL, "
            + LECTURE_START_DATE + " TEXT, "
            + LECTURE_END_DATE + " TEXT, "
            + LECTURE_LECTURER + " TEXT NOT NULL, "
            + LECTURE_ASSISTANT + " TEXT, "
            + "FOREIGN KEY(" + CHANNEL_ID_FOREIGN + ") REFERENCES " + CHANNEL_TABLE + "(" + CHANNEL_ID + "));";

    // Columns of the Event table.
    public static final String EVENT_TABLE = "Event";
    public static final String EVENT_COST = "Cost";
    public static final String EVENT_ORGANIZER = "Organizer";

    /** SQL statement to create the Event table. */
    private static final String CREATE_TABLE_EVENT = "CREATE TABLE " + EVENT_TABLE + "("
            + CHANNEL_ID_FOREIGN + " INTEGER PRIMARY KEY NOT NULL, "
            + EVENT_COST + " TEXT, "
            + EVENT_ORGANIZER + " TEXT, "
            + "FOREIGN KEY(" + CHANNEL_ID_FOREIGN + ") REFERENCES " + CHANNEL_TABLE + "(" + CHANNEL_ID + "));";

    // Columns of the Sports table.
    public static final String SPORTS_TABLE = "Sports";
    public static final String SPORTS_COST = "Cost";
    public static final String SPORTS_PARTICIPANTS = "NumberOfParticipants";

    /** SQL statement to create the Sports table. */
    private static final String CREATE_TABLE_SPORTS = "CREATE TABLE " + SPORTS_TABLE + "("
            + CHANNEL_ID_FOREIGN + " INTEGER PRIMARY KEY NOT NULL, "
            + SPORTS_COST + " TEXT, "
            + SPORTS_PARTICIPANTS + " TEXT, "
            + "FOREIGN KEY(" + CHANNEL_ID_FOREIGN + ") REFERENCES " + CHANNEL_TABLE + "(" + CHANNEL_ID + "));";

    // Columns of the Group table.
    public static final String GROUP_TABLE = "\"Group\"";
    public static final String GROUP_ID = "_id";
    public static final String GROUP_ID_FOREIGN = "Group_Id";
    public static final String GROUP_NAME = "Name";
    public static final String GROUP_DESCRIPTION = "Description";
    public static final String GROUP_TYPE = "Type";
    public static final String GROUP_CREATION_DATE = "CreationDate";
    public static final String GROUP_MODIFICATION_DATE = "ModificationDate";
    public static final String GROUP_TERM = "Term";
    public static final String GROUP_DELETED = "Deleted";
    public static final String GROUP_ADMIN = "GroupAdmin_User_Id";

    /** SQL statement to create the Group table. */
    private static final String CREATE_TABLE_GROUP = "CREATE TABLE " + GROUP_TABLE + "("
            + GROUP_ID + " INTEGER PRIMARY KEY NOT NULL, "
            + GROUP_NAME + " TEXT NOT NULL, "
            + GROUP_DESCRIPTION + " TEXT, "
            + GROUP_TYPE + " INTEGER NOT NULL, "
            + GROUP_CREATION_DATE + " TEXT NOT NULL, "
            + GROUP_MODIFICATION_DATE + " TEXT NOT NULL, "
            + GROUP_TERM + " TEXT, "
            + GROUP_DELETED + " INTEGER NOT NULL, "
            + GROUP_ADMIN + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + GROUP_ADMIN + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "));";

    // Column of the UserGroup table.
    public static final String USER_GROUP_TABLE = "UserGroup";
    public static final String USER_GROUP_ACTIVE = "Active";

    /** SQL statement to create the UserGroup table. */
    private static final String CREATE_TABLE_USER_GROUP = "CREATE TABLE " + USER_GROUP_TABLE + "("
            + USER_ID_FOREIGN + " INTEGER NOT NULL, "
            + GROUP_ID_FOREIGN + " INTEGER NOT NULL, "
            + USER_GROUP_ACTIVE + " INTEGER NOT NULL, "
            + "PRIMARY KEY(" + USER_ID_FOREIGN + ", " + GROUP_ID_FOREIGN + "), "
            + "FOREIGN KEY(" + USER_ID_FOREIGN + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "), "
            + "FOREIGN KEY(" + GROUP_ID_FOREIGN + ") REFERENCES " + GROUP_TABLE + "(" + GROUP_ID + "));";

    /**
     * Get the instance of the DatabaseManager class.
     *
     * @param context The context from which this method is called.
     * @return Instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new DatabaseManager(context);
        }
        return _instance;
    }

    /**
     * Creates the instance of DatabaseManager.
     *
     * @param context The context from which this method is called.
     */
    private DatabaseManager(Context context) {
        // Use the application context, which will ensure that one don't accidentally leak an Activity's context.
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(LOG_TAG, "Creating database " + DATABASE_NAME);
        // Enable foreign key support.
        Log.i(LOG_TAG, FOREIGN_KEYS_ON);
        database.execSQL(FOREIGN_KEYS_ON);
        // Create tables.
        Log.i(LOG_TAG, CREATE_TABLE_LOCAL_USER);
        database.execSQL(CREATE_TABLE_LOCAL_USER);
        Log.i(LOG_TAG, CREATE_TABLE_USER);
        database.execSQL(CREATE_TABLE_USER);
        Log.i(LOG_TAG, CREATE_TABLE_CHANNEL);
        database.execSQL(CREATE_TABLE_CHANNEL);
        Log.i(LOG_TAG, CREATE_TABLE_LECTURE);
        database.execSQL(CREATE_TABLE_LECTURE);
        Log.i(LOG_TAG, CREATE_TABLE_EVENT);
        database.execSQL(CREATE_TABLE_EVENT);
        Log.i(LOG_TAG, CREATE_TABLE_SPORTS);
        database.execSQL(CREATE_TABLE_SPORTS);
        Log.i(LOG_TAG, CREATE_TABLE_GROUP);
        database.execSQL(CREATE_TABLE_GROUP);
        Log.i(LOG_TAG, CREATE_TABLE_SUBSCRIBED_CHANNELS);
        database.execSQL(CREATE_TABLE_SUBSCRIBED_CHANNELS);
        Log.i(LOG_TAG, CREATE_TABLE_USER_GROUP);
        database.execSQL(CREATE_TABLE_USER_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseManager.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + LOCAL_USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CHANNEL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LECTURE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SPORTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBSCRIBED_CHANNELS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USER_GROUP_TABLE);
        onCreate(db);
    }
}
