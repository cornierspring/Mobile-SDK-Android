package com.dji.sdk.sample.internal.utils;

import com.dji.sdk.sample.internal.model.CompletionCallbackImplementation;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.mission.waypoint.WaypointMissionState;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.actions.ShootPhotoAction;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LATITUDE;
import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LONGITUDE;

public class MissionSetter {

    private String[][] routeCoordinates;

    public WaypointMission setMission(String[][] coordinates, int numCoordinates) {

        MissionControl mc = DJISDKManager.getInstance().getMissionControl();
        WaypointMissionOperator wmo = mc.getWaypointMissionOperator();

        WaypointMission.Builder builder =  new WaypointMission.Builder();

        double baseLatitude = 22;
        double baseLongitude = 113;
        Object latitudeValue = KeyManager.getInstance().getValue((FlightControllerKey.create(HOME_LOCATION_LATITUDE)));
        Object longitudeValue = KeyManager.getInstance().getValue((FlightControllerKey.create(HOME_LOCATION_LONGITUDE)));
        builder.autoFlightSpeed(5f);
        builder.maxFlightSpeed(10f);
        builder.setExitMissionOnRCSignalLostEnabled(false);
        builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
        builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
        builder.headingMode(WaypointMissionHeadingMode.AUTO);
        builder.repeatTimes(0);

        if (latitudeValue != null && latitudeValue instanceof Double) {
            baseLatitude = (double) latitudeValue;
        }
        if (longitudeValue != null && longitudeValue instanceof Double) {
            baseLongitude = (double) longitudeValue;
        }

        List<Waypoint> listOfWaypoints = new ArrayList<>();


        for (int i = 0; i < numCoordinates; i++) {
            final Waypoint wp = new Waypoint(Double.parseDouble(routeCoordinates[i][0]), // c
                    Double.parseDouble(routeCoordinates[i][1]),
                    Float.parseFloat(routeCoordinates[i][2]));

            WaypointAction turn = new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,
                    Integer.parseInt(routeCoordinates[i][3]));// may need action param
            wp.addAction(turn);

//            WaypointAction gimbal = new WaypointAction(WaypointActionType.GIMBAL_PITCH, routeCoordinates[i][4]);
//            wp.addAction(gimbal);

            WaypointAction photo = new WaypointAction(WaypointActionType.START_TAKE_PHOTO,1);
            wp.addAction(photo);

            listOfWaypoints.add(wp);

        }

        builder.waypointList(listOfWaypoints);
        builder.waypointCount(listOfWaypoints.size());

        WaypointMission wp = builder.build();
        return wp;
//        wmo.loadMission(wp);
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//
//        }
//
//        CommonCallbacks.CompletionCallback ccb = new CompletionCallbackImplementation();
//
//        wmo.startMission(ccb);
//
//
    }

    public void executeMission() {

    }

}
