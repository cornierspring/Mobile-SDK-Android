package com.dji.sdk.sample.demo.missionoperator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.dji.sdk.sample.R;
import com.dji.sdk.sample.demo.missionmanager.MissionBaseView;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.MissionSetter;
import com.dji.sdk.sample.internal.utils.Reader;
import com.dji.sdk.sample.internal.utils.ToastUtils;
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
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LATITUDE;
import static dji.keysdk.FlightControllerKey.HOME_LOCATION_LONGITUDE;

/**
 * Class for waypoint mission.
 */
public class WaypointMissionOperatorView extends MissionBaseView {

    private static final double[][] ROUTE1;
    private static final double[][] ROUTE2;
    private static final double[][] ROUTE3;
    private static final double[][] TEST;


    private static final double ONE_METER_OFFSET = 0.00000899322;
    private static final String TAG = WaypointMissionOperatorView.class.getSimpleName();
    private WaypointMissionOperator waypointMissionOperator;
    private WaypointMission mission;
    private WaypointMissionOperatorListener listener;
    private Reader rdr = new Reader();

    //private final int WAYPOINT_COUNT = 5;

    public WaypointMissionOperatorView(Context context) {
        super(context);
    }

    //region Mission Action Demo
    @Override
    public void onClick(View v) {
        if (waypointMissionOperator == null) {
            waypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        }
        switch (v.getId()) {
            case R.id.btn_route1:

                mission = createWaypointMission(1);
                DJIError djiError = waypointMissionOperator.loadMission(mission);

//                showResultToast(djiError);
//               if (getFlightController() != null) {
//                   flightController.getSimulator()
//                                   .start(InitializationData.createInstance(new LocationCoordinate2D(22, 113), 10, 10),
//                                          new CommonCallbacks.CompletionCallback() {
//                                              @Override
//                                              public void onResult(DJIError djiError) {
//                                                  showResultToast(djiError);
//                                              }
//                                          });
               }
                break;
            case R.id.btn_set_route2:

                mission = createWaypointMission(2);
                DJIError djiError = waypointMissionOperator.loadMission(mission);

//                showResultToast(djiError);
//                if (getFlightController() != null) {
//                    flightController.setMaxFlightHeight(500, new CommonCallbacks.CompletionCallback() {
//                        @Override
//                        public void onResult(DJIError djiError) {
//                            ToastUtils.setResultToToast(djiError == null ? "Max Flight Height is set to 500m!" : djiError.getDescription());
//                        }
//                    });
//                }
                break;

            case R.id.btn_route3:

                mission = createWaypointMission(3);
                DJIError djiError = waypointMissionOperator.loadMission(mission);

  //              showResultToast(djiError);
//                if (getFlightController() != null) {
//                    flightController.setMaxFlightRadius(500, new CommonCallbacks.CompletionCallback() {
//                        @Override
//                        public void onResult(DJIError djiError) {
//                            ToastUtils.setResultToToast(djiError == null ? "Max Flight Radius is set to 500m!" : djiError.getDescription());
//                        }
//                    });
//                }
                break;
            case R.id.btn_load:
                // Example of loading a Mission
                mission = createWaypointMission(4);
                DJIError djiError = waypointMissionOperator.loadMission(mission);

                break;

            case R.id.btn_upload:
                // Example of uploading a Mission
                if (WaypointMissionState.READY_TO_RETRY_UPLOAD.equals(waypointMissionOperator.getCurrentState())
                    || WaypointMissionState.READY_TO_UPLOAD.equals(waypointMissionOperator.getCurrentState())) {
                    waypointMissionOperator.uploadMission(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            showResultToast(djiError);
                        }
                    });
                } else {
                    ToastUtils.setResultToToast("Not ready!");
                }
                break;
            case R.id.btn_start:
                // Example of starting a Mission
                if (mission != null) {
                    waypointMissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            showResultToast(djiError);
                        }
                    });
                } else {
                    ToastUtils.setResultToToast("Prepare Mission First!");
                }
                break;
            case R.id.btn_stop:
                // Example of stopping a Mission
                waypointMissionOperator.stopMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        showResultToast(djiError);
                    }
                });
                break;
            case R.id.btn_pause:
                // Example of pausing an executing Mission
                waypointMissionOperator.pauseMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        showResultToast(djiError);
                    }
                });
                break;
            case R.id.btn_resume:
                // Example of resuming a paused Mission
                waypointMissionOperator.resumeMission(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        showResultToast(djiError);
                    }
                });
                break;
            case R.id.btn_download:
                // Example of downloading an executing Mission
                if (WaypointMissionState.EXECUTING.equals(waypointMissionOperator.getCurrentState()) ||
                    WaypointMissionState.EXECUTION_PAUSED.equals(waypointMissionOperator.getCurrentState())) {
                    waypointMissionOperator.downloadMission(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            showResultToast(djiError);
                        }
                    });
                } else {
                    ToastUtils.setResultToToast("Mission can be downloaded when the mission state is EXECUTING or EXECUTION_PAUSED!");
                }
                break;
            default:
                break;
        }
    }
    //endregion

    //region View Life-Cycle
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BaseProduct product = DJISampleApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            ToastUtils.setResultToToast("Disconnect");
            return;
        } else {
            if (product instanceof Aircraft) {
                flightController = ((Aircraft) product).getFlightController();
            }

            if (flightController != null) {

                flightController.setStateCallback(new FlightControllerState.Callback() {
                    @Override
                    public void onUpdate(@NonNull FlightControllerState flightControllerState) {
//                        homeLatitude = flightControllerState.getHomeLocation().getLatitude();
//                        homeLongitude = flightControllerState.getHomeLocation().getLongitude();
                        homeLatitude =  -36.955024;
                        homeLongitude = 174.784514;
                        flightState = flightControllerState.getFlightMode();

                        updateWaypointMissionState();
                    }
                });

            }
        }
        waypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        setUpListener();
    }

    @Override
    protected void onDetachedFromWindow() {
        tearDownListener();
        if (flightController != null) {
            flightController.getSimulator().stop(null);
            flightController.setStateCallback(null);
        }
        super.onDetachedFromWindow();
    }
    //endregion

    //region Internal Helper Methods
    private FlightController getFlightController(){
        if (flightController == null) {
            BaseProduct product = DJISampleApplication.getProductInstance();
            if (product != null && product instanceof Aircraft) {
                flightController = ((Aircraft) product).getFlightController();
            } else {
                ToastUtils.setResultToToast("Product is disconnected!");
            }
        }

        return flightController;
    }
    private void updateWaypointMissionState(){
        if (waypointMissionOperator != null && waypointMissionOperator.getCurrentState() != null) {
            ToastUtils.setResultToText(FCPushInfoTV,
                                       "home point latitude: "
                                           + homeLatitude
                                           + "\nhome point longitude: "
                                           + homeLongitude
                                           + "\nFlight state: "
                                           + flightState.name()
                                           + "\nCurrent Waypointmission state : "
                                           + waypointMissionOperator.getCurrentState().getName());
        } else {
            ToastUtils.setResultToText(FCPushInfoTV,
                                       "home point latitude: "
                                           + homeLatitude
                                           + "\nhome point longitude: "
                                           + homeLongitude
                                           + "\nFlight state: "
                                           + flightState.name());
        }
    }
    //endregion

    //region Example of Creating a Waypoint Mission

    /**
     */
    //endregion

    private WaypointMission createWaypointMission(int route) {
        WaypointMission.Builder builder = new WaypointMission.Builder();

        String[][] points;
        if (route == 1) {
            points = ROUTE1;
        } else if (route == 2) {
            points = ROUTE2;
        } else if (route == 3) {
            points = ROUTE3;
        } else {
            points = TEST;
        }
        // find bridge coordinates
        // find house coordinates
        double baseLatitude = 22; //
        double baseLongitude = 113; //
        Object latitudeValue = KeyManager.getInstance().getValue((FlightControllerKey.create(HOME_LOCATION_LATITUDE)));
        Object longitudeValue =
                KeyManager.getInstance().getValue((FlightControllerKey.create(HOME_LOCATION_LONGITUDE)));
        if (latitudeValue != null && latitudeValue instanceof Double) {
            baseLatitude = (double) latitudeValue;
        }
        if (longitudeValue != null && longitudeValue instanceof Double) {
            baseLongitude = (double) longitudeValue;
        }

        final float baseAltitude = 20.0f;
        builder.autoFlightSpeed(5f);
        builder.maxFlightSpeed(10f);
        builder.setExitMissionOnRCSignalLostEnabled(false);
        builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
        builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
        builder.headingMode(WaypointMissionHeadingMode.AUTO);

        List<Waypoint> waypointList = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {

            final Waypoint eachWaypoint = new Waypoint(points[i][0], points[i][1], points[i][1]); // convert to double double and float

            eachWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, points[i][3]));
            eachWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,points[i][4]));

            waypointList.add(eachWaypoint);
        }
        builder.waypointList(waypointList).waypointCount(waypointList.size());
        return builder.build();
    }

    //region Not important stuff
    private void setUpListener() {
        // Example of Listener
        listener = new WaypointMissionOperatorListener() {
            @Override
            public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
                // Example of Download Listener
                rdr.readInput();
                if (waypointMissionDownloadEvent.getProgress() != null
                    && waypointMissionDownloadEvent.getProgress().isSummaryDownloaded
//                    && waypointMissionDownloadEvent.getProgress().downloadedWaypointIndex == (rdr.size() - 1)) {
                   && waypointMissionDownloadEvent.getProgress().downloadedWaypointIndex == (5 - 1)) {
                    ToastUtils.setResultToToast("Download successful!");
                }
                updateWaypointMissionState();
            }

            @Override
            public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
                // Example of Upload Listener
                rdr.readInput();
                if (waypointMissionUploadEvent.getProgress() != null
                    && waypointMissionUploadEvent.getProgress().isSummaryUploaded
//                        && waypointMissionUploadEvent.getProgress().uploadedWaypointIndex == (rdr.size() - 1)) {
                    && waypointMissionUploadEvent.getProgress().uploadedWaypointIndex == (4)) {
                    ToastUtils.setResultToToast("Upload successful!");
                }
                updateWaypointMissionState();
            }

            @Override
            public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
                // Example of Execution Listener
                Log.d(TAG,
                      (waypointMissionExecutionEvent.getPreviousState() == null
                       ? ""
                       : waypointMissionExecutionEvent.getPreviousState().getName())
                          + ", "
                          + waypointMissionExecutionEvent.getCurrentState().getName()
                          + (waypointMissionExecutionEvent.getProgress() == null
                             ? ""
                             : waypointMissionExecutionEvent.getProgress().targetWaypointIndex));
                updateWaypointMissionState();
            }

            @Override
            public void onExecutionStart() {
                ToastUtils.setResultToToast("Execution started!");
                updateWaypointMissionState();
            }

            @Override
            public void onExecutionFinish(@Nullable DJIError djiError) {
                ToastUtils.setResultToToast("Execution finished!");
                updateWaypointMissionState();
            }
        };

        if (waypointMissionOperator != null && listener != null) {
            // Example of adding listeners
            waypointMissionOperator.addListener(listener);
        }
    }

    private void tearDownListener() {
        if (waypointMissionOperator != null && listener != null) {
            // Example of removing listeners
            waypointMissionOperator.removeListener(listener);
        }
    }

    private void showResultToast(DJIError djiError) {
        ToastUtils.setResultToToast(djiError == null ? "Action started!" : djiError.getDescription());
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_waypoint_mission_operator;
    }

    //endregion
}
