package com.demo.monitor;

//import com.hikvision.netsdk.HCNetSDK;
//import com.hikvision.netsdk.PTZCommand;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 主线程
 */
public class MonitorActivity extends Activity implements OnTouchListener,OnClickListener{

	private TextView tv_Loading;
	private SurfaceView sf_VideoMonitor;
	private View layout;
	private EditText ip;
	private EditText port;
	private EditText userName;
	private EditText passWord;
	private EditText channel;

	private Button start, set, stop;
	private Button btn_Up, btn_Down, btn_Left, btn_Right;
	private Button btn_UpLeft, btn_DownLeft, btn_UpRight, btn_DownRight;
	private Button btn_ZoomIn, btn_ZoomOut;
    private Button btn_FocusNear, btn_FocusFar;
    private Button btn_Auto;
	private boolean isMoving;

	private final StartRenderingReceiver receiver = new StartRenderingReceiver();
	/**
	 * 返回标记
	 */
	private boolean backflag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor);
		// 设置用于发广播的上下文
		HC_DVRManager.getInstance().setContext(getApplicationContext());
		initView();
	}

	private DeviceBean getDeviceBean() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"dbinfo", 0);
		String ip = sharedPreferences.getString("ip", "");
		String port = sharedPreferences.getString("port", "");
		String userName = sharedPreferences.getString("userName", "");
		String passWord = sharedPreferences.getString("passWord", "");
		String channel = sharedPreferences.getString("channel", "");
		DeviceBean bean = new DeviceBean();
		// bean.setIP("192.168.10.64");
		// bean.setPort("8000");
		// bean.setUserName("admin");
		// bean.setPassWord("123");
		// bean.setChannel("1");
		bean.setIP(ip);
		bean.setPort(port);
		bean.setUserName(userName);
		bean.setPassWord(passWord);
		bean.setChannel(channel);
		return bean;
	}

	// 向系统中存入devicebean的相关数据
	public void setDBData(String ip, String port, String userName,
			String passWord, String channel) {
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"dbinfo", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("ip", ip);
		editor.putString("port", port);
		editor.putString("userName", userName);
		editor.putString("passWord", passWord);
		editor.putString("channel", channel);
		editor.commit();
	}

	protected void startPlay() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(HC_DVRManager.ACTION_START_RENDERING);
		filter.addAction(HC_DVRManager.ACTION_DVR_OUTLINE);
		registerReceiver(receiver, filter);

		tv_Loading.setVisibility(View.VISIBLE);
		tv_Loading.setText(getString(R.string.tv_connect_cam));
		if (backflag) {
			backflag = false;
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().setSurfaceHolder(
							sf_VideoMonitor.getHolder());
					HC_DVRManager.getInstance().realPlay();
				}
			}.start();
		} else {
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().setDeviceBean(getDeviceBean());
					HC_DVRManager.getInstance().setSurfaceHolder(
							sf_VideoMonitor.getHolder());
					HC_DVRManager.getInstance().initSDK();
					HC_DVRManager.getInstance().loginDevice();
					HC_DVRManager.getInstance().realPlay();
				}
			}.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		new Thread() {
			@Override
			public void run() {
				HC_DVRManager.getInstance().logoutDevice();
				HC_DVRManager.getInstance().freeSDK();
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new Thread() {
				@Override
				public void run() {
					HC_DVRManager.getInstance().stopPlay();
				}
			}.start();
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
		 public boolean onTouch(final View v, final MotionEvent event) {
		        if (isMoving) {
		            return true;
		        }
		        new Thread() {
		            @Override
		            public void run() {
		                switch (v.getId()) {
		                    case R.id.btn_UpLeft:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(7);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(7);
		                        }
		                        break;
		                    case R.id.btn_Up:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(8);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(8);
		                        }
		                        break;
		                    case R.id.btn_UpRight:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(9);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(9);
		                        }
		                        break;
		                    case R.id.btn_Left:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(4);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(4);
		                        }
		                        break;
		                    case R.id.btn_Right:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(6);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(6);
		                        }
		                        break;
		                    case R.id.btn_DownLeft:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(1);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(1);
		                        }
		                        break;
		                    case R.id.btn_Down:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(2);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(2);
		                        }
		                        break;
		                    case R.id.btn_DownRight:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startMove(3);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopMove(3);
		                        }
		                        break;
		                    case R.id.btn_ZoomIn:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startZoom(1);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopZoom(1);
		                        }
		                        break;
		                    case R.id.btn_ZoomOut:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startZoom(-1);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopZoom(-1);
		                        }
		                        break;
		                    case R.id.btn_FocusNear:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startFocus(-1);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopFocus(-1);
		                        }
		                        break;
		                    case R.id.btn_FocusFar:
		                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
		                            HC_DVRManager.getInstance().startFocus(1);
		                        }
		                        if (event.getAction() == MotionEvent.ACTION_UP) {
		                            HC_DVRManager.getInstance().stopFocus(1);
		                        }
		                        break;
		                    default:
		                        break;
		                }
		            }
		        }.start();
		        return false;
		    }


	   
