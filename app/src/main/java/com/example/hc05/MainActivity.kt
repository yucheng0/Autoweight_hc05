package com.example.hc05

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.hc05.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

const val BT_MAC = "98:D3:31:FC:1B:1B" //""24:79:F3:8E:55:AA"

class MainActivity : AppCompatActivity() {
    companion object {
       lateinit var mBluetoothAdapter:BluetoothAdapter
        lateinit var btSocket:BluetoothSocket
  //      lateinit var bytes:ByteArray

        //       val myUUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")
        var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databinding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

//       setContentView(R.layout.activity_main)
        val myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)


        CheckBt()
        Connect()

  //     writeData("1")
   //     myViewModel.init()


       myViewModel.reReadListenKey .observe(this, androidx.lifecycle.Observer {
       if ( myViewModel.readableEnable == true  ){
            myViewModel.init()}
        })

        btn1.setOnClickListener {
            myViewModel.init()            //注意讀寫都去協程做
            myViewModel.readableEnable == true
        }

        }


    private fun CheckBt() {
        Toast.makeText(applicationContext, "It has started", Toast.LENGTH_SHORT).show()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!mBluetoothAdapter.enable()) {
            Toast.makeText(applicationContext, "Bluetooth Disabled !", Toast.LENGTH_SHORT).show()
            /* It tests if the bluetooth is enabled or not, if not the app will show a message. */
            finish()
        }
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Bluetooth null !", Toast.LENGTH_SHORT).show()
        }
    }
    fun Connect() {
        val device = mBluetoothAdapter.getRemoteDevice(BT_MAC)   //Renox10 mac  配對
        Log.d("", "Connecting to ... $device")
        Toast.makeText(applicationContext, "Connecting to ... ${device.name} mac: ${device.uuids[0]} address: ${device.address}", Toast.LENGTH_LONG).show()
        mBluetoothAdapter.cancelDiscovery()
        try {
            btSocket = device.createRfcommSocketToServiceRecord(myUUID)
            /* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */
            btSocket.connect()
            Log.d("", "Connection made.")
            Toast.makeText(applicationContext, "Connection made.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            try {
                btSocket.close()
            } catch (e2: IOException) {
                Log.d("", "Unable to end the connection")
                Toast.makeText(applicationContext, "Unable to end the connection", Toast.LENGTH_SHORT).show()
            }
            Log.d("", "Socket creation failed")
            Toast.makeText(applicationContext, "Socket creation failed", Toast.LENGTH_SHORT).show()
        }
        //beginListenForData()
        /* this is a method used to read what the Arduino says for example when you write Serial.print("Hello world.") in your Arduino code */
    }
    private fun writeData(data: String) {
        var outStream = btSocket.outputStream
        try {
            outStream = btSocket.outputStream
        } catch (e: IOException) {
            //Log.d(FragmentActivity.TAG, "Bug BEFORE Sending stuff", e)
        }
        val msgBuffer = data.toByteArray()
        try {
            outStream.write(msgBuffer)
        } catch (e: IOException) {
            //Log.d(FragmentActivity.TAG, "Bug while sending stuff", e)
        }
    }



}