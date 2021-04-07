package com.dsdairysytem.clientappfinal.utils;

import com.google.firebase.firestore.BuildConfig;

public class AppConstants {

    public static final String BASE_URL = "https://fcm.googleapis.com";

    /**
     * Add Server Key from Firebase console
     * <p>
     * Project setting -> Cloud messaging -> Server Key
     */
    public static final String SERVER_KEY = "AAAAK3rM-uA:APA91bFE2Oo1u41IsHKwBG4XA7HgR567g-evhqaO168VXp3FJ4bqD00KTiIdz_UmunMVGID2WbIndcp71M0LZkKvaXtLY9MYI1KczdkHaOgijLM200QZaoLmssIyDHFyqFU4r-xuzAnd";

    public static final String PROJECT_ID = BuildConfig.APPLICATION_ID;

}
