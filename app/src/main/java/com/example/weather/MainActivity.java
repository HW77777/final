package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.weather.bean.City;
import com.example.weather.bean.Province;
import com.google.gson.Gson;
import com.qweather.sdk.bean.Basic;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Type;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String weatherCountry = "http://guolin.tech/api/china/";//中国的省份城市api
    private String weatherProvince, weatherCity;//获取到的省份id和城市id
    private ArrayList<Province> provinceList;//中国的省份集合
    private ArrayList<City> cityList;//具体省份的城市集合

    private String weatherUrl = "https://free-api.heweather.net/s6/weather/now?location=%s&key=%s";//和风天气 免费的api接口
    private String cityId="101010100";//具体城市天气id，如湛江，"weather_id"=CN101281001

    private String userName = "HE2011022101131657";
    private String key = "c76371e73025444f95284382fe1ed72a";//自己申请的key

    /**
     * 请输入该省份的城市
     */
    private EditText mCityEdit;
    /**
     * 查询
     */
    private Button mSearch;
    /**
     * 城市的天气
     */
    private TextView mCityWeather;
    private TextView mWeather;
    private TextView mShidu;
    private TextView mview;
    private TextView mjs;
    /**
     * 省份：
     */
    private TextView mProvinceTv;
    /**
     * 请输入要查询的天气的省份
     */
    private EditText mProvinceEdit;

    String province, city;//接收输入的省份和城市收用的字符串
    private LinearLayout mWeatherInfo;
    private TextView mSuggestion;

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        HeConfig.init(userName, key);
        HeConfig.switchToDevService();
    }

    private void initView() {
        mCityEdit = (EditText) findViewById(R.id.city_edit);
        mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(this);
        mCityWeather = (TextView) findViewById(R.id.city_weather);
        mWeather = (TextView) findViewById(R.id.weather);
        mShidu = (TextView) findViewById(R.id.shidu);
        mProvinceTv = (TextView) findViewById(R.id.province_tv);
        mProvinceEdit = (EditText) findViewById(R.id.province_edit);
        mWeatherInfo = (LinearLayout) findViewById(R.id.weather_info);
        mview=(TextView)findViewById(R.id.viw);
        mjs=(TextView)findViewById(R.id.js);
       // mSuggestion = (TextView) findViewById(R.id.suggestion);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.search:
                province = mProvinceEdit.getText().toString().trim();
                city = mCityEdit.getText().toString().trim();
                queryWeather2();
                break;
        }
    }

    private void queryWeather() {
        provinceList = new ArrayList<Province>();//省份集合
        cityList = new ArrayList<City>();//具体省份的城市集合
        new Thread() {
            @Override
            public void run() {
                try {
                    //weatherCountry = "http://guolin.tech/api/china/"
                    URL url = new URL(weatherCountry);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    StringBuffer sb = new StringBuffer();
                    String string;

                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb.append(string);
                    }

                    String result = sb.toString();

                    Log.d("MainActivity", "" + result);

                    JSONArray provinceArray = new JSONArray(result);
                    for (int i = 0; i < provinceArray.length(); i++) {
                        JSONObject provinceInfo = provinceArray.getJSONObject(i);//获取每个省份信息
                        Province provinceBean = new Province();//创建省份实体类对象
                        Gson gson = new Gson();//创建Gson解析对象

                        //反序例化，将json数据转化为实体类对象的成员变量值
                        provinceBean = gson.fromJson(provinceInfo.toString(), Province.class);
                        //添加保存好的省份对象数据进入省份集合
                        provinceList.add(provinceBean);
                    }

                    for (Province pro : provinceList) {
                        //如果该省份为用户输入的省份
                        if (pro.getName().equals(province)) {
                            //则拼接链接
                            //如：北京 weatherProvince = "http://guolin.tech/api/china/1/"
                            weatherProvince = weatherCountry + pro.getId() + "/";
                        }
                    }

                    Log.d("WeatherProvince", "" + weatherProvince);

                    //如：北京 weatherProvince = "http://guolin.tech/api/china/1/"
                    url = new URL(weatherProvince);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);

                    //将请求返回的数据流转换成字节输入流对象
                    is = connection.getInputStream();
                    //将字节输入流对象转换成字符输入流对象
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);

                    StringBuffer sb2 = new StringBuffer();
                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb2.append(string);
                    }

                    String result2 = sb2.toString();

                    Log.d("MainActivity2", "" + result2);

                    JSONArray cityArray = new JSONArray(result2);
                    for (int i = 0; i < cityArray.length(); i++) {
                        JSONObject cityInfo = cityArray.getJSONObject(i);//获取具体省份城市信息
                        City cityBean = new City();//创建城市实体类对象
                        Gson gson = new Gson();//创建Gson解析对象
                        //反序例化，将json数据转化为实体类对象的成员变量值
                        cityBean = gson.fromJson(cityInfo.toString(), City.class);
                        //添加保存好的城市对象数据进入城市集合
                        cityList.add(cityBean);
                    }

                    for (City c : cityList) {
                        //如果该城市为用户输入的城市
                        if (c.getName().equals(city)) {
                            //则拼接链接
                            //如：北京 weatherCity = "http://guolin.tech/api/china/1/1/"
                            weatherCity = weatherProvince + c.getId() + "/";
                        }
                    }

                    Log.d("WeatherCity", ""+weatherCity);

                    //如：北京 weatherCity = "http://guolin.tech/api/china/1/1/"
                    url = new URL(weatherCity);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    is = connection.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);

                    StringBuffer sb3 = new StringBuffer();
                    //读文本
                    while ((string = br.readLine()) != null) {
                        sb3.append(string);
                    }

                    String result3 = sb3.toString();

                    Log.d("MainActivity3", "" + result3);

                    JSONArray jsonArray = new JSONArray(result3);
                    JSONObject cityIdInfo = jsonArray.getJSONObject(0);
                    cityId=cityIdInfo.getString("weather_id");

                    //拼接字符串
                    String weatherApi = String.format(weatherUrl, cityId, key);
                    Log.d("WeatherApi", "" + weatherApi);
                    queryWeather2();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public void queryWeather2(){
        QWeather.getWeatherNow(MainActivity.this, "101010100",Lang.ZH_HANS,Unit.METRIC,new QWeather.OnResultWeatherNowListener(){

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
                Log.i(TAG, "getWeather onSuccess: " + new Gson().toJson(weatherBean));
                if (Code.OK.getCode().equalsIgnoreCase(weatherBean.getCode())) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    String tmp=now.getTemp();
                    String cond_txt=now.getText();
                    String wind_dir=now.getWindDir();

                    mWeather.setText("当前温度："+tmp+"℃，"+cond_txt+"，"+wind_dir);
                    String hum=now.getHumidity();
                    mShidu.setText(hum+"%");
                    String nj=now.getVis();
                    mview.setText(nj+"km");
                    String j=now.getPrecip();
                    mjs.setText(j+"mm");
                    Message message=new Message();
                    message.what=1;
                    MainActivity.this.myHandler.sendMessage(message);
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });
   //   QWeather.getWeather7D(MainActivity.this, cityId, Lang.ZH_HANS, Unit.METRIC, new QWeather.OnResultWeatherDailyListener() {
   //        @Override
   //        public void onError(Throwable throwable) {

   //        }

    //       @Override
     //      public void onSuccess(WeatherDailyBean weatherDailyBean) {
      //         List<WeatherDailyBean.DailyBean> wd=weatherDailyBean.getDaily();
      //         String max=wd.get(0).getTempMax();
      //         String min=wd.get(6).getTempMin();
      //         mSuggestion.setText("最高温"+max+"\n"+"最低温"+min+"\n");


    //       }
   //    });

    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    mCityWeather.setVisibility(View.VISIBLE);
                    mWeatherInfo.setVisibility(View.VISIBLE);
                    weatherProvince = "";
                    weatherCity = "";
                    break;
            }
            super.handleMessage(msg);
        }
    };

}

