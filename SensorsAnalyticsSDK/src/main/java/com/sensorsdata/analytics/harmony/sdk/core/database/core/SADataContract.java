package com.sensorsdata.analytics.harmony.sdk.core.database.core;

public class SADataContract {

    public enum Type {
        SQLITE,
        PREFERENCES
    }

    public interface MatchCode {
        int PREFERENCE_CODE = 10;
        int SQLITE_CODE = 20;
    }


    public interface SQLite {

        /* 数据库名称 */
        String DATABASE_NAME = "sensorsdata.db";
        /* 数据库版本号 */
        int DATABASE_VERSION = 1;
        String BASE_PATH = "sqlite";
        String GZIP_DATA_EVENT = "1";
        String GZIP_DATA_ENCRYPT = "9";
        int DB_OUT_OF_MEMORY_ERROR = -2;

        interface Events {
            String TABLE_NAME = "events";

            interface Columns {
                String PRIMARY_ID = "_id";
                String KEY_DATA = "data";
                String KEY_CREATED_AT = "created_at";
            }
        }

        /* 删除所有数据 */
        String DB_DELETE_ALL = "DB_DELETE_ALL";

    }

    public interface Preferences {
        String BASE_PATH = "preferences";
        String NAME = "sensors_preferences";
        String COLUMN = "preferences_column";
        String LOGIN_ID = "preferences_login_id";
        String ANONYMOUS_ID = "preferences_anonymous_id";
        String SUPER_PROPERTIES = "preferences_super_properties";
        String FIRST_DAY = "preferences_first_day";
        String EVENT_UPLOAD = "preferences_event_upload";
        String FIRST_TRACK_INSTALLATION = "preferences_first_track_installation";
        String FIRST_TRACK_INSTALLATION_CALLBACK = "preferences_first_track_installation_callback";
    }
}
