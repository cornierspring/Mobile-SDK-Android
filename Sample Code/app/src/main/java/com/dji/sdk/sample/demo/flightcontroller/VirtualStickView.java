package com.dji.sdk.sample.demo.flightcontroller;

import android.app.Service;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.DialogUtils;
import com.dji.sdk.sample.internal.utils.ModuleVerificationUtil;
import com.dji.sdk.sample.internal.utils.OnScreenJoystick;
import com.dji.sdk.sample.internal.OnScreenJoystickListener;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.view.PresentableView;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.simulator.InitializationData;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.Simulator;

//TODO: Refactor needed

/**
 * Class for virtual stick.
 */
public class VirtualStickView extends RelativeLayout
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, PresentableView {

    private static final double[][] ROUTE1 = {{0.35517395,0.070466323,5,8.145868517,0.582580552},
        {0.70891774,0.281581551,5,8.193464199,1.169763851},
        {1.059806968,0.632495596,5,8.273794242,1.766297274},
        {1.406428728,1.121795451,5,8.388411997,2.37722708},
        {1.747387296,1.747510879,5,8.539591117,3.008072987},
        {2.081309752,2.507122344,5,8.730434268,3.665036174},
        {2.406851508,3.39757116,5,8.965028254,4.355257899},
        {2.722701722,4.415271803,5,9.248659718,5.087152475},
        {3.027588578,5.556126351,5,9.588112338,5.870848787},
        {3.320284403,6.815540986,5,9.992076199,6.718790955},
        {3.599610614,8.188444491,5,10.47171458,7.646575257},
        {3.864442464,9.669308666,5,11.0414551,8.674143784},
        {4.113713568,11.2521706,5,11.72010478,9.827528068},
        {4.3464202,12.93065665,5,12.53243686,11.14146112},
        {4.561625331,14.69800817,5,13.51146528,12.66339805},
        {4.758462408,16.54710863,5,14.70170816,14.45988911},
        {4.936138835,18.47051238,5,16.163806,16.62700567},
        {5.093939174,20.46047454,5,17.98073853,19.30796699},
        {5.231228017,22.50898224,5,20.26495968,22.72389562},
        {5.347452552,24.60778686,5,23.16192879,27.22869258},
        {5.442144783,26.74843726,5,26.83200155,33.40600849},
        {5.514923417,28.92231378,5,31.35169363,42.22076035},
        {5.565495401,31.120663,5,36.39086753,55.1222095},
        {5.593657099,33.33463295,5,40.58688021,73.42045814},
        {5.599295115,35.55530873,5,41.62461867,95.66377455},
        {5.582386745,37.77374849,5,38.73364092,116.4216334},
        {5.543000075,39.98101934,5,33.85932339,131.9433189},
        {5.481293699,42.16823338,5,28.99053293,142.596139},
        {5.397516088,44.32658348,5,24.89138151,149.9410108},
        {5.292004585,46.44737872,5,21.62665303,155.1893884},
        {5.165184047,48.5220794,5,19.05609152,159.0940686},
        {5.017565136,50.54233144,5,17.02166039,162.1082495},
        {4.849742261,52.5,5,15.39419597,164.5104506},
        {4.662391186,54.38720224,5,14.07683772,166.4778274},
        {4.456266307,56.19633905,5,12.99864786,168.127186},
        {4.232197616,57.92012569,5,12.10772284,169.5381817},
        {3.99108736,59.55162107,5,11.36578045,170.7668194},
        {3.733906403,61.08425574,5,10.74426512,171.8535724},
        {3.461690323,62.51185832,5,10.22162204,172.8284245},
        {3.175535238,63.82868035,5,9.781398598,173.7140999},
        {2.876593393,65.02941946,5,9.410915673,174.5281897},
        {2.566068522,66.1092407,5,9.100329566,175.2845907},
        {2.245210998,67.06379601,5,8.841963135,175.9945018},
        {1.915312803,67.88924173,5,8.629824518,176.6671309},
        {1.577702318,68.58225408,5,8.459258462,177.3102083},
        {1.233738984,69.14004254,5,8.326693009,177.930369},
        {0.884807817,69.5603611,5,8.229456219,178.5334447},
        {0.532313843,69.84151729,5,8.165645696,179.1246947},
        {0.177676428,69.98237898,5,8.134039277,179.7089957},
        {-0.177676428,69.98237898,5,8.134039277,180.2910043},
        {-0.532313843,69.84151729,5,8.165645696,180.8753053},
        {-0.884807817,69.5603611,5,8.229456219,181.4665553},
        {-1.233738984,69.14004254,5,8.326693009,182.069631},
        {-1.577702318,68.58225408,5,8.459258462,182.6897917},
        {-1.915312803,67.88924173,5,8.629824518,183.3328691},
        {-2.245210998,67.06379601,5,8.841963135,184.0054982},
        {-2.566068522,66.1092407,5,9.100329566,184.7154093},
        {-2.876593393,65.02941946,5,9.410915673,185.4718103},
        {-3.175535238,63.82868035,5,9.781398598,186.2859001},
        {-3.461690323,62.51185832,5,10.22162204,187.1715755},
        {-3.733906403,61.08425574,5,10.74426512,188.1464276},
        {-3.99108736,59.55162107,5,11.36578045,189.2331806},
        {-4.232197616,57.92012569,5,12.10772284,190.4618183},
        {-4.456266307,56.19633905,5,12.99864786,191.872814},
        {-4.662391186,54.38720224,5,14.07683772,193.5221726},
        {-4.849742261,52.5,5,15.39419597,195.4895494},
        {-5.017565136,50.54233144,5,17.02166039,197.8917505},
        {-5.165184047,48.5220794,5,19.05609152,200.9059314},
        {-5.292004585,46.44737872,5,21.62665303,204.8106116},
        {-5.397516088,44.32658348,5,24.89138151,210.0589892},
        {-5.481293699,42.16823338,5,28.99053293,217.403861},
        {-5.543000075,39.98101934,5,33.85932339,228.0566811},
        {-5.582386745,37.77374849,5,38.73364092,243.5783666},
        {-5.599295115,35.55530873,5,41.62461867,264.3362255},
        {-5.593657099,33.33463295,5,40.58688021,286.5795419},
        {-5.565495401,31.120663,5,36.39086753,304.8777905},
        {-5.514923417,28.92231378,5,31.35169363,317.7792396},
        {-5.442144783,26.74843726,5,26.83200155,326.5939915},
        {-5.347452552,24.60778686,5,23.16192879,332.7713074},
        {-5.231228017,22.50898224,5,20.26495968,337.2761044},
        {-5.093939174,20.46047454,5,17.98073853,340.692033},
        {-4.936138835,18.47051238,5,16.163806,343.3729943},
        {-4.758462408,16.54710863,5,14.70170816,345.5401109},
        {-4.561625331,14.69800817,5,13.51146528,347.3366019},
        {-4.3464202,12.93065665,5,12.53243686,348.8585389},
        {-4.113713568,11.2521706,5,11.72010478,350.1724719},
        {-3.864442464,9.669308666,5,11.0414551,351.3258562},
        {-3.599610614,8.188444491,5,10.47171458,352.3534247},
        {-3.320284403,6.815540986,5,9.992076199,353.281209},
        {-3.027588578,5.556126351,5,9.588112338,354.1291512},
        {-2.722701722,4.415271803,5,9.248659718,354.9128475},
        {-2.406851508,3.39757116,5,8.965028254,355.6447421},
        {-2.081309752,2.507122344,5,8.730434268,356.3349638},
        {-1.747387296,1.747510879,5,8.539591117,356.991927},
        {-1.406428728,1.121795451,5,8.388411997,357.6227729},
        {-1.059806968,0.632495596,5,8.273794242,358.2337027},
        {-0.70891774,0.281581551,5,8.193464199,358.8302361},
        {-0.35517395,0.070466323,5,8.145868517,359.4174194}};
    private static final double[][] ROUTE2 = {{ 0.545445709,-2.923493706,8,11.91076083,0.824016443},
        {1.088695101,-2.694282888,8,11.97745373,1.6543702},
        {1.627560701,-2.313290496,8,12.08990535,2.49758959},
        {2.15987269,-1.782050653,8,12.2501145,3.360596903},
        {2.683487633,-1.102702474,8,12.46099028,4.250936563},
        {3.196297119,-0.277981455,8,12.72647158,5.177044394},
        {3.696236244,0.688791545,8,13.05169381,6.148578521},
        {4.18129193,1.793723671,8,13.44321419,7.176839629},
        {4.64951103,3.032365752,8,13.90931101,8.275319449},
        {5.09900819,4.399730214,8,14.4603768,9.460433413},
        {5.527973443,5.890311161,8,15.10943123,10.75251974},
        {5.934679499,7.498106552,8,15.87278358,12.17722833},
        {6.317488694,9.216642361,8,16.77087522,13.76748738},
        {6.674859593,11.03899865,8,17.82931886,15.56633828},
        {7.005353188,12.95783744,8,19.08010248,17.63109221},
        {7.307638698,14.96543223,8,20.56278726,20.03951713},
        {7.580498926,17.05369916,8,22.32518015,22.89914564},
        {7.82283516,19.21422951,8,24.42212906,26.36128821},
        {8.033671598,21.43832358,8,26.90921086,30.64167008},
        {8.212159276,23.71702574,8,29.8242134,36.04849079},
        {8.357579488,26.04116045,8,33.14285033,43.01137698},
        {8.469346676,28.40136925,8,36.69067745,52.07715852},
        {8.547010794,30.7881484,8,40.01655629,63.76654407},
        {8.590259117,33.1918872,8,42.34350177,78.11365912},
        {8.598917498,35.60290663,8,42.86351098,94.01068684},
        {8.572951073,38.01149836,8,41.36154005,109.3553082},
        {8.5124644,40.40796385,8,38.4233435,122.4277722},
        {8.417701037,42.78265339,8,34.90833108,132.7551769},
        {8.289042564,45.12600492,8,31.43906823,140.6965997},
        {8.127007041,47.42858261,8,28.31250766,146.8194664},
        {7.932246929,49.68111478,8,25.61377522,151.6175246},
        {7.705546459,51.87453128,8,23.32824616,155.4567755},
        {7.447818473,54,8,21.40571999,158.5952951},
        {7.16010075,56.04896243,8,19.78966269,161.2135407},
        {6.843551829,58.01316811,8,18.42838558,163.4388131},
        {6.499446339,59.88470789,8,17.278218,165.3623482},
        {6.129169874,61.65604573,8,16.3035209,167.0507482},
        {5.734213404,63.32004909,8,15.47565064,168.5535631},
        {5.316167281,64.8700176,8,14.77172213,169.9083539},
        {4.876714829,66.29971009,8,14.17347758,171.144118},
        {4.417625568,67.6033697,8,13.66634592,172.2836451},
        {3.940748087,68.77574705,8,13.23869377,173.3451662},
        {3.448002604,69.81212138,8,12.88124168,174.3435289},
        {2.941373233,70.70831959,8,12.58661465,175.2910511},
        {2.422899989,71.460733,8,12.34899872,176.1981526},
        {1.894670582,72.0663319,8,12.16388095,177.0738343},
        {1.358812005,72.52267777,8,12.027855,177.9260492},
        {0.817481972,72.82793306,8,11.93847924,178.7620001},
        {0.272860228,72.98086861,8,11.89417776,179.5883857},
        {-0.272860228,72.98086861,8,11.89417776,180.4116143},
        {-0.817481972,72.82793306,8,11.93847924,181.2379999},
        {-1.358812005,72.52267777,8,12.027855,182.0739508},
        {-1.894670582,72.0663319,8,12.16388095,182.9261657},
        {-2.422899989,71.460733,8,12.34899872,183.8018474},
        {-2.941373233,70.70831959,8,12.58661465,184.7089489},
        {-3.448002604,69.81212138,8,12.88124168,185.6564711},
        {-3.940748087,68.77574705,8,13.23869377,186.6548338},
        {-4.417625568,67.6033697,8,13.66634592,187.7163549},
        {-4.876714829,66.29971009,8,14.17347758,188.855882},
        {-5.316167281,64.8700176,8,14.77172213,190.0916461},
        {-5.734213404,63.32004909,8,15.47565064,191.4464369},
        {-6.129169874,61.65604573,8,16.3035209,192.9492518},
        {-6.499446339,59.88470789,8,17.278218,194.6376518},
        {-6.843551829,58.01316811,8,18.42838558,196.5611869},
        {-7.16010075,56.04896243,8,19.78966269,198.7864593},
        {-7.447818473,54,8,21.40571999,201.4047049},
        {-7.705546459,51.87453128,8,23.32824616,204.5432245},
        {-7.932246929,49.68111478,8,25.61377522,208.3824754},
        {-8.127007041,47.42858261,8,28.31250766,213.1805336},
        {-8.289042564,45.12600492,8,31.43906823,219.3034003},
        {-8.417701037,42.78265339,8,34.90833108,227.2448231},
        {-8.5124644,40.40796385,8,38.4233435,237.5722278},
        {-8.572951073,38.01149836,8,41.36154005,250.6446918},
        {-8.598917498,35.60290663,8,42.86351098,265.9893132},
        {-8.590259117,33.1918872,8,42.34350177,281.8863409},
        {-8.547010794,30.7881484,8,40.01655629,296.2334559},
        {-8.469346676,28.40136925,8,36.69067745,307.9228415},
        {-8.357579488,26.04116045,8,33.14285033,316.988623},
        {-8.212159276,23.71702574,8,29.8242134,323.9515092},
        {-8.033671598,21.43832358,8,26.90921086,329.3583299},
        {-7.82283516,19.21422951,8,24.42212906,333.6387118},
        {-7.580498926,17.05369916,8,22.32518015,337.1008544},
        {-7.307638698,14.96543223,8,20.56278726,339.9604829},
        {-7.005353188,12.95783744,8,19.08010248,342.3689078},
        {-6.674859593,11.03899865,8,17.82931886,344.4336617},
        {-6.317488694,9.216642361,8,16.77087522,346.2325126},
        {-5.934679499,7.498106552,8,15.87278358,347.8227717},
        {-5.527973443,5.890311161,8,15.10943123,349.2474803},
        {-5.09900819,4.399730214,8,14.4603768,350.5395666},
        {-4.64951103,3.032365752,8,13.90931101,351.7246806},
        {-4.18129193,1.793723671,8,13.44321419,352.8231604},
        {-3.696236244,0.688791545,8,13.05169381,353.8514215},
        {-3.196297119,-0.277981455,8,12.72647158,354.8229556},
        {-2.683487633,-1.102702474,8,12.46099028,355.7490634},
        {-2.15987269,-1.782050653,8,12.2501145,356.6394031},
        {-1.627560701,-2.313290496,8,12.08990535,357.5024104},
        {-1.088695101,-2.694282888,8,11.97745373,358.3456298},
        {-0.545445709,-2.923493706,8,11.91076083,359.1759836}};
    private static final double[][] ROUTE3 = {{0.672293548,-4.919467059,10,14.06152248,0.964841082},
        {1.341880008,-4.678192513,10,14.1377727,1.936952385},
        {2.00606319,-4.277147891,10,14.2662481,2.923816338},
        {2.662168664,-3.717948056,10,14.44908755,3.93335247},
        {3.307554524,-3.00284471,10,14.68939078,4.974168914},
        {3.93962203,-2.134717321,10,14.99133014,6.055857104},
        {4.555826068,-1.117061531,10,15.36030371,7.189350725},
        {5.153685403,0.046024917,10,15.8031369,8.38737685},
        {5.730792665,1.349858687,10,16.32834148,9.665037675},
        {6.284824048,2.789189699,10,16.94644126,11.04057672},
        {6.813548663,4.358222275,10,17.67037223,12.53640665},
        {7.314837522,6.050638476,10,18.51595805,14.18051013},
        {7.786672112,7.859623538,10,19.50244327,16.00837671},
        {8.227152521,9.777893317,10,20.65302295,18.0657129},
        {8.634505092,11.79772362,10,21.99521304,20.41226863},
        {9.007089557,13.9109813,10,23.56070638,23.12725831},
        {9.343405653,16.10915701,10,25.38395663,26.31698627},
        {9.642099151,18.38339948,10,27.49795712,30.12526724},
        {9.901967319,20.72455114,10,29.92431383,34.74657511},
        {10.12196376,23.12318499,10,32.65273902,40.43915076},
        {10.30120262,25.56964258,10,35.60378694,47.52708837},
        {10.43896218,28.05407289,10,38.57354406,56.36080818},
        {10.53468772,30.566472,10,41.18336331,67.17618658},
        {10.5879938,33.09672337,10,42.90951515,79.80945705},
        {10.59866575,35.63463855,10,43.28408513,93.42672803},
        {10.56666063,38.16999827,10,42.19107174,106.6992447},
        {10.49210728,40.69259353,10,39.95416754,118.4824694},
        {10.37530593,43.19226672,10,37.10558791,128.2943669},
        {10.21672688,45.65895255,10,34.10953283,136.2135593},
        {10.01700868,48.08271853,10,31.25353766,142.5599035},
        {9.776955518,50.45380503,10,28.6716461,147.6802287},
        {9.497534007,52.7626645,10,26.40282205,151.8669923},
        {9.17986928,55,10,24.43793667,155.3451864},
        {8.825240459,57.15680255,10,22.74792763,158.2821925},
        {8.43507551,59.22438749,10,21.29827605,160.8015794},
        {8.010945488,61.19442936,10,20.05560492,162.9949898},
        {7.554558217,63.05899551,10,18.99023683,164.9311058},
        {7.067751405,64.81057799,10,18.07684214,166.6620907},
        {6.552485254,66.44212379,10,17.2942626,168.2281434},
        {6.010834557,67.94706326,10,16.62503273,169.6607164},
        {5.444980351,69.31933653,10,16.0548365,170.9848031},
        {4.85720113,70.55341795,10,15.57199885,172.2205831},
        {4.249863675,71.6443383,10,15.16704725,173.3846191},
        {3.625413519,72.58770483,10,14.8323494,174.4907431},
        {2.986365103,73.37971894,10,14.56182213,175.5507219},
        {2.335291648,74.01719148,10,14.35070223,176.5747683},
        {1.674814797,74.49755555,10,14.19537004,177.5719417},
        {1.007594059,74.8188769,10,14.09321789,178.5504722},
        {0.336316095,74.9798617,10,14.04255679,179.5180314},
        {-0.336316095,74.9798617,10,14.04255679,180.4819686},
        {-1.007594059,74.8188769,10,14.09321789,181.4495278},
        {-1.674814797,74.49755555,10,14.19537004,182.4280583},
        {-2.335291648,74.01719148,10,14.35070223,183.4252317},
        {-2.986365103,73.37971894,10,14.56182213,184.4492781},
        {-3.625413519,72.58770483,10,14.8323494,185.5092569},
        {-4.249863675,71.6443383,10,15.16704725,186.6153809},
        {-4.85720113,70.55341795,10,15.57199885,187.7794169},
        {-5.444980351,69.31933653,10,16.0548365,189.0151969},
        {-6.010834557,67.94706326,10,16.62503273,190.3392836},
        {-6.552485254,66.44212379,10,17.2942626,191.7718566},
        {-7.067751405,64.81057799,10,18.07684214,193.3379093},
        {-7.554558217,63.05899551,10,18.99023683,195.0688942},
        {-8.010945488,61.19442936,10,20.05560492,197.0050102},
        {-8.43507551,59.22438749,10,21.29827605,199.1984206},
        {-8.825240459,57.15680255,10,22.74792763,201.7178075},
        {-9.17986928,55,10,24.43793667,204.6548136},
        {-9.497534007,52.7626645,10,26.40282205,208.1330077},
        {-9.776955518,50.45380503,10,28.6716461,212.3197713},
        {-10.01700868,48.08271853,10,31.25353766,217.4400965},
        {-10.21672688,45.65895255,10,34.10953283,223.7864407},
        {-10.37530593,43.19226672,10,37.10558791,231.7056331},
        {-10.49210728,40.69259353,10,39.95416754,241.5175306},
        {-10.56666063,38.16999827,10,42.19107174,253.3007553},
        {-10.59866575,35.63463855,10,43.28408513,266.573272},
        {-10.5879938,33.09672337,10,42.90951515,280.190543},
        {-10.53468772,30.566472,10,41.18336331,292.8238134},
        {-10.43896218,28.05407289,10,38.57354406,303.6391918},
        {-10.30120262,25.56964258,10,35.60378694,312.4729116},
        {-10.12196376,23.12318499,10,32.65273902,319.5608492},
        {-9.901967319,20.72455114,10,29.92431383,325.2534249},
        {-9.642099151,18.38339948,10,27.49795712,329.8747328},
        {-9.343405653,16.10915701,10,25.38395663,333.6830137},
        {-9.007089557,13.9109813,10,23.56070638,336.8727417},
        {-8.634505092,11.79772362,10,21.99521304,339.5877314},
        {-8.227152521,9.777893317,10,20.65302295,341.9342871},
        {-7.786672112,7.859623538,10,19.50244327,343.9916233},
        {-7.314837522,6.050638476,10,18.51595805,345.8194899},
        {-6.813548663,4.358222275,10,17.67037223,347.4635934},
        {-6.284824048,2.789189699,10,16.94644126,348.9594233},
        {-5.730792665,1.349858687,10,16.32834148,350.3349623},
        {-5.153685403,0.046024917,10,15.8031369,351.6126231},
        {-4.555826068,-1.117061531,10,15.36030371,352.8106493},
        {-3.93962203,-2.134717321,10,14.99133014,353.9441429},
        {-3.307554524,-3.00284471,10,14.68939078,355.0258311},
        {-2.662168664,-3.717948056,10,14.44908755,356.0666475},
        {-2.00606319,-4.277147891,10,14.2662481,357.0761837},
        {-1.341880008,-4.678192513,10,14.1377727,358.0630476},
        {-0.672293548,-4.919467059,10,14.06152248,359.0351589}};
    private static final double[][] testRoute = {{1,1,3,10,45}, {2,2,3,45,90}, {};

    private boolean yawControlModeFlag = true;
    private boolean rollPitchControlModeFlag = true;
    private boolean verticalControlModeFlag = true;
    private boolean horizontalCoordinateFlag = true;

    // find where other buttons are called
    private Button loadMission1;
    private Button loadMission2;
    private Button loadMission3;
    private Button testMission;

    private Button btnEnableVirtualStick;
    private Button btnDisableVirtualStick;
    private Button btnHorizontalCoordinate;
    private Button btnSetYawControlMode;
    private Button btnSetVerticalControlMode;
    private Button btnSetRollPitchControlMode;
    private ToggleButton btnSimulator;
    private Button btnTakeOff;

    private TextView textView;

    private OnScreenJoystick screenJoystickRight;
    private OnScreenJoystick screenJoystickLeft;

    private Timer sendVirtualStickDataTimer;
    private SendVirtualStickDataTask sendVirtualStickDataTask;

    private float pitch;
    private float roll;
    private float yaw;
    private float throttle;
    private FlightControllerKey isSimulatorActived;

    public VirtualStickView(Context context) {
        super(context);
        init(context);
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

    private double[][] calculatePoints(double[][] input) {
        return input;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setUpListeners();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (null != sendVirtualStickDataTimer) {
            if (sendVirtualStickDataTask != null) {
                sendVirtualStickDataTask.cancel();

            }
            sendVirtualStickDataTimer.cancel();
            sendVirtualStickDataTimer.purge();
            sendVirtualStickDataTimer = null;
            sendVirtualStickDataTask = null;
        }
        tearDownListeners();
        super.onDetachedFromWindow();
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_virtual_stick, this, true);

        initAllKeys();
        initUI();
    }

    private void initAllKeys() {
        isSimulatorActived = FlightControllerKey.create(FlightControllerKey.IS_SIMULATOR_ACTIVE);
    }

    private void initUI() {
        btnEnableVirtualStick = (Button) findViewById(R.id.btn_enable_virtual_stick);
        btnDisableVirtualStick = (Button) findViewById(R.id.btn_disable_virtual_stick);
        btnHorizontalCoordinate = (Button) findViewById(R.id.btn_horizontal_coordinate);
        btnSetYawControlMode = (Button) findViewById(R.id.btn_yaw_control_mode);
        btnSetVerticalControlMode = (Button) findViewById(R.id.btn_vertical_control_mode);
        btnSetRollPitchControlMode = (Button) findViewById(R.id.btn_roll_pitch_control_mode);
        btnTakeOff = (Button) findViewById(R.id.btn_take_off);

        btnSimulator = (ToggleButton) findViewById(R.id.btn_start_simulator);

        textView = (TextView) findViewById(R.id.textview_simulator);

        screenJoystickRight = (OnScreenJoystick) findViewById(R.id.directionJoystickRight);
        screenJoystickLeft = (OnScreenJoystick) findViewById(R.id.directionJoystickLeft);

        btnEnableVirtualStick.setOnClickListener(this);
        btnDisableVirtualStick.setOnClickListener(this);
        btnHorizontalCoordinate.setOnClickListener(this);
        btnSetYawControlMode.setOnClickListener(this);
        btnSetVerticalControlMode.setOnClickListener(this);
        btnSetRollPitchControlMode.setOnClickListener(this);
        btnTakeOff.setOnClickListener(this);
        btnSimulator.setOnCheckedChangeListener(VirtualStickView.this);

        Boolean isSimulatorOn = (Boolean) KeyManager.getInstance().getValue(isSimulatorActived);
        if (isSimulatorOn != null && isSimulatorOn) {
            btnSimulator.setChecked(true);
            textView.setText("Simulator is On.");
        }
    }

    private void setUpListeners() {
        Simulator simulator = ModuleVerificationUtil.getSimulator();
        if (simulator != null) {
            simulator.setStateCallback(new SimulatorState.Callback() {
                @Override
                public void onUpdate(@NonNull final SimulatorState simulatorState) {
                    ToastUtils.setResultToText(textView,
                            "Yaw : "
                                    + simulatorState.getYaw()
                                    + ","
                                    + "X : "
                                    + simulatorState.getPositionX()
                                    + "\n"
                                    + "Y : "
                                    + simulatorState.getPositionY()
                                    + ","
                                    + "Z : "
                                    + simulatorState.getPositionZ());
                }
            });
        } else {
            ToastUtils.setResultToToast("Disconnected!");
        }

        screenJoystickLeft.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }
                float pitchJoyControlMaxSpeed = 10;
                float rollJoyControlMaxSpeed = 10;

                if (horizontalCoordinateFlag) {
                    if (rollPitchControlModeFlag) {
                        pitch = (float) (pitchJoyControlMaxSpeed * pX);

                        roll = (float) (rollJoyControlMaxSpeed * pY);
                    } else {
                        pitch = -(float) (pitchJoyControlMaxSpeed * pY);

                        roll = (float) (rollJoyControlMaxSpeed * pX);
                    }
                }

                if (null == sendVirtualStickDataTimer) {
                    sendVirtualStickDataTask = new SendVirtualStickDataTask();
                    sendVirtualStickDataTimer = new Timer();
                    sendVirtualStickDataTimer.schedule(sendVirtualStickDataTask, 100, 200);
                }
            }
        });

        screenJoystickRight.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if (Math.abs(pX) < 0.02) {
                    pX = 0;
                }

                if (Math.abs(pY) < 0.02) {
                    pY = 0;
                }
                float verticalJoyControlMaxSpeed = 2;
                float yawJoyControlMaxSpeed = 3;

                yaw = yawJoyControlMaxSpeed * pX;
                throttle = verticalJoyControlMaxSpeed * pY;

                if (null == sendVirtualStickDataTimer) {
                    sendVirtualStickDataTask = new SendVirtualStickDataTask();
                    sendVirtualStickDataTimer = new Timer();
                    sendVirtualStickDataTimer.schedule(sendVirtualStickDataTask, 0, 200);
                }
            }
        });
    }

    private void tearDownListeners() {
        Simulator simulator = ModuleVerificationUtil.getSimulator();
        if (simulator != null) {
            simulator.setStateCallback(null);
        }
        screenJoystickLeft.setJoystickListener(null);
        screenJoystickRight.setJoystickListener(null);
    }

    @Override
    public void onClick(View v) {
        FlightController flightController = ModuleVerificationUtil.getFlightController();
        if (flightController == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_enable_virtual_stick:
                flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        DialogUtils.showDialogBasedOnError(getContext(), djiError);
                    }
                });
                break;

            case R.id.btn_disable_virtual_stick:
                flightController.setVirtualStickModeEnabled(false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        DialogUtils.showDialogBasedOnError(getContext(), djiError);
                    }
                });
                break;

            case R.id.btn_roll_pitch_control_mode:
                if (rollPitchControlModeFlag) {
                    flightController.setRollPitchControlMode(RollPitchControlMode.ANGLE);
                    rollPitchControlModeFlag = false;
                } else {
                    flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
                    rollPitchControlModeFlag = true;
                }
                try {
                    ToastUtils.setResultToToast(flightController.getRollPitchControlMode().name());
                } catch (Exception ex) {
                }
                break;

            case R.id.btn_yaw_control_mode:
                if (yawControlModeFlag) {
                    flightController.setYawControlMode(YawControlMode.ANGLE);
                    yawControlModeFlag = false;
                } else {
                    flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
                    yawControlModeFlag = true;
                }
                try {
                    ToastUtils.setResultToToast(flightController.getYawControlMode().name());
                } catch (Exception ex) {
                }
                break;

            case R.id.btn_vertical_control_mode:
                if (verticalControlModeFlag) {
                    flightController.setVerticalControlMode(VerticalControlMode.POSITION);
                    verticalControlModeFlag = false;
                } else {
                    flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
                    verticalControlModeFlag = true;
                }
                try {
                    ToastUtils.setResultToToast(flightController.getVerticalControlMode().name());
                } catch (Exception ex) {
                }
                break;

            case R.id.btn_horizontal_coordinate:
                if (horizontalCoordinateFlag) {
                    flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.GROUND);
                    horizontalCoordinateFlag = false;
                } else {
                    flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
                    horizontalCoordinateFlag = true;
                }
                try {
                    ToastUtils.setResultToToast(flightController.getRollPitchCoordinateSystem().name());
                } catch (Exception ex) {
                }
                break;

            case R.id.btn_take_off:

                flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        DialogUtils.showDialogBasedOnError(getContext(), djiError);
                    }
                });

                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == btnSimulator) {
            onClickSimulator(b);
        }
    }

    private void onClickSimulator(boolean isChecked) {
        Simulator simulator = ModuleVerificationUtil.getSimulator();
        if (simulator == null) {
            return;
        }
        if (isChecked) {

            textView.setVisibility(VISIBLE);

            simulator.start(InitializationData.createInstance(new LocationCoordinate2D(23, 113), 10, 10),
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {

                        }
                    });
        } else {

            textView.setVisibility(INVISIBLE);

            simulator.stop(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

                }
            });
        }
    }

    @Override
    public int getDescription() {
        return R.string.flight_controller_listview_virtual_stick;
    }

    private class SendVirtualStickDataTask extends TimerTask {

        @Override
        public void run() {
            if (ModuleVerificationUtil.isFlightControllerAvailable()) {
                DJISampleApplication.getAircraftInstance()
                        .getFlightController()
                        .sendVirtualStickFlightControlData(new FlightControlData(pitch,
                                        roll,
                                        yaw,
                                        throttle),
                                new CommonCallbacks.CompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {

                                    }
                                });
            }
        }
    }
}
