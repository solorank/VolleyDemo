package com.example.wangxiang.volleydemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private  String TAG = "MainActivity";
    private ImageView imgView;
    private NetworkImageView networkImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.img);
        /**
         * 请求方式分为三步:创建RequestQueue,创建具体请求对象,将请求对象加入请求队列
         */
        RequestQueue mQueue = Volley.newRequestQueue(this);
        //1.get方式请求:三个参数url,成功回调,失败回调
        StringRequest strRequest = new StringRequest("http://blog.csdn.net/u010361524", new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d(TAG,s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG,volleyError.getMessage(),volleyError);
            }
        });
        mQueue.add(strRequest);

        //2.post方式请求:四个参数post,url,成功回调,失败回调
        StringRequest postRequest = new StringRequest(Request.Method.POST,"http://blog.csdn.net/u010361524",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d(TAG,s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG,volleyError.getMessage(),volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("params1", "value1");
                map.put("params2", "value2");
                return map;
            }
        };
        mQueue.add(postRequest);

        //3.JsonObjectRequest和JsonArrayRequest:一个是用于请求一段JSON数据的，一个是用于请求一段JSON数组的。
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG,jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG,volleyError.getMessage(),volleyError);
            }
        });
        mQueue.add(jsonObjectRequest);

        /**
         * 4.最原始的volley请求图片方式
         * ImageRequest的构造函数接收六个参数，
         * 第一个参数就是图片的URL地址，这个没什么需要解释的。
         * 第二个参数是图片请求成功的回调，这里我们把返回的Bitmap参数设置到ImageView中。
         * 第三第四个参数分别用于指定允许图片最大的宽度和高度，如果指定的网络图片的宽度或高度大于这里的最大值，则会对图片进行压缩，指定成0的话就表示不管图片有多大，都不会进行压缩。
         * 第五个参数用于指定图片的颜色属性，Bitmap.Config下的几个常量都可以在这里使用，其中ARGB_8888可以展示最好的颜色属性，每个图片像素占据4个字节的大小，而RGB_565则表示每个图片像素占据2个字节大小。
         * 第六个参数是图片请求失败的回调，这里我们当请求失败时在ImageView中显示一张默认图片。
         */
        ImageRequest imageRequest = new ImageRequest("http://imgsrc.baidu.com/forum/w%3D580/sign=ce6fefe98126cffc692abfba89014a7d/b2946fcf3bc79f3d81f67d40b9a1cd11738b29d0.jpg",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imgView.setImageBitmap(bitmap);
                    }
                }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                imgView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        mQueue.add(imageRequest);

        /**
         * 5.ImageLoader也可以用于加载网络上的图片，并且它的内部也是使用ImageRequest来实现的，
         * 不过ImageLoader明显要比ImageRequest更加高效，因为它不仅可以帮我们对图片进行缓存，还可以过滤掉重复的链接，避免重复发送请求。
         */
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imgView,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
        imageLoader.get("http://imgsrc.baidu.com/forum/w%3D580/sign=ce6fefe98126cffc692abfba89014a7d/b2946fcf3bc79f3d81f67d40b9a1cd11738b29d0.jpg",listener);


        //6.NetworkImageView是一个自定义控制，它是继承自ImageView的，具备ImageView控件的所有功能，并且在原生的基础之上加入了加载网络图片的功能。
        networkImageView = (NetworkImageView) findViewById(R.id.network_image_view);
        networkImageView.setDefaultImageResId(R.mipmap.ic_launcher);
        networkImageView.setErrorImageResId(R.mipmap.ic_launcher);
        networkImageView.setImageUrl("http://imgsrc.baidu.com/forum/w%3D580/sign=ce6fefe98126cffc692abfba89014a7d/b2946fcf3bc79f3d81f67d40b9a1cd11738b29d0.jpg",
                imageLoader);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    /**
     * Volley是一个轻量级的联网请求框架,在对于图片的处理方面与Picasso,Glide等专注于图片加载的框架相比,Volley略显不足.
     * volley不能加载本地图片,在使用加载图片的框架时,Google推荐的图片加载库，专注于流畅的滚动。Glide和Picasso都是非常完美的库。
     * Glide加载图像以及磁盘缓存的方式都要优于Picasso，速度更快，并且Glide更有利于减少OutOfMemoryError的发生，
     * GIF动画是Glide的杀手锏。不过Picasso的图片质量更高。
     */
}
