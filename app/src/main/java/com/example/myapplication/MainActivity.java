package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/*import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;*/

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    final Context context = MainActivity.this;
    private Button button,buttonB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlDatos c = new sqlDatos(MainActivity.this);
        c.tablas();

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        initQR();

        button = (Button) findViewById(R.id.btnDetalle);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialogNotificacion();
            }
        } );

        buttonB = (Button) findViewById(R.id.btnBorrar);
        buttonB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sqlDatos c = new sqlDatos(MainActivity.this);
                c.borrarDetalle();
            }
        } );
    }

    public void initQR() {

        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.i("token", token);

                        sqlDatos c = new sqlDatos(MainActivity.this);
                        c.insertQR(token,"-gps","prueba");

                        /*
                        if (URLUtil.isValidUrl(token)) {
                            // si es una URL valida abre el navegador
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(token));
                            startActivity(browserIntent);
                        } else {
                            // comparte en otras apps
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, token);
                            shareIntent.setType("text/plain");
                            startActivity(shareIntent);
                        } */

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            }
        });

    }


    public void dialogNotificacion() {
        int i;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this , R.style.Theme_AppCompat_DayNight_Dialog);
        LayoutInflater inflater = LayoutInflater.from(this);

        View alertView = inflater.inflate(R.layout.detalle, null);
        builder.setTitle("D E T A L L E");
        builder.setView(alertView);

        TableLayout stk = (TableLayout) alertView.findViewById(R.id.tabla);
        TableRow tbrow0 = new TableRow(context);

        tbrow0.addView( celda("Fecha", Color.WHITE,Gravity.CENTER,"#3d455b"));
        tbrow0.addView( celda("Lugar", Color.WHITE,Gravity.CENTER,"#3d455b"));
        stk.addView(tbrow0);

        sqlDatos c = new sqlDatos(MainActivity.this);
        ArrayList<listaOrm> ls = (ArrayList<listaOrm>) c.ltDetalle();

        int color = 0;
        if (ls.size() > 0) {
            for (listaOrm e: ls) {
                TableRow tbrow = new TableRow(context);
                //tbrow.setId(10);
                tbrow.setBackgroundColor(Color.GRAY);
                tbrow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                celdaDet(tbrow);

                tbrow.addView(celdaDet(e.getFecha().toString(), Color.BLACK,Gravity.LEFT,"#FFFFFF",color));
                tbrow.addView(celdaDet(e.getLugar(), Color.BLACK,Gravity.LEFT,"#FFF7A2",color));

                /*if ( color % 2 == 0) {
                    tbrow.setBackgroundColor(Color.parseColor("#FFFFFF"));//LINEA BLANCA
                }
                else {
                    tbrow.setBackgroundColor(Color.parseColor("#FFF7A2"));//LINEA GRIS
                } */

                color ++;
                //stk.addView(tbrow);
                stk.addView(tbrow, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            }
        }

        builder.setCancelable(true);
        builder.setNeutralButton("Regresar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public TextView celda(String _texto, int _cTexto, int _alineacion, String _cBack) {

        TextView _tv = new TextView(context);
        _tv.setText(_texto);
        _tv.setTextColor(_cTexto);
        _tv.setGravity(_alineacion);
        _tv.setBackgroundColor(Color.parseColor(_cBack));

        return _tv;
    }

    public TextView celdaDet(String _texto, int _cTexto, int _alineacion, String _cBack, int _color) {

        TextView _tv = new TextView(context);
        _tv.setText(_texto);
        _tv.setTextColor(_cTexto);
        _tv.setGravity(_alineacion);
        if ( _color % 2 == 0) {
            _tv.setBackgroundResource(R.drawable.border1);
        }
        else {
            _tv.setBackgroundResource(R.drawable.border2);
        }

        //_tv.setLayoutParams(_tlparams);
        //_tv.setBackgroundColor(Color.parseColor(_cBack));
        _tv.setPadding(5, 5, 5, 5);

        return _tv;
    }

    public void celdaDet(TableRow _row) {
        _row = new TableRow(context);
        TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        _row.setLayoutParams(tlparams);
    }


}