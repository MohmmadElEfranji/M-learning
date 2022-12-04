package com.nerds.m_learning.student.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.nerds.m_learning.firebase_service.FirebaseResponse
import com.nerds.m_learning.student.model.Students
import com.nerds.m_learning.student.model.TextMessage
import com.nerds.m_learning.teacher.model.Teachers
import kotlinx.coroutines.tasks.await
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.net.ssl.HttpsURLConnection

object StudentsFirebaseRepo {


    private val collectionRef = Firebase.firestore.collection(Students.COLLECTION_STUDENTS)
    private val collectionRefAllCourses =
        Firebase.firestore.collection(Teachers.COLLECTION_ALL_COURSES)
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val collectionRefChatChannel =
        Firebase.firestore.collection(Teachers.COLLECTION_CHAT_CHANNEL)
    private var inputStream: InputStream? = null
    private val collectionRefTeachers = Firebase.firestore.collection(Teachers.COLLECTION_TEACHERS)
    /*----------------------------------*//*----------------------------------*/

    // region Log in

    private suspend fun checkStudentAccountExistsOnFireStore(
        context: Context,
        studentID: String
    ): FirebaseResponse<Students.Student> {
        return try {
            val result =
                collectionRef.document(studentID).get().await().toObject<Students.Student>()
            val repository = DataStoreRepository.getInstance(context)

            repository.saveToDataStore2(result!!.firstLogin.toString())

            FirebaseResponse.Success(result)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message!!)
        }
    }

     suspend fun checkStudentAccountExists(
        studentID: String
    ): FirebaseResponse<Students.Student> {
        return try {
            val result =
                collectionRef.document(studentID).get().await().toObject<Students.Student>()

            FirebaseResponse.Success(result)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message!!)
        }
    }

    suspend fun loginOnFirebaseAuth(
        context: Context,
        studentEmail: String,
        studentPassword: String
    ): FirebaseResponse<Boolean> {
        return try {
            val result =
                auth.signInWithEmailAndPassword(studentEmail, studentPassword).await()
            val userID = result.user!!.uid

            checkStudentAccountExistsOnFireStore(context, userID)
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    // endregion


    /*----------------------------------*//*----------------------------------*/

    //region SignUp
    private suspend fun signUpOnFirestoreStudent2(
        student: Students.Student
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRef.document(student.studentID).set(student).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message!!)
        }
    }

    suspend fun registerNewStudentAccount(student: Students.Student): FirebaseResponse<Boolean> {
        return try {
            val result =
                auth.createUserWithEmailAndPassword(student.email!!, student.password!!).await()
            val userID = result.user!!.uid
            student.studentID = userID
            signUpOnFirestoreStudent2(student)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message!!)
        }
    }
    //endregion

    //region checkLoggedInState
    suspend fun getDataOfStudent(studentID: String): FirebaseResponse<Students.Student> {
        return try {
            val user = collectionRef.document(studentID).get().await().toObject<Students.Student>()
            FirebaseResponse.Success(user)
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateNumberOfRegisteredCourses(
        studentID: String,
        numberOfRegisteredCourses: Int
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                transaction.update(userRef, "numberOfRegisteredCourses", numberOfRegisteredCourses)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateFirstLogin(
        studentID: String
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                transaction.update(userRef, "firstLogin", true)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(false, message = e.message)
        }
    }

    suspend fun updateInterests(
        studentID: String,
        arrayOfInterests: MutableList<String>
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                transaction.update(userRef, "interests", arrayOfInterests)

                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(false, message = e.message)
        }
    }
    //endregion


    //region Get all courses
    suspend fun getAllCoursesWithFilter(courseID: MutableList<String>): FirebaseResponse<MutableList<Teachers.Courses>> {

        return try {
            val allCourses = collectionRefAllCourses.whereEqualTo("visibility", true)
                .whereEqualTo("publishing", true).whereNotIn("courseID", courseID)

                .get().await()
                .toObjects<Teachers.Courses>() as MutableList

            FirebaseResponse.Success(allCourses)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllCoursesWithOutFilter(): FirebaseResponse<MutableList<Teachers.Courses>> {

        return try {
            val allCourses = collectionRefAllCourses.whereEqualTo("visibility", true)
                .whereEqualTo("publishing", true).get().await()
                .toObjects<Teachers.Courses>() as MutableList

            FirebaseResponse.Success(allCourses)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllCourseSearchWithFilter(
        text: String?,
        courseID: MutableList<String>
    ): FirebaseResponse<MutableList<Teachers.Courses>> {

        return try {

            if (!text.isNullOrEmpty() && text.isNotBlank()) {
                val allCourses = collectionRefAllCourses.whereEqualTo("visibility", true)
                    .whereEqualTo("publishing", true).whereNotIn("courseID", courseID)
                    .orderBy("courseName")
                    .startAt(
                        text
                    ).endAt(
                        text + '\uf8ff',
                    )
                    .startAt(
                        text.lowercase()
                    ).endAt(
                        text.lowercase() + '\uf8ff',
                    )
                    .startAt(
                        text.uppercase()
                    ).endAt(
                        text.uppercase() + '\uf8ff',
                    )
                    .startAt(
                        "${text[0].toString().lowercase()}${text.substring(1)}"
                    ).endAt(
                        "${text[0].toString().lowercase()}${text.substring(1)}" + '\uf8ff',
                    )
                    .startAt(
                        "${text[0].toString().uppercase()}${text.substring(1)}"
                    ).endAt(
                        "${text[0].toString().uppercase()}${text.substring(1)}" + '\uf8ff',
                    ).limit(10)
                    .get().await()
                    .toObjects<Teachers.Courses>() as MutableList

                FirebaseResponse.Success(allCourses)
            } else {
                val allCourses = collectionRefAllCourses.whereEqualTo("visibility", true)
                    .whereEqualTo("publishing", true).whereNotIn("courseID", courseID)

                    .get().await()
                    .toObjects<Teachers.Courses>() as MutableList
                FirebaseResponse.Success(allCourses)
            }

        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllCourseSearchWithOutFilter(text: String?): FirebaseResponse<MutableList<Teachers.Courses>> {

        return try {
            if (!text.isNullOrEmpty() && text.isNotBlank()) {
                val allCourses = collectionRefAllCourses.whereEqualTo("visibility", true)
                    .whereEqualTo("publishing", true)
                    .orderBy("courseName")
                    .startAt(
                        text
                    ).endAt(
                        text + '\uf8ff',
                    )
                    .startAt(
                        text.lowercase()
                    ).endAt(
                        text.lowercase() + '\uf8ff',
                    )
                    .startAt(
                        text.uppercase()
                    ).endAt(
                        text.uppercase() + '\uf8ff',
                    )
                    .startAt(
                        "${text[0].toString().lowercase()}${text.substring(1)}"
                    ).endAt(
                        "${text[0].toString().lowercase()}${text.substring(1)}" + '\uf8ff',
                    )
                    .startAt(
                        "${text[0].toString().uppercase()}${text.substring(1)}"
                    ).endAt(
                        "${text[0].toString().uppercase()}${text.substring(1)}" + '\uf8ff',
                    ).limit(10)


                    .get().await()
                    .toObjects<Teachers.Courses>() as MutableList



                FirebaseResponse.Success(allCourses)
            } else {
                val allCourses = collectionRefAllCourses.whereEqualTo("visibility", true)
                    .whereEqualTo("publishing", true)
                    .get().await()
                    .toObjects<Teachers.Courses>() as MutableList

                FirebaseResponse.Success(allCourses)
            }


        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
    //endregion

    suspend fun addCourse(
        course: Students.Courses,
        studentID: String,
        courseStudents: Teachers.CourseStudents
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(course.courseID).set(course).await()

            collectionRefTeachers.document(course.teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(course.courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .document(courseStudents.StudentID)
                .set(courseStudents).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllCoursesSubscribed(StudentID: String): FirebaseResponse<MutableList<Students.Courses>> {

        return try {
            val course =
                collectionRef.document(StudentID)
                    .collection(Teachers.SUB_COLLECTION_COURSES).whereEqualTo("visibility", true)
                    .get().await().toObjects<Students.Courses>() as MutableList
            FirebaseResponse.Success(course)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllCoursesInterests(courseField: MutableList<String>): FirebaseResponse<MutableList<Teachers.Courses>> {

        return try {
            val allCourses = collectionRefAllCourses.whereIn("courseField", courseField)
                .whereEqualTo("visibility", true)
                .whereEqualTo("publishing", true)
                .get().await()
                .toObjects<Teachers.Courses>() as MutableList

            FirebaseResponse.Success(allCourses)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    fun getInputStreamFromURL(url: String?): InputStream? {
        try {
            val mUrl = URL(url)
            // below is the step where we are
            // creating our connection.
            val urlConnection: HttpURLConnection = mUrl.openConnection() as HttpsURLConnection

            if (urlConnection.responseCode == 200) {
                // response is success.
                // we are getting input stream from url
                // and storing it in our variable.
                inputStream = BufferedInputStream(urlConnection.inputStream)
            }
        } catch (e: IOException) {
            // this is the method
            // to handle errors.
            e.printStackTrace()
            return null
        }
        return inputStream!!
    }

    suspend fun addSubmissionAssignment(
        assignment: Students.SubmissionAssignmentsOfLecture,
        teacherID: String,
        studentID: String,
        courseID: String,
        lectureID: String,
        assignmentID: String,
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .document(assignmentID)
                .collection(Teachers.SUB_COLLECTION_Submission_ASSIGNMENTS)
                .document(studentID)
                .set(assignment).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllLecturesOfCourse(
        studentID: String,
        courseID: String
    ): FirebaseResponse<MutableList<Students.LecturesOfCourse>> {
        return try {
            val lecture = collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .whereEqualTo("visibility", true)
                .get().await().toObjects<Students.LecturesOfCourse>() as MutableList

            FirebaseResponse.Success(lecture)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun getAllAssignmentsOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<MutableList<Teachers.AssignmentsOfLecture>> {
        return try {
            val assignment = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).collection(Teachers.SUB_COLLECTION_ASSIGNMENTS)
                .whereEqualTo("visibility", true)
                .get().await().toObjects<Teachers.AssignmentsOfLecture>() as MutableList

            FirebaseResponse.Success(assignment)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun getAllChapterOfLecture(
        teacherID: String,
        courseID: String,
        lectureID: String,
    ): FirebaseResponse<MutableList<Teachers.ChapterOfLecture>> {
        return try {
            val chapter = collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID).collection(Teachers.SUB_COLLECTION_CHAPTERS)
                .whereEqualTo("visibility", true)
                .get().await().toObjects<Teachers.ChapterOfLecture>() as MutableList

            FirebaseResponse.Success(chapter)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun unsubscribeCourse(
        studentID: String,
        course: Students.Courses,
    ): FirebaseResponse<Boolean> {

        return try {
            //delete course from collection students
            collectionRef.document(studentID)
                .collection(Students.SUB_COLLECTION_COURSES)
                .document(course.courseID).delete().await()


            //delete name student from collection teachers
            collectionRefTeachers.document(course.teacherID)
                .collection(Teachers.SUB_COLLECTION_COURSES)
                .document(course.courseID)
                .collection(Teachers.SUB_COLLECTION_COURSE_STUDENTS)
                .document(studentID).delete().await()

            //to update numberOfRegisteredCourses after delete

            if (course.finished == false) {
                val user =
                    collectionRef.document(studentID).get().await().toObject<Students.Student>()
                val oldNumber = user!!.numberOfRegisteredCourses
                val newNumber = oldNumber - 1
                updateNumberOfRegisteredCourses(studentID, newNumber)
            }


            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun deleteLecturesOfCourse(
        studentID: String,
        courseID: String,
        lectureID: String,

        ): FirebaseResponse<Boolean> {

        return try {
            //delete course from collection students
            collectionRef.document(studentID)
                .collection(Students.SUB_COLLECTION_COURSES)
                .document(courseID)
                .collection(Teachers.SUB_COLLECTION_LECTURES)
                .document(lectureID)
                .delete().await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun updateLectureWatchedState(
        studentID: String,
        lecture: Students.LecturesOfCourse,
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(lecture.courseID)
                    .collection(Teachers.SUB_COLLECTION_LECTURES)
                    .document(lecture.lectureID)

                transaction.update(userRef, "watched", true)


                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }

    suspend fun updateCourseFinishedState(
        studentID: String,
        courseID: String,
    ): FirebaseResponse<Boolean> {

        return try {
            Firebase.firestore.runTransaction { transaction ->
                val userRef = collectionRef.document(studentID)
                    .collection(Teachers.SUB_COLLECTION_COURSES)
                    .document(courseID)

                transaction.update(userRef, "finished", true)


                FirebaseResponse.Success(true)
            }.await()
        } catch (e: Exception) {
            FirebaseResponse.Failure(message = e.message)
        }
    }


    suspend fun sendNotificationEmail(
        receiverEmail: String,
        subject: String,
        body: String,
    ): FirebaseResponse<Boolean> {

        return try {
            val stringHost = "smtp.gmail.com"
            val senderEmail = "nerds2023@gmail.com"
            val passwordSenderEmail = "123456789#$"

            val properties: Properties = System.getProperties()

            properties["mail.smtp.host"] = stringHost
            properties["mail.smtp.port"] = "465"
            properties["mail.smtp.ssl.enable"] = "true"
            properties["mail.smtp.auth"] = "true"


            val session: Session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, passwordSenderEmail)
                }
            })
            val mimeMessage = MimeMessage(session)
            mimeMessage.addRecipient(Message.RecipientType.TO, InternetAddress(receiverEmail))
            mimeMessage.subject = subject
            mimeMessage.setText(body)

            Transport.send(mimeMessage)

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {
            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun addOfChatChannel(
        chatChannelID: String,
        chatChannel: Teachers.ChatChannel
    ): FirebaseResponse<Boolean> {
        return try {

            collectionRefChatChannel.document(chatChannelID).set(chatChannel).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }

    suspend fun addOfMessageChannel(
        chatChannelID: String,
        messages: TextMessage
    ): FirebaseResponse<Boolean> {
        return try {

            collectionRefChatChannel.document(chatChannelID)
                .collection("Messages")
                .document()
                .set(messages).await()

            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun addChatChannelForTeacher(
        teacherID: String,
        courseID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRefTeachers.document(teacherID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(courseID).set(chatChannelID).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun addChatChannelForStudent(
        studentID: String,
        courseID: String,
        chatChannelID: Teachers.ChatChannelForUser
    ): FirebaseResponse<Boolean> {
        return try {
            collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(courseID).set(chatChannelID).await()
            FirebaseResponse.Success(true)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }


    suspend fun getChatChannelForStudent(
        studentID: String,
        courseID: String

    ): FirebaseResponse<Teachers.ChatChannelForUser> {
        return try {
            val result = collectionRef.document(studentID)
                .collection(Teachers.SUB_COLLECTION_CHAT_CHANNEL)
                .document(courseID).get().await().toObject<Teachers.ChatChannelForUser>()
            FirebaseResponse.Success(result)
        } catch (exception: Exception) {

            FirebaseResponse.Failure(message = exception.message)
        }
    }
}

