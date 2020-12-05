package edu.cs371m.hellofriend

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val appContext = getApplication<Application>().applicationContext
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var schedules = MutableLiveData<List<Schedule>>()
    private var mySchedules = MutableLiveData<List<Schedule>>()

    fun observeSchedules(): LiveData<List<Schedule>> {
        return schedules
    }

    fun observeMySchedules(): LiveData<List<Schedule>> {
        return mySchedules
    }

    fun saveSchedule(schedule: Schedule) {
        Log.d(
            "HomeViewModel",
            String.format(
                "saveSchedule location(%s) age(%s)",
                schedule.scheduleID,
                schedule.age
            )
        )
        schedule.scheduleID = db.collection("globalSchedule").document().id
        db.collection("globalSchedule")
                .document(schedule.scheduleID)
                .set(schedule)
                .addOnSuccessListener {
                    Log.d("xxx", "Schedule create id: ${schedule.scheduleID}")
                    getSchedule()
                }
                .addOnFailureListener { e ->
                    Log.d("xxx", "Schedule create FAILED \"${schedule.scheduleID}\"")
                    Log.w("xxx", "Error", e)
                }
    }

    fun deleteSchedule(schedule: Schedule) {
        db.collection("globalSchedule").document(schedule.scheduleID).delete()
                .addOnSuccessListener {
                    Log.d("xxx", "Note delete, id: ${schedule.scheduleID}")
                    getSchedule()
                }
                .addOnFailureListener { e ->
                    Log.d("xxx", "Note delete FAILED \"${schedule.scheduleID}\"")
                    Log.w("xxx", "Error", e)
                }
    }

    fun getSchedule() {
        db.collection("globalSchedule")
                .orderBy("timeStamp")
                .limit(100)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w("xxx", "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d("xxx", "fetch ${querySnapshot!!.documents.size}")
                    schedules.postValue(querySnapshot.documents.mapNotNull {
                        it.toObject(Schedule::class.java)
                    })
                }
    }

    fun getMySchedule(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            Toast.makeText(appContext, "Please sign in first", Toast.LENGTH_SHORT).show()
        } else {
            db.collection("globalSchedule")
                .whereEqualTo("ownerUid", currentUser.uid)
                .orderBy("timeStamp")
                .limit(100)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w("xxx", "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d("xxx", "fetch ${querySnapshot!!.documents.size}")
                    mySchedules.postValue(querySnapshot.documents.mapNotNull {
                        it.toObject(Schedule::class.java)
                    })
                }
        }

    }
}