/*	public boolean onTouch(final View v, final MotionEvent event){
		
//		new Thread() {
 //           public void run() {
                switch (v.getId()) {
                    case R.id.btn_UpLeft:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(0, PTZCommand.UP_LEFT, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(0, PTZCommand.UP_LEFT, 1, 2);
                        }
                        break;
                    case R.id.btn_Up:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(1, PTZCommand.TILT_UP, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(1, PTZCommand.TILT_UP, 1, 2);
                        }
                        break;
                    case R.id.btn_UpRight:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(0, PTZCommand.UP_RIGHT, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.UP_RIGHT, 1, 2);
                        }
                        break;
                    case R.id.btn_Left:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(1, PTZCommand.PAN_LEFT, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.PAN_LEFT, 1, 2);
                        }
                        break;
                    case R.id.btn_Right:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.PAN_RIGHT, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.PAN_RIGHT, 1, 2);
                        }
                        break;
                    case R.id.btn_DownLeft:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.DOWN_LEFT, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.DOWN_LEFT, 1, 2);
                        }
                        break;
                    case R.id.btn_Down:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.TILT_DOWN, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.TILT_DOWN, 1, 2);
                        }
                        break;
                    case R.id.btn_DownRight:
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.DOWN_RIGHT, 0, 2);
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                        	HCNetSDK.getInstance().NET_DVR_PTZControlWithSpeed(-1, PTZCommand.DOWN_RIGHT, 1, 2);
                        }
                        break; 
                    default:
                        break;
                }
//            }
//        }.start();
		
		
		return false;
		
	}*/
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.start:
			startPlay();
			break;
		case R.id.stop:
			HC_DVRManager.getInstance().stopPlay();
			break;
		case R.id.set:
			setPlayer();
			break;
		}
	}

	public void setPlayer() {
		LayoutInflater inflater = getLayoutInflater();
		layout = inflater.inflate(R.layout.alert,(ViewGroup) findViewById(R.id.alert));
		ip = (EditText) layout.findViewById(R.id.ip);
		port = (EditText) layout.findViewById(R.id.port);
		userName = (EditText) layout.findViewById(R.id.userName);
		passWord = (EditText) layout.findViewById(R.id.passWord);
		channel = (EditText) layout.findViewById(R.id.channel);
		DeviceBean db = getDeviceBean();
		ip.setText(db.getIP());
		port.setText(db.getPort());
		userName.setText(db.getUserName());
		passWord.setText(db.getPassWord());
		channel.setText(db.getChannel());

		new AlertDialog.Builder(this).setTitle("设置").setView(layout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setDBData(ip.getText().toString(), port.getText()
								.toString(), userName.getText().toString(),
								passWord.getText().toString(), channel
										.getText().toString());
					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		// 获取手机分辨率
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		tv_Loading = (TextView) findViewById(R.id.tv_Loading);
		sf_VideoMonitor = (SurfaceView) findViewById(R.id.sf_VideoMonitor);

		start = (Button) findViewById(R.id.start);
		start.setOnClickListener(this);
		stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(this);
		set = (Button) findViewById(R.id.set);
		set.setOnClickListener(this);
		
		btn_UpLeft = (Button) findViewById(R.id.btn_UpLeft);
        btn_UpLeft.setOnTouchListener(this);
        
        btn_Up = (Button) findViewById(R.id.btn_Up);
        btn_Up.setOnTouchListener(this);
        
        btn_UpRight = (Button) findViewById(R.id.btn_UpRight);
        btn_UpRight.setOnTouchListener(this);
        
        btn_Left = (Button) findViewById(R.id.btn_Left);
        btn_Left.setOnTouchListener(this);
        
        btn_Right = (Button) findViewById(R.id.btn_Right);
        btn_Right.setOnTouchListener(this);
        
        btn_DownLeft = (Button) findViewById(R.id.btn_DownLeft);
        btn_DownLeft.setOnTouchListener(this);
        
        btn_Down = (Button) findViewById(R.id.btn_Down);
        btn_Down.setOnTouchListener(this);
        
        btn_DownRight = (Button) findViewById(R.id.btn_DownRight);
        btn_DownRight.setOnTouchListener(this);
        
        btn_ZoomIn = (Button) findViewById(R.id.btn_ZoomIn);
        btn_ZoomIn.setOnTouchListener(this);
        
        btn_ZoomOut = (Button) findViewById(R.id.btn_ZoomOut);
        btn_ZoomOut.setOnTouchListener(this);
        
        btn_FocusNear = (Button) findViewById(R.id.btn_FocusNear);
        btn_FocusNear.setOnTouchListener(this);
        
        btn_FocusFar = (Button) findViewById(R.id.btn_FocusFar);
        btn_FocusFar.setOnTouchListener(this);
        
        btn_Auto = (Button) findViewById(R.id.btn_Auto);
        btn_Auto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isMoving) {
                    v.setBackgroundResource(R.drawable.btn_auto_normal);
                } else {
                    v.setBackgroundResource(R.drawable.btn_auto_pressed);
                }
                new Thread() {
                    @Override
                    public void run() {
                        if (isMoving) {
                            HC_DVRManager.getInstance().stopMove(5);
                            isMoving = false;
                        } else {
                            HC_DVRManager.getInstance().startMove(5);
                            isMoving = true;
                        }
                    }
                }.start();
            }
        });

		LayoutParams lp = sf_VideoMonitor.getLayoutParams();
		lp.width = dm.widthPixels - 30;
		lp.height = lp.width / 16 * 9;
		sf_VideoMonitor.setLayoutParams(lp);
		tv_Loading.setLayoutParams(lp);
		Log.d("DEBUG", "视频窗口尺寸：" + lp.width + "x" + lp.height);

		sf_VideoMonitor.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d("DEBUG", getLocalClassName() + " surfaceDestroyed");
				sf_VideoMonitor.destroyDrawingCache();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d("DEBUG", getLocalClassName() + " surfaceCreated");
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				Log.d("DEBUG", getLocalClassName() + " surfaceChanged");
			}
		});

	}

	// 广播接收器
	private class StartRenderingReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (HC_DVRManager.ACTION_START_RENDERING.equals(intent.getAction())) {
				tv_Loading.setVisibility(View.GONE);
			}
			if (HC_DVRManager.ACTION_DVR_OUTLINE.equals(intent.getAction())) {
				tv_Loading.setText(getString(R.string.tv_connect_cam_error));
			}
		}
	}

}
