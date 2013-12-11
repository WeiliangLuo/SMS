package com.example.sms;

/**
 * Global Constants
 *
 **/
public class Constant {
	/**
	 *  Id of new message notification in status bar 
	 **/
	public static final int NEW_SMS_NOTIFICATION_ID = 7758258;
	/**
	 *  Id of new message notification in status bar 
	 **/
	public static final int SCHEDULED_SMS_NOTIFICATION_ID = 7758259;
	/**
	 *  String identifier of SMS received action in Android
	 **/
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	/**
	 *  Repeat pattern for scheduled message: None
	 **/
	public static final int REPEAT_NONE = 0;
	/**
	 *  Repeat pattern for scheduled message: Daily
	 **/
	public static final int REPEAT_DAILY = 1;
	/**
	 *  Repeat pattern for scheduled message: Weekly
	 **/
	public static final int REPEAT_WEEKLY = 2;
	/**
	 *  Repeat pattern for scheduled message: Monthly
	 **/
	public static final int REPEAT_MONTHLY = 3;
	/**
	 *  Repeat pattern for scheduled message: Yearly
	 **/
	public static final int REPEAT_YEARLY = 4;

}
