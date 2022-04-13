package com.example.movie

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity(), Response.Listener<String> {
    lateinit var submitBt :Button
    lateinit var dateEt :EditText
    lateinit var rv :RecyclerView
    lateinit var adapter: MainRvAdapter
    lateinit var loadLayout: RelativeLayout

    var arr : ArrayList<MovieData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        submitBt = findViewById(R.id.submitBt)
        dateEt = findViewById(R.id.dateEt)
        rv = findViewById(R.id.rv)
        loadLayout = findViewById(R.id.loadLayout)

        adapter = MainRvAdapter(this, arr)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        submitBt.setOnClickListener {
            var date = dateEt.text.toString()
            loadLayout.visibility = View.VISIBLE
            request(date)
        }

        loadLayout.setOnClickListener{

        }
    }

    var idx = 0
    fun requestDirector(){
        if(idx < arr.size ) {
            val requestQueue = Volley.newRequestQueue(this)
            val url =
                "http://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json" +
                        "?key=2965ab5d32ae5db7e464c368159aaf62&movieCd=" + arr.get(idx).movieCd

            val request: StringRequest = object : StringRequest(
                Request.Method.GET, url,
                Response.Listener { response ->
                    //파싱
                    parseDirector(response)
                    idx++
                    requestDirector()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(MainActivity@ this, error.toString(), Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String,String> = HashMap()
                    params["userid"] = "aaaaa"
                    params["password"] = "1234"
                    params["mobileNO"] = "010-1234-1234"
                    return params
                } //포스트 방식
            }
            requestQueue.add(request)
        }else{
            //리싸이클러 뷰에 보여주기
            adapter.notifyDataSetChanged()
            loadLayout.visibility = View.INVISIBLE
        }
    }

    fun parseDirector(data : String){
        var rootObj = JSONObject(data)
        var movieInfoResult = rootObj.getJSONObject("movieInfoResult")
        var movieInfo = movieInfoResult.getJSONObject("movieInfo")
        var directors = movieInfo.getJSONArray("directors")
        var total = ""
        for (i in 0 until directors.length() step 1){
            var tempObj = directors.getJSONObject(i)
            var peopleNm = tempObj.getString("peopleNm");
            total += peopleNm+","
        }
        arr.get(idx).director = total
        Log.d("aabb","name: $total")
    }

    fun request(date : String){
        val requestQueue = Volley.newRequestQueue(this)
        val url = "http://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json" +
                "?key=44630460128d8f36e53de4dfafcf1550" +
                "&targetDt="+date

        val request: StringRequest = object : StringRequest(Request.Method.GET, url, this,
            Response.ErrorListener { error ->
                Toast.makeText(MainActivity@this, error.toString(), Toast.LENGTH_LONG).show()
            }
        ) {
        }
        requestQueue.add(request)
    }

    fun parseList(data : String){
        var rootObj  = JSONObject(data)
        var boxOfficeResultObj = rootObj.getJSONObject("boxOfficeResult")
        var dailyBoxOfficeListArr = boxOfficeResultObj.getJSONArray("dailyBoxOfficeList")

        for (i in 0 until dailyBoxOfficeListArr.length() step 1){
            var tempObj = dailyBoxOfficeListArr.getJSONObject(i)
            var movieNm = tempObj.getString("movieNm")
            var audiCnt = tempObj.getString("audiCnt")
            var movieCd = tempObj.getString("movieCd")

            arr.add(MovieData(movieNm, audiCnt,movieCd))
        }
        idx = 0
        requestDirector()
    }

    override fun onResponse(response: String?) {
        parseList(response!!)
    }

    inner class MainRvAdapter(val context: Context, val arr: ArrayList<MovieData>) :
        RecyclerView.Adapter<MainRvAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
            return Holder(view)
        }

        override fun getItemCount(): Int {
            return arr.size
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.movieNmTv.setText(arr.get(position).movieNm)
            holder.audiCntTv.setText("관객 수:" +arr.get(position).audiCnt)
            holder.directorTv.setText("감독:" +arr.get(position).director)

            holder.itemView.setOnClickListener{

            }
        }

        inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
            var movieNmTv: TextView = itemView!!.findViewById(R.id.movieNmTv)
            val directorTv: TextView = itemView!!.findViewById(R.id.directorTv)
            val audiCntTv: TextView = itemView!!.findViewById(R.id.audiCntTv)
        }
    }

}