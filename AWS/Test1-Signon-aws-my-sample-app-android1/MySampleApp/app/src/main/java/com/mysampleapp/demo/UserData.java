package com.mysampleapp.demo;

import android.util.Log;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;

/**
 * Created by prayyaga on 2/24/2016.
 */
public class UserData {
    private static final String LOG_TAG = UserData.class.getSimpleName();

    // dataset name to store user settings
    private static final String USER_DATA_DATASET_NAME = "user_data";
    // key names in dataset
    private static final String USER_DATA_KEY_FIRSTNAME = "firstname";
    private static final String USER_DATA_KEY_LASTNAME = "lastname";
    private static final String USER_DATA_KEY_EMAIL = "email";

    private static UserData instance;

    private String firstName;
    private String lastName;
    private String emailId;

    /**
     * Loads user settings from local dataset into memory.
     */
    public void loadFromDataset() {
        Dataset dataset = getDataset();
        final String firstName = dataset.get(USER_DATA_KEY_FIRSTNAME);
        final String lastName = dataset.get(USER_DATA_KEY_LASTNAME);
        final String emailId = dataset.get(USER_DATA_KEY_EMAIL);
    }

    public Dataset getDataset() {
        return AWSMobileClient.defaultMobileClient()
                .getSyncManager()
                .openOrCreateDataset(USER_DATA_DATASET_NAME);
    }

    public void saveToDataset() {
        Dataset dataset = getDataset();
        dataset.put(USER_DATA_KEY_FIRSTNAME, firstName);
        dataset.put(USER_DATA_KEY_LASTNAME, lastName);
        dataset.put(USER_DATA_KEY_EMAIL, emailId);
    }

    public static UserData getInstance() {
        if (instance != null) {
            return instance;
        }
        instance = new UserData();
        final IdentityManager identityManager = AWSMobileClient.defaultMobileClient()
                .getIdentityManager();
        identityManager.addSignInStateChangeListener(
                new IdentityManager.SignInStateChangeListener() {
                    @Override
                    public void onUserSignedIn() {
                        Log.d(LOG_TAG, "load from dataset on user sign in");
                        instance.loadFromDataset();
                    }

                    @Override
                    public void onUserSignedOut() {
                        Log.d(LOG_TAG, "wipe user data after sign out");
                        AWSMobileClient.defaultMobileClient().getSyncManager().wipeData();
                        instance.saveToDataset();
                    }
                });
        return instance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }




}
