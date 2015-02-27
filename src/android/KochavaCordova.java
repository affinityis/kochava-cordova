/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Affinity Influencing Systems
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.kochava.sdk.cordova;

import com.kochava.android.tracker.*;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LinearLayoutSoftKeyboardDetect;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import android.view.View;
import java.util.Iterator;

/**
 * This class defines the native implementation of the AdColony Cordova plugin.
 */
public class KochavaCordova extends CordovaPlugin implements AdColonyAdListener, AdColonyAdAvailabilityListener, AdColonyV4VCListener {

	private static final String TAG = "KochavaCordova";
	/** Cordova Actions */
	private static final String ACTION_INITIALIZE = "initializeKochava";
	private static final String ACTION_TRACK_EVENT = "trackEvent";

	private static Feature kTracker;
	private boolean _hasInitialized;

	@Override
	public boolean execute(String action, JSONArray inputs, CallbackContext callbackContext) throws JSONException
	{
		try {
			if (action.equals(ACTION_INITIALIZE)) {
				if (_hasInitialized) return false;
				execInitialize(inputs, callbackContext);
				return true;
			} else if (action.equals(ACTION_TRACK_EVENT)) {
				execTrackEvent(inputs, callbackContext);
				return true;
			} else {
				return false;
			}
		} catch (JSONException exception) {
			callbackContext.error(exception.getMessage());
			return false;
		}
	}

	/** Private Methods */

	private void execInitialize(JSONArray inputs, CallbackContext callbackContext) throws JSONException {
		String appToken = inputs.getString(0);
		boolean debug = data.getBoolean(1);

		// Enable debug logging
		Feature.enableDebug(debug);
		Feature.setErrorDebug(debug);

		// Initialize Kochava
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(Feature.INPUTITEMS.KOCHAVA_APP_ID, appToken);
		// datamap.put(Feature.INPUTITEMS.CURRENCY , Feature.CURRENCIES.EUR);
		kTracker = new Feature(this.cordova.getActivity(), dataMap);

		_hasInitialized = true;
		Log.d(TAG, "Initialized with "+ appToken);
		callbackContext.success();
	}

	private void execTrackEvent(JSONArray inputs, CallbackContext callbackContext) throws JSONException {
		String viewToken = inputs.getString(0);
		String eventToken = inputs.getString(0);
		if (_hasInitialized) {
			kTracker.event(viewToken, eventToken);
			Log.d(TAG, "Event with "+ viewToken +": "+ eventToken);
			callbackContext.success();
		} else {
			callbackContext.error();
		}
	}

}
