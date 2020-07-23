package sontung.dangvu.directorytest

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileFilter

const val PERMISSION_REQUEST_CODE = 1001
const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private val mFolders = ArrayList<File>()
    private lateinit var mFolderAdapter : FileAdapter
    private lateinit var mFileAdapter: FileAdapter
    private var mFiles = ArrayList<File>()

    private var currentFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            getSubDirs()
        }
    }

    private fun initView() {
        mFolderAdapter = FileAdapter(mFolders, onItemCLickListener)
        mFileAdapter = FileAdapter(mFiles, null)

        with(list_folders) {
            layoutManager = LinearLayoutManager(context)
            adapter = mFolderAdapter
        }

        with(list_files) {
            layoutManager = LinearLayoutManager(context)
            adapter = mFileAdapter
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSubDirs()
                }
                else {
                    Toast.makeText(this, "m bi ngu a", Toast.LENGTH_LONG);
                }
            }
        }
    }

    private fun getSubDirs() {
//        val path = Environment.getExternalStorageDirectory().path
//        Log.d(TAG, "path = $path")
//        val file = File(path)
//        val file = Environment.getExternalStorageDirectory()
        val file = File("/storage/emulated/0")
        val path = file.path
        Log.d(TAG, "path = $path, ??? = ${Environment.getExternalStorageState()}")
        val files = file.listFiles()
        for (f in files) {
            if (f.isDirectory) {
                mFolders.add(f)
            } else {
                mFiles.add(f)
            }
        }
    }

    private fun openSubDirs(path : String) {
        currentFile = File(path)
        val listFolder = ArrayList<File>()
        val listFile = ArrayList<File>()
        val file = File(path)
        val files = file.listFiles()
        for (f in files) {
            if (f.isDirectory) {
                listFolder.add(f)
            } else {
                listFile.add(f)
            }
        }
        Log.d(TAG, "list size : ${listFolder.size}")
        mFolderAdapter.updateDir(listFolder)
        mFileAdapter.updateDir(listFile)
    }

    private val onItemCLickListener = object : FileAdapter.OnItemSelectedListener {
        override fun onItemSelectedListener(file : File) {
            if (file.isDirectory) {
                openSubDirs(file.path)
            } else if (file.path.endsWith(".apk")) {
                openApkfile(file)
            }
        }
    }

    private fun openApkfile(file : File) {
        val intent = Intent(ACTION_VIEW)
        intent.setDataAndType(FileProvider.getUriForFile(this, "app.provider", file), "application/vnd.android.package-archive")
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent)
    }
    override fun onBackPressed() {
        currentFile?.parent?.let { openSubDirs(it) }
    }

    private fun getApkFiles() {
        val apkFile = currentFile!!.listFiles(ApkFilter())
        mFiles.addAll(apkFile)
    }

}

class ApkFilter : FileFilter {
    override fun accept(pathname: File?): Boolean {
        return pathname?.endsWith(".apk")!!
    }

}
