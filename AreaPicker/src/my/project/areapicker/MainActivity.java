package my.project.areapicker;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button btn_click, btn_ok;
	RelativeLayout test_pop_layout;
	int width, height;

	private TextView tt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 获取屏幕的高度和宽度
		Display display = this.getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();

		// 获取弹出的layout
		test_pop_layout = (RelativeLayout) findViewById(R.id.test_pop_layout);
		tt = (TextView) findViewById(R.id.tpop2_tv);

		btn_click = (Button) findViewById(R.id.btn_click);
		btn_click.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 显示 popupWindow
				PopupWindow popupWindow = makePopupWindow(MainActivity.this);
				int[] xy = new int[2];
				test_pop_layout.getLocationOnScreen(xy);
				popupWindow.showAtLocation(test_pop_layout, Gravity.CENTER
						| Gravity.BOTTOM, 0, -height);
			}
		});
	}

	// Scrolling flag
	private boolean scrolling = false;
	private TextView tv;

	// 创建一个包含自定义view的PopupWindow
	private PopupWindow makePopupWindow(Context cx) {
		final PopupWindow window;
		window = new PopupWindow(cx);

		View contentView = LayoutInflater.from(this).inflate(
				R.layout.cities_layout, null);
		window.setContentView(contentView);

		tv = (TextView) contentView.findViewById(R.id.tv_cityName);

		final WheelView country = (WheelView) contentView
				.findViewById(R.id.country);
		country.setVisibleItems(3);
		country.setViewAdapter(new CountryAdapter(this));

		final String cities[][] = AddressData.CITIES;
		final String ccities[][][] = AddressData.COUNTIES;
		final WheelView city = (WheelView) contentView.findViewById(R.id.city);
		city.setVisibleItems(0);

		country.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updateCities(city, cities, newValue);
				}
			}
		});

		country.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updateCities(city, cities, country.getCurrentItem());

				tv.setText(AddressData.PROVINCES[country.getCurrentItem()]);
			}
		});

		// 地区选择
		final WheelView ccity = (WheelView) contentView
				.findViewById(R.id.ccity);
		ccity.setVisibleItems(0);

		city.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!scrolling) {
					updatecCities(ccity, ccities, country.getCurrentItem(),
							newValue);
				}
			}
		});

		city.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;
				updatecCities(ccity, ccities, country.getCurrentItem(),
						city.getCurrentItem());

				tv.setText(AddressData.PROVINCES[country.getCurrentItem()]
						+ "-"
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]);

			}
		});

		ccity.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				scrolling = false;

				tv.setText(AddressData.PROVINCES[country.getCurrentItem()]
						+ "-"
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
						+ "-"
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()]);

			}
		});

		country.setCurrentItem(1);

		// 点击事件处理
		btn_ok = (Button) contentView.findViewById(R.id.button_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tt.setText(AddressData.PROVINCES[country.getCurrentItem()]
						+ "-"
						+ AddressData.CITIES[country.getCurrentItem()][city
								.getCurrentItem()]
						+ "-"
						+ AddressData.COUNTIES[country.getCurrentItem()][city
								.getCurrentItem()][ccity.getCurrentItem()]);
				window.dismiss(); // 隐藏
			}
		});

		window.setWidth(width);
		window.setHeight(height / 2);

		// 设置PopupWindow外部区域是否可触摸
		window.setFocusable(true); // 设置PopupWindow可获得焦点
		window.setTouchable(true); // 设置PopupWindow可触摸
		window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		return window;
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				cities[index]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(cities[index].length / 2);
	}

	/**
	 * Updates the ccity wheel
	 */
	private void updatecCities(WheelView city, String ccities[][][], int index,
			int index2) {
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(this,
				ccities[index][index2]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
		city.setCurrentItem(ccities[index][index2].length / 2);
	}

	/**
	 * Adapter for countries
	 */
	private class CountryAdapter extends AbstractWheelTextAdapter {
		// Countries names
		private String countries[] = AddressData.PROVINCES;

		/**
		 * Constructor
		 */
		protected CountryAdapter(Context context) {
			super(context, R.layout.country_layout, NO_RESOURCE);

			setItemTextResource(R.id.country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return countries[index];
		}
	}

}
