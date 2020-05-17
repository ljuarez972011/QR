package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class sqlDatos {

    Context mContext;
    String text = null;

    public sqlDatos() {
    }


    public sqlDatos(Context mContext) {
        this.mContext = mContext;
        tablas();
    }

    public void tablas() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(mContext, Configuracion.BD, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        bd.execSQL("create table if not exists QR_LOGIN(fecha timestamp, lugar text, gps text, imei text )");

        bd.close();
    }

    public void insertQR(String _lugar, String _gps, String _imei) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(mContext, Configuracion.BD, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues values = new ContentValues();
        Date fecha = new Date();
        //values.put("FECHA", " time('now') "  );
        values.put("FECHA", Long.toString(fecha.getTime()) );
        values.put("LUGAR", _lugar);
        values.put("GPS", _gps);
        values.put("IMEI", _imei);

        db.insert("QR_LOGIN", null, values);
        db.close();

    }

    /***********************************************************************************************
     *      BOPRRAR DATOS
     **********************************************************************************************/

    public void borrarDetalle() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(mContext, Configuracion.BD, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        bd.execSQL("delete from QR_LOGIN ");
    }

    /***********************************************************************************************
     *      D E V O L V E R  L I S T A S
     **********************************************************************************************/

    public List<listaOrm> ltDetalle() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(mContext, Configuracion.BD, null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor f = bd.rawQuery(
                "select fecha, lugar, gps, imei from QR_LOGIN order by 1 ", null);

        List<listaOrm> lista = new ArrayList<>();

        if (f.moveToFirst()) {
            do {

                //Timestamp date = StringFechaHora(f.getString(0));
                Date date = StringFecha(f.getString(0));
                lista.add(new listaOrm(new Timestamp(f.getLong(0)), f.getString(1), f.getString(2), f.getString(3) ));

            } while (f.moveToNext());
        } else {
            Toast.makeText(mContext, "No Hay registros", Toast.LENGTH_SHORT).show();
        }

        bd.close();
        return lista;
    }


    /***********************************************************************************************
     *      F U N C I O N E S  V A R I A S
     **********************************************************************************************/

    public Date StringFecha(String pFeccha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date d = null;
        try {
            d = dateFormat.parse(pFeccha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  d;
    }

    public Date StringFecha2(String pFeccha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = dateFormat.parse(pFeccha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  d;
    }

    public Timestamp StringFechaHora(String pFeccha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Timestamp d = null;
        try {
            d =  new Timestamp( dateFormat.parse(pFeccha).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  d;
    }

    public String timeString (Timestamp pFecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
       String x = null;
        x = dateFormat.format(pFecha);
        return  x;
    }


    public String timeHString (Timestamp pFecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String x = null;
        x = dateFormat.format(pFecha);
        return  x;
    }


}
