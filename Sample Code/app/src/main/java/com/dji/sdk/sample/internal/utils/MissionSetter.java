package com.dji.sdk.sample.internal.utils;

import com.dji.sdk.sample.internal.model.CompletionCallbackImplementation;

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

public class MissionSetter {

    private String[][] routeCoordinates;

    public void setMission(String[][] coordinates) {

        Reader reader = new Reader();
        routeCoordinates = reader.readInput();

        MissionControl mc = DJISDKManager.getInstance().getMissionControl();
        WaypointMissionOperator wmo = mc.getWaypointMissionOperator();

        WaypointMission.Builder builder =  new WaypointMission.Builder();

        for (int i = 0; i < reader.size(); i++) {
            Waypoint wp = new Waypoint(Double.parseDouble(routeCoordinates[i][0]),
                    Double.parseDouble(routeCoordinates[i][1]),
                    Float.parseFloat(routeCoordinates[i][2]));

            WaypointAction turn = new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,
                    Integer.parseInt(routeCoordinates[i][3]));// may need action param
            wp.addAction(turn);

            WaypointAction photo = new WaypointAction(WaypointActionType.START_TAKE_PHOTO,1);
            wp.addAction(photo);

            builder.addWaypoint(wp);

        }

        WaypointMission wp = builder.build();
        wmo.loadMission(wp);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {

        }

        CommonCallbacks.CompletionCallback ccb = new CompletionCallbackImplementation();

        wmo.startMission(ccb);



    }

    public void executeMission() {



        
    }

}
