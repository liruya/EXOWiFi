package com.exoterra.comman;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

/**
 * Created by liruya on 2018/4/13.
 */

public class PrefUtil
{
    private static final String TAG = "PrefUtil";

    public static void putBoolean( Context context, String file, String key, boolean value )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit().putBoolean( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
    }

    public static void putInt( Context context, String file, String key, int value )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit().putInt( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
    }

    public static void putFloat( Context context, String file, String key, float value )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit().putFloat( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
    }

    public static void putLong( Context context, String file, String key, long value )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit().putLong( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
    }

    public static void putString( Context context, String file, String key, String value )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sp.edit().putString( key, value );
        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
    }

    public static void putObject( Context context, String file, String key, Object object )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        if ( object == null )
        {
            SharedPreferences.Editor editor = sp.edit().remove( key );
            SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream( baos );
            oos.writeObject( object );
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        String objectStr = new String( Base64.encode( baos.toByteArray(), Base64.DEFAULT ));
        try
        {
            baos.close();
            oos.close();
        } catch ( IOException e )
        {
            e.printStackTrace();
        }
        SharedPreferences.Editor editor = sp.edit().putString( key, objectStr );
        SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
    }

    public static boolean getBoolean( Context context, String file, String key, boolean def )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        return sp.getBoolean( key, def );
    }

    public static int getInt( Context context, String file, String key, int def )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        return sp.getInt( key, def );
    }

    public static float getFloat( Context context, String file, String key, float def )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        return sp.getFloat( key, def );
    }

    public static long getLong( Context context, String file, String key, long def )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        return sp.getLong( key, def );
    }

    public static String getString( Context context, String file, String key, String def )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        return sp.getString( key, def );
    }

    public static Object getObject( Context context, String file, String key )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        String objectStr = sp.getString( key, "" );
        if ( objectStr == null || objectStr.equals( "" ) )
        {
            return null;
        }
        byte[] objBytes = Base64.decode( objectStr.getBytes(), Base64.DEFAULT );
        ByteArrayInputStream bais = new ByteArrayInputStream( objBytes );
        try
        {
            ObjectInputStream ois = new ObjectInputStream( bais );
            Object object = ois.readObject();
            bais.close();
            ois.close();
            return object;
        } catch ( IOException e )
        {
            e.printStackTrace();
        } catch ( ClassNotFoundException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void remove( Context context, String file, String key )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        if ( sp.contains( key ) )
        {
            SharedPreferences.Editor editor = sp.edit().remove( key );
            SharedPreferencesCompat.EditorCompat.getInstance().apply( editor );
        }
    }

    public static Set<String> getAllKeys( Context context, String file )
    {
        SharedPreferences sp = context.getSharedPreferences( file, Context.MODE_PRIVATE );
        return sp.getAll().keySet();
    }
}
