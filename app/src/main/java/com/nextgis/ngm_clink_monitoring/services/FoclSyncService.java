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

package com.nextgis.ngm_clink_monitoring.services;

import android.content.Context;
import com.nextgis.maplib.datasource.ngw.SyncAdapter;
import com.nextgis.maplib.service.NGWSyncService;
import com.nextgis.ngm_clink_monitoring.adapters.FoclSyncAdapter;


public class FoclSyncService
        extends NGWSyncService
{
    @Override
    protected SyncAdapter createSyncAdapter(
            Context context,
            boolean autoInitialize)
    {
        return new FoclSyncAdapter(context, autoInitialize);
    }
}
