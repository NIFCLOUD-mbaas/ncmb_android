/*
 * Copyright 2017 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nifty.cloud.mb.core;

/**
 * NCMBDialogPushConfiguration is used to setting of dialog push notification
 * 
 */
public class NCMBDialogPushConfiguration {
	/**
	 * display format nothing to display
	 */
	public static final int DIALOG_DISPLAY_NONE = 0x00;
	/**
	 * display format that display dialog
	 */
	public static final int DIALOG_DISPLAY_DIALOG = 0x01;
	/**
	 * display format that display dialog with original background image
	 */
	public static final int DIALOG_DISPLAY_BACKGROUND = 0x02;
	/**
	 * display format that display original layout dialog
	 */
	public static final int DIALOG_DISPLAY_ORIGINAL = 0x04;

	// display format
	private int displayType;

	/**
	 * Costructor<br>
	 * default display formati is DIALOG_DISPLAY_NONE <br>
	 *
	 */
	public NCMBDialogPushConfiguration(){
		//デフォルト非表示
		this.displayType = DIALOG_DISPLAY_NONE;
	}

	/**
	 * Constructor <br>
	 *
	 * @param displayType display format
	 */
	public NCMBDialogPushConfiguration(int displayType){
		this.displayType = displayType;
	}

	/**
	 * set the display format
	 *
	 * @param displayType setting of display format
	 */
	public void setDisplayType(int displayType){
		this.displayType = displayType;
	}

	/**
	 * get the dislay format setting
	 *
	 * @return curernt display format
	 */
	public int getDisplayType(){
		return this.displayType;
	}

}
