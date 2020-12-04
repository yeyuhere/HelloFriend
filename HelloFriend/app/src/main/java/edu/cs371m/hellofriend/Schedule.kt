package edu.cs371m.hellofriend

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

class Schedule {
    var location: String? = null
    var age: String? = null
    var fromH: String? = null
    var fromM: String? = null
    var toH: String? = null
    var toM: String? = null
    @ServerTimestamp val timeStamp: Timestamp? = null
    var scheduleID = ""
}