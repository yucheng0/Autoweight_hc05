package com.example.hc05

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

// 55 3F 04 00 14 00 05 A3 90

class MyViewModel : ViewModel() {
    val TAG = "myTag"
    var readableEnable = false
    val reReadListenKey = MutableLiveData<Boolean>()
    var readResult = ArrayList<String>()
    var readResultcopy = ArrayList<String>()
    var indexstartbyte = 0


    init {
        reReadListenKey.value = false
        readResultcopy.clear()
    }

    private fun readData() {
        var inStream = MainActivity.btSocket.inputStream
        try {
            inStream = MainActivity.btSocket.inputStream
        } catch (e: IOException) {
            println(e.printStackTrace())
        }

        val buffer: ByteArray = ByteArray(1024)
        val numBytes = inStream.read(buffer)  // bytes returned from read()
        println("numBytes = $numBytes")
        for (i in 0..numBytes - 1) {   //先知道nubBytes的數字再去讀
            if (buffer[i].toInt() >= 0) {
                readResult.add(buffer[i].toString())
            } else {
                val b1 = 256 + buffer[i].toInt()
                readResult.add(b1.toString())
            }

        }

//Parser 開始
//去除第1次資料
        //array在交替時是不能用指定的, 要用add否則會出錯
        if (readResult.size >= 90) {
            readResult.clear()
        } else if (readResult.size >= 9) {
             //Start Parser
             indexstartbyte = readResult.indexOf("85")
//            readResult.removeAt(indexstartbyte)
            if (readResult[indexstartbyte + 1] == "63") {         //命令
                
            } else {
                delRangeofArrayData(readResult,indexstartbyte,1)
            }
            println(readResult)
            readResult.clear()
        }
    }

    fun delRangeofArrayData (arr: ArrayList< String >, startIndex: Int, num:Int) {
            println ("Error")
            for (i in 0..startIndex) {               //刪除 85之前的(含85)
                arr.removeAt(startIndex)
            }
            for (i in 0..num-1){                    //刪除第幾個索引
                arr.removeAt(0)
            }
        }




    //清除及搬移
    fun errdataproc() {

        //     println ("readResult.size=${readResult.size}")
        //    println ("originalreadResultcopy = ${readResultcopy} ")
        for (i in indexstartbyte..(readResult.size) - 1) {
            readResultcopy.add(readResult[i])               //搬到新的Array
        }
        //      println ("readResultcopy = ${readResultcopy}")
        readResult.clear()                                //清掉原資料Array
        for (i in 0..(readResultcopy.size) - 1) {        //新的資料Array
            readResult.add(readResultcopy[i])
        }           // 搬到舊的資料Array
        readResultcopy.clear()                          //清掉新資料Array
        //  println ("readResult = ${readResult}")
        //   println ("readResultcopyclear = ${readResultcopy}")
    }


    fun init() {
        viewModelScope.launch(Dispatchers.Main) {
            delay200ms()
        }
// 先執行再delay

    }

    suspend fun delay200ms() {
        delay(200)
        readData()
        readableEnable = true
        reReadListenKey.value = reReadListenKey.value
    }
}
