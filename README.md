# Gimbal SDK Sample App for Android

## Basic Setup

1. Create an Android app on Gimbal Manager. Set the Package Name to `com.gimbal.android.sample`.

2. After creation, copy the Gimbal API Key and paste it into the value of
   `GimbalIntegration.GIMBAL_APP_API_KEY`.
   
3. Run it! After granting Location permissions, the Sample App should provide Place events and
   Communicates based on user location and proximity to beacons. Additional setup is required in
   Gimbal Manager to set up Places and Place-based Communications. See the section below for details
   on setting up push messaging for Time-based Communications.
   
## Using Firebase Messaging for Time-based Communications

The Sample App is written to remove as much of the setup work as possible. That said, there are few
steps to initialize Firebase and to to let Gimbal know that it should turn on messaging.

1. Sign in to the Firebase Console: https://console.firebase.google.com

2. If you don't already have a Firebase project that you've been using for Gimbal SDK communications,
   add one.
   
3. Do you already have an Android app corresponding to this Sample App in your Firebase project?
   - **NO**: Add one. The following numbered items should match the configuration steps in the
     Console.
     1. Call it `com.gimbal.android.sample` so it matches the sample app package. Register it.
     2. Download `google-services.json` to the `app` subdirectory of the Sample App project.
     3. You may skip app verification.
   - **YES**: From the Project Overview page:
     1. Click on the Android app badge/button near the top of the main content panel
     2. Click on the Settings gear in the small panel that bubbles up for the app.
     3. Scroll down to the "Your apps" section and download `google-services.json` to the `app`
        subdirectory of this Sample App project.

4. Set up an Application in Gimbal Manager for the Sample App. (You may skip this if you have
   already done this.)
   1. Create an Android Application. Set the Package Name to `com.gimbal.android.sample`.
   2. After Application creation, enter the Cloud Messaging Key in the Push Configuration tab.
      Get this value from the Cloud Messaging tab of your Firebase app's settings. Then click
      Save Push Configuration.
        
5. Uncomment the final line of `app/build.gradle`:

       apply plugin: 'com.google.gms.google-services'
       
6. Change the value of `GimbalIntegration.ENABLE_PUSH_MESSAGING` to `true`.

7. Run it! Opt in to the location permissions if prompted.

8. Send a push Communication to your app.  
   a. Create a new Communication at https://manager.gimbal.com/communicate/new  
   b. Change the Triggering Event to `Time Triggered: Now (Instant)`  
   c. Enter a name for the Communication, and a Title and Description text that will be seen on your
      test device.  
   d. Click "Save & Publish"
