/*
 * Project:  NextGIS mobile apps for Compulink
 * Purpose:  Mobile GIS for Android
 * Authors:  Dmitry Baryshnikov (aka Bishop), polimax@mail.ru
 *           NikitaFeodonit, nfeodonit@yandex.com
 * *****************************************************************************
 * Copyright (C) 2014-2015 NextGIS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.ngm_clink_monitoring.map;

import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.nextgis.maplib.api.IStyleRule;
import com.nextgis.maplib.display.RuleFeatureRenderer;
import com.nextgis.maplib.display.Style;
import com.nextgis.maplib.map.NGWVectorLayer;
import com.nextgis.maplib.util.Constants;
import com.nextgis.maplib.util.FeatureChanges;
import com.nextgis.ngm_clink_monitoring.GISApplication;
import com.nextgis.ngm_clink_monitoring.util.FoclConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import static com.nextgis.maplib.util.Constants.TAG;


public class FoclVectorLayer
        extends NGWVectorLayer
{
    protected static final String JSON_FOCL_TYPE_KEY = "focl_type";

    protected int mFoclLayerType;


    public FoclVectorLayer(
            Context context,
            File path)
    {
        super(context, path);
        mLayerType = FoclConstants.LAYERTYPE_FOCL_VECTOR;
    }


    public static int getFoclLayerTypeFromString(String type)
    {
        if (type.equals(FoclConstants.JSON_OPTICAL_CABLE_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_OPTICAL_CABLE;
        }
        if (type.equals(FoclConstants.JSON_FOSC_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_FOSC;
        }
        if (type.equals(FoclConstants.JSON_OPTICAL_CROSS_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_OPTICAL_CROSS;
        }
        if (type.equals(FoclConstants.JSON_ACCESS_POINT_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_ACCESS_POINT;
        }
        if (type.equals(FoclConstants.JSON_SPECIAL_TRANSITION_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_SPECIAL_TRANSITION;
        }

        if (type.equals(FoclConstants.JSON_REAL_OPTICAL_CABLE_POINT_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_REAL_OPTICAL_CABLE_POINT;
        }
        if (type.equals(FoclConstants.JSON_REAL_FOSC_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_REAL_FOSC;
        }
        if (type.equals(FoclConstants.JSON_REAL_OPTICAL_CROSS_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_REAL_OPTICAL_CROSS;
        }
        if (type.equals(FoclConstants.JSON_REAL_ACCESS_POINT_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_REAL_ACCESS_POINT;
        }
        if (type.equals(FoclConstants.JSON_REAL_SPECIAL_TRANSITION_POINT_VALUE)) {
            return FoclConstants.LAYERTYPE_FOCL_REAL_SPECIAL_TRANSITION_POINT;
        }

        return FoclConstants.LAYERTYPE_FOCL_UNKNOWN;
    }


    public int getFoclLayerType()
    {
        return mFoclLayerType;
    }


    public void setFoclLayerType(int foclLayerType)
    {
        mFoclLayerType = foclLayerType;
    }


    @Override
    protected Style getDefaultStyle()
            throws Exception
    {
        return FoclStyleRule.getDefaultStyle(mFoclLayerType);
    }


    @Override
    protected void setDefaultRenderer()
    {
        try {
            Style style = getDefaultStyle();
            IStyleRule rule = getStyleRule();
            mRenderer = new RuleFeatureRenderer(this, rule, style);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            mRenderer = null;
        }
    }


    protected IStyleRule getStyleRule()
    {
        return new FoclStyleRule(mContext, getPath().getName(), mFoclLayerType);
    }


    @Override
    public JSONObject toJSON()
            throws JSONException
    {
        JSONObject rootConfig = super.toJSON();
        rootConfig.put(JSON_FOCL_TYPE_KEY, mFoclLayerType);
        return rootConfig;
    }


    @Override
    public void fromJSON(JSONObject jsonObject)
            throws JSONException, SQLiteException
    {
        mFoclLayerType = jsonObject.getInt(JSON_FOCL_TYPE_KEY);
        super.fromJSON(jsonObject);
    }


    @Override
    public void sync(
            String authority,
            SyncResult syncResult)
    {
        syncResult.clear();

        if (0 != (mSyncType & Constants.SYNC_NONE) || mFields == null || mFields.isEmpty()) {
//            if (Constants.DEBUG_MODE) {
                Log.d(
                        Constants.TAG,
                        "Layer " + getName() + " is not checked to sync or not inited");
//            }
            return;
        }

        GISApplication app = (GISApplication) mContext.getApplicationContext();

        // 1. get remote changes
        if (app.isFullSync() && !getChangesFromServer(authority, syncResult)) {
//            if (Constants.DEBUG_MODE) {
                Log.d(Constants.TAG, "Get remote changes failed");
//            }
            return;
        }

        if (isRemoteReadOnly()) {
            return;
        }

        // 2. send current changes
        if (!sendLocalChanges(syncResult)) {
//            if (Constants.DEBUG_MODE) {
                Log.d(Constants.TAG, "Set local changes failed");
//            }
            //return;
        }

        // for debug
//        switch (getPath().getName()) {
//            case "layer_2015081018082514":
//            case "layer_2015081018082857":
//            case "layer_2015081018083084":
//            case "layer_20150810180823055":
//            case "layer_20150810180826243":
//            case "layer_20150810180827365":
//            case "layer_20150810180827495":
//            case "layer_20150810180828663":
//            case "layer_20150810180829798":
//            case "layer_20150810180830981":
//                Log.d(
//                        TAG, "SEND, path name: " + getPath().getName() + " - line name: " +
//                                (null == mParent ? "null" : mParent.getName()) + " - layer name: " +
//                                getName());
//
//                if (!sendLocalChanges(syncResult)) {
//                    Log.d(TAG, "Set local changes failed");
//                    //return;
//                }
//                break;
//
//            default:
//                Log.d(
//                        TAG, "NOT SEND, path name: " + getPath().getName() + " - line name: " +
//                                (null == mParent ? "null" : mParent.getName()) + " - layer name: " +
//                                getName());
//                break;
//        }
    }


    public boolean isChanges()
    {
        return FeatureChanges.isChanges(getChangeTableName());
    }
}
