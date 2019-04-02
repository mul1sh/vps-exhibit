package com.verve.vps.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.verve.vps.GlideApp

import com.verve.vps.R
import com.verve.vps.helpers.OBArrestHelper.fingerPrintBase64
import com.verve.vps.helpers.RotateTransformation
import com.verve.vps.utils.Utils

import fgtit.fpengine.constants
import fgtit.fpengine.fpdevice

import kotlinx.android.synthetic.main.fingerprint_layout.*
import java.io.ByteArrayOutputStream
import java.util.*


class FingerPrintFragment : Fragment(){

    private lateinit var parentActivity: AppCompatActivity
    private lateinit var mContext: Context

    // fingerprint reader vars
    private val fpdev = fpdevice()
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private val bmpdata = ByteArray(74806)
    private val bmpsize = IntArray(1)


    private val refdata = ByteArray(512)
    private val refsize = IntArray(1)
    private val matdata = ByteArray(512)
    private val matsize = IntArray(1)

    private var handler: Handler? = null
    private var fingerprintCaptured = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fingerprint_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init the public vars
        parentActivity = activity as AppCompatActivity
        mContext = parentActivity.applicationContext
    }

    override fun onResume() {
        super.onResume()

        //set up the fingerprint device
        fpdev.SetInstance(parentActivity)
        fpdev.SetUpImage(true)

        // init the buttons
        initButtons()
    }

    private fun initButtons() {

        btnCapture.setOnClickListener {
            captureFingerprint()
        }

        btnVerify.setOnClickListener {
            verifyFingerprint()
        }

        btnSkipVerification.setOnClickListener {
            skipVerification()
        }
        btnSkipVerification.paintFlags = btnSkipVerification.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun captureFingerprint() {


        when (fpdev.OpenDevice()) {
            0 -> {
                timerStart()
                fpdev.CaptureImage()
            }
            -1 -> fingerprint_status.text = "Finger print reader not found"
            -2 -> fingerprint_status.text = "Finger print reader evaluation period has expired "
            -3 -> fingerprint_status.text = "Device not connected "
        }

        @SuppressLint("HandlerLeak")
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    1 -> {
                        val work = fpdev.GetWorkMsg()
                        val ret = fpdev.GetRetMsg()
                        when (work) {

                            constants.FPM_DEVICE -> {
                                fingerprint_status.text = "Please Open Device"
                                timerStop()
                            }
                            constants.FPM_PLACE -> {
                                fingerprint_status.text = "Place Finger"
                            }
                            constants.FPM_LIFT -> fingerprint_status.text = "Lift Finger"
                            constants.FPM_CAPTURE -> {
                                if (ret == 1) {
                                    fingerprint_status.text = "Capture success!"
                                    val bm1 = BitmapFactory.decodeByteArray(bmpdata, 0, 74806)

                                    GlideApp
                                    .with(this@FingerPrintFragment)
                                    .load(bm1)
                                    .transform(RotateTransformation( 180f))
                                    .into(fingerprint_img).clearOnDetach()


                                    fingerprint_img.setImageBitmap(bm1)
                                    fingerprintCaptured = true

                                    // save the base64 string
                                    val byteArrayOutputStream = ByteArrayOutputStream()
                                    bm1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                                    val byteArray = byteArrayOutputStream.toByteArray()
                                    fingerPrintBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)

                                } else {
                                    fingerprint_status.text = "Capture Image Fail"
                                }
                                timerStop()
                            }
                            constants.FPM_GENCHAR -> {
                                if (ret == 1) {
                                    fingerprint_status.text = "Generate Template OK"
                                    fpdev.GetTemplateByGen(matdata, matsize)

                                    val mret = fpdev.MatchTemplate(refdata, refsize[0], matdata, matsize[0])
                                    fingerprint_status.text = String.format("Match Return:%d", mret)
                                } else {
                                    fingerprint_status.text = "Generate Template Fail"
                                }
                                timerStop()

                            }
                            constants.FPM_ENRFPT -> {
                                if (ret == 1) {
                                    fingerprint_status.text = "Enrol Template OK"
                                    fpdev.GetTemplateByEnl(refdata, refsize)
                                } else {
                                    fingerprint_status.text = "Enrol Template Fail"
                                }
                                timerStop()
                            }
                            constants.FPM_NEWIMAGE -> {
                                fpdev.GetBmpImage(bmpdata, bmpsize)

                            }
                            constants.FPM_TIMEOUT -> {
                                fingerprint_status.text = "Finger print reader time out"
                            }
                        }
                    }
                }
                super.handleMessage(msg)
            }
        }


    }

    private fun verifyFingerprint() {

        if(fingerprintCaptured) {
            Utils.showSnackbar(parentActivity,"Finger print search API not ready, please try again later!!", R.drawable.ic_error_red)
        } else {
            Utils.showSnackbar(parentActivity,"Please capture a finger print first in order to verify!!!", R.drawable.ic_error_red)
        }

    }

    private fun skipVerification() {
        val newFragment = CellBookingFragment()
        val args = Bundle()
        args.putString("title", "Cell Booking")
        newFragment.arguments = args
        Utils.goToFragment(newFragment,parentActivity)
    }

    private fun timerStart() {
        if (mTimer == null) {
            mTimer = Timer()
        }
        if (mTimerTask == null) {
            mTimerTask = object : TimerTask() {
                override fun run() {
                    val message = Message()
                    message.what = 1
                    handler!!.sendMessage(message)
                }
            }
        }
        if (mTimer != null && mTimerTask != null)
            mTimer!!.schedule(mTimerTask, 200, 200)
    }

    private fun timerStop() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
            mTimerTask!!.cancel()
            mTimerTask = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fpdev.CloseDevice()
    }



}