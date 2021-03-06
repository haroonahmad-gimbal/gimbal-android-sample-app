/**
 * Copyright (C) 2017 Gimbal, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of Gimbal, Inc.
 *
 * The following sample code illustrates various aspects of the Gimbal SDK.
 *
 * The sample code herein is provided for your convenience, and has not been
 * tested or designed to work on any particular system configuration. It is
 * provided AS IS and your use of this sample code, whether as provided or
 * with any modification, is at your own risk. Neither Gimbal, Inc.
 * nor any affiliate takes any liability nor responsibility with respect
 * to the sample code, and disclaims all warranties, express and
 * implied, including without limitation warranties on merchantability,
 * fitness for a specified purpose, and against infringement.
 */
package com.gimbal.android.sample;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.DeviceAttributesManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Visit;
import com.gimbal.android.sample.GimbalEvent.TYPE;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class GimbalIntegration {
    private static final String GIMBAL_APP_API_KEY = "a8c68d2f-af77-4261-9858-a6b20183521a";
    private static final Boolean ENABLE_PUSH_MESSAGING = null;

    private static final int MAX_NUM_EVENTS = 100;


    private Application app;
    private Context     appContext;

    private LinkedList<GimbalEvent> events;
    private PlaceEventListener placeEventListener;
    private CommunicationListener communicationListener;

    private static GimbalIntegration instance;

    public static GimbalIntegration init(Application app) {
        if (instance == null) {
            instance = new GimbalIntegration(app);
        }
        return instance;
    }

    public static GimbalIntegration instance() {
        if (instance == null) {
            throw new IllegalStateException("Gimbal integration not initialized from Application");
        }
        return instance;
    }

    private GimbalIntegration(Application app) {
        this.app = app;
        this.appContext = app.getApplicationContext();
    }

    public void onCreate() {
        Gimbal.setApiKey(app, GIMBAL_APP_API_KEY);
        setDeviceAttributes();

        if (ENABLE_PUSH_MESSAGING != null) {
            // Only needs to be enabled once per app instance.  Additional calls will have no effect.
            CommunicationManager.getInstance().enablePushMessaging(ENABLE_PUSH_MESSAGING);
        }

        events = new LinkedList<>(GimbalDAO.getEvents(app));

        // Setup PlaceEventListener
        placeEventListener = new PlaceEventListener() {

            @Override
            public void onVisitStart(Visit visit) {
                addEvent(new GimbalEvent(TYPE.PLACE_ENTER, visit.getPlace().getName(),
                                         new Date(visit.getArrivalTimeInMillis())));
                //Option 1 - Set & get your membership id in the device attributes
                String membershipID = DeviceAttributesManager.getInstance().getDeviceAttributes().get("YOUR_KEY");
                Log.d("Gimbal",membershipID);
                //Option 2 - Get Gimbal's appInstanceID which is unique for every install
                Log.d("Gimbal",Gimbal.getApplicationInstanceIdentifier());
            }

            @Override
            public void onVisitStartWithDelay(Visit visit, int delayTimeInSeconds) {
                if (delayTimeInSeconds > 0) {
                    addEvent(new GimbalEvent(TYPE.PLACE_ENTER_DELAY, visit.getPlace().getName(),
                                             new Date(System.currentTimeMillis())));
                }

            }

            @Override
            public void onVisitEnd(Visit visit) {
                addEvent(new GimbalEvent(TYPE.PLACE_EXIT, visit.getPlace().getName(),
                                         new Date(visit.getDepartureTimeInMillis())));
            }
        };
        PlaceManager.getInstance().addListener(placeEventListener);

        // Setup CommunicationListener
        communicationListener = new CommunicationListener() {
            @Override
            public Notification.Builder prepareCommunicationForDisplay(Communication communication,
                                                                       Visit visit, int notificationId) {
                addEvent(new GimbalEvent(TYPE.COMMUNICATION_PRESENTED,
                        communication.getTitle() + ":  CONTENT_DELIVERED", new Date()));

                // If you want a custom notification create and return it here
                return null;
            }

            @Override
            public Notification.Builder prepareCommunicationForDisplay(Communication communication,
                                                                       Push push, int notificationId) {
                addEvent(new GimbalEvent(TYPE.COMMUNICATION_INSTANT_PUSH,
                        communication.getTitle() + ":  CONTENT_DELIVERED", new Date()));
                // communication.getAttributes()

                // If you want a custom notification create and return it here
                return null;
            }

            @Override
            public void onNotificationClicked(List<Communication> communications) {
                for (Communication communication : communications) {
                    if(communication != null) {
                        addEvent(new GimbalEvent(TYPE.NOTIFICATION_CLICKED,
                                communication.getTitle() + ": CONTENT_CLICKED", new Date()));
                        Intent intent  = new Intent(appContext, AppActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        appContext.startActivity(intent);
                    }
                }
            }
        };
        CommunicationManager.getInstance().addListener(communicationListener);
    }

    private void addEvent(GimbalEvent event) {
        while (events.size() >= MAX_NUM_EVENTS) {
            events.removeLast();
        }
        events.add(0, event);
        GimbalDAO.setEvents(appContext, events);
    }

    public void setDeviceAttributes(){
        Map<String,String> attributes = new HashMap<>();
        attributes.put("YOUR_KEY","MEMBERSHIP_ID");
        DeviceAttributesManager.getInstance().setDeviceAttributes(attributes);
    }

    public void onTerminate() {
        PlaceManager.getInstance().removeListener(placeEventListener);
        CommunicationManager.getInstance().removeListener(communicationListener);
    }
}
