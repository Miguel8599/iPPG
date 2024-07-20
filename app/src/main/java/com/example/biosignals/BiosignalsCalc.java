package com.example.biosignals;

import static java.lang.Float.parseFloat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.Request;

public class BiosignalsCalc extends AppCompatActivity implements OnChartValueSelectedListener, SurfaceHolder.Callback {

    private Camera camera;
    private Camera.Parameters parameters;
    private Camera.PreviewCallback call;
    private SurfaceHolder holder;
    private LineChart chart;
    private LineChart chart2;
    private LineChart chart12;
    private List<Float> Interpolate = new ArrayList<Float>();
    private List<Float> Filtrate = new ArrayList<Float>();
    private List<Float> Time = new ArrayList<Float>();
    private List<Float> TimePeaks = new ArrayList<Float>();
    private List<Float> Peaks = new ArrayList<Float>();
    private List<Float> HRPeaks = new ArrayList<Float>();
    private List<Float> SaveSignal = new ArrayList<Float>();
    private List<Float> SaveSignal2 = new ArrayList<Float>();
    private List<Float> SaveSignal3 = new ArrayList<Float>();
    private List<Float> SaveSignal4 = new ArrayList<Float>();
    private double timestart = 0;
    private double timestart2 = 0;
    private double timestart3 = 0;
    private float previPPG = 0;
    private float totaltime = 15;
    private boolean flash = true;
    private float HRmean = 0;
    private float RRmean = 0;
    private int color = Color.BLUE;
    private String list1[] = {"15 seconds", "30 seconds", "60 seconds"};
    private String item1;
    private String list2[] = {"On", "Off"};
    private String item2;
    private String list3[] = {"Blue", "Green", "Red"};
    private String item3;
    private boolean flag = false;
    private boolean flag2 = false;
    private boolean flag3 = false;
    private boolean flag4 = false;
    private boolean flag5 = true;
    private int sp;
    private int dp;
    private boolean flag10 = true;
    private float DPETF = 80;
    private float SPETF = 120;
    private float OXETF = 96;
    private Object List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biosignals_calc);

        int cameraId = 0;

        createAppBar(R.id.miAppBar_bp, R.color.colorPrimary, false);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        startGUI();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BiosignalsCalc.this, android.R.layout.simple_list_item_single_choice,list1);

        ListView lv1 = findViewById(R.id.listview1);

        lv1.setAdapter(adapter);

        lv1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv1.setSelection(0);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = (String) adapterView.getItemAtPosition(i);
                item1 = item;

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(BiosignalsCalc.this, android.R.layout.simple_list_item_single_choice,list2);

        ListView lv2 = findViewById(R.id.listview2);

        lv2.setAdapter(adapter2);

        lv2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv2.setSelection(1);

        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = (String) adapterView.getItemAtPosition(i);
                item2 = item;

            }
        });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(BiosignalsCalc.this, android.R.layout.simple_list_item_single_choice,list3);

        ListView lv3 = findViewById(R.id.listview3);

        lv3.setAdapter(adapter3);

        lv3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lv3.setSelection(1);

        lv3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = (String) adapterView.getItemAtPosition(i);
                item3 = item;

            }
        });

        chart.setOnChartValueSelectedListener(this);
        chart2.setOnChartValueSelectedListener(this);
        chart12.setOnChartValueSelectedListener(this);
        chart.getDescription().setEnabled(false);
        chart2.getDescription().setEnabled(false);
        chart12.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart2.setTouchEnabled(false);
        chart12.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart2.setDragEnabled(false);
        chart12.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart2.setScaleEnabled(false);
        chart12.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart2.setDrawGridBackground(false);
        chart12.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart2.setPinchZoom(false);
        chart12.setPinchZoom(false);
        LineData datachart = new LineData();
        datachart.setValueTextColor(Color.BLACK);
        LineData datachart2 = new LineData();
        datachart2.setValueTextColor(Color.BLACK);
        LineData datachart12 = new LineData();
        datachart12.setValueTextColor(Color.BLACK);
        chart.setData(datachart);
        chart2.setData(datachart2);
        chart12.setData(datachart12);
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);
        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        XAxis xl2 = chart2.getXAxis();
        xl2.setTextColor(Color.BLACK);
        xl2.setDrawGridLines(false);
        xl2.setAvoidFirstLastClipping(true);
        xl2.setEnabled(true);
        xl2.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend l12 = chart12.getLegend();
        l12.setForm(Legend.LegendForm.LINE);
        l12.setTextColor(Color.BLACK);
        XAxis xl12 = chart12.getXAxis();
        xl12.setTextColor(Color.BLACK);
        xl12.setDrawGridLines(true);
        xl12.setAvoidFirstLastClipping(true);
        xl12.setEnabled(true);
        xl12.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        YAxis leftAxis2 = chart2.getAxisLeft();
        leftAxis2.setEnabled(false);
        leftAxis2.setTextColor(Color.BLACK);
        leftAxis2.setDrawGridLines(false);
        YAxis rightAxis2 = chart2.getAxisRight();
        rightAxis2.setEnabled(false);
        YAxis leftAxis12 = chart12.getAxisLeft();
        leftAxis12.setTextColor(Color.BLACK);
        leftAxis12.setDrawGridLines(true);
        YAxis rightAxis12 = chart12.getAxisRight();
        rightAxis12.setEnabled(false);

        camera = Camera.open(cameraId);
        parameters = camera.getParameters();
        setCameraDisplayOrientation(BiosignalsCalc.this,cameraId,camera);

        call = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera arg1) {

                if(flag){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height,null);
                    yuvimage.compressToJpeg(new Rect(0,0,arg1.getParameters().getPreviewSize().width,arg1.getParameters().getPreviewSize().height), 80, baos);

                    byte[] byteArray = baos.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                    int newwidth = (int) (arg1.getParameters().getPreviewSize().width/2);
                    int newheight = (int) (arg1.getParameters().getPreviewSize().height/2);
                    int count1 = 0;
                    int count2 = 0;
                    double sum = 0;
                    int greenValue = 0;
                    int redValue = 0;
                    int blueValue = 0;
                    int pix = 0;
                    for(count1=0;count1<151;count1++){
                        for(count2=0;count2<151;count2++){
                            pix = bitmap.getPixel(newwidth-75+count1,newheight-75+count2);
                            greenValue = Color.green(pix);
                            redValue = Color.red(pix);
                            blueValue = Color.blue(pix);
                            sum = sum + 0.299*(double)redValue + 0.587*(double)greenValue + 0.114*(double)blueValue;
                        }
                    }

                    float iPPG = (float)sum/(float)(151*151);

                    iPPG = (iPPG - previPPG)/(float) 100;

                    previPPG = iPPG;


                    if(flag2){
                        Interpolate.add(iPPG);
                        float timez = (float)((System.currentTimeMillis() - timestart2)/(double)1000);
                        Time.add(timez);
                    }

                }

            }
        };

        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        recibirParametros();
    }

    //==============================================================================================
    Button btnSave, btnContinue, btnStop, btnBack;
    SurfaceView cameraView;
    ImageView btnPlaystart, btnConfigstart, btnInformstart;
    LinearLayout linLay1, linLay2, linLay3, linLay4, linLay5, linLay6, linLay7,linlay8;
    ScrollView scroll1;
    TextView txtStart, txtClose, txtHR, txtC1, txtC2, titlehr, titlehrv;
    TextView SPET,DPET, HRET, RRET;
    RadioGroup RGHR;

    String posPaciente;
    Paciente miPaciente;

    private void recibirParametros () {
        Bundle parametros = getIntent().getExtras();
        miPaciente = new Paciente(parametros.getString("paciente"));
        posPaciente = parametros.getString("posPaciente");
    }


    private void startGUI()
    {

        btnPlaystart = findViewById(R.id.btnplaystart);
        btnConfigstart = findViewById(R.id.btnconfigstart);
        btnInformstart = findViewById(R.id.btninformstart);
        cameraView = findViewById(R.id.CameraView);
        scroll1 = findViewById(R.id.nomostrar);
        chart = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
        chart12 = findViewById(R.id.chart12);
        linLay1 = findViewById(R.id.configscreen);
        linLay2 = findViewById(R.id.startlin);
        linLay3 = findViewById(R.id.graphlin);
        linLay4 = findViewById(R.id.configlin);
        linLay5 = findViewById(R.id.waitlin);
        linLay6 = findViewById(R.id.progresslin);
        linLay7 = findViewById(R.id.finallin);
        linlay8 = findViewById(R.id.finallin2);
        txtStart = findViewById(R.id.textstart);
        txtClose = findViewById(R.id.closebtn);
        txtHR = findViewById(R.id.ahrvalue);
        txtC1 = findViewById(R.id.txtchange1);
        txtC2 = findViewById(R.id.txtchange2);
        titlehr = findViewById(R.id.hrtitle);
        titlehrv = findViewById(R.id.hrvtitle);
        btnSave = findViewById(R.id.btnsave);
        btnContinue = findViewById(R.id.btncontinue);
        btnStop = findViewById(R.id.btnstop);
        btnBack = findViewById(R.id.btnbackmain);
        SPET = findViewById(R.id.miEtSP);
        DPET = findViewById(R.id.miEtDP);
        HRET = findViewById(R.id.miEtHR);
        RRET = findViewById(R.id.miEtRR);
        RGHR = (RadioGroup) findViewById(R.id.miRgChart);

        btnPlaystart.setOnClickListener(v -> clickedStart(v));
        btnConfigstart.setOnClickListener(v -> clickedConfig(v));
        btnInformstart.setOnClickListener(v -> clickedInform(v));
        txtClose.setOnClickListener(v -> clickedCloseConf(v));
        btnSave.setOnClickListener(v -> clickedCloseSet(v));
        btnContinue.setOnClickListener(v -> clickedContinue(v));
        btnStop.setOnClickListener(v -> clickedStop(v));
        btnBack.setOnClickListener(v -> clickedBack(v));

        linLay3.setVisibility(View.INVISIBLE);
        linLay1.setVisibility(View.INVISIBLE);
        linLay4.setVisibility(View.INVISIBLE);
        linLay5.setVisibility(View.INVISIBLE);
        linLay6.setVisibility(View.INVISIBLE);
        linLay7.setVisibility(View.INVISIBLE);
        linlay8.setVisibility(View.INVISIBLE);

    }

    //==============================================================================================

    @Override
    public void onBackPressed() {
        if(linLay1.getVisibility() == View.VISIBLE){
            linLay1.setVisibility(View.INVISIBLE);
            txtStart.setVisibility(View.VISIBLE);
            linLay2.setVisibility(View.VISIBLE);
        } else {
            if(linLay4.getVisibility() == View.VISIBLE){
                linLay4.setVisibility(View.INVISIBLE);
                txtStart.setVisibility(View.VISIBLE);
                linLay2.setVisibility(View.VISIBLE);
            } else {
                if(linLay2.getVisibility() == View.VISIBLE){
                    if(camera!=null){
                        camera.setPreviewCallback(null);
                        camera.stopPreview();
                        camera.release();
                    }
                    flag5 = false;
                    if (thread != null)
                        thread.interrupt();
                    Intent intent = new Intent(getApplicationContext(), Patients.class);
                    intent.putExtra("paciente", miPaciente.getCadenaDatos());
                    intent.putExtra("posPaciente", String.valueOf(0));
                    startActivity(intent);
                    finish();
                } else {
                    if(camera!=null){
                        camera.setPreviewCallback(null);
                        camera.stopPreview();
                        camera.release();
                    }
                    flag5 = false;
                    if (thread != null)
                        thread.interrupt();
                    Intent intent = new Intent(getApplicationContext(), BiosignalsCalc.class);
                    intent.putExtra("paciente", miPaciente.getCadenaDatos());
                    intent.putExtra("posPaciente", String.valueOf(0));
                    startActivity(intent);
                    finish();
                }
            }
        }

    }

     //==============================================================================================

    public void clickedStop(View v){

        if(camera!=null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        flag5 = false;
        if (thread != null)
            thread.interrupt();
        Intent intent = new Intent(getApplicationContext(), BiosignalsCalc.class);
        intent.putExtra("paciente", miPaciente.getCadenaDatos());
        intent.putExtra("posPaciente", String.valueOf(0));
        startActivity(intent);
        finish();
    }

    public void clickedBack(View v){

        if(camera!=null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        flag5 = false;
        if (thread != null)
            thread.interrupt();
        if(guardarSignal()){
            Intent intent = new Intent(getApplicationContext(), BiosignalsCalc.class);
            intent.putExtra("paciente", miPaciente.getCadenaDatos());
            intent.putExtra("posPaciente", String.valueOf(0));
            startActivity(intent);
            finish();
        }

    }

    public void clickedStart(View v){
        txtStart.setVisibility(View.INVISIBLE);
        linLay2.setVisibility(View.INVISIBLE);
        linLay3.setVisibility(View.VISIBLE);

        if(flash){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        parameters.setPreviewFpsRange(30000,30000);
        camera.setParameters(parameters);
        camera.setPreviewCallback(call);
        camera.startPreview();
    }

    public void clickedConfig(View v){
        linLay4.setVisibility(View.VISIBLE);
        txtStart.setVisibility(View.INVISIBLE);
        linLay2.setVisibility(View.INVISIBLE);
    }

    public void clickedInform(View v){
        linLay1.setVisibility(View.VISIBLE);
        txtStart.setVisibility(View.INVISIBLE);
        linLay2.setVisibility(View.INVISIBLE);
    }

    public void clickedCloseConf(View v){
        linLay1.setVisibility(View.INVISIBLE);
        txtStart.setVisibility(View.VISIBLE);
        linLay2.setVisibility(View.VISIBLE);
    }

    public void clickedCloseSet(View v){
        linLay4.setVisibility(View.INVISIBLE);
        txtStart.setVisibility(View.VISIBLE);
        linLay2.setVisibility(View.VISIBLE);
        Toast.makeText(BiosignalsCalc.this,R.string.savedset, Toast.LENGTH_SHORT).show();
        if(item1 == "15 seconds"){
            totaltime = 15;
        }
        if(item1 == "30 seconds"){
            totaltime = 30;
        }
        if(item1 == "60 seconds"){
            totaltime = 60;
        }
        if(item2 == "On"){
            flash = true;
        }
        if(item2 == "Off"){
            flash = false;
        }
        if(item3 == "Blue"){
            color = Color.BLUE;
        }
        if(item3 == "Green"){
            color = Color.GREEN;
        }
        if(item3 == "Red"){
            color = Color.RED;
        }

    }

    public void clickedContinue(View v){
        linLay3.setVisibility(View.INVISIBLE);
        linLay5.setVisibility(View.VISIBLE);
        linLay6.setVisibility(View.VISIBLE);
        flag = true;
        timestart = System.currentTimeMillis();
        feedMultiple();


    }

    //==============================================================================================

    private Thread thread;

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if(flag5) {
                    if ((float) ((System.currentTimeMillis() - timestart) / (double) 1000) > (float) 2.5 && !flag2 && timestart != 0) {
                        flag2 = true;
                        timestart2 = System.currentTimeMillis();
                    }
                    if ((float) ((System.currentTimeMillis() - timestart) / (double) 1000) > (float) 6.5 && !flag3 && timestart != 0) {
                        flag3 = true;
                        linLay6.setVisibility(View.INVISIBLE);
                        timestart3 = System.currentTimeMillis();
                        txtC1.setText("Time");
                        txtC2.setText("We are acquiring the data.\nPlease, do not move.");
                    }
                    if ((float) ((System.currentTimeMillis() - timestart3) / (double) 1000) <= (float) totaltime && flag3 && timestart3 != 0) {
                        addEntry2();
                    }
                    if ((float) ((System.currentTimeMillis() - timestart3) / (double) 1000) > totaltime && !flag4 && timestart3 != 0) {
                        flag4 = true;
                        linLay5.setVisibility(View.INVISIBLE);
                        linLay7.setVisibility(View.VISIBLE);
                        txtC2.setText("We are processing the data.\nPlease, wait.");
                        if (camera != null) {
                            camera.setPreviewCallback(null);
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                        }
                        addEntry();
                    }
                }
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 2000; i++) {

                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    //==============================================================================================

    private void addEntry2() {

        LineData data = chart2.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet2();
                data.addDataSet(set);
            }

            data.addEntry(new Entry((float)((System.currentTimeMillis() - timestart3)/(double)1000), (float) 1), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart2.notifyDataSetChanged();


            float totaltime22 = totaltime;
            if(totaltime==15){
                totaltime22 = 16;
            }
            // limit the number of visible entries
            chart2.setVisibleXRange(0,totaltime22);
            chart2.setVisibleYRangeMaximum(1,YAxis.AxisDependency.LEFT);
            chart2.setVisibleYRangeMinimum(0,YAxis.AxisDependency.LEFT);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart2.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);


        }
    }

    //==============================================================================================

    public void changeChart(View v) {
        if(RGHR.getCheckedRadioButtonId() == R.id.miRbHR) {
            linlay8.setVisibility(View.INVISIBLE);
            titlehr.setVisibility(View.VISIBLE);
            titlehrv.setVisibility(View.INVISIBLE);
            chart12.setVisibility(View.INVISIBLE);
            chart.setVisibility(View.VISIBLE);
        } else {
            linlay8.setVisibility(View.VISIBLE);
            titlehr.setVisibility(View.INVISIBLE);
            titlehrv.setVisibility(View.VISIBLE);
            chart12.setVisibility(View.VISIBLE);
            chart.setVisibility(View.INVISIBLE);
            scroll1.setVisibility(View.INVISIBLE);
        }

    }


    public void getHR(){
        HRET.setText("Heart Rate: " + (int)HRmean + " bpm");
    }

    public void getRR(){
        List<Float> newInterpolate = new ArrayList<Float>();
        List<Float> newTime = new ArrayList<Float>();
        List<Float> newFiltrate = new ArrayList<Float>();
        List<Float> TF = new ArrayList<>();
        List<Float> TFHz = new ArrayList<>();
        List<Float> TFMax = new ArrayList<>();
        List<Float> TFCort = new ArrayList<>();
        List<Float> TFCort2 = new ArrayList<>();
        List <Float> NewFil = new ArrayList<>();


        float timeS = Time.get(0);

        for(int h=0;h<Time.size();h++){
            Time.set(h,Time.get(h)-timeS);
        }

        for(int a = 0; a<Time.get(Time.size()-1)*250; a++){
            newTime.add((float)(a+1)/(float)250);
            newInterpolate.add(-SplineCubico(Time,Interpolate,newTime.get(a)));
        }

        filter2 C = new filter2();

        double[] a3 = {1.0, -1.995, 0.995};
        double[] b3 = {0.9975,-1.995,0.9975};
        double[] N3 = new double[newInterpolate.size()];
        double[] p3 = new double[newInterpolate.size()];

        for(int i=0;i<newInterpolate.size();i++){
            N3[i] = newInterpolate.get(i);
        }

        C.fil(a3,b3,N3,p3);

        for (int i=0; i<N3.length; i++){
            Filtrate.add((float)p3[i]);
        }

        int newnewsizeT = newTime.size();

        for(int u=0;u<875;u++){
            Filtrate.remove(0);
            newTime.remove(newnewsizeT-1-u);
        }


        filter2 A = new filter2();

        double[] a = {1.0, -1.968, 0.9685};
        double[] b = {0.0001259,0.0002518,0.0001259};
        double[] N1 = new double[Filtrate.size()];
        double[] p1 = new double[Filtrate.size()];

        for(int i=0;i<Filtrate.size();i++){
            N1[i] = Filtrate.get(i);
        }

        A.fil(a,b,N1,p1);

        for (int i=0; i<N1.length; i++){
            newFiltrate.add((float)p1[i]);
        }

        int newsizeT = newTime.size();

        for(int u=0;u<125;u++){
            newFiltrate.remove(0);
            newTime.remove(newsizeT-1-u);
        }

        float min = Collections.min(newFiltrate);

        for(int i=0;i<newFiltrate.size();i++){
            newFiltrate.set(i,newFiltrate.get(i) + Math.abs(min));
        }

        List<Float> newFiltrate2 = new ArrayList<>();

        for(int i=0;i<newFiltrate.size()/2;i++){
            newFiltrate2.add(newFiltrate.get(i*2));
        }

        List<Float> TFMaxPeak = new ArrayList<>();
        List<Float> TFIndexTotal = new ArrayList<>();

        for(int g = 0; g<1;g++){

            TF.clear();
            TFCort.clear();
            TFCort2.clear();
            TFHz.clear();
            NewFil.clear();
            TFMaxPeak.clear();
            TFIndexTotal.clear();

            for(int h = g*250; h<g*250+totaltime*250;h++){
                if(h<=newFiltrate.size()-1){
                    NewFil.add(newFiltrate.get(h));
                }
            }

            double[] fourierreal = new double[NewFil.size()];
            double[] fourierimag = new double[NewFil.size()];
            double[] fourierreal2 = new double[NewFil.size()/2];

            for (int i = 0; i < NewFil.size(); i++) {
                fourierreal[i] = NewFil.get(i);
                fourierimag[i] = 0;
            }

            transform(fourierreal, fourierimag);

            for (int i = 0; i < fourierreal.length/2; i++) {
                fourierreal2[i] = fourierreal[i];
            }

            for (int i = 0; i < fourierreal2.length; i++) {

                TF.add((float) Math.sqrt(fourierreal[i]*fourierreal[i]));
            }


            int frecmu = 250;
            int frecmu2 = frecmu / 2;

            double incremento = (double) frecmu2 / (double) TF.size();

            for (int i = 0; i < TF.size(); i++) {
                TFHz.add((float)i * (float)incremento);
            }

            for(int i = 0; i < TFHz.size(); i++){
                if(TFHz.get(i)>=(float)0.1 && TFHz.get(i)<=0.9){
                    TFCort.add((float)Math.pow(TF.get(i),2));
                    TFCort2.add(TFHz.get(i));
                }
            }

            TFMax.add(TFCort.get(0));

            for(int i=1; i < TFCort.size()-1; i++){
                if(TFCort.get(i)>TFCort.get(i-1)&&TFCort.get(i)>TFCort.get(i+1)){
                    TFMaxPeak.add(TFCort.get(i));
                    TFIndexTotal.add(TFCort2.get(i));
                }
            }

            int nummax = 0;

            if(TFMaxPeak.size()>=1){
                TFMax.set(g,TFMaxPeak.get(0));
                for(int k=0;k<TFMaxPeak.size();k++){
                    if(TFMaxPeak.get(k)>=TFMax.get(g)){
                        TFMax.set(g,TFMaxPeak.get(k));
                        nummax = k;
                    }
                }
            }

            if(TFIndexTotal.get(nummax)==0.5){
                TFMaxPeak.remove(nummax);
                TFMax.set(g,TFMaxPeak.get(0));
                if(TFMaxPeak.size()>=1){
                    TFMax.set(g,TFMaxPeak.get(0));
                    for(int k=0;k<TFMaxPeak.size();k++){
                        if(TFMaxPeak.get(k)>=TFMax.get(g)){
                            TFMax.set(g,TFMaxPeak.get(k));
                            nummax = k;
                        }
                    }
                }
            }

            TFMax.set(g,60*TFIndexTotal.get(nummax));
            Log.d("signal", String.valueOf(60*TFIndexTotal.get(nummax)));


        }

        List<Float> TFTime = new ArrayList<>();

        for(int i = 0; i < 3; i++){
            TFMax.add(0,TFMax.get(0));
        }

        for(int i = 0; i < 4; i++){
            TFMax.add(TFMax.get(TFMax.size()-1));
        }

        for(int i=0;i<TFMax.size();i++){
            TFTime.add((float)i);
        }

        List<Float> TFInterpol = new ArrayList<Float>();
        List<Float> TimeInterpol = new ArrayList<Float>();


        float timeSJ = TFTime.get(0);

        for(int h=0;h<TFTime.size();h++){
            TFTime.set(h,TFTime.get(h)-timeSJ);
        }

        for(int aj = 0; aj<TFTime.get(TFTime.size()-1)*10; aj++){
            TimeInterpol.add((float)(aj+1)/(float)10);
            TFInterpol.add(SplineCubico(TFTime,TFMax,TimeInterpol.get(aj)));
        }

        List<Float> TFInterpol2 = new ArrayList<>();
        List<Float> TimeInterpol2 = new ArrayList<>();

        for(int i=0; i<=totaltime;i++){
            TFInterpol2.add(TFMax.get(0));
            TimeInterpol2.add((float)i);
        }

        float mediaTF = 0;

        for(int i = 0; i<TFInterpol.size();i++){
            mediaTF = mediaTF + TFInterpol.get(i);
        }

        mediaTF = mediaTF/TFInterpol.size();

        RRmean = (int)mediaTF;

        RRET.setText("Respiratory Rate: " + (int)RRmean + " brpm");

    }

    public void getBP(){

        // Not working with new version of Heroku
        String herokuModelPredictURL = "https://blood-pressure-estimation.herokuapp.com/predict";
        String mediaType = "application/json";
        Request request = new Request.Builder().url( herokuModelPredictURL ).build();
        String result = request.toString();
        SPETF = parseFloat(result.substring(0,2));
        DPETF = parseFloat(result.substring(3,6));
        SPET.setText("Systolic Pressure: " + (int)SPETF + " mmh");
        DPET.setText("Diastolic Pressure: " + (int)DPETF + " mmh");
    }



    //==============================================================================================
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    //==============================================================================================

    public static float SplineCubico(List<Float> x, List<Float> y, float X) {
        if (x == null || y == null || x.size() != y.size() || x.size() < 2) {
            throw new IllegalArgumentException("There must be at least two control "
                    + "points and the arrays must be of equal length.");
        }

        final int n = x.size();
        float[] d = new float[n - 1]; // could optimize this out
        float[] m = new float[n];

        // Compute slopes of secant lines between successive points.
        for (int i = 0; i < n - 1; i++) {
            float h = x.get(i + 1) - x.get(i);
            if (h <= 0f) {
                throw new IllegalArgumentException("The control points must all "
                        + "have strictly increasing X values.");
            }
            d[i] = (y.get(i + 1) - y.get(i)) / h;
        }

        // Initialize the tangents as the average of the secants.
        m[0] = d[0];
        for (int i = 1; i < n - 1; i++) {
            m[i] = (d[i - 1] + d[i]) * 0.5f;
        }
        m[n - 1] = d[n - 2];

        // Update the tangents to preserve monotonicity.
        for (int i = 0; i < n - 1; i++) {
            if (d[i] == 0f) { // successive Y values are equal
                m[i] = 0f;
                m[i + 1] = 0f;
            } else {
                float a = m[i] / d[i];
                float b = m[i + 1] / d[i];
                float h = (float) Math.hypot(a, b);
                if (h > 9f) {
                    float t = 3f / h;
                    m[i] = t * a * d[i];
                    m[i + 1] = t * b * d[i];
                }
            }
        }

        final List<Float> mX;
        final List<Float> mY;
        final float[] mM;

        mX = x;
        mY = y;
        mM = m;

        final int N = mX.size();
        if (Float.isNaN(X)) {
            return X;
        }
        if (X <= mX.get(0)) {
            return mY.get(0);
        }
        if (X >= mX.get(N - 1)) {
            return mY.get(N - 1);
        }

        // Find the index 'i' of the last point with smaller X.
        // We know this will be within the spline due to the boundary tests.
        int i = 0;
        while (X >= mX.get(i + 1)) {
            i += 1;
            if (X == mX.get(i)) {
                return mY.get(i);
            }
        }

        // Perform cubic Hermite spline interpolation.
        float h = mX.get(i + 1) - mX.get(i);
        float t = (X - mX.get(i)) / h;
        return (mY.get(i) * (1 + 2 * t) + h * mM[i] * t) * (1 - t) * (1 - t)
                + (mY.get(i + 1) * (3 - 2 * t) + h * mM[i + 1] * (t - 1)) * t * t;


    }

    //==============================================================================================

    private void addEntry() {

        List<Float> newInterpolate = new ArrayList<Float>();
        List<Float> newTime = new ArrayList<Float>();
        List<Float> newFiltrate = new ArrayList<Float>();


        float timeS = Time.get(0);

        for(int h=0;h<Time.size();h++){
            Time.set(h,Time.get(h)-timeS);
        }

        for(int a = 0; a<Time.get(Time.size()-1)*250; a++){
            newTime.add((float)(a+1)/(float)250);
            newInterpolate.add(SplineCubico(Time,Interpolate,newTime.get(a)));
        }

        BiosignalsCalc.filter2 C = new BiosignalsCalc.filter2();

        double[] a3 = {1.0, -1.9751, 0.9754};
        double[] b3 = {0.9876,-1.9753,0.9876};
        double[] N3 = new double[newInterpolate.size()];
        double[] p3 = new double[newInterpolate.size()];

        for(int i=0;i<newInterpolate.size();i++){
            N3[i] = newInterpolate.get(i);
        }

        C.fil(a3,b3,N3,p3);

        for (int i=0; i<N3.length; i++){
            Filtrate.add((float)p3[i]);
        }

        int newnewsizeT = newTime.size();

        for(int u=0;u<875;u++){
            Filtrate.remove(0);
            newTime.remove(newnewsizeT-1-u);
        }


        BiosignalsCalc.filter2 A = new BiosignalsCalc.filter2();


        double[] a = {1.0, -1.8935, 0.8989};
        double[] b = {0.0013,0.0027,0.0013};
        double[] N1 = new double[Filtrate.size()];
        double[] p1 = new double[Filtrate.size()];

        for(int i=0;i<Filtrate.size();i++){
            N1[i] = Filtrate.get(i);
        }

        A.fil(a,b,N1,p1);

        for (int i=0; i<N1.length; i++){
            newFiltrate.add((float)p1[i]);
        }

        int newsizeT = newTime.size();

        for(int u=0;u<125;u++){
            newFiltrate.remove(0);
            newTime.remove(newsizeT-1-u);
        }

        float min = Collections.min(newFiltrate);

        for(int i=0;i<newFiltrate.size();i++){
            newFiltrate.set(i,newFiltrate.get(i) + Math.abs(min));
        }

        peaksPPG(newFiltrate,newTime);

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            for(int c=0; c<newTime.size(); c++){
                data.addEntry(new Entry(newTime.get(c), newFiltrate.get(c)), 0);
            }

            SaveSignal = newFiltrate;
            SaveSignal2 = newTime;

            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(totaltime);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());


        }

        LineData data12 = chart12.getData();

        if (data12 != null) {

            ILineDataSet set12 = data12.getDataSetByIndex(0);

            if (set12 == null) {
                set12 = createSet12();
                data12.addDataSet(set12);
            }

            List<Float> HRVInterpol = new ArrayList<Float>();
            List<Float> TimeInterpol = new ArrayList<Float>();


            float timeSj = TimePeaks.get(0);

            for(int h=0;h<TimePeaks.size();h++){
                TimePeaks.set(h,TimePeaks.get(h)-timeSj);
            }

            Log.d("tag", String.valueOf(TimePeaks.size()));
            Log.d("tag", String.valueOf(HRPeaks.size()));
            for(int aj = 0; aj<TimePeaks.get(TimePeaks.size()-1)*4; aj++){
                TimeInterpol.add((float)(aj+1)/(float)4);
                HRVInterpol.add(SplineCubico(TimePeaks,HRPeaks,TimeInterpol.get(aj)));
            }


            for(int c=0; c<TimeInterpol.size(); c++){
                data12.addEntry(new Entry(TimeInterpol.get(c), (float)60000/HRVInterpol.get(c)), 0);

                Log.d("tag", String.valueOf(TimePeaks.size()));
                Log.d("tag", String.valueOf(TimeInterpol.size()));

            }

            SaveSignal3 = HRVInterpol;
            SaveSignal4 = TimeInterpol;


            data12.notifyDataChanged();

            // let the chart know it's data has changed
            chart12.notifyDataSetChanged();

            // limit the number of visible entries
            chart12.setVisibleXRangeMaximum(totaltime);

            // move to the latest entry
            chart12.moveViewToX(data12.getEntryCount());

        }

        getHR();
        getRR();
        getBP();

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    //==============================================================================================
    public class filter2 {
        void fil(final double a[], final double b[], final double N[], double p[])
        {
            int indi = N.length;
            double b_b[]=new double[3];
            double b_a[]=new double[3];
            int k;
            double dbuffer[]=new double[3];
            int j;
            for (k = 0; k < 3; k++) {
                b_b[k] = b[k];
                b_a[k] = a[k];
            }
            for (k = 0; k < 3; k++) {
                b_b[k] /= a[0];
            }
            for (k = 0; k < 2; k++) {
                b_a[k + 1] /= a[0];
            }
            b_a[0] = 1.0;

            for (k = 0; k < 2; k++) {
                dbuffer[k + 1] = 0.0;
            }
            for (j = 0; j < indi; j++) {
                for (k = 0; k < 2; k++) {
                    dbuffer[k] = dbuffer[k + 1];
                }
                dbuffer[2] = 0.0;
                for (k = 0; k < 3; k++) {
                    dbuffer[k] += N[j] * b_b[k];
                }
                for (k = 0; k < 2; k++) {
                    dbuffer[k + 1] -= dbuffer[0] * b_a[k + 1];
                }
                p[j] = dbuffer[0];
            }
        }
    }

    //==============================================================================================
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "PPG (arbitrary units)");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setDrawCircles(false);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private LineDataSet createSet12() {

        LineDataSet set12 = new LineDataSet(null, "HRV (miliseconds)");
        set12.setAxisDependency(YAxis.AxisDependency.LEFT);
        set12.setColor(color);
        set12.setDrawCircles(false);
        set12.setCircleColor(Color.RED);
        set12.setLineWidth(2f);
        set12.setCircleRadius(4f);
        set12.setFillAlpha(65);
        set12.setFillColor(ColorTemplate.getHoloBlue());
        set12.setHighLightColor(Color.rgb(244, 117, 117));
        set12.setValueTextColor(Color.BLACK);
        set12.setValueTextSize(9f);
        set12.setDrawValues(false);
        return set12;
    }

    private LineDataSet createSet2() {

        LineDataSet set = new LineDataSet(null, "Time");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.RED);
        set.setDrawCircles(false);
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.RED);
        set.setDrawFilled(true);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    //==============================================================================================

    private void peaksPPG(List<Float> PPGsignal,List<Float> Timesignal){
        float numsteps = (float) 0;
        float numsteps2 = (float) 0;
        float threshold = (float) 15;
        boolean possiblepeak = false;
        float valuepossiblepeak = 0;
        float timepossiblepeak = 0;
        float valuepeak = 0;
        float timepeak = 0;
        float extra = (float) 0.1;
        List<Float> Ind = new ArrayList<Float>();

        for(int i=1;i<PPGsignal.size();i++){
            if(PPGsignal.get(i)>PPGsignal.get(i-1)){
                numsteps = numsteps + (float) 1;
            } else {
                if(numsteps>=threshold){
                    possiblepeak = true;
                    numsteps2 = numsteps;
                    valuepossiblepeak = PPGsignal.get(i-1);
                    timepossiblepeak = Timesignal.get(i-1);
                } else {
                    if(possiblepeak){
                        if(PPGsignal.get(i-1)>valuepossiblepeak){
                            valuepeak = PPGsignal.get(i-1);
                            timepeak = Timesignal.get(i-1);
                        } else {
                            valuepeak = valuepossiblepeak;
                            timepeak = timepossiblepeak;
                        }
                        Peaks.add(valuepeak);
                        TimePeaks.add(timepeak);
                        Ind.add((float)i);
                        threshold = (float)0.4*(float)numsteps2;
                        possiblepeak = false;
                        numsteps2 = 0;
                    }
                }
                numsteps = (float) 0;
            }
        }


        for(int x=1;x<TimePeaks.size();x++){
            if(TimePeaks.get(x)-TimePeaks.get(x-1)<(float)0.3){
                TimePeaks.remove(x);
                Peaks.remove(x);
                Ind.remove(x);
                x = x -1;
            }
        }


        for(int x=1;x<Peaks.size()-1;x++){
            float xx1 = Ind.get(x-1);
            float xx2 = Ind.get(x);
            float xx3 = Ind.get(x+1);
            float sum1 = 0;
            float sum2 = 0;

            for(int y = (int)xx1; y<=(int)xx2;y++){
                sum1 = sum1 + PPGsignal.get(y);
            }
            sum1 = sum1/(float) (xx2-xx1+1);

            for(int y = (int)xx2; y<=(int)xx3;y++){
                sum2 = sum2 + PPGsignal.get(y);
            }
            sum2 = sum2/(float) (xx3-xx2+1);

            if(Peaks.get(x)<sum1 && Peaks.get(x)<sum2){
                Peaks.remove(x);
                TimePeaks.remove(x);
                Ind.remove(x);
                x = x -1;
            }
        }


        float meanpeaks = 0;

        for(int h=1;h<TimePeaks.size();h++){
            HRPeaks.add((float)60/(TimePeaks.get(h)-TimePeaks.get(h-1)));
            meanpeaks = meanpeaks + (float)60/(TimePeaks.get(h)-TimePeaks.get(h-1));
        }

        meanpeaks = meanpeaks/HRPeaks.size();

        float valueto;

        if(meanpeaks>90){
            valueto = (float) 1;
        } else {
            valueto = (float) 1.3;
        }


        List<Float> lista = new ArrayList<Float>();

        for(int x=1;x<TimePeaks.size();x++){
            if(TimePeaks.get(x)-TimePeaks.get(x-1)>valueto){
                float d1 = Ind.get(x);
                float d2 = Ind.get(x-1);
                float enter = 0;
                for(int f = (int)d2+76; f<(int)d1-76;f++){
                    lista.add(PPGsignal.get(f));
                }

                float max = Collections.max(lista);
                int en;

                for(int f = (int)d2+76; f<(int)d1-76;f++){
                    if(PPGsignal.get(f) == max){
                        if(PPGsignal.get(f)>PPGsignal.get(f-1) && PPGsignal.get(f)>PPGsignal.get(f+1)){
                            enter = f;
                            en = (int) enter;
                            lista.clear();
                            TimePeaks.add(x,Timesignal.get(en));
                            Peaks.add(x,PPGsignal.get(en));
                            Ind.add(x,enter);
                            x = x - 1;
                        }
                    }
                }

            }
        }


        List<Float> lista2 = new ArrayList<Float>();


        for(int x=0;x<Peaks.size()-1;x++){
            float d12 = Ind.get(x);
            float d22 = Ind.get(x+1);
            float enter2 = 0;
            for(int f = (int)d12+1; f<(int)d22-76;f++){
                lista2.add(PPGsignal.get(f));
            }

            if(lista2.size()>1){
                float max2 = Collections.max(lista2);
                int en2;

                for(int f = (int)d12+1; f<(int)d22-76;f++){
                    if(PPGsignal.get(f) == max2){
                        if(PPGsignal.get(f)>PPGsignal.get(f-1) && PPGsignal.get(f)>PPGsignal.get(f+1)){
                            enter2 = f;
                            en2 = (int) enter2;
                            TimePeaks.set(x,Timesignal.get(en2));
                            Peaks.set(x,PPGsignal.get(en2));
                            Ind.set(x,enter2);
                        }
                    }
                }
            }

            lista2.clear();

        }


        HRPeaks.clear();

        for(int h=1;h<TimePeaks.size();h++){
            HRPeaks.add((float)60/(TimePeaks.get(h)-TimePeaks.get(h-1)));
        }

        TimePeaks.remove(0);

        double HRstd = 0;

        for(int h=0;h<HRPeaks.size();h++){
            HRmean = HRmean + HRPeaks.get(h);
        }

        HRmean = HRmean/HRPeaks.size();

        for(int h=0;h<HRPeaks.size();h++){
            HRstd = Math.pow(HRPeaks.get(h) - HRmean,2) + HRstd;
        }

        HRstd = Math.sqrt(HRstd/((double)HRPeaks.size()-(double)1));

        int sizeHRPeaks = HRPeaks.size();

        for(int i=0;i<sizeHRPeaks;i++){
            if(HRPeaks.get(i)>HRmean+0.5*HRstd || HRPeaks.get(i)<HRmean-0.5*HRstd){
                HRPeaks.remove(i);
                TimePeaks.remove(i);
                sizeHRPeaks = sizeHRPeaks - 1;
            }
        }

        HRmean = 0;

        for(int h=0;h<HRPeaks.size();h++){
            HRmean = HRmean + HRPeaks.get(h);
        }

        HRmean = HRmean/HRPeaks.size();

    }

    //==============================================================================================
    private void createAppBar(int appBarLayaout, int colorFondoAppBar, boolean upBoton) {
        Toolbar miAppBar = (Toolbar) findViewById(appBarLayaout);
        setSupportActionBar(miAppBar);
        miAppBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), colorFondoAppBar));
        miAppBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        String title = getResources().getString(R.string.app_name);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upBoton);
    }
    //==============================================================================================

    public boolean guardarSignal () {

        ArrayList<Float> BPVal = new ArrayList<>();
        ArrayList<Float> OXVal = new ArrayList<>();
        ArrayList<Float> HRVal = new ArrayList<>();
        ArrayList<Float> RRVal = new ArrayList<>();

        String HRETN = "";
        String RRETN = "";

        float HRETF = (int)HRmean;
        float RRETF = RRmean;



        String directorioPrincipal = Environment.getExternalStorageDirectory() + getResources().getString(R.string.archivo_directorioPrincipal);
        String directorioPaciente = miPaciente.getDirectorioPaciente();
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tf = new SimpleDateFormat("HH-mm-ss");
        String fecha = df.format(date);
        String hora = tf.format(date);
        String directorioSignal = "/Signal_" + fecha + "_" + hora + "/";
        String ruta = directorioPrincipal + directorioPaciente + directorioSignal;
        File rutaAlDirectorioPaciente = new File(ruta);
        Utilerias.crearDirectorio(rutaAlDirectorioPaciente);


        String nombreArchivo11 = "Signal_PPG_" + fecha + "_" + hora + ".txt";
        File f = new File(ruta, nombreArchivo11);
        Log.d("tag", ruta);
        OperacionesArray.guardarFloat(f, (ArrayList<Float>) SaveSignal);

        String nombreArchivo111 = "Signal_PPG_Time_" + fecha + "_" + hora + ".txt";
        File f11 = new File(ruta, nombreArchivo111);
        Log.d("tag", ruta);
        OperacionesArray.guardarFloat(f11, (ArrayList<Float>) SaveSignal2);

        String nombreArchivo112 = "Signal_HRV_" + fecha + "_" + hora + ".txt";
        File f12 = new File(ruta, nombreArchivo112);
        Log.d("tag", ruta);
        OperacionesArray.guardarFloat(f12, (ArrayList<Float>) SaveSignal3);

        String nombreArchivo113 = "Signal_HRV_Time_" + fecha + "_" + hora + ".txt";
        File f13 = new File(ruta, nombreArchivo113);
        Log.d("tag", ruta);
        OperacionesArray.guardarFloat(f13, (ArrayList<Float>) SaveSignal4);


        BPVal.add(SPETF);
        BPVal.add(DPETF);
        String nombreArchivo12 = "BP_Values_" + fecha + "_" + hora + ".txt";
        File f2 = new File(ruta, nombreArchivo12);
        OperacionesArray.guardarFloat(f2, BPVal);

        OXVal.add(OXETF);
        String nombreArchivo14 = "Oxygen_Value_" + fecha + "_" + hora + ".txt";
        File f4 = new File(ruta, nombreArchivo14);
        OperacionesArray.guardarFloat(f4, OXVal);

        HRVal.add(HRETF);
        String nombreArchivo15 = "Heart_Rate_Value_" + fecha + "_" + hora + ".txt";
        File f5 = new File(ruta, nombreArchivo15);
        OperacionesArray.guardarFloat(f5, HRVal);

        RRVal.add(RRETF);
        String nombreArchivo16 = "Respiratory_Rate_Value_" + fecha + "_" + hora + ".txt";
        File f6 = new File(ruta, nombreArchivo16);
        OperacionesArray.guardarFloat(f6, RRVal);


        Toast.makeText(BiosignalsCalc.this, "Data saved", Toast.LENGTH_LONG).show();

            return true;


    }

    public static void transform(double[] real, double[] imag) {
        int n = real.length;
        if (n != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        if (n == 0)
            return;
        else if ((n & (n - 1)) == 0)  // Is power of 2
            transformRadix2(real, imag);
        else  // More complicated algorithm for arbitrary sizes
            transformBluestein(real, imag);
    }


    /*
     * Computes the inverse discrete Fourier transform (IDFT) of the given complex vector, storing the result back into the vector.
     * The vector can have any length. This is a wrapper function. This transform does not perform scaling, so the inverse is not a true inverse.
     */
    public static void inverseTransform(double[] real, double[] imag) {
        transform(imag, real);
    }


    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     * The vector's length must be a power of 2. Uses the Cooley-Tukey decimation-in-time radix-2 algorithm.
     */
    public static void transformRadix2(double[] real, double[] imag) {
        // Length variables
        int n = real.length;
        if (n != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        int levels = 31 - Integer.numberOfLeadingZeros(n);  // Equal to floor(log2(n))
        if (1 << levels != n)
            throw new IllegalArgumentException("Length is not a power of 2");

        // Trigonometric tables
        double[] cosTable = new double[n / 2];
        double[] sinTable = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            cosTable[i] = Math.cos(2 * Math.PI * i / n);
            sinTable[i] = Math.sin(2 * Math.PI * i / n);
        }

        // Bit-reversed addressing permutation
        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Cooley-Tukey decimation-in-time radix-2 FFT
        for (int size = 2; size <= n; size *= 2) {
            int halfsize = size / 2;
            int tablestep = n / size;
            for (int i = 0; i < n; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    int l = j + halfsize;
                    double tpre =  real[l] * cosTable[k] + imag[l] * sinTable[k];
                    double tpim = -real[l] * sinTable[k] + imag[l] * cosTable[k];
                    real[l] = real[j] - tpre;
                    imag[l] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }
            if (size == n)  // Prevent overflow in 'size *= 2'
                break;
        }
    }


    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     * The vector can have any length. This requires the convolution function, which in turn requires the radix-2 FFT function.
     * Uses Bluestein's chirp z-transform algorithm.
     */
    public static void transformBluestein(double[] real, double[] imag) {
        // Find a power-of-2 convolution length m such that m >= n * 2 + 1
        int n = real.length;
        if (n != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        if (n >= 0x20000000)
            throw new IllegalArgumentException("Array too large");
        int m = Integer.highestOneBit(n) * 4;

        // Trigonometric tables
        double[] cosTable = new double[n];
        double[] sinTable = new double[n];
        for (int i = 0; i < n; i++) {
            int j = (int)((long)i * i % (n * 2));  // This is more accurate than j = i * i
            cosTable[i] = Math.cos(Math.PI * j / n);
            sinTable[i] = Math.sin(Math.PI * j / n);
        }

        // Temporary vectors and preprocessing
        double[] areal = new double[m];
        double[] aimag = new double[m];
        for (int i = 0; i < n; i++) {
            areal[i] =  real[i] * cosTable[i] + imag[i] * sinTable[i];
            aimag[i] = -real[i] * sinTable[i] + imag[i] * cosTable[i];
        }
        double[] breal = new double[m];
        double[] bimag = new double[m];
        breal[0] = cosTable[0];
        bimag[0] = sinTable[0];
        for (int i = 1; i < n; i++) {
            breal[i] = breal[m - i] = cosTable[i];
            bimag[i] = bimag[m - i] = sinTable[i];
        }

        // Convolution
        double[] creal = new double[m];
        double[] cimag = new double[m];
        convolve(areal, aimag, breal, bimag, creal, cimag);

        // Postprocessing
        for (int i = 0; i < n; i++) {
            real[i] =  creal[i] * cosTable[i] + cimag[i] * sinTable[i];
            imag[i] = -creal[i] * sinTable[i] + cimag[i] * cosTable[i];
        }
    }


    /*
     * Computes the circular convolution of the given real vectors. Each vector's length must be the same.
     */
    public static void convolve(double[] xvec, double[] yvec, double[] outvec) {
        int n = xvec.length;
        if (n != yvec.length || n != outvec.length)
            throw new IllegalArgumentException("Mismatched lengths");
        convolve(xvec, new double[n], yvec, new double[n], outvec, new double[n]);
    }


    /*
     * Computes the circular convolution of the given complex vectors. Each vector's length must be the same.
     */
    public static void convolve(double[] xreal, double[] ximag,
                                double[] yreal, double[] yimag, double[] outreal, double[] outimag) {

        int n = xreal.length;
        if (n != ximag.length || n != yreal.length || n != yimag.length
                || n != outreal.length || n != outimag.length)
            throw new IllegalArgumentException("Mismatched lengths");

        xreal = xreal.clone();
        ximag = ximag.clone();
        yreal = yreal.clone();
        yimag = yimag.clone();
        transform(xreal, ximag);
        transform(yreal, yimag);

        for (int i = 0; i < n; i++) {
            double temp = xreal[i] * yreal[i] - ximag[i] * yimag[i];
            ximag[i] = ximag[i] * yreal[i] + xreal[i] * yimag[i];
            xreal[i] = temp;
        }
        inverseTransform(xreal, ximag);

        for (int i = 0; i < n; i++) {  // Scaling (because this FFT implementation omits it)
            outreal[i] = xreal[i] / n;
            outimag[i] = ximag[i] / n;
        }
    }

}