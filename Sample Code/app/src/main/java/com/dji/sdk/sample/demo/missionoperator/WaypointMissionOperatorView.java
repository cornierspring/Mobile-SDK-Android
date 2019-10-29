package com.dji.sdk.sample.demo.missionoperator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.dji.sdk.sample.R;
import com.dji.sdk.sample.demo.missionmanager.MissionBaseView;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.ToastUtils;

import dji.common.camera.SettingsDefinitions;
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
import dji.sdk.mission.error.ShootPhotoActionError;
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

    //route gps coordinates - hardcoded, as I broke the reader class right before the flight
    private static final double[][] ROUTE1 = {{174.855486,-36.906615,20,25,0.0},
            {174.855524,-36.906624,20,25,2.181985241},
            {174.855563,-36.906645,20,25,4.449278067},
            {174.855606,-36.906638,20,25,6.900460118},
            {174.855651,-36.90663,20,25,9.665037675},
            {174.855696,-36.906629,20,25,12.9322915},
            {174.855741,-36.906626,20,25,17.00501025},
            {174.855787,-36.90662,20,25,22.40870691},
            {174.855828,-36.906606,20,25,30.12526724},
            {174.855872,-36.906593,20,25,42.06647678},
            {174.855913,-36.906583,20,25,61.51753058},
            {174.855957,-36.906573,20,25,90.0},
            {174.856,-36.906565,20,25,118.4824694},
            {174.856041,-36.906558,20,25,137.9335232},
            {174.856082,-36.906542,20,25,149.8747328},
            {174.856122,-36.906526,20,25,157.5912931},
            {174.856159,-36.906507,20,25,162.9949898},
            {174.856202,-36.906489,20,25,167.0677085},
            {174.856237,-36.906471,20,25,170.3349623},
            {174.856266,-36.906442,20,25,173.0995399},
            {174.856295,-36.90642,20,25,175.5507219},
            {174.856311,-36.906397,20,25,177.8180148},
            {174.856301,-36.906381,20,25,180.0},
            {174.856283,-36.906359,20,25,182.1819852},
            {174.85626,-36.90635,20,25,184.4492781},
            {174.856223,-36.90634,20,25,186.9004601},
            {174.856177,-36.906337,20,25,189.6650377},
            {174.856135,-36.906338,20,25,192.9322915},
            {174.856091,-36.906344,20,25,197.0050102},
            {174.856044,-36.906353,20,25,202.4087069},
            {174.855996,-36.906356,20,25,210.1252672},
            {174.855954,-36.90636,20,25,222.0664768},
            {174.855896,-36.906378,20,25,241.5175306},
            {174.855872,-36.90639,20,25,270.0},
            {174.855836,-36.906404,20,25,298.4824694},
            {174.855794,-36.906422,20,25,317.9335232},
            {174.855755,-36.906441,20,25,329.8747328},
            {174.855716,-36.906459,20,25,337.5912931},
            {174.85568,-36.906479,20,25,342.9949898},
            {174.855642,-36.906497,20,25,347.0677085},
            {174.855601,-36.906513,20,25,350.3349623},
            {174.855566,-36.906537,20,25,353.0995399},
            {174.855532,-36.90656,20,25,355.5507219},
            {174.855525,-36.906591,20,25,360.0}};
    private static final double[][] ROUTE2 = {{174.855527,-36.90663,25,35,0.0},
            {174.855546,-36.906652,25,35,1.998119687},
            {174.855574,-36.906678,25,35,4.061557956},
            {174.85562,-36.906686,25,35,6.264084858},
            {174.855663,-36.906686,25,35,8.698674182},
            {174.855708,-36.90668,25,35,11.49391221},
            {174.855752,-36.906674,25,35,14.84222706},
            {174.855797,-36.906668,25,35,19.05259477},
            {174.85584,-36.906665,25,35,24.65481361},
            {174.855883,-36.906656,25,35,32.60975485},
            {174.855929,-36.906647,25,35,44.68294435},
            {174.855972,-36.906637,25,35,63.5817274},
            {174.856012,-36.906622,25,35,90.0},
            {174.856056,-36.906608,25,35,116.4182726},
            {174.856098,-36.906591,25,35,135.3170557},
            {174.856136,-36.906575,25,35,147.3902451},
            {174.856174,-36.906559,25,35,155.3451864},
            {174.856212,-36.906537,25,35,160.9474052},
            {174.856247,-36.906515,25,35,165.1577729},
            {174.856282,-36.906493,25,35,168.5060878},
            {174.856306,-36.90646,25,35,171.3013258},
            {174.856326,-36.906429,25,35,173.7359151},
            {174.856346,-36.906377,25,35,175.938442},
            {174.856333,-36.906344,25,35,178.0018803},
            {174.856312,-36.906318,25,35,180.0},
            {174.856272,-36.906301,25,35,181.9981197},
            {174.856235,-36.906291,25,35,184.061558},
            {174.856204,-36.906287,25,35,186.2640849},
            {174.856163,-36.906282,25,35,188.6986742},
            {174.856116,-36.906276,25,35,191.4939122},
            {174.856068,-36.906274,25,35,194.8422271},
            {174.856028,-36.906277,25,35,199.0525948},
            {174.85598,-36.906278,25,35,204.6548136},
            {174.855937,-36.906284,25,35,212.6097549},
            {174.855894,-36.906295,25,35,224.6829443},
            {174.85585,-36.906307,25,35,243.5817274},
            {174.855809,-36.906318,25,35,270.0},
            {174.855772,-36.906339,25,35,296.4182726},
            {174.855731,-36.906358,25,35,315.3170557},
            {174.855691,-36.906378,25,35,327.3902451},
            {174.855658,-36.906402,25,35,335.3451864},
            {174.855626,-36.906425,25,35,340.9474052},
            {174.855597,-36.906453,25,35,345.1577729},
            {174.855568,-36.906484,25,35,348.5060878},
            {174.855543,-36.906512,25,35,351.3013258},
            {174.855525,-36.906538,25,35,353.7359151},
            {174.855513,-36.90657,25,35,355.938442},
            {174.855506,-36.906593,25,35,358.0018803},
            {174.855502,-36.906615,25,35,360.0}};
    private static final double[][] ROUTE3 = {{174.855499,-36.906631,30,45,0.0},
            {174.855516,-36.906665,30,45,2.3},
            {174.855547,-36.906696,30,45,2.3},
            {174.855578,-36.906722,30,45,2.3},
            {174.855659,-36.906748,30,45,2.3},
            {174.85575,-36.906738,30,45,15.25252053},
            {174.855839,-36.906729,30,45,23.72323921},
            {174.855885,-36.906722,30,45,30.12526724},
            {174.85593,-36.906715,30,45,39.20020778},
            {174.855972,-36.906706,30,45,52.59748066},
            {174.856013,-36.906695,30,45,72.04179084},
            {174.856181,-36.906635,30,45,145.7589574},
            {174.856218,-36.906618,30,45,153.3327854},
            {174.856289,-36.906572,30,45,162.9949898},
            {174.85632,-36.906545,30,45,166.3302315},
            {174.856364,-36.906487,30,45,171.4887141},
            {174.856373,-36.906452,30,45,173.6100742},
            {174.856379,-36.906379,30,45,177.3742521},
            {174.856363,-36.986348,30,45,179.1318493},
            {174.856344,-36.906315,30,45,180.8681507},
            {174.856308,-36.906293,30,45,182.6257479},
            {174.826269,-36.906273,30,45,184.4492781},
            {174.85623,-36.906256,30,45,186.3899258},
            {174.856189,-36.906242,30,45,188.5112859},
            {174.856099,-36.906222,30,45,193.6697685},
            {174.856057,-36.906217,30,45,197.0050102},
            {174.855923,-36.906209,30,45,214.2410426},
            {174.85588,-36.906208,30,45,225.2329453},
            {174.855796,-36.906229,30,45,263.847125},
            {174.855722,-36.90627,30,45,307.4025193},
            {174.855685,-36.906291,30,45,320.7997922},
            {174.85565,-36.906319,30,45,329.8747328},
            {174.855624,-36.906344,30,45,336.2767608},
            {174.855598,-36.906371,30,45,341.0343009},
            {174.855575,-36.906404,30,45,344.7474795},
            {174.855537,-36.906469,30,45,350.3349623},
            {174.855504,-36.90652,30,45,354.5986383},
            {174.855491,-36.906553,30,45,356.473715}};
    private static final double[][] TEST = {{174.784546, -36.954981, 20, 10, 30}, {174.784400, -36.954800, 20, 10, 30}};


    //constructors
    private static final double ONE_METER_OFFSET = 0.00000899322;
    private static final String TAG = WaypointMissionOperatorView.class.getSimpleName();
    private WaypointMissionOperator waypointMissionOperator;
    private WaypointMission mission;
    private WaypointMissionOperatorListener listener;
    public WaypointMissionOperatorView(Context context) {
        super(context);
    }

    // actrions when each button is touched
    @Override
    public void onClick(View v) {


        if (waypointMissionOperator == null) {
            waypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        }


        DJIError djiError;
        switch (v.getId()) {
            //route 1 button actions
            case R.id.btn_route1:

                // load route 1
                mission = createWaypointMission(1);
                 djiError = waypointMissionOperator.loadMission(mission);
                showResultToast(djiError);


        break;
            //route 2 button actions
        case R.id.btn_route2:
        // load route 2
        mission = createWaypointMission(2);
         djiError = waypointMissionOperator.loadMission(mission);
        showResultToast(djiError);


        break;
        // route 3 button actions
        case R.id.btn_route3:
        // load route 3
        mission = createWaypointMission(3);
         djiError = waypointMissionOperator.loadMission(mission);
        showResultToast(djiError);

        // route 4 button actions
        break;
        case R.id.btn_load:
        // load test mission
        mission = createWaypointMission(4);
         djiError = waypointMissionOperator.loadMission(mission);
        showResultToast(djiError);

        break;

        //upload mission to drone
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
        //start mission
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

                        // change these if gps cant find location
                        homeLatitude =  36.906624;
                        homeLongitude = 174.855524;
                        flightState = flightControllerState.getFlightMode();

                        updateWaypointMissionState();
                    }
                });

            }
        }
        waypointMissionOperator = MissionControl.getInstance().getWaypointMissionOperator();
        setUpListener();
    }

    // when window is closed
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

    //UI related
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


    // Mission creator
    private WaypointMission createWaypointMission(int route) {
        WaypointMission.Builder builder = new WaypointMission.Builder();

        // choosing routes
        double[][] points;
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
        double baseLongitude = 174.855524; //
        double baseLatitude = -36.906624; //
        Object latitudeValue = KeyManager.getInstance().getValue((FlightControllerKey.create(HOME_LOCATION_LATITUDE)));
        Object longitudeValue =
                KeyManager.getInstance().getValue((FlightControllerKey.create(HOME_LOCATION_LONGITUDE)));
        if (latitudeValue != null && latitudeValue instanceof Double) {
            baseLatitude = (double) latitudeValue;
        }
        if (longitudeValue != null && longitudeValue instanceof Double) {
            baseLongitude = (double) longitudeValue;
        }

        // settings can be found on the documentation but butter just to experiment as these are difficult to understand
        final float baseAltitude = 20.0f;
        builder.autoFlightSpeed(5f);
        builder.maxFlightSpeed(10f);
        builder.setExitMissionOnRCSignalLostEnabled(false);
        builder.finishedAction(WaypointMissionFinishedAction.NO_ACTION);
        builder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        builder.gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY);
        builder.headingMode(WaypointMissionHeadingMode.AUTO);

        List<Waypoint> waypointList = new ArrayList<>();

        // loop through coordinates to create waypoint actions
        for (int i = 0; i < points.length; i++) {

            final Waypoint eachWaypoint = new Waypoint(points[i][1], points[i][0], (float) points[i][2]); // convert to double double and float

            eachWaypoint.addAction(new WaypointAction(WaypointActionType.GIMBAL_PITCH, ((int) points[i][3]) * -1 ));
            eachWaypoint.addAction(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,-82 )); // (int) points[i][4]
            eachWaypoint.addAction((new WaypointAction(WaypointActionType.START_TAKE_PHOTO,0)));

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
