/*******************************************************************************
 * Project:  NextGIS mobile apps for Compulink
 * Purpose:  Mobile GIS for Android
 * Authors:  Dmitry Baryshnikov (aka Bishop), polimax@mail.ru
 *           NikitaFeodonit, nfeodonit@yandex.com
 * *****************************************************************************
 * Copyright (C) 2014-2015 NextGIS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.nextgis.ngm_clink_monitoring.adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nextgis.maplib.map.VectorLayer;
import com.nextgis.ngm_clink_monitoring.R;
import com.nextgis.ngm_clink_monitoring.util.FoclConstants;


public class ObjectCursorAdapter
        extends CursorAdapter
{
    private static LayoutInflater mInflater = null;


    public ObjectCursorAdapter(
            Context context,
            Cursor c,
            int flags)
    {
        super(context, c, flags);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public static String getObjectName(Cursor cursor)
    {
        String id = cursor.getString(cursor.getColumnIndex(VectorLayer.FIELD_ID));
        String name = cursor.getString(cursor.getColumnIndex(FoclConstants.FIELD_NAME));

        String objectName;

        if (id.length() == 1) {
            id = "0" + id;
        }

        objectName = id;

        if (null != name) {
            objectName += " - " + name;
        }

        return objectName;
    }


    @Override
    public View newView(
            Context context,
            Cursor cursor,
            ViewGroup viewGroup)
    {
        View view = mInflater.inflate(R.layout.item_object_name, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        view.setTag(viewHolder);

        viewHolder.mObjectName = (TextView) view.findViewById(R.id.item_object_name);
        viewHolder.mObjectStatus = (TextView) view.findViewById(R.id.item_object_status);

        return view;
    }


    @Override
    public void bindView(
            View view,
            Context context,
            Cursor cursor)
    {
        if (null == view) {
            view = mInflater.inflate(R.layout.item_object_name, null);
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.mObjectName.setText(getObjectName(cursor));

        String status = cursor.getString(cursor.getColumnIndex(FoclConstants.FIELD_STATUS_BUILT));

        Drawable background;

        switch (status) {
            case FoclConstants.FIELD_VALUE_PROJECT:
                viewHolder.mObjectStatus.setText(mContext.getString(R.string.project));
            default:
                background = mContext.getResources().getDrawable(R.drawable.border_status_red);
                break;

            case FoclConstants.FIELD_VALUE_BUILT:
                viewHolder.mObjectStatus.setText(mContext.getString(R.string.built));
                background = mContext.getResources().getDrawable(R.drawable.border_status_green);
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewHolder.mObjectStatus.setBackground(background);
        } else {
            viewHolder.mObjectStatus.setBackgroundDrawable(background);
        }
    }


    public static class ViewHolder
    {
        public TextView mObjectName;
        public TextView mObjectStatus;
    }
}