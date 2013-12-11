package com.example.sms.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sms.Alarm;
import com.example.sms.Constant;
import com.example.sms.Contact;
import com.example.sms.ContactManager;
import com.example.sms.Message;
import com.example.sms.MessageManager;
import com.example.sms.R;

public class ScheduleSMSActivity extends FragmentActivity implements OnClickListener, OnTimeSetListener, OnDateSetListener {
	private RelativeLayout layout_date;
	private RelativeLayout layout_time;
	private EditText et_to;
	private EditText et_content;
	
	private ImageButton btn_add;
	private Button btn_send;
	private Button btn_date;
	private Button btn_time;
	
	private TextView tv_date;
	private TextView tv_time;
	
	private Spinner sp_repeat;
	
	private int repeat;
	private int year;
	private int month;
	private int day;
	private int dayOfWeek;
	private int hour;
	private int minute;
	
	private Message msg = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_sms);
		
		repeat = 0;
		layout_date = (RelativeLayout) findViewById(R.id.date_picker);
		layout_time = (RelativeLayout) findViewById(R.id.time_picker);
		et_to = (EditText) findViewById(R.id.text_to);
		et_content = (EditText) findViewById(R.id.text_content);
		et_content.addTextChangedListener(contentWatcher);
		
		btn_add = (ImageButton) findViewById(R.id.btn_add);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_date = (Button) findViewById(R.id.btn_date);
		btn_time = (Button) findViewById(R.id.btn_time);
		btn_add.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_date.setOnClickListener(this);
		btn_time.setOnClickListener(this);
		btn_send.setEnabled(false);

		
		tv_date = (TextView) findViewById(R.id.text_date);
		tv_time = (TextView) findViewById(R.id.text_time);
		
		sp_repeat = (Spinner) findViewById(R.id.spinner_repeat);
		String[] repeatArray = getResources().getStringArray(R.array.repeat_options);
		List<String> repeatOptions = new ArrayList<String>(Arrays.asList(repeatArray));
		
		ArrayAdapter<String> rep_adp = new ArrayAdapter<String> (this,android.R.layout.simple_dropdown_item_1line, repeatOptions);
		rep_adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
	    sp_repeat.setAdapter(rep_adp);
	    sp_repeat.setOnItemSelectedListener(
	    		new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int position, long id) {
						repeat = position;
						updateDateTimeUI();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						
					}
	    		});
	    
	   Intent in = getIntent();
	   Bundle b = in.getExtras();
	   if(b != null){
		   long id = b.getLong("id");
		   msg = MessageManager.getScheduledMessageById(this, id);
		   initTime(b.getLong("timestamp"));
		   
		   String address = b.getString("address");
		   Contact contact = ContactManager.getContactByNumber(this, address);
		   if(contact.getNameOrNumber() == contact.getPhoneNumber()){
			   et_to.setText(address);
		   }
		   else{
			   et_to.setText(String.format("%s <%s>", contact.getName(), address));
		   }
		   et_content.setText(b.getString("body"));
		   repeat = b.getInt("repeat");
		   updateDateTimeUI();
		   sp_repeat.setSelection(repeat);
	   }
	   else{
		   initTime(System.currentTimeMillis());
	   }
	   
	   this.setTitle("NewSchedule");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule_sms_menu, menu);
		if(msg==null){
			return false;
		}
		return true;
	}

	/**
	 *	Handle click on action bar 
	 * 
	 **/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_delete:
	        	AlertDialog.Builder alert = new AlertDialog.Builder(ScheduleSMSActivity.this);
	        	alert.setTitle("Alert");
	        	alert.setMessage("Configured SMS will be discarded permanently!");
	        	alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			// delete this scheduled sms
	        			msg.delete(ScheduleSMSActivity.this);
	        			finish();
	        		}
	        	});
	        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			// do nothing
	        		}
	        	});
	        	alert.show();

	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void initTime(long time){
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);
		year = date.get(Calendar.YEAR);
		month = date.get(Calendar.MONTH);
		day = date.get(Calendar.DAY_OF_MONTH);
		dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
		hour = date.get(Calendar.HOUR_OF_DAY);
		minute = date.get(Calendar.MINUTE);
	}
	
	private String getWeekday(int dayOfWeek){
		switch(dayOfWeek){
		case 1:
			return "Sun";
			
		case 2:
			return "Mon";
			
		case 3:
			return "Tue";
			
		case 4:
			return "Wed";
			
		case 5:
			return "Thu";
			
		case 6:
			return "Fri";
			
		case 7:
			return "Sat";
		
		default:
			return "Not possible";
		}
	}

	@Override
	public void onClick(View v) {
		if(v.equals(btn_add)){
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
			startActivityForResult(intent, 1);
		}
		else if(v.equals(btn_date)){
			// choose a week day
			if(repeat == Constant.REPEAT_WEEKLY){
			    AlertDialog.Builder alert = new Builder(this);
			    alert.setTitle("Select day of the week");
			    alert.setItems(R.array.day_of_week, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dayOfWeek = which+1;
						updateDateTimeUI();
					}
				});
			    alert.show();
			}
			// choose month-day
			else{
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");				
			}
		}
		else if(v.equals(btn_time)){
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getFragmentManager(), "timePicker");			
		}
		else if(v.equals(btn_send)){
			if(sendScheduledMessage()){
				finish();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(data!=null){
			Uri uri = data.getData();
			
	        if (uri != null) {
	            Cursor c = null;
	            try {
	                c = getContentResolver().query(uri, new String[]{ 
	                            ContactsContract.CommonDataKinds.Phone.NUMBER,  
	                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY },
	                        null, null, null);

	                if (c != null && c.moveToFirst()) {
	                    String number = c.getString(0);
	                    String name = c.getString(1);
	                    String recipent = String.format("%s <%s>", name, number);
	                    et_to.setText(recipent);
	                }
	            } finally {
	                if (c != null) {
	                    c.close();
	                }
	            }
	        }
		}
	}
	
	public static class TimePickerFragment extends DialogFragment {
	    private Activity mActivity;
	    private OnTimeSetListener mListener;

	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mActivity = activity;
	        // This error will remind you to implement an OnTimeSetListener 
	        //   in your Activity if you forget
	        try {
	            mListener = (OnTimeSetListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
	        }
	    }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(mActivity, mListener, hour, minute, true);
		}
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	    this.hour = hourOfDay;
	    this.minute = minute;
	    updateDateTimeUI();
	}

	public static class DatePickerFragment extends DialogFragment {
	    private Activity mActivity;
	    private OnDateSetListener mListener;

	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mActivity = activity;

	        // This error will remind you to implement an OnTimeSetListener 
	        //   in your Activity if you forget
	        try {
	            mListener = (OnDateSetListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
	        }
	    }
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(mActivity, mListener, year, month, day);
		}
	}

	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		this.year = year;
		this.month = monthOfYear;
		this.day = dayOfMonth;
		updateDateTimeUI();
	}

	public void updateDateTimeUI(){
		switch(repeat){
		case Constant.REPEAT_NONE:		// No
			layout_date.setVisibility(View.VISIBLE);
			layout_time.setVisibility(View.VISIBLE);
			tv_date.setText(String.format("%4s-%02d-%02d", year, month+1, day));
			tv_time.setText(String.format("%02d:%02d", hour, minute));
			break;
			
		case Constant.REPEAT_DAILY:		// Daily
			layout_date.setVisibility(View.GONE);
			tv_time.setText(String.format("%02d:%02d every day", hour, minute));
			break;
			
		case Constant.REPEAT_WEEKLY:		// Weekly
			layout_date.setVisibility(View.VISIBLE);
			layout_time.setVisibility(View.VISIBLE);
			tv_date.setText(getWeekday(dayOfWeek)+" every week");
			tv_time.setText(String.format("%02d:%02d", hour, minute));
			break;
			
		case Constant.REPEAT_MONTHLY:		// Monthly
			layout_date.setVisibility(View.VISIBLE);
			layout_time.setVisibility(View.VISIBLE);
			tv_date.setText(day+"th every month");
			tv_time.setText(String.format("%02d:%02d", hour, minute));
			break;
			
		case Constant.REPEAT_YEARLY:		// Yearly
			layout_date.setVisibility(View.VISIBLE);
			layout_time.setVisibility(View.VISIBLE);
			tv_date.setText(String.format("%02d-%02d every year", month+1, day));
			tv_time.setText(String.format("%02d:%02d", hour, minute));
			break;
		}
	}
	
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dayOfWeek = which;
		}
		
	};
	
	private TextWatcher contentWatcher = new TextWatcher(){
		@Override
		public void afterTextChanged(Editable etd) {
			if(etd.length()==0){
				btn_send.setEnabled(false);
			}
			else{
				btn_send.setEnabled(true);
			}
		}
	
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}
	
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private String parseRecipient(){
		String to = et_to.getText().toString();
		String number = "";
		if(to.length()!=0){
			if(to.contains("<")){
				// get <content> inside <>
				Pattern pattern = Pattern.compile("<(.*?)>");
				Matcher matcher = pattern.matcher(to);
				if (matcher.find()){
				    to = matcher.group(1);
				    // get numbers
					pattern = Pattern.compile("\\d+");
					matcher = pattern.matcher(to);
					while(matcher.find()){
						number += matcher.group();
					}
				}
			}
			else{
				// input must be all numberic characters.
				if(to.matches("^[0-9]+$")){
					number = to;
				}
			}
		}
		return number;
	}
	
	private boolean sendScheduledMessage(){
		String address = parseRecipient();
		if(address==""){
			Toast.makeText(this, 
					"Invalid recipient:"+et_to.getText().toString(), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// validate time
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute);
		if(repeat==Constant.REPEAT_WEEKLY){
			cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		}
		long nextTriggerTime = Alarm.nextTriggerTime(cal.getTimeInMillis(), repeat);
		if(nextTriggerTime==-1){
			Toast.makeText(this, 
					"Time already passed!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		// update database
		int cid = -1;
		String body = et_content.getText().toString();
		Contact receiver = ContactManager.getContactByNumber(this, address);
		
		if(msg!=null){
			msg.setContent(body);
			msg.setTimeStamp(cal.getTimeInMillis());
			msg.setReceiver(receiver);
			msg.setRepeat(repeat);
			msg.update(this);
		}
		else{
			msg = new Message(-1, cid, body, Message.MESSAGE_TYPE_SCHEDULED, false, cal.getTimeInMillis(), null, receiver);
			msg.setRepeat(repeat);
			msg.insert(this);
		}
		
		// alarmManager
		Alarm.SetAlarm(this, nextTriggerTime);
		return true;
	}
}